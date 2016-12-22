/*
 * 2014-9-24 下午4:10:06 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
// import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.commonbean.GettingUpdatedChannelGoodIdListParam;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.mapper.SynonymMapper;
import com.mbgo.mybatis.mbstock.mapper.StockMapper;
import com.mbgo.mybatis.mbstore.mapper.MallBeautyGoodsMapper;
// import com.mbgo.mybatis.mbshop.mapper.ProductInfoMapper;
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.constant.Symbol;
import com.mbgo.search.core.bean.index.AttrValue;
import com.mbgo.search.core.bean.index.BeautyInfo;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.SizeInfo;
import com.mbgo.search.core.bean.index.ThemeInfo;
import com.mbgo.search.core.bean.indexthrd.DocumentCreatorManager;
import com.mbgo.search.core.bean.indexthrd.DocumentUpdateManager;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.dataetl.stock.ProductStockManager;
import com.mbgo.search.core.dataetl.stock.StockProduct;
import com.mbgo.search.core.filter.price.PriceCaculator;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;
import com.mbgo.search.core.service.DataEtlService;
import com.mbgo.search.core.service.ProductIndexService;
import com.mbgo.search.core.service.SearchErrorLogService;
import com.mbgo.search.core.service.use4.CacheService;
import com.mbgo.search.core.tools.ClearVarnishCacheHandler;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.core.tools.JobLockHandler;
import com.mbgo.search.core.tools.LastIntervalRegister;
import com.mbgo.search.core.tools.TimeUtil;
import com.mbgo.search.util.StringUtil;
import com.metersbonwe.pcs.bean.ChannelGoodsMapping;
import com.metersbonwe.pcs.dao.ExtendedProductChannelGoodsMapper;
import com.metersbonwe.pcs.dao.SearchGoodsTagMapper;
import com.metersbonwe.pcs.dao.SkuLevelProductInfoMapper;
import com.metersbonwe.promotion.api.PlatformPromotionApi;
import com.metersbonwe.promotion.bean.APIPromotionBackMsgBean;
import com.metersbonwe.promotion.bean.PromoProductBean;

@Service("productIndexService")
public class ProductIndexServiceImpl implements ProductIndexService {

  private static final String GOODS_LOCK = "solrcloudgoodsLock";
  // private static final String GOODS_LOCK = "testsolrcloudgoodsLock";
  // private static final String GOODS_LOCK = "localsolrcloudgoodsLock";
  // private static final String GOODS_LOCK = "clonesolrcloudgoodsLock";

  private static final int defaultPidCount = 4000;

  @Value("#{solrboostconfig['nameboost']}")
  private String nameBoost;

  @Value("#{solrboostconfig['categoryboost']}")
  private String categoryBoost;

  @Value("#{solrboostconfig['colorboost']}")
  private String colorBoost;

  @Override
  public void rebuildProductIndex() {

    boolean isGetLock = JobLockHandler.getLock(GOODS_LOCK, 120);
    if (!isGetLock) {
      log.debug("rebuilding - acquiring lock failed");
      return;
    }
    String updateTime = lastIntervalRegister.getUpdateTimestampFromRedis();
    // 设置当前时间戳
    String currentTime = getCurrentTime();
    lastIntervalRegister.saveUpdateTimestampToRedis(currentTime);
    // 索引版本号，以当前时间毫秒数为准
    long currentVersion = System.currentTimeMillis();
    log.debug(
        "get lock[{}] success and rebuildProductIndex begin, version:{}",
        GOODS_LOCK, currentVersion);
    try {
      // 分页信息

      long totalRecord = extendedProductChannelGoodsMapper.countAll();

      if (totalRecord < 10) {
        searchErrorLogService.log(
            "数据库数据异常，重建索引失败！总数据量为：" + totalRecord, 1);
        return;
      }

      PageManager pageManager = new PageManager(totalRecord, defaultPidCount);

      while (pageManager.hasNextPage()) {
        long startPosition = pageManager.getFirst();
        long limitSize = pageManager.getMax();
        List<String> productIds = dataEtlService.getProductIds(
            startPosition, limitSize);
        if (productIds == null || productIds.size() < 1) {
          log.debug("has no productIds,startPosition:" + startPosition);
          continue;
        }
        List<Product> products = dataEtlService.getProductList(-1, -1, productIds);

        DocumentCreatorManager documentCreatorManager = new DocumentCreatorManager(
            this, currentVersion, products);
        documentCreatorManager.run();
        documentCreatorManager.waiFinish();
      }

      // SolrQuery query = new SolrQuery("*:*");
      // query.setRows(0);
      // log.debug(IndexGoodsSolrServer.query(query).getResults()
      // .getNumFound()
      // + " goods in the index before sweeping");

      cloudSearchGoodsSolrClient.deleteByQuery(SolrCollectionNameDefineBean.GOODS,
          FieldUtil.DATA_VERSION + ": [0 TO " + (currentVersion - 1) + "]");
      // cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);

      long spent = System.currentTimeMillis() - currentVersion;
      log.debug(spent + "ms spent to rebuild");
    } catch (Exception e) {
      log.error(e.getMessage());
      searchErrorLogService.log(e.getMessage(), 1);
    } finally {
      if (isGetLock) {
        // use timed task later to fulfill optimization

        // try {
        // IndexGoodsSolrServer.optimize();
        // } catch (Exception e) {
        // }
        // log.debug("rebuildProductIndex end, version:{}",
        // currentVersion);
        JobLockHandler.closeLock(GOODS_LOCK);

        log.debug("start to clear varnish cache........");
        clearVarnishCacheHandler.clearCache(updateTime);

      }
    }
  }

  /*
   * @Override public void rebuildProductIndex() {
   * 
   * boolean isGetLock = JobLockHandler.getLock(GOODS_LOCK, 120); if (!isGetLock) {
   * log.debug("rebuilding - acquiring lock failed"); return; } // 设置当前时间戳 String currentTime =
   * getCurrentTime(); lastIntervalRegister.saveUpdateTimestampToRedis(currentTime); //
   * 索引版本号，以当前时间毫秒数为准 long currentVersion = System.currentTimeMillis(); log.debug(
   * "get lock[{}] success and rebuildProductIndex begin, version:{}", GOODS_LOCK, currentVersion);
   * try { // 分页信息
   * 
   * long totalRecord = extendedProductChannelGoodsMapper.countAll();
   * 
   * if (totalRecord < 10) { searchErrorLogService.log( "数据库数据异常，重建索引失败！总数据量为：" + totalRecord, 1);
   * return; } // 初始化线程池 Map<Integer, Future<Boolean>> resultMap = new HashMap<Integer,
   * Future<Boolean>>(); PageManager pageManager = new PageManager(totalRecord, defaultPidCount);
   * 
   * while (pageManager.hasNextPage()) { int startPosition = (int) pageManager.getFirst();
   * RebuildProductIndexThread rebuildProductIndexThread = new
   * RebuildProductIndexThread(startPosition, pageManager.getMax(), currentVersion, dataEtlService,
   * this); Future<Boolean> future = ThreadPoolUtil.submit(rebuildProductIndexThread);
   * resultMap.put(startPosition, future); log.debug("submit task:{}", startPosition); } // 检查线程完成结果
   * for (Map.Entry<Integer, Future<Boolean>> entry : resultMap.entrySet()) { long startPosition =
   * entry.getKey(); Future<Boolean> future = entry.getValue(); Boolean ok = future.get(); if (!ok)
   * { log.debug("failed task:{}", startPosition); } }
   * 
   * cloudSearchGoodsSolrClient.deleteByQuery(SolrCollectionNameDefineBean.GOODS,
   * FieldUtil.DATA_VERSION + ": [0 TO " + (currentVersion - 1) + "]"); //
   * cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
   * 
   * long spent = System.currentTimeMillis() - currentVersion; log.debug(spent +
   * "ms spent to rebuild"); // LastIntervalRegister.setInterval(spent); } catch (Exception e) {
   * log.error(e.getMessage()); searchErrorLogService.log(e.getMessage(), 1); } finally { if
   * (isGetLock) { commit(); JobLockHandler.closeLock(GOODS_LOCK); } } }
   */

  @Override
  public void addDocumentToServer(List<SolrInputDocument> productIndex,
      List<SolrInputDocument> colorProductIndex) {
    try {
      if (productIndex != null && productIndex.size() > 0) {

        cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, productIndex, 15000);
      }
    } catch (Exception e) {
      log.debug("There occurs a exception related to add products.");
      log.error(e.getMessage(), e);
      // add for debug
      log.error(e.getCause().toString());
    } /*
       * finally { log.debug("start to commit to solr server"); commit(); }
       */

  }

  private String getCurrentTime() {
    Date date = new Date();
    int min = date.getMinutes();
    int m = (min / 10) * 10;
    String time = TimeUtil.getStrDateByFormate(date, "yyyy-MM-dd HH:");
    if (m == 0) {
      time += "00:00";
    } else {

      time += m + ":00";
    }
    return time;
  }

  @Override
  public void commit() {
    try {
      cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
      // use timed task later to fulfill optimization

      // IndexGoodsSolrServer.optimize();
    } catch (Exception e) {}
  }

  @Override
  public void updateProductIndex() {
    // 如果有全场参加的多买，满减，满赠，则走全量重建流程
    APIPromotionBackMsgBean<PromoProductBean> apiReturnBean = null;
    String updateTime = null;
    try {
      apiReturnBean = promotionService.getProductOnPromoAll();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    PromoProductBean promoProductBean = null;
    if (apiReturnBean != null) {
      promoProductBean = apiReturnBean.getResult();
    }
    List<String> allShopList = null;
    if (promoProductBean != null) {
      allShopList = promoProductBean.getAllShopList();
    }
    if (allShopList != null && allShopList.size() > 0) {
      log.debug("有全场参加的多买，满减，满赠促销，rebuilding");
      rebuildProductIndex();
      return;
    } else {
      // use the same lock to keep index from being corrupted
      boolean isGetLockSuccess = JobLockHandler.getLock(GOODS_LOCK, 90);

      if (!isGetLockSuccess) {
        log.debug("updating - acquiring lock failed");
        return;
      }
      // 首先获取更新时间戳
      updateTime = lastIntervalRegister.getUpdateTimestampFromRedis();
      log.debug("get products(s) which updated after [" + updateTime
          + "]");
      // 再设置当前时间戳
      String currentTime = getCurrentTime();
      lastIntervalRegister.saveUpdateTimestampToRedis(currentTime);
      // 索引版本号，以当前时间毫秒数为准
      long currentVersion = System.currentTimeMillis();
      log.debug("get lock[{}] success and updateProductIndex, version:{}",
          GOODS_LOCK, currentVersion);
      try {

        List<String> updateProductIds = getUpdatedProductIds(updateTime);
        if (updateProductIds == null || updateProductIds.size() <= 0) {
          log.debug("需要更新的商品总数据量为0");
          updateProductIds = new ArrayList<String>();

        }

        // 从促销接口获取所有参加促销的上架商品productId集合
        List<String> promotionList = null;
        if (promoProductBean != null) {
          promotionList = promoProductBean.getProductList();
        }
        if (promotionList == null || promotionList.size() <= 0) {
          log.debug("全部参加促销的上架商品总数据量为0");
          promotionList = new ArrayList<String>();
        }
        if (promotionList.size() > updateProductIds.size()) {
          // merge productIds
          for (String pid : updateProductIds) {
            if (StringUtils.isNotBlank(pid)
                && !promotionList.contains(pid)) {
              promotionList.add(pid);
            }
          }

          log.debug("update : {}, {}", updateTime + "-"
              + promotionList.size(), promotionList);
          updateProductIndexByPids(promotionList, currentVersion);

        } else {
          // merge productIds
          for (String pid : promotionList) {
            if (StringUtils.isNotBlank(pid)
                && !updateProductIds.contains(pid)) {
              updateProductIds.add(pid);
            }
          }

          log.debug("update : {}, {}", updateTime + "-"
              + updateProductIds.size(), updateProductIds);
          updateProductIndexByPids(updateProductIds, currentVersion);

        }
        long spent = System.currentTimeMillis() - currentVersion;
        log.debug(spent + "ms spent to update");
        // LastIntervalRegister.setInterval(spent);

      } catch (Exception e) {
        log.error(e.getMessage());
      } finally {
        if (isGetLockSuccess) {
          // use timed task later to fulfill optimization

          // try {
          // IndexGoodsSolrServer.optimize();
          // } catch (SolrServerException e) {
          // } catch (IOException e) {
          // }
          // use the same lock to keep index from being corrupted
          JobLockHandler.closeLock(GOODS_LOCK);

          log.debug("start to clear varnish cache........");
          clearVarnishCacheHandler.clearCache(updateTime);

        }
      }
    }
  }

  private List<String> getUpdatedProductIds(String updateTime) {
    GettingUpdatedChannelGoodIdListParam param = new GettingUpdatedChannelGoodIdListParam();
    param.setUpdatedTimePointStr(updateTime);
    /*
     * String channelCode = AuxiliaryDataRefresher.getChannelCode(); if (channelCode != null &&
     * !channelCode.equals("") && !channelCode.equals(ChannelConst.CODE_OF_ALL_CHANNEL)) {
     * param.setChannelCode(channelCode); }
     */
    // 排除掉有范的商品
    param.setChannelCode(ChannelConst.DEFAULT_YOUFAN_CHANNEL_CODE);

    List<String> updateProductIds = extendedProductChannelGoodsMapper
        .getUpdatedChannelGoodIdList(param);
    List<String> idListOfGoodsPriceChanged = extendedProductChannelGoodsMapper
        .getIdListOfGoodsPriceChanged(param);
    List<String> idListOfGoodsAttributeChanged = extendedProductChannelGoodsMapper
        .getIdListOfGoodsAttributeChanged(param);
    List<String> idListOfGoodsColorMapChanged = extendedProductChannelGoodsMapper
        .getIdListOfGoodsColorMapChanged(param);
    List<String> idListOfGoodsSkuLevelInfoChanged = skuLevelProductInfoMapper
        .getIdListOfGoodsSkuLevelInfoChanged(param);
    List<String> themePids = mallThemeGoodsMapper
        .getUpdatedThemeInfoProductIds(updateTime);

    List<String> beautyPids = mallBeautyGoodsMapper.getUpdatedBeautyInfoProductIds(updateTime);
    List<String> idListOfGoodStockFluctuated = stockMapper
        .getCodeListOfGoodStockFluctuated(param);
    List<String> idListOfGoodsTagChanged = searchGoodsTagMapper.getAllProductIds(param);
    List<String> nonDuplicatedGidList = new ArrayList<String>();

    if (updateProductIds != null) {
      for (String pid : updateProductIds) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (idListOfGoodsPriceChanged != null) {
      for (String pid : idListOfGoodsPriceChanged) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (idListOfGoodsAttributeChanged != null) {
      for (String pid : idListOfGoodsAttributeChanged) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (idListOfGoodsColorMapChanged != null) {
      for (String pid : idListOfGoodsColorMapChanged) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (idListOfGoodsSkuLevelInfoChanged != null) {
      for (String pid : idListOfGoodsSkuLevelInfoChanged) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (themePids != null) {
      for (String pid : themePids) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (beautyPids != null) {
      for (String pid : beautyPids) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }

    if (idListOfGoodStockFluctuated != null) {
      for (String pid : idListOfGoodStockFluctuated) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    if (idListOfGoodsTagChanged != null) {
      for (String pid : idListOfGoodsTagChanged) {
        if (!nonDuplicatedGidList.contains(pid)) {
          nonDuplicatedGidList.add(pid);
        }
      }
    }
    return nonDuplicatedGidList;
  }

  @Override
  public void updateProductTags(List<String> pids) {
    if (pids != null && pids.size() > 0) {
      MybatisBean option = new MybatisBean(pids);
      List<Product> tags = dataEtlService.getGoodsTags(option);
      if (tags != null && tags.size() > 0) {
        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        try {
          for (Product p : tags) {
            SolrInputDocument updateDoc = new SolrInputDocument();

            Map<String, Object> ky = new HashMap<String, Object>();

            ky.put("set", p.getTags());
            updateDoc.setField(FieldUtil.PRODUCT_TAG, ky, 2);
            updateDoc.setField(FieldUtil.PRODUCT_ID,
                p.getProductId());

            docs.add(updateDoc);
          }

        } catch (Exception e) {
          log.error(e.getMessage());
        } finally {
          try {
            cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, docs);
            cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
          } catch (SolrServerException e) {
            log.error(e.getMessage(), e);
          } catch (IOException e) {
            log.error(e.getMessage(), e);
          }
        }
      }
    }
  }

  /**
   * 在业务端将关键字拼装，比在solr端用copyField效率要高很多
   * 
   * @return
   */
  @SuppressWarnings("unused")
  private String getProductKeyword() {
    return null;
  }

  private List<String> getSynos4Cat(String catName) throws Exception {
    if (catName != null && !catName.equals("")) {
      return AuxiliaryDataRefresher.getSynonyms(catName);
    }
    return null;
  }

  private List<String> getSynos4Brand(String brandName) throws Exception {
    if (brandName != null && !brandName.equals("")) {
      return AuxiliaryDataRefresher.getSynonyms(brandName);
    }
    return null;
  }

  public SolrInputDocument createDocument(Product p, long currentVersion)
      throws Exception {
    SolrInputDocument d = new SolrInputDocument();
    // no more check
    // try {
    // if (!checkProductValidate(p)) {
    // return null;
    // }
    // // 设置分类信息
    // // p.setAllCategoryIds(cacheService.getAllCategoryIds(p
    // // .getCategoryId()));
    // } catch (Exception e) {
    // }
    d.setField(FieldUtil.PRODUCT_UUID, p.getProductUuid());

    d.setField(FieldUtil.CHANNEL_CODE, p.getChannelCode());

    Date firstOnSellData = p.getFirstOnSellData();
    if (firstOnSellData != null) {
      d.setField(FieldUtil.FIRST_ON_SELL_DATA, firstOnSellData.getTime());
    }
    Date onSaleDate = p.getOnSaleDate();
    if (onSaleDate != null) {
      d.setField(FieldUtil.ON_SALE_DATE, onSaleDate.getTime());
    }
    d.setField(FieldUtil.PRODUCT_INFO, FieldUtil.METERSBONWE);
    // d.addField(FieldUtil.PRODUCT_INFO, cacheService.keywordInfo(p));
    d.setField(FieldUtil.PRODUCT_ID, p.getProductId());
    d.setField(FieldUtil.PRODUCT_CODE, p.getProductCode());
    d.setField(FieldUtil.STORE_ID, p.getStoreId());
    d.setField(FieldUtil.STORE_NAME, p.getStoreName());
    d.setField(FieldUtil.BRAND_CODE, p.getBrandCode() == null ? "" : p
        .getBrandCode().toUpperCase());
    d.setField(FieldUtil.BRAND_NAME, p.getBrandName());
    // 搜索美特斯邦威卫衣，出现全部美特斯邦威商品，添加同义词进索引所致
    /*
     * List<String> synos4BrandList = getSynos4Brand(p.getBrandName()); if (synos4BrandList != null
     * && synos4BrandList.size() > 0) { for (String syno4Brand : synos4BrandList) { if
     * (StringUtils.isNotBlank(syno4Brand)) { d.addField(FieldUtil.PRODUCT_INFO, syno4Brand); } } }
     */
    if (p.getBrandCode() != null && !p.getBrandCode().equals(""))
      d.setField(FieldUtil.BRAND_MAPPING,
          new StringBuilder().append(p.getBrandCode().toUpperCase())
              .append(Symbol.BRANDCODE_CONNECTOR_BRANDNAME)
              .append(p.getBrandName()).toString());
    d.setField(FieldUtil.PRODUCT_NAME, p.getProductName(),
        Long.parseLong(nameBoost));
    d.setField(FieldUtil.MARKET_PRICE, p.getMarketPrice());
    d.setField(FieldUtil.SALES_PRICE, p.getSalesPrice());

    // 折扣
    double discount = StringUtil.calculateDiscount(p.getSalesPrice(),
        p.getMarketPrice());
    d.setField(FieldUtil.DISCOUNT, discount);
    if (StringUtils.isNotBlank(StringUtil.getDiscountName(discount))) {

      d.addField(FieldUtil.PRODUCT_INFO, StringUtil.getDiscountName(discount));
    }
    if (StringUtils.isNotBlank(p.getGoodsName())) {

      d.addField(FieldUtil.PRODUCT_INFO, p.getGoodsName());
    }
    // d.addField(FieldUtil.PRODUCT_INFO, p.getSalePoint());

    // 计算价格段
    d.setField(FieldUtil.DYN_PRICE_INTERVAL,
        PriceCaculator.compartPrice(p.getSalesPrice()));
    d.setField(FieldUtil.IMAGE_URL, p.getImgUrl());

    long updateTime = -1;
    long createTimeLong = -1;
    Date createTime = p.getCreateTime();
    if (createTime != null) {
      createTimeLong = createTime.getTime();
      if (p.getStock() > 0) {
        updateTime = createTimeLong;
      }
    } else {
      createTimeLong = System.currentTimeMillis();
    }
    d.setField(FieldUtil.UPDATE_TIME, updateTime);
    d.setField(FieldUtil.CREATE_TIME, createTimeLong);

    d.setField(FieldUtil.GSI_RANK, p.getGsiRank());
    ChannelGoodsMapping channelGoods = AuxiliaryDataRefresher
        .getSaleCountMouth(p.getProductCode());
    if (channelGoods != null) {
      Integer saleCount = channelGoods.getSaleCountMonth();
      if (saleCount != null) {
        d.setField(FieldUtil.SALE_COUNT, saleCount);
        p.setSaleCount(saleCount);
      } else {
        d.setField(FieldUtil.SALE_COUNT, p.getSaleCount());
      }
    }
    d.setField(FieldUtil.DATA_VERSION, currentVersion);
    d.setField(FieldUtil.STOCK, p.getStock());
    // 无库存商品加上标识，并设置值为1，应用按照out_of_stock字段升序排列，最终达到无库存自动沉底的功能

    if (p.getStock() <= 0) {
      d.setField(FieldUtil.OUTOFSTOCK, 1);
    }

    d.setField(FieldUtil.PRICE_AND_STOCK,
        (int) (p.getSalesPrice() * p.getStock()));
    List<AttrValue> avs = p.getValues();
    if (avs != null && avs.size() > 0) {
      for (AttrValue av : avs) {
        if (av != null) {
          // 基础属性
          d.addField(FieldUtil.ATTRIBUTE_VALUE_CODE_M + av.getKey(),
              av.getCode());
          d.addField(
              FieldUtil.ATTRIBUTE_KEY_CODE_NAME_MAPPING,
              new StringBuilder().append(av.getKey())
                  .append(Symbol.AKEYCODE_CONNECTOR_AKEYNAME)
                  .append(av.getKeyName()).toString());
          d.addField(FieldUtil.PRODUCT_INFO, av.getName());
          d.addField(
              FieldUtil.ATTRIBUTE_VALUE_CODE_NAME_MAPPING,
              new StringBuilder().append(av.getCode())
                  .append(Symbol.AVALCODE_CONNECTOR_AVALNAME)
                  .append(av.getName()).toString());

          // 基础属性映射
          d.addField(FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M + av.getKey(),
              av.getSeriesCode() + Symbol.POSTFIX_OF_SERIES_CODE);
          d.addField(
              FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_NAME_MAPPING,
              new StringBuilder().append(av.getSeriesCode()).append(Symbol.POSTFIX_OF_SERIES_CODE)
                  .append(Symbol.AVALCODE_CONNECTOR_AVALNAME)
                  .append(av.getSeriesName()).toString());

        }
      }
    }

    // for (Integer id : p.getAllCategoryIds()) {
    // if (id > 0) {
    // Category c = cacheService.getCategoryById(id, false);
    // if (c != null) {
    // // solr configuration of production environment has not been
    // // modified
    // // d.setField(FieldUtil.CATEGORY_NAME,c.getCateName(),1f);
    // }
    // d.addField(FieldUtil.CATEGORY_ID_M, id);
    // }
    // }

    String basicCatId = String.valueOf(p.getCategoryId());
    List<String> allParentId = new ArrayList<String>();
    allParentId.add(basicCatId);
    AuxiliaryDataRefresher.getAllparentId(basicCatId, allParentId);
    if (allParentId != null && allParentId.size() > 0) {
      for (String pid : allParentId) {
        d.addField(FieldUtil.CATEGORY_ID_M, pid);
        // I think it's better to put basic category name into index.
        // d.addField(FieldUtil.PRODUCT_INFO, exCat.getCateName());
      }
    }

    d.setField(FieldUtil.CATEGORY_NAME, p.getCategoryName());

    List<String> synos4CatList = getSynos4Cat(p.getCategoryName());
    if (synos4CatList != null && synos4CatList.size() > 0) {
      for (String syno4Cat : synos4CatList) {
        if (StringUtils.isNotBlank(syno4Cat)) {

          d.addField(FieldUtil.PRODUCT_INFO, syno4Cat);
        }
      }
    }

    Set<String> parentCatNames = AuxiliaryDataRefresher
        .getParentCatNamesByCatId(String.valueOf(p.getCategoryId()));
    if (parentCatNames != null && parentCatNames.size() > 0) {
      for (String parentCatName : parentCatNames) {
        if (StringUtils.isNotBlank(parentCatName)) {
          d.addField(FieldUtil.PRODUCT_INFO, parentCatName);

        }
        List<String> synos4ParentCatList = getSynos4Cat(parentCatName);
        if (synos4ParentCatList != null
            && synos4ParentCatList.size() > 0) {
          for (String syno4ParentCat : synos4ParentCatList) {
            if (StringUtils.isNotBlank(syno4ParentCat)) {

              d.addField(FieldUtil.PRODUCT_INFO, syno4ParentCat);
            }
          }
        }
      }
    }
    // 设置主题
    List<ThemeInfo> themes = p.getThemes();
    if (themes != null && themes.size() > 0) {
      for (ThemeInfo ti : themes) {
        String tcode = ti.getCode();
        if (StringUtils.isBlank(tcode)) {
          continue;
        }
        d.addField(FieldUtil.THEME_CODE_M, tcode);
        d.addField(FieldUtil.THEME_ORDER_PREFIX + tcode, ti.getOrder());
      }
    }
    // 设置门店闪购
    List<BeautyInfo> beautys = p.getBeautys();
    if (beautys != null && beautys.size() > 0) {
      for (BeautyInfo bi : beautys) {
        String bcode = bi.getCode();
        if (StringUtils.isBlank(bcode)) {
          continue;
        }
        d.addField(FieldUtil.BEAUTY_CODE_M, bcode);
        d.addField(FieldUtil.BEAUTY_ORDER_PREFIX + bcode, bi.getOrder());
      }
    }
    // 设置标签
    Set<String> tags = p.getTags();
    if (tags != null && tags.size() > 0) {
      for (String tag : tags) {
        if (StringUtils.isNotBlank(tag)) {
          d.addField(FieldUtil.PRODUCT_TAG, tag);
        }
      }
    }
    // 设置显示标签
    String displayTag = p.getDisplayTag();
    if (StringUtils.isNotBlank(displayTag)) {
      d.addField(FieldUtil.PRODUCT_DISPLAY_TAG, displayTag);
    }

    // 设置各渠道促销id
    List<String> pcPromoIds = p.getPcPromotionIds();
    if (pcPromoIds != null && pcPromoIds.size() > 0) {
      for (String pid : pcPromoIds) {
        if (StringUtils.isNotBlank(pid)) {
          d.addField(FieldUtil.PC_PROMOTION_IDS, pid);
        }
      }
    }

    List<String> appPromoIds = p.getAppPromotionIds();
    if (appPromoIds != null && appPromoIds.size() > 0) {
      for (String pid : appPromoIds) {
        if (StringUtils.isNotBlank(pid)) {
          d.addField(FieldUtil.APP_PROMOTION_IDS, pid);
        }
      }
    }

    List<String> wapPromoIds = p.getWapPromotionIds();
    if (wapPromoIds != null && wapPromoIds.size() > 0) {
      for (String pid : wapPromoIds) {
        if (StringUtils.isNotBlank(pid)) {
          d.addField(FieldUtil.WAP_PROMOTION_IDS, pid);
        }
      }
    }

    List<String> webchatPromoIds = p.getWebchatPromotionIds();
    if (webchatPromoIds != null && webchatPromoIds.size() > 0) {
      for (String pid : webchatPromoIds) {
        if (StringUtils.isNotBlank(pid)) {
          d.addField(FieldUtil.WECHAT_PROMOTION_IDS, pid);
        }
      }
    }
    // 设置各渠道单品促销价格
    d.setField(FieldUtil.PC_PROMOTION_PRICE, p.getPcPromotionPrice());
    d.setField(FieldUtil.APP_PROMOTION_PRICE, p.getAppPromotionPrice());
    d.setField(FieldUtil.WAP_PROMOTION_PRICE, p.getWapPromotionPrice());
    d.setField(FieldUtil.WECHAT_PROMOTION_PRICE,
        p.getWebchatPromotionPrice());

    return d;
  }

  /**
   * 商品信息转solr输入文档
   * 
   * @param p
   * @param currentVersion
   * @return
   */
  public SolrInputDocument createProductDocument(Product p,
      long currentVersion) throws Exception {
    SolrInputDocument d = createDocument(p, currentVersion);
    // if (d == null) {
    // if (log.isDebugEnabled())
    // synchronized (lock) {
    // discardedNum++;
    // }
    // log.debug("prodcuct " + p.getProductId() + " discarded");
    // return null;
    // }

    // 保存商品信息到redis
    dataEtlService.saveProductToRedis(p);
    // 尺码
    List<SizeInfo> sizeInfos = p.getAllSizes();
    if (sizeInfos != null) {
      Set<String> sizeFilter = new HashSet<String>();
      for (SizeInfo s : sizeInfos) {
        String sizeCode = s.getSizeCode();
        if (sizeFilter.contains(sizeCode) || s.getSizeStatus() != 1) {// 尺寸下架
          continue;
        }
        sizeFilter.add(sizeCode);
        d.addField(FieldUtil.SIZE_CODE_M, sizeCode);
        if (s.getStock() > 0) {
          d.addField(FieldUtil.SIZE_CODE_M_STOCK, sizeCode);
        }
        StringBuilder sb = new StringBuilder();
        d.addField(
            FieldUtil.SIZE_MAPPING,
            sb.append(sizeCode)
                .append(Symbol.SIZECODE_CONNECTOR_SIZENAME)
                .append(s.getSizeName()).toString());
      }
    }

    // 尺码系
    // List<SizeInfo> sizeInfos = p.getAllSizes();
    if (sizeInfos != null) {
      Set<String> sizeFilter = new HashSet<String>();
      for (SizeInfo s : sizeInfos) {
        if (s == null) {
          continue;
        }
        String sizeSeriesCode = s.getSizeSeriesCode();
        if (StringUtils.isNotBlank(sizeSeriesCode) && StringUtils.isNotBlank(sizeSeriesCode)) {
          if (sizeFilter.contains(sizeSeriesCode) || s.getSizeStatus() != 1) {// 尺寸下架
            continue;
          }

          sizeFilter.add(sizeSeriesCode);
          d.addField(FieldUtil.SIZE_SERIES, sizeSeriesCode + Symbol.POSTFIX_OF_SERIES_CODE);
          StringBuilder sb = new StringBuilder();
          d.addField(
              FieldUtil.SIZE_SERIES_MAPPING,
              sb.append(sizeSeriesCode).append(Symbol.POSTFIX_OF_SERIES_CODE)
                  .append(Symbol.SIZECODE_CONNECTOR_SIZENAME)
                  .append(s.getSizeSeriesName()).toString());
        }
      }
    }

    // 色系
    StringBuilder sb = new StringBuilder();
    if (p.isValidate()) {
      List<ColorProduct> cps = p.getColorProducts();
      Set<String> colorSeriesCodeFilter = new HashSet<String>();
      for (ColorProduct cp : cps) {
        if (cp == null || cp.getCpStatus() != 1) {// 颜色上下架状态
          continue;
        }

        String colorSeriesCode = cp.getColorSeriesCode();

        String colorName = cp.getColorName();
        if (StringUtils.isNotBlank(colorSeriesCode)) {
          // 重复色系code不加入索引
          if (!colorSeriesCodeFilter.contains(colorSeriesCode)) {
            d.addField(FieldUtil.COLOR_SERIES, cp.getColorSeriesCode());
            d.addField(
                FieldUtil.COLOR_SYSTEM_MAPPING,
                new StringBuilder()
                    .append(colorSeriesCode)
                    .append(Symbol.COLORSYSTEMCODE_CONNECTOR_COLORSYSTEMNAME)
                    .append(cp.getColorSeriesName()).toString());
            colorSeriesCodeFilter.add(colorSeriesCode);
          }
          sb.append(colorName).append(" ").append(cp.getColorSeriesName())
              .append(" ");
        }

      }
      d.addField(FieldUtil.COLOR_INFO, sb.toString());
    }
    return d;
  }

  public List<UpdateOption> updateProductIndex(List<String> pids)
      throws Exception {
    List<UpdateOption> rs = new ArrayList<UpdateOption>();

    if (pids == null || pids.size() < 1) {
      return rs;
    }

    List<SolrInputDocument> productTemp = new ArrayList<SolrInputDocument>();
    List<String> deletePids = new ArrayList<String>();
    try {
      // 鍟嗗搧涓婁笅鏋讹紝澶勭悊pids锛屾壘鍑簎uid
      for (String pid : pids) {
        if (StringUtils.isNotBlank(pid) && pid.length() != 6) {
          deletePids.add(pid);
        }
      }
      for (String cPid : deletePids) {
        if (StringUtils.isNotBlank(cPid)) {
          pids.remove(cPid);
          String temp = cPid.substring(cPid.length() - 6);
          if (!pids.contains(temp)) {
            pids.add(temp);
          }
        }
      }
      List<Product> products = dataEtlService
          .getProductList(-1, -1, pids);

      if (products == null || products.size() < 1) {
        return rs;
      }

      long currentVersion = System.currentTimeMillis();

      // List<String> deletePids = new ArrayList<String>();

      for (Product p : products) {
        // 杩囨护鎺夌姸鎬佹甯哥殑鍟嗗搧
        // pids.remove(p.getProductId());
        deletePids.remove(p.getProductUuid());
        // 鍏ㄤ笅鏋禰鍏ㄨ壊鍏ㄧ爜涓嬫灦]鐘舵�鐨勫晢鍝�
        if (p.getpStatus() != 1 || p.getColorProducts().size() < 1) {
          log.debug("good [{}] has no valid color and size",
              p.getProductUuid());
          deletePids.add(p.getProductUuid());
          continue;
        }

        SolrInputDocument doc = createProductDocument(p, currentVersion);

        if (doc != null) {
          productTemp.add(doc);
          rs.add(UpdateOption.update(p.getProductId()).success());
        } else {
          rs.add(UpdateOption.update(p.getProductId()).failed());
        }

        if (productTemp.size() > 500) {
          cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, productTemp);
          productTemp = new ArrayList<SolrInputDocument>();
        }
      }
      // 闇�鍒犻櫎鐨勫晢鍝�
      if (deletePids.size() > 0) {
        // deletePids.addAll(pids);
        log.debug("updateProductIndex delete goods: {}, {}", deletePids.size(), deletePids);
        // cloudSearchGoodsSolrClient.deleteById(SolrCollectionNameDefineBean.GOODS, deletePids,
        // 2000);
        cloudSearchGoodsSolrClient.deleteById(SolrCollectionNameDefineBean.GOODS, deletePids);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      // add for debug
      log.error(e.getCause().toString());
    } finally {

      if (productTemp.size() > 0) {
        try {
          cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, productTemp);
          productTemp = new ArrayList<SolrInputDocument>();
        } catch (Exception e2) {
          log.error(e2.getMessage(), e2);
          // add for debug
          log.error(e2.getCause().toString());
        }
      }
      cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
    }
    return rs;
  }

  @Override
  public void updateProductIndexByPids(List<String> pids, long currentVersion)
      throws Exception {
    if (pids == null || pids.size() < 1) {
      return;
    }
    try {
      PageManager pageManager = new PageManager(pids.size(), defaultPidCount);
      while (pageManager.hasNextPage()) {
        int startPosition = (int) pageManager.getFirst();
        int endPosition = pageManager.getToIndex();

        List<String> subProductIds = new ArrayList<String>();
        subProductIds.addAll(pids.subList(
            startPosition, endPosition));

        /*
         * List<String> subProductIds = pids.subList( startPosition, endPosition);
         */

        // 商品上下架，处理productIds，找出uuid
        List<String> deleteproductIds = Collections.synchronizedList(new ArrayList<String>());
        for (String pid : subProductIds) {
          if (StringUtils.isNotBlank(pid) && pid.length() != 6) {
            deleteproductIds.add(pid);
          }
        }

        for (String cPid : deleteproductIds) {
          if (StringUtils.isNotBlank(cPid)) {
            subProductIds.remove(cPid);
            String temp = cPid.substring(cPid.length() - 6);
            if (!subProductIds.contains(temp)) {
              subProductIds.add(temp);
            }
          }
        }
        if (subProductIds == null || subProductIds.size() < 1) {
          log.debug("has no productIds,startPosition:" + startPosition);
          continue;
        }
        List<Product> products = dataEtlService.getProductList(-1, -1, subProductIds);

        DocumentUpdateManager documentUpdateManager = new DocumentUpdateManager(
            this, currentVersion, products, deleteproductIds);
        documentUpdateManager.run();
        documentUpdateManager.waiFinish();
        // 需要删除的商品
        if (deleteproductIds.size() > 0) {
          log.debug("updateProductIndex delete goods: {}, {}", deleteproductIds.size(), deleteproductIds);
          deleteProductIndex(deleteproductIds);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      // add for debug
      log.error(e.getCause().toString());
    }
  }

  /*
   * @Override public boolean updateProductIndexByPids(List<String> pids, long currentVersion)
   * throws Exception { boolean successFlag = false; if (pids == null || pids.size() < 1) { return
   * true; } try { // 初始化线程池 Map<Integer, Future<Boolean>> resultMap = new HashMap<Integer,
   * Future<Boolean>>(); PageManager pageManager = new PageManager(pids.size(), defaultPidCount);
   * while (pageManager.hasNextPage()) { int startPosition = (int) pageManager.getFirst();
   * List<String> subProductIds = new ArrayList<String>(); subProductIds.addAll(pids.subList(
   * startPosition, pageManager.getToIndex()));
   * 
   * UpdateProductIndexThread updateProductIndexThread = new UpdateProductIndexThread(subProductIds,
   * currentVersion, dataEtlService, this); Future<Boolean> future =
   * ThreadPoolUtil.submit(updateProductIndexThread); resultMap.put(startPosition, future);
   * log.debug("submit task:{}", startPosition);
   * 
   * } // 检查线程完成结果 for (Map.Entry<Integer, Future<Boolean>> entry : resultMap.entrySet()) { long
   * startPosition = entry.getKey(); Future<Boolean> future = entry.getValue(); Boolean ok =
   * future.get(); if (!ok) { log.debug("failed task:{}", startPosition); } } successFlag = true; }
   * catch (Exception e) { log.error(e.getMessage()); // add for debug
   * log.error(e.getCause().toString()); } finally { commit(); } return successFlag; }
   */

  @Override
  public boolean deleteProductIndex(List<String> pids) throws Exception {
    try {
      if (pids != null && pids.size() > 0) {
        for (String productCode : pids) {
          cloudSearchGoodsSolrClient.deleteById(SolrCollectionNameDefineBean.GOODS, productCode);
        }
        // cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
        return true;
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  @Override
  public void sycnProductStocks(List<String> pids) {
    if (pids == null || pids.size() < 1) {
      return;
    }
    List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    try {

      ProductStockManager manager = dataEtlService
          .sycnProductStocks(pids);

      for (StockProduct p : manager.getProductStockMap().values()) {
        String pid = p.getProductId();
        int stock = p.getStock();

        List<String> sizeCodes = p.getProductSize();

        SolrInputDocument productDoc = createStockUpdateDocument(
            FieldUtil.PRODUCT_ID, pid, stock, sizeCodes);
        if (productDoc != null) {
          docs.add(productDoc);
        } else {
          continue;
        }

        if (docs.size() > 100) {
          cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, docs);
          docs = new ArrayList<SolrInputDocument>();
        }
      }

    } catch (Exception e) {

    } finally {
      try {
        if (docs.size() > 0) {
          cloudSearchGoodsSolrClient.add(SolrCollectionNameDefineBean.GOODS, docs);
        }
        cloudSearchGoodsSolrClient.commit(SolrCollectionNameDefineBean.GOODS);
        cloudSearchGoodsSolrClient.optimize(SolrCollectionNameDefineBean.GOODS);
      } catch (Exception e2) {}
    }
  }

  // The only method call this is 'OutUpdateHandler.synPromotionInfoToIndex'
  // which is private!!! So I comment out this method body.
  @Override
  public void sycnProductPromotions(List<String> pids) {
    // List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    // List<SolrInputDocument> colorDocs = new
    // ArrayList<SolrInputDocument>();
    //
    // try {
    // if(pids == null || pids.size() < 1) {
    // return;
    // }
    //
    // List<Product> products = dataEtlService.sycnProductPromotions(pids);
    //
    // for(Product p : products) {
    // if(!p.isValidate()) {
    // continue;
    // }
    // SolrInputDocument productDoc = createPromotionUpdateDocument(p,
    // FieldUtil.PRODUCT_ID, p.getProductId());
    // if(productDoc != null) {
    // docs.add(productDoc);
    // } else {
    // continue;
    // }
    // List<ColorProduct> cps = p.getColorProducts();
    // for(ColorProduct cp : cps) {
    // SolrInputDocument colorProductDoc = createPromotionUpdateDocument(p,
    // FieldUtil.COLOR_CODE_ID, cp.getColorCodeId());
    // if(colorDocs != null) {
    // colorDocs.add(colorProductDoc);
    // }
    // }
    //
    // if(docs.size() > 100) {
    // IndexGoodsSolrServer.add(docs);
    // docs = new ArrayList<SolrInputDocument>();
    // if(colorDocs.size() > 0) {
    // IndexColorGoodsSolrServer.add(colorDocs);
    // colorDocs = new ArrayList<SolrInputDocument>();
    // }
    // }
    // }
    //
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // } finally {
    // try {
    // if(docs.size() > 100) {
    // IndexGoodsSolrServer.add(docs);
    // }
    // if(colorDocs.size() > 0) {
    // IndexColorGoodsSolrServer.add(colorDocs);
    // }
    // IndexGoodsSolrServer.commit();
    // IndexGoodsSolrServer.optimize();
    // IndexColorGoodsSolrServer.commit();
    // IndexColorGoodsSolrServer.optimize();
    // } catch (Exception e2) {
    // }
    // }
  }

  // private SolrInputDocument createPromotionUpdateDocument(Product p,
  // String uniqueKey, String keyValue) {
  // System.out.println(keyValue);
  // List<PromotionInfo> infos = p.getPromotionInfos();
  // if (infos == null || infos.size() < 1) {
  // return null;
  // }
  // List<String> promotionIds = new ArrayList<String>();
  // List<String> promotionInfos = new ArrayList<String>();
  //
  // for (PromotionInfo pi : infos) {
  // promotionIds.add(pi.getPromotionId() + "");
  // promotionInfos.add(pi.info());
  // }
  // System.out.println(promotionIds);
  // System.out.println(promotionInfos);
  //
  // SolrInputDocument updateDoc = new SolrInputDocument();
  // try {
  // updateDoc.setField(uniqueKey, keyValue);
  // setUpdateFiled(updateDoc, FieldUtil.PROMOTION_IDS, promotionIds);
  // setUpdateFiled(updateDoc, FieldUtil.PROMOTION_NAME, promotionInfos);
  // } catch (Exception e) {
  // return null;
  // }
  //
  // return updateDoc;
  // }

  private void setUpdateFiled(SolrInputDocument doc, String k, Object values) {
    Map<String, Object> ky = new HashMap<String, Object>();

    ky.put("set", values);

    doc.setField(k, ky);
  }

  private SolrInputDocument createStockUpdateDocument(String uniqueKey,
      String keyValue, int stock, List<String> sizes) {
    SolrInputDocument updateDoc = new SolrInputDocument();
    try {
      if (sizes == null) {
        sizes = new ArrayList<String>();
      }
      updateDoc.setField(uniqueKey, keyValue);

      setUpdateFiled(updateDoc, FieldUtil.STOCK, stock);
      setUpdateFiled(updateDoc, FieldUtil.SIZE_CODE_M_STOCK, sizes);
      setUpdateFiled(updateDoc, FieldUtil.UPDATE_TIME,
          System.currentTimeMillis());

    } catch (Exception e) {
      return null;
    }

    return updateDoc;
  }

  private static Logger log = LoggerFactory
      .getLogger(ProductIndexServiceImpl.class);

  @Autowired
  private SearchGoodsTagMapper searchGoodsTagMapper;
  @Autowired
  private ExtendedProductChannelGoodsMapper extendedProductChannelGoodsMapper;
  @Autowired
  private MallThemeGoodsMapper mallThemeGoodsMapper;
  @Autowired
  private MallBeautyGoodsMapper mallBeautyGoodsMapper;
  @Autowired
  private SkuLevelProductInfoMapper skuLevelProductInfoMapper;
  @Autowired
  private StockMapper stockMapper;
  @Autowired
  private SynonymMapper synonymMapper;
  @Autowired
  private PlatformPromotionApi promotionService;

  /*
   * @Resource(name = "lbIndexGoodsSolrServer") private LBHttpSolrClient IndexGoodsSolrServer;
   */
  @Resource(name = "cloudSearchGoodsSolrClient")
  private CloudSolrClient cloudSearchGoodsSolrClient;

  @Resource(name = "dataEtlService")
  private DataEtlService dataEtlService;
  @Resource(name = "cacheService")
  private CacheService cacheService;
  @Resource(name = "searchErrorLogService")
  private SearchErrorLogService searchErrorLogService;

  @Resource(name = "lastIntervalRegister")
  private LastIntervalRegister lastIntervalRegister;

  @Resource(name = "clearVarnishCacheHandler")
  private ClearVarnishCacheHandler clearVarnishCacheHandler;

  public DataEtlService getDataEtlService() {
    return dataEtlService;
  }

  public void setDataEtlService(DataEtlService dataEtlService) {
    this.dataEtlService = dataEtlService;
  }

  /**
   * 开启或者关闭自动重建索引
   */
  @Override
  public String rebuildAndUpdateSwitch(String password, int openFlag) {
    try {
      if (StringUtils.isBlank(password) || !StringUtils.equals(password, "mbsearch2014")) {
        return "密码有误";
      }
      lastIntervalRegister.saveRebuildAndUpdateSwitchStatusToRedis(openFlag);
      if (openFlag == 0) {
        return "关闭成功";
      } else {
        return "开启成功";
      }
    } catch (Exception e) {

    }
    return "程序出错，请重试";

  }

}
