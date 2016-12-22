/*
 * 2014-10-17 下午4:21:18 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.MgrHotKeyword;
import com.mbgo.mybatis.mbsearch.mapper.MgrDicKeywordMapper;
import com.mbgo.mybatis.mbsearch.mapper.MgrHotKeywordMapper;
import com.mbgo.search.autokey.py.Levenshtein;
import com.mbgo.search.autokey.py.Pinyin;
import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.ProductSearchService;
import com.mbgo.search.core.service.SpellCheckService;
import com.mbgo.search.core.service.use4.RedisCacheService;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.core.tools.JobLockHandler;
import com.mbgo.search.core.tools.query.SolrQueryObj;
import com.mbgo.search.core.tools.spell.CheckerBean;
import com.mbgo.search.core.tools.spell.SpellCheckerUtil;
import com.mbgo.search.util.FormatUtil;

@Service("spellCheckService")
public class SpellCheckServiceImpl implements SpellCheckService {

  private static final String SPELL_LOCK = "solrcloudspellLock";

  @Override
  public void rebuildIndex() {
    boolean isGetStart = JobLockHandler.getLock(SPELL_LOCK, 120);
    if (!isGetStart) {
      return;
    }

    long dataVersion = System.currentTimeMillis();
    long counter = 0;
    // 分类名称
    try {
      log.debug("get lock[{}] success and rebuild spell check index begin", SPELL_LOCK);
      long total = mgrDicKeywordMapper.countAll();
      if (total < 10) {
        return;
      }
      PageManager pm = new PageManager(total, 10000);
      // 行业词、分词词典
      while (pm.hasNextPage()) {
        List<String> dics = mgrDicKeywordMapper.getDicKeyword(new MybatisBean(pm.getFirst(), pm.getMax()));
        if (dics != null) {
          for (String word : dics) {
            long count = productSearchService.countForWord(word);
            if (count < 4) {
              continue;
            }
            counter++;
            addGramDocument(word, dataVersion);
          }
          if (counter > 1) {
            cloudSpellCheckSearchSolrClient.commit(SolrCollectionNameDefineBean.SPELL);
          }
        }
      }

      pm = new PageManager(mgrHotKeywordMapper.countAll(), 10000);
      // 热门词
      while (pm.hasNextPage()) {
        List<MgrHotKeyword> dics = mgrHotKeywordMapper.getHotKeyword(new MybatisBean(pm.getFirst(), pm.getMax()));
        if (dics != null) {
          for (MgrHotKeyword word : dics) {
            addGramDocument(word.getWord(), dataVersion);
          }
          cloudSpellCheckSearchSolrClient.commit(SolrCollectionNameDefineBean.SPELL);
        }
      }
      cloudSpellCheckSearchSolrClient.deleteByQuery(SolrCollectionNameDefineBean.SPELL,
          FieldUtil.DATA_VERSION + ": [0 TO " + (dataVersion - 1) + "]");
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {

      if (isGetStart) {
        try {
          cloudSpellCheckSearchSolrClient.commit(SolrCollectionNameDefineBean.SPELL);
          cloudSpellCheckSearchSolrClient.optimize(SolrCollectionNameDefineBean.SPELL);
        } catch (SolrServerException e) {
          log.error(e.getMessage(), e);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
        log.debug("rebuild spell check index end");
        JobLockHandler.closeLock(SPELL_LOCK);
      }
    }
  }

  @Override
  public String spellCheckWord(String src) {
    try {
      if (StringUtils.isBlank(src)) {
        return src;
      }

      String tempRedis = redisCacheService.readFromRedis(src);
      if (tempRedis != null) {
        return tempRedis;
      }

      String[] ws = src.split(" ");
      String rs = "";

      // 对于空格分开的多个关键字一次进行拼写检查，最终返回空格分隔的检查结果
      for (String w : ws) {
        if (w.length() < 1) {
          continue;
        }
        rs += bestSpellCheck(w) + " ";
      }

      redisCacheService.saveToRedis(src, rs.trim());

      return rs.trim();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }

  /**
   * 根据单个关键词，寻找与之最接近的新词
   * 
   * @param word
   * @return
   */
  private String bestSpellCheck(String word) {
    try {
      List<CheckerBean> cbs = new ArrayList<CheckerBean>();
      int chineseLen = 0; // 对应汉字的大概长度
      // 如果是纯粹的汉字，则添加汉字分隔信息和计算汉字长度
      if (!FormatUtil.isNumberOrLetter(word)) {
        cbs.addAll(SpellCheckerUtil.getNGrams(word));
        chineseLen = word.length();
      } else {
        chineseLen = SpellCheckerUtil.getWordPinyinLen(word);
      }

      // 添加拼音的分析信息
      cbs.addAll(SpellCheckerUtil.getPinyins(word));

      SolrQuery query = new SolrQuery();
      query.setRows(10);
      SolrQueryObj queryObj = new SolrQueryObj(true);

      // 拼接汉字、拼音的分析信息，设置查询条件
      for (CheckerBean cb : cbs) {
        if (cb.getValue().equalsIgnoreCase("_")) {
          continue;
        }
        if (cb.isAnd()) {
          queryObj.pushPrefixAnd(cb.getKey(), cb.getValue());
        } else {
          queryObj.pushOr(cb.getKey(), cb.getValue());
        }
      }

      query.setQuery(queryObj.toString());

      QueryResponse response = cloudSpellCheckSearchSolrClient.query(
          SolrCollectionNameDefineBean.SPELL, query);
      SolrDocumentList list = response.getResults();

      // 最终返回关键字
      String key = "";
      // 编辑距离
      int dis = 10000;
      // 原始关键字的拼音
      String wordPy = Pinyin.getPinyin(word, "", false);

      for (SolrDocument doc : list) {
        // 备选关键字
        String key1 = doc.getFieldValue("keyword").toString();
        // 备选关键字对应的拼音
        String keyPy = Pinyin.getPinyin(key1, "", false);

        int distance = Levenshtein.distance(wordPy, keyPy);

        if (dis > distance && key1.length() == chineseLen) {
          key = key1;
          dis = distance;
        }
      }
      return key;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "";
  }

  /**
   * 将关键字转换成拼写检查文档 按照汉子和拼音两种方式来分布转换
   * 
   * @param word
   */
  private void addGramDocument(String word, long dataVersion) {

    if (StringUtils.isNotBlank(word)) {
      List<CheckerBean> result = SpellCheckerUtil.getNGrams(word);
      SolrInputDocument document = new SolrInputDocument();
      for (CheckerBean cb : result) {
        document.addField(cb.getKey(), cb.getValue(), 1f);
      }

      List<CheckerBean> result1 = SpellCheckerUtil.getPinyins(word);
      for (CheckerBean cb : result1) {
        document.addField(cb.getKey(), cb.getValue(), 1f);
      }

      document.addField("keyword", word, 1f);
      document.setField(FieldUtil.DATA_VERSION, dataVersion);

      try {
        cloudSpellCheckSearchSolrClient.add(SolrCollectionNameDefineBean.SPELL, document);
      } catch (SolrServerException e) {
        log.error(e.getMessage());
      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }
  }

  @Resource(name = "redisCacheService")
  private RedisCacheService redisCacheService;

  /*
   * @Resource(name = "lbSpellCheckIndexSolrServer") private LBHttpSolrClient
   * spellCheckIndexSolrServer;
   * 
   * @Resource(name = "lbSpellCheckSearchSolrServer") private LBHttpSolrClient
   * spellCheckSearchSolrServer;
   */

  @Resource(name = "cloudSpellCheckSearchSolrClient")
  private CloudSolrClient cloudSpellCheckSearchSolrClient;

  @Autowired
  private MgrHotKeywordMapper mgrHotKeywordMapper;
  @Autowired
  private MgrDicKeywordMapper mgrDicKeywordMapper;
  @Resource(name = "productSearchService")
  private ProductSearchService productSearchService;

  private static Logger log = LoggerFactory.getLogger(SpellCheckServiceImpl.class);
}
