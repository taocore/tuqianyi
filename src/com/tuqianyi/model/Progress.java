package com.tuqianyi.model;

import java.io.Serializable;

public class Progress implements Serializable{

	private int total;
	private int processed;
	
	public void setTotal(int total) {
		this.total = total;
	}
	
	public int getTotal() {
		return total;
	}

	public void setProcessed(int processed) {
		this.processed = processed;
	}

	public int getProcessed() {
		return processed;
	}
	
}
