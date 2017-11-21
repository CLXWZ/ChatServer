package com.chatsystem.chat.dispatcher;

import com.chatsystem.executor.ExecutorServiceMgr;
import com.chatsystem.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * 消息分发管理器
 */
public class ChatDispatchMgr
{
	private static final Logger logger = LoggerFactory.getLogger(ChatDispatchMgr.class);

	private static final ChatDispatchMgr instance = new ChatDispatchMgr();
	private final ArrayList<ChatDispatcher> chatDispatchers = new ArrayList<>();
	private final int processorNum = Runtime.getRuntime().availableProcessors();

	private ChatDispatchMgr()
	{

	}

	public static ChatDispatchMgr getInstance()
	{
		return instance;
	}

	public boolean init()
	{
		for (int i = 0; i < processorNum; ++i)
		{
			ChatDispatcher dispatcher = new ChatDispatcher();
			dispatcher.init();
			chatDispatchers.add(dispatcher);

			ExecutorServiceMgr.getInstance().addExecutorTask("dispatcher" + i, () -> { dispatcher.update(); });
		}

		return true;
	}

	public void destroy()
	{

	}

	public void addUser(User user)
	{
		int index = (int)(user.getId() % processorNum);
		ChatDispatcher dispatcher = chatDispatchers.get(index);
		if (null != dispatcher)
		{
			dispatcher.addUser(user);
		}
	}

	public void removeUser(long userId)
	{
		int index = (int)(userId % processorNum);
		ChatDispatcher dispatcher = chatDispatchers.get(index);
		if (null != dispatcher)
		{
			dispatcher.removeUser(userId);
		}
	}
}
