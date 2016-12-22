package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.MgrHotKeyword;

public interface MgrHotKeywordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MgrHotKeyword record);

    int insertSelective(MgrHotKeyword record);

    MgrHotKeyword selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MgrHotKeyword record);

    int updateByPrimaryKey(MgrHotKeyword record);
    
    long countAll();
    
    List<MgrHotKeyword> getHotKeyword(MybatisBean params);
}