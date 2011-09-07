package com.tuqianyi.action;

import java.util.logging.Level;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;

public class DeleteLabelAction extends ActionBase{

	private ImageLabel label;
	
	public String execute()
	{
		try
		{
			Dao.INSTANCE.deleteLabel(label);
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "error", e);
		}
		return SUCCESS;
	}

	public void setLabel(ImageLabel label) {
		this.label = label;
	}

	public ImageLabel getLabel() {
		return label;
	}
}
