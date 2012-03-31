package com.tuqianyi.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.taobao.api.response.ItemsInventoryGetResponse;
import com.taobao.api.response.ItemsOnsaleGetResponse;
import com.tuqianyi.Constants;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.ItemsFilter;
import com.tuqianyi.model.Merge;
import com.tuqianyi.service.MainService;
import com.tuqianyi.service.MergeTask;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergeByCategoriesAction extends ActionBase{
	
	private ItemsFilter filter;
	private List<Merge> merges;
	private ImageLabel frame;
	
	public String execute() throws Exception
	{
		if (filter == null)
		{
			filter = new ItemsFilter();
		}
		_log.info("filter: " + filter);
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
		if (resultItems != null)
		{
			if (!checkItemsCount(resultItems.size()))
			{
				return Constants.OUT_OF_ALLOWED_ITEMS;
			}
			Connection conn = null;
			try
			{
				conn = DBUtils.getConnection();
				merging(resultItems, conn);
				merge(resultItems, frame, merges);
			}
			catch (Exception e)
			{
				error(e);
			}
			finally
			{
				DBUtils.close(conn, null, null);
			}
		}
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
	
	private boolean checkItemsCount(int processingItemsCount)
	{
		long processedItemsCount;
		try {
			processedItemsCount = Dao.INSTANCE.getMergedItemsCount(getUser());
			int allowedItemsCount = getAllowedItems();
			_log.info("allowed items: " + allowedItemsCount);
			return (processedItemsCount + processingItemsCount <= allowedItemsCount);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		return false;
	}
	
	private void merging(List<Item> items, final Connection conn)
	{
		for (Item item : items)
		{
			if (item.getStatus() != Item.STATUS_NORMAL)
			{
				continue;
			}
			try {
				String picUrl = item.getOldPicUrl();
				if (picUrl == null)
				{
					picUrl = item.getPicUrl();
				}
				Dao.INSTANCE.merging(item, getUser(), picUrl, -1L, conn);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	public String merge(List<Item> items, ImageLabel frame, final List<Merge> merges)
	{
		int count = items.size();
//		updateProgress(count, 0);
		MainService service = new MainService();
		for (final Item item : items)
		{
			if (item.getStatus() != Item.STATUS_NORMAL)
			{
//				increaseProgress();
				continue;
			}
			Runnable task = new MergeTask(item, frame, merges, getSession());
			service.executeInPool(task);
//			task.run();
//			increaseProgress();
		}
		return null;
	}

	public void setFilter(ItemsFilter filter) {
		this.filter = filter;
	}

	public ItemsFilter getFilter() {
		return filter;
	}

	public void setMerges(List<Merge> merges) {
		this.merges = merges;
	}

	public List<Merge> getMerges() {
		return merges;
	}

	public void setFrame(ImageLabel frame) {
		this.frame = frame;
	}

	public ImageLabel getFrame() {
		return frame;
	}

}
