/*
* 2014-12-10 下午4:32:28
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.keyword;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.mbgo.search.autokey.py.Pinyin;
import com.mbgo.search.util.NullPathException;
import com.mbgo.search.util.ResourceReader;
import com.mbgo.search.util.ResourceWriter;

public class CreateKeyword {

	private static String file = "G:/mbsearch/txt/all-keyword.txt";
	private static String out = "D:/jakarta-jmeter-2.4/bin/autokey.txt";
	@Test
	public void test() throws NullPathException, IOException {
		ResourceReader r = new ResourceReader(new File(file));
		ResourceWriter w = new ResourceWriter(new File(out));
		String line = null;
		r.load();
		while((line = r.readLine()) != null) {
//			String py = Pinyin.getshengmu(line);
			if(StringUtils.isNumeric(line)) {
				continue;
			}
			String utf8 = URLEncoder.encode(line, "utf-8");
			w.write(utf8);
		}
		
		r.close();
		w.close();
	}
	

	@Test
	public void spellCheck() throws NullPathException, IOException {
		ResourceReader r = new ResourceReader(new File(file));
		ResourceWriter w = new ResourceWriter(new File("D:/jakarta-jmeter-2.4/bin/spell.txt"));
		String line = null;
		r.load();
		while((line = r.readLine()) != null) {
			if(StringUtils.isNumeric(line)) {
				continue;
			}
			String py = Pinyin.getshengmu(line);
			if(py.length() > 7) {
				py = py.substring(0, 7);
			}
			w.write(py);
		}
		
		r.close();
		w.close();
	}
}
