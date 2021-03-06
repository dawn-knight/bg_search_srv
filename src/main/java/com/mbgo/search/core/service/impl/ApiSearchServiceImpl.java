/*
 * 2014-9-24 下午4:05:34 吴健 HQ01U8435
 */

package com.mbgo.search.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.mbstore.mapper.MallSecondGoodsMapper;
import com.mbgo.search.core.bean.query.ParameterNewlyProductBean;
import com.mbgo.search.core.bean.query.ProductQuery;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.bean.query.ReturnNewlyProductBean;
import com.mbgo.search.core.bean.update.UpdateOption;
import com.mbgo.search.core.filter.FilterData;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.ProductIndexService;
import com.mbgo.search.core.service.ProductSearchService;
import com.mbgo.search.core.service.SearchService;
import com.mbgo.search.core.tools.queue.KeywordLogQueueManager;
import com.metersbonwe.promotion.api.PlatformPromotionApi;

@Service("apiSearchService")
public class ApiSearchServiceImpl implements SearchService {

  @Override
  public ProductQueryResult search(ProductQuery query) {
    ProductQueryResult result = null;
    try {
      result = searchProduct(query);
      String srcWord = query.getWord();
      // 记录关键字以及查询结果
      long total = result.getTotal();
      if (total < 1 && query.hasWord()) {
        String word = query.getWord();
        List<String> sameWord = AuxiliaryDataRefresher.getSynonyms(word);
        if (sameWord != null && sameWord.size() > 0) {
          int num = sameWord.size();
          num = num > 5 ? 5 : num;
          for (int i = 0; i < num; i++) {
            if (!word.equals(sameWord.get(i))) {
              query.setWord(sameWord.get(i));
              query.init();
              result = searchProduct(query);
              if (result.getTotal() > 0) {
                result.setNewWord(sameWord.get(i));
                break;
              }
            }
          }
        }
      }
      // if (StringUtils.isNotBlank(srcWord)) {
      // keywordLogQueueManager.addLogWord(srcWord, total);
      // }

      // 需要关键字重组的情况
      if (total < 1 && query.hasWord()) {
        List<String> another = keywordDicService.rebuildKeyword(query.getWord(), 5);
        // 遍历所有新获得的关键字进行搜索
        for (String newWord : another) {

          // 使用新的关键字进行转换前，需要进过关键字转换这一步
          query.setWord(newWord);
          query.init();

          result = searchProduct(query);

          // 如果能找到新的关键字，则设置
          if (result.getTotal() > 0) {
            result.setNewWord(newWord);
            break;
          }
        }
      }

      result.setSrcWord(srcWord);
      return result;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  /**
   * 搜索，根据searchType，确定是product还是colorProduct
   * 
   * @param query
   * @return
   */
  private ProductQueryResult searchProduct(ProductQuery query) {
    ProductQueryResult result = new ProductQueryResult();
    try {
      if (query.getSearchType() == 1) {
        // productSearch
        result = productSearchService.search(query);
      } else {
        // colorProductSearch
        // result = colorProductSearchService.search(query);
        result = productSearchService.searchColor(query);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  public FilterData searchFilterData(ProductQuery query) {
    FilterData filterData = null;
    try {
      filterData = searchFilterDataBySearchType(query);
      String srcWord = query.getWord();

      // 需要关键字重组的情况
//      if (filterData != null && filterData.getTotal() < 1 && query.hasWord()) {
//        List<String> another = keywordDicService.rebuildKeyword(query.getWord(), 5);
//        // 遍历所有新获得的关键字进行搜索
//        for (String newWord : another) {
//
//          // 使用新的关键字进行转换前，需要进过关键字转换这一步
//          query.setWord(newWord);
//          query.init();
//
//          filterData = searchFilterDataBySearchType(query);
//
//          // 如果能找到新的关键字，则设置
//          if (filterData.getTotal() > 0) {
//            filterData.setNewWord(newWord);
//            break;
//          }
//        }
//      }
      if (filterData != null) {
        filterData.setSrcWord(srcWord);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return filterData;
  }

  /**
   * 搜索，根据searchType，确定是product还是colorProduct
   * 
   * @param query
   * @return
   */
  private FilterData searchFilterDataBySearchType(ProductQuery query) {
    FilterData fd = null;

    try {
      if (query.getSearchType() == 1) {
        // productSearch
        fd = productSearchService.searchFilterData(query);
      } else {
        // colorProductSearch
        // fd = colorProductSearchService.searchFilterData(query);
      }
      if (fd != null) {
        fd.setBrand(query.getConvertResult().getBrand());
        fd.setColor(query.getConvertResult().getColor());
        fd.setStore(query.getConvertResult().getShop());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return fd;
  }

  @Override
  public int updateProductIndex(String pid, String type) {

    boolean isOk = false;
    try {
      List<String> list = new ArrayList<String>();
      list.add(pid);
      if (type.equalsIgnoreCase("update")) {
        List<UpdateOption> uors = productIndexService.updateProductIndex(list);

        isOk = uors.size() < 0 ? false : uors.get(0).isOk();
      } else if (type.equalsIgnoreCase("delete")) {
        isOk = productIndexService.deleteProductIndex(list);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return isOk ? 1 : 0;
  }

  @Override
  public List<UpdateOption> updateProductIndexByList(List<UpdateOption> uos) {
    List<UpdateOption> result = new ArrayList<UpdateOption>();
    try {
      if (uos == null || uos.size() < 1) {
        return result;
      }
      List<String> update = new ArrayList<String>();
      List<String> delete = new ArrayList<String>();

      for (UpdateOption o : uos) {
        if (o.isUpdate()) {
          update.add(o.getProductId());
        } else if (o.isDelete()) {
          delete.add(o.getProductId());
        }
      }

      if (update.size() > 0) {
        result = productIndexService.updateProductIndex(update);
      }
      if (delete.size() > 0) {
        boolean deleteRs = productIndexService.deleteProductIndex(delete);
        UpdateOption dr = UpdateOption.delete("0000000");
        dr.setOk(deleteRs);
        result.add(dr);
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  public boolean updateProductTags(List<String> pids) {
    try {
      productIndexService.updateProductTags(pids);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  private static Logger log = LoggerFactory.getLogger(ApiSearchServiceImpl.class);

  @Resource(name = "productSearchService")
  private ProductSearchService productSearchService;

  // @Resource(name = "colorProductSearchService")
  // private ColorProductSearchService colorProductSearchService;

  @Resource(name = "keywordDicService")
  private KeywordDicService keywordDicService;

  @Resource(name = "productIndexService")
  private ProductIndexService productIndexService;

  @Resource(name = "keywordLogQueueManager")
  private KeywordLogQueueManager keywordLogQueueManager;

  @Autowired
  private MallSecondGoodsMapper mallSecondGoodsMapper;

  @Autowired
  private PlatformPromotionApi promotionService;

  @Override
  public ReturnNewlyProductBean searchNewlyProduct(ParameterNewlyProductBean p) {
    ReturnNewlyProductBean result = new ReturnNewlyProductBean();
    try {
      result = productSearchService.searchNewlyProduct(p);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }
}
