/*
 * 2014-12-8 上午10:17:51 吴健 HQ01U8435
 */

package com.mbgo.search.core.quartz;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.core.service.AutoKeyService;
import com.mbgo.search.core.service.KeywordDicService;
import com.mbgo.search.core.service.ProductIndexService;
import com.mbgo.search.core.service.SpellCheckService;
import com.mbgo.search.core.tools.LastIntervalRegister;

public class RebuildProductIndexQuartz {

  private static Logger log = LoggerFactory.getLogger(RebuildProductIndexQuartz.class);

  public void rebuildIndex() {
    try {
      int status = lastIntervalRegister.getRebuildAndUpdateSwitchStatusFromRedis();
      log.debug("rebuildSwitch:" + status);
      if (status == 1) {
        productIndexService.rebuildProductIndex();
      }

    } catch (Exception e) {

    }
  }

  public void rebuildKeywordIndex() {
    try {
      keywordDicService.rebuildDicIndex();

      // spellCheckService.rebuildIndex();

      autoKeyService.rebuildAutokeyIndex();
    } catch (Exception e) {

    }
  }

  public void updateIndex() {
    try {
      int status = lastIntervalRegister.getRebuildAndUpdateSwitchStatusFromRedis();
      log.debug("updateSwitch:" + status);
      if (status == 1) {
        productIndexService.updateProductIndex();
      }
    } catch (Exception e) {

    }
  }

  @Resource(name = "productIndexService")
  private ProductIndexService productIndexService;

  @Resource(name = "autoKeyService")
  private AutoKeyService autoKeyService;
  @Resource(name = "keywordDicService")
  private KeywordDicService keywordDicService;
  @Resource(name = "spellCheckService")
  private SpellCheckService spellCheckService;
  @Resource(name = "lastIntervalRegister")
  private LastIntervalRegister lastIntervalRegister;

}
