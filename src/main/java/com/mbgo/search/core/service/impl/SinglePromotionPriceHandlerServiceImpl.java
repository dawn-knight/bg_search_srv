package com.mbgo.search.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.SinglePromotionPriceHandlerService;
import com.metersbonwe.promotion.api.PlatformPromotionApi;
import com.metersbonwe.promotion.bean.APIPromotionBackMsgBean;
import com.metersbonwe.promotion.bean.ParaSinglePromotionPriceByAllAppIdsBean;
import com.metersbonwe.promotion.bean.ReturnSinglePromoPriceMapByAllAppIdsBean;

@Service("singlePromotionPriceHandlerService")
public class SinglePromotionPriceHandlerServiceImpl implements SinglePromotionPriceHandlerService {

  @Autowired
  private PlatformPromotionApi promotionService;

  private static Logger log = LoggerFactory.getLogger(SinglePromotionPriceHandlerServiceImpl.class);

  @Override
  public ReturnSinglePromoPriceMapByAllAppIdsBean querySinglePromotionPrice(List<Product> products,
      String channelCode) {
    Map<String, Double> pcPromoPromotionPriceMap = new HashMap<String, Double>();
    Map<String, Double> appPromoPromotionPriceMap = new HashMap<String, Double>();
    Map<String, Double> wapPromoPromotionPriceMap = new HashMap<String, Double>();
    Map<String, Double> webchatPromoPromotionPriceMap = new HashMap<String, Double>();

    PageManager pageManager = new PageManager(products.size(), 500);
    while (pageManager.hasNextPage()) {
      List<Product> tempProducts = products
          .subList((int) pageManager.getFirst(), pageManager.getToIndex());
      try {
        ParaSinglePromotionPriceByAllAppIdsBean paraBean = new ParaSinglePromotionPriceByAllAppIdsBean();
        paraBean.setChannelCode(channelCode);
        Map<String, Double> minShoppingPriceMap = new HashMap<String, Double>();
        if (products != null && products.size() > 0) {
          for (Product p : tempProducts) {
            if (p != null) {
              minShoppingPriceMap.put(p.getProductId(), p.getSalesPrice());
            }
          }
        }
        paraBean.setMinShoppingPriceMap(minShoppingPriceMap);

        APIPromotionBackMsgBean<ReturnSinglePromoPriceMapByAllAppIdsBean> apiBean = promotionService
            .getGoodsPromotionPriceByAllAppIds(paraBean);

        ReturnSinglePromoPriceMapByAllAppIdsBean tempResult = apiBean.getResult();
        if(tempResult !=null){
          if(tempResult.getPcPromoPromotionPriceMap() != null && tempResult.getPcPromoPromotionPriceMap().size() > 0){
            pcPromoPromotionPriceMap.putAll(tempResult.getPcPromoPromotionPriceMap());
          }
          if(tempResult.getAppPromoPromotionPriceMap() != null && tempResult.getAppPromoPromotionPriceMap().size() > 0){
            appPromoPromotionPriceMap.putAll(tempResult.getAppPromoPromotionPriceMap());
          }
          if(tempResult.getWapPromoPromotionPriceMap() != null && tempResult.getWapPromoPromotionPriceMap().size() > 0){
            wapPromoPromotionPriceMap.putAll(tempResult.getWapPromoPromotionPriceMap());
          }
          if(tempResult.getWebchatPromoPromotionPriceMap() != null && tempResult.getWebchatPromoPromotionPriceMap().size() > 0){
            webchatPromoPromotionPriceMap.putAll(tempResult.getWebchatPromoPromotionPriceMap());
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    ReturnSinglePromoPriceMapByAllAppIdsBean result = new ReturnSinglePromoPriceMapByAllAppIdsBean();
    result.setPcPromoPromotionPriceMap(pcPromoPromotionPriceMap);
    result.setAppPromoPromotionPriceMap(appPromoPromotionPriceMap);
    result.setWapPromoPromotionPriceMap(wapPromoPromotionPriceMap);
    result.setWebchatPromoPromotionPriceMap(webchatPromoPromotionPriceMap);
    return result;
  }
}
