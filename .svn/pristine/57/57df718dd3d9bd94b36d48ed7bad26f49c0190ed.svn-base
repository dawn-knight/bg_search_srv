package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.commonbean.MybatisBean;
import com.mbgo.mybatis.mbsearch.bean.StockDetailInfo;

public interface StockDetailInfoMapper {
    int deleteByPrimaryKey(String sku);

    int insert(StockDetailInfo record);

    int insertSelective(StockDetailInfo record);

    StockDetailInfo selectByPrimaryKey(String sku);

    int updateByPrimaryKeySelective(StockDetailInfo record);

    int updateByPrimaryKey(StockDetailInfo record);
    
    List<StockDetailInfo> getProductStockDetails(MybatisBean params);
}