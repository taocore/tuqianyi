package com.tuqianyi.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.LabelCategory;

public class LabelsAction extends ActionBase {

	private List<LabelCategory> labelCategories;
	private List<ImageLabel> customLabels;
	
	public String execute()
	{
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
		try
		{
			setCustomLabels(Dao.INSTANCE.getCustomLabels(getUser()));
		}
		catch (Exception e)
		{
			error(e);
		}
		_log.info("categories: " + labelCategories);
		return SUCCESS;
	}

	public void setLabelCategories(List<LabelCategory> labelCategories) {
		this.labelCategories = labelCategories;
	}

	public List<LabelCategory> getLabelCategories() {
		return labelCategories;
	}

	public void setCustomLabels(List<ImageLabel> customLabels) {
		this.customLabels = customLabels;
	}

	public List<ImageLabel> getCustomLabels() {
		return customLabels;
	}
}
