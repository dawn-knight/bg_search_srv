package com.mbgo.mybatis.mbsearch.bean;

import java.util.Date;

import com.mbgo.search.autokey.KeywordBean;

public class MgrDicKeyword {
    private Long id;

    private String word;

    private Long addTime;

    private Date lastUpdate;

    private Integer weight;
    
	private float newWeigh;
	
    public MgrDicKeyword(String word, Integer weight, float newWeigh) {
		super();
		this.word = word;
		this.weight = weight;
		this.newWeigh = newWeigh;
	}
	public MgrDicKeyword() {
		
	}
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

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

	public float getNewWeigh() {
		return newWeigh;
	}

	public void setNewWeigh(float newWeigh) {
		this.newWeigh = newWeigh;
	}
    
	public KeywordBean convert() {
		return new KeywordBean(word, weight);
	}
}