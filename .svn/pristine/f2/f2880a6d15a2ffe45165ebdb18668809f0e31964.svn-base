/*
* 2014-12-16 下午4:32:00
* 吴健 HQ01U8435
*/

package com.mbgo.search.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mbgo.search.core.outer.OuterUpdateHandler;

@Controller
@RequestMapping("/outUpdate")
public class OuterUpdateHandlerController {

	
	@RequestMapping(value = "/stockPids")
	public @ResponseBody String updateStockByPids(@RequestParam(value = "pids", defaultValue = "", required = true) String pids) {
		try {
			if(StringUtils.isBlank(pids)) {
				return "0";
			}
			
			outerUpdateHandler.addStockProductId(pids);
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "0";
	}
	

//	@RequestMapping(value = "/promotionPids")
//	public @ResponseBody String updatePromotionByPids(@RequestParam(value = "pids", defaultValue = "", required = true) String pids) {
//		try {
//			if(StringUtils.isBlank(pids)) {
//				return "0";
//			}
//			
//			outerUpdateHandler.addPromotionId(pids);
//			
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}
//		return "0";
//	}
	
	@Resource(name = "outerUpdateHandler")
	private OuterUpdateHandler outerUpdateHandler;

	private static Logger log = LoggerFactory.getLogger(OuterUpdateHandlerController.class);
}
