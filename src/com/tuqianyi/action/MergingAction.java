package com.tuqianyi.action;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergingAction extends ActionBase {

	private String numIids;
	private Item item;
	
	public String execute()
	{
		_log.info("numIids: " + numIids);
		if (numIids != null)
		{
			String[] iids = StringUtils.split(numIids, ',');
			if (iids.length == 1)
			{
				try {
					ItemGetResponse rsp = TaobaoProxy.getItem(getSessionId(), Long.parseLong(iids[0]));
					if (rsp.isSuccess())
					{
						item = rsp.getItem();
					}
					else
					{
						error(rsp);
					}
				} catch (NumberFormatException e) {
					error(e);
				} catch (ApiException e) {
					error(e);
				}
			}
		}
		return SUCCESS;
	}

	public void setNumIids(String numIids) {
		this.numIids = numIids;
	}

	public String getNumIids() {
		return numIids;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}
}
