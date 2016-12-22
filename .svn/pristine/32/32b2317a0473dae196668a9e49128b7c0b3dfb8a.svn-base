package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.MgrDicKeyword;

public interface MgrDicKeywordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MgrDicKeyword record);

    int insertSelective(MgrDicKeyword record);

    MgrDicKeyword selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MgrDicKeyword record);

    int updateByPrimaryKey(MgrDicKeyword record);
    
    List<String> getDicKeyword(MybatisBean params);
    
    long countAll();
    
    List<MgrDicKeyword> getDicKeywordBean(MybatisBean params);
}