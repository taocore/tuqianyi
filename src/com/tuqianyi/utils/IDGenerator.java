package com.tuqianyi.utils;

public class IDGenerator {

	private static int tail = 0;
	
	public synchronized static long generateID()
	{
		if (tail >= 1000)
		{
			tail = 0;
		}
		return System.currentTimeMillis() + tail++;
	}
}
