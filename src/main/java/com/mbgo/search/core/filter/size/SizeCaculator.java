/*
* 2014-10-13 下午3:42:48
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.filter.size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.mbgo.search.core.filter.itf.FilterBeanConvertor;
import com.mbgo.search.core.filter.itf.FilterCaculater;

public class SizeCaculator extends FilterCaculater {
	private static Map<Pattern, Integer> patternSort = new LinkedHashMap<Pattern, Integer>();

	static {
		patternSort.clear();
		patternSort.put(Pattern.compile("^(\\d{1,})\\/(\\d{1,})[a-zA-Z]*$", 0), 2);//150/76A
		patternSort.put(Pattern.compile("(\\d{1,})\\/(\\d+)[-|或|和]", 0), 2);//上装90/52-下装90/50
		patternSort.put(Pattern.compile("^(\\d{1,})$", 0), 1);//225
		patternSort.put(Pattern.compile("(\\d{1,})mm", 0), 1);//255mm
		patternSort.put(Pattern.compile("(\\d{1,})cm", 0), 1);//90cm
		patternSort.put(Pattern.compile("[a-z](\\d{1,})", 0), 1);//A75
	}
	
	private List<SizeBean> sizeBeans = new ArrayList<SizeBean>();
	public SizeCaculator() {
		
	}
	public void addSizeBeanInfo(String sizeCode, String sizeName, long count) {
		if(StringUtils.isNotBlank(sizeCode) && count > 0) {
			SizeBean sizeBean = new SizeBean();
			sizeBean.setSizeCode(sizeCode);
			sizeBean.setCount(count);
			sizeBean.setSizeName(sizeName);
			int sort = getSortValue(sizeName);
			sizeBean.setSort(sort);
			sizeBeans.add(sizeBean);
		}
	}
	
		
	private int getSortValue(String code) {
		if(StringUtils.isBlank(code)) {
			return -1;
		}
		code = code.toLowerCase();
		int max = patternSort.size() + 2;
		for(Map.Entry<Pattern, Integer> en : patternSort.entrySet()) {
			int levelSort = max * 20000 + 1000;
			int sort = getSort(en.getKey(), code, 1);
			max --;
			if(sort > 0) {
				int second = 0;
				if(en.getValue() > 1) {
					second = getSort(en.getKey(), code, 2);
				}
				
				int rs = levelSort - sort * 10 - second;
//				System.out.println(code+" : "+sort+" : "+second+" : " + levelSort + " : " +rs);
				return rs;
			}
		}
		return -1;
	}
	
	private int getSort(Pattern p, String code, int index) {
		Matcher m = p.matcher(code);
		
		try {
			while(m.find()) {
				return Integer.parseInt(m.group(index));
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	@Override
	public List<? extends FilterBeanConvertor> getConvertor() {
		Collections.sort(sizeBeans, new Comparator<SizeBean>() {
			@Override
			public int compare(SizeBean o1, SizeBean o2) {
				return o2.getSort().compareTo(o1.getSort());
			}
		});
		return sizeBeans;
	}

}
