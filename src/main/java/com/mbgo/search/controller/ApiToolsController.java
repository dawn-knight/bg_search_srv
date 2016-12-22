/*
 * 2014-9-2 上午11:35:30 吴健 HQ01U8435
 */

package com.mbgo.search.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mbgo.search.core.service.AutoKeyService;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.ProductIndexService;
import com.mbgo.search.core.service.SpellCheckService;
import com.mbgo.search.core.service.use4.CacheService;

@Controller("apiTools")
@RequestMapping("/apiTools")
public class ApiToolsController {

  @RequestMapping(value = "/rebuildProduct")
  public @ResponseBody void rebuildIndex() {
    log.debug("[manuRebuild]rebuild product index");
    productIndexService.rebuildProductIndex();
  }

  @RequestMapping(value = "/buildAutokey")
  public @ResponseBody void buildAutokey() {
    try {
      log.debug("[manuRebuild]rebuild autokey");
      autoKeyService.rebuildAutokeyIndex();
    } catch (Exception e) {}
  }

  @RequestMapping(value = "/buildSpellCheck")
  public @ResponseBody void buildSpellCheck() {
    try {
      log.debug("[manuRebuild]rebuild spell check");
      spellCheckService.rebuildIndex();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @RequestMapping(value = "/buildKeywordDic")
  public @ResponseBody void buildKeywordDic() {
    try {
      log.debug("[manuRebuild]rebuild keyword index");
      keywordDicService.rebuildDicIndex();
    } catch (Exception e) {

    }
  }

  @RequestMapping(value = "/reloadCache")
  public @ResponseBody String reloadCache(@RequestParam(value = "type", defaultValue = "", required = false) String type) {
    // type:
    // category,color,size,attribute,brand,store,siteCategory,tagChain,wordConvert
    try {
      log.debug("[manuRebuild]reload cache[{}]", type);
      if (StringUtils.isBlank(type)) {
        return "error : must select something.";
      }
      String[] types = type.split(",");

      for (String t : types) {
        log.debug("reload cate [{}]", t);
        cacheService.reload(t.trim());
      }
    } catch (Exception e) {

    }
    return "reload cache [" + type + "] succesfully.";
  }

  @RequestMapping(value = "/rebuildAndUpdateSwitch")
  public @ResponseBody String rebuildAndUpdateSwitch(
      @RequestParam(value = "password", defaultValue = "", required = true) String password,
      @RequestParam(value = "openFlag", defaultValue = "1", required = true) int openFlag) {
    try {
      log.debug("[manuRebuild]change rebuild and update index switch status to:" + openFlag);
      return productIndexService.rebuildAndUpdateSwitch(password, openFlag);
    } catch (Exception e) {

    }
    return "execute method occurs error";
  }

  @Resource(name = "cacheService")
  private CacheService cacheService;

  @Resource(name = "productIndexService")
  ProductIndexService productIndexService;

  @Resource(name = "autoKeyService")
  private AutoKeyService autoKeyService;

  @Resource(name = "spellCheckService")
  private SpellCheckService spellCheckService;

  @Resource(name = "keywordDicService")
  private KeywordDicService keywordDicService;

  private static Logger log = LoggerFactory.getLogger(ApiToolsController.class);

}
