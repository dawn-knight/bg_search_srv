/*
 * 2014-9-28 下午3:09:13 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ProductManager {

  // 初始商品列表
  private List<Product> baseProducts = new ArrayList<Product>(0);
  // 商品productId列表
  private List<String> productIds = new ArrayList<String>();
  // 商品productId与信息的对应关系
  private Map<String, Product> productMapper = new HashMap<String, Product>();
  // 商品颜色码colorCodeId与颜色商品信息的对应关系
  private Map<String, ColorProduct> colorProductMapper = new HashMap<String, ColorProduct>();

  private Map<String, String> idToCode = new HashMap<String, String>();
  private Map<String, String> codeToId = new HashMap<String, String>();
  private List<String> productCodes = new ArrayList<String>();

  private boolean hasData = true;

  public ProductManager(List<Product> baseProducts) {
    super();
    this.baseProducts = baseProducts;
    init();
  }

  private void init() {
    if (baseProducts != null && baseProducts.size() > 0) {
      for (Product p : baseProducts) {
        String productUuid = p.getProductUuid();
        String productId = p.getProductId();
        String productCode = p.getProductCode();
        if (StringUtils.isBlank(productCode)) {
          continue;
        }
        productIds.add(productId);
        productCodes.add(productCode);

        // productMapper.put(productId, p);
        productMapper.put(productUuid, p);

        idToCode.put(productId, productCode);
        codeToId.put(productCode, productId);

        List<ColorProduct> colors = p.getColorProducts();
        if (colors != null && colors.size() > 0) {
          for (ColorProduct cp : colors) {
            String colorCodeId = cp.getColorCodeId();
            colorProductMapper.put(colorCodeId, cp);
          }
        }
      }
    } else {
      hasData = false;
    }
  }

  public ProductManager merge(IProductProcessor p) {
    if (hasData) {
      p.execute(productMapper, colorProductMapper);
    }
    return this;
  }

  public List<String> getProductIds() {
    return productIds;
  }

  public void setProductIds(List<String> productIds) {
    this.productIds = productIds;
  }

  public List<Product> getBaseProducts() {
    return baseProducts;
  }

  public void setBaseProducts(List<Product> baseProducts) {
    this.baseProducts = baseProducts;
  }

  public boolean isHasData() {
    return hasData;
  }

  public List<String> getProductCodes() {
    return productCodes;
  }

  public Map<String, String> getCodeToId() {
    return codeToId;
  }

}
