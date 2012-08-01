package com.tuqianyi;

import java.io.IOException;
import java.net.URLEncoder;
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

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.domain.ArticleUserSubscribe;
import com.taobao.api.domain.User;
import com.taobao.api.internal.util.TaobaoUtils;
import com.tuqianyi.db.Dao;
import com.tuqianyi.taobao.TaobaoProxy;

public class SessionFilter implements Filter, Constants{

	static Logger _log = Logger.getLogger(SessionFilter.class.getName());

	public void init(FilterConfig arg0) throws ServletException {
		
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
			List<ArticleUserSubscribe> subscriptions = null;
			try {
				subscriptions = TaobaoProxy.getSubscription(nick, ARTICLE_CODE);
			} catch (ApiException e) {
				_log.log(Level.SEVERE, "", e);
			}
			if (itemCode != null)
			{
				String[] itemCodes = StringUtils.split(itemCode, ":");
				if (version == null)
				{
					int vNumber = 0;
					for (String item : itemCodes)
					{
						if (ITEM_CODE_FREE.equals(item))
						{
							vNumber = Math.max(0, vNumber);
						}
						else if (ITEM_CODE_1.equals(item))
						{
							vNumber = Math.max(1, vNumber);
						}
						else if (ITEM_CODE_2.equals(item))
						{
							vNumber = Math.max(2, vNumber);
						}
						else if (ITEM_CODE_3.equals(item))
						{
							vNumber = Math.max(3, vNumber);
						}
						v = (subscriptions != null && !subscriptions.isEmpty());
						_log.info("subscription verified: " + v);
					}
					version = String.valueOf(vNumber);
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
				session.setAttribute(SUBSCRIPTION, subscriptions);
				session.setAttribute("query", queryString);
				session.setAttribute("browser", browser);
				session.setAttribute("admin", (Long.parseLong(userId) == ADMIN_ID));
				try {
					User user = TaobaoProxy.getUser(topSession);
					long level = user.getSellerCredit().getLevel();
					Dao.INSTANCE.updateUser(Long.parseLong(userId), nick, level, topSession);
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
		
	}
}
