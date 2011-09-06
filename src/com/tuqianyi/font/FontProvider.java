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

public class FontProvider {

	static Logger _log = Logger.getLogger(FontProvider.class.getName());
	
	private String root;
	private Map<String, FontImage> fonts;
	
	public FontProvider(String root)
	{
		this.root = root;
		_log.info("root: " + root);
		fonts = new HashMap<String, FontImage>();
		try {
			fonts.put("simhei", createFontImage("simhei", root + "fonts/simhei.ttf"));
			fonts.put("simkai", createFontImage("simkai", root + "fonts/simkai.ttf"));
			fonts.put("simsun", createFontImage("simsun", root + "fonts/simsun.ttc"));
			fonts.put("msyh", createFontImage("msyh", root + "fonts/msyh.ttf"));
			fonts.put("hkst", createFontImage("hkst", root + "fonts/hkst.ttf"));
			fonts.put("mnjccy", createFontImage("mnjccy", root + "fonts/mnjccy.ttf"));
			fonts.put("mnjdh", createFontImage("mnjdh", root + "fonts/mnjdh.ttf"));
			fonts.put("mnxf", createFontImage("mnxf", root + "fonts/mnxf.ttf"));
		} catch (FontFormatException e) {
			_log.log(Level.SEVERE, "", e);
		} catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	private FontImage createFontImage(String name, String path) throws FontFormatException, IOException
	{
		FontImage fontImage = new FontImage();
		Font f = Font.createFont(Font.TRUETYPE_FONT, new File(path));
		fontImage.setName(name);
		fontImage.setFont(f);
		fontImage.setImage(createText("热卖", f));
		return fontImage;
	}
	
	private BufferedImage createText(String text, Font font) throws IOException
	{
		BufferedImage image = ImageIO.read(new File(root + "images/clear.png"));
		font = font.deriveFont(Font.BOLD, 36);
		
		Graphics2D g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		g.dispose();
		_log.info("text.size: " + width + ", " + height);
		image = ImageUtils.resize(image, width, height);
		image = ImageUtils.pressText(image, text, font, 
				Color.blue, null, null, 0, 0, 0, 1F);
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
}
