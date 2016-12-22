/*
 * 2014-9-25 下午4:43:50 吴健 HQ01U8435
 */

package com.mbgo.search.controller;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.core.bean.query.ApiParameter;
import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.SearchService;
import com.mbgo.search.core.service.SearchWordService;
import com.mbgo.search.core.service.use4.CacheService;

@Controller("/search")
@RequestMapping("/search")
public class SearchController {

  /**
   * 
   * @param productCode
   * @param type
   * @return
   */
  @RequestMapping(value = "/upsertProductById.do")
  public @ResponseBody String testApi(
      @RequestParam(value = "productId", defaultValue = "", required = true) String productId,
      @RequestParam(value = "type", defaultValue = "update", required = true) String type,
      HttpServletRequest request) {
    try {
      String goodsSn = request.getParameter("productId");
      return searchService.updateProductIndex(goodsSn, type) + "";
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  // @RequestMapping(value = "/upsertProductById.do")
  // public @ResponseBody String testApi(
  // @RequestParam(value = "productId", defaultValue = "", required = true) String productId,
  // @RequestParam(value = "type", defaultValue = "update", required = true) String type) {
  // try {
  // return searchService.updateProductIndex(productId, type) + "";
  // } catch (Exception e) {
  // log.error(e.getMessage());
  // }
  // return "0";
  // }

  @RequestMapping(value = "/upsertProductByIds.do")
  public @ResponseBody String batchUpdate(
      @RequestParam(value = "list", defaultValue = "", required = true) String list) {
    try {
      if (StringUtils.isBlank(list)) {
        return "[]";
      }
      List<UpdateOption> options = JSON.parseArray(list, UpdateOption.class);

      List<UpdateOption> result = searchService.updateProductIndexByList(options);

      return JSON.toJSONString(result);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "[]";
  }

  @RequestMapping(value = "/updateProductTagsByIds.do")
  public @ResponseBody String updateProductTags(
      @RequestParam(value = "list", defaultValue = "", required = true) String list) {
    try {
      if (StringUtils.isBlank(list)) {
        return "0";
      }
      String[] ids = list.split(",");

      boolean rs = searchService.updateProductTags(Arrays.asList(ids));

      return rs ? "1" : "0";
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  /*
   * @RequestMapping(value = "/listAllStat") public @ResponseBody String listAllStat() { try {
   * 
   * MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); ObjectName mbeanName = new
   * ObjectName("com.metersbonwe.search.common.perf.perf:type=StopWatch,name=InMemoryStopWatch");
   * Object o = mbs.invoke(mbeanName, "listAllStat", null, null); return JSON.toJSONString(o); }
   * catch (Exception e) { log.error(e.getMessage()); } return "oh,no!"; }
   * 
   * @RequestMapping(value = "/clear") public @ResponseBody String clear() { try {
   * 
   * MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); ObjectName mbeanName = new
   * ObjectName("com.metersbonwe.search.common.perf.perf:type=StopWatch,name=InMemoryStopWatch");
   * mbs.invoke(mbeanName, "clear", null, null); // Object o = mbs.getAttribute(mbeanName,
   * "listAllStat"); return "ok"; } catch (Exception e) { log.error(e.getMessage()); } return "0"; }
   */

  /**
   * 搜索商品接口
   * 
   * @param p
   *          参数，参考：{@link com.mbgo.search.core.bean.query.ApiParameter}
   * @return
   */
  @RequestMapping(value = "/qg")
  public @ResponseBody String searchProduct(@ModelAttribute ApiParameter p) {
    try {
      long begin = System.currentTimeMillis();
      ProductQuery productQuery = p.toProductQuery();
      ProductQueryResult queryResult = searchService.search(productQuery);
      // 保存关键字信息
      String word = p.getWord();
      searchWordService.addOrUpdateWord(word, queryResult);
      long cost = System.currentTimeMillis() - begin;
      // caculate(cost);
      log.info("searchController.qgCost={}", cost);
      return JSON.toJSONString(queryResult);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  /**
   * 返回最近新上架商品，按照品牌码区分
   * 
   * @param p
   * @return
   */
  @RequestMapping(value = "/qng")
  public @ResponseBody String searchNewlyProduct(@ModelAttribute ParameterNewlyProductBean p) {
    if (p == null || StringUtils.isBlank(p.getBrandCode())) {
      return "0";
    }
    try {
      long begin = System.currentTimeMillis();
      ReturnNewlyProductBean result = searchService.searchNewlyProduct(p);
      long cost = System.currentTimeMillis() - begin;
      log.info("qng={}", cost);

      return JSON.toJSONString(result);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  private static AtomicLong total = new AtomicLong(0);
  private static AtomicLong min = new AtomicLong(10000);
  private static AtomicLong max = new AtomicLong(0);
  private static AtomicLong count = new AtomicLong(0);

  @RequestMapping(value = "/info")
  public @ResponseBody String info(
      @RequestParam(value = "type", defaultValue = "", required = true) String type) {
    try {
      if (StringUtils.isNotBlank(type) && type.equalsIgnoreCase("clear")) {
        caculate(-1);
      } else {

        long tt = total.longValue();
        float ttime = count.floatValue();
        long maxt = max.longValue();
        long mint = min.longValue();
        return "总耗时：" + tt + "<br />总次数：" + count.longValue() + "<br />最大耗时：" + maxt
            + "<br />最小耗时：" + mint + "<br />平均耗时：" + (tt / ttime);
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "1";
  }

  public void caculate(long cost) {
    if (cost == -1) {
      total.set(0);
      min.set(0);
      max.set(0);
      count.set(0);
    } else {
      total.addAndGet(cost);
      count.incrementAndGet();
      if (min.longValue() > cost) {
        min.set(cost);
      }
      if (max.longValue() < cost) {
        max.set(cost);
      }
    }
  }

  /**
   * 筛选器接口
   * 
   * @param p
   *          参数，参考：{@link com.mbgo.search.core.bean.query.ApiParameter}
   * @return
   */
  @RequestMapping(value = "/qf")
  public @ResponseBody String searchFilterData(@ModelAttribute ApiParameter p) {
    try {
      long begin = System.currentTimeMillis();

      ProductQuery productQuery = p.toProductQuery();

      FilterData result = searchService.searchFilterData(productQuery);

      long cost = System.currentTimeMillis() - begin;
      // caculate(cost);
      log.info("searchController.qfCost={}", cost);

      return JSON.toJSONString(result);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  /**
   * 
   * @param productCode
   * @param type
   * @return
   */
  @RequestMapping(value = "/reloadCache.do")
  public @ResponseBody String updateCache(
      @RequestParam(value = "type", defaultValue = "siteCategory", required = false) String type) {
    try {
      cacheService.reload(type);
      return "1";
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }
  
  @RequestMapping(value = "/q")
  public @ResponseBody String q(@ModelAttribute ApiParameter p) {
    try {
      ProductQuery productQuery = p.toProductQuery();
      ProductQueryResult queryResult = searchService.search(productQuery);
      return JSON.toJSONString(queryResult);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "0";
  }

  private static Logger log = LoggerFactory.getLogger(SearchController.class);

  @Resource(name = "apiSearchService")
  private SearchService searchService;

  @Resource(name = "cacheService")
  private CacheService cacheService;

  @Resource(name = "searchWordService")
  private SearchWordService searchWordService;

  public static void main(String[] args) {
    AtomicLong count = new AtomicLong(0);
    count.addAndGet(11);
    System.out.println(count.get());
    count.incrementAndGet();
    System.out.println(count.get());
  }
}
