package com.tuqianyi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.LabelCategory;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergingAction extends ActionBase{

	private String numIids;
	private Item item;
	private List<LabelCategory> labelCategories;
	
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
						_log.info("item: " + item);
						if (item != null)
						{
							_log.info("pic_url: " + item.getPicUrl());
						}
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
		labelCategories = new ArrayList<LabelCategory>();
		for (LabelCategory c : LabelCategory.BUILDIN_CATEGORIES)
		{
			try {
				LabelCategory cc = new LabelCategory(c);
				List<ImageLabel> labels = Dao.INSTANCE.getLabels(c.getCategoryID(), getUser());
				cc.setLabels(labels);
				labelCategories.add(cc);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "error", e);
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

	public void setLabelCategories(List<LabelCategory> labelCategories) {
		this.labelCategories = labelCategories;
	}

	public List<LabelCategory> getLabelCategories() {
		return labelCategories;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItem() {
		return item;
	}
}
