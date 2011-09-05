package com.tuqianyi.font;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tuqianyi.image.ImageUtils;

public class FontProvider {

	static Logger _log = Logger.getLogger(FontProvider.class.getName());
	
	private Map<String, FontImage> fonts;
	
	public FontProvider(String root)
	{
		_log.info("root: " + root);
		fonts = new HashMap<String, FontImage>();
		try {
			fonts.put("simhei", createFontImage("simhei", root + "fonts/simhei.ttf"));
			fonts.put("simkai", createFontImage("simkai", root + "fonts/simkai.ttf"));
			fonts.put("simsun", createFontImage("simsun", root + "fonts/simsun.ttc"));
			fonts.put("msyh", createFontImage("simsun", root + "fonts/msyh.ttf"));
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
		font = font.deriveFont(Font.BOLD, 22);
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
//		image = g.getDeviceConfiguration().createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
//		g.dispose();
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
 
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, 1, 1);
		g.fill(rect);
		g.dispose();
		
		g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		_log.info("text.size: " + width + ", " + height);
		image = ImageUtils.resize(image, width, height);
		image = ImageUtils.pressText(image, text, font, 
				Color.CYAN, null, 0, 0, 0, 1F);
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
