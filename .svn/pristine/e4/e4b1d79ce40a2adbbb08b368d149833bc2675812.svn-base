/*
 * 2014-12-2 下午5:18:18 吴健 HQ01U8435
 */

package com.mbgo.search.core.filter.attr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mbgo.search.core.filter.itf.FilterBeanContainer;
import com.mbgo.search.core.quartz.AuxiliaryDataRefresher;

public class AttributeCaculatorManager {

  // private Map<String, String> attrKeyNameMapper = new HashMap<String,
  // String>();
  private Map<String, AttributeCaculator> caculatorMap = new HashMap<String, AttributeCaculator>();

  // public AttributeCaculatorManager(Map<String, String> attrKeyNameMapper) {
  // super();
  // this.attrKeyNameMapper = attrKeyNameMapper;
  // }
  public void addBean(String attrKey, String valueCode, String valueName,
      long count) {
    AttributeCaculator ac = caculatorMap.get(attrKey);
    if (ac == null) {
      ac = new AttributeCaculator();
      caculatorMap.put(attrKey, ac);
    }
    ac.addAttrBean(valueCode, valueName, count);
  }

  private List<String> attrList = null;

  public boolean hasNext() {
    if (attrList == null) {
      Set<String> temp = caculatorMap.keySet();
      attrList = new ArrayList<String>();
      for (String k : temp) {
        attrList.add(k);
      }
    }
    return attrList.size() > 0;
  }

  public FilterBeanContainer next() {
    FilterBeanContainer result = null;
    String key = attrList.remove(0);
    // 筛选列表按先后顺序分别为：品牌、价格、性别97、尺码2、季节85、场合3、系列86、版型10、面料/材质6、颜色1、功能8
    switch (key) {
    case "97":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),3);
      break;
    case "85":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),5);
      break;
    case "3":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),6);
      break;
    case "86":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),7);
      break;
    case "10":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),8);
      break;
    case "6":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),9);
      break;
    case "8":
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),11);
      break;

    default:
      result = FilterBeanContainer.createFilter(key,
          AuxiliaryDataRefresher.getAttributeKeyNameByCode(key),
          caculatorMap.get(key),12);
      break;
    }
    return result;
  }
}
