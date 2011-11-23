package com.tuqianyi;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.taobao.api.domain.ArticleUserSubscribe;
import com.taobao.api.internal.util.TaobaoUtils;
import com.taobao.api.response.ItemGetResponse;
import com.tuqianyi.db.DBUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.taobao.TaobaoProxy;

public class SessionFilter implements Filter, Constants{

	static Logger _log = Logger.getLogger(SessionFilter.class.getName());

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
	private boolean verify(ServletRequest req) throws IOException, ServletException
	{
		//req.setCharacterEncoding("GBK");
		String queryString = ((HttpServletRequest)req).getQueryString();
		_log.info(queryString);
		HttpSession session = ((HttpServletRequest)req).getSession(false);
		if (session != null && session.getAttribute(USER) != null)
		{
			return true;
		}
		
		String topParams = req.getParameter("top_parameters");
		String topSession = req.getParameter("top_session");
		String topSign = req.getParameter("top_sign");
		String appKey = req.getParameter("top_appkey");
		String version = req.getParameter("versionNo");
		String leaseId = req.getParameter("leaseId");
		String timestamp = req.getParameter("timestamp");
		String sign = req.getParameter("sign");
		String itemCode = req.getParameter("itemCode");
		
		if (topParams == null || topSession == null || topSign==null || appKey == null)
		{
			return false;
		}
		
		boolean verifiedTopParameters = TaobaoUtils.verifyTopResponse(topParams, topSession, topSign, appKey, TaobaoProxy.SECRET);
		_log.info("top parameters verified: " + verifiedTopParameters);
		if (verifiedTopParameters)
		{
			String browser = ((HttpServletRequest)req).getHeader("user-agent");
			_log.info("browser: " + browser);
			Map<String, String> topMap = TaobaoUtils.decodeTopParams(URLEncoder.encode(topParams, "GBK"));
			_log.warning("parsed top params: " + topMap);
			String userId = topMap.get("visitor_id");
			String nick = topMap.get("visitor_nick");
			_log.info("uid: " + userId);
			_log.info("session: " + topSession);
			_log.info("itemCode: " + itemCode);
			boolean v = false;
			ArticleUserSubscribe sub = null;
			if (itemCode != null)
			{
				sub = TaobaoProxy.verifySubscription(nick, itemCode);
				v = (sub != null);
				_log.info("subscription verified: " + v);
				if (version == null)
				{
					if (ITEM_CODE_3.equals(itemCode))
					{
						version = "3";
					}
					else if (ITEM_CODE_2.equals(itemCode))
					{
						version = "2";
					}
					else
					{
						version = "1";
					}
				}
			}
			else
			{
				v = TaobaoProxy.verifyVersion(appKey, leaseId, timestamp, version, sign, SECRET);
				_log.info("version verified: " + v);
			}
			v = true;
			if (v)
			{
				session = ((HttpServletRequest)req).getSession(true);
				session.setAttribute(TOP_SESSION, topSession);
				session.setAttribute(USER, nick);
				session.setAttribute(USER_ID, userId);
				session.setAttribute(VERSION, version);
				session.setAttribute(SUBSCRIPTION, sub);
				session.setAttribute("query", queryString);
				session.setAttribute("browser", browser);
				try {
					Dao.INSTANCE.updateUser(Long.parseLong(userId), nick, topSession);
				} catch (Exception e) {
					_log.log(Level.SEVERE, "", e);
				}
				return true;
			}
		}
		return false;
	}
	
	public void doFilter(ServletRequest req, ServletResponse rsp,
			FilterChain chain) throws IOException, ServletException {
		try 
		{
			if (verify(req))
		    {
//				HttpSession session = ((HttpServletRequest)req).getSession(false);
//				final String user = (String) session.getAttribute(USER);
//				new Thread()
//				{
//					public void run()
//					{
//						sync(user);
//					}
//				}.start();
		    	chain.doFilter(req, rsp);
		    }
			else
			{
				rsp.setContentType("text/html;charset=UTF-8");
				rsp.getWriter().println("未授权。");
			}
		} 
		catch (IOException e) {
			_log.log(Level.SEVERE, "", e);
			throw e;
		}
	}
	
	private void sync(String user)
	{
		_log.info("syncing");
		Connection conn = null;
		try {
			conn = DBUtils.getConnection();
			List<Long> ids = Dao.INSTANCE.getMergedItemIds(user, conn);
			for (long id : ids)
			{
				ItemGetResponse response = TaobaoProxy.getItem(id);
				if (!response.isSuccess())
				{
					_log.info(response.getErrorCode() + ": " +response.getMsg() + ", " + response.getSubCode() + ": " + response.getSubMsg());
					if ("isv.item-is-delete:invalid-numIid".equals(response.getSubCode()) || "isv.item-is-delete:invalid-numIid-or-iid".equals(response.getSubCode()) || "isv.item-get-service-error:ITEM_NOT_FOUND".equals(response.getSubCode()))
					{
						_log.info("deleting invalid item: " + id);
						Dao.INSTANCE.deleteMergedItem(id, conn);
					}
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
		finally
		{
			DBUtils.close(conn, null, null);
		}
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
