package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.MgrGoodsTags;
import com.mbgo.search.core.bean.index.Product;
import com.mbgo.search.core.bean.index.TagInfo;
import com.mbgo.search.core.filter.CategoryAttibute;

public interface MgrGoodsTagsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MgrGoodsTags record);

    int insertSelective(MgrGoodsTags record);

    MgrGoodsTags selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MgrGoodsTags record);

    int updateByPrimaryKey(MgrGoodsTags record);
    
    List<Product> getProductTags(MybatisBean params);
    
    List<TagInfo> getDisplayTags();
    
    List<CategoryAttibute> getCategoryAttibute();
    List<MgrGoodsTags> queryTagsBygoodsSn(String goodsSn);
    public List<MgrGoodsTags> getProductTagsProId();
}