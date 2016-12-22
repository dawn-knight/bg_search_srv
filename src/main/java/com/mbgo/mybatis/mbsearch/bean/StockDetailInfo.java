package com.mbgo.mybatis.mbsearch.bean;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.banggo.stockcenter.client.dataobject.PlatformStock;
import com.mbgo.search.core.dataetl.stock.SkuSpliter;

public class StockDetailInfo {

  private String sku;
  private String productUuid;
  private String productId;

  private String colorCodeId;

  private Integer stock;

  private String sizeCode;

  private Date addTime;

  private Date updateTime;

  public String getProductUuid() {
    return productUuid;
  }

  public void setProductUuid(String productUuid) {
    this.productUuid = productUuid;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku == null ? null : sku.trim();
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId == null ? null : productId.trim();
  }

  public String getColorCodeId() {
    return colorCodeId;
  }

  public void setColorCodeId(String colorCodeId) {
    this.colorCodeId = colorCodeId == null ? null : colorCodeId.trim();
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock == null ? 0 : stock;
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

  public StockDetailInfo() {

  }

  public String getSizeCode() {
    return sizeCode;
  }

  public void setSizeCode(String sizeCode) {
    this.sizeCode = sizeCode;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StockDetailInfo [sku=").append(sku)
        .append(", productId=").append(productId)
        .append(", colorCodeId=").append(colorCodeId)
        .append(", stock=").append(stock).append(", sizeCode=")
        .append(sizeCode).append(", addTime=").append(addTime)
        .append(", updateTime=").append(updateTime).append("]");
    return builder.toString();
  }

  public StockDetailInfo(PlatformStock s) {
    String sku = s.getSku();
    if (StringUtils.isBlank(sku)) {
      sku = s.getBarcodeId();
    }

    SkuSpliter ss = new SkuSpliter(sku);
    stock = s.getStock();
    // private String sku;
    this.sku = ss.getSku();
    // private String productId;
    this.productId = ss.getPid();
    // private String colorCodeId;
    this.colorCodeId = ss.getColorId();
    // private Integer stock;
    // private Date addTime;
    this.addTime = new Date();
    // private Date updateTime;
    this.updateTime = new Date();

    this.sizeCode = ss.getSizeCode();
  }
}