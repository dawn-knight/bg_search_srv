/*
 * 2014-12-2 下午4:06:51 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ProductAttributeValueProcessor implements IProductProcessor {

  private List<Product> ps = new ArrayList<Product>();

  public ProductAttributeValueProcessor(List<Product> ps) {
    super();
    this.ps = ps;
  }

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (pm != null && ps != null && ps.size() > 0) {
      for (Product p : ps) {
        if (p != null) {
          // String pid = p.getProductId();
          String pUuid = p.getProductUuid();
          // Product old = pm.get(pid);
          Product old = pm.get(pUuid);
          if (old != null) {
            old.setValues(p.getValues());
          }
        }
      }
    }
  }
}
