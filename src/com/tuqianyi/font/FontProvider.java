package com.tuqianyi.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FontProvider {

	static Logger _log = Logger.getLogger(FontProvider.class.getName());
	
	private Font HEI;
	private Font MSYAHEI;
	private Font KAI;
	private Font SONG;
	
	public FontProvider(String dir)
	{
		try {
			HEI = Font.createFont(Font.TRUETYPE_FONT, new File(dir + "simhei.ttf"));
			MSYAHEI = Font.createFont(Font.TRUETYPE_FONT, new File(dir + "msyh.ttf"));
			KAI = Font.createFont(Font.TRUETYPE_FONT, new File(dir + "simkai.ttf"));
			SONG = Font.createFont(Font.TRUETYPE_FONT, new File(dir + "simsun.ttc"));
		} catch (FontFormatException e) {
			_log.log(Level.SEVERE, "", e);
		} catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
		}
	}

	public Font getFont(String font)
	{
		if ("黑体".equals(font))
		{
			return HEI;
		}
		else if ("楷体".equals(font))
		{
			return KAI;
		}
		else if ("宋体".equals(font))
		{
			return SONG;
		}
		else
		{
			return MSYAHEI;
		}
	}
}
