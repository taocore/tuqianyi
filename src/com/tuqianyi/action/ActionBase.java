package com.tuqianyi.action;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.taobao.api.TaobaoResponse;
import com.tuqianyi.Constants;

public class ActionBase extends ActionSupport implements Constants{
	
	static Logger _log = Logger.getLogger(ActionBase.class.getName());
	
	protected void error(TaobaoResponse rsp)
	{
		_log.info(rsp.getErrorCode() + " - " + rsp.getMsg() + " - " + rsp.getSubCode() + " - " + rsp.getSubMsg());
	}
	
	protected void error(Throwable e)
	{
		_log.log(Level.SEVERE, "", e);
	}
	
	protected String getSessionId()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		return (String)session.get(TOP_SESSION);
	}
	
	protected long getUserId()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		String userId = (String)session.get(USER_ID);
		if (userId != null)
		{
			return Long.parseLong(userId);
		}
		return -1;
	}
	
	protected String getUser()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		return (String)session.get(USER);
	}
	
	public void updateProgress(int total, int processed)
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (session != null)
		{
			session.put(TOTAL, total);
			session.put(PROCESSED, processed);
		}
	}
}
