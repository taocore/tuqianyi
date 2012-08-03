package com.tuqianyi.action;

import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;

import com.opensymphony.xwork2.ActionContext;

public class LogoutAction extends ActionBase{

	public String execute()
	{
		Map session = ActionContext.getContext().getSession();
		if (session instanceof SessionMap)
		{
			((SessionMap) session).invalidate();
		}
		return SUCCESS;
	}
}
