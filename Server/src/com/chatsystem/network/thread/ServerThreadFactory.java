package com.chatsystem.network.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerThreadFactory implements ThreadFactory
{
	private final ThreadGroup threadGroup;
	private final AtomicInteger threadNum = new AtomicInteger(1);
	private final String namePrefix;

	public ServerThreadFactory (String name)
	{
		SecurityManager sManager = System.getSecurityManager();
		threadGroup = sManager != null ? sManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = name + "-thread-";
	}

	@Override
	public Thread newThread(Runnable r)
	{
		Thread thread = new Thread(threadGroup, r, namePrefix + threadNum.getAndIncrement());

		if (thread.isDaemon())
		{
			thread.setDaemon(false);
		}

		if (thread.getPriority() != Thread.NORM_PRIORITY)
		{
			thread.setPriority(Thread.NORM_PRIORITY);
		}

		return thread;
	}
}
