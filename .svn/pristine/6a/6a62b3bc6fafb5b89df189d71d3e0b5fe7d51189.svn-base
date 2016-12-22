/*
 * 2014-10-29 下午4:34:55 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl.stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;
import com.mbgo.search.constant.SkuLevelProductStatusEnum;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.SizeInfo;
import com.mbgo.search.core.dataetl.IProductProcessor;

public class StockProductProcessor implements IProductProcessor {

  // private Map<String, Integer> skuMap = null;

  private Map<String, Map<String, Integer>> stockMap = null;

  private Logger log = LoggerFactory.getLogger(StockProductProcessor.class);

  // @Override
  // public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
  // if(skuMap != null && cpm != null) {
  // for(Map.Entry<String, ColorProduct> en : cpm.entrySet()) {
  // List<SizeInfo> sizes = en.getValue().getSizeList();
  // String colorCodeId = en.getKey();
  // int colorStock = 0;
  // if(sizes != null) {
  // for(SizeInfo si : sizes) {
  // String sku = colorCodeId + si.getSizeCode();
  // Integer stock = skuMap.get(sku);
  // if(stock != null) {
  // colorStock += stock;
  // si.setStock(stock);
  // }
  // }
  // en.getValue().setStock(colorStock);
  // }
  // }
  // for(Product p : pm.values()) {
  // p.setStockInfo();
  // }
  // }
  // }
  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    try {
      if (pm != null && stockMap != null) {
        for (Product product : pm.values()) {
          // Map<String, Integer> skuLevelStockMap = stockMap.get(product.getProductId());
          Map<String, Integer> skuLevelStockMap = stockMap.get(product.getProductUuid());
          if (skuLevelStockMap != null && product.getColorProducts() != null && product.getColorProducts().size() > 0) {
            for (ColorProduct productWithColorAndSizeInfo : product.getColorProducts()) {
              List<SizeInfo> skuLevelInfoList = productWithColorAndSizeInfo.getSizeList();
              if (skuLevelInfoList != null && skuLevelInfoList.size() > 0) {
                for (SizeInfo skuLevelInfo : skuLevelInfoList) {
                  if (skuLevelInfo.getSizeStatus() == SkuLevelProductStatusEnum.RACKED.ordinal()) {
                    Integer skuLevelStock = skuLevelStockMap.get(skuLevelInfo.getSku());
                    if (skuLevelStock != null && skuLevelStock.intValue() > 0) {
                      product.setStock(product.getStock() + skuLevelStock);
                      productWithColorAndSizeInfo.setStock(skuLevelStock);
                    } else {
                      log.debug("product [{}] sku [{}] out of stock", product.getProductId(), skuLevelInfo.getSku());
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // public StockProductProcessor(List<StockDetailInfo> list) {
  // if(list != null) {
  // skuMap = new HashMap<String, Integer>();
  // for(StockDetailInfo info : list) {
  // skuMap.put(info.getSku(), info.getStock());
  // }
  // }
  // }

  public StockProductProcessor(List<StockDetailInfo> list) {
    if (list != null && list.size() > 0) {
      stockMap = new HashMap<String, Map<String, Integer>>();
      for (StockDetailInfo sInfo : list) {
        if (sInfo != null) {
          // Map<String, Integer> skuLevelStockMap = stockMap.get(sInfo.getProductId());
          Map<String, Integer> skuLevelStockMap = stockMap.get(sInfo.getProductUuid());
          if (skuLevelStockMap == null) {
            skuLevelStockMap = new HashMap<String, Integer>();
          }
          skuLevelStockMap.put(sInfo.getSku(), sInfo.getStock());
          stockMap.put(sInfo.getProductUuid(), skuLevelStockMap);
        }
      }
    }
  }
}
