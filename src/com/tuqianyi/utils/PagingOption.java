package com.tuqianyi.utils;

public class PagingOption {

	private int offset;
	private int limit = 50;
	
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getLimit() {
		return limit;
	}
	
	public int getCurrentPage()
	{
		return getOffset() / getLimit();
	}
	
	public String toString()
	{
		return "offset: " + offset + ", limit: " + limit;
	}
}
