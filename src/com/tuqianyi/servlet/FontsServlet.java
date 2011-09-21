package com.tuqianyi.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

public class FontsServlet extends HttpServlet{

	static Logger _log = Logger.getLogger(FontsServlet.class.getName());
	
	private static String root;
	
	public void init()
	{
		root = getServletContext().getRealPath("/");
	}
	
	public static String getRootPath()
	{
		return root;
	}
}
