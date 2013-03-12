package com.tuqianyi.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MainService {

	static Logger _log = Logger.getLogger(MainService.class.getName());
	
	static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);

	public void executeInPool(Runnable task)
	{
		_log.info(getInfoOfThreadPool());
		THREAD_POOL.execute(task);
	}
	
	public static void shutdown()
	{
		THREAD_POOL.shutdown();
	}
	
	public String getInfoOfThreadPool()
	{
		StringBuilder sb = new StringBuilder();
		if (THREAD_POOL instanceof ThreadPoolExecutor)
		{
			ThreadPoolExecutor e = (ThreadPoolExecutor)THREAD_POOL;
			sb.append("active count: ").append(e.getActiveCount());
			sb.append("\ncompleted task count: ").append(e.getCompletedTaskCount());
			sb.append("\ncore pool size: ").append(e.getCorePoolSize());
			sb.append("\nkeep ailve time: ").append(e.getKeepAliveTime(TimeUnit.MINUTES)).append("minutes");
			sb.append("\nlagest pool size: ").append(e.getLargestPoolSize());
			sb.append("\nmax pool size: ").append(e.getMaximumPoolSize());
			sb.append("\npool size: ").append(e.getPoolSize());
			sb.append("\ntask count: ").append(e.getTaskCount());
		}
		return sb.toString();
	}
}
