package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.SearchErrorLog;

public interface SearchErrorLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SearchErrorLog record);

    int insertSelective(SearchErrorLog record);

    SearchErrorLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SearchErrorLog record);

    int updateByPrimaryKey(SearchErrorLog record);
    
    public List<SearchErrorLog> getErrorLogs(MybatisBean option);
}