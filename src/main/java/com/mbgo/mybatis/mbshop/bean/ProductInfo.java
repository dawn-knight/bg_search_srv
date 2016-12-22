package com.mbgo.mybatis.mbshop.bean;

import java.util.Date;

public class ProductInfo {
    private Integer productId;

    private String storeId;

    private Integer categoryId;

    private Integer brandId;

    private Integer originId;

    private String productCode;

    private String productName;

    private Byte salesModel;

    private Byte salesType;

    private String keywords;

    private String productUrl;

    private Byte productWeight;

    private Byte productLength;

    private Byte productWidth;

    private Byte productHeight;

    private String packageList;

    private String customerService;

    private Byte status;

    private Date updateTime;

    private Date createTime;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId == null ? null : storeId.trim();
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Byte getSalesModel() {
        return salesModel;
    }

    public void setSalesModel(Byte salesModel) {
        this.salesModel = salesModel;
    }

    public Byte getSalesType() {
        return salesType;
    }

    public void setSalesType(Byte salesType) {
        this.salesType = salesType;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl == null ? null : productUrl.trim();
    }

    public Byte getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(Byte productWeight) {
        this.productWeight = productWeight;
    }

    public Byte getProductLength() {
        return productLength;
    }

    public void setProductLength(Byte productLength) {
        this.productLength = productLength;
    }

    public Byte getProductWidth() {
        return productWidth;
    }

    public void setProductWidth(Byte productWidth) {
        this.productWidth = productWidth;
    }

    public Byte getProductHeight() {
        return productHeight;
    }

    public void setProductHeight(Byte productHeight) {
        this.productHeight = productHeight;
    }

    public String getPackageList() {
        return packageList;
    }

    public void setPackageList(String packageList) {
        this.packageList = packageList == null ? null : packageList.trim();
    }

    public String getCustomerService() {
        return customerService;
    }

    public void setCustomerService(String customerService) {
        this.customerService = customerService == null ? null : customerService.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}