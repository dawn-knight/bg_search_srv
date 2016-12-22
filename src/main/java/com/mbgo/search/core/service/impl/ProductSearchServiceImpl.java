/*
 * 2014-9-24 下午4:04:34 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.AttrValue;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.bean.query.ReturnProductByBrandCodeBean;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.BasicService;
import com.mbgo.search.core.service.DataEtlService;
import com.mbgo.search.core.service.ProductSearchService;
import com.mbgo.search.core.service.SearchErrorLogService;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.util.FormatUtil;

@Service("productSearchService")
public class ProductSearchServiceImpl extends BasicService implements ProductSearchService {

  /*
   * private LBHttpSolrClient lbSearchGoodsSolrServer;
   * 
   * public ProductSearchServiceImpl() { String[] solrServerUrls = new String[] {
   * "http://10.80.15.16:8080/solr/goods_shard1_replica1",
   * "http://10.80.15.75:8080/solr/goods_shard1_replica3",
   * "http://10.80.15.74:8080/solr/goods_shard1_replica2" };
   * 
   * try { lbSearchGoodsSolrServer = new LBHttpSolrClient(solrServerUrls); } catch
   * (MalformedURLException e) { // TODO Auto-generated catch block // e.printStackTrace(); } }
   */

  @Override
  public ProductQueryResult search(ProductQuery query) {

    ProductQueryResult result = new ProductQueryResult();
    if (query.getBrand() != null && !query.getBrand().equals("")) {
      result.setNewWord(query.getBrand());
    }
    try {

      int queryTimes = 0;
      boolean isRetry = false;
      do {

        queryTimes++;

        SolrQuery solrQuery = packProductQuery(query);
        solrQuery.setFields(FieldUtil.PRODUCT_UUID, FieldUtil.PRODUCT_ID, FieldUtil.PRODUCT_NAME, FieldUtil.STOCK, FieldUtil.SALE_COUNT, FieldUtil.DISCOUNT,
            FieldUtil.PRODUCT_DISPLAY_TAG);
        // cloudSearchGoodsSolrClient.setZkClientTimeout(30000);
        // cloudSearchGoodsSolrClient.setZkConnectTimeout(30000);

        QueryResponse queryResponse =
            cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, solrQuery);

        /*
         * String[] solrServerUrls = new String[] {
         * "http://10.80.15.16:8080/solr/goods_shard1_replica1",
         * "http://10.80.15.75:8080/solr/goods_shard1_replica3",
         * "http://10.80.15.74:8080/solr/goods_shard1_replica2" };
         * 
         * LBHttpSolrClient lbSearchGoodsSolrServer = new LBHttpSolrClient(solrServerUrls);
         */
        // QueryResponse queryResponse = lbSearchGoodsSolrServer.query(solrQuery);
        // lbSearchGoodsSolrServer.close();

        SolrDocumentList documentList = queryResponse.getResults();
        long total = documentList.getNumFound(); // 总记录数
        int pageGetNum = documentList.size(); // 当前条件得到当前页的记录数
        if (total <= 0) {
          return result;
        }
        if (pageGetNum < 1) { // 总结果数大于0，当前页的结果数小于1，则说明页数超出，直接跳转第一页，重新查询
          query.setPageNo(1);
          isRetry = true;
          continue;
        }

        result.setTotal(total);
        // 获取相关的高亮信息
        // Map<String, Map<String, List<String>>> high = queryResponse.getHighlighting();

        // 拼装商品id
        List<String> productIds = new ArrayList<String>();
        for (SolrDocument d : documentList) {
          // productIds.add(d.get(FieldUtil.PRODUCT_ID).toString());
          productIds.add(d.get(FieldUtil.PRODUCT_UUID).toString());
        }
        // 根据pid从redis批量获取缓存；如果没有，则从mysql获取，然后更新保存到redis
        Map<String, Product> redisProduct = readFromRedis(productIds);
        List<Product> list = new ArrayList<Product>();
        Map<String, Integer> productIdOrders = null;
        // 6位码传入顺序返回
        if (StringUtils.isNotBlank(query.getProductId()) && query.getProductId().length() > 6) {
          String[] splitProductIds = query.getProductId().split("[|]");
          // 如果传入6位码数量大于1，则按照传入顺序返回
          if (splitProductIds.length > 1) {
            productIdOrders = new HashMap<String, Integer>();
            for (int i = 0; i < splitProductIds.length; i++) {
              if (StringUtils.isNotBlank(splitProductIds[i])) {
                productIdOrders.put(splitProductIds[i].trim(), i);
              }
            }
          }
        }
        for (SolrDocument d : documentList) {
          String productId = d.get(FieldUtil.PRODUCT_UUID).toString();
          String goodsTitle = d.get(FieldUtil.PRODUCT_NAME).toString();
          String discount = null;
          String displayTag = null;
          int stock = 0;
          try {
            // 标题高亮
            // goodsTitle = getHighLight(high, productId, FieldUtil.PRODUCT_NAME, goodsTitle);
            // 库存
            stock = Integer.parseInt(d.getFieldValue(FieldUtil.STOCK).toString());

            discount = d.getFieldValue(FieldUtil.DISCOUNT).toString();
            Object objDisplayTag = d.getFieldValue(FieldUtil.PRODUCT_DISPLAY_TAG);
            if (objDisplayTag != null) {
              displayTag = objDisplayTag.toString();
            }
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
          // 设置颜色商品的库存
          Product p = redisProduct.get(productId);
          if (p == null) {
            continue;
          }

          // 邦购商品设置促销价格，不参加促销，则不予替换
          if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode())) {
            int appId = query.getAppId();
            switch (appId) {
            case 1:
              p.setSalesPrice(p.getPcPromotionPrice());
              break;
            case 2:
              p.setSalesPrice(p.getAppPromotionPrice());
              break;
            case 3:
              p.setSalesPrice(p.getWapPromotionPrice());
              break;
            case 4:
              p.setSalesPrice(p.getWebchatPromotionPrice());
              break;
            default:
              p.setSalesPrice(p.getPcPromotionPrice());
              break;
            }
          }
          p.setDiscount(discount);
          p.setStock(stock);
          p.setProductName(goodsTitle);
          p.setDisplayTag(displayTag);
          removeColorProductsWhichNoStock(p, query);

          list.add(p);
        }
        // 按照6位码排序
        if (productIdOrders != null && productIdOrders.size() > 0) {
          final Map<String, Integer> finalMap = productIdOrders;
          Collections.sort(list, new Comparator<Product>() {

            @Override
            public int compare(Product o1, Product o2) {
              return finalMap.get(o1.getProductId()) - finalMap.get(o2.getProductId());
            }
          });
        }
        result.setList(list);
      } while ((queryTimes <= 1 && isRetry));
    } catch (Exception e) {
      searchErrorLogService.log(e.getMessage(), 1);
      log.error(e.getMessage());
    }
    return result;
  }

  private void removeColorProductsWhichNoStock(Product p, ProductQuery query) {
    if (query.getStock() > 0) {
      List<ColorProduct> colorProducts = p.getColorProducts();
      if (CollectionUtils.isNotEmpty(colorProducts)) {
        List<ColorProduct> tCp = new ArrayList<ColorProduct>();
        for (ColorProduct cp : colorProducts) {
          if (cp.getStock() < 1) {
            tCp.add(cp);
          }
        }
        colorProducts.removeAll(tCp);
      }
    }
  }

  @Override
  public QueryResponse queryFilterDataResponse(ProductQuery qp) throws SolrServerException, IOException {
    QueryResponse result = null;
    try {
      result = cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, packFilterQuery(qp));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public FilterData searchFilterData(ProductQuery query) throws IOException {

    try {
      return queryFilterDataNoCache(query);
    } catch (SolrServerException e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public long countForWord(String word) {
    try {
      if (StringUtils.isBlank(word)) {
        return 0;
      }
      ProductQuery productQuery = new ProductQuery(word, null, null);
      productQuery.init();

      SolrQuery solrQuery = packProductQuery(productQuery);

      solrQuery.setRows(0);

      return cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, solrQuery).getResults().getNumFound();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return 0;
  }

  @SuppressWarnings("unused")
  private Product solrDocumentToProduct(SolrDocument d) {
    Product p = new Product();
    // SolrInputDocument d = new SolrInputDocument();
    //
    // // <field name="keyword" type="text" indexed="true" stored="false" multiValued="false"
    // required="true" />
    // d.setField(FieldUtil.KEYWORD, FieldUtil.METERSBONWE);
    // //
    // // <field name="product_id" type="string" indexed="true" stored="true" multiValued="false"
    // required="true" />
    // d.setField(FieldUtil.PRODUCT_ID, p.getProductId());
    //
    // // <field name="product_code" type="string" indexed="true" stored="true" multiValued="false"
    // />
    // d.setField(FieldUtil.PRODUCT_CODE, p.getProductCode());
    //
    // // <field name="store_id" type="string" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.STORE_ID, p.getStoreId());
    //
    // // <field name="store_name" type="string" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.STORE_NAME, p.getStoreName());
    //
    // // <field name="brand_code" type="string" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.BRAND_CODE, p.getBrandCode().toUpperCase());
    //
    // // <field name="brand_name" type="string" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.BRAND_NAME, p.getBrandName());
    //
    // // <field name="product_name" type="string" indexed="true" stored="true" multiValued="false"
    // />
    // d.setField(FieldUtil.PRODUCT_NAME, p.getProductName());
    //
    // // <field name="market_price" type="double" indexed="true" stored="true" multiValued="false"
    // />
    // d.setField(FieldUtil.MARKET_PRICE, p.getMarketPrice());
    //
    // // <field name="sales_price" type="double" indexed="true" stored="true" multiValued="false"
    // />
    // d.setField(FieldUtil.SALES_PRICE, p.getSalesPrice());
    //
    // // <field name="spec_price" type="double" indexed="true" stored="true" multiValued="false" />
    // // d.setField(FieldUtil.SPEC_PRICE, p.get);
    //
    // // <field name="stock" type="int" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.STOCK, p.getStock());
    //
    // // <field name="update_time" type="long" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.UPDATE_TIME, p.getUpdateTime().getTime());
    //
    // // <field name="img_url" type="string" indexed="false" stored="true" multiValued="false" />
    // d.setField(FieldUtil.IMAGE_URL, p.getImgUrl());
    //
    // // <field name="create_time" type="long" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.CREATE_TIME, p.getCreateTime().getTime());
    //
    // // <field name="gsi_rank" type="float" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.GSI_RANK, p.getGsiRank());
    //
    // // <field name="sale_count" type="int" indexed="true" stored="true" multiValued="false" />
    // d.setField(FieldUtil.SALE_COUNT, p.getSaleCount());
    //
    // // <field name="data_version_timestamp" type="long" indexed="true" stored="true"
    // multiValued="false" />
    // d.setField(FieldUtil.DATA_VERSION, currentVersion);
    //
    // // <dynamicField name="attribute_*" type="int" indexed="true" stored="true"
    // multiValued="true" />
    // // d.setField(FieldUtil.PRODUCT_CODE, p.getProductCode());
    //
    // // <field name="category_id" type="int" indexed="true" stored="true" multiValued="true" />
    // for(Integer id : p.getAllCategoryIds()) {
    // d.addField(FieldUtil.CATEGORY_ID_M, id);
    // }
    //
    // d.setField(FieldUtil.PRODUCT_CODE, p.getProductCode());
    //
    // // <field name="theme_code" type="string" indexed="true" stored="true" multiValued="true" />
    // List<ThemeInfo> themes = p.getThemes();
    // if(themes != null && themes.size() > 0) {
    // for(ThemeInfo ti : themes) {
    // d.setField(FieldUtil.THEME_CODE_M, ti.getCode());
    // }
    // }
    //
    // // <field name="size_code" type="string" indexed="true" stored="true" multiValued="true" />
    // List<SizeInfo> sizeInfos = p.getAllSizes();
    // if(sizeInfos != null) {
    // for(SizeInfo s : sizeInfos) {
    // d.setField(FieldUtil.SIZE_CODE_M, s.getSizeCode());
    // }
    // }
    //
    // // <field name="tag" type="string" indexed="true" stored="true" multiValued="true" />
    // List<String> tags = p.getTags();
    // if(tags != null && tags.size() > 0) {
    // for(String tag : tags) {
    // d.setField(FieldUtil.PRODUCT_TAG, tag);
    // }
    // }

    return p;
  }

  /**
   * 从redis获取内容
   * 
   * @param productIds
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unused")
  private Map<String, Product> readFromRedisForOrder(List<String> productIds) throws Exception {
    Map<String, Product> result = new HashMap<String, Product>();

    List<Product> products = dataEtlService.readProductFromRedis(productIds);

    for (Product product : products) {
      if (product != null) {
        setColorGoodsSizeNullForOrder(product);
        // result.put(product.getProductId(), product);
        result.put(product.getProductUuid(), product);
      }
    }

    if (productIds.size() == result.size()) {
      return result;
    }
    for (String id : result.keySet()) {
      productIds.remove(id);
    }

    if (productIds.size() > 0) {
      long b = System.currentTimeMillis();
      List<Product> productsFromMySQL = dataEtlService.getProductList(-1, -1, productIds);
      long cost = System.currentTimeMillis() - b;
      if (productsFromMySQL != null && productsFromMySQL.size() > 0) {
        log.debug("productsFromMySQL is not null , size={}, cost={}", productsFromMySQL.size(),
            cost);
        for (Product product : productsFromMySQL) {
          setColorGoodsSizeNullForOrder(product);
          String pid = product.getProductId();
          result.put(pid, product);
          dataEtlService.saveProductToRedis(product);
        }
      }
    }

    return result;
  }

  /**
   * 从redis获取内容
   * 
   * @param productIds
   * @return
   * @throws Exception
   */
  private Map<String, Product> readFromRedis(List<String> productIds) throws Exception {
    Map<String, Product> result = new HashMap<String, Product>();
    List<Product> products = dataEtlService.readProductFromRedis(productIds);

    for (Product product : products) {
      if (product != null) {
        setColorGoodsSizeNull(product);
        // result.put(product.getProductId(), product);
        result.put(product.getProductUuid(), product);
      }
    }

    if (productIds.size() == result.size()) {
      return result;
    }
    for (String id : result.keySet()) {
      productIds.remove(id);
    }

    if (productIds.size() > 0) {
      long b = System.currentTimeMillis();
      List<Product> productsFromMySQL = dataEtlService.getProductList(-1, -1, productIds);
      long cost = System.currentTimeMillis() - b;
      if (productsFromMySQL != null && productsFromMySQL.size() > 0) {
        log.debug("productsFromMySQL is not null , size={}, cost={}", productsFromMySQL.size(),
            cost);
        for (Product product : productsFromMySQL) {
          setColorGoodsSizeNull(product);
          // String pid = product.getProductId();
          String pid = product.getProductUuid();
          result.put(pid, product);
          dataEtlService.saveProductToRedis(product);
        }
      }
    }

    return result;
  }

  @Override
  public List<String> analyzeWord(String word) {
    List<String> rs = new ArrayList<String>(0);
    try {
      if (StringUtils.isBlank(word) || word.length() < 2) {
        return rs;
      }

      FieldAnalysisRequest request = new FieldAnalysisRequest();

      // 设置分词域
      request.addFieldName(FieldUtil.KEYWORD);
      // 设置待分词字符串
      request.setFieldValue(word);

      // 查询分词结果
      FieldAnalysisResponse response = request.process(cloudSearchGoodsSolrClient, SolrCollectionNameDefineBean.GOODS);

      if (response == null) {
        return rs;
      }

      for (Entry<String, FieldAnalysisResponse.Analysis> r : response.getAllFieldNameAnalysis()) {
        FieldAnalysisResponse.Analysis ana = r.getValue();
        Iterator<AnalysisPhase> ps = ana.getIndexPhases().iterator();
        while (ps.hasNext()) {
          AnalysisPhase p = ps.next();

          for (TokenInfo ti : p.getTokens()) {
            rs.add(ti.getText());
          }

          if (p.getClassName().equalsIgnoreCase("org.wltea.analyzer.lucene.IKTokenizer")) {
            return rs;
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage() + "getAnalyzeResult(String " + FieldUtil.KEYWORD + ", String "
          + word + ")");
    }
    return rs;
  }

  private static Logger log = LoggerFactory.getLogger(ProductSearchServiceImpl.class);

  /*
   * @Resource(name = "lbSearchGoodsSolrServer") private LBHttpSolrClient lbSearchGoodsSolrServer;
   */
  @Resource(name = "cloudSearchGoodsSolrClient")
  private CloudSolrClient cloudSearchGoodsSolrClient;
  @Resource(name = "dataEtlService")
  private DataEtlService dataEtlService;
  @Resource(name = "searchErrorLogService")
  private SearchErrorLogService searchErrorLogService;

  private ReturnProductByBrandCodeBean returnReturnProductByBrandCodeBean(int startPosition,
      int endPosition, SolrDocumentList documentList, String brandCode, long totalCount) {
    ReturnProductByBrandCodeBean result = new ReturnProductByBrandCodeBean();
    result.setBrandCode(brandCode);
    result.setTotal(totalCount);
    // 拼装商品id
    List<String> productIds = new ArrayList<String>();
    int tempStartPosition = startPosition;
    do {
      // productIds.add(documentList.get(tempStartPosition).get(FieldUtil.PRODUCT_ID).toString());
      productIds.add(documentList.get(tempStartPosition).get(FieldUtil.PRODUCT_UUID).toString());
      tempStartPosition++;
    } while (tempStartPosition <= endPosition);

    // 根据pid从redis批量获取缓存；如果没有，则从mysql获取，然后更新保存到redis
    Map<String, Product> redisProduct = null;
    try {
      redisProduct = readFromRedis(productIds);
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    List<Product> list = new ArrayList<Product>();
    do {
      SolrDocument d = documentList.get(startPosition);
      // String productId = d.get(FieldUtil.PRODUCT_ID).toString();
      String productId = d.get(FieldUtil.PRODUCT_UUID).toString();
      String goodsTitle = d.get(FieldUtil.PRODUCT_NAME).toString();
      String discount = "";
      int stock = 0;
      Set<String> tagSet = new LinkedHashSet<String>();
      String displayTag = "";
      try {
        // 库存
        stock = Integer.parseInt(d.getFieldValue(FieldUtil.STOCK).toString());

        discount = d.getFieldValue(FieldUtil.DISCOUNT).toString();

        @SuppressWarnings("unchecked")
        List<String> tags = (ArrayList<String>) d.get(FieldUtil.PRODUCT_TAG);
        if (tags != null && tags.size() > 0) {
          for (String s : tags) {
            tagSet.add(s);
          }
          displayTag = tagSet.iterator().next();
        }

      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }

      // 设置颜色商品的库存
      Product product = redisProduct.get(productId);
      if (product == null) {
        continue;
      }
      // 该接口只提供给APP使用，故默认2
      int appId = 2;
      switch (appId) {
      case 1:
        product.setSalesPrice(product.getPcPromotionPrice());
        break;
      case 2:
        product.setSalesPrice(product.getAppPromotionPrice());
        break;
      case 3:
        product.setSalesPrice(product.getWapPromotionPrice());
        break;
      case 4:
        product.setSalesPrice(product.getWebchatPromotionPrice());
        break;
      default:
        product.setSalesPrice(product.getAppPromotionPrice());
        break;
      }

      product.setDiscount(discount);
      product.setTags(tagSet);
      product.setStock(stock);
      product.setProductName(goodsTitle);
      product.setDisplayTag(displayTag);

      //
      /*
       * List<ColorProduct> colorProducts = product.getColorProducts(); if
       * (CollectionUtils.isNotEmpty(colorProducts)) { List<ColorProduct> tCp = new
       * ArrayList<ColorProduct>(); for (ColorProduct cp : colorProducts) { if (cp.getStock() < 1) {
       * tCp.add(cp); } } colorProducts.removeAll(tCp); }
       */

      list.add(product);

      startPosition++;
    } while (startPosition <= endPosition);
    result.setProductList(list);
    return result;
  }

  public static void main(String[] args) {
    System.out.println(new Date(1428891483000l));
  }

  private List<String> getProductBrandCodeList(String brandCode) {
    List<String> result = new ArrayList<String>();
    String[] brandCodes = brandCode.split(",");
    if (brandCodes != null && brandCodes.length > 0) {
      for (String brand : brandCodes) {
        if (StringUtils.isNotBlank(brand)) {
          // 将品牌码全部装换为大写字母
          result.add(brand.toUpperCase());
        }
      }
    }
    return result;
  }

  /**
   * 如果timeInterval=999，则按照上架时间逆序返回10条记录 ;brandCode最多有5个
   * 
   * @param p
   * @return
   */
  private ReturnNewlyProductBean searchNewlyProductForSpecialCode(ParameterNewlyProductBean para) {
    ReturnNewlyProductBean result = new ReturnNewlyProductBean();
    try {
      // 品牌码集合
      String brandCodes = para.getBrandCode();

      List<String> brandCodeList = getProductBrandCodeList(brandCodes);
      if (brandCodeList == null || brandCodeList.size() <= 0) {
        return result;
      }
      int totalNum = 0;
      List<ReturnProductByBrandCodeBean> returnProductByBrandCodeBeans = new ArrayList<ReturnProductByBrandCodeBean>();
      for (String brandCode : brandCodeList) {
        if (StringUtils.isNotBlank(brandCode)) {
          // 构造查询参数
          // brand_code:MM
          SolrQuery solrQuery = new SolrQuery();
          // 设置查询关键字
          StringBuilder sb = new StringBuilder("");
          sb.append(FieldUtil.BRAND_CODE).append(":").append(brandCode.toUpperCase()).append(" AND ").append(FieldUtil.CHANNEL_CODE).append(":")
              .append(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE);
          solrQuery.setQuery(sb.toString());
          // 排序,更新时间逆序
          solrQuery.setSort(new SortClause(FieldUtil.ON_SALE_DATE, ORDER.desc));
          // 设置返回field
          solrQuery.setFields(FieldUtil.PRODUCT_UUID, FieldUtil.BRAND_CODE, FieldUtil.PRODUCT_ID, FieldUtil.PRODUCT_NAME,
              FieldUtil.STOCK, FieldUtil.DISCOUNT, FieldUtil.PRODUCT_DISPLAY_TAG);
          // 默认返回10条记录
          solrQuery.setStart(0);
          solrQuery.setRows(10);
          QueryResponse queryResponse = cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, solrQuery);
          SolrDocumentList documentList = queryResponse.getResults();
          long total = documentList.getNumFound(); // 总记录数
          if (total <= 0) {
            ReturnProductByBrandCodeBean bean = new ReturnProductByBrandCodeBean();
            bean.setBrandCode(brandCode);
            returnProductByBrandCodeBeans.add(bean);
            continue;
          }
          totalNum += total;

          // 拼装商品id
          List<String> productIds = new ArrayList<String>();
          for (SolrDocument d : documentList) {
            // productIds.add(d.get(FieldUtil.PRODUCT_ID).toString());
            productIds.add(d.get(FieldUtil.PRODUCT_UUID).toString());
          }

          // 根据pid从redis批量获取缓存；如果没有，则从mysql获取，然后更新保存到redis
          Map<String, Product> redisProduct = readFromRedis(productIds);

          List<Product> list = new ArrayList<Product>();
          for (SolrDocument d : documentList) {
            String productId = d.get(FieldUtil.PRODUCT_UUID).toString();
            String goodsTitle = d.get(FieldUtil.PRODUCT_NAME).toString();
            String discount = "";
            String displayTag = null;
            int stock = 0;
            try {
              // 库存
              stock = Integer.parseInt(d.getFieldValue(FieldUtil.STOCK).toString());

              discount = d.getFieldValue(FieldUtil.DISCOUNT).toString();
              Object objDisplayTag = d.getFieldValue(FieldUtil.PRODUCT_DISPLAY_TAG);
              if (objDisplayTag != null) {
                displayTag = objDisplayTag.toString();
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
            }

            // 设置颜色商品的库存
            Product p = redisProduct.get(productId);
            if (p == null) {
              continue;
            }
            // 邦购商品设置促销价格，不参加促销，则不予替换
            // 该接口只提供给APP使用，故默认2
            int appId = 2;
            switch (appId) {
            case 1:
              p.setSalesPrice(p.getPcPromotionPrice());
              break;
            case 2:
              p.setSalesPrice(p.getAppPromotionPrice());
              break;
            case 3:
              p.setSalesPrice(p.getWapPromotionPrice());
              break;
            case 4:
              p.setSalesPrice(p.getWebchatPromotionPrice());
              break;
            default:
              p.setSalesPrice(p.getAppPromotionPrice());
              break;
            }

            p.setDiscount(discount);
            p.setStock(stock);
            p.setProductName(goodsTitle);
            p.setDisplayTag(displayTag);
            // p.setDisplayTag(TagChainUtil.getDisplayTag(p.getTags()));
            // removeColorProductsWhichNoStock(p, query);
            // p = solrDocumentToProduct(d);

            list.add(p);
          }

          ReturnProductByBrandCodeBean bean = new ReturnProductByBrandCodeBean();
          bean.setBrandCode(brandCode);
          bean.setProductList(list);
          bean.setTotal(total);
          returnProductByBrandCodeBeans.add(bean);
        }
      }
      result.setTotal(totalNum);
      // 设置商品集合，按照商品码为key
      result.setReturnProductByBrandCodeBeans(returnProductByBrandCodeBeans);
    } catch (Exception e) {
      searchErrorLogService.log(e.getMessage(), 1);
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  public ReturnNewlyProductBean searchNewlyProduct(ParameterNewlyProductBean p) {
    // 如果timeInterval=999,则按照上架时间逆序返回10条记录
    if (StringUtils.equals("999", p.getTimeInterval())) {
      return searchNewlyProductForSpecialCode(p);
    }

    ReturnNewlyProductBean result = new ReturnNewlyProductBean();

    try {
      // 构造查询参数
      // brand_code:(MM OR 4M OR MC) AND on_sale_date:[1428891483000 TO *]
      SolrQuery solrQuery = new SolrQuery();
      // 设置查询关键字
      StringBuilder sb = new StringBuilder("");

      sb.append(FieldUtil.CHANNEL_CODE).append(":")
          .append(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE).append(" AND ").append(FieldUtil.BRAND_CODE).append(":(");
      // 品牌码集合
      String brandCodes = p.getBrandCode();

      List<String> brandCodeList = getProductBrandCodeList(brandCodes);
      if (brandCodeList == null || brandCodeList.size() <= 0) {
        return result;
      }
      for (int i = 0; i < brandCodeList.size(); i++) {
        if (StringUtils.isNotBlank(brandCodeList.get(i))) {
          if (i == brandCodeList.size() - 1) {
            sb.append(brandCodeList.get(i).toUpperCase()).append(")");
          } else {
            sb.append(brandCodeList.get(i).toUpperCase()).append(" OR ");
          }
        }
      }

      // 最近多少天
      String timeInterval = p.getTimeInterval();
      if (StringUtils.isBlank(timeInterval) || !FormatUtil.isNumber(timeInterval)) {
        p.setTimeInterval("21");
      }
      int timeIntervalInt = Integer.parseInt(timeInterval);
      // 转换为毫秒数
      long timeIntervalMs = timeIntervalInt * 24 * 3600 * 1000l;
      long nowTime = System.currentTimeMillis();
      long lastTime = nowTime - timeIntervalMs;
      sb.append(" AND ").append(FieldUtil.ON_SALE_DATE).append(":[").append(lastTime)
          .append(" TO *]");

      solrQuery.setQuery(sb.toString());
      // solrQuery.setQuery("brand_code:(MM OR 4M OR MUUUU) AND on_sale_date:[1428891483000 TO *]");
      // 设置筛选器统计facet
      solrQuery.setFacet(true);
      solrQuery.addFacetField(FieldUtil.BRAND_CODE);
      solrQuery.setFacetMinCount(1);
      // 排序，品牌码升序，上架时间逆序
      List<SortClause> SortClauseList = new ArrayList<SortClause>();
      SortClauseList.add(new SortClause(FieldUtil.BRAND_CODE, ORDER.asc));
      SortClauseList.add(new SortClause(FieldUtil.ON_SALE_DATE, ORDER.desc));
      solrQuery.setSorts(SortClauseList);
      // 设置返回field
      solrQuery.setFields(FieldUtil.PRODUCT_UUID, FieldUtil.BRAND_CODE, FieldUtil.PRODUCT_ID, FieldUtil.PRODUCT_NAME,
          FieldUtil.STOCK, FieldUtil.PRODUCT_TAG, FieldUtil.SALE_COUNT, FieldUtil.DISCOUNT);
      // 默认最多返回一万条记录
      solrQuery.setStart(0);
      int maxRows = 10000;
      solrQuery.setRows(maxRows);
      QueryResponse queryResponse = cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, solrQuery);
      SolrDocumentList documentList = queryResponse.getResults();
      long total = documentList.getNumFound(); // 总记录数
      if (total <= 0) {
        return result;
      }
      result.setTotal(total);

      // 返回商品数量
      if (StringUtils.isBlank(p.getRecordSize()) || !FormatUtil.isNumber(p.getRecordSize())) {
        p.setRecordSize("5");
      }
      int recordSize = Integer.parseInt(p.getRecordSize());
      // 获取品牌码对应的商品数量
      FacetField facetField = queryResponse.getFacetField(FieldUtil.BRAND_CODE);
      List<Count> counts = facetField.getValues();
      // 处理品牌码没有商品的情况
      for (Count count : counts) {
        if (count != null && StringUtils.isNotBlank(count.getName())) {
          brandCodeList.remove(count.getName().toUpperCase());
        }
      }
      List<ReturnProductByBrandCodeBean> returnProductByBrandCodeBeans = new ArrayList<ReturnProductByBrandCodeBean>();
      if (brandCodeList.size() > 0) {
        for (String brand : brandCodeList) {
          if (StringUtils.isNotBlank(brand)) {
            ReturnProductByBrandCodeBean bean = new ReturnProductByBrandCodeBean();
            bean.setBrandCode(brand);
            returnProductByBrandCodeBeans.add(bean);
          }
        }
      }
      // 获取起始位置，生成返回信息
      int startPosition = 0;
      int endPosition = 0;
      String brandCode = null;
      long brandCodeTotalCount = 0;
      int flag = counts.size();
      while (flag > 0) {
        // 超过最大记录数，跳出
        if (startPosition >= maxRows) {
          break;
        }
        SolrDocument doc = documentList.get(startPosition);
        if (doc != null) {
          brandCode = doc.get(FieldUtil.BRAND_CODE).toString();
        }
        if (StringUtils.isBlank(brandCode)) {
          continue;
        }
        int nextStartPosition = startPosition;
        for (Count count : counts) {
          if (count != null && StringUtils.equalsIgnoreCase(count.getName(), brandCode)) {
            brandCodeTotalCount = count.getCount();
            nextStartPosition += (int) brandCodeTotalCount;
            if (brandCodeTotalCount >= recordSize) {
              endPosition = recordSize - 1 + startPosition;
            } else {
              endPosition = (int) brandCodeTotalCount - 1 + startPosition;
            }
            break;
          }
        }
        ReturnProductByBrandCodeBean bean = returnReturnProductByBrandCodeBean(startPosition,
            endPosition, documentList, brandCode, brandCodeTotalCount);
        returnProductByBrandCodeBeans.add(bean);
        startPosition = nextStartPosition;
        flag--;
      }

      // 设置商品集合，按照商品码为key
      result.setReturnProductByBrandCodeBeans(returnProductByBrandCodeBeans);
    } catch (Exception e) {
      searchErrorLogService.log(e.getMessage(), 1);
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  public ProductQueryResult searchColor(ProductQuery query) {
    int count = 0;
    String productIdFlag = "";
    // int totalStart = 0;
    int resultCount = 0;
    int pageSize = query.getPageSize() > 0 ? query.getPageSize() : 40;
    int start = (query.getPageNo() - 1) * pageSize;
    int queryTotal = start + pageSize;
    if (start < 0) {
      start = 0;
    }
    boolean flag = true;
    Map<String, String> begin = new HashMap<String, String>();
    Map<String, String> end = new HashMap<String, String>();
    ProductQueryResult result = new ProductQueryResult();
    try {

      int queryTimes = 0;
      boolean isRetry = false;
      do {
        queryTimes++;
        SolrQuery solrQuery = packProductColorQuery(query, queryTotal);
        QueryResponse queryResponse = cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS, solrQuery);
        SolrDocumentList documentList = queryResponse.getResults();
        long total = documentList.getNumFound(); // 总记录数
        int pageGetNum = documentList.size(); // 当前条件得到当前页的记录数
        if (total <= 0) {
          return result;
        }
        if (pageGetNum < 1) { // 总结果数大于0，当前页的结果数小于1，则说明页数超出，直接跳转第一页，重新查询
          query.setPageNo(1);
          isRetry = true;
          continue;
        }

        int totalResult = 0;
        for (SolrDocument solrDocument : documentList) {
          List<Object> con = (List<Object>) solrDocument.getFieldValues("color_series");
          if (con != null) {
            totalResult += con.size();
          }
        }
        result.setTotal(totalResult);
        // 获取相关的高亮信息
        // Map<String, Map<String, List<String>>> high = queryResponse.getHighlighting();

        // 拼装商品id
        List<String> productIds = new ArrayList<String>();

        for (SolrDocument solrDocument : documentList) {
          // totalStart +=1;
          // if(totalStart >= start){
          List<Object> con = (List<Object>) solrDocument.getFieldValues("color_series");
          count += con.size();
          if (count > start) {
            // productIdFlag = solrDocument.get(FieldUtil.PRODUCT_ID).toString();
            productIdFlag = solrDocument.get(FieldUtil.PRODUCT_UUID).toString();
            productIds.add(productIdFlag);
            if (flag) {
              int manyBegin = count - start;
              if (manyBegin > 0) {
                int coMany = (con.size() - manyBegin);
                begin.put("productId", productIdFlag);
                begin.put("count", coMany + "");
                resultCount += manyBegin;
                flag = false;
              } else {
                // resultCount += con.size();
                flag = false;
              }
            } else {
              resultCount += con.size();
            }
            // map.put(solrDocument.get(FieldUtil.PRODUCT_ID).toString(), con.size());

            if (resultCount >= pageSize) {
              productIdFlag = (String) solrDocument.get("product_id");
              int many = resultCount - pageSize;
              if (many > 0) {
                end.put("productId", productIdFlag);
                end.put("count", (con.size() - many) + "");
              }
              break;
            }
          }
          // }
        }
        // 根据pid从redis批量获取缓存；如果没有，则从mysql获取，然后更新保存到redis
        Map<String, Product> redisProduct = readFromRedis(productIds);
        // result.setTotal(total);
        List<Product> list = new ArrayList<Product>();
        for (String pId : productIds) {
          Product p = redisProduct.get(pId);
          if (p == null) {
            continue;
          }
          // 邦购商品设置促销价格，不参加促销，则不予替换
          if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode())) {
            int appId = query.getAppId();
            switch (appId) {
            case 1:
              p.setSalesPrice(p.getPcPromotionPrice());
              break;
            case 2:
              p.setSalesPrice(p.getAppPromotionPrice());
              break;
            case 3:
              p.setSalesPrice(p.getWapPromotionPrice());
              break;
            case 4:
              p.setSalesPrice(p.getWebchatPromotionPrice());
              break;
            default:
              p.setSalesPrice(p.getPcPromotionPrice());
              break;
            }
          }
          String goodsTitle = p.getProductName();
          String discount = "";
          int stockR = 0;
          try {
            // 标题高亮（去掉高亮）
            // goodsTitle = getHighLight(high, productId, FieldUtil.PRODUCT_NAME, goodsTitle);
            // //库存
            // stock = Integer.parseInt(d.getFieldValue(FieldUtil.STOCK).toString());
            // //
            // discount = d.getFieldValue(FieldUtil.DISCOUNT).toString();
            stockR = p.getStock();
            discount = p.getDiscount();
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
          String id = "";
          String index = "";
          String idBegin = "";
          String indexBegin = "";
          int i = 1;
          int iBegin = 1;
          if (end.size() > 0) {
            id = end.get("productId");
            index = end.get("count");
          }
          if (begin.size() > 0) {
            idBegin = begin.get("productId");
            indexBegin = begin.get("count");
          }
          for (ColorProduct colorProduct : p.getColorProducts()) {
            if (p.getProductId().equals(id)) {
              if (Integer.parseInt(index) >= i) {
                Product productColor = new Product();
                PropertyUtils.copyProperties(productColor, p);
                String imgUrl = colorProduct.getImgUrl();
                productColor.setImgUrl(imgUrl);
                List<AttrValue> listValues = p.getValues();
                productColor.setValues(listValues);
                List<ColorProduct> listColor = new ArrayList<ColorProduct>();
                listColor.add(colorProduct);
                productColor.setColorProducts(listColor);
                productColor.setDiscount(discount);
                productColor.setTags(p.getTags());
                productColor.setStock(stockR);
                productColor.setProductName(goodsTitle);
                productColor.setDisplayTag(p.getDisplayTag());
                // p.setDisplayTag(TagChainUtil.getDisplayTag(p.getTags()));
                removeColorProductsWhichNoStock(productColor, query);
                // p = solrDocumentToProduct(d);
                list.add(productColor);
              }
              i += 1;
            } else if (p.getProductId().equals(idBegin)) {
              if (Integer.parseInt(indexBegin) < iBegin) {
                Product productColor = new Product();
                PropertyUtils.copyProperties(productColor, p);
                String imgUrl = colorProduct.getImgUrl();
                productColor.setImgUrl(imgUrl);
                List<AttrValue> listValues = p.getValues();
                productColor.setValues(listValues);
                List<ColorProduct> listColor = new ArrayList<ColorProduct>();
                listColor.add(colorProduct);
                productColor.setColorProducts(listColor);
                productColor.setDiscount(discount);
                productColor.setTags(p.getTags());
                productColor.setStock(stockR);
                productColor.setProductName(goodsTitle);
                productColor.setDisplayTag(p.getDisplayTag());
                // p.setDisplayTag(TagChainUtil.getDisplayTag(p.getTags()));
                removeColorProductsWhichNoStock(productColor, query);
                // p = solrDocumentToProduct(d);
                list.add(productColor);
              }
              iBegin += 1;
            } else {
              Product productColor = new Product();
              PropertyUtils.copyProperties(productColor, p);
              String imgUrl = colorProduct.getImgUrl();
              productColor.setImgUrl(imgUrl);
              List<AttrValue> listValues = p.getValues();
              productColor.setValues(listValues);
              List<ColorProduct> listColor = new ArrayList<ColorProduct>();
              listColor.add(colorProduct);
              productColor.setColorProducts(listColor);
              productColor.setDiscount(discount);
              productColor.setTags(p.getTags());
              productColor.setStock(stockR);
              productColor.setProductName(goodsTitle);
              productColor.setDisplayTag(p.getDisplayTag());
              // p.setDisplayTag(TagChainUtil.getDisplayTag(p.getTags()));
              removeColorProductsWhichNoStock(productColor, query);
              // p = solrDocumentToProduct(d);
              list.add(productColor);
            }
          }

          result.setList(list);
        }
      } while ((queryTimes <= 1 && isRetry));
    } catch (Exception e) {
      searchErrorLogService.log(e.getMessage(), 1);
      log.error(e.getMessage());
    }
    return result;
  }
}
