package com.mbgo.mybatis.mbsearch.bean;

import java.util.Date;

import org.joda.time.DateTime;

public class SearchErrorLog {
    private Long id;

    private String productId;

    private Integer logType;

    private String logMsg;

    private Date addTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId == null ? null : productId.trim();
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg == null ? null : logMsg.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
    
    public String getAddTimeStr() {
    	try {
        	return new DateTime(addTime.getTime()).toString("yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
			return "";
		}
    }
}