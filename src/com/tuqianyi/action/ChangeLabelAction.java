package com.tuqianyi.action;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.Constants;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.Item;
import com.tuqianyi.model.Merge;
import com.tuqianyi.service.MergeTask;
import com.tuqianyi.service.MergeTask2;
import com.tuqianyi.taobao.TaobaoProxy;

public class ChangeLabelAction extends ActionBase{

	private String numIids;
	private List<Merge> merges;
	private ImageLabel frame;
	
	public String execute() throws Exception {
		if (numIids != null)
		{
			final String[] iids = StringUtils.split(numIids, ',');
			if (!checkItemsCount(iids.length))
			{
				return Constants.OUT_OF_ALLOWED_ITEMS;
			}
			updateProgress(iids.length, 0);
			Connection conn = null;
			try
			{
				conn = DBUtils.getConnection();
				final List<Item> items = getItems(iids, conn);
				merging(items, conn);
				merge(items, frame, merges);
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
	
	private List<Item> getItems(String[] numIids, Connection conn)
	{
		List<Item> items = new ArrayList<Item>();
		for (String numIid : numIids)
		{
			try {
				Item item = Dao.INSTANCE.getMergedItem(Long.parseLong(numIid), conn);
				if (item == null || item.getPicUrl() == null)
				{
					ItemGetResponse rsp = TaobaoProxy.getItem(getSessionId(), Long.parseLong(numIid));
					if (rsp.isSuccess())
					{
						com.taobao.api.domain.Item i = rsp.getItem();
						item = new Item(i);
					}
					else
					{
						error(rsp);
					}
				}
				if (item != null)
				{
					items.add(item);
				}
			} catch (NumberFormatException e) {
				error(e);
			} catch (ApiException e) {
				error(e);
			} 
			catch (Exception e)
			{
				error(e);
			}
		}
		return items;
	}
	
	private void merging(List<Item> items, final Connection conn)
	{
		for (Item item : items)
		{
//			if (item.getStatus() != Item.STATUS_NORMAL)
//			{
//				continue;
//			}
			String oldPic = item.getOldPicUrl();
			if (oldPic == null)
			{
				oldPic = item.getPicUrl();
			}
			try {
				Dao.INSTANCE.merging(item, getUser(), oldPic, -1L, conn);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "", e);
			}
		}
	}
	
	public String merge(List<Item> items, ImageLabel frame, final List<Merge> merges)
	{
		int count = items.size();
		updateProgress(count, 0);
		for (final Item item : items)
		{
//			if (item.getStatus() != Item.STATUS_NORMAL)
//			{
//				increaseProgress();
//				continue;
//			}
			Runnable task = new MergeTask2(item, frame, merges, getSession());
//			executeInPool(task);
			task.run();
			increaseProgress();
		}
		return null;
	}

	private boolean checkItemsCount(int processingItemsCount)
	{
//		long processedItemsCount;
//		try {
//			processedItemsCount = Dao.INSTANCE.getMergedItemsCount(getUser());
//			int allowedItemsCount = getAllowedItems();
//			_log.info("allowed items: " + allowedItemsCount);
//			return (processedItemsCount + processingItemsCount <= allowedItemsCount);
//		} catch (Exception e) {
//			_log.log(Level.SEVERE, "", e);
//		}
//		return false;
		return true;
	}

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
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
