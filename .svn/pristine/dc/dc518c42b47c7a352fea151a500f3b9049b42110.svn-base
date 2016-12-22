/*
* 2014-12-12 下午4:12:53
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.dataupdate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.mbgo.search.util.NullPathException;
import com.mbgo.search.util.ResourceReader;
import com.mbgo.search.util.ResourceWriter;

public class ProductCategory {

	public Map<String, String> getCategoryMap(String path) throws NullPathException, IOException {
		Map<String, String> rs = new HashMap<String, String>();
		ResourceReader r = new ResourceReader(new File(path));
		r.load();
		String line = null;
		while((line = r.readLine()) != null) {
			String[] vs = line.split(",");
			rs.put(vs[0].trim(), vs[1].trim());
		}
		return rs;
	}
	private Map<String, String> cateMap = null;
	private Map<String, String> goodsCate = null;
	@Before
	public void init() {
		try {
			cateMap = getCategoryMap("D:/t同事文档/l路远/分类.rtf");
			goodsCate = getCategoryMap("D:/t同事文档/l路远/老商品_分类.txt");
		} catch (NullPathException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test() {
//		UPDATE product_info SET category_id = ? WHERE product_code = ?;
		
		ResourceWriter w = new ResourceWriter(new File("D:/t同事文档/l路远/更新商品分类数据.sql"));
		for(Map.Entry<String, String> en : goodsCate.entrySet()) {
			String sn = en.getKey();
			String oldCate = en.getValue();
			String newCate = cateMap.get(oldCate);
			if(StringUtils.isNotBlank(newCate)) {
				String line = "UPDATE product_info SET category_id = " + newCate + " WHERE product_code = '" + sn + "';";
				w.write(line);
			}
		}
		w.close();
	}
}
