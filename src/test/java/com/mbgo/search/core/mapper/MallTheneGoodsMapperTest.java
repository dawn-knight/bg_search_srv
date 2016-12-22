/*
 * 2014-9-28 上午11:03:22 吴健 HQ01U8435
 */

package com.mbgo.search.core.mapper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbstore.bean.ReturnProductIdAndSecondPriceBean;
import com.mbgo.mybatis.mbstore.mapper.MallSecondGoodsMapper;
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.core.bean.index.ProductTheme;
import com.mbgo.search.core.service.BaseTest;

public class MallTheneGoodsMapperTest extends BaseTest {

  @Autowired
  private MallThemeGoodsMapper mapper;
  
  @Autowired
  private MallSecondGoodsMapper mallSecondGoodsMapper;

  @Test
  public void testmapper() {
    // '207558','110261','570910','570911','881809'
    List<String> pids = new ArrayList<String>();
    pids.add("207558");
    pids.add("110261");
    pids.add("570910");
    pids.add("570911");
    pids.add("881809");
    MybatisBean option = new MybatisBean();
    option.setParams(pids);
    List<ReturnProductIdAndSecondPriceBean> result = mallSecondGoodsMapper
        .getReturnProductIdAndSecondPriceBeanByProductIds(pids);

    System.out.println(result);
    for (ReturnProductIdAndSecondPriceBean t : result) {
      System.out.println(t.getProductId() + "" + t.getSecondPrice());
    }
  }

  @Test
  public void testList() {
    List<String> pids = new ArrayList<String>();
    // pids.add("123");
    // pids.add("1");

    MybatisBean option = new MybatisBean();
    option.setParams(pids);
    List<ProductTheme> list = mapper.getProductThemes(option);
    System.out.println(list);
    for (ProductTheme t : list) {
      System.out.println(t.getProductId() + "" + t.getThemes());
    }
  }

}
