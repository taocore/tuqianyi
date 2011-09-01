package com.tuqianyi.model;

public class ImageLabel extends Label{
	
	public static final String[] EXTENSIONS = {"png", "jpg", "gif"};
	
	public static final short STORAGE_FILE = 0;
	public static final short STORAGE_URL = 1;
	public static final short STORAGE_BLOB = 2;
	
	private long id = -1;
	private long categoryID;
	private String src;
	private int opacity = 100;

	public void setSrc(String src) {
		this.src = src;
	}

	public String getSrc() {
		return src;
	}

	public void setCategoryID(long categoryID) {
		this.categoryID = categoryID;
	}

	public long getCategoryID() {
		return categoryID;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	
	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getOpacity() {
		return opacity;
	}
	
	public static boolean isLocal(String src)
	{
		return !src.startsWith("http://");
	}
}
