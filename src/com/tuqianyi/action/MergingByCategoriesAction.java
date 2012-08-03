package com.tuqianyi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taobao.api.response.ItemsInventoryGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.ItemsFilter;
import com.tuqianyi.model.Merge;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergingByCategoriesAction extends ActionBase{

	private ItemsFilter filter;
	
	public String execute() throws Exception
	{
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
				ItemsInventoryGetResponse rsp = TaobaoProxy.getInventory(topSession, 1, 200, filter.getBanner(), filter.getSellerCids(), filter.getKeyWord());
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
				ItemsOnsaleGetResponse rsp = TaobaoProxy.getOnSales(topSession, 1, 200, filter.getSellerCids(), filter.getKeyWord());
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
			total = Dao.INSTANCE.getMergedItems(getUser(), filter.getStatus(), 0, 200, resultItems);
		}
		_log.info("result items: " + resultItems.size());
		_log.info("total: " + total);
		return SUCCESS;
	}
	
	private List<com.tuqianyi.model.Item> getItems(List<com.taobao.api.domain.Item> taobaoItems, ItemsFilter filter) throws Exception
	{
		List<com.tuqianyi.model.Item> items = new ArrayList<com.tuqianyi.model.Item>();
		if (taobaoItems != null)
		{
			Map<Long, com.tuqianyi.model.Item> mergedItems = Dao.INSTANCE.getMergedItems(getUser(), filter.getStatus());
			for (com.taobao.api.domain.Item item : taobaoItems)
			{
				com.tuqianyi.model.Item it = mergedItems.remove(item.getNumIid());
				if (it == null)
				{
					it = new com.tuqianyi.model.Item(item);
				}
				items.add(it);
			}
		}
		return items;
	}

	public void setFilter(ItemsFilter filter) {
		this.filter = filter;
	}

	public ItemsFilter getFilter() {
		return filter;
	}

}
