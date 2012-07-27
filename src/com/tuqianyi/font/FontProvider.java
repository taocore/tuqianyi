package com.tuqianyi.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.tuqianyi.image.ImageUtils;
import com.tuqianyi.model.TextLabel;
import com.tuqianyi.servlet.FontsServlet;

public class FontProvider {

	static Logger _log = Logger.getLogger(FontProvider.class.getName());
	private static FontProvider instance = new FontProvider();
	private BufferedImage canvas;
	
	private Map<String, FontImage> fonts;
	
	private FontProvider()
	{
		String root = FontsServlet.getRootPath();
		String fontDir = root + "fonts/";
		_log.info("root: " + root);
		fonts = new HashMap<String, FontImage>();
		try {
			fonts.put("simhei", createFontImage("simhei", fontDir + "simhei.ttf"));
			fonts.put("simkai", createFontImage("simkai", fontDir + "simkai.ttf"));
//			fonts.put("simsun", createFontImage("simsun", fontDir + "simsun.ttc"));
//			fonts.put("msyh", createFontImage("msyh", fontDir + "msyh.ttf"));
			fonts.put("hkst", createFontImage("hkst", fontDir + "hkst.ttf"));
			fonts.put("mnjccy", createFontImage("mnjccy", fontDir + "mnjccy.ttf"));
			fonts.put("mnjdh", createFontImage("mnjdh", fontDir + "mnjdh.ttf"));
			fonts.put("mnxf", createFontImage("mnxf", fontDir + "mnxf.ttf"));
			fonts.put("mshkj", createFontImage("mshkj", fontDir + "mshkj.ttf"));
			fonts.put("hdzb_46", createFontImage("hdzb_46", fontDir + "hdzb_46.ttf"));
			fonts.put("stliti", createFontImage("stliti", fontDir + "stliti.ttf"));
			fonts.put("jdjykd", createFontImage("jdjykd", fontDir + "jdjykd.ttf"));
			fonts.put("sszhjt", createFontImage("sszhjt", fontDir + "sszhjt.ttf"));
			fonts.put("ygyxsziti", createFontImage("ygyxsziti", fontDir + "ygyxsziti.ttf"));
			fonts.put("mnjzy", createFontImage("mnjzy", fontDir + "mnjzy.ttf"));
//			fonts.put("stfajcgbxs", createFontImage("stfajcgbxs", fontDir + "stfajcgbxs.ttf"));
			fonts.put("mnjpl", createFontImage("mnjpl", fontDir + "mnjpl.ttf"));
//			fonts.put("wdztggt", createFontImage("wdztggt", fontDir + "wdztggt.ttf"));
			fonts.put("jdzytj", createFontImage("jdzytj", fontDir + "jdzytj.ttf"));
			fonts.put("mnjhpt", createFontImage("mnjhpt", fontDir + "mnjhpt.ttf"));
//			fonts.put("jmgyftbz", createFontImage("jmgyftbz", fontDir + "jmgyftbz.ttf"));
			fonts.put("mnjjz", createFontImage("mnjjz", fontDir + "mnjjz.ttf"));
			fonts.put("sxsrst", createFontImage("sxsrst", fontDir + "sxsrst.ttf"));
			fonts.put("fzmwt", createFontImage("fzmwt", fontDir + "fzmwt.ttf"));
			fonts.put("mnjcd", createFontImage("mnjcd", fontDir + "mnjcd.ttf"));
			fonts.put("mnjbaiqi", createFontImage("mnjbaiqi", fontDir + "mnjbaiqi.ttf"));
			fonts.put("mnjboqing", createFontImage("mnjboqing", fontDir + "mnjboqing.ttf"));
			fonts.put("mnjbwks", createFontImage("mnjbwks", fontDir + "mnjbwks.ttf"));
		} catch (FontFormatException e) {
			_log.log(Level.SEVERE, "", e);
		} catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	public static FontProvider getInstance()
	{
		return instance;
	}
	
	private FontImage createFontImage(String name, String path) throws FontFormatException, IOException
	{
		FontImage fontImage = new FontImage();
		Font f = Font.createFont(Font.TRUETYPE_FONT, new File(path));
		fontImage.setName(name);
		fontImage.setFont(f);
		fontImage.setImage(createTextLabel(TextLabel.DEFAULT_TEXT, f));
		return fontImage;
	}
	
	public BufferedImage createTextLabel(String text, Font font) throws IOException
	{
		BufferedImage image = getCanvas();
		font = font.deriveFont(Font.PLAIN, 36);
		
		Graphics2D g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		g.dispose();
		_log.info("text.size: " + width + ", " + height);
		image = ImageUtils.resize(image, width, height);
		image = ImageUtils.pressText(image, text, font, 
				Color.blue, null, TextLabel.LINE_NONE, null, 0, 0, 0, 1F, false);
		return image;
	}

	public BufferedImage createText(String text, String font, String color, String backColor, int style, int line, int borderWidth, int angle, boolean isVertical) throws IOException
	{
		_log.info("iiiiiiiiiiiiiiiiisvertical:" + isVertical);
		return createText(text, getFont(font), color, backColor, style, line, borderWidth, angle, isVertical);
	}
	
	public BufferedImage createText(String text, Font font, String color, String backColor, int style, int line, int borderWidth, int angle, boolean isVertical) throws IOException
	{
		BufferedImage image = getCanvas();
		font = font.deriveFont(style, 72);
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
				foreground, background, line, foreground, borderWidth, 0, 0, 1F, isVertical);
		if (angle != 0)
		{
			if (angle < 0)
			{
				angle += 360;
			}
			image = ImageUtils.rotate(image, angle, null);
		}
		return image;
	}
	
	public Font getFont(String fontName)
	{
		return fonts.get(fontName).getFont();
	}
	
	public BufferedImage getFontImage(String name)
	{
		return fonts.get(name).getImage();
	}
	
	public BufferedImage getCanvas()
	{
		if (canvas == null)
		{
			try {
				canvas = ImageIO.read(new File(FontsServlet.getRootPath() + "images/clear.png"));
			} catch (IOException e) {
				_log.log(Level.SEVERE, "", e);
			}
		}
		return canvas;
	}
}
