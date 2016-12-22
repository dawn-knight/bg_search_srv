/*
* 2014-10-28 下午3:17:12
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.use4;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banggo.common.redis.RedisTemplate;
import com.mbgo.mybatis.mbsearch.bean.MgrHotSearchKeyword;
import com.mbgo.mybatis.mbsearch.mapper.MgrHotSearchKeywordMapper;
import com.mbgo.search.core.bean.keyword.HotWordQuery;
import com.mbgo.search.core.bean.keyword.HotWordResult;
import com.mbgo.search.core.tools.ProductTools;
import com.mbgo.search.util.StringUtil;

/**
 * 相关关键词查询，redis
 * @author 吴健 HQ01U8435
 *
 */
@Service("redisCacheService")
public class RedisCacheService {
	private static Logger log = LoggerFactory.getLogger(RedisCacheService.class);

	/**
	 * 最热搜关键词列表
	 * @param query
	 * @return
	 */
	public List<String> findHotWord(HotWordQuery query) {
		List<String> rs = new ArrayList<String>();
		try {
			if(query == null) {
				return rs;
			}
			
			rs = readFromRedis(query);
			if(rs == null || rs.size() > 0) {
				return rs;
			}
			
			List<MgrHotSearchKeyword> keywords = mgrHotSearchKeywordMapper.selectList(query);
			int len = 0, maxLen = query.getLen();
			
			for(MgrHotSearchKeyword k : keywords) {
				String word = k.getWord();
				len += StringUtil.lengthDBC(word);
				if(len > maxLen) {
					break;
				}
				rs.add(word);
			}
			
			saveToRedis(query, rs);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return rs;
	}
	
	public List<String> readFromRedis(HotWordQuery query) {
		if(!query.getIsCache().equalsIgnoreCase("1")) {
			return new ArrayList<String>(0);
		}
		String key = query.redisKey();
		HotWordResult rs = redisTemplate.getPojo(key);
		if(rs == null) {
			return new ArrayList<String>(0);
		}
		return rs.getList();
	}
	
	public void saveToRedis(HotWordQuery query, List<String> rs) {
		redisTemplate.setExPojo(query.redisKey(), 60, new HotWordResult(rs));
	}
	
	@Autowired
	private MgrHotSearchKeywordMapper mgrHotSearchKeywordMapper;
	@Resource(name = "redisTemplate")
	private RedisTemplate redisTemplate;
	
	public String readFromRedis(String key) {
		return redisTemplate.get(key);
	}
	public void saveToRedis(String key, String value) {
		redisTemplate.setEx(key, ProductTools.keyword_live, value);
	}
	
	public static void main(String[] args) {
		System.out.println(new DateTime().minusHours(24).toString("yyyy-MM-dd HH:mm:ss"));
	}
}
