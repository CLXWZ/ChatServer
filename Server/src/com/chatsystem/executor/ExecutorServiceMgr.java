package com.chatsystem.executor;

import com.chatsystem.function.Action;
import com.chatsystem.network.thread.ServerThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类
 */
public class ExecutorServiceMgr
{
	private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceMgr.class);

	private static final ExecutorServiceMgr instance = new ExecutorServiceMgr();

	private ExecutorServiceMgr()
	{

	}

	public static ExecutorServiceMgr getInstance ()
	{
		return instance;
	}

	private ExecutorService executorService;

	private Map<String, Executor> tasks = new HashMap<String, Executor>();

	public boolean init ()
	{
		try
		{
			int threadNum = Runtime.getRuntime().availableProcessors();
			executorService = Executors.newFixedThreadPool(threadNum, new ServerThreadFactory("WorkerThreadPool"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	public void destroy () throws InterruptedException
	{
		executorService.shutdown();
		executorService.awaitTermination(1, TimeUnit.SECONDS);
	}

	public void update ()
	{
		invokeAll();
	}

	public boolean addExecutorTask (String uniqueKey, Action action)
	{
		if (tasks.containsKey(uniqueKey))
		{
			return false;
		}

		Executor executor = new Executor(action);

		tasks.put(uniqueKey, executor);

		return true;
	}

	public void removeExecutorTask (String uniqueKey)
	{
		if (!tasks.containsKey(uniqueKey))
		{
			return;
		}

		tasks.remove(uniqueKey);
	}

	public void invokeAll ()
	{
		if (tasks.isEmpty())
		{
			return;
		}

		try
		{
			executorService.invokeAll(tasks.values());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
