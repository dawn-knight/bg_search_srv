package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.ProductBeauty;

public class ProductBeautyProcessor implements IProductProcessor {

  private List<ProductBeauty> beautys = null;

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (pm == null || pm.size() < 1) {
      return;
    }
    if (beautys != null && beautys.size() > 0) {
      for (ProductBeauty pt : beautys) {
        if (pt != null) {
          String productUuid = pt.getProductUuid();
          // 只有非邦购商品有门店闪购
          Product product = pm.get(productUuid);
          if (product != null) {
            product.setBeautys(pt.getBeautys());
          }
        }
      }
    }
  }

  public ProductBeautyProcessor(List<ProductBeauty> beautys) {
    super();
    this.beautys = beautys;
  }

}
