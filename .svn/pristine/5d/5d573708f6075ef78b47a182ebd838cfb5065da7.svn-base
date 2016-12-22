/*
 * 2014-12-8 下午4:21:06 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.PromotionHandlerService;
import com.metersbonwe.promotion.api.PlatformPromotionApi;
import com.metersbonwe.promotion.bean.APIPromotionBackMsgBean;
import com.metersbonwe.promotion.bean.PromoProductBean;

@Service("promotionHandlerService")
public class PromotionHandlerServiceImpl implements PromotionHandlerService {

  @Autowired
  private PlatformPromotionApi promotionService;

  private static Logger log = LoggerFactory
      .getLogger(PromotionHandlerServiceImpl.class);

  @Override
  public PromoProductBean queryPromotionIds(List<String> productIds,
      String channelCode) {
    Map<String, List<String>> pcProductJoinPromoMap = new HashMap<String, List<String>>();
    Map<String, List<String>> appProductJoinPromoMap = new HashMap<String, List<String>>();
    Map<String, List<String>> wapProductJoinPromoMap = new HashMap<String, List<String>>();
    Map<String, List<String>> webchatProductJoinPromoMap = new HashMap<String, List<String>>();

    PageManager pageManager = new PageManager(productIds.size(), 500);
    while (pageManager.hasNextPage()) {
      List<String> temps = productIds.subList(
          (int) pageManager.getFirst(), pageManager.getToIndex());
      try {
        PromoProductBean para = new PromoProductBean();
        para.setProductList(temps);

        APIPromotionBackMsgBean<PromoProductBean> apiBean = promotionService
            .getProductJoinPromoAll(para);
        PromoProductBean tempResult = apiBean
            .getResult();

        if (tempResult != null) {
          if (tempResult.getPcProductJoinPromo() != null && tempResult.getPcProductJoinPromo().size() > 0) {
            pcProductJoinPromoMap.putAll(tempResult.getPcProductJoinPromo());
          }
          if (tempResult.getAppProductJoinPromo() != null && tempResult.getAppProductJoinPromo().size() > 0) {
            appProductJoinPromoMap.putAll(tempResult.getAppProductJoinPromo());
          }
          if (tempResult.getWapProductJoinPromo() != null && tempResult.getWapProductJoinPromo().size() > 0) {
            wapProductJoinPromoMap.putAll(tempResult.getWapProductJoinPromo());
          }
          if (tempResult.getWebchatProductJoinPromo() != null && tempResult.getWebchatProductJoinPromo().size() > 0) {
            webchatProductJoinPromoMap.putAll(tempResult.getWebchatProductJoinPromo());
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }
    PromoProductBean result = new PromoProductBean();
    result.setPcProductJoinPromo(pcProductJoinPromoMap);
    result.setAppProductJoinPromo(appProductJoinPromoMap);
    result.setWapProductJoinPromo(wapProductJoinPromoMap);
    result.setWebchatProductJoinPromo(webchatProductJoinPromoMap);
    return result;
  }
}
