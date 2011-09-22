package com.tuqianyi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.taobao.api.ApiException;
import com.taobao.api.domain.SellerCat;
import com.taobao.api.response.ItemsInventoryGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;
import com.taobao.api.response.SellercatsListGetResponse;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.ItemsFilter;
import com.tuqianyi.taobao.TaobaoProxy;
import com.tuqianyi.utils.PagingOption;
import com.tuqianyi.utils.PagingResult;

public class ItemsAction extends ActionBase{
	
	private ItemsFilter filter;
	private PagingOption option;
	private PagingResult<com.tuqianyi.model.Item> pagingItems;
	private List<SellerCat> categories;
	
	public String execute() throws Exception {
		if (option == null)
		{
			option = new PagingOption();
			option.setLimit(50);
		}
		if (filter == null)
		{
			filter = new ItemsFilter();
		}
		String topSession = getSessionId();
		List<com.taobao.api.domain.Item> taobaoItems = null;
		List<Item> resultItems = null;
		long total = 0;
		if (filter.getStatus() == Item.STATUS_ALL)
		{
			if (filter.getSaleStatus() == ItemsFilter.STATUS_INVENTORY)
			{
				ItemsInventoryGetResponse rsp = TaobaoProxy.getInventory(topSession, option.getCurrentPage() + 1, option.getLimit(), null, filter.getSellerCids(), filter.getKeyWord());
				if (rsp.isSuccess())
				{
					taobaoItems = rsp.getItems();
					if (taobaoItems != null)
					{
						total = rsp.getTotalResults();
					}
				}
				else
				{
					error(rsp);
				}
			}
			else
			{
				ItemsOnsaleGetResponse rsp = TaobaoProxy.getOnSales(topSession, option.getCurrentPage() + 1, option.getLimit(), filter.getSellerCids(), filter.getKeyWord());
				if (rsp.isSuccess())
				{
					taobaoItems = rsp.getItems();
					if (taobaoItems != null)
					{
						total = rsp.getTotalResults();
					}
				}
				else
				{
					error(rsp);
				}
			}
			_log.info("taobao items: " + (taobaoItems == null ? null : taobaoItems.size()));
			resultItems = getItems(taobaoItems, filter);
		}
		else
		{
			resultItems = new ArrayList<Item>();
			total = Dao.INSTANCE.getMergedItems(getUser(), filter.getStatus(), option.getOffset(), option.getLimit(), resultItems);
		}
		_log.info("result items: " + resultItems.size());
		_log.info("total: " + total);
		pagingItems = new PagingResult<Item>();
		pagingItems.setItems(resultItems);
		pagingItems.setTotal(total);
		pagingItems.setOption(option);
		retriveCategories();
		return SUCCESS;
	}
	
	private List<com.tuqianyi.model.Item> getItems(List<com.taobao.api.domain.Item> taobaoItems, ItemsFilter filter) throws Exception
	{
		List<com.tuqianyi.model.Item> items = new ArrayList<com.tuqianyi.model.Item>();
		if (taobaoItems != null)
		{
			Map<Long, com.tuqianyi.model.Item> mergedItems = Dao.INSTANCE.getMergedItems(getUser());
			for (com.taobao.api.domain.Item item : taobaoItems)
			{
				com.tuqianyi.model.Item it = mergedItems.remove(item.getNumIid());
				if (it == null)
				{
					it = new com.tuqianyi.model.Item();
				}
				copyTo(item, it);
				items.add(it);
			}
		}
		return items;
	}
	
	private void copyTo(com.taobao.api.domain.Item item, com.tuqianyi.model.Item it)
	{
		it.setNumIid(item.getNumIid());
		it.setTitle(item.getTitle());
		it.setPrice(item.getPrice());
		it.setPicUrl(item.getPicUrl());
		it.setDetailUrl(item.getDetailUrl());
	}
	
	private void retriveCategories() throws ApiException
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		String nick = (String)session.get(USER);
		SellercatsListGetResponse catRsp = TaobaoProxy.getSellerCategories(nick);
		categories = catRsp.getSellerCats();
	}
	
	public PagingResult<com.tuqianyi.model.Item> getPagingItems()
	{
		return pagingItems;
	}

	public void setFilter(ItemsFilter filter) {
		this.filter= filter;
	}

	public ItemsFilter getFilter() {
		return filter;
	}
	
	public void setOption(PagingOption option) {
		this.option = option;
	}

	public PagingOption getOption() {
		return option;
	}
	
	public List<SellerCat> getCategories() {
		return categories;
	}
}
