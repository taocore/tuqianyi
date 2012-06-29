package com.tuqianyi.action;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;

public class ProgressAction extends ActionBase{
	
	public String execute() throws Exception {
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (session != null)
		{
			int total = (Integer) session.get(TOTAL);
			int processed = (Integer) session.get(PROCESSED);
			if (total == processed)
			{
				return "finish";
			}
		}
		return SUCCESS;
	}
}
