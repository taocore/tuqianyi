package com.tuqianyi.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.util.ServletContextAware;

import com.tuqianyi.model.FrameLabel;
import com.tuqianyi.model.ImageLabel;

public class FramesAction extends ActionBase implements ServletContextAware{

	private ServletContext context;
	private List<FrameLabel> frameLabels;
	
	public String execute()
	{
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
