package com.tuqianyi.model;

import java.io.Serializable;

public class Label implements Serializable{
	
	private int opacity = 100;

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getOpacity() {
		return opacity;
	}
}
