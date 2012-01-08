package com.tuqianyi.utils;

import java.util.List;

public class PagingResult<T> {

	private List<T> items;
	private long total;
	private PagingOption option;

	public void setItems(List<T> items) {
		this.items = items;
	}

	public List<T> getItems() {
		return items;
	}

	public void setOption(PagingOption option) {
		this.option = option;
	}

	public PagingOption getOption() {
		return option;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getTotal() {
		return total;
	}
	
	/**
	 * 
	 * @return page index, begins from 0
	 */
	public int getCurrentPage()
	{
		return option.getCurrentPage();
	}
	
	public int getTotalPages()
	{
//		return (int)(total / option.getLimit() + 1);
		return getPageCount(total, option.getLimit());
	}
	
	public static int getPageCount(long total, int pageSize)
	{
		return (int)(total / pageSize + (total % pageSize == 0 ? 0 : 1));
	}
}
