package com.tuqianyi.action;

import java.util.List;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.LabelCategory;

public class StuffLabelsAction extends ActionBase {

	private List<ImageLabel> stuffLabels;
	
	public String execute()
	{
		try
		{
			stuffLabels = Dao.INSTANCE.getLabels(LabelCategory.CATEGORY_ID_STUFF, getUser());
		}
		catch (Exception e)
		{
			error(e);
		}
		return SUCCESS;
	}

	public void setStuffLabels(List<ImageLabel> stuffLabels) {
		this.stuffLabels = stuffLabels;
	}

	public List<ImageLabel> getStuffLabels() {
		return stuffLabels;
	}
}
