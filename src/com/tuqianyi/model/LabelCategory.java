package com.tuqianyi.model;

import java.io.Serializable;
import java.util.List;

import com.tuqianyi.model.ImageLabel;

public class LabelCategory implements Serializable{
	
	public static final LabelCategory REMAI = new LabelCategory();
	public static final LabelCategory XINPIN = new LabelCategory();
	public static final LabelCategory TEJIA = new LabelCategory();
	public static final LabelCategory DAZHE = new LabelCategory();
	public static final LabelCategory JIERI = new LabelCategory();
	public static final LabelCategory QITA = new LabelCategory();
	public static final LabelCategory DINGZHI = new LabelCategory();
	public static final LabelCategory BAOYOU = new LabelCategory();
	public static final LabelCategory ZHENGPIN = new LabelCategory();
	static {
		REMAI.setCategoryID(1);
		REMAI.setName("热卖");
		XINPIN.setCategoryID(2);
		XINPIN.setName("新品");
		TEJIA.setCategoryID(3);
		TEJIA.setName("特价");
		DAZHE.setCategoryID(4);
		DAZHE.setName("打折");
		JIERI.setCategoryID(5);
		JIERI.setName("节日");
		QITA.setCategoryID(6);
		QITA.setName("其它");
		DINGZHI.setCategoryID(7);
		DINGZHI.setName("定制");
		BAOYOU.setCategoryID(8);
		BAOYOU.setName("包邮");
		ZHENGPIN.setCategoryID(9);
		ZHENGPIN.setName("正品");
	}
	
	public static final LabelCategory[] BUILDIN_CATEGORIES = {REMAI, BAOYOU, ZHENGPIN, XINPIN, TEJIA, DAZHE, JIERI, QITA};

	public static final long CATEGORY_ID_STUFF = 10;
	
	private long categoryID;
	private String name;
	private List<ImageLabel> labels;
	
	public LabelCategory()
	{
		
	}

	public LabelCategory(LabelCategory c)
	{
		this.categoryID = c.getCategoryID();
		this.name = c.getName();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setLabels(List<ImageLabel> labels) {
		this.labels = labels;
	}

	public List<ImageLabel> getLabels() {
		return labels;
	}
}
