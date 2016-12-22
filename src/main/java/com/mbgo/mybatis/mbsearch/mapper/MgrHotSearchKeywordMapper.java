package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.mbsearch.bean.MgrHotSearchKeyword;
import com.mbgo.search.core.bean.keyword.HotWordQuery;

public interface MgrHotSearchKeywordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MgrHotSearchKeyword record);

    int insertSelective(MgrHotSearchKeyword record);

    MgrHotSearchKeyword selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MgrHotSearchKeyword record);

    int updateByPrimaryKey(MgrHotSearchKeyword record);
    
    List<MgrHotSearchKeyword> selectList(HotWordQuery query);
}