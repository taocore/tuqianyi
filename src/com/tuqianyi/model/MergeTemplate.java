package com.tuqianyi.model;

import java.util.List;

public class MergeTemplate {

	private List<Merge> merges;
	private ImageLabel frame;
	
	public void setMerges(List<Merge> merges) {
		this.merges = merges;
	}
	
	public List<Merge> getMerges() {
		return merges;
	}

	public void setFrame(ImageLabel frame) {
		this.frame = frame;
	}

	public ImageLabel getFrame() {
		return frame;
	}
}
