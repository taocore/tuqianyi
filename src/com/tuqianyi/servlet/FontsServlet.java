package com.tuqianyi.servlet;

import javax.servlet.http.HttpServlet;

import com.tuqianyi.font.FontProvider;

public class FontsServlet extends HttpServlet{

	private static FontProvider fontProvider;
	
	public void init()
	{
		String dir = getServletContext().getRealPath("/");
		fontProvider = new FontProvider(dir);
	}
	
	public static FontProvider getFontProvider()
	{
		return fontProvider;
	}
}
