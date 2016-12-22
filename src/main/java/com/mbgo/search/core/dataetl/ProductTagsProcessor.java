/*
 * 2014-10-15 上午11:19:57 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;

public class ProductTagsProcessor implements IProductProcessor {

  private List<Product> productTags = null;

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (pm == null || pm.size() < 1) {
      return;
    }
    if (productTags != null && productTags.size() > 0) {
      for (Product pt : productTags) {
        if (pt != null) {
          String productId = pt.getProductId();
          // 只有邦购商品有标签
          String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
          Product product = pm.get(channelCode + productId);
          if (product != null) {
            product.setTags(pt.getTags());
          }
        }
      }
    }
  }

  public ProductTagsProcessor(List<Product> pgs) {
    this.productTags = pgs;
  }
}
