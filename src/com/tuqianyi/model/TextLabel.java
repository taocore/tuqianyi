package com.tuqianyi.model;

import java.awt.Font;


public class TextLabel extends Label{
	
	private static final String TOKEN_PRICE = "#价格#";
	
	private String id;
	private String text = "热卖";
	private String font = "simhei";
	private int fontSize = 18; //normal font size = 16px = 12pt
	private String color = "#ff0000";
	private String background;
	private int angle;
	private int style = Font.PLAIN;
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFont() {
		return font;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getBackground() {
		return background;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getAngle() {
		return angle;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public boolean hasToken()
	{
		return (this.text.indexOf(TOKEN_PRICE) >= 0) || (this.text.indexOf("折#") >= 0);
	}
	
	public String getParseText(String price)
	{
		String text = this.text.replace(TOKEN_PRICE, price);
		String[] tmp = text.split("#", 3);
		if (tmp.length != 3)
		{
			return text;
		}
		String token = tmp[1].substring(0, tmp[1].lastIndexOf("折"));
		try
		{
			float off = Float.parseFloat(token);
			double offPrice = Double.parseDouble(price) * off * 0.1;
			long round = Math.round(offPrice);
			String s = String.valueOf(round);
			text  = tmp[0] + s + tmp[2];
		}
		catch (NumberFormatException e)
		{
			
		}
		return text;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("text: ").append(this.text);
		return sb.toString();
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getStyle() {
		return style;
	}
}
