package com.tuqianyi.action;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.taobao.api.TaobaoResponse;
import com.tuqianyi.Constants;
import com.tuqianyi.service.MergeService;

public class ActionBase extends ActionSupport implements Constants{
	
	protected static Logger _log = Logger.getLogger(ActionBase.class.getName());
	
	protected static void error(TaobaoResponse rsp)
	{
		if (rsp != null)
		{
			_log.info(rsp.getErrorCode() + " - " + rsp.getMsg() + " - " + rsp.getSubCode() + " - " + rsp.getSubMsg());
		}
		else
		{
			_log.info("NULL RSP");
		}
	}
	
	protected static void error(Throwable e)
	{
		_log.log(Level.SEVERE, "", e);
	}
	
	protected Map<String, Object> getSession()
	{
		return ActionContext.getContext().getSession();
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
	
	protected static String getUser()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		return (String)session.get(USER);
	}
	
	protected String getVersion()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		return (String)session.get(VERSION);
	}
	
	public static void updateProgress(int total, int processed)
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		if (session != null)
		{
			session.put(TOTAL, total);
			session.put(PROCESSED, processed);
		}
	}
	
	public static synchronized void increaseProgress()
	{
		Map<String, Object> session = ActionContext.getContext().getSession();
		int i = (Integer)session.get(PROCESSED);
		if (session != null)
		{
			session.put(PROCESSED, ++i);
		}
	}
	
	public int getAllowedItems()
	{
		String version = getVersion();
		_log.info("version: " + version);
		return new MergeService().getAllowedItems(version);
	}
	
	public boolean isAdmin()
	{
		return getUserId() == ADMIN_ID;
	}
}
