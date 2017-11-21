package com.chatsystem.tools;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

/**
 * 泛型对象池
 */
public class ObjPool<T>
{
	private Supplier<T> creator;
	private final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<T>();

	public ObjPool (Supplier<T> supplier)
	{
		creator = supplier;
	}

	public T dequeue () throws Exception
	{
		T obj = pool.poll();

		if (null == obj)
		{
			if (null != creator)
			{
				obj = creator.get();
			}
		}

		return obj;
	}

	public void enqueue (T obj)
	{
		pool.offer(obj);
	}
}
