package com.mbgo.mybatis.mbsearch.mapper;

import java.util.List;

public interface SynonymMapper {
	public List<String> getSynonyms(String word);
	public List<String> getAllSynonyms();
}
