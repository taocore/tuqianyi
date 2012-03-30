package com.tuqianyi.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainService {

	static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(3);

	public void executeInPool(Runnable task)
	{
		THREAD_POOL.execute(task);
	}
}
