/*
 * 2014-12-8 下午4:14:36 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl.promotion;

import java.util.List;
import java.util.Map;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.dataetl.IProductProcessor;
import com.metersbonwe.promotion.bean.PromoProductBean;

public class ProductPromotionProcessor implements IProductProcessor {

  private PromoProductBean bean;

  public ProductPromotionProcessor(PromoProductBean bean) {
    this.bean = bean;
  }

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (bean == null) {
      return;
    }
    String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
    Map<String, List<String>> pcProductJoinPromoMap = bean.getPcProductJoinPromo();
    if (pcProductJoinPromoMap != null && pcProductJoinPromoMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, List<String>> en : pcProductJoinPromoMap.entrySet()) {
        String pid = en.getKey();
        List<String> pcPromotionIds = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && pcPromotionIds != null && pcPromotionIds.size() > 0) {
          old.setPcPromotionIds(pcPromotionIds);
        }
      }
    }
    Map<String, List<String>> appProductJoinPromoMap = bean.getAppProductJoinPromo();
    if (appProductJoinPromoMap != null && appProductJoinPromoMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, List<String>> en : appProductJoinPromoMap.entrySet()) {
        String pid = en.getKey();
        List<String> appPromotionIds = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && appPromotionIds != null && appPromotionIds.size() > 0) {
          old.setAppPromotionIds(appPromotionIds);
        }
      }
    }
    Map<String, List<String>> wapProductJoinPromoMap = bean.getWapProductJoinPromo();
    if (wapProductJoinPromoMap != null && wapProductJoinPromoMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, List<String>> en : wapProductJoinPromoMap.entrySet()) {
        String pid = en.getKey();
        List<String> wapPromotionIds = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && wapPromotionIds != null && wapPromotionIds.size() > 0) {
          old.setWapPromotionIds(wapPromotionIds);
        }
      }
    }
    Map<String, List<String>> webchatProductJoinPromoMap = bean.getWebchatProductJoinPromo();
    if (webchatProductJoinPromoMap != null && webchatProductJoinPromoMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, List<String>> en : webchatProductJoinPromoMap.entrySet()) {
        String pid = en.getKey();
        List<String> webchatPromotionIds = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && webchatPromotionIds != null && webchatPromotionIds.size() > 0) {
          old.setWebchatPromotionIds(webchatPromotionIds);
        }
      }
    }
  }
}
