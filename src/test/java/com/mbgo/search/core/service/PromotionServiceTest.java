/*
* 2014-12-8 下午3:05:30
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.metersbonwe.promotion.api.PlatformPromotionApi;
import com.metersbonwe.promotion.bean.APIPromotionBackMsgBean;
import com.metersbonwe.promotion.bean.ParaSingleGoodsInfoBean;
import com.metersbonwe.promotion.bean.ReturnSingleGoodsPromoInfoBean;

public class PromotionServiceTest extends BaseTest {/*
	
	@Autowired
	private PlatformPromotionApi promotionService;
	private List<ParaSingleGoodsInfoBean> createParam(String[] pids) {
		List<ParaSingleGoodsInfoBean> params = new ArrayList<ParaSingleGoodsInfoBean>();
		for(String s : pids) {
			ParaSingleGoodsInfoBean p = new ParaSingleGoodsInfoBean();
			p.setProductId(s);
			p.setShopNumber("HQ01S116");
			params.add(p);
		}
		return params;
	}
	@Test
	public void test() {
		List<ParaSingleGoodsInfoBean> params = createParam(new String[]{"100000001", "100000033", "100020675", "100001039"});
		APIPromotionBackMsgBean<ReturnSingleGoodsPromoInfoBean> rs = promotionService.getGoodsPromoInfos(params);

		ReturnSingleGoodsPromoInfoBean goodsPromoInfoBean = rs.getResult();
		
		if(goodsPromoInfoBean == null) {
			System.out.println("goodsPromoInfoBean is null");
			return;
		}
		List<ParaSingleGoodsInfoBean> list = goodsPromoInfoBean.getPromoInfos();
		for(ParaSingleGoodsInfoBean p : list) {
			Map<String, String> map = p.getPromoIdAndNameMap();
			if(map == null) {
				System.out.println("map is null");
			}
			System.out.println(p.getProductId() + " : " + map);
			System.out.println();
		}
	}
*/}
