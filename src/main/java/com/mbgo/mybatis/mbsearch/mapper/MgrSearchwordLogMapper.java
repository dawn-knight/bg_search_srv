package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

import com.mbgo.mybatis.mbsearch.bean.MgrSearchwordLog;

public interface MgrSearchwordLogMapper {

	public int addWord(MgrSearchwordLog searchwordLog);
	public int updateWord(MgrSearchwordLog searchwordLog);
	public List<MgrSearchwordLog> selectByWordAndDate(MgrSearchwordLog searchwordLog);
}