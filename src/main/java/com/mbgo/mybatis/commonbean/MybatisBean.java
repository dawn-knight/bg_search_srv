/*
 * 2014-9-28 上午11:19:27 吴健 HQ01U8435
 */

package com.mbgo.mybatis.commonbean;

import java.util.ArrayList;
import java.util.List;

public class MybatisBean {

  private long first = -1;
  private long max = -1;
  private String pUuid;
  private String channelCode;
  private String pid;

  private String bt;
  private String et;

  public String getpUuid() {
    return pUuid;
  }

  public void setpUuid(String pUuid) {
    this.pUuid = pUuid;
  }

  public String getChannelCode() {
    return channelCode;
  }

  public void setChannelCode(String channelCode) {
    this.channelCode = channelCode;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  private List<String> params = new ArrayList<String>();

  public MybatisBean() {

  }

  public MybatisBean(long first, long max) {
    super();
    this.first = first;
    this.max = max;
  }

  public MybatisBean(List<String> params) {
    super();
    this.params = params;
  }

  public long getFirst() {
    return first;
  }

  public void setFirst(long first) {
    this.first = first;
  }

  public long getMax() {
    return max;
  }

  public void setMax(long max) {
    this.max = max;
  }

  public List<String> getParams() {
    return params;
  }

  public void setParams(List<String> params) {
    this.params = params;
  }

  public String getBt() {
    return bt;
  }

  public void setBt(String bt) {
    this.bt = bt;
  }

  public String getEt() {
    return et;
  }

  public void setEt(String et) {
    this.et = et;
  }
}
