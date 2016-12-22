/*
* 2014-9-25 下午4:43:50
* 吴健 HQ01U8435
*/

package com.mbgo.search.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mbgo.search.core.bean.keyword.HotWordQuery;
import com.mbgo.search.core.service.AutoKeyService;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.SpellCheckService;
import com.mbgo.search.core.service.use4.RedisCacheService;

/**
 * 关键字相关接口
 *
 */
@Controller("/keyword")
@RequestMapping("/keyword")
public class KeywordController {

	/**
	 * 关键字自动补全接口
	 * @param word 关键字
	 * @param limit	限制结果数量
	 * @param url	示例接口：../keyword/autokey.do?word=&limit=
	 * @return
	 */
	@RequestMapping(value = "/autokey.do")
	public @ResponseBody String autokey(
			@RequestParam(value = "word", defaultValue = "", required = true) String word,
			@RequestParam(value = "limit", defaultValue = "20", required = true) int limit) {
		try {
			long begin = System.currentTimeMillis();
			String rs = autoKeyService.searchAutoKey(word, limit);
			long cost = System.currentTimeMillis() - begin;
			log.info("autokey={}", cost);
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "[]";
	}

	/**
	 * 关键字拼写检查接口：您是不是在找
	 * @param word 	原始关键字
	 * @param url	示例接口：../keyword/spell.do?word=
	 * @return
	 */
	@RequestMapping(value = "/spell.do")
	public @ResponseBody String spellCheck(
			@RequestParam(value = "word", defaultValue = "", required = true) String word) {
		try {
			long begin = System.currentTimeMillis();
			String rs = spellCheckService.spellCheckWord(word);

			long cost = System.currentTimeMillis() - begin;
			log.info("spell={}", cost);
			
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "";
	}

	/**
	 * 相关关键字查询接口：相关搜索
	 * @param word	原始关键字
	 * @param limit	限制数量
	 * @param len	限制半角长度
	 * @param url	示例接口：../keyword/alsolike.do?word=&limit=&len=
	 * @return
	 */
	@RequestMapping(value = "/alsolike.do")
	public @ResponseBody String alsolike(
			@RequestParam(value = "word", defaultValue = "", required = true) String word,
			@RequestParam(value = "limit", defaultValue = "20", required = true) int limit,
			@RequestParam(value = "len", defaultValue = "80", required = true) int len) {
		try {
			long begin = System.currentTimeMillis();
			String rs = keywordDicService.alsoLike(word, limit, len);

			long cost = System.currentTimeMillis() - begin;
			log.info("alsolike={}", cost);
			
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "[]";
	}
	
	/**
	 * 热门关键字列表接口
	 * @param limit	数量
	 * @param len	半角长度
	 * @param url	示例接口：../keyword/hotword.do?limit=&len=
	 * @return
	 */
	@RequestMapping(value = "/hotword.do")
	public @ResponseBody String hotword(@ModelAttribute HotWordQuery q) {
		try {
			long begin = System.currentTimeMillis();
			q.init();
			String rs = JSON.toJSONString(redisCacheService.findHotWord(q));
			long cost = System.currentTimeMillis() - begin;
			log.info("hotword={}", cost);
			
			return rs;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "[]";
	}
	
	private static Logger log = LoggerFactory.getLogger(KeywordController.class);
	
	@Resource(name = "autoKeyService")
	private AutoKeyService autoKeyService;

	@Resource(name = "spellCheckService")
	private SpellCheckService spellCheckService;

	@Resource(name = "keywordDicService")
	private KeywordDicService keywordDicService;

	@Resource(name = "redisCacheService")
	private RedisCacheService redisCacheService;
}
