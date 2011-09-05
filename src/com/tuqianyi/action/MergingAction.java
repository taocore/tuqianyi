package com.tuqianyi.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.util.ServletContextAware;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Item;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.db.Dao;
import com.tuqianyi.model.FrameLabel;
import com.tuqianyi.model.ImageLabel;
import com.tuqianyi.model.LabelCategory;
import com.tuqianyi.taobao.TaobaoProxy;

public class MergingAction extends ActionBase implements ServletContextAware{

	private ServletContext context;
	private String numIids;
	private Item item;
	private List<LabelCategory> labelCategories;
	private List<ImageLabel> customLabels;
	private List<FrameLabel> frameLabels;
	
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
		frameLabels = new ArrayList<FrameLabel>();
		String realRootPath = context.getRealPath("/");
		Collection<File> files = FileUtils.listFiles(new File(realRootPath + "images/frames"), ImageLabel.EXTENSIONS, false);
		String pathPrefix = "images/frames/";
		for (File f : files)
		{
			FrameLabel label = new FrameLabel();
			label.setSrc(pathPrefix + f.getName());
			frameLabels.add(label);
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

	public void setCustomLabels(List<ImageLabel> customLabels) {
		this.customLabels = customLabels;
	}

	public List<ImageLabel> getCustomLabels() {
		return customLabels;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}

	public void setFrameLabels(List<FrameLabel> frameLabels) {
		this.frameLabels = frameLabels;
	}

	public List<FrameLabel> getFrameLabels() {
		return frameLabels;
	}
}
