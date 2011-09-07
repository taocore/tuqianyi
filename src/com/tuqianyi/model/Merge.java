package com.tuqianyi.model;

import java.io.Serializable;

public class Merge implements Serializable{
	
	private ImageLabel label;
	private float x;
	private float y;
	private int z;
	private int width = 100;
	private int height = 100;
	
	public void setLabel(ImageLabel label) {
		this.label = label;
	}
	
	public ImageLabel getLabel() {
		return label;
	}

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
}
