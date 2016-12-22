/*
 * 2014-9-28 下午3:56:45 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ColorProductProcessor implements IProductProcessor {

  private List<Product> products = null;

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (products == null || products.size() < 1) {
      return;
    }
    if (pm == null || pm.size() < 1) {
      return;
    }
    for (Product p : products) {
      if (p != null) {
        // String productId = p.getProductId();
        String productUuid = p.getProductUuid();
        List<ColorProduct> colorProducts = p.getColorProducts();

        // Product baseProduct = pm.get(productId);
        Product baseProduct = pm.get(productUuid);
        if (baseProduct != null && colorProducts != null && colorProducts.size() > 0) {
          List<ColorProduct> baseColorProducts = baseProduct.getColorProducts();

          if (baseColorProducts == null || baseColorProducts.size() < 1) {
            // 如果这个款式商品信息没有颜色商品信息，则直接加入
            baseProduct.setColorProducts(colorProducts);
            // I think there is no need to divide product information by series color and then index
            // for now
            // for(ColorProduct cp : colorProducts) {
            // cpm.put(cp.getColorCodeId(), cp);
            // }
          } else {
            // 依次更新颜色商品信息
            // The reason is same as above
            for (ColorProduct cp : colorProducts) {
              // String colorCodeId = cp.getColorCodeId();
              //
              // ColorProduct baseColorProduct = cpm.get(colorCodeId);
              // if(baseColorProduct != null) {
              // baseColorProduct.copyFrom(cp);
              // } else {
              baseColorProducts.add(cp);
              // cpm.put(colorCodeId, cp);
              // }
            }
          }
          // 设置product，colorProduct，sizeInfo的status状态值
          baseProduct.setProductStatus();
        }
      }
    }
  }

  public ColorProductProcessor(List<Product> products) {
    super();
    this.products = products;
  }

}
