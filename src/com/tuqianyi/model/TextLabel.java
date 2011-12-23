package com.tuqianyi.model;

import java.awt.Font;


public class TextLabel extends Label{
	
	public static final String DEFAULT_TEXT = "狂销";
	public static final String DEFAULT_COLOR = "#ff0000";
	
	private static final String TOKEN_PRICE = "#价格#";
	public static final int LINE_NONE = 0;
	public static final int LINE_UNDER = 1;
	public static final int LINE_THROUGH = 2;
	public static final int LINE_SLASH = 3;
	
	private String id;
	private String text = DEFAULT_TEXT;
	private String font = "simhei";
	private int fontSize = 18; //normal font size = 16px = 12pt
	private String color = DEFAULT_COLOR;
	private String background;
	private int angle;
	private int style = Font.PLAIN;
	private int line = LINE_NONE;
	private int borderWidth;
	private int outLine;
	private String outLineColor = "#ffffff";
	private boolean isVertical;
	
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
		float fPrice = Float.parseFloat(price);
		String trunkPrice = String.valueOf(Math.round(fPrice));
		String text = this.text.replace(TOKEN_PRICE, trunkPrice);
		String[] tmp = text.split("#", 3);
		if (tmp.length != 3)
		{
			return text;
		}
		String token = tmp[1].substring(0, tmp[1].lastIndexOf("折"));
		try
		{
			float off = Float.parseFloat(token);
			double offPrice = fPrice * off * 0.1;
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
		sb.append("angle: ").append(this.angle);
		return sb.toString();
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getStyle() {
		return style;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setOutLine(int outLine) {
		this.outLine = outLine;
	}

	public int getOutLine() {
		return outLine;
	}

	public void setOutLineColor(String outLineColor) {
		this.outLineColor = outLineColor;
	}

	public String getOutLineColor() {
		return outLineColor;
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
	}

	public boolean isVertical() {
		return isVertical;
	}
}
