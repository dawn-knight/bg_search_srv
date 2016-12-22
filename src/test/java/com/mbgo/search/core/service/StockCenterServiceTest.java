/*
* 2014-10-29 上午10:27:28
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.banggo.stockcenter.client.dataobject.PlatformStock;
import com.banggo.stockcenter.client.dataobject.PlatformStockOperatePO;
import com.banggo.stockcenter.client.dataobject.PlatformStockOperateRst;
import com.banggo.stockcenter.client.service.StockService;

public class StockCenterServiceTest extends BaseTest {

	@Resource(name = "stockService")
	private StockService stockService;
	
	@Test
	public void read() {
		PlatformStockOperatePO po = new PlatformStockOperatePO();
		List<PlatformStock> stockParamList = new ArrayList<PlatformStock>();

		PlatformStock stock = new PlatformStock();
		stock.setStoreId("");
		stock.setProductId("100022591");
		
		PlatformStock stock01 = new PlatformStock();
		stock01.setStoreId("HQ01S116");
		stock01.setProductId("100017011");
		stockParamList.add(stock);
//		stockParamList.add(stock01);

		po.setStockParamList(stockParamList);
		System.out.println(JSON.toJSONString(po));
		
		long b = System.currentTimeMillis();
		PlatformStockOperateRst result = stockService.platformGetSkuStockList(po);
		long time = System.currentTimeMillis() - b;
		System.out.println(JSON.toJSONString(result));
		System.out.println("stockService.platformGetSkuStockList " + time);
		if(result != null) {
			List<PlatformStock> pss = result.getStockList();
			if(pss != null) {
				for(PlatformStock ps : pss) {
					System.out.println(ps.getBarcodeId() +" : "+ps.getStock());
				}
			}
		}
	}
}
