/*
* 2014-11-5 下午1:23:14
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mbgo.search.api.service.SearchApiService;
import com.mbgo.search.core.bean.keyword.HotWordQuery;
import com.mbgo.search.core.bean.query.ApiParameter;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.service.AutoKeyService;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.SearchService;
import com.mbgo.search.core.service.SpellCheckService;
import com.mbgo.search.core.service.use4.RedisCacheService;

@Service("searchApiService")
public class SearchApiServiceImpl implements SearchApiService {

	@Override
	public ProductQueryResult searchProduct(ApiParameter apiParameter) {
		try {
			return searchService.search(apiParameter.toProductQuery());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new ProductQueryResult();
	}
	
	@Override
	public FilterData searchFilterData(ApiParameter apiParameter) {
		try {
			return searchService.searchFilterData(apiParameter.toProductQuery());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new FilterData();
	}
	
	@Override
	public String queryAutokey(String word, int limit) {
		
		return autoKeyService.searchAutoKey(word, limit);
	}
	
	@Override
	public String alsoLike(String word, int limit, int len) {
		
		return keywordDicService.alsoLike(word, limit, len);
	}
	
	@Override
	public List<String> hotSearchWord(HotWordQuery hq) {
		
		return redisCacheService.findHotWord(hq);
	}
	
	@Override
	public String spellCheck(String word) {
		
		return spellCheckService.spellCheckWord(word);
	}
	
	@Override
	public int updateProductIndex(String productId, String type) {
		
		return searchService.updateProductIndex(productId, type);
	}
	
	@Override
	public List<UpdateOption> updateProductIndexByList(List<UpdateOption> uos) {
		return searchService.updateProductIndexByList(uos);
	}
	
	@Override
	public boolean updateProductTags(List<String> pids) {
		return searchService.updateProductTags(pids);
	}
	
	private static Logger log = LoggerFactory.getLogger(SearchApiServiceImpl.class);
	@Resource(name = "apiSearchService")
	private SearchService searchService;
	@Resource(name = "autoKeyService")
	private AutoKeyService autoKeyService;

	@Resource(name = "spellCheckService")
	private SpellCheckService spellCheckService;

	@Resource(name = "keywordDicService")
	private KeywordDicService keywordDicService;

	@Resource(name = "redisCacheService")
	private RedisCacheService redisCacheService;
}
