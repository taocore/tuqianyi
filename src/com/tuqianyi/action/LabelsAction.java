package com.tuqianyi.action;

import java.util.ArrayList;
import java.util.List;

import com.tuqianyi.db.Dao;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.LabelCategory;

public class LabelsAction extends ActionBase {

	private List<LabelCategory> labelCategories;
	
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
				error(e);
			}
		}
		return SUCCESS;
	}

	public void setLabelCategories(List<LabelCategory> labelCategories) {
		this.labelCategories = labelCategories;
	}

	public List<LabelCategory> getLabelCategories() {
		return labelCategories;
	}
}
