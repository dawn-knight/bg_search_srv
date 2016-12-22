/*
* 2014-10-13 下午3:09:58
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.service.use4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.commonbean.MybatisBean;
//import com.mbgo.mybatis.mborg.mapper.ChannelInfoMapper;
//import com.mbgo.mybatis.mborg.model.ChannelInfoBean;
import com.mbgo.mybatis.mbsearch.bean.MgrConvertKeyword;
import com.mbgo.mybatis.mbsearch.mapper.MgrConvertKeywordMapper;
import com.mbgo.mybatis.mbsearch.mapper.MgrGoodsTagsMapper;
//import com.mbgo.mybatis.mbshop.bean.ProductLibColor;
//import com.mbgo.mybatis.mbshop.mapper.ProductInfoMapper;
//import com.mbgo.mybatis.mbshop.mapper.ProductLibBrandMapper;
//import com.mbgo.mybatis.mbshop.mapper.ProductLibColorMapper;
import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
//import com.mbgo.search.constant.ChannelConst;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.query.tag.TagChainUtil;
import com.mbgo.search.core.cache.CacheUtil;
//import com.mbgo.search.core.filter.CategoryFilterSortManager;
//import com.mbgo.search.core.filter.FilterContainerSorter;
//import com.mbgo.search.core.filter.attr.AttributeBean;
//import com.mbgo.search.core.filter.brand.BrandBean;
import com.mbgo.search.core.filter.category.Category;
import com.mbgo.search.core.filter.category.CategoryCaculator;
//import com.mbgo.search.core.filter.color.ColorCaculator;
//import com.mbgo.search.core.filter.size.SizeBean;
import com.mbgo.search.core.tools.keyword.QueryWordConvertor;
//import com.metersbonwe.pcs.dao.ExtendedProductLibBrandMapper;
import com.metersbonwe.pcs.dao.ExtendedProductLibCategoryMapper;
//import com.metersbonwe.pcs.dao.ProductLibAttributeMapper;
//import com.metersbonwe.pcs.dao.ProductLibColorSeriesMapper;
@Service("cacheService")
public class CacheService {
	
//	@Autowired
//	private ExtendedProductLibCategoryMapper extendedProductLibCategoryMapper;
//	@Autowired
//	private ProductLibAttributeMapper productLibAttributeMapper;
//	@Autowired
//	private ProductLibColorSeriesMapper productLibColorSeriesMapper;
//	@Autowired
//	private ExtendedProductLibBrandMapper extendedProductLibBrandMapper;
//	@Autowired
//	private ChannelInfoMapper channelInfoMapper;
	
//	@Value("#{channelConfig['channel_to_serve']}")
//	private String idOfChannelToServeStr = "1";
	
//	private static String channelCode = ChannelConst.DEFAULT_MB_CHANNEL_CODE;
	
//	private static Map<Category, List<String>> topLevelVc2BcIdsMap = new HashMap<Category, List<String>>();
//	
//	private static Map<Integer, List<String>> nonTopLevelVcId2BcIdsMap = new HashMap<Integer, List<String>>();
//
//	private static Map<Integer, List<Category>> vcId2SubVcsMap = new HashMap<Integer, List<Category>>();
	
//	private static Map<Integer, List<String>> vIdGendersMap = new HashMap<Integer, List<String>>();
	
//	private static String attributeIdOfGender = "98";
	
//	@Autowired
//	private ProductInfoMapper productInfoMapper;//获得款式级别详细信息
//	@Autowired
//	private ProductLibColorMapper productLibColorMapper;
//	@Autowired
//	private MgrConvertKeywordMapper mgrConvertKeywordMapper;
//	@Autowired
//	private ProductLibBrandMapper productLibBrandMapper;
//	@Autowired
//	private MallThemeGoodsMapper mallThemeGoodsMapper;
	@Autowired
	private MgrGoodsTagsMapper mgrGoodsTagsMapper;
	//基础分类信息
//	private static CategoryCaculator CATEGORY_CACULATOR = null;
	//分类对应关系
//	private static Map<Integer, Category> CATEGORY_MAP = new HashMap<Integer, Category>();//分类
	//颜色信息
//	private static Map<String, ProductLibColor> COLOR_CODE_MAP = new HashMap<String, ProductLibColor>();//颜色
	//尺寸映射关系
//	private static Map<String, String> SIZE_CODE_MAP = new HashMap<String, String>();//尺寸
	//属性key信息
//	private static Map<String, String> ATTRIBUTE_KEY_CODE_NAME = new HashMap<String, String>();
	//属性值信息
//	private static Map<String, String> ATTRIBUTE_VALUE_CODE_NAME = new HashMap<String, String>();
	//品牌
//	private static Map<String, String> BRAND_CODE_NAME_MAP = new HashMap<String, String>();
	//店铺
//	private static Map<String, BrandBean> STORE_CODE_NAME_MAP = new HashMap<String, BrandBean>();
	//扩展分类
//	private static List<Category> SITE_CATEGORY = new ArrayList<Category>();
	//扩展分类对应关系
//	private static Map<String, Category> SITE_CATEGORY_MAP = new HashMap<String, Category>();//分类
	//扩展分类的计算器
//	private static CategoryCaculator SITE_CATE_CACULATOR = null;
	//扩展分类和基础分类的映射关系
//	private static Map<String, String> SITEID_TO_BASICID = new HashMap<String, String>();
	//分类下属性排序
//	private static CategoryFilterSortManager categoryFilterSortManager = new CategoryFilterSortManager();
	
//	public static Boolean isCreate = false;
	public CacheService() {
//		if(!isCreate) {
//			synchronized (isCreate) {
//				if(!isCreate) {
//					isCreate = true;
//					try {
//						//启动缓存自动更新线程
//						ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//						executor.scheduleWithFixedDelay(new Runnable() {
//							public void run() {
//								initAll();
//							}
//						}, 4, 5, TimeUnit.MINUTES);
//					} catch (Exception e) {
//						
//					}
//				}
//			}
//		}
	}
	/**
	 * 初始化分类相关数据
	 */
//	public void initCategoryMap() {
//		CATEGORY_MAP = CacheUtil.createMap(1, productInfoMapper.getAllCategorys());
////		log.debug("{} init category map", LOG_TYPE);
//	}
	// new version
//	public void initCategoryMap() {
//		CATEGORY_MAP = CacheUtil.createMap(1, extendedProductLibCategoryMapper.getCategoryList());
//	}
	
	/**
	 * 初始化颜色码和颜色详细信息的映射关系
	 */
//	public void initColorCodeMap() {
//		COLOR_CODE_MAP = CacheUtil.createMap("", productLibColorMapper.getAllColorValues());
////		log.debug("{} init brandCode map", LOG_TYPE);
//	}
	// new version
//	public void initColorCodeMap() {
//		COLOR_CODE_MAP = CacheUtil.createMap("", productLibAttributeMapper.getColorList());
//	}
	
	/**
	 * 初始化分类统计计算器
	 */
//	public void initCategoryCaculator() {
//		if(CATEGORY_CACULATOR == null) {
//			CATEGORY_CACULATOR = new CategoryCaculator();
//		}
//		synchronized(CATEGORY_CACULATOR) {
//			CATEGORY_CACULATOR = new CategoryCaculator().initParentCategorys(productInfoMapper.getAllCategorys());
////			log.debug("{} init categoryCaculator", LOG_TYPE);
//		}
//	}
	// new version
//	public void initCategoryCaculator() {
//		if(CATEGORY_CACULATOR == null) {
//			CATEGORY_CACULATOR = new CategoryCaculator();
//		}
//		synchronized(CATEGORY_CACULATOR) {
//			CATEGORY_CACULATOR = new CategoryCaculator().initParentCategorys(extendedProductLibCategoryMapper.getCategoryList());
////			log.debug("{} init categoryCaculator", LOG_TYPE);
//		}
//	}
	
	/**
	 * 初始化分类下属性显示设置信息
	 */
//	public void initCategorySort() {
//		synchronized(categoryFilterSortManager) {
//			categoryFilterSortManager.init(mgrGoodsTagsMapper.getCategoryAttibute());
//		}
//	}
	/**
	 * 初始化尺码和名称的映射关系
	 */
//	public void initSizeCodeNameMap() {
//		synchronized(SIZE_CODE_MAP) {
//			List<SizeBean> sbs = productLibColorMapper.getSizeCodeNameMap();
//			for(SizeBean sb : sbs) {
//				SIZE_CODE_MAP.put(sb.getSizeCode(), sb.getSizeName());
//			}
////			log.debug("{} init sizeCodeNameMap", LOG_TYPE);
//		}
//	}
	// new version
//	public void initSizeCodeNameMap() {
//		List<SizeBean> sizeList = productLibAttributeMapper.getSizeList();
//		for(SizeBean size:sizeList) {
//			SIZE_CODE_MAP.put(size.getSizeCode(), size.getSizeName());
//		}
//	}
		
	/**
	 * 初始化颜色统计计算器
	 */
//	public void initColorSeriesMapper() {
//		ColorCaculator.initSeriesMapper(productLibColorMapper.getSeriesMap());
////		log.debug("{} init colorSeriesMap", LOG_TYPE);
//	}
	// new version
//	public void initColorSeriesMapper() {
//		ColorCaculator.initSeriesMapper(productLibColorSeriesMapper.getColorSeriesList());
//	}
	
	/**
	 * 初始化属性code和属性名的映射关系
	 */
//	public void initAttributeMapper() {
//		List<AttributeBean> keys = productLibAttributeMapper.getAttributeKeyList();
//		if(keys != null) {
//			for(AttributeBean abb : keys) {
//				ATTRIBUTE_KEY_CODE_NAME.put(abb.getCode(), abb.getName());
//			}
//		}
////		log.debug("{} init attributeKeyMap", LOG_TYPE);
//		
//		List<AttributeBean> values = productLibAttributeMapper.getAttributeValueList();
//		if(keys != null) {
//			for(AttributeBean abb : values) {
//				ATTRIBUTE_VALUE_CODE_NAME.put(abb.getCode(), abb.getName());
//			}
//		}
////		log.debug("{} init attributeValueMap", LOG_TYPE);
//		
//		attributeIdOfGender = productLibAttributeMapper.getAttributeIdOfGender();
//		log.debug("attribute of gender : {}", attributeIdOfGender);
//	}
	/**
	 * 初始化关键字转换
	 */
//	public void initWordConvertInfo() {
//		MybatisBean option = new MybatisBean(0, 10000);
//		List<MgrConvertKeyword> keywords = mgrConvertKeywordMapper.selectConvertList(option);
//		for(MgrConvertKeyword keyword : keywords) {
//			String srcWord = keyword.getOldword();
//			String distWord = keyword.getNewword();
//			if(StringUtils.isBlank(srcWord) || StringUtils.isBlank(distWord)) {
//				continue;
//			}
//			QueryWordConvertor.testAdd(srcWord, distWord);
//		}
////		log.debug("{} init word convert info", LOG_TYPE);
//	}
	/**
	 * 初始化品牌code和名称的映射关系
	 */
//	public void initBrandCodeNameMap() {
//		List<BrandBean> brands = productLibBrandMapper.getBrandInfo();
//		if(brands != null) {
//			for(BrandBean bb : brands) {
//				BRAND_CODE_NAME_MAP.put(bb.getCode().toUpperCase(), bb.getName());
//			}
//		}
////		log.debug("{} init brandCodeNameMap", LOG_TYPE);
//	}
	// new version
//	public void initBrandCodeNameMap() {
//		List<BrandBean> brandList = extendedProductLibBrandMapper.getBrandList();
//		if(brandList!=null && brandList.size() > 0) {
//			for(BrandBean brand:brandList) {
//				BRAND_CODE_NAME_MAP.put(brand.getCode().toUpperCase(), brand.getName());
//			}
//		}
//	}
	
	/**
	 * 初始化店铺code和名称的映射关系
	 */
//	public void initStoreCodeNameMap() {
//		List<BrandBean> stores = productLibBrandMapper.getStoreInfo();
//		if(stores != null) {
//			for(BrandBean bb : stores) {
//				STORE_CODE_NAME_MAP.put(bb.getCode().toUpperCase(), bb);
//			}
//		}
////		log.debug("{} init storeCodeNameMap", LOG_TYPE);
//	}
	
//	private void classifyVirtualCategory(Category c) {
//		List<String> basicCategoryList = null;
//		String[] basicCategoryArray = c.getBasicCateIds().split("[,]");
//		if (basicCategoryArray != null && basicCategoryArray.length > 0) {
//			basicCategoryList = new ArrayList<String>();
//			for(int i = 0; i < basicCategoryArray.length; i++) {
//				basicCategoryList.add(basicCategoryArray[i]);
//			}
//		}
//		if (basicCategoryList != null && basicCategoryList.size() > 0) {
//			if (c.getParentId() == 0) {
//				topLevelVc2BcIdsMap.put(c, basicCategoryList);
//			} else {
//				nonTopLevelVcId2BcIdsMap.put(c.getCateId(), basicCategoryList);
//			}
//		}		
//	}
//	
//	private void generateVcId2SubVcMap(Category c) {
//		List<Category> subVcList = vcId2SubVcsMap.get(c.getParentId());
//		if (subVcList == null) 
//			subVcList = new ArrayList<Category>();
//		if (!subVcList.contains(c)) {
//			subVcList.add(c);
//			vcId2SubVcsMap.put(c.getParentId(), subVcList);
//		}
//	}
//	
//	private void generateVidGendersMap(Category c) {
//		List<String> genderList = null;
//		String genders = c.getSiteSex();
//		if (genders != null && !genders.equals("") && !genders.equalsIgnoreCase("null")) {
//			genderList = Arrays.asList(genders.split("[|]"));
//		}
//		vIdGendersMap.put(c.getCateId(), genderList);
//	}
	
	/**
	 * 初始化扩展分类和基础分类之间的相关信息
	 */
//	public void initSiteCategory() {
//		SITE_CATEGORY = mallThemeGoodsMapper.getAllExCats();
//		for(Category c : SITE_CATEGORY) {
//			classifyVirtualCategory(c);
//			generateVcId2SubVcMap(c);
//
//			String[] vals = c.getBasicCateIds().split(",");
//			Set<String> filter = new HashSet<String>();
//			Set<String> useful = new HashSet<String>();
//			/*
//			 * 子父类id去重复
//			 */
//			for(String k : vals) {
//				if(filter.contains(k)) {
//					continue;
//				}
//				useful.add(k);//加入当前分类
//				List<String> subs = CATEGORY_CACULATOR.getSubs(k);
//				if(subs != null) {//存在子分类，则将自分类记录筛选过滤，然后从useful里面移除
//					for(String sub : subs) {
//						filter.add(sub);
//						useful.remove(sub);
//					}
//				}
//			}
//			StringBuilder temp = new StringBuilder("");
//			for(String t : useful) {
//				temp.append(t).append("|");
//			}
//			if(temp.length() > 1) {
//				temp.deleteCharAt(temp.length() - 1);
//			}
//			
//			String cid = c.getCateId() + "";
//			
//			//扩展分类和基础分类信息
//			SITEID_TO_BASICID.put(cid, temp.toString());
//			
////			//扩展分类id和分类信息
////			SITE_CATEGORY_MAP.put(cid, c);
//			
//			generateVidGendersMap(c);
//		}
//		//扩展分类统计计算器
////		SITE_CATE_CACULATOR = new CategoryCaculator().initParentCategorys(SITE_CATEGORY);
////		log.debug("{} init siteCategory", LOG_TYPE);
//	}
	/**
	 * 显示标签标签链
	 */
	public void initTagChain() {
		TagChainUtil.initChain(mgrGoodsTagsMapper.getDisplayTags());
//		log.debug("{} init display tag chain", LOG_TYPE);
	}
	public void initAll() {
//		initCategoryMap(); // new version
//		initColorCodeMap(); // new version
//		initCategoryCaculator(); // new version
//		initCategorySort(); // no need to modify
//		initWordConvertInfo(); // no need to modify
//		initColorSeriesMapper(); // new version
//		initSizeCodeNameMap(); // new version
//		initAttributeMapper();
//		initBrandCodeNameMap(); // new version
//		initStoreCodeNameMap(); // new version
//		initSiteCategory(); // no need to modify
		initTagChain(); // no need to modify
//		initChannelInfo();
//		log.debug("{} category, colorCode, wordConvert, colorSeries, sizeCode, attribute, brandCode, storeCode, siteCategory, tagChain", LOG_TYPE);
//		log.debug("{} all", LOG_TYPE);
	}
	
	
	/**
	 * 根据id查询分类信息
	 * @param id
	 * @param isFresh
	 * @return
	 */
//	public Category getCategoryById(int id, boolean isFresh) {
//		if(isFresh) {
//			Category newCategory = productInfoMapper.selectCategoryById(id);
//			if(newCategory != null) {
//				CATEGORY_MAP.put(id, newCategory);
//			}
//			return newCategory;
//		} else {
//			if(CATEGORY_MAP == null || CATEGORY_MAP.size() < 1) {
//				initCategoryMap();
//			}
//			return CATEGORY_MAP.get(id);
//		}
//	}
	// new version
//	public Category getCategoryById(int id, boolean isFresh) {
//		if(isFresh) {
//			Category newCategory = extendedProductLibCategoryMapper.getCategoryById(id);
//			if(newCategory != null) {
//				CATEGORY_MAP.put(id, newCategory);
//			}
//			return newCategory;
//		} else {
//			if(CATEGORY_MAP == null || CATEGORY_MAP.size() < 1) {
//				initCategoryMap();
//			}
//			return CATEGORY_MAP.get(id);
//		}
//	}	
	
//	public ProductLibColor getColorByCode(String colorCode) {
//		if(COLOR_CODE_MAP == null || COLOR_CODE_MAP.size() < 1) {
//			initColorCodeMap();
//		}
//		return COLOR_CODE_MAP.get(colorCode);
//	}
//	public List<Integer> getAllCategoryIds(Integer categoryId) {
//		if(CATEGORY_CACULATOR == null) {
//			initCategoryCaculator();
//		}
//		return CATEGORY_CACULATOR.getAllCategoryId(categoryId);
//	}
//	public String getSizeName(String sizeCode) {
//		if(SIZE_CODE_MAP == null) {
//			initSizeCodeNameMap();
//		}
//		return SIZE_CODE_MAP.get(sizeCode);
//	}
//	public Map<String, String> getAttributeKey() {
//		return ATTRIBUTE_KEY_CODE_NAME;
//	}
//	public String getAttributeValueName(String valueCode) {
//		return ATTRIBUTE_VALUE_CODE_NAME.get(valueCode);
//	}
//	public String getBrandNameByCode(String code) {
//		return BRAND_CODE_NAME_MAP.get(code);
//	}
//	public String getStoreNameByCode(String code) {
//		return STORE_CODE_NAME_MAP.get(code).getName();
//	}
//	public BrandBean createNewStoreBean(String code, long count) {
//		BrandBean b = STORE_CODE_NAME_MAP.get(code);
//		if(b != null) {
//			b.setCount(count);
//			return b;
//		}
//		return null;
//	}
	/**
	 * 根据基础分类id以及商品数的统计信息，对应计算出扩展分类的数量信息
	 * @param map
	 * @return
	 */
//	public List<Category> getSiteCategorys(Map<String, Long> map) {
//		List<Category> rs = new ArrayList<Category>();
//
//		if(map == null || map.size() < 1) {
//			return rs;
//		}
//		if(SITE_CATEGORY.size() < 1) {
//			initSiteCategory();
//		}
//		for(Category c : SITE_CATEGORY) {
//			String basicIds = SITEID_TO_BASICID.get(String.valueOf(c.getCateId()));
//			
//			if(StringUtils.isNotBlank(basicIds)) {
//				String[] vals = basicIds.split("[,|\\|]");
//				long count = 0;
//				
//				for(String k : vals) {
//					Long mapCount = map.get(k);
//					if(mapCount != null) {
//						count += mapCount;
//					}
//				}
//				if(count < 1) {
//					continue;
//				}
//				Category nc = new Category();
//				nc.setCateId(c.getCateId());
//				nc.setCateName(c.getCateName());
//				nc.setParentId(c.getParentId());
//				nc.setCount(count);
//				nc.setSort(c.getSort());
//				rs.add(nc);
//			}
//		}
//		return rs;
//	}
	/**
	 * 根据扩展分类id，获得对应的一批基础分类ID
	 * @param id
	 * @return
	 */
//	public String getBasicIds(String id) {
//		if(StringUtils.isNotBlank(id)) {
//			return SITEID_TO_BASICID.get(id);
//		}
//		return null;
//	}
	
	//是否是底层分类
//	private boolean isBottomCategory(Integer cid) {
//		List<String> rs = SITE_CATE_CACULATOR.getSubs(String.valueOf(cid));
//		return rs == null || rs.size() < 1;
//	}
	//获得最后一级分类
//	private Integer getCateIdFromCurrent(Category c) {
//		if(c == null) {
//			return null;
//		}
//		List<Category> subs = c.getSubs();
//		if(subs != null && subs.size() == 1) {
//			return getCateIdFromCurrent(subs.get(0));
//		}
//		return c.getCateId();
//	}
	
	/**
	 * 如果当前分类的同级数为一的分类，是最底层分类，则统计该分类的同级其他分类
	 * @param c
	 * @return
	 */
//	public Category createNewCurrentForBottom(Category c, Map<String, Long> bottomCateMap) {
//		Integer currentId = getCateIdFromCurrent(c);
//		if(isBottomCategory(currentId)) {
//			String cateId = String.valueOf(currentId);
//			List<String> sameLevelIds = SITE_CATE_CACULATOR.getSameLevelCids(cateId);
//			List<Category> sameCategorys = new ArrayList<Category>();
//			for(String siteId : sameLevelIds) {	//分别计算所同级分类
//				String basicIds = getBasicIds(siteId);
//				
//				Category newCate = SITE_CATEGORY_MAP.get(siteId);
//				if(newCate == null) {
//					continue;
//				}
//				long total = 0;
//				if(StringUtils.isNotBlank(basicIds)) {
//					String[] vals = basicIds.split("\\|");
//					for(String basicId : vals) {	//某一个分类，对应若干个基础分类，求和
//						Long basicCount = bottomCateMap.get(basicId);
//						if(basicCount != null) {
//							total += basicCount;
//						}
//					}
//					if(total > 0) {
//						newCate.setCount(total);
//						sameCategorys.add(newCate);
//					}
//				}
//			}
//			
//			if(sameCategorys.size() > 1) {
//				//替换子分类
//				setCategorySubs(c, currentId, sameCategorys);
//			}
//		}
//		
//		return setCurrentCategory(c, c);
//	}
	
	//设置当前分类
//	private Category setCurrentCategory(Category parent, Category c) {
//		if(c != null) {
//			if(c.getSubs().size() == 0) {
//				return c;
//			}
//			if(c.getSubs().size() > 1) {
//				return c;
//			}
//			return setCurrentCategory(c, c.getSubs().get(0));
//		}
//		return parent;
//	}

	//递归设置子分类
//	private void setCategorySubs(Category c, int currentId, List<Category> subs) {
//		if(c != null) {
//			List<Category> tempSub = c.getSubs();
//			if(tempSub != null && tempSub.size() == 1) {
//				if(tempSub.get(0).getCateId() == currentId) {
//					c.setSubs(subs);
//				}
//				setCategorySubs(tempSub.get(0), currentId, subs);
//			}
//		} 
//	}
	
	public void reload(String type) {
		// type:
		// category,color,size,attribute,brand,store,siteCategory,tagChain,wordConvert
		try {
//			if(StringUtils.isBlank(type)) {
//				return;
//			}
//			if(type.equalsIgnoreCase("category")) {
//				initCategoryMap();
//				initCategoryCaculator();
//			} else if(type.equalsIgnoreCase("color")) {
//				initColorCodeMap();
//				
//			} else if(type.equalsIgnoreCase("wordConvert")) {
//				initWordConvertInfo();
//				
//			} else if(type.equalsIgnoreCase("color")) {
//				initColorSeriesMapper();
//				
//			} else if(type.equalsIgnoreCase("size")) {
//				initSizeCodeNameMap();
//				
//			} else if(type.equalsIgnoreCase("attribute")) {
//				initAttributeMapper();
//				
//			} else if(type.equalsIgnoreCase("brand")) {
//				initBrandCodeNameMap();
//				
//			} else if(type.equalsIgnoreCase("store")) {
//				initStoreCodeNameMap();
//				
//			} else if(type.equalsIgnoreCase("siteCategory")) {
//				initSiteCategory();
//				initCategorySort();
//			} else if(type.equalsIgnoreCase("tagChain")) {
				initTagChain();
//			} 
		} catch (Exception e) {

		}
	}
	
//	public String keywordInfo(Product p) {
//		StringBuilder keyword = new StringBuilder();
//		try {
//			//分类
//			int cid = p.getCategoryId();
//			List<Integer> parents = CATEGORY_CACULATOR.getAllCategoryId(cid);
//			if(parents != null && parents.size() > 0) {
//				for(Integer id : parents) {
//					Category c = getCategoryById(id, false);
//					if(c != null) {
//						keyword.append(" ").append(c.getCateName());
//					}
//				}
//			}
//		} catch (Exception e) {
//			
//		}
//		return keyword.toString();
//	}
	
//	public FilterContainerSorter getCategorySorter(int cid) {
//		return categoryFilterSortManager.getCategorySorter(cid);
//	}
	
//	private void initChannelInfo() {
//		Integer idOfChannelToServe = new Integer(1);
//		try {
//			idOfChannelToServe = Integer.valueOf(idOfChannelToServeStr);
//		} catch (NumberFormatException e) {
//			log.error(e.getMessage());
//		}
//		log.debug("id of channel to serve [{}]", idOfChannelToServe);
//		if (idOfChannelToServe.equals(new Integer(0))) {
//			channelCode = ChannelConst.CODE_OF_ALL_CHANNEL;
//		} else {
//			List<ChannelInfoBean> channelInfoList = channelInfoMapper
//					.getChannelList();
//			if (channelInfoList != null && channelInfoList.size() > 0) {
//				for (ChannelInfoBean ci:channelInfoList) {
//					if(ci.getChannelId().equals(idOfChannelToServe)) {
//						channelCode = ci.getChannelCode();
//						break;
//					}
//				}
//			}
//		}
//		log.info("channel code [{}]", channelCode);
//	}
	
//	public static String getChannelCode() {
//		return channelCode;
//	}
	
//	private static Logger log = LoggerFactory.getLogger(CacheService.class);
	
//	private static final String LOG_TYPE = "[cache init]";
	
//	public Category getDirectSubVcWithBcMapped2ByVId(String vidStr, List<Count> basicCategoryQuantityList) {
//		log.debug("use vid[{}] to get direct sub-virtual-category with basic category mapped 2", vidStr);
//		Integer vid = null;
//		boolean formatted = true;
//		try {
//			vid = Integer.valueOf(vidStr);
//		} catch (NumberFormatException e) {
//			formatted = false;
//		}
//		if (vid != null && formatted && vid != 0) {
//			Category category = new Category();
//			for (Category c : SITE_CATEGORY) {
//				if(c.getCateId() == vid) {
//					category = new Category(c);
//					break;
//				}
//			}
//			List<Category> subVcList = vcId2SubVcsMap.get(vid);
//			if (subVcList != null) {
//				for(Category subVc : subVcList) {
//					List<String> bcIdList = nonTopLevelVcId2BcIdsMap.get(subVc.getCateId());
//					if (bcIdList != null && bcIdList.size() > 0) {
//						for(Count count:basicCategoryQuantityList){
//							if(bcIdList.contains(count.getName()) && count.getCount() > 0) {
//								category.getSubs().add(subVc);
//								break;
//							}							
//						}
//					}
//				}
//			}
//			return category;
//		} else {
//			Category root = new Category(0, "root", -1, -1L);
//			for (Category topVc : topLevelVc2BcIdsMap.keySet()) {
//				List<String> bcIdList = topLevelVc2BcIdsMap.get(topVc);
//				if (bcIdList != null && bcIdList.size() > 0) {
//					for(Count count:basicCategoryQuantityList){
//						if(bcIdList.contains(count.getName()) && count.getCount() > 0) {
//							root.getSubs().add(topVc);
//							break;
//						}
//					}
//				}
//			}
//			return root;
//		}
//	}
	
//	public Map<Integer, List<String>> getVidGendersMap() {
//		return vIdGendersMap;
//	}
//	
//	public String getAttributeIdOfGender() {
//		return attributeIdOfGender;
//	}
}
