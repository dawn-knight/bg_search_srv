/*
 * 2014-10-15 下午2:07:24 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banggo.common.redis.RedisTemplate;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;
import com.mbgo.mybatis.mbsearch.mapper.MgrGoodsTagsMapper;
import com.mbgo.mybatis.mbstock.mapper.StockMapper;
import com.mbgo.mybatis.mbstore.mapper.MallBeautyGoodsMapper;
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.ProductBeauty;
import com.mbgo.search.core.bean.index.ProductTheme;
import com.mbgo.search.core.bean.index.SizeInfo;
import com.mbgo.search.core.dataetl.ColorProductProcessor;
import com.mbgo.search.core.dataetl.ProductAttributeValueProcessor;
import com.mbgo.search.core.dataetl.ProductBeautyProcessor;
import com.mbgo.search.core.dataetl.ProductDisplayTagProcessor;
import com.mbgo.search.core.dataetl.ProductManager;
import com.mbgo.search.core.dataetl.ProductTagsProcessor;
import com.mbgo.search.core.dataetl.ProductThemeProcessor;
import com.mbgo.search.core.dataetl.promotion.ProductPromotionProcessor;
import com.mbgo.search.core.dataetl.promotion.ProductSinglePromotionPriceProcessor;
import com.mbgo.search.core.dataetl.stock.ProductStockManager;
import com.mbgo.search.core.dataetl.stock.StockProductProcessor;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;
import com.mbgo.search.core.service.DataEtlService;
import com.mbgo.search.core.service.PromotionHandlerService;
import com.mbgo.search.core.service.SinglePromotionPriceHandlerService;
import com.metersbonwe.pcs.dao.ExtendedProductChannelGoodsMapper;
import com.metersbonwe.pcs.dao.SearchGoodsTagMapper;
import com.metersbonwe.pcs.dao.SkuLevelProductInfoMapper;

@Service("dataEtlService")
public class DataEtlServiceImpl implements DataEtlService {

  @Override
  public List<String> getProductIds(long first, long max) throws Exception {
    MybatisBean option = new MybatisBean();
    option.setFirst(first);
    option.setMax(max);

    // 排除有范商品
    option.setPid(ChannelConst.DEFAULT_YOUFAN_CHANNEL_CODE);
    List<String> productIds = extendedProductChannelGoodsMapper.getChannelProductIdList(option);
    return productIds;
  }

  @Override
  public List<Product> getProductList(long first, long max, List<String> pids) throws Exception {
    ProductManager manager = null;
    MybatisBean option = new MybatisBean();

    if (pids == null || pids.size() < 1) { // 如果指定了一批的productId，则设置参数，否则设置分页信息
      option.setFirst(first);
      option.setMax(max);
    } else {
      option.setParams(pids);
    }
    // 排除有范商品
    option.setPid(ChannelConst.DEFAULT_YOUFAN_CHANNEL_CODE);
    /*
     * String channelCode = AuxiliaryDataRefresher.getChannelCode(); if (channelCode != null &&
     * !channelCode.equals(ChannelConst.CODE_OF_ALL_CHANNEL) && !channelCode.equals("")) {
     * option.setPid(channelCode); }
     */

    List<Product> products = extendedProductChannelGoodsMapper.getChannelProductList(option);

    // 将查询的信息进行包装
    manager = new ProductManager(products);

    if (!manager.isHasData()) {
      return new ArrayList<Product>(0);
    }

    // 这里设置了后续查询的商品号
    option.setParams(manager.getProductIds());
    option.setMax(-1);
    // 获取邦购主题
    List<ProductTheme> themes = mallThemeGoodsMapper.getProductThemes(option);

    ProductThemeProcessor themeProcessor = new ProductThemeProcessor(themes);
    // 获取门店闪购商品集合
    List<ProductBeauty> beautys = mallBeautyGoodsMapper.getProductBeautys(option);

    ProductBeautyProcessor beautyProcessor = new ProductBeautyProcessor(beautys);
    // 色系，尺码信息

    List<Product> colorProducts = skuLevelProductInfoMapper.getProductList(option);
    // 设置尺码系信息
    if (colorProducts != null && colorProducts.size() > 0) {
      for (Product p : colorProducts) {
        if (p != null) {
          for (ColorProduct cp : p.getColorProducts()) {
            if (cp != null) {
              for (SizeInfo sInfo : cp.getSizeList()) {
                if (sInfo != null) {
                  String sizeCode = sInfo.getSizeCode();
                  if (StringUtils.isNotBlank(sizeCode)) {
                    SizeInfo value = AuxiliaryDataRefresher.getSizeInfoByCode(sizeCode);
                    if (value != null) {
                      sInfo.setSizeSeriesCode(value.getSizeSeriesCode());
                      sInfo.setSizeSeriesName(value.getSizeSeriesName());
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    // no relevant statistic table in new product category system now
    // 统计信息
    // List<Product> statisticProduct = statisticMapper.getProductStatistic(option);

    // 标签信息
    // List<Product> productTags = mgrGoodsTagsMapper.getProductTags(option);

    List<Product> productTags = searchGoodsTagMapper.getProductTags(option);

    // 显示标签，db里sort字段最小值，优先级最高

    List<Product> productDisplayTag = searchGoodsTagMapper.getDisplayTag(option);

    // minimal sales price has already been set
    // 最低价格
    // List<Product> minSalePrices =
    // skuLevelProductInfoMapper.getListOfProductWithMinSalePrice(option);

    // 属性

    List<Product> attrValues = extendedProductChannelGoodsMapper
        .getChannelGoodAttributeList(option);

    // sale point is no more needed
    // List<Product> nameSalePoints = productGoodsMapper.getProductNameAndSalePoint(new
    // MybatisBean(manager.getProductCodes()));
    // 促销信息
    // 只有邦购商品需要获取促销信息
    String banggoChannelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
    manager.merge(new ProductPromotionProcessor(promotionHandlerService.queryPromotionIds(
        manager.getProductIds(), banggoChannelCode)));

    // 合并主题信息；颜色尺寸信息；统计信息

    manager.merge(themeProcessor)
        .merge(beautyProcessor)
        .merge(new ColorProductProcessor(colorProducts))
        // no more merging
        // .merge(new ProductStatisticProcessor(statisticProduct))
        .merge(new ProductTagsProcessor(productTags))
        .merge(new ProductDisplayTagProcessor(productDisplayTag))
        // no more merging
        // .merge(new SalePriceProcessor(minSalePrices))
        .merge(new ProductAttributeValueProcessor(attrValues))
    // no more merging
    // .merge(new GoodsNameAndSalePointProcessor(manager.getCodeToId(), nameSalePoints));
    ;

    // 库存信息

    List<StockDetailInfo> stockDetailInfos = stockMapper.getSkuLevelStockOfChannelProduct(option);

    manager.merge(new StockProductProcessor(stockDetailInfos));

    // 设置各渠道促销价格

    manager.merge(new ProductSinglePromotionPriceProcessor(singlePromotionPriceHandlerService
        .querySinglePromotionPrice(manager.getBaseProducts(), banggoChannelCode)));

    // 返回4个渠道单品促销价格，如果价格为0，则用salesPrice填充后返回
    List<Product> baseProducts = manager.getBaseProducts();
    for (Product p : baseProducts) {
      // 只有邦购商品需要获取促销价格
      if (p != null && StringUtils.equals(p.getChannelCode(), banggoChannelCode)) {
        if (p.getPcPromotionPrice() == 0) {
          p.setPcPromotionPrice(p.getSalesPrice());
        }
        if (p.getAppPromotionPrice() == 0) {
          p.setAppPromotionPrice(p.getSalesPrice());
        }
        if (p.getWapPromotionPrice() == 0) {
          p.setWapPromotionPrice(p.getSalesPrice());
        }
        if (p.getWebchatPromotionPrice() == 0) {
          p.setWebchatPromotionPrice(p.getSalesPrice());
        }
      }
    }

    return baseProducts;
  }

  @Override
  public void saveProductToRedis(Product product) {
    try {
      // redisTemplate.setPojo(PREFIX + product.getProductId(), product);
      // redisTemplate.setPojo(PREFIX + product.getProductUuid(), product);
      redisTemplate.setPojo(product.getProductUuid(), product);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public void saveColorProductToRedis(String colorCodeId, Product p) {
    try {
      redisTemplate.setPojo(colorCodeId, p);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Override
  public List<Product> readProductFromRedis(List<String> productIds) {
    List<Product> result = new ArrayList<Product>();
    if (productIds == null || productIds.size() < 1) {
      return result;
    }
    try {
      result = redisTemplate.mGetPojo(productIds.toArray(new String[] {}));
    } catch (Exception e) {
      log.error("redis error : {}", e.getMessage());
    }
    if (result == null) {
      return new ArrayList<Product>();
    }
    return result;
  }

  @Override
  public List<Product> readColorProductFromRedis(List<String> colorProductIds) {
    List<Product> result = new ArrayList<Product>();
    if (colorProductIds == null || colorProductIds.size() < 1) {
      return result;
    }

    try {
      result = redisTemplate.mGetPojo(colorProductIds.toArray(new String[] {}));
    } catch (Exception e) {
      log.error("redis error : {}", e.getMessage());
    }
    if (result == null) {
      return new ArrayList<Product>();
    }
    return result;
  }

  @Override
  public List<Product> getGoodsTags(MybatisBean option) {
    return searchGoodsTagMapper.getProductTags(option);
    // return mgrGoodsTagsMapper.getProductTags(option);
  }

  // The only method call this is 'ProductIndexServiceImpl.sycnProductPromotions' which is commented
  // out!!! So I also comment out this method body.
  @Override
  public List<Product> sycnProductPromotions(List<String> pids) {
    List<Product> result = new ArrayList<Product>();
    // try {
    // MybatisBean option = new MybatisBean(pids);
    // List<Product> products = productInfoMapper.getProductStoreIdByProductId(option);
    //
    // Map<String, List<PromotionInfo>> temp = promotionHandlerService.queryPromotions(products);
    //
    // if(temp.size() < 1) {
    // return result;
    // }
    //
    // for(Product p : products) {
    // List<PromotionInfo> infos = temp.get(p.getProductId());
    // p.setPromotionInfos(infos);
    // }
    //
    // return products;
    // } catch (Exception e) {
    //
    // }
    return result;
  }

  @Override
  public ProductStockManager sycnProductStocks(List<String> pids) {

    try {
      MybatisBean option = new MybatisBean(pids);
      List<Product> products = extendedProductChannelGoodsMapper.getChannelProductList(option);

      List<StockDetailInfo> infos = stockMapper.getSkuLevelStockOfChannelProduct(option);

      if (infos == null || infos.size() < 1) {
        return null;
      }

      ProductStockManager manager = new ProductStockManager();

      for (StockDetailInfo sdi : infos) {
        // System.out.println(sdi.getSku()+":"+sdi.getStock());
        manager.add(sdi);
      }
      return manager;
    } catch (Exception e) {

    }
    return null;
  }

  @Autowired
  private SkuLevelProductInfoMapper skuLevelProductInfoMapper;
  @Autowired
  private ExtendedProductChannelGoodsMapper extendedProductChannelGoodsMapper;
  @Autowired
  private StockMapper stockMapper;

  // @Autowired
  // private ProductInfoMapper productInfoMapper;//获得款式级别详细信息
  @Autowired
  private MgrGoodsTagsMapper mgrGoodsTagsMapper;

  @Autowired
  private SearchGoodsTagMapper searchGoodsTagMapper;

  @Autowired
  private MallThemeGoodsMapper mallThemeGoodsMapper; // 获得款式级别的主题活动信息

  @Autowired
  private MallBeautyGoodsMapper mallBeautyGoodsMapper; // 获得款式级别的门店闪购活动信息
  // @Autowired
  // private ProductStoreBarcodeListMapper barcodeListMapper; //获得颜色级别信息以及颜色尺寸信息
  // @Autowired
  // private ProductStoreGoodsStatisticMapper statisticMapper; //获得款式级别的统计信息
  @Resource(name = "redisTemplate")
  private RedisTemplate redisTemplate;
  // @Resource(name = "stockHandlerService")
  // private StockHandlerService stockHandlerService;
  @Resource(name = "promotionHandlerService")
  private PromotionHandlerService promotionHandlerService;

  @Resource(name = "singlePromotionPriceHandlerService")
  private SinglePromotionPriceHandlerService singlePromotionPriceHandlerService;
  // @Autowired
  // private ProductGoodsMapper productGoodsMapper;
  private static Logger log = LoggerFactory.getLogger(DataEtlServiceImpl.class);

}
