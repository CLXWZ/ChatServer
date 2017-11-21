package com.chatsystem.chat.dispatcher;

import com.chatsystem.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *  消息分发器
 */
public class ChatDispatcher
{
	private static final Logger logger = LoggerFactory.getLogger(ChatDispatcher.class);

	private final Map<Long, User> chatUsers = new HashMap<>();

	public boolean init ()
	{
		return true;
	}

	public void update ()
	{
		for (User user : chatUsers.values())
		{
			user.update();
		}
	}

	public void destroy ()
	{
		chatUsers.clear();
	}

	public void addUser(User user)
	{
		if (chatUsers.containsKey(user.getId()))
		{
			return;
		}

		chatUsers.put(user.getId(), user);
	}

	public void removeUser(long userId)
	{
		if (!chatUsers.containsKey(userId))
		{
			return;
		}

		chatUsers.remove(userId);
	}
}
