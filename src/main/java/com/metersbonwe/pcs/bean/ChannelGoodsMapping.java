package com.metersbonwe.pcs.bean;

public class ChannelGoodsMapping {

	private String sellerCode;
	private String channelCode;
    private String sysCode;
    private Integer saleCount;
    private Integer saleCountWeek;
    private Integer saleCountMonth;
    
	public ChannelGoodsMapping() {
	}
	public ChannelGoodsMapping(String sellerCode, String channelCode,
			String sysCode, Integer saleCount, Integer saleCountWeek,
			Integer saleCountMonth) {
		super();
		this.sellerCode = sellerCode;
		this.channelCode = channelCode;
		this.sysCode = sysCode;
		this.saleCount = saleCount;
		this.saleCountWeek = saleCountWeek;
		this.saleCountMonth = saleCountMonth;
	}
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getSysCode() {
		return sysCode;
	}
	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}
	public Integer getSaleCount() {
		return saleCount;
	}
	public void setSaleCount(Integer saleCount) {
		this.saleCount = saleCount;
	}
	public Integer getSaleCountWeek() {
		return saleCountWeek;
	}
	public void setSaleCountWeek(Integer saleCountWeek) {
		this.saleCountWeek = saleCountWeek;
	}
	public Integer getSaleCountMonth() {
		return saleCountMonth;
	}
	public void setSaleCountMonth(Integer saleCountMonth) {
		this.saleCountMonth = saleCountMonth;
	}
    
    
}
