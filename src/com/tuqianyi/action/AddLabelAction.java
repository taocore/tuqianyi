package com.tuqianyi.action;

import java.util.logging.Level;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;

public class AddLabelAction extends ActionBase{

	private ImageLabel label;
	
	public String execute()
	{
		try
		{
			long id = Dao.INSTANCE.addLabel(label, getUser());
			label.setId(id);
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
