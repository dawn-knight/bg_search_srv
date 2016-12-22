package com.metersbonwe.pcs.bean;

public class CatIdCatNameLevelMapping {
	private String catId;
	private String catName;
    private String parentId;
    private int level;
    
	public CatIdCatNameLevelMapping() {
		super();
	}
	public CatIdCatNameLevelMapping(String catId, String catName,
			String parentId, int level) {
		super();
		this.catId = catId;
		this.catName = catName;
		this.parentId = parentId;
		this.level = level;
	}
	public String getCatId() {
		return catId;
	}
	public void setCatId(String catId) {
		this.catId = catId;
	}
	public String getCatName() {
		return catName;
	}
	public void setCatName(String catName) {
		this.catName = catName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
    
}
