package com.mbgo.search.core.tools.alsolike.device;

import java.util.List;

import com.mbgo.search.core.tools.alsolike.wordstock.Stock;

/**
 * 
 * @author 吴健 (HQ01U8435)
 * 分类器的分词核心
 */
public class AnalyzerDevice {
	
	public static class AnalyzerHanlder{
		public static AnalyzerDevice analyzerDevice = new AnalyzerDevice();
	}
	public static AnalyzerDevice getInstance() {
		return AnalyzerHanlder.analyzerDevice;
	}
	/**
	 * 分词器基础词典
	 */
	private static final String path = Device.TXT_BASE_PATH + "base.txt";
	/**
	 * 分类器对应的包含匹配树的词典对象
	 */
	private static Stock _stock = initStock();
	
	private static Stock initStock() {
		Stock stock = new Stock(path);
		try {
			stock.initDictMap();
//			IndustryWordDao dao = new IndustryWordDao(SpringUtil.getBeanAsPool("connectionPool"));
//			Query<IndustryWord> q = dao.createQuery();
//			for(IndustryWord ind : q) {
//				stock.loadExternal(ind.getKeyWord());
//			}
		} catch (Exception e) { }
		return stock;
	}
	
	public static void load(String k) {
		if(_stock == null) {
			_stock = initStock();
		}
		_stock.loadExternal(k);
	}

	public List<String> analyze(String word) {
		if(_stock == null) {
			_stock = initStock();
		}
		List<String> rs = _stock.process(word, false);
		return rs;
	}
}
