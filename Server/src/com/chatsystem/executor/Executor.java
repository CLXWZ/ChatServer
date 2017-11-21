package com.chatsystem.executor;

import com.chatsystem.function.Action;

import java.util.concurrent.Callable;

/**
 * 线程执行者
 */
public class Executor implements Callable<Boolean>
{
	private Action executorAction;

	public Executor(Action action)
	{
		executorAction = action;
	}

	@Override
	public Boolean call() throws Exception
	{
		executorAction.invoke();
		return true;
	}
}
