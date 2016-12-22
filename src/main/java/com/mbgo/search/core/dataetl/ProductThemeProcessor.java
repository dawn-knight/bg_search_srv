/*
 * 2014-9-28 下午3:42:58 吴健 HQ01U8435
 */

package com.mbgo.search.core.dataetl;

import java.util.List;
import java.util.Map;

import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.ColorProduct;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.ProductTheme;

public class ProductThemeProcessor implements IProductProcessor {

  private List<ProductTheme> themes = null;

  @Override
  public void execute(Map<String, Product> pm, Map<String, ColorProduct> cpm) {
    if (pm == null || pm.size() < 1) {
      return;
    }
    if (themes != null && themes.size() > 0) {
      for (ProductTheme pt : themes) {
        if (pt != null) {
          String productId = pt.getProductId();
          // 只有邦购商品有主题
          String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
          Product product = pm.get(channelCode + productId);
          if (product != null) {
            product.setThemes(pt.getThemes());
          }
        }
      }
    }
  }

  public ProductThemeProcessor(List<ProductTheme> themes) {
    super();
    this.themes = themes;
  }

}
