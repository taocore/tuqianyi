package com.tuqianyi.action.admin;

import java.util.ArrayList;
import java.util.List;

import com.tuqianyi.action.ActionBase;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.Item;
import com.tuqianyi.utils.PagingOption;
import com.tuqianyi.utils.PagingResult;

public class UsersAction extends ActionBase{

	private PagingOption option;
	private PagingResult<Item> pagingItems;
	
	public String execute() throws Exception
	{
		if (option == null)
		{
			option = new PagingOption();
			option.setLimit(50);
		}
		List<Item> list = new ArrayList<Item>();
		long total = Dao.INSTANCE.getHistoryItems(option.getOffset(), option.getLimit(), list);
		pagingItems = new PagingResult<Item>();
		pagingItems.setItems(list);
		pagingItems.setTotal(total);
		pagingItems.setOption(option);
		return SUCCESS;
	}

	public void setOption(PagingOption option) {
		this.option = option;
	}

	public PagingOption getOption() {
		return option;
	}

	public void setItems(PagingResult<Item> pagingItems) {
		this.pagingItems = pagingItems;
	}

	public PagingResult<Item> getPagingItems() {
		return pagingItems;
	}
}
