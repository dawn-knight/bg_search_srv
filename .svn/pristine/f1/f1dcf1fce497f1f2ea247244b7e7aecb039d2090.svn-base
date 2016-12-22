package com.mbgo.search.core.dataetl.promotion;

import java.util.Map;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.dataetl.IProductProcessor;
import com.metersbonwe.promotion.bean.ReturnSinglePromoPriceMapByAllAppIdsBean;

public class ProductSinglePromotionPriceProcessor implements IProductProcessor {

  private ReturnSinglePromoPriceMapByAllAppIdsBean bean;

  public ProductSinglePromotionPriceProcessor(ReturnSinglePromoPriceMapByAllAppIdsBean bean) {
    this.bean = bean;
  }

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (bean == null) {
      return;
    }
    String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
    Map<String, Double> pcPromotionPriceMap = bean.getPcPromoPromotionPriceMap();
    if (pcPromotionPriceMap != null && pcPromotionPriceMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, Double> en : pcPromotionPriceMap.entrySet()) {
        String pid = en.getKey();
        Double pcPromotionPrice = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && pcPromotionPrice != null && pcPromotionPrice != 0) {
          old.setPcPromotionPrice(pcPromotionPrice.doubleValue());
        }
      }
    }
    Map<String, Double> appPromotionPriceMap = bean.getAppPromoPromotionPriceMap();
    if (appPromotionPriceMap != null && appPromotionPriceMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, Double> en : appPromotionPriceMap.entrySet()) {
        String pid = en.getKey();
        Double appPromotionPrice = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && appPromotionPrice != null && appPromotionPrice != 0) {
          old.setAppPromotionPrice(appPromotionPrice.doubleValue());
        }
      }
    }
    Map<String, Double> wapPromotionPriceMap = bean.getWapPromoPromotionPriceMap();
    if (wapPromotionPriceMap != null && wapPromotionPriceMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, Double> en : wapPromotionPriceMap.entrySet()) {
        String pid = en.getKey();
        Double wapPromotionPrice = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && wapPromotionPrice != null && wapPromotionPrice != 0) {
          old.setWapPromotionPrice(wapPromotionPrice.doubleValue());
        }
      }
    }
    Map<String, Double> webchatPromotionPriceMap = bean.getWebchatPromoPromotionPriceMap();
    if (webchatPromotionPriceMap != null && webchatPromotionPriceMap.size() > 0 && pm != null
        && pm.size() > 0) {
      for (Map.Entry<String, Double> en : webchatPromotionPriceMap.entrySet()) {
        String pid = en.getKey();
        Double webchatPromotionPrice = en.getValue();

        Product old = pm.get(channelCode + pid);
        if (old != null && webchatPromotionPrice != null && webchatPromotionPrice != 0) {
          old.setWebchatPromotionPrice(webchatPromotionPrice.doubleValue());
        }
      }
    }
  }
}
