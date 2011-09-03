package com.tuqianyi.model;

import java.io.Serializable;

public class Merge implements Serializable{
	
	private Label label;
	private int x;
	private int y;
	private int z;
	private int width = 100;
	private int height = 100;
	
	public void setLabel(Label label) {
		this.label = label;
	}
	
	public Label getLabel() {
		return label;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
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
