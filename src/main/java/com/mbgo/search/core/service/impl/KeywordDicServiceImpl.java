/*
 * 2014-10-19 下午12:34:17 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
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
import com.mbgo.mybatis.mbsearch.bean.MgrDicKeyword;
import com.mbgo.mybatis.mbsearch.mapper.MgrDicKeywordMapper;
import com.mbgo.search.autokey.Assemble;
import com.mbgo.search.autokey.KeywordBean;
import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.core.dataetl.page.PageManager;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.ProductSearchService;
import com.mbgo.search.core.service.use4.RedisCacheService;
import com.mbgo.search.core.tools.FieldUtil;
import com.mbgo.search.core.tools.JobLockHandler;
import com.mbgo.search.core.tools.alsolike.SameWordFilter;
import com.mbgo.search.core.tools.alsolike.WordAnalyzer;
import com.mbgo.search.core.tools.alsolike.device.AnalyzerDevice;
import com.mbgo.search.core.tools.alsolike.device.IWeightingDevice;
import com.mbgo.search.core.tools.query.SolrQueryObj;
import com.mbgo.search.util.AutokeyConvernt;
import com.mbgo.search.util.KeywordFilter;
import com.mbgo.search.util.StringUtil;
import com.mbgo.search.util.bean.WordBean;

@Service("keywordDicService")
public class KeywordDicServiceImpl implements KeywordDicService {

  private static final String KEYWORD_LOCK = "solrcloudkeywordDicLock";

  @Override
  public boolean rebuildDicIndex() {
    boolean isGetStart = JobLockHandler.getLock(KEYWORD_LOCK, 120);

    if (!isGetStart) {
      return false;
    }

    log.debug("get lock[{}] success and rebuildDicIndex begin", KEYWORD_LOCK);
    long dataVersion = System.currentTimeMillis();
    long counter = 0;
    try {
      long total = mgrDicKeywordMapper.countAll();
      if (total < 10) {
        return false;
      }
      PageManager pageManager = new PageManager(total, 10000);

      while (pageManager.hasNextPage()) {
        List<MgrDicKeyword> dics = mgrDicKeywordMapper.getDicKeywordBean(new MybatisBean(pageManager.getFirst(), pageManager.getMax()));

        List<SolrInputDocument> doces = new ArrayList<SolrInputDocument>();
        if (dics != null && dics.size() > 0) {
          for (MgrDicKeyword word : dics) {
            SolrInputDocument d = createDocument(word, dataVersion);
            if (d != null) {
              doces.add(d);
              counter++;
            }
          }
          if (doces.size() > 0) {
            cloudKeywordDicSearchSolrClient.add(SolrCollectionNameDefineBean.INDKEY, doces);
          }
          if (counter % 50000 == 0) {
            cloudKeywordDicSearchSolrClient.commit(SolrCollectionNameDefineBean.INDKEY);
          }
        }
      }
      cloudKeywordDicSearchSolrClient.deleteByQuery(SolrCollectionNameDefineBean.INDKEY,
          FieldUtil.DATA_VERSION + ": [0 TO " + (dataVersion - 1) + "]");
      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      if (isGetStart) {
        try {
          cloudKeywordDicSearchSolrClient.commit(SolrCollectionNameDefineBean.INDKEY);
          cloudKeywordDicSearchSolrClient.optimize(SolrCollectionNameDefineBean.INDKEY);
        } catch (SolrServerException e) {
          log.error(e.getMessage(), e);
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
        JobLockHandler.closeLock(KEYWORD_LOCK);
      }
    }
    return false;
  }

  private SolrInputDocument createDocument(MgrDicKeyword w, long dateVersion) {
    SolrInputDocument doc = new SolrInputDocument();
    try {
      if (w == null || StringUtils.isBlank(w.getWord())) {
        return null;
      }

      doc.setField(FieldUtil.KEYWORD_DIC.KEY, w.getWord(), 1.0f);
      doc.setField(FieldUtil.KEYWORD_DIC.KEY_NOT, w.getWord(), 1.0f);
      doc.setField(FieldUtil.KEYWORD_DIC.WEIGHING, w.getWeight(), 1.0f);
      doc.setField(FieldUtil.DATA_VERSION, dateVersion);
      return doc;
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Override
  public String alsoLike(String word, int num, int len) {

    String resultString = "[]";
    if (StringUtils.isBlank(word)) {
      return resultString;
    }
    List<String> result = new ArrayList<String>(0);

    String redisKey = word + "__" + num + "__" + len;
    try {

      resultString = redisCacheService.readFromRedis(redisKey);
      if (StringUtils.isNotBlank(resultString)) {
        return resultString;
      }

      SolrDocumentList rs = null;
      // WordAnalyzer
      WordBean wordBean = AutokeyConvernt.findQueryWordBean(word);
      word = wordBean.getWordAsString();

      // redis缓存读取
      num = num > 0 && num < 30 ? num : 7;

      // 根据输入关键字和附加的查询关键字，搜索出相应的行业词汇，然后找出包含这些行业词的热门词
      // 获取WordAnalyzer的附加关键字
      WordAnalyzer analyzer = new WordAnalyzer(word);
      analyzer.setDebug(false);
      analyzer.initDevices();
      List<String> querys = analyzer.queryWords();

      // 根据输入关键字和附加的查询关键字，搜索出相应的行业词汇，然后找出包含这些行业词的热门词（获取关键字中包含的行业词）
      List<String> tokens = productSearchService.analyzeWord(word);
      tokens.addAll(AnalyzerDevice.getInstance().analyze(word));
      List<MgrDicKeyword> dicKeywords = getMgrDicKeywords(word, tokens);

      String hotkeyQuery = getQuery(dicKeywords, tokens, querys, analyzer);

      // 根据响应行业词、原始关键字，查询热门关键字
      SolrQuery q1 = new SolrQuery();

      q1.setSort(new SortClause(FieldUtil.AUTOKEY.SORT, ORDER.desc));

      q1.setQuery(hotkeyQuery);
      // 各个词的权重信息
      Map<String, Integer> weighs = getWeighInfo(dicKeywords, tokens, analyzer);
      q1.setRows(100);
      // 获取搜索词
      Set<String> ks = weighs.keySet();
      // 根据分词结果以及行业词信息，获取热门词
      rs = cloudAutokeySearchSolrClient.query(SolrCollectionNameDefineBean.AUTOKEY, q1)
          .getResults();

      // 去重复、计算长度
      // 遍历上一步取得的所有热门词，进行一些简单的过滤、排序
      List<MgrDicKeyword> alsos = new ArrayList<MgrDicKeyword>(0);
      for (SolrDocument d : rs) {
        String key = d.get(FieldUtil.AUTOKEY.SHOW).toString().toLowerCase();

        int w = 0;
        // 过滤
        if (KeywordFilter.filterAlsoLike(word, key)) {
          continue;
        }

        // 统计各个热门词汇中包含的行业词汇的权重总和
        for (String k : ks) {
          if (key.indexOf(k) > -1) {// 累加权重
            w += weighs.get(k);
          }
        }
        MgrDicKeyword ind = new MgrDicKeyword();
        ind.setWord(key);
        ind.setWeight(w);
        alsos.add(ind);
      }
      SameWordFilter sameWordFilter = new SameWordFilter(alsos);

      List<MgrDicKeyword> inds = sameWordFilter.filter();
      analyzer.calculate(inds);
      int count = 0;

      // 原始关键字的编码
      String oldKeyCode = AutokeyConvernt.wordCode(word);

      int currentLen = 0;
      for (MgrDicKeyword ind : inds) {
        String dicWord = ind.getWord();
        // 待推荐关键字的编码
        String wordCode = AutokeyConvernt.wordCode(dicWord);

        // 相同或包含的关键字，过滤掉
        if (oldKeyCode.endsWith(wordCode) || oldKeyCode.indexOf(wordCode) > -1) {
          continue;
        }

        // 长度限制，防止在html页面上超出宽度
        int tempLen = StringUtil.lengthDBC(dicWord);
        if (currentLen + tempLen > len) {
          break;
        }

        currentLen += tempLen;

        result.add(dicWord);
        count++;
        if (count >= num) {
          break;
        }
      }

      resultString = JSON.toJSONString(result);
      if (result.size() > 0) {
        redisCacheService.saveToRedis(redisKey, resultString);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return resultString;
  }

  @Override
  public List<String> rebuildKeyword(String word, int limit) {
    try {
      word = AutokeyConvernt.spliteWord(word);
      WordBean qwb = AutokeyConvernt.findQueryWordBean(word);
      word = qwb.getWordAsString();

      List<String> tokens = productSearchService.analyzeWord(word);

      List<MgrDicKeyword> dics = getMgrDicKeywords(word, tokens);
      List<KeywordBean> rs = toKeywordBean(dics);

      int num = dics.size();
      if (num < 1) {
        String[] ws = word.split(" ");
        if (ws.length > 1) {
          for (int i = 0; i < ws.length; i++) {
            String w = ws[i];
            if (!w.trim().isEmpty()) {
              rs.add(new KeywordBean(w, 1, 5));
            }
          }
        }
      }

      if (rs.size() < 1) {
        Set<String> filter = new HashSet<String>();
        for (String t : tokens) {
          if (t == null) {
            continue;
          }
          if (!filter.contains(t)) {
            filter.add(t);
            rs.add(new KeywordBean(t, 1, 5));
          }
        }
      }

      num = rs.size();
      num = num > 5 ? 5 : num;
      // 将取得的最多5个行业词汇进行组合算法，得到新的关键词列表
      List<List<KeywordBean>> newWordsList = Assemble.get(rs.subList(0, num));
      Collections.sort(newWordsList, new Comparator<List<KeywordBean>>() {

        @Override
        public int compare(List<KeywordBean> o1, List<KeywordBean> o2) {
          return Assemble.getWeigh(o2) - Assemble.getWeigh(o1);
        }
      });
      // 按照行业词的权重进行重排，然后将多个行业词用空格合并成新的关键字
      List<String> another = anotherWord(newWordsList, qwb.getNotWords());

      return another;
    } catch (Exception e) {

    }
    return new ArrayList<String>(0);
  }

  // 计算重组关键字
  private List<String> anotherWord(List<List<KeywordBean>> inds, List<String> not) {
    String notw = "";
    for (String n : not) {
      notw += " -" + n + "";
    }
    List<String> anothers = new ArrayList<String>();
    Set<String> filter = new HashSet<String>();
    for (List<KeywordBean> r : inds) {
      String w = StringUtil.replaceSameWord(join(r) + notw);
      if (filter.contains(w) || (w.trim().length() == 1)) {
        continue;
      }
      filter.add(w);
      anothers.add(w);
    }
    return anothers;
  }

  /**
   * 合并
   * 
   * @param inds
   * @return
   */
  private String join(List<KeywordBean> inds) {
    StringBuilder sb = new StringBuilder("");
    for (int i = 0, len = inds.size(); i < len; i++) {
      sb.append(inds.get(i).getWord());
      if (i < len - 1) {
        sb.append(" ");
      }
    }
    return sb.toString();
  }

  private List<KeywordBean> toKeywordBean(List<MgrDicKeyword> dics) {
    List<KeywordBean> kbs = new ArrayList<KeywordBean>();
    for (MgrDicKeyword mdk : dics) {
      kbs.add(mdk.convert());
    }
    return kbs;
  }

  /**
   * 根据word查找dicKeyword
   * 
   * @param word
   * @return
   */
  private List<MgrDicKeyword> getMgrDicKeywords(String word, List<String> analyzes) {
    List<MgrDicKeyword> inds = new ArrayList<MgrDicKeyword>();

    try {
      List<String> tokens = null;
      tokens = AutokeyConvernt.pickString(word);
      if (tokens.size() > 0) { // 如果关键字中含有六位码，则提取
        for (String t : tokens) {
          inds.add(new MgrDicKeyword(t, 1, 5));
        }
        return inds;
      }
      if (StringUtil.isNumberOrLetters(word)) {
        tokens = new ArrayList<String>();
      } else {
        tokens = analyzes;
      }

      int anaLeng = tokens.size();
      SolrQueryObj obj = new SolrQueryObj();

      Set<String> filter = new HashSet<String>();
      for (String s : tokens) {
        String newS = s;
        if (s == null || filter.contains(s)) {
          continue;
        }
        filter.add(s);
        if (StringUtil.isNumberAndLetter(s)) {
          newS = s.toUpperCase() + "*";
        }
        obj.pushOr(FieldUtil.KEYWORD_DIC.KEY_NOT, newS);
      }

      SolrQuery query = new SolrQuery(obj.toString());
      SolrDocumentList rs = cloudKeywordDicSearchSolrClient.query(
          SolrCollectionNameDefineBean.INDKEY, query).getResults();
      Set<String> exists = new HashSet<String>();
      for (int i = 0; i < rs.size(); i++) {
        SolrDocument d = rs.get(i);
        String w = d.get(FieldUtil.KEYWORD_DIC.KEY).toString();

        int weigh = Integer.parseInt(d.get(FieldUtil.KEYWORD_DIC.WEIGHING).toString());
        int weighFinal = weigh > 0 ? weigh * 5 : 1;
        MgrDicKeyword ind = new MgrDicKeyword();
        ind.setWeight(weighFinal);
        ind.setWord(w);
        inds.add(ind);
        exists.add(w);
      }

      if (((float) inds.size() / (float) anaLeng) < 0.7) {
        for (String k : tokens) {
          k = k.trim();
          if (!exists.contains(k)) {
            MgrDicKeyword ind = new MgrDicKeyword();
            ind.setWeight(1);
            ind.setWord(k);
            inds.add(ind);
            exists.add(k);
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return inds;
  }

  /**
   * 获得query语句
   * 
   * @param rs
   * @param tokens
   * @return
   */
  private String getQuery(List<MgrDicKeyword> rs, List<String> tokens, List<String> filterWords, WordAnalyzer an) {
    try {
      SolrQueryObj solrQueryObj = new SolrQueryObj();
      // 当关键字属于分类名称的时候，权重设置为500
      // 行业词
      for (int i = 0; i < rs.size(); i++) {
        MgrDicKeyword d = rs.get(i);
        String w = d.getWord();
        int weigh = d.getWeight();
        int weighFinal = weigh > 0 ? weigh : 1;
        weighFinal *= getWeightByWord(w, an, 70);
        solrQueryObj.pushOr(FieldUtil.AUTOKEY.ANALYZE, w + "^" + weighFinal);
      }
      // 分词
      for (int i = 0; i < tokens.size(); i++) {
        String d = tokens.get(i);
        int w = getWeightByWord(d, an, 20);
        solrQueryObj.pushOr(FieldUtil.AUTOKEY.ANALYZE, d + "^" + w);
      }
      // 附加查询词
      for (int i = 0; i < filterWords.size(); i++) {
        String d = filterWords.get(i);
        int w = getWeightByWord(d, an, 5);
        solrQueryObj.pushOr(FieldUtil.AUTOKEY.ANALYZE, d + "^" + w);
      }
      String qS = "(" + solrQueryObj.toString() + ")";
      return qS;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return "";
  }

  /**
   * 获取行业词、分词结果词汇的信息（包括权重）
   * 
   * @param rs
   * @param tokens
   * @return
   */
  private Map<String, Integer> getWeighInfo(List<MgrDicKeyword> wordInds, List<String> tokens, WordAnalyzer an) {
    Map<String, Integer> weighs = new HashMap<String, Integer>();
    try {
      // 统计行业词的权重信息
      for (int i = 0; i < wordInds.size(); i++) {
        MgrDicKeyword d = wordInds.get(i);
        String w = d.getWord();
        int weigh = getKeywordWeight(w, an, 1);
        weigh = weigh > 0 ? weigh : 1;
        weighs.put(w, weigh);
      }
      // 统计分词结果的权重信息
      for (int i = 0; i < tokens.size(); i++) {
        String d = tokens.get(i);
        Integer w = getKeywordWeight(d, an, 1);
        if (w == null) {
          weighs.put(d, 1);
        } else {
          weighs.put(d, w + 1);
        }
      }
    } catch (Exception e) {

    }
    return weighs;
  }

  private int getKeywordWeight(String word, WordAnalyzer an, int dft) {
    int w = 0;
    for (IWeightingDevice device : an.getDevices()) {
      if (device.contain(word) > 0) {
        w += device.getKeyWeight();
      }
    }
    if (w > 0) {
      return w;
    }
    return dft;
  }

  /**
   * 根据关键字，返回重要性数值。 判断关键字是否属于分类名称
   * 
   * @param word
   *          关键字
   * @param v
   *          如果是关键字，返回权重值
   * @param dft
   *          如果不是关键字，返回默认值
   * @return
   */
  private int getWeightByWord(String word, WordAnalyzer a, int dft) {
    int w = 0;
    for (IWeightingDevice device : a.getDevices()) {
      if (device.contain(word) > 0) {
        w += device.getQueryWeight();
      }
    }
    if (w > 0) {
      return w;
    }
    return dft;
  }

  /*
   * @Resource(name = "lbKeywordDicSearchSolrServer") private LBHttpSolrClient
   * keywordDicSearchSolrServer;
   * 
   * @Resource(name = "lbKeywordDicIndexSolrServer") private LBHttpSolrClient
   * keywordDicIndexSolrServer;
   */
  @Resource(name = "productSearchService")
  private ProductSearchService productSearchService;
  /*
   * @Resource(name = "lbAutokeySearchSolrServer") private LBHttpSolrClient autokeySearchSolrServer;
   */

  @Resource(name = "cloudKeywordDicSearchSolrClient")
  private CloudSolrClient cloudKeywordDicSearchSolrClient;

  @Resource(name = "cloudAutokeySearchSolrClient")
  private CloudSolrClient cloudAutokeySearchSolrClient;

  @Autowired
  private MgrDicKeywordMapper mgrDicKeywordMapper;

  @Resource(name = "redisCacheService")
  private RedisCacheService redisCacheService;
  private static Logger log = LoggerFactory.getLogger(KeywordDicServiceImpl.class);
}
