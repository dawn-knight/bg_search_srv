/*
 * 2014-10-16 下午3:12:27 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.MgrHotKeyword;
import com.mbgo.mybatis.mbsearch.mapper.MgrHotKeywordMapper;
import com.mbgo.search.autokey.py.Pinyin;
import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.core.bean.keyword.Autokey;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.AutoKeyService;
import com.mbgo.search.core.service.ProductSearchService;
import com.mbgo.search.core.service.use4.RedisCacheService;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.core.tools.JobLockHandler;
import com.mbgo.search.core.tools.query.SolrQueryObj;
import com.mbgo.search.util.AutokeyConvernt;
import com.mbgo.search.util.FormatUtil;

@Service("autoKeyService")
public class AutoKeyServiceImpl implements AutoKeyService {

  @Override
  public String searchAutoKey(String word, int limit) {
    String result = "[]";
    List<Autokey> keys = new ArrayList<Autokey>(limit + 2);
    if (StringUtils.isBlank(word)) {
      return result;
    }

    String redisKey = word + "__" + limit;

    String rsFromRedis = redisCacheService.readFromRedis(redisKey);
    if (StringUtils.isNotBlank(rsFromRedis)) {
      return rsFromRedis;
    }

    try {

      limit = FormatUtil.valueAsDefault(limit, 1, 50, 20);

      word = AutokeyConvernt.withReplaceSpace(word);

      SolrQueryObj queryObj = new SolrQueryObj();
      queryObj.pushPrefixOr(FieldUtil.AUTOKEY.INDEX, word);
      queryObj.pushPrefixOr(FieldUtil.AUTOKEY.SHENGMU, word);
      queryObj.pushPrefixOr(FieldUtil.AUTOKEY.PINYING, word);

      String suff = Pinyin.convertString(word, "*", 10);
      if (suff != null && suff.trim().length() > 1) {
        queryObj.pushOr(FieldUtil.AUTOKEY.PINYING2, suff);
      }

      SolrQuery query = new SolrQuery();

      query.setQuery(queryObj.toString());

      query.setRows(limit);
      query.setSort(FieldUtil.AUTOKEY.SORT, ORDER.desc);

      SolrDocumentList rs = cloudAutokeySearchSolrClient.query(
          SolrCollectionNameDefineBean.AUTOKEY, query).getResults();

      // 遍历查询结果，取出需要的信息
      for (SolrDocument d : rs) {
        // 用于显示的内容
        String k = d.get(FieldUtil.AUTOKEY.SHOW).toString();
        long c = Long.parseLong(d.get(FieldUtil.AUTOKEY.COUNT).toString());

        Autokey key = new Autokey();
        key.setWord(k);
        key.setCount(c);

        keys.add(key);
      }

      result = JSON.toJSONString(keys);
      if (keys.size() > 0) {// 有结果，就保存redis
        redisCacheService.saveToRedis(redisKey, result);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  private static final String AUTOKEY_LOCK = "solrcloudautokeyLock";

  @Override
  public boolean rebuildAutokeyIndex() {

    boolean isGetStart = JobLockHandler.getLock(AUTOKEY_LOCK, 120);
    if (!isGetStart) {
      return false;
    }
    long autokeyDataVersion = System.currentTimeMillis();
    long totalCount = 0;
    try {
      log.debug("get lock[{}] success and rebuildAutokeyIndex begin, version:{}", AUTOKEY_LOCK, autokeyDataVersion);
      long total = mgrHotKeywordMapper.countAll();
      if (total < 10) {
        log.debug("rebuildAutokeyIndex failed, there is no enough datas.");
        return false;
      }

      PageManager pageManager = new PageManager(total, 5000);
      while (pageManager.hasNextPage()) {
        List<MgrHotKeyword> keywords = mgrHotKeywordMapper.getHotKeyword(new MybatisBean(pageManager.getFirst(), pageManager.getMax()));

        List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
        if (keywords != null) {
          long b = System.currentTimeMillis();
          for (MgrHotKeyword keyword : keywords) {
            SolrInputDocument d = createAutokeyDocument(keyword, autokeyDataVersion);
            if (d != null) {
              docs.add(d);
              totalCount++;
            }
          }
          if (docs.size() > 0) {
            cloudAutokeySearchSolrClient.add(SolrCollectionNameDefineBean.AUTOKEY, docs);
          }
          if (totalCount > 20000) {
            totalCount = 0;
            cloudAutokeySearchSolrClient.commit(SolrCollectionNameDefineBean.AUTOKEY);
          }
          long cost = System.currentTimeMillis() - b;
          log.debug("rebuildAutokeyIndex first={}, max={}, cost={} ms", new Object[] { pageManager.getFirst(), pageManager.getMax(), cost });
        }
      }
      if (totalCount > 0) {
        cloudAutokeySearchSolrClient.deleteByQuery(SolrCollectionNameDefineBean.AUTOKEY, FieldUtil.DATA_VERSION + ": [0 TO "
            + (autokeyDataVersion - 1) + "]");
      }

      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (isGetStart) {
        try {
          cloudAutokeySearchSolrClient.commit(SolrCollectionNameDefineBean.AUTOKEY);
          cloudAutokeySearchSolrClient.optimize(SolrCollectionNameDefineBean.AUTOKEY);
        } catch (SolrServerException e) {
          log.error(e.getMessage(), e);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
        log.debug("rebuildAutokeyIndex end, version:{}", autokeyDataVersion);
        JobLockHandler.closeLock(AUTOKEY_LOCK);
      }
    }

    return false;
  }

  private SolrInputDocument createAutokeyDocument(MgrHotKeyword keyword, long version) {
    SolrInputDocument document = new SolrInputDocument();

    document.setField(FieldUtil.DATA_VERSION, version);

    String key = keyword.getWord();

    long count = productSearchService.countForWord(key);

    if (count < 1) {
      return null;
    }

    String pingyin = Pinyin.getPinyin(key);
    String shengmu = Pinyin.getshengmu(key);

    document.addField(FieldUtil.AUTOKEY.INDEX, AutokeyConvernt.withReplaceSpace(key), 1.0f);
    document.addField(FieldUtil.AUTOKEY.SHOW, AutokeyConvernt.withOneSpace(key), 1.0f);
    document.addField(FieldUtil.AUTOKEY.ANALYZE, key, 1.0f);
    document.addField(FieldUtil.AUTOKEY.PINYING, pingyin, 1.0f);

    document.addField(FieldUtil.AUTOKEY.PINYING2, Pinyin.convertString(key, -1), 1.0f);

    document.addField(FieldUtil.AUTOKEY.SHENGMU, shengmu, 1.0f);
    document.addField(FieldUtil.AUTOKEY.COUNT, count, 1.0f);
    document.addField(FieldUtil.AUTOKEY.SORT, count, 1.0f);

    return document;
  }

  @Autowired
  private MgrHotKeywordMapper mgrHotKeywordMapper;

  /*
   * @Resource(name = "lbAutokeySearchSolrServer") private LBHttpSolrClient autokeySearchSolrServer;
   * 
   * @Resource(name = "lbAutokeyIndexSolrServer") private LBHttpSolrClient autokeyIndexSolrServer;
   */

  @Resource(name = "cloudAutokeySearchSolrClient")
  private CloudSolrClient cloudAutokeySearchSolrClient;

  @Resource(name = "productSearchService")
  private ProductSearchService productSearchService;
  @Resource(name = "redisCacheService")
  private RedisCacheService redisCacheService;

  private static Logger log = LoggerFactory.getLogger(AutoKeyServiceImpl.class);
}
