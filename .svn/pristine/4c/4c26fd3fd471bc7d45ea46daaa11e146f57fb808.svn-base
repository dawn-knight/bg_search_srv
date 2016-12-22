package com.mbgo.search.core.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.mbgo.mybatis.mborg.mapper.ChannelInfoMapper;
import com.mbgo.mybatis.mborg.model.ChannelInfoBean;
import com.mbgo.mybatis.mbsearch.bean.MgrGoodsTags;
import com.mbgo.mybatis.mbsearch.mapper.MgrGoodsTagsMapper;
import com.mbgo.mybatis.mbsearch.mapper.SynonymMapper;
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.common.SolrCollectionNameDefineBean;
import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.constant.Symbol;
import com.mbgo.search.core.bean.index.SizeInfo;
import com.mbgo.search.core.filter.brand.BrandBean;
import com.mbgo.search.core.filter.category.Category;
import com.mbgo.search.core.filter.category.CategoryCrumbs;
import com.mbgo.search.core.filter.category.Crumbs;
import com.mbgo.search.core.filter.category.OneCategory;
import com.mbgo.search.core.tools.FieldUtil;
import com.metersbonwe.pcs.bean.CatIdCatNameLevelMapping;
import com.metersbonwe.pcs.bean.CatIdCatNameMapping;
import com.metersbonwe.pcs.bean.CatIdParentIdMapping;
import com.metersbonwe.pcs.bean.CatIdSubCatIdMapping;
import com.metersbonwe.pcs.bean.ChannelGoodsMapping;
import com.metersbonwe.pcs.dao.ChannelGoodsMapper;
import com.metersbonwe.pcs.dao.ExtendedProductLibBrandMapper;
import com.metersbonwe.pcs.dao.ExtendedProductLibCategoryMapper;
import com.metersbonwe.pcs.dao.ProductLibAttributeMapper;
import com.metersbonwe.pcs.dao.SkuLevelProductInfoMapper;

public class AuxiliaryDataRefresher {

  static private Logger log = LoggerFactory
      .getLogger(AuxiliaryDataRefresher.class);

  static private String channelCode = ChannelConst.DEFAULT_BANGGO_CHANNEL_CODE;
  static private String attributeKeyIdOfGender = "-1";

  /*
   * @Resource(name = "lbIndexGoodsSolrServer") private LBHttpSolrClient server;
   */

  @Resource(name = "cloudSearchGoodsSolrClient")
  private CloudSolrClient cloudSearchGoodsSolrClient;
  @Autowired
  private SkuLevelProductInfoMapper skuLevelProductInfoMapper;
  @Autowired
  private ChannelInfoMapper channelInfoMapper;
  @Autowired
  private MallThemeGoodsMapper exCatMapper;
  @Autowired
  private ExtendedProductLibCategoryMapper basicCatMapper;

  @Autowired
  private ExtendedProductLibBrandMapper productBrandMapper;

  @Autowired
  private ProductLibAttributeMapper attributeMapper;
  @Autowired
  private SynonymMapper synonymMapper;
  @Autowired
  private ChannelGoodsMapper channelGoodsMapper;
  @Autowired
  private MgrGoodsTagsMapper goodsTagsMapper;
  static private Map<String, SizeInfo> sizeInfoMap = new HashMap<String, SizeInfo>();
  static private Map<String, String> sizeMap = new HashMap<String, String>();
  static private Map<String, String> sizeSeriesMap = new HashMap<String, String>();
  static private Map<String, String> brandMap = new HashMap<String, String>();
  static private Map<String, String> colorSystemMap = new HashMap<String, String>();
  static private Map<String, String> attributeKeyMap = new HashMap<String, String>();
  static private Map<String, String> attributeValueMap = new HashMap<String, String>();
  static private Map<String, String> attributeSeriesValueMap = new HashMap<String, String>();
  static private Map<String, Set<Category>> basicCatId2ExCatSetMap = new HashMap<String, Set<Category>>();
  static private Map<Category, Set<Category>> exCat2DirectSubExCatsMapping = new HashMap<Category, Set<Category>>();
  static private List<CatIdParentIdMapping> catIdParentIdMapping = new ArrayList<CatIdParentIdMapping>();
  static private List<CatIdCatNameMapping> catIdCatNameMapping = new ArrayList<CatIdCatNameMapping>();

  static private Map<Integer, Integer> catIdCatLevelMapping = new HashMap<Integer, Integer>();

  static private Map<String, List<String>> synonymMapping = new HashMap<String, List<String>>();
  static private List<CatIdSubCatIdMapping> cIdSubCidMappingList = new ArrayList<CatIdSubCatIdMapping>();
  static private List<CatIdCatNameLevelMapping> catIdCatNameLevelMapping = new ArrayList<CatIdCatNameLevelMapping>();
  static private Map<String, ChannelGoodsMapping> saleCountMapping = new HashMap<String, ChannelGoodsMapping>();
  static private Map<String, String> mgrGoodsMapping = new HashMap<String, String>();
  static private QueryResponse queryResponse;
  static private List<BrandBean> allBrand = new ArrayList<BrandBean>();
  @Value("#{channelConfig['channel_to_serve']}")
  private String idOfChannelToServeStr = "1";

  public void refresh() {
    // 尺码系映射
    try {
      List<SizeInfo> sizeInfoList = skuLevelProductInfoMapper.getSizeSeriesInfo();
      if (sizeInfoList != null && sizeInfoList.size() > 0) {
        sizeInfoMap.clear();
        for (SizeInfo sizeInfo : sizeInfoList) {
          if (sizeInfo != null && StringUtils.isNotBlank(sizeInfo.getSizeCode())) {
            sizeInfoMap.put(sizeInfo.getSizeCode(), sizeInfo);
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    SolrQuery query = new SolrQuery();
    query.setFacet(true);
    // query.setFacetLimit(10000);
    query.setFacetLimit(-1);
    query.addFacetField(FieldUtil.SIZE_MAPPING, FieldUtil.SIZE_SERIES_MAPPING, FieldUtil.BRAND_MAPPING,
        FieldUtil.COLOR_SYSTEM_MAPPING,
        FieldUtil.ATTRIBUTE_KEY_CODE_NAME_MAPPING,
        FieldUtil.ATTRIBUTE_VALUE_CODE_NAME_MAPPING, FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_NAME_MAPPING);
    query.setStart(0);
    query.setRows(0);
    try {
      QueryResponse response = cloudSearchGoodsSolrClient.query(SolrCollectionNameDefineBean.GOODS,
          query);
      // log.debug(response.toString());
      if (response != null && response.getFacetFields() != null) {
        for (FacetField field : response.getFacetFields()) {

          if (field.getName().equals(FieldUtil.SIZE_MAPPING)) {
            sizeMap.clear();
            for (Count count : field.getValues()) {
              String sizeMapping = count.getName();
              if (sizeMapping != null && !sizeMapping.equals("")) {
                String[] sizeCodeSizeName = sizeMapping
                    .split(Symbol.SIZECODE_CONNECTOR_SIZENAME);
                if (sizeCodeSizeName != null
                    && sizeCodeSizeName.length == 2) {
                  sizeMap.put(sizeCodeSizeName[0],
                      sizeCodeSizeName[1]);
                }
              }
            }
            // log.debug(sizeMap.toString());
          }
          // 尺码系
          if (field.getName().equals(FieldUtil.SIZE_SERIES_MAPPING)) {
            sizeSeriesMap.clear();
            for (Count count : field.getValues()) {
              String sizeSeriesMapping = count.getName();
              if (sizeSeriesMapping != null && !sizeSeriesMapping.equals("")) {
                String[] sizeSeriesCodeSizeName = sizeSeriesMapping
                    .split(Symbol.SIZECODE_CONNECTOR_SIZENAME);
                if (sizeSeriesCodeSizeName != null
                    && sizeSeriesCodeSizeName.length == 2) {
                  sizeSeriesMap.put(sizeSeriesCodeSizeName[0],
                      sizeSeriesCodeSizeName[1]);
                }
              }
            }
            // log.debug(sizeSeriesMap.toString());
          }
          if (field.getName().equals(FieldUtil.BRAND_MAPPING)) {
            brandMap.clear();
            for (Count count : field.getValues()) {
              String brandMapping = count.getName();
              if (brandMapping != null
                  && !brandMapping.equals("")) {
                String[] brandCodeBrandName = brandMapping
                    .split(Symbol.BRANDCODE_CONNECTOR_BRANDNAME);
                if (brandCodeBrandName != null
                    && brandCodeBrandName.length == 2) {
                  brandMap.put(brandCodeBrandName[0],
                      brandCodeBrandName[1]);
                }
              }
            }
            // log.debug(brandMap.toString());
          }

          if (field.getName().equals(FieldUtil.COLOR_SYSTEM_MAPPING)) {
            colorSystemMap.clear();
            for (Count count : field.getValues()) {
              String colorSystemMapping = count.getName();
              if (colorSystemMapping != null
                  && !colorSystemMapping.equals("")) {
                String[] csCodeCsName = colorSystemMapping
                    .split(Symbol.COLORSYSTEMCODE_CONNECTOR_COLORSYSTEMNAME);
                if (csCodeCsName != null
                    && csCodeCsName.length == 2) {
                  colorSystemMap.put(csCodeCsName[0],
                      csCodeCsName[1]);
                }
              }
            }
            // log.debug(colorSystemMap.toString());
          }

          if (field.getName().equals(
              FieldUtil.ATTRIBUTE_KEY_CODE_NAME_MAPPING)) {
            attributeKeyMap.clear();
            for (Count count : field.getValues()) {
              String attribueKeyMapping = count.getName();
              if (attribueKeyMapping != null
                  && !attribueKeyMapping.equals("")) {
                String[] aKeyCodeAkeyName = attribueKeyMapping
                    .split(Symbol.AKEYCODE_CONNECTOR_AKEYNAME);
                if (aKeyCodeAkeyName != null
                    && aKeyCodeAkeyName.length == 2) {
                  attributeKeyMap.put(aKeyCodeAkeyName[0],
                      aKeyCodeAkeyName[1]);
                }
              }
            }
            // log.debug(attributeKeyMap.toString());
          }

          if (field.getName().equals(
              FieldUtil.ATTRIBUTE_VALUE_CODE_NAME_MAPPING)) {
            attributeValueMap.clear();
            for (Count count : field.getValues()) {
              String attributeValueMapping = count.getName();
              if (attributeValueMapping != null
                  && !attributeValueMapping.equals("")) {
                String[] aValueCodeAvalueName = attributeValueMapping
                    .split(Symbol.AVALCODE_CONNECTOR_AVALNAME);
                if (aValueCodeAvalueName != null
                    && aValueCodeAvalueName.length == 2) {
                  attributeValueMap.put(
                      aValueCodeAvalueName[0],
                      aValueCodeAvalueName[1]);
                }
              }
            }
            // log.debug(attributeValueMap.toString());
          }
          //
          if (field.getName().equals(
              FieldUtil.ATTRIBUTE_SERIES_VALUE_CODE_NAME_MAPPING)) {
            attributeSeriesValueMap.clear();
            for (Count count : field.getValues()) {
              String attributeSeriesValueMapping = count.getName();
              if (attributeSeriesValueMapping != null
                  && !attributeSeriesValueMapping.equals("")) {
                String[] aValueSeriesCodeAvalueSeriesName = attributeSeriesValueMapping
                    .split(Symbol.AVALCODE_CONNECTOR_AVALNAME);
                if (aValueSeriesCodeAvalueSeriesName != null
                    && aValueSeriesCodeAvalueSeriesName.length == 2) {
                  attributeSeriesValueMap.put(
                      aValueSeriesCodeAvalueSeriesName[0],
                      aValueSeriesCodeAvalueSeriesName[1]);
                }
              }
            }
            // log.debug(attributeSeriesValueMap.toString());
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void initChannelInfo() {
    try {
      Integer idOfChannelToServe = new Integer(1);
      idOfChannelToServe = Integer.valueOf(idOfChannelToServeStr);
      log.debug("id of channel to serve [{}]", idOfChannelToServe);
      if (idOfChannelToServe.equals(ChannelConst.SERVING_ALL_CHANNEL)) {
        channelCode = ChannelConst.CODE_OF_ALL_CHANNEL;
      } else {
        List<ChannelInfoBean> channelInfoList = channelInfoMapper
            .getChannelList();
        if (channelInfoList != null && channelInfoList.size() > 0) {
          for (ChannelInfoBean ci : channelInfoList) {
            if (ci.getChannelId().equals(idOfChannelToServe)) {
              channelCode = ci.getChannelCode();
              break;
            }
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.debug("channel code [{}]", channelCode);
  }

  private void populateBasicCatId2ExCatSetMap(String basicCatId, Category cat) {
    Set<Category> exCatSet = basicCatId2ExCatSetMap.get(basicCatId);
    if (exCatSet == null) {
      exCatSet = new LinkedHashSet<Category>();
    }
    exCatSet.add(cat);
    basicCatId2ExCatSetMap.put(basicCatId, exCatSet);
  }

  private void generateBasicCatIdMapping(Category cat,
      List<CatIdSubCatIdMapping> cIdSubCidMappingList) {
    String basicCatIdStr = cat.getBasicCateIds();
    if (basicCatIdStr != null && !basicCatIdStr.equals("")) {
      // log.debug("{} -> [{}]", cat.getCateId(), cat.getBasicCateIds());
      String[] basicCatIdArray = basicCatIdStr
          .split(Symbol.BASIC_CATEGORY_ID_SEPERATOR);
      if (basicCatIdArray != null && basicCatIdArray.length > 0) {
        for (String basicCatId : basicCatIdArray) {
          try {
            Integer basicCatIdInt = Integer.valueOf(basicCatId);
            populateBasicCatId2ExCatSetMap(basicCatId, cat);
            if (cIdSubCidMappingList != null) {
              for (CatIdSubCatIdMapping mapping : cIdSubCidMappingList) {
                if (mapping.getCatId().equals(basicCatId)) {
                  List<String> subCatIds = mapping
                      .getSubCatIds();
                  if (subCatIds != null) {
                    for (String subCatId : subCatIds) {
                      populateBasicCatId2ExCatSetMap(
                          subCatId, cat);
                    }
                  }
                  break;
                }
              }
            }
          } catch (NumberFormatException nfe) {

          }
        }
      }
    }
  }

  private void generateExCatMapping(Category cat, List<Category> exCatList) {
    Category parentCategory = new Category(cat.getParentId());
    if (exCatList != null && exCatList.size() > 0) {
      for (Category c : exCatList) {
        if (c.equals(parentCategory)) {
          parentCategory = c;
          break;
        }
      }
    }
    Set<Category> directSubExCats = exCat2DirectSubExCatsMapping
        .get(parentCategory);
    if (directSubExCats == null) {
      directSubExCats = new LinkedHashSet<Category>();
    }
    directSubExCats.add(cat);
    exCat2DirectSubExCatsMapping.put(parentCategory, directSubExCats);
  }

  /**
   * 虚拟目录，已废弃
   */
  public void refreshExCat() {
    try {
      List<Category> exCatList = basicCatMapper.getAllCats();
      if (exCatList != null) {
        cIdSubCidMappingList = basicCatMapper.getCatIdSubCatIdMapping();
        exCat2DirectSubExCatsMapping.clear();
        for (Category exCat : exCatList) {
          generateExCatMapping(exCat, exCatList);
        }
        for (Category exCat : exCatList) {
          if (!exCat2DirectSubExCatsMapping.keySet().contains(exCat)) {
            exCat2DirectSubExCatsMapping.put(exCat, null);
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  // public void refreshExCat() {
  // try {
  // // List<Category> exCatList = exCatMapper.getAllExCats();
  // List<Category> exCatList = basicCatMapper.getAllCats();
  // if (exCatList != null) {
  // List<CatIdSubCatIdMapping> cIdSubCidMappingList = basicCatMapper
  // .getCatIdSubCatIdMapping();
  // basicCatId2ExCatSetMap.clear();
  // exCat2DirectSubExCatsMapping.clear();
  // for (Category exCat : exCatList) {
  // generateBasicCatIdMapping(exCat, cIdSubCidMappingList);
  // generateExCatMapping(exCat, exCatList);
  // }
  // for (Category exCat : exCatList) {
  // if (!exCat2DirectSubExCatsMapping.keySet().contains(exCat)) {
  // exCat2DirectSubExCatsMapping.put(exCat, null);
  // }
  // }
  // // log.debug(basicCatId2ExCatSetMap.toString());
  // // log.debug(exCat2DirectSubExCatsMapping.toString());
  // }
  // } catch (Exception e) {
  // log.error(e.getMessage(), e);
  // }
  // }

  public void refreshCat() {
    try {
      catIdParentIdMapping = basicCatMapper.getCatIdParentIdMapping();
      // log.debug(catIdParentIdMapping.toString());
      catIdCatNameMapping = basicCatMapper.getCatIdCatNameMapping();
      catIdCatNameLevelMapping = basicCatMapper.getCatIdCatNameLevelMapping();
      // categoryId-level mapping
      for (CatIdCatNameLevelMapping cIdNameLevel : catIdCatNameLevelMapping) {
        catIdCatLevelMapping.put(Integer.parseInt(cIdNameLevel.getCatId()), cIdNameLevel.getLevel());
      }
      allBrand = productBrandMapper.getBrandList();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  static public QueryResponse getQueryResponse() {
    return queryResponse;
  }

  public void initAttributeKeyIdOfGender() {
    // try {
    // attributeKeyIdOfGender = attributeMapper.getAttributeIdOfGender();
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // }
  }

  public void refreshSynonyms() {
    try {
      List<String> synosInStrList = synonymMapper.getAllSynonyms();
      if (synosInStrList != null && synosInStrList.size() > 0) {
        synonymMapping.clear();
        for (String synosInStr : synosInStrList) {
          String[] synoArr = synosInStr.split("[,]");
          if (synoArr != null) {
            for (String syno : synoArr) {
              if (!syno.trim().equals("")
                  && !synonymMapping.containsKey(syno)) {
                synonymMapping
                    .put(syno, Arrays.asList(synoArr));
              }
            }
          }
        }
        // log.debug(synonymMapping.toString());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void refreshSaleCountList() {
    try {
      List<ChannelGoodsMapping> channelGoodsist = channelGoodsMapper.getChannelGoodsList();
      if (channelGoodsist != null && channelGoodsist.size() > 0) {
        saleCountMapping.clear();
        for (ChannelGoodsMapping goods : channelGoodsist) {
          saleCountMapping.put(goods.getSysCode(), goods);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void refreshGoodsTagList() {
    try {
      List<MgrGoodsTags> list = (List<MgrGoodsTags>) goodsTagsMapper.getProductTagsProId();
      if (list != null && list.size() > 0) {
        mgrGoodsMapping.clear();
        for (MgrGoodsTags goods : list) {
          if (mgrGoodsMapping.get(goods.getGoodsSn()) == null) {
            mgrGoodsMapping.put(goods.getGoodsSn(), goods.getTagWord());
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  static public String getGoodsTag(String goodsSn) {
    if (mgrGoodsMapping != null) {
      return mgrGoodsMapping.get(goodsSn);
    }
    return null;
  }

  static public int getCategoryLevelByCid(int categoryId) {
    // 默认3级
    if (catIdCatLevelMapping != null) {
      Integer cLevel = catIdCatLevelMapping.get(categoryId);
      if (cLevel == null) {
        log.debug("categoryId:{} level is null,default return 3", categoryId);
        return 3;
      }
      return cLevel;
    }
    log.debug("getCategoryLevelByCid.catIdCatLevelMapping is null,,default return 3");
    return 3;
  }

  static public String getSizeNameByCode(String sizeCode) {
    if (sizeMap != null) {
      return sizeMap.get(sizeCode);
    }
    return null;
  }

  static public SizeInfo getSizeInfoByCode(String sizeCode) {
    if (sizeInfoMap != null) {
      return sizeInfoMap.get(sizeCode);
    }
    return null;
  }

  static public String getSizeSeriesNameBySeriesCode(String sizeSeriesCode) {
    if (sizeSeriesMap != null) {
      return sizeSeriesMap.get(sizeSeriesCode);
    }
    return null;
  }

  static public String getBrandNameByCode(String brandCode) {
    if (brandMap != null) {
      return brandMap.get(brandCode);
    }
    return null;
  }

  static public String getColorSystemNameByCode(String colorSystemCode) {
    if (colorSystemMap != null) {
      return colorSystemMap.get(colorSystemCode);
    }
    return null;
  }

  static public String getAttributeKeyNameByCode(String attributeKeyCode) {
    if (attributeKeyMap != null) {
      return attributeKeyMap.get(attributeKeyCode);
    }
    return null;
  }

  static public String getAttributeValueByCode(String attributeValueCode) {
    if (attributeValueMap != null) {
      return attributeValueMap.get(attributeValueCode);
    }
    return null;
  }

  static public String getAttributeSeriesValueBySeriesCode(String attributeValueSeriesCode) {
    if (attributeSeriesValueMap != null) {
      return attributeSeriesValueMap.get(attributeValueSeriesCode);
    }
    return null;
  }

  static public Set<String> getAllAttributeKeyCodes() {
    if (attributeKeyMap != null && attributeKeyMap.keySet() != null) {
      return attributeKeyMap.keySet();
    }
    return null;
  }

  static public Set<Category> getExCatSetByBasicCatId(String basicCatId) {
    if (basicCatId2ExCatSetMap != null) {
      return basicCatId2ExCatSetMap.get(basicCatId);
    }
    return null;
  }

  static public void getExCatTreeUnderOneExCat(Category currentCategory,
      List<Count> countList, boolean fillStuff) {
    Set<Category> directSubExCats = exCat2DirectSubExCatsMapping
        .get(currentCategory);
    // Category category = new Category();
    if (fillStuff) {
      if (exCat2DirectSubExCatsMapping.keySet() != null
          && exCat2DirectSubExCatsMapping.keySet().size() > 0) {
        for (Category c : exCat2DirectSubExCatsMapping.keySet()) {
          if (c.equals(currentCategory)) {
            currentCategory.setCateName(c.getCateName());
            currentCategory.setParentId(c.getParentId());
            currentCategory.setSort(c.getSort());
            currentCategory.setLevel(c.getLevel());
            // add count
            currentCategory.setCount(c.getCount());
            /*
             * category.setCateId(c.getCateId()); category.setCateName(c.getCateName());
             * category.setParentId(c.getParentId()); category.setLevel(c.getLevel());
             */
            break;
          }
        }
      }
    }

    if (directSubExCats != null && directSubExCats.size() > 0) {
      for (Category directSubExCat : directSubExCats) {
        for (Count count : countList) {
          try {
            if (new Category(Integer.valueOf(count.getName()))
                .equals(directSubExCat) && count.getCount() > 0) {
              // add count
              Category category = new Category(directSubExCat);
              category.setCount(count.getCount());
              currentCategory.getSubs().add(category);
              /*
               * currentCategory.getSubs().add( new Category(directSubExCat));
               */
              break;
            }
          } catch (NumberFormatException nfe) {
            log.error(nfe.getMessage(), nfe);
          }
        }
      }
      if (currentCategory.getSubs() != null
          && currentCategory.getSubs().size() > 0)
        for (Category addedDirectSubExCat : currentCategory.getSubs()) {
          getExCatTreeUnderOneExCat(addedDirectSubExCat, countList,
              false);
        }
      List<Category> list = currentCategory.getSubs();
      if (list != null && list.size() > 1) {
        Collections.sort(list, new Comparator<Category>() {

          public int compare(Category c1, Category c2) {
            return c1.getSort() - c2.getSort();
          }
        });
      }
    }
  }

  static public void cacleSubsNull(Category currentCategory,
      List<Count> countList, boolean fillStuff) {
    Category category = new Category();
    if (fillStuff) {
      if (exCat2DirectSubExCatsMapping.keySet() != null
          && exCat2DirectSubExCatsMapping.keySet().size() > 0) {
        for (Category c : exCat2DirectSubExCatsMapping.keySet()) {
          if (c.equals(currentCategory)) {
            currentCategory.setCateName(c.getCateName());
            currentCategory.setParentId(c.getParentId());
            currentCategory.setSort(c.getSort());
            currentCategory.setLevel(c.getLevel());
            category.setCateId(c.getCateId());
            category.setCateName(c.getCateName());
            category.setParentId(c.getParentId());
            category.setLevel(c.getLevel());
            break;
          }
        }
      }
    }
    if (currentCategory.getSubs() == null || currentCategory.getSubs().size() <= 0) {
      if (currentCategory.getCateId() != 0 && currentCategory.getParentId() != 0 && currentCategory.getLevel() != 0) {
        currentCategory.addSub(category);
      }
    }
  }

  static public void getCrumbs(int currentExCatId, CategoryCrumbs parentCategory) {
    for (CatIdCatNameLevelMapping cIdNameLevel : catIdCatNameLevelMapping) {
      if (cIdNameLevel.getCatId().equals(String.valueOf(currentExCatId))) {
        Crumbs crumbs = new Crumbs();
        String catId = cIdNameLevel.getCatId();
        String catName = cIdNameLevel.getCatName();
        String parentId = cIdNameLevel.getParentId();
        int level = cIdNameLevel.getLevel();
        crumbs.setCatId(catId);
        crumbs.setCatName(catName);
        crumbs.setParentId(parentId);
        crumbs.setLevel(level);
        parentCategory.getSubs().add(crumbs);
        if (parentId.equals("0")) {
          break;
        } else {
          getCrumbs(Integer.parseInt(parentId), parentCategory);
        }
      }
    }
  }

  static public void getOneCategory(OneCategory oneCategory, List<Count> countList) {
    Set<Category> directSubExCats = exCat2DirectSubExCatsMapping
        .get(new Category(0));
    if (directSubExCats != null && directSubExCats.size() > 0) {
      for (Category directSubExCat : directSubExCats) {
        for (Count count : countList) {
          try {
            if (new Category(Integer.valueOf(count.getName()))
                .equals(directSubExCat) && count.getCount() > 0) {
              if (directSubExCat.getParentId() == 0) {
                Crumbs crumbs = new Crumbs();
                int catId = directSubExCat.getCateId();
                String catName = directSubExCat.getCateName();
                int parentId = directSubExCat.getParentId();
                int level = directSubExCat.getLevel();
                crumbs.setCatId(String.valueOf(catId));
                crumbs.setCatName(catName);
                crumbs.setParentId(String.valueOf(parentId));
                crumbs.setLevel(level);
                oneCategory.getSubs().add(crumbs);
              }
            }
          } catch (NumberFormatException nfe) {
            log.error(nfe.getMessage(), nfe);
          }
        }
      }
    }
  }

  static public String getBrandName(String code) {
    for (BrandBean brandBean : allBrand) {
      if (brandBean.getCode().equals(code)) {
        return brandBean.getName();
      }
    }
    return "";
  }

  static public String getCategoryName(String cid) {
    for (CatIdCatNameLevelMapping cIdNameLevel : catIdCatNameLevelMapping) {
      if (cIdNameLevel.getCatId().equals(cid)) {
        return cIdNameLevel.getCatName();
      }
    }
    return "";
  }

  static public String getChannelCode() {
    return channelCode;
  }

  static public String getAttributeKeyIdOfGender() {
    return attributeKeyIdOfGender;
  }

  static public Set<String> getGenderInfoOfExCat(String exCatId) {
    try {
      if (exCat2DirectSubExCatsMapping != null
          && exCat2DirectSubExCatsMapping.keySet() != null) {
        for (Category exCat : exCat2DirectSubExCatsMapping.keySet()) {
          if (exCat.equals(new Category(Integer.valueOf(exCatId)))) {
            String genderInfo = exCat.getSiteSex();
            if (genderInfo != null && !genderInfo.equals("")) {
              Set<String> genderSet = null;
              String[] genderArray = genderInfo.split("[|]");
              for (String gender : genderArray) {
                if (!gender.equals("")) {
                  try {
                    Integer.valueOf(gender);
                    if (genderSet == null)
                      genderSet = new HashSet<String>();
                    genderSet.add(gender);
                  } catch (NumberFormatException nfe) {

                  }
                }
              }
              return genderSet;
            }
            break;
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  static private void populateParentIdList(List<String> parentIdList,
      String catId) {
    if (catIdParentIdMapping != null && catIdParentIdMapping.size() > 0) {
      for (CatIdParentIdMapping mapping : catIdParentIdMapping) {
        if (mapping.getCatId().equals(catId)) {
          String parentId = mapping.getParentId();
          if (!parentId.equals("0")) {
            parentIdList.add(parentId);
            populateParentIdList(parentIdList, parentId);
          }
          break;
        }
      }
    }
  }

  static public Set<String> getParentCatNamesByCatId(String catId) {
    if (catIdParentIdMapping != null && catIdParentIdMapping.size() > 0) {
      try {
        List<String> parentIdList = new ArrayList<String>();
        populateParentIdList(parentIdList, catId);
        if (parentIdList != null && parentIdList.size() > 0
            && catIdCatNameMapping != null
            && catIdCatNameMapping.size() > 0) {
          Set<String> parentNames = new LinkedHashSet<String>();
          for (String parentId : parentIdList) {
            for (CatIdCatNameMapping mapping : catIdCatNameMapping) {
              if (mapping.getCatId().equals(parentId)) {
                parentNames.add(mapping.getCatName());
                break;
              }
            }
          }
          return parentNames;
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return null;
  }

  static public List<String> getSynonyms(String word) {
    if (synonymMapping != null) {
      return synonymMapping.get(word);
    }
    return null;
  }

  static public ChannelGoodsMapping getSaleCountMouth(String code) {
    if (saleCountMapping != null) {
      return saleCountMapping.get(code);
    }
    return null;
  }

  static public void getAllparentId(String cateId, List<String> parentId) {
    for (CatIdParentIdMapping cateParentId : catIdParentIdMapping) {
      if (cateParentId.getCatId().equals(cateId)) {
        String pId = cateParentId.getParentId();
        if (pId.equals("0")) {
          break;
        } else {
          parentId.add(pId);
          getAllparentId(pId, parentId);
        }

      }
    }
  }

}
