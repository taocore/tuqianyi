package com.tuqianyi.action.admin;

import java.util.ArrayList;
import java.util.List;

import com.tuqianyi.action.ActionBase;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.Item;
import com.tuqianyi.utils.PagingOption;
import com.tuqianyi.utils.PagingResult;

public class UserItemsAction extends ActionBase{

	private String nick;
	private PagingOption option;
	private PagingResult<Item> pagingItems;
	
	public String execute() throws Exception
	{
		_log.info("nickkkkkkkkkkkkkkkkkkk: " + nick);
		if (option == null)
		{
			option = new PagingOption();
			option.setLimit(50);
		}
		List<Item> list = new ArrayList<Item>();
		long total = Dao.INSTANCE.getMergedItems(nick, Item.STATUS_OK, option.getOffset(), option.getLimit(), list);
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

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}
}
