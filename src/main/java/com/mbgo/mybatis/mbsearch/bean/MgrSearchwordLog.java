package com.mbgo.mybatis.mbsearch.bean;

import java.util.Date;

public class MgrSearchwordLog {
    private Long id;

	private String word;

	private String srcWord;
	private String newWord;
	private String wordCode;

	private Long searchCount;
	private Long rsCount;

	private Long addTime;
	private Long endTime;

	private Date dayDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word == null ? null : word.trim();
	}
	
	
	public String getSrcWord() {
		return srcWord;
	}

	public void setSrcWord(String srcWord) {
		this.srcWord = srcWord == null ? null : srcWord.trim();
	}

	public String getNewWord() {
		return newWord;
	}

	public void setNewWord(String newWord) {
		this.newWord = newWord == null ? null : newWord.trim();
	}

	public String getWordCode() {
		return wordCode;
	}

	public void setWordCode(String wordCode) {
		this.wordCode = wordCode == null ? null : wordCode.trim();
	}

	public Long getSearchCount() {
		return searchCount;
	}
	public void setSearchCount(Long searchCount) {
		this.searchCount = searchCount;
	}

	public Long getRsCount() {
		return rsCount;
	}

	public void setRsCount(Long rsCount) {
		this.rsCount = rsCount;
	}

	public Long getAddTime() {
		return addTime;
	}

	public void setAddTime(Long addTime) {
		this.addTime = addTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Date getDayDate() {
		return dayDate;
	}

	public void setDayDate(Date dayDate) {
		this.dayDate = dayDate;
	}

	

}