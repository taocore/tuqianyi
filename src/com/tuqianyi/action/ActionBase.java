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
	
	protected static void error(TaobaoResponse rsp)
	{
		_log.info(rsp.getErrorCode() + " - " + rsp.getMsg() + " - " + rsp.getSubCode() + " - " + rsp.getSubMsg());
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
		return getAllowedItems(version);
	}
	
	private int getAllowedItems(String version)
	{
		int allowedItemsCount = 10;
		if ("1".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V1;
		}
		else if ("2".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V2;
		}
		else if ("3".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V3;
		};
		return allowedItemsCount;
	}
	
	public boolean isAdmin()
	{
		return getUserId() == ADMIN_ID;
	}
}
