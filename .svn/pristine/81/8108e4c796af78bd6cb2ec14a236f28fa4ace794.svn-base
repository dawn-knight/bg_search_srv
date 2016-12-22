/*
 * 2014-10-13 下午1:56:22 吴健 HQ01U8435
 */

package com.mbgo.search.core.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.use4.CacheService;

/*
 * // // _oo0oo_ // o8888888o // 88" . "88 // (| -_- |) // 0\ = /0 // ___/`---'\___ // .' \\| |// '.
 * // / \\||| : |||// \ // / _||||| -:- |||||- \ // | | \\\ - /// | | // | \_| ''\---/'' |_/ | // \
 * .-\__ '-' ___/-. / // ___'. .' /--.--\ `. .'___ // ."" '< `.___\_<|>_/___.' >' "". // | | : `-
 * \`.;`\ _ /`;.`/ - ` : | | // \ \ `_. \_ __\ /__ _/ .-` / / // =====`-.____`.___
 * \_____/___.-`___.-'===== // `=---=' // // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ // 佛祖保佑
 * 永无BUG // 本模块已经过开光处理，绝无可能再产生BUG // // //
 */
public class ProductSearchServiceTest extends BaseTest {

  @Resource(name = "productSearchService")
  private ProductSearchService productSearchService;

  @Autowired
  private CacheService cacheService;

  @Before
  public void init() {
    cacheService.initAll();
  }

  @Test
  public void testSearchNewlyProduct() {

    ParameterNewlyProductBean p = new ParameterNewlyProductBean();

    p.setBrandCode("Mm,4m");
    p.setTimeInterval("60");
    p.setRecordSize("20");
    System.out.println(JSON.toJSONString(p));
    ReturnNewlyProductBean result = productSearchService.searchNewlyProduct(p);

    System.out.println(JSON.toJSONString(result));
  }

  @Test
  public void testSearchNewlyProductSpecialCode() {

    ParameterNewlyProductBean p = new ParameterNewlyProductBean();

    p.setBrandCode("Mm,4m,Mn,Aa");
    p.setTimeInterval("999");
    p.setRecordSize("20");
    System.out.println(JSON.toJSONString(p));
    ReturnNewlyProductBean result = productSearchService.searchNewlyProduct(p);

    System.out.println(JSON.toJSONString(result));
  }

  @Test
  public void testBasiceServiceFilterData() throws IOException {

    long b = System.currentTimeMillis();
    ProductQuery productQuery = new ProductQuery("", "", "");
    // productQuery.setCid("5");
    FilterData filterData = productSearchService.searchFilterData(productQuery);

    assert filterData != null;

    System.out.println(JSON.toJSONString(filterData));
    System.out.println(System.currentTimeMillis() - b);
  }

  @Test
  public void testProductSearchService() {
    ProductQuery productQuery = new ProductQuery("美邦 男", "", "");
    productQuery.setBrand("MC|mb,kids");
    productQuery.init();

    long b = System.currentTimeMillis();
    ProductQueryResult result = productSearchService.search(productQuery);
    System.out.println("cost : " + (System.currentTimeMillis() - b));

    System.out.println(result.getTotal());
    System.out.println(result.getList());
    System.out.println();

    b = System.currentTimeMillis();
    result = productSearchService.search(productQuery);
    System.out.println("cost : " + (System.currentTimeMillis() - b));

    System.out.println(result.getTotal());
    System.out.println(result.getList());

    System.out.println(JSON.toJSONString(result));
  }

  @Test
  public void testAnalyzeWord() {
    System.out.println(productSearchService.analyzeWord("【冬装新品】MooMoo女童针织空气层连帽披肩【冬装新品】  购物满199免邮"));
  }

  @Test
  public void testWord() {
    ProductQuery productQuery = new ProductQuery("885364", "", "");
    // productQuery.setBrand("MC|mb,kids");
    productQuery.init();

    long b = System.currentTimeMillis();
    ProductQueryResult result = productSearchService.search(productQuery);
    System.out.println("cost : " + (System.currentTimeMillis() - b));

    System.out.println(result.getTotal());
    System.out.println(result.getList());
    System.out.println();

    b = System.currentTimeMillis();
    result = productSearchService.search(productQuery);
    System.out.println("cost : " + (System.currentTimeMillis() - b));

    System.out.println(result.getTotal());
    System.out.println(result.getList());

    System.out.println(JSON.toJSONString(result));

  }

}
