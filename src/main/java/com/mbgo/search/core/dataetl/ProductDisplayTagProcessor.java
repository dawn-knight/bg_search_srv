package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ProductDisplayTagProcessor implements IProductProcessor {

  private List<Product> productDisplayTag = null;

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (pm == null || pm.size() < 1) {
      return;
    }
    if (productDisplayTag != null && productDisplayTag.size() > 0) {
      for (Product pt : productDisplayTag) {
        if (pt != null) {
          String productId = pt.getProductId();
          // 只有邦购商品有标签
          String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
          Product product = pm.get(channelCode + productId);
          if (product != null) {
            if (product.getDisplayTagSort() == -1 || (pt.getDisplayTagSort() < product.getDisplayTagSort())) {
              product.setDisplayTag(pt.getDisplayTag());
              product.setDisplayTagSort(pt.getDisplayTagSort());
            }
          }
        }
      }
    }
  }

  public ProductDisplayTagProcessor(List<Product> pgs) {
    this.productDisplayTag = pgs;
  }
}
