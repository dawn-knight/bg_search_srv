package com.mbgo.mybatis.mbstore.bean;

import java.math.BigDecimal;
import java.util.Date;

public class MallSecondGoods {

  private Integer id;

  private String gid;

  private String imgUrl;

  private Integer status;

  private String sCode;

  private BigDecimal secondPrice;

  private Date addTime;

  private Date updateTime;

  private Integer sortOrder;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getGid() {
    return gid;
  }

  public void setGid(String gid) {
    this.gid = gid;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getsCode() {
    return sCode;
  }

  public void setsCode(String sCode) {
    this.sCode = sCode;
  }

  public BigDecimal getSecondPrice() {
    return secondPrice;
  }

  public void setSecondPrice(BigDecimal secondPrice) {
    this.secondPrice = secondPrice;
  }

  public Date getAddTime() {
    return addTime;
  }

  public void setAddTime(Date addTime) {
    this.addTime = addTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

}
