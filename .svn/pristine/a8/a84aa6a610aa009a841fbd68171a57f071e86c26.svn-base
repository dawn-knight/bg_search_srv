package com.mbgo.search.core.tools.alsolike.device;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.tools.alsolike.wordstock.Stock;

/**
 * 权重加分器实现
 * @author HQ01U8435
 *
 */
public class Device implements IWeightingDevice {
	private static Logger log = LoggerFactory.getLogger(IWeightingDevice.class);
	
	public static String TXT_BASE_PATH = "/com/mbgo/search/core/tools/alsolike/device/";
	
	/**
	 * 分类器对应的包含匹配树的词典对象
	 */
	protected Stock _stock;
	/**
	 * 分类器对应的默认查询关键字
	 */
	private String _words;
	
	public Device(String path, String words) {
		//根据匹配树所需字典路径，初始化一个词典对象
		_stock = new Stock(path);
		_words = words;
		try {
			//初始化匹配树
			_stock.initDictMap();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public int contain(String word) {
		List<String> rs = _stock.process(word, false);
		return rs.size();
	}

	@Override
	public String getQueryWord() {
		
		return _words;
	}

	@Override
	public int getQueryWeight() {
		return 5;
	}

	@Override
	public void loadWord(String w) {
		_stock.loadExternal(w);
	}
	
	@Override
	public int getKeyWeight() {
		return 1;
	}
}
