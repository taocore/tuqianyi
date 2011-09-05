package com.tuqianyi.font;

import java.awt.Font;
import java.awt.image.BufferedImage;

public class FontImage {
	
	private String name;
	private Font font;
	private BufferedImage image;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
}
