package com.tuqianyi.action;

import com.tuqianyi.model.ItemsFilter;

public class RecoverByCategoriesAction extends ActionBase{

	private ItemsFilter filter;
	
	public String execute()
	{
		return SUCCESS;
	}

	public void setFilter(ItemsFilter filter) {
		this.filter = filter;
	}

	public ItemsFilter getFilter() {
		return filter;
	}

}
