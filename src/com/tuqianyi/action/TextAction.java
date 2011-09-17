package com.tuqianyi.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.struts2.util.ServletContextAware;

import com.opensymphony.xwork2.ActionContext;
import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.TextLabel;
import com.tuqianyi.servlet.FontsServlet;

public class TextAction extends ActionBase implements ServletContextAware{

	private ServletContext context;
	private TextLabel label;
	private InputStream stream;
	
	public String execute() throws Exception
	{ 
		_log.info("label: " + label);
//		label.setText(new String(label.getText().getBytes("ISO-8859-1"),"UTF-8"));
//		_log.info("fixed: " + label.getText());
		try
		{
			Font f = FontsServlet.getFontProvider().getFont(label.getFont());
			BufferedImage image = createText(label.getText(), f, label.getColor(), label.getBackground());
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
	
	private BufferedImage createText(String text, Font font, String color, String backColor) throws IOException
	{
		String root = context.getRealPath("/");
		BufferedImage image = ImageIO.read(new File(root + "images/clear.png"));
		font = font.deriveFont(Font.PLAIN, 72);
		Color foreground = Color.decode(color);
		Color background = null;
		if (backColor != null && backColor.length() > 0)
		{
			background = Color.decode(backColor);
		}
		
		Graphics2D g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		g.dispose();
		_log.info("text.size: " + width + ", " + height);
		image = ImageUtils.resize(image, width, height);
		image = ImageUtils.pressText(image, text, font, 
				foreground, background, null, 0, 0, 0, 1F);
		return image;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
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
