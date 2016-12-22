package com.mbgo.search.core.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.mbsearch.bean.MgrSearchwordLog;
import com.mbgo.mybatis.mbsearch.mapper.MgrSearchwordLogMapper;
import com.mbgo.search.core.bean.query.ProductQueryResult;
import com.mbgo.search.core.service.SearchWordService;
import com.mbgo.search.util.AutokeyConvernt;

@Service("searchWordService")
public class SearchWordServiceImpl implements SearchWordService{
	private static Logger log = LoggerFactory.getLogger(SearchWordServiceImpl.class);
	
	@Autowired
	private MgrSearchwordLogMapper searchwordLogMapper;
	
	@Override
	public void addOrUpdateWord(String word,ProductQueryResult result) {
		try{
		if (null == word || word.isEmpty())
		return;
		if(!AutokeyConvernt.isValidate(word)) {
			return;
		}
		word = AutokeyConvernt.withOneSpace(word);
		Long rsize = result.getTotal();
		String srcWord = result.getConstrutor().getSrcWord();
		String newWord = result.getConstrutor().getNewWord();
		String wordCode = AutokeyConvernt.wordCode(word);
		Long addTime = System.currentTimeMillis();
		Long endTime = System.currentTimeMillis();
		MgrSearchwordLog searchWord = new MgrSearchwordLog();
		Date date = new Date();
		searchWord.setWord(word);
		searchWord.setSrcWord(srcWord);
		searchWord.setNewWord(newWord);
		searchWord.setWordCode(wordCode);
		searchWord.setRsCount(rsize);
		searchWord.setAddTime(addTime);
		searchWord.setEndTime(endTime);
		searchWord.setDayDate(date);
	    List<MgrSearchwordLog> num = searchwordLogMapper.selectByWordAndDate(searchWord);
		if(num.size() > 0){
		   searchwordLogMapper.updateWord(searchWord);
		}else{
		searchWord.setSearchCount(Long.parseLong("1"));
		searchwordLogMapper.addWord(searchWord);
	    }
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		}

}

