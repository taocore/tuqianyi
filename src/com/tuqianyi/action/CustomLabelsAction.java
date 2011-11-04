package com.tuqianyi.action;

import java.util.List;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;

public class CustomLabelsAction extends ActionBase{

	private List<ImageLabel> customLabels;
	
	public String execute()
	{
		try
		{
			setCustomLabels(Dao.INSTANCE.getCustomLabels(getUser()));
		}
		catch (Exception e)
		{
			error(e);
		}
		return SUCCESS;
	}

	public void setCustomLabels(List<ImageLabel> customLabels) {
		this.customLabels = customLabels;
	}

	public List<ImageLabel> getCustomLabels() {
		return customLabels;
	}
}
