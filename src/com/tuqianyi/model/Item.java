package com.tuqianyi.model;


public class Item extends com.taobao.api.domain.Item{
	public static final short STATUS_ALL = -1;
	public static final short STATUS_NORMAL = 0;
	public static final short STATUS_READY = 1;
	public static final short STATUS_PENDING = 2;
	public static final short STATUS_FAILED = 3;
	public static final short STATUS_OK = 4;
	
	public static final short ACTION_MERGE = 0;
	public static final short ACTION_RECOVER = 1;
	
	private String detailUrl;
	private String oldPicUrl;
	private short action = ACTION_MERGE;
	private short status = STATUS_NORMAL;
	private String errorMsg;
	private String errorCode;
	
	public Item()
	{
		
	}
	
	public Item(com.taobao.api.domain.Item item)
	{
		this.setNumIid(item.getNumIid());
		this.setTitle(item.getTitle());
		this.setPrice(item.getPrice());
		this.setPicUrl(item.getPicUrl());
		this.setDetailUrl(item.getDetailUrl());
	}
	
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setOldPicUrl(String oldPicUrl) {
		this.oldPicUrl = oldPicUrl;
	}

	public String getOldPicUrl() {
		return oldPicUrl;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public short getStatus() {
		return status;
	}
	
	public boolean isMerged()
	{
		return oldPicUrl != null;
	}

	public void setAction(short action) {
		this.action = action;
	}

	public short getAction() {
		return action;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
	
	public boolean equals(Object o)
	{
		if (o instanceof Item)
		{
			return getNumIid() == ((Item)o).getNumIid();
		}
		return false;
	}
	
	public int hashCode()
	{
		return ((Long)getNumIid()).hashCode();
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
