/*
* 2014-10-30 上午10:08:09
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banggo.stockcenter.client.dataobject.PlatformStock;
import com.banggo.stockcenter.client.dataobject.PlatformStockOperatePO;
import com.banggo.stockcenter.client.dataobject.PlatformStockOperateRst;
import com.banggo.stockcenter.client.service.StockService;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;
import com.mbgo.mybatis.mbsearch.mapper.StockDetailInfoMapper;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.SearchErrorLogService;
import com.mbgo.search.core.service.StockHandlerService;

@Service("stockHandlerService")
public class StockHandlerServiceImpl implements StockHandlerService {

	@Override
	public List<StockDetailInfo> getProductStockInfo(List<Product> products) {
		List<StockDetailInfo> detailInfos = new ArrayList<StockDetailInfo>();
		PageManager pageManager = new PageManager(products.size(), 500);
		while(pageManager.hasNextPage()) {
			List<Product> temps = products.subList((int)pageManager.getFirst(), pageManager.getToIndex());
			
			List<String> productIds = new ArrayList<String>();
			
			//调用接口获得库存信息
			List<PlatformStock> result = queryFromStockCenter(temps, productIds);
			
			//如果接口未取得库存信息，则从数据库取得
			if(result == null || result.size() < 1) {
				List<StockDetailInfo> detailsFromMysql = stockDetailInfoMapper.getProductStockDetails(new MybatisBean(productIds));
				if(detailsFromMysql != null) {
					detailInfos.addAll(detailsFromMysql);
				}
			} else {
				detailInfos.addAll(convertToDeailts(result));
			}
		}
		
		return detailInfos;
	}
	
	private List<StockDetailInfo> convertToDeailts(List<PlatformStock> stocks) {
		List<StockDetailInfo> rs = new ArrayList<StockDetailInfo>();
		if(stocks != null) {
			for(PlatformStock stock : stocks) {
				StockDetailInfo sdi = new StockDetailInfo(stock);
				if(StringUtils.isNotBlank(sdi.getProductId())) {
					rs.add(sdi);
				}
			}
		}
		return rs;
	}
	/**
	 * 库存查询
	 * @param list
	 * @return
	 */
	private List<PlatformStock> queryFromStockCenter(List<Product> list, List<String> productIds) {

//		PlatformStockOperatePO po = new PlatformStockOperatePO();
//		try {
//			po.setStockParamList(packStockQuerys(list, productIds));
//			PlatformStockOperateRst result = stockService.platformGetSkuStockList(po);
//			
//			return result.getStockList();
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			searchErrorLogService.log(e.getMessage(), 2);
//		}
		return new ArrayList<PlatformStock>(0);
	}
	/**
	 * 将product对象封装成库存中心库存查询对象
	 * @param list
	 * @return
	 */
	private List<PlatformStock> packStockQuerys(List<Product> list, List<String> productIds) {
		List<PlatformStock> query = new ArrayList<PlatformStock>();
		
		if(list != null) {
			for(Product p : list) {
				PlatformStock ps = new PlatformStock();
				ps.setStoreId(p.getStoreId());
				ps.setProductId(p.getProductId());
				productIds.add(p.getProductId());
				query.add(ps);
			}
		}
		
		return query;
	}
	
	@Autowired
	private StockDetailInfoMapper stockDetailInfoMapper;

//	@Resource(name = "stockService")
//	private StockService stockService;

	@Resource(name = "searchErrorLogService")
	private SearchErrorLogService searchErrorLogService;
	private static Logger log = LoggerFactory.getLogger(StockHandlerServiceImpl.class);
}
