package com.mbgo.search.core.tools.alsolike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mbgo.mybatis.mbsearch.bean.MgrDicKeyword;

/**
 * 过滤同字组成的词汇
 * 例如：牛仔裤 男， 男牛仔裤， 牛仔男裤
 * @author HQ01U8435
 *
 */
public class SameWordFilter {

	private List<MgrDicKeyword> _words = new ArrayList<MgrDicKeyword>(0);
	private Comparator<Integer> _comparator = new IntegerSort();
	private Map<String, String> _resultFilter = new HashMap<String, String>(5, 0.95f);
	
	public SameWordFilter(List<MgrDicKeyword> words) {
		_words = words;
	}

	public String code(String s) {
		String rs = "";
		List<Integer> codes = new ArrayList<Integer>();
		for(int i = 0, len = s.length(); i < len; i ++) {
			int cv = s.codePointAt(i);
			if(cv == 32) {
				continue;
			}
			codes.add(cv);
		}
		Collections.sort(codes, _comparator);
		for(Integer code : codes) {
			rs += String.valueOf(code) + "_";
		}
		return rs;
	}
	
	public List<MgrDicKeyword> filter() {
		List<MgrDicKeyword> rs = new ArrayList<MgrDicKeyword>(0);
		
		for(MgrDicKeyword word : _words) {
			String key = word.getWord();
			String code = code(key);
			String prevWord = _resultFilter.get(code);
			if(prevWord == null) {
				_resultFilter.put(code, key);
				rs.add(word);
			}
		}
		
		return rs;
	}
	
	
	class IntegerSort implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o1 - o2;
		}
	}
}
