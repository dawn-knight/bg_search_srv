/*
 * 2014-9-24 下午3:58:57 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.constant.ExCategoryConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.filter.attr.AttributeCaculatorManager;
import com.mbgo.search.core.filter.brand.BrandCaculator;
import com.mbgo.search.core.filter.category.Category;
import com.mbgo.search.core.filter.category.CategoryCrumbs;
import com.mbgo.search.core.filter.category.OneCategory;
import com.mbgo.search.core.filter.color.ColorCaculator;
import com.mbgo.search.core.filter.itf.FilterBeanContainer;
import com.mbgo.search.core.filter.price.PriceCaculator;
import com.mbgo.search.core.filter.price.PriceRange;
import com.mbgo.search.core.filter.size.SizeCaculator;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;
import com.mbgo.search.core.service.use4.CacheService;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.core.tools.query.SpliteQuery;
import com.mbgo.search.util.AutokeyConvernt;

public abstract class BasicService {

  private static Logger log = LoggerFactory.getLogger(BasicService.class);

  @Value("#{filterAttributeFacetFieldConfig['facetField']}")
  private String facetField;

  @Value("#{facetFieldVisibleLevelConfig['visibleLevel']}")
  private String visibleLevel;

  /**
   * 商品查询
   * 
   * @param query
   * @return
   */
  public SolrQuery packProductQuery(ProductQuery query) {
    SolrQuery solrQuery = new SolrQuery();

    // 设置查询关键字
    setSolrKeyword(query, solrQuery);

    // 设置筛选器统计facet
    // setSolrFacet(solrQuery);

    setSortField(query, solrQuery);

    // 设置分页
    setSolrPage(query, solrQuery);

    boolean isAndFirst = query.isAndFirst();

    try {

      // 查询语句（query语句）
      StringBuffer filterQueries = new StringBuffer();

      // 分类id
      String ids = query.getCid();
      if (StringUtils.isNotBlank(ids)) {
        // 分类值拼接，值用英文逗号或竖线分割，逗号“,”表示且，竖线“|”表示或
        SpliteQuery temp = new SpliteQuery(FieldUtil.CATEGORY_ID_M, ids);
        String cidQueryString = temp.getQueryString(isAndFirst);
        if (StringUtils.isNotBlank(cidQueryString)) {
          filterQueries.append(" AND ").append(cidQueryString);
          solrQuery.addFilterQuery(cidQueryString);
        }
      }
      // 渠道号
      if (StringUtils.isNotBlank(query.getChannelCode())) {
        String channelCodeQuery = analyzeQuery(FieldUtil.CHANNEL_CODE, query.getChannelCode(), isAndFirst);
        if (StringUtils.isNotBlank(channelCodeQuery)) {
          filterQueries.append(" AND ").append(channelCodeQuery);
        }
      }
      // 商品id
      if (StringUtils.isNotBlank(query.getProductId())) {
        String productIdQuery = analyzeQuery(FieldUtil.PRODUCT_ID, query.getProductId(), isAndFirst);
        if (StringUtils.isNotBlank(productIdQuery)) {
          filterQueries.append(" AND ").append(productIdQuery);
        }
      }

      float maxPrice = query.getPrmax();
      float minPrice = query.getPrmin();
      if (maxPrice > 0 && minPrice >= 0) {
        String priceStr = "(" + FieldUtil.SALES_PRICE + ":[" + minPrice + " TO " + maxPrice + "])";
        filterQueries.append(" AND " + priceStr);
      }

      // 库存
      int stock = query.getStock();
      if (stock > 0) {
        String avnStr = "(" + FieldUtil.STOCK + ":[" + stock + " TO * ])";
        filterQueries.append(" AND ").append(avnStr);
      }
      // 尺寸
      if (StringUtils.isNotBlank(query.getSizeCode())) {
        String sizeCodeQuery = null;
        if (StringUtils.isNumeric(query.getSizeCode())) {
          sizeCodeQuery = analyzeQuery(FieldUtil.SIZE_CODE_M, query.getSizeCode(),
              false, isAndFirst);
        } else {
          sizeCodeQuery = analyzeQuery(FieldUtil.SIZE_SERIES, query.getSizeCode(),
              false, isAndFirst);
        }
        if (StringUtils.isNotBlank(sizeCodeQuery)) {
          filterQueries.append(" AND ").append(sizeCodeQuery);
        }
      }

      // 折扣
      float disMax = query.getDisMax();
      float disMin = query.getDisMin();
      if (disMax > 0 && disMin >= 0) {
        String discountStr = "(" + FieldUtil.DISCOUNT + ":[" + disMin + " TO " + disMax + "])";
        filterQueries.append(" AND ").append(discountStr);

      }

      if (StringUtils.isNotBlank(query.getBrand())) {
        // 品牌code
        String brandQuery = analyzeQuery(
            FieldUtil.BRAND_CODE, query.getBrand(), isAndFirst);
        if (StringUtils.isNotBlank(brandQuery)) {
          filterQueries.append(" AND ").append(brandQuery);
        }
      }

      if (StringUtils.isNotBlank(query.getColor())) {
        // 适合颜色
        String colorQuery = analyzeQuery(
            FieldUtil.COLOR_SERIES, query.getColor(), isAndFirst);
        if (StringUtils.isNotBlank(colorQuery)) {
          filterQueries.append(" AND ").append(colorQuery);
        }
      }

      if (StringUtils.isNotBlank(query.getTags())) {
        // 标签
        String tagQuery = analyzeQuery(
            FieldUtil.PRODUCT_TAG, query.getTags(), isAndFirst);
        if (StringUtils.isNotBlank(tagQuery)) {
          filterQueries.append(" AND ").append(tagQuery);
        }
      }

      // 主题
      if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getThemeCode())) {
        String themeCodeQuery = analyzeQuery(FieldUtil.THEME_CODE_M, query.getThemeCode(), isAndFirst);
        if (StringUtils.isNotBlank(themeCodeQuery)) {
          filterQueries.append(" AND ").append(themeCodeQuery);
        }
      }

      // 门店闪购
      if (!StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getBeautyCode())) {
        String beautyCodeQuery = analyzeQuery(FieldUtil.BEAUTY_CODE_M, query.getBeautyCode(), isAndFirst);
        if (StringUtils.isNotBlank(beautyCodeQuery)) {
          filterQueries.append(" AND ").append(beautyCodeQuery);
        }
      }

      // 促销
      if (StringUtils.isNotBlank(query.getPromotionId())) {
        int appId = query.getAppId();
        filterQueries = queryPromoIdByAppId(filterQueries, appId, query.getPromotionId(), isAndFirst);
      }

      // 属性
      String attr = query.getAttrValue();
      if (StringUtils.isNotBlank(attr)) {
        String[] attrsArray = attr.split(";");
        for (String kvs : attrsArray) {
          String[] kv = kvs.split(":");
          if (kv.length != 2) {
            continue;
          }
          String vs = kv[1];
          if (StringUtils.isNotBlank(vs)) {
            String k = null;
            if (StringUtils.isNumeric(vs)) {
              k = FieldUtil.ATTRIBUTE_VALUE_CODE_M + kv[0];
            } else {
              k = FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M + kv[0];
            }
            String attrQuery = null;
            attrQuery = analyzeQuery(k, vs, isAndFirst);
            if (StringUtils.isNotBlank(attrQuery)) {
              filterQueries
                  .append(" AND ").append(attrQuery);
            }
          }
        }
      }

      // solrQuery.setHighlight(true);
      /*
       * hl.usePhraseHighlighter: 如果一个查询中含有短语（引号框起来的）那么会保证一定要完全匹配短语的才会被高亮。 默认值：true
       */
      // query.setParam("hl.usePhraseHighlighter", false);
      // solrQuery.addHighlightField(FieldUtil.PRODUCT_NAME);
      // 以下两个方法主要是在高亮的关键字前后加上html代码
      // solrQuery.setHighlightSimplePre(query.getHighlightPre());
      // solrQuery.setHighlightSimplePost(query.getHighlightLast());

      String qs = solrQuery.getQuery();
      if (StringUtils.isNotBlank(filterQueries.toString())) {
        solrQuery.setQuery(qs + filterQueries.toString());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return solrQuery;
  }

  private StringBuffer queryPromoIdByAppId(StringBuffer filterQueries, int appId, String promotionId, boolean isAndFirst) {
    String promotionIdQuery = null;
    switch (appId) {
    case 1:
      promotionIdQuery = analyzeQuery(FieldUtil.PC_PROMOTION_IDS, promotionId, isAndFirst);
      break;
    case 2:
      promotionIdQuery = analyzeQuery(FieldUtil.APP_PROMOTION_IDS, promotionId, isAndFirst);
      break;
    case 3:
      promotionIdQuery = analyzeQuery(FieldUtil.WAP_PROMOTION_IDS, promotionId, isAndFirst);
      break;
    case 4:
      promotionIdQuery = analyzeQuery(FieldUtil.WECHAT_PROMOTION_IDS, promotionId, isAndFirst);
      break;
    default:
      promotionIdQuery = analyzeQuery(FieldUtil.PC_PROMOTION_IDS, promotionId, isAndFirst);
      break;
    }
    if (StringUtils.isNotBlank(promotionIdQuery)) {
      filterQueries
          .append(" AND ").append(promotionIdQuery);
    }
    return filterQueries;
  }

  public SolrQuery packProductColorQuery(ProductQuery query, int queryTotal) {
    SolrQuery solrQuery = new SolrQuery();

    // 设置查询关键字
    setSolrKeyword(query, solrQuery);

    setSortField(query, solrQuery);
    solrQuery.setStart(0);
    solrQuery.setRows(14000);
    boolean isAndFirst = query.isAndFirst();

    try {

      // 查询语句（query语句）
      StringBuffer filterQueries = new StringBuffer();
      // 分类id
      String ids = query.getCid();
      if (StringUtils.isNotBlank(ids)) {
        // 分类值拼接，值用英文逗号或竖线分割，逗号“,”表示且，竖线“|”表示或
        SpliteQuery temp = new SpliteQuery(FieldUtil.CATEGORY_ID_M, ids);
        String cidQueryString = temp.getQueryString(isAndFirst);
        if (StringUtils.isNotBlank(cidQueryString)) {
          filterQueries.append(" AND ").append(cidQueryString);
          solrQuery.addFilterQuery(cidQueryString);
        }
      }
      // 渠道号
      if (StringUtils.isNotBlank(query.getChannelCode())) {
        String channelCodeQuery = analyzeQuery(FieldUtil.CHANNEL_CODE, query.getChannelCode(), isAndFirst);
        if (StringUtils.isNotBlank(channelCodeQuery)) {
          filterQueries.append(" AND ").append(channelCodeQuery);
        }
      }
      // 商品id
      if (StringUtils.isNotBlank(query.getProductId())) {
        String productIdQuery = analyzeQuery(FieldUtil.PRODUCT_ID, query.getProductId(), isAndFirst);
        if (StringUtils.isNotBlank(productIdQuery)) {
          filterQueries.append(" AND ").append(productIdQuery);
        }
      }

      float maxPrice = query.getPrmax();
      float minPrice = query.getPrmin();
      if (maxPrice > 0 && minPrice >= 0) {
        String priceStr = "(" + FieldUtil.SALES_PRICE + ":[" + minPrice + " TO " + maxPrice + "])";
        filterQueries.append(" AND " + priceStr);
      }
      // 尺寸
      if (StringUtils.isNotBlank(query.getSizeCode())) {
        String sizeCodeQuery = null;
        if (StringUtils.isNumeric(query.getSizeCode())) {
          sizeCodeQuery = analyzeQuery(FieldUtil.SIZE_CODE_M, query.getSizeCode(),
              false, isAndFirst);
        } else {
          sizeCodeQuery = analyzeQuery(FieldUtil.SIZE_SERIES, query.getSizeCode(),
              false, isAndFirst);
        }
        if (StringUtils.isNotBlank(sizeCodeQuery)) {
          filterQueries.append(" AND ").append(sizeCodeQuery);
        }
      }
      // 库存
      int stock = query.getStock();
      if (stock > 0) {
        String avnStr = "(" + FieldUtil.STOCK + ":[" + stock + " TO * ])";
        filterQueries.append(" AND ").append(avnStr);
      }
      // 折扣
      float disMax = query.getDisMax();
      float disMin = query.getDisMin();
      if (disMax > 0 && disMin >= 0) {
        String discountStr = "(" + FieldUtil.DISCOUNT + ":[" + disMin + " TO " + disMax + "])";
        filterQueries.append(" AND ").append(discountStr);
      }

      if (StringUtils.isNotBlank(query.getBrand())) {
        // 品牌code
        String brandQuery = analyzeQuery(
            FieldUtil.BRAND_CODE, query.getBrand(), isAndFirst);
        if (StringUtils.isNotBlank(brandQuery)) {
          filterQueries.append(" AND ").append(brandQuery);
        }
      }

      if (StringUtils.isNotBlank(query.getColor())) {
        // 适合颜色
        String colorQuery = analyzeQuery(
            FieldUtil.COLOR_SERIES, query.getColor(), isAndFirst);
        if (StringUtils.isNotBlank(colorQuery)) {
          filterQueries.append(" AND ").append(colorQuery);
        }
      }

      if (StringUtils.isNotBlank(query.getTags())) {
        // 标签
        String tagQuery = analyzeQuery(
            FieldUtil.PRODUCT_TAG, query.getTags(), isAndFirst);
        if (StringUtils.isNotBlank(tagQuery)) {
          filterQueries.append(" AND ").append(tagQuery);
        }
      }

      // 主题
      if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getThemeCode())) {
        String themeCodeQuery = analyzeQuery(FieldUtil.THEME_CODE_M, query.getThemeCode(), isAndFirst);
        if (StringUtils.isNotBlank(themeCodeQuery)) {
          filterQueries.append(" AND ").append(themeCodeQuery);
        }
      }
      // 门店闪购
      if (!StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getBeautyCode())) {
        String beautyCodeQuery = analyzeQuery(FieldUtil.BEAUTY_CODE_M, query.getBeautyCode(), isAndFirst);
        if (StringUtils.isNotBlank(beautyCodeQuery)) {
          filterQueries.append(" AND ").append(beautyCodeQuery);
        }
      }
      // 促销
      if (StringUtils.isNotBlank(query.getPromotionId())) {
        int appId = query.getAppId();
        filterQueries = queryPromoIdByAppId(filterQueries, appId, query.getPromotionId(), isAndFirst);
      }
      // 属性
      String attr = query.getAttrValue();
      if (StringUtils.isNotBlank(attr)) {
        String[] attrsArray = attr.split(";");
        for (String kvs : attrsArray) {
          String[] kv = kvs.split(":");
          if (kv.length != 2) {
            continue;
          }
          String vs = kv[1];
          if (StringUtils.isNotBlank(vs)) {
            String k = null;
            if (StringUtils.isNumeric(vs)) {
              k = FieldUtil.ATTRIBUTE_VALUE_CODE_M + kv[0];
            } else {
              k = FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M + kv[0];
            }
            String attrQuery = null;
            attrQuery = analyzeQuery(k, vs, isAndFirst);
            if (StringUtils.isNotBlank(attrQuery)) {
              filterQueries
                  .append(" AND ").append(attrQuery);
            }
          }
        }
      }
      String qs = solrQuery.getQuery();
      if (StringUtils.isNotBlank(filterQueries.toString())) {
        solrQuery.setQuery(qs + filterQueries.toString());
      }
      // log.debug("color qg query:{}", solrQuery.getQuery());
      // log.debug(solrQuery.toString());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return solrQuery;
  }

  /**
   * 筛选器查询
   * 
   * @param query
   * @return
   */
  public SolrQuery packFilterQuery(ProductQuery query) {

    SolrQuery solrQuery = new SolrQuery();

    boolean isAndFirst = query.isAndFirst();

    // 设置查询关键字
    setSolrKeyword(query, solrQuery);

    // 设置筛选器统计facet
    setSolrFacet(solrQuery, query);

    // setSortField(query, solrQuery);
    // 设置分页
    // setSolrPage(query, solrQuery);
    // 生成筛选器，无需返回数据
    solrQuery.setStart(0);
    solrQuery.setRows(0);

    try {

      // 查询语句（query语句）
      StringBuffer filterQueries = new StringBuffer();
      // 分类id
      String ids = query.getCid();
      if (StringUtils.isNotBlank(ids)) {
        // 分类值拼接，值用英文逗号或竖线分割，逗号“,”表示且，竖线“|”表示或
        SpliteQuery temp = new SpliteQuery(FieldUtil.CATEGORY_ID_M, ids);
        solrQuery.addFilterQuery("{!tag=category}" + temp.getQueryString(isAndFirst));
      }
      // 价格
      float maxPrice = query.getPrmax();
      float minPrice = query.getPrmin();
      if (maxPrice > 0 && minPrice >= 0) {
        String priceStr = new StringBuilder().append(FieldUtil.SALES_PRICE).append(":[")
            .append(minPrice).append(" TO ").append(maxPrice).append("]").toString();
        // filterQueries.append( priceStr );
        solrQuery.addFilterQuery("{!tag=price}(" + priceStr + ")");
      }

      // 库存
      int stock = query.getStock();
      if (stock > 0) {
        String avnStr = new StringBuilder().append("(" + FieldUtil.STOCK).append(":[").append(stock)
            .append(" TO * ])").toString();
        filterQueries.append(" AND ").append(avnStr);
      }
      // 尺寸
      if (StringUtils.isNotBlank(query.getSizeCode())) {
        if (StringUtils.isNumeric(query.getSizeCode())) {
          solrQuery.addFilterQuery(analyzeQueryWithTag("size", FieldUtil.SIZE_CODE_M,
              query.getSizeCode(), false, isAndFirst));
        } else {
          solrQuery.addFilterQuery(analyzeQueryWithTag("size", FieldUtil.SIZE_SERIES,
              query.getSizeCode(), false, isAndFirst));
        }
      }
      // 折扣
      float disMax = query.getDisMax();
      float disMin = query.getDisMin();
      if (disMax > 0 && disMin >= 0) {
        String discountStr = new StringBuilder().append(FieldUtil.DISCOUNT).append(":[")
            .append(disMin).append(" TO ").append(disMax).append("]").toString();
        // filterQueries.append(" ").append(priceStr);
        solrQuery.addFilterQuery("{!tag=discount}(" + discountStr + ")");
      }

      // 品牌code
      if (StringUtils.isNotBlank(query.getBrand())) {
        solrQuery.addFilterQuery(analyzeQueryWithTag("brand", FieldUtil.BRAND_CODE, query.getBrand(),
            isAndFirst));
      }

      // 店铺
      // solrQuery.addFilterQuery(analyzeQueryWithTag("store", FieldUtil.STORE_ID,
      // query.getStoreId(), isAndFirst));

      // 适合颜色
      if (StringUtils.isNotBlank(query.getColor())) {
        solrQuery.addFilterQuery(analyzeQueryWithTag("color", FieldUtil.COLOR_SERIES,
            query.getColor(), isAndFirst));
      }

      // 标签
      if (StringUtils.isNotBlank(query.getTags())) {
        String tagQuery = analyzeQuery(
            FieldUtil.PRODUCT_TAG, query.getTags(), isAndFirst);
        if (StringUtils.isNotBlank(tagQuery)) {
          filterQueries.append(" AND ").append(tagQuery);
        }
      }

      // 主题
      if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getThemeCode())) {
        String themeCodeQuery = analyzeQuery(FieldUtil.THEME_CODE_M, query.getThemeCode(), isAndFirst);
        if (StringUtils.isNotBlank(themeCodeQuery)) {
          filterQueries.append(" AND ").append(themeCodeQuery);
        }
      }

      // 门店闪购
      if (!StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, query.getChannelCode()) && StringUtils.isNotBlank(query.getBeautyCode())) {
        String beautyCodeQuery = analyzeQuery(FieldUtil.BEAUTY_CODE_M, query.getBeautyCode(), isAndFirst);
        if (StringUtils.isNotBlank(beautyCodeQuery)) {
          filterQueries.append(" AND ").append(beautyCodeQuery);
        }
      }
      // 渠道号
      if (StringUtils.isNotBlank(query.getChannelCode())) {
        String channelCodeQuery = analyzeQuery(FieldUtil.CHANNEL_CODE, query.getChannelCode(), isAndFirst);
        if (StringUtils.isNotBlank(channelCodeQuery)) {
          filterQueries.append(" AND ").append(channelCodeQuery);
        }
      }
      // 产品id
      if (StringUtils.isNotBlank(query.getProductId())) {
        String productIdQuery = analyzeQuery(FieldUtil.PRODUCT_ID, query.getProductId(), isAndFirst);
        if (StringUtils.isNotBlank(productIdQuery)) {
          filterQueries.append(" AND ").append(productIdQuery);
        }
      }
      // 促销
      if (StringUtils.isNotBlank(query.getPromotionId())) {
        int appId = query.getAppId();
        filterQueries = queryPromoIdByAppId(filterQueries, appId, query.getPromotionId(), isAndFirst);
      }
      /*
       * filterQueries .append(analyzeQuery(FieldUtil.PROMOTION_IDS, query.getPromotionId(),
       * isAndFirst));
       */
      setSolrAttributeFacet(query, solrQuery);

      // ORDER order = ORDER.desc;
      // String orderField = FieldUtil;
      // solrQuery.setSort(orderField, order);
      // solrQuery.setHighlight(true);
      /*
       * hl.usePhraseHighlighter: 如果一个查询中含有短语（引号框起来的）那么会保证一定要完全匹配短语的才会被高亮。 默认值：true
       */
      // query.setParam("hl.usePhraseHighlighter", false);
      // solrQuery.addHighlightField(FieldUtil.PRODUCT_NAME);
      // // 以下两个方法主要是在高亮的关键字前后加上html代码
      // solrQuery.setHighlightSimplePre(query.getHighlightPre());
      // solrQuery.setHighlightSimplePost(query.getHighlightLast());

      String qs = solrQuery.getQuery();
      if (StringUtils.isNotBlank(filterQueries.toString())) {
        solrQuery.setQuery(qs + filterQueries.toString());
      }

      solrQuery.add("stats", "true");
      solrQuery.add("stats.field", FieldUtil.SALES_PRICE);
      // log.debug(solrQuery.toString());
      solrQuery.add("facet.threads", "-1");
      solrQuery.add("facet.method", "enum");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return solrQuery;
  }

  private void setSolrAttributeFacet(ProductQuery q, SolrQuery query) {
    // 搜索结果筛选项中，颜色、尺码、场合、性别、面料/材质在同一个表中，当选择了其中一个筛选项，其他筛选项就消失了，没有返回。
    // http://jira.bg.com/browse/MBXQ-339

    // 筛选属性
    boolean isAndFirst = q.isAndFirst();
    if (q != null && q.getAttrValue() != null) {
      String[] attrsArray = q.getAttrValue().split(";");
      for (String kvs : attrsArray) {
        String[] kv = kvs.split(":");
        if (kv.length != 2) {
          continue;
        }
        // String k = FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M + kv[0];
        String vs = kv[1];
        // query.addFacetField("{!ex=" + k + " key=" + k + "}" + k);
        if (StringUtils.isNotBlank(vs)) {
          if (StringUtils.isNumeric(vs)) {
            String k = FieldUtil.ATTRIBUTE_VALUE_CODE_M + kv[0];
            query.addFilterQuery(analyzeQueryWithTag(k, k, vs, isAndFirst));
          } else {
            String k = FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M + kv[0];
            query.addFilterQuery(analyzeQueryWithTag(k, k, vs, isAndFirst));
          }
        }
      }
    }
    // 现在写死，只需要返回如下属性
    // 性别：97 季节：85 场合：3 系列：86 版型：10 面料/材质：6 功能：8
    // 性别：97 季节：85 场合：3 系列：86 版型：10 面料/材质：6 功能：8 颜色：1 尺码：2
    int categoryLevel = getcategoryLevelByCid(q.getCid());
    String[] facetFields = facetField.split(",");
    if (facetFields != null && facetFields.length > 0) {
      for (String attr : facetFields) {
        if (StringUtils.isNotBlank(attr)) {
          if (categoryLevel + getVisibleLevelByAttrCode(attr) >= 4) {
            String k = new StringBuilder().append(FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M)
                .append(attr).toString();
            query.addFacetField(new StringBuilder().append("{!ex=").append(k).append(" key=").append(k)
                .append("}").append(k).toString());
          }
        }
      }
    }
  }

  protected String getHighLight(Map<String, Map<String, List<String>>> high, String key,
      String field, String oldTitle) throws Exception {
    String k = "";
    String rs = "";
    try {
      if (high != null && high.size() > 0 && high.get(key) != null) {
        k = high.get(key).get(field).get(0);
      }
      rs = k.replace("\\/", "/");
    } catch (Exception e) {
      return oldTitle;
    }

    return rs;
  }

  /**
   * 设置查询的排序信息
   * 
   * @param q
   * @param query
   */
  private void setSortField(ProductQuery q, SolrQuery query) {
    List<SortClause> sortClauses = new ArrayList<SortClause>();
    // 根据是否通过关键字搜索，来确定是否需要放弃文本相关性，解决无库存沉底的事情
    // 通过关键字搜索，保留文本相关性；否则按照库存标识来排序
    boolean hasWord = q.hasWord();

    if (q.isSort()) {
      if (!hasWord) {
        SortClause sortClause = new SortClause(FieldUtil.OUTOFSTOCK, ORDER.asc);
        sortClauses.add(sortClause);
        // query.setSort(FieldUtil.OUTOFSTOCK, ORDER.asc);
      }
      // 指定排序
      // 大于零表示升序，小于零表示降序
      ORDER order = ORDER.desc;
      if (q.getSortType() > 0) {
        order = ORDER.asc;
      } else if (q.getSortType() < 0) {
        order = ORDER.desc;
      }
      String orderField = FieldUtil.UPDATE_TIME;
      switch (q.getSortFieldNum()) {
      /**
       * 排序字段，取值如下，1：price，2：saleCount，3：marketDate，4：weekSaleCount，5：saticsfaction，6：discount，7：
       * priceAndStock
       */
      case 1:
        // orderField = FieldUtil.SALES_PRICE;
        if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, q.getChannelCode())) {
          int appId = q.getAppId();
          switch (appId) {
          case 1:
            orderField = FieldUtil.PC_PROMOTION_PRICE;
            break;
          case 2:
            orderField = FieldUtil.APP_PROMOTION_PRICE;
            break;
          case 3:
            orderField = FieldUtil.WAP_PROMOTION_PRICE;
            break;
          case 4:
            orderField = FieldUtil.WECHAT_PROMOTION_PRICE;
            break;
          default:
            orderField = FieldUtil.PC_PROMOTION_PRICE;
            break;
          }
        } else {
          orderField = FieldUtil.SALES_PRICE;
        }
        break;
      case 2:
        orderField = FieldUtil.SALE_COUNT;
        break;
      case 3:
        // orderField = FieldUtil.CREATE_TIME;
        orderField = FieldUtil.FIRST_ON_SELL_DATA;
        break;
      case 4:
        orderField = FieldUtil.SALE_COUNT;
        break;
      case 5:
        orderField = FieldUtil.GSI_RANK;
        break;
      case 6:
        orderField = FieldUtil.DISCOUNT;
        break;
      case 7:
        orderField = FieldUtil.PRICE_AND_STOCK;
        break;
      default:
        break;
      }
      SortClause sortClause = new SortClause(orderField, order);
      sortClauses.add(sortClause);
      // query.setSort(orderField, order);

    } else {
      if (StringUtils.isEmpty(q.getThemeCode()) && StringUtils.isEmpty(q.getBeautyCode()) && !hasWord) {
        SortClause sortClause = new SortClause(FieldUtil.OUTOFSTOCK, ORDER.asc);
        sortClauses.add(sortClause);
        // query.setSort(FieldUtil.OUTOFSTOCK, ORDER.asc);
      }
      // 主题页和闪购页，需要按照运营导款顺序排列
      if (StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, q.getChannelCode()) && StringUtils.isNotEmpty(q.getThemeCode())) {
        SortClause sortClause = new SortClause(FieldUtil.THEME_ORDER_PREFIX
            + q.getThemeCode(), ORDER.asc);
        sortClauses.add(sortClause);
        /*
         * query.setSort(FieldUtil.THEME_ORDER_PREFIX + q.getThemeCode(), ORDER.asc);
         */
      } else if (!StringUtils.equals(ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE, q.getChannelCode()) && StringUtils.isNotEmpty(q.getBeautyCode())) {
        SortClause sortClause = new SortClause(FieldUtil.BEAUTY_ORDER_PREFIX
            + q.getBeautyCode(), ORDER.asc);
        sortClauses.add(sortClause);

        /*
         * query.setSort(FieldUtil.BEAUTY_ORDER_PREFIX + q.getBeautyCode(), ORDER.asc);
         */
      }
    }
    // set sorts
    query.setSorts(sortClauses);
  }

  // 关键字
  public void setSolrKeyword(ProductQuery q, SolrQuery query) {
    // 如果查询中含有关键字，或含有“not”关键字，则需要按照关键字处理
    boolean isQuery = q.hasWord();
    StringBuilder sb = new StringBuilder("");
    if (isQuery) {
      String word = AutokeyConvernt.convertSpecialSymbolic(q.getWordBean());
      sb.append("(").append(FieldUtil.KEYWORD).append(":").append(word).append("");
      sb.append(" OR ");
      sb.append(FieldUtil.PRODUCT_NAME).append(":").append(word).append(")");
    } else {
      // 赋予默认关键字，保证文档boosts起到作用
      sb.append("").append(FieldUtil.KEYWORD).append(":\"").append(FieldUtil.METERSBONWE)
          .append("\"~1000");
    }

    query.setQuery(sb.toString());
  }

  /*
   * 针对用户搜索，cid是单值
   */
  private int getcategoryLevelByCid(String cid) {
    int defaultCid = ExCategoryConst.ROOT_EXCATEGORY_ID;
    // cid为null，说明在顶层分类，返回0
    if (StringUtils.isBlank(cid)) {
      return defaultCid;
    }
    String[] cidSplit = cid.split("[|]");
    if (cidSplit != null && cidSplit.length > 0) {
      defaultCid = Integer.valueOf(cidSplit[0]);
    }
    if (defaultCid == 0) {
      return defaultCid;
    }
    return AuxiliaryDataRefresher.getCategoryLevelByCid(defaultCid);
  }

  // 筛选器统计信息
  private void setSolrFacet(SolrQuery query, ProductQuery pq) {
    // 性别：97 季节：85 场合：3 系列：86 版型：10 面料/材质：6 功能：8 颜色：1 尺码：2
    // 控制筛选器属性的可见性
    // 可见性算法：visibleLevel+categoryLevel >= 4,则显示，否则不显示
    int categoryLevel = getcategoryLevelByCid(pq.getCid());
    query.setFacet(true);
    // 设置筛选器
    query.addFacetField("{!ex=brand}" + FieldUtil.BRAND_CODE);
    // query.addFacetField("{!ex=store}" + FieldUtil.STORE_ID);
    if (categoryLevel + getVisibleLevelByAttrCode("1") >= 4) {
      query.addFacetField("{!ex=color}" + FieldUtil.COLOR_SERIES);
    }
    // query.addFacetField("{!ex=price}" + FieldUtil.SALES_PRICE);
    query.addFacetField("{!ex=price}" + FieldUtil.DYN_PRICE_INTERVAL);
    if (categoryLevel + getVisibleLevelByAttrCode("2") >= 4) {
      query.addFacetField("{!ex=size key=" + FieldUtil.SIZE_SERIES + "}" + FieldUtil.SIZE_SERIES);
    }
    // query.addFacetField("{!ex=discount}" + FieldUtil.DISCOUNT);
    // query.addFacetField("{!ex=size key=" + FieldUtil.SIZE_CODE_M + "}" + FieldUtil.SIZE_CODE_M);
    // query.addFacetField("{!ex=category key=test}" + FieldUtil.CATEGORY_ID_M);//同类相斥
    // query.addFacetField("{!key=category_id}" + FieldUtil.CATEGORY_ID_M);
    query.addFacetField("{!ex=category}" + FieldUtil.CATEGORY_ID_M);
    /*
     * if (pq.getStock() > 0) { // 库存对尺寸的影响 // query.addFacetField("{!ex=size key=" +
     * FieldUtil.SIZE_CODE_M + "}" + FieldUtil.SIZE_CODE_M_STOCK); //
     * query.addFacetField("{!ex=size key=" + FieldUtil.SIZE_CODE_M + "}" + FieldUtil.SIZE_CODE_M);
     * query.addFacetField("{!ex=size key=" + FieldUtil.SIZE_CODE_M + "}" + FieldUtil.SIZE_CODE_M);
     * } else { query.addFacetField("{!ex=size key=" + FieldUtil.SIZE_CODE_M + "}" +
     * FieldUtil.SIZE_CODE_M); }
     */
    query.setFacetMinCount(1);
    query.setFacetLimit(2000);
  }

  // 分页
  private void setSolrPage(ProductQuery q, SolrQuery query) {
    // 页码
    int pageSize = q.getPageSize() > 0 ? q.getPageSize() : 40;
    int start = (q.getPageNo() - 1) * pageSize;
    if (start > 0) {
      query.setStart(start);
    } else {
      query.setStart(0);
    }
    query.setRows(pageSize);
  }

  public String analyzeQuery(String field, String val, boolean isAndFirst) {
    return analyzeQuery(field, val, false, isAndFirst);
  }

  /**
   * 对拼接得到的查询语句，添加标志，告诉solr，需要在生成filter的时候，将此条件排除
   * 
   * @param tag
   *          标志字符串，同query.addFacetField("{!ex=brand}bc");中的ex值对应
   * @param field
   * @param val
   * @return
   */
  public String analyzeQueryWithTag(String tag, String field, String val, boolean isAndFirst) {
    String t = analyzeQuery(field, val, false, isAndFirst);
    if (StringUtils.isBlank(t)) {
      return "";
    }
    return "{!tag=" + tag + "}" + t;
  }

  /**
   * 对拼接得到的查询语句，添加标志，告诉solr，需要在生成filter的时候，将此条件排除
   * 
   * @param tag
   *          标志字符串，同query.addFacetField("{!ex=brand}bc");中的ex值对应
   * @param field
   * @param val
   * @param isString
   * @return
   */
  public String analyzeQueryWithTag(String tag, String field, String val, boolean isString,
      boolean isAndFirst) {
    String t = analyzeQuery(field, val, isString, isAndFirst);
    if (StringUtils.isBlank(t)) {
      return "";
    }
    return "{!tag=" + tag + "}" + t;
  }

  /**
   * 分析参数，拼接成查询字符串；且优先 eg. attrValue:14,15,结果是(attrValue:14)AND(attrValue:15)
   * 
   * @param field
   * @param val
   * @param isString
   *          val是否是字符类型，true：需要加引号
   * @return
   */
  public String analyzeQuery(String field, String val, boolean isString, boolean isAndFirst) {
    if (StringUtils.isBlank(val)) {
      return "";
    }
    // val = val.toUpperCase();
    if (isString) {
      val = "\"" + val + "\"";
    }
    SpliteQuery temp = new SpliteQuery(field, val);
    return temp.getQueryString(isAndFirst);
  }

  private int getVisibleLevelByAttrCode(String attrCode) {
    // 默认显示层级是3级
    int level = 1;
    // 解析
    // visibleLevel=1:4,2:3,85:4,3:4,86:3,10:2,6:1,8:2
    String[] codeLevelMappings = visibleLevel.split(",");
    for (String codeLevelMapping : codeLevelMappings) {
      String[] clm = codeLevelMapping.split(":");
      if (StringUtils.equals(attrCode, clm[0])) {
        level = Integer.valueOf(clm[1]);
        break;
      }
    }
    return level;
  }

  /**
   * 统计筛选器
   * 
   * @param pq
   * @return
   * @throws SolrServerException
   * @throws IOException
   */
  public FilterData queryFilterDataNoCache(ProductQuery pq) throws SolrServerException, IOException {
    FilterData filterData = new FilterData();

    QueryResponse response = queryFilterDataResponse(pq);

    long total = response.getResults().getNumFound();

    filterData.setTotal(total);

    if (total < 1) {
      return filterData;
    }

    try {
      // 动态价格段计算
      PriceCaculator dynPriceCaculator = new PriceCaculator();
      // 尺寸统计计算
      SizeCaculator sizeCaculator = new SizeCaculator();
      // 分类统计
      // CategoryCaculator categoryCaculator = null;
      // Map<String, Long> basicCateCount = new HashMap<String, Long>();

      // 最底层分类统计：facet需要排除的那一项
      // Map<String, Long> bottomBasicCount = new HashMap<String, Long>();
      // 颜色统计
      ColorCaculator colorCaculator = new ColorCaculator();
      // 属性
      AttributeCaculatorManager attributeCaculatorManager = new AttributeCaculatorManager();
      // 品牌
      BrandCaculator brandCaculator = new BrandCaculator();
      // 店铺
      // BrandCaculator storeCaculator = new BrandCaculator();

      // 折扣段计算
      // PriceCaculator discountCaculator = new PriceCaculator(5);
      // Map<Integer, Long> discountMap = new HashMap<Integer, Long>();
      // FilterBean discountAbover = new FilterBean("9折以上", "9-111", 0);

      // int lowerBoundary = 0;
      // int upperBoundary = 0;

      // 筛选器数据
      List<FacetField> list = response.getFacetFields();
      // 基础分类
      Category currentCategory = new Category();
      // 面包屑
      CategoryCrumbs parentCategory = new CategoryCrumbs();
      // 品牌下基础分类http://www.banggo.tn/brand.shtml，只返回顶层分类
      OneCategory oneCategory = new OneCategory();
      for (FacetField ff : list) {
        String code = ff.getName();
        List<Count> counts = ff.getValues();
        if (code == null || counts == null) {
          continue;
        }

        // 品牌
        if (code.equalsIgnoreCase(FieldUtil.BRAND_CODE)) {
          for (Count counter : counts) {
            String brandCode = counter.getName();
            long count = counter.getCount();
            String brandName = AuxiliaryDataRefresher.getBrandNameByCode(brandCode);
            if (count > 0 && brandName != null && !brandName.equals("")) {
              brandCaculator.addBrandInfo(brandCode, brandName, count);
            }
          }
          // } else if(code.equalsIgnoreCase(FieldUtil.STORE_ID)) {//店铺
          // for(Count counter : counts) {
          // String storeId = counter.getName();
          // long count = counter.getCount();
          // if(count > 0) {
          // storeCaculator.addBrandInfo(cacheService.createNewStoreBean(storeId, count));
          // }
          // }
        } else if (code.equalsIgnoreCase(FieldUtil.COLOR_SERIES)) { // 色系
          for (Count counter : counts) {
            String name = counter.getName();
            long count = counter.getCount();
            if (count > 0) {
              colorCaculator.addSeriesInfo(name, count);
            }
          }

        } else if (code.equalsIgnoreCase(FieldUtil.DYN_PRICE_INTERVAL)) {// 动态价格段
          for (Count counter : counts) {
            String name = counter.getName();
            long count = counter.getCount();
            if (count > 0) {
              dynPriceCaculator.addPriceRange(new PriceRange(name, count));

              // try {
              // String[] priceBoundaries = name.split("-");
              // int currentLowerBoundary = Integer.valueOf(priceBoundaries[0].trim());
              // if(currentLowerBoundary < lowerBoundary && currentLowerBoundary > 0)
              // lowerBoundary = currentLowerBoundary;
              // int currentLowerUpperBoundary = Integer.valueOf(priceBoundaries[1].trim());
              // if (currentLowerUpperBoundary > upperBoundary)
              // upperBoundary = currentLowerUpperBoundary;
              // } catch (Exception e) {
              // log.error(e.getMessage());
              // }
            }
          }
        } else if (code.equalsIgnoreCase(FieldUtil.SIZE_SERIES)) {// 尺寸，根据stock参数，决定库存统计
          for (Count counter : counts) {
            String sizeSeriesCode = counter.getName();
            long count = counter.getCount();
            String sizeSeriesName = AuxiliaryDataRefresher.getSizeSeriesNameBySeriesCode(sizeSeriesCode);
            if (count > 0 && sizeSeriesName != null && !sizeSeriesName.equals("")) {
              sizeCaculator.addSizeBeanInfo(sizeSeriesCode, sizeSeriesName, count);
            }
          }
        } else if (code.equalsIgnoreCase(FieldUtil.CATEGORY_ID_M)) {// 分类id
          // for(Count counter : counts) {
          // String cateId = counter.getName();
          // long count = counter.getCount();
          // if(count > 0) {
          // basicCateCount.put(cateId, count);
          // }
          // }
          // currentCategory = cacheService.getDirectSubVcWithBcMapped2ByVId(pq.getCid(), counts);
          int currentExCatId = ExCategoryConst.ROOT_EXCATEGORY_ID;
          String[] cidSplit = null;
          int parentCurrentId = 0;
          try {
            // currentExCatId = Integer.valueOf(pq.getCid());
            String splitCid = pq.getCid();
            if (splitCid != null && !splitCid.equals("")) {
              String[] cIdLine = splitCid.split("[|]");
              cidSplit = cIdLine;
            } else {
              parentCurrentId = Integer.valueOf(pq.getCid());
            }
          } catch (NumberFormatException nfe) {

          }
          if (cidSplit != null && cidSplit.length > 0) {
            parentCurrentId = Integer.valueOf(cidSplit[0]);
          }
          if (cidSplit != null && cidSplit.length > 0) {
            for (String string : cidSplit) {
              currentExCatId = Integer.valueOf(string);
              currentCategory.setCateId(Integer.valueOf(string));
              AuxiliaryDataRefresher.getExCatTreeUnderOneExCat(currentCategory, counts, true);
              AuxiliaryDataRefresher.cacleSubsNull(currentCategory, counts, true);
            }
          } else {
            currentCategory.setCateId(Integer.valueOf(parentCurrentId));
            AuxiliaryDataRefresher.getExCatTreeUnderOneExCat(currentCategory, counts, true);
            AuxiliaryDataRefresher.cacleSubsNull(currentCategory, counts, true);
          }
          AuxiliaryDataRefresher.getCrumbs(parentCurrentId, parentCategory);
          AuxiliaryDataRefresher.getOneCategory(oneCategory, counts);
        }
        /*
         * else if (code.equalsIgnoreCase(FieldUtil.DISCOUNT)) {// 折扣 for (Count counter : counts) {
         * String discount = counter.getName(); long count = counter.getCount(); if (count > 0) {
         * double value = Double.parseDouble(discount);
         * 
         * if (value > pq.getMaxDiscount() || value < pq.getMinDiscount() - 0.9) { continue; }
         * 
         * if (value >= 8.5) { discountAbover.addCount(count); } else { int dis = (int)
         * Math.ceil(value); Long tempC = discountMap.get(dis); if (tempC == null) {
         * discountMap.put(Integer.valueOf(dis), count); } else { discountMap.put(dis, tempC +
         * count); } } } } }
         */

        else if (code.indexOf(FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_M) == 0) {// 属性
          String key = code.substring(17);
          for (Count counter : counts) {
            String valueSeriesCode = counter.getName();
            long count = counter.getCount();
            String valueSeriesName = AuxiliaryDataRefresher.getAttributeSeriesValueBySeriesCode(valueSeriesCode);
            /*
             * if (count > 0 && StringUtils.isNotBlank(valueName) && (key.equals("3") ||
             * (key.equals("6") || (key.equals("97"))))) { attributeCaculatorManager.addBean(key,
             * valueCode, valueName, count); }
             */
            if (count > 0 && StringUtils.isNotBlank(valueSeriesName)) {
              attributeCaculatorManager.addBean(key, valueSeriesCode, valueSeriesName, count);
            }
          }
        }
      }

      // 计算当前分类：用扩展分离
      // categoryCaculator = new CategoryCaculator(cacheService.getSiteCategorys(basicCateCount));
      // Category currentCategory = categoryCaculator.getCurrentCategory();
      /*
       * 当当前分类为最底层分类的时候，筛选器返回该分类的同级所有分类
       */
      // Category newCurrent = cacheService.createNewCurrentForBottom(currentCategory,
      // bottomBasicCount);
      filterData.setCurrentCategory(currentCategory);
      filterData.setParentCategory(parentCategory);
      filterData.setOneCategory(oneCategory);
      Object lowerBoundary = null;
      Object upperBoundary = null;
      FieldStatsInfo fldStatsInfo = response.getFieldStatsInfo().get(FieldUtil.SALES_PRICE);
      if (fldStatsInfo != null) {
        lowerBoundary = fldStatsInfo.getMin();
        upperBoundary = fldStatsInfo.getMax();
      }
      // 筛选列表按先后顺序分别为：品牌、价格、性别、尺码、季节、场合、系列、版型、面料/材质、颜色、功能
      // 品牌，价格显示顺便默认为第一和第二，其他跟seller_goods关联的属性按照后台设置的顺序显示
      filterData.addSubFilter(FilterBeanContainer.createFilter("brand", "品牌", brandCaculator, 1));
      // 计算并设置动态价格段信息
      filterData.addSubFilter(FilterBeanContainer.createFilter("price", "价格", dynPriceCaculator, 2,
          String.valueOf(lowerBoundary), String.valueOf(upperBoundary)));

      filterData.addSubFilter(FilterBeanContainer.createFilter("size", "尺寸", sizeCaculator, 4));
      filterData.addSubFilter(FilterBeanContainer.createFilter("color", "颜色", colorCaculator, 10));
      // filterData.addSubFilter(FilterBeanContainer.createFilter("store", "店铺", storeCaculator,
      // 22));
      // 计算并设置折扣信息
      /*
       * for (Map.Entry<Integer, Long> en : discountMap.entrySet()) { int dis = en.getKey();
       * discountCaculator.addPriceRange(new PriceRange(dis + "-" + dis, en.getValue(), "折")); }
       * FilterBeanContainer disBeanContainer = FilterBeanContainer.createFilter("discount", "折扣",
       * discountCaculator, 21); if (disBeanContainer != null) { if (discountAbover.getCount() > 0)
       * { disBeanContainer.getValues().add(discountAbover); }
       * 
       * for (FilterBean fb : disBeanContainer.getValues()) {
       * fb.setCode(resetDiscountRange(fb.getCode())); }
       * 
       * filterData.addSubFilter(disBeanContainer); }
       */

      // 属性筛选器
      while (attributeCaculatorManager.hasNext()) {
        filterData.addSubFilter(attributeCaculatorManager.next());
      }
      // 获取商品基本属性显示顺序，重新排列，返回

      // filterData.sortFilters(cacheService.getCategorySorter(newCurrent.getCateId()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return filterData;
  }

  public void setColorGoodsSizeNull(Product p) {
    if (p.isValidate()) {
      List<ColorProduct> cps = p.getColorProducts();
      for (ColorProduct cp : cps) {
        cp.setSizeList(null);
      }
      p.setThemes(null);
    }
  }

  public void setColorGoodsSizeNullForOrder(Product p) {
    if (p.isValidate()) {
      List<ColorProduct> cps = p.getColorProducts();
      for (ColorProduct cp : cps) {
        cp.setSizeList(null);
      }
    }
  }

  // 4.5折商品算作5折，因此在5-5折的区间，应该包含4.5折，所以，5-5折应该改写成4.1-5折
  private String resetDiscountRange(String code) {

    try {
      int index = code.indexOf('-');
      if (index < 0) {
        return code;
      }
      int left = Integer.parseInt(code.substring(0, index));
      int right = Integer.parseInt(code.substring(index + 1));

      double min = Math.min(left, right);
      double max = Math.max(left, right);
      min = min - 1 + 0.1;

      return min + "-" + max;

    } catch (Exception e) {
      return code;
    }
  }

  public abstract QueryResponse queryFilterDataResponse(ProductQuery qp) throws SolrServerException, IOException;

  @Autowired
  private CacheService cacheService;

  public CacheService getCacheService() {
    return cacheService;
  }

  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public static void main(String[] args) {
    String code = "5-5";
    int index = code.indexOf('-');
    int left = Integer.parseInt(code.substring(0, index));
    int right = Integer.parseInt(code.substring(index + 1));
    System.out.println(left);
    System.out.println(right);

    double min = Math.min(left, right);
    double max = Math.max(left, right);
    min = min - 1 + 0.1;

    System.out.println(min);
    System.out.println(max);
  }
}
