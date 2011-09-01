package com.tuqianyi;

import java.io.IOException;
import java.net.URLEncoder;
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

import com.taobao.api.internal.util.TaobaoUtils;
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
		
		if (topParams == null || topSession == null || topSign==null || appKey == null)
		{
			return false;
		}
		
		boolean verifiedTopParameters = TaobaoUtils.verifyTopResponse(topParams, topSession, topSign, appKey, TaobaoProxy.SECRET);
		_log.info("top parameters verified: " + verifiedTopParameters);
		boolean versionVerified = TaobaoProxy.verifyVersion(appKey, leaseId, timestamp, version, sign, SECRET);
		if (TaobaoProxy.isTest() || (verifiedTopParameters && versionVerified))
		{
			Map<String, String> topMap = TaobaoUtils.decodeTopParams(URLEncoder.encode(topParams, "GBK"));
			_log.warning("parsed top params: " + topMap);
			String userId = topMap.get("visitor_id");
			String nick = topMap.get("visitor_nick");
			_log.info("uid: " + userId);
			_log.info("session: " + topSession);
			
			session = ((HttpServletRequest)req).getSession(true);
			session.setAttribute(TOP_SESSION, topSession);
			session.setAttribute(USER, nick);
			session.setAttribute(USER_ID, userId);
			session.setAttribute(VERSION, version);
			try {
				Dao.INSTANCE.updateUser(Long.parseLong(userId), nick, topSession);
			} catch (Exception e) {
				_log.log(Level.SEVERE, "", e);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void doFilter(ServletRequest req, ServletResponse rsp,
			FilterChain chain) throws IOException, ServletException {
		try 
		{
			if (verify(req))
		    {
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
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
