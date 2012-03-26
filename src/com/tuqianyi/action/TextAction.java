package com.tuqianyi.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import com.opensymphony.xwork2.ActionContext;
import com.tuqianyi.font.FontProvider;
import com.tuqianyi.model.TextLabel;

public class TextAction extends ActionBase {

	private TextLabel label;
	private InputStream stream;
	
	public String execute() throws Exception
	{ 
		_log.info("label: " + label);
		//set encoding by Connector attribute URIEncoding="UTF-8" in server.xml 
//		label.setText(new String(label.getText().getBytes("ISO-8859-1"),"UTF-8"));
//		_log.info("fixed: " + label.getText());
		try
		{
			BufferedImage image = FontProvider.getInstance().createText(label.getText(), label.getFont(), label.getColor(), label.getBackground(), label.getStyle(), label.getLine(), label.getBorderWidth(), label.getAngle(), label.isVertical());
			Map<String, Object> session = ActionContext.getContext().getSession();
			String mid = label.getId();
			session.put(mid, image);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ImageIO.write(image, "png", out);
	        stream = new ByteArrayInputStream(out.toByteArray());
		}
		catch (Exception e)
		{
			error(e);
		}
		return SUCCESS;
	}

	public void setLabel(TextLabel label) {
		this.label = label;
	}

	public TextLabel getLabel() {
		return label;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public InputStream getStream() {
		return stream;
	}
}
