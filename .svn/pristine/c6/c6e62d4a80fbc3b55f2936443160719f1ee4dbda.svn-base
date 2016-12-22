/*
* 2014-12-16 下午2:37:39
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.outer;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mbgo.search.core.service.ProductIndexService;
import com.mbgo.search.core.service.SearchErrorLogService;

@SuppressWarnings("unused")
@Service("outerUpdateHandler")
public class OuterUpdateHandler {

	private IdsManager stockManager = null;
	
	private IdsManager promotionManager = null;
	
	private static OuterUpdateSync syncThread = null;
	
	
	public OuterUpdateHandler() {
		stockManager = new ProductIdsManager("stock");
		promotionManager = new ProductIdsManager("promotion");
		syncThread = new OuterUpdateSync();
		syncThread.start();
	}
	
	public void addStockProductId(String ids) {
		add(stockManager, ids);
	}
	
	public void addPromotionId(String ids) {
//		add(promotionManager, ids);
	}

	private void add(IdsManager manager, String ids) {
		if(StringUtils.isNotBlank(ids)) {
			String[] vals = ids.split(",");
			for(String s : vals) {
				manager.add(s);
			}
		}
	}
	
	class OuterUpdateSync extends Thread {
		public void run() {
			while(true) {
				try {
					Thread.sleep(60000);
					List<String> stockIds = stockManager.get();
					synStockInfoToIndex(stockIds);
					
//					List<String> promotionIds = promotionManager.get();
//					synPromotionInfoToIndex(promotionIds);
				} catch (Exception e) {
					searchErrorLogService.log(e.getMessage(), 4);
				}
			}
		}
		
		@Override
		public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
			log.error("setUncaughtExceptionHandler");
			if(syncThread == null || !syncThread.isAlive()) {
				syncThread = new OuterUpdateSync();
				syncThread.start();
			}
			super.setUncaughtExceptionHandler(eh);
		}
	}
	
	private void synStockInfoToIndex(List<String> pids) {
		productIndexService.sycnProductStocks(pids);
	}
//	private void synPromotionInfoToIndex(List<String> pids) {
//		productIndexService.sycnProductPromotions(pids);
//	}
	
	@Resource(name = "productIndexService")
	private ProductIndexService productIndexService;

	@Resource(name = "searchErrorLogService")
	private SearchErrorLogService searchErrorLogService;
	
	private static Logger log = LoggerFactory.getLogger(OuterUpdateHandler.class);
}
