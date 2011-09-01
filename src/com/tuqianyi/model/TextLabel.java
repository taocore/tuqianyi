package com.tuqianyi.model;


public class TextLabel extends Label{
	private String text = "卖疯了！";
	private String font = "黑体";
	private int fontSize = 18; //normal font size = 16px = 12pt
	private String color = "#ff0000";
	private String background;
	private int angle;
	
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
}
