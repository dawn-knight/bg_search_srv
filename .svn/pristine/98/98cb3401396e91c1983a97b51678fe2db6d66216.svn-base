package com.metersbonwe.pcs.dao;

import java.util.List;

import com.mbgo.search.core.filter.category.Category;
import com.metersbonwe.pcs.bean.CatIdCatNameLevelMapping;
import com.metersbonwe.pcs.bean.CatIdCatNameMapping;
import com.metersbonwe.pcs.bean.CatIdParentIdMapping;
import com.metersbonwe.pcs.bean.CatIdSubCatIdMapping;

public interface ExtendedProductLibCategoryMapper extends
		ProductLibCategoryMapper {
	public List<Category> getCategoryList();
	
	public Category getCategoryById(Integer categoryId);
	
	public List<CatIdSubCatIdMapping> getCatIdSubCatIdMapping();
	
	public List<CatIdParentIdMapping> getCatIdParentIdMapping();
	
	public List<CatIdCatNameMapping> getCatIdCatNameMapping();
	
	public List<Category> getAllCats();
	public List<CatIdCatNameLevelMapping> getCatIdCatNameLevelMapping();
}
