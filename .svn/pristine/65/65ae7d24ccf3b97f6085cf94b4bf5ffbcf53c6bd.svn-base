/*
* 2014-12-9 下午1:48:02
* 吴健 HQ01U8435
*/

package com.mbgo.search.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.mbgo.mybatis.mbsearch.bean.SearchErrorLog;
import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.service.SearchErrorLogService;
import com.mbgo.search.core.service.StockHandlerService;

@Controller
@RequestMapping("/jsp")
public class JspController {

	@RequestMapping(value = "/index")
	public ModelAndView projectView() {

		ModelAndView modelAndView = new ModelAndView("index");

		return modelAndView;
	}

	@RequestMapping(value = "/tools")
	public ModelAndView toolsPage() {

		ModelAndView modelAndView = new ModelAndView("tools");

		return modelAndView;
	}
	

	@RequestMapping(value = "/spell.do")
	public @ResponseBody String spellCheck(
			@RequestParam(value = "word", defaultValue = "", required = true) String word) {
		try {
		} catch (Exception e) {
		}
		return "";
	}
	
	@RequestMapping(value = "/errorLogs")
	public @ResponseBody String getErrorLogs(
			@RequestParam(value = "pageSize", defaultValue = "50", required = true) int pageSize, 
			@RequestParam(value = "pageNo", defaultValue = "1", required = true) int pageNo, 
			@RequestParam(value = "pid", defaultValue = "", required = true) String pid) {
		try {
			List<SearchErrorLog> list = searchErrorLogService.getErrorLogs(pageNo, pageSize, pid, "", "");
			
			return JSON.toJSONString(list);
		} catch (Exception e) {
		}
		return "[]";
	}
	
	@RequestMapping(value = "/stock")
	public @ResponseBody String getStocksFromStockCenter( 
			@RequestParam(value = "pid", defaultValue = "", required = true) String pid) {
//		if(StringUtils.isBlank(pid) || pid.length() < 6) {
//			return "";
//		}
//		try {
//			List<Product> products = new ArrayList<Product>();
//			Product p = new Product();
//			p.setProductId(pid);
//			products.add(p);
//			List<StockDetailInfo> rs = stockHandlerService.getProductStockInfo(products);
//			return JSON.toJSONString(rs);
//		} catch (Exception e) {
//		}
		return "[]";
	}
	
	@Resource(name = "searchErrorLogService")
	private SearchErrorLogService searchErrorLogService;
	
//	@Resource(name = "stockHandlerService")
//	private StockHandlerService stockHandlerService;
}
