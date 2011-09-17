package com.tuqianyi.model;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Merge implements Serializable{
	
	private ImageLabel imageLabel;
	private TextLabel textLabel;
	private int opacity = 100;
	private float x;
	private float y;
	private int z;
	private int width = 100;
	private int height = 100;
	private BufferedImage image;

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getZ() {
		return z;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setImageLabel(ImageLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

	public ImageLabel getImageLabel() {
		return imageLabel;
	}

	public void setTextLabel(TextLabel textLabel) {
		this.textLabel = textLabel;
	}

	public TextLabel getTextLabel() {
		return textLabel;
	}
	
	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getOpacity() {
		return opacity;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
}
