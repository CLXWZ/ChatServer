package com.chatsystem.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 服务器命令管理器
 */
public class CommandMgr
{
	private static final Logger logger = LoggerFactory.getLogger(CommandMgr.class);

	private static final CommandMgr instance = new CommandMgr();
	private final Map<String, Consumer<String[]>> commandMap = new HashMap<>();

	private CommandMgr ()
	{

	}

	public static CommandMgr getInstance ()
	{
		return instance;
	}

	public boolean init()
	{
		return true;
	}

	public void destroy()
	{

	}

	public boolean registerCommand(String command, Consumer<String[]> consumer)
	{
		if (commandMap.containsKey(command))
		{
			return false;
		}

		commandMap.put(command, consumer);
		return true;
	}

	public boolean unRegisterCommand(String command)
	{
		if (!commandMap.containsKey(command))
		{
			return false;
		}

		commandMap.remove(command);
		return true;
	}

	public void process(String command, String[] args)
	{
		if (!commandMap.containsKey(command))
		{
			return;
		}

		commandMap.get(command).accept(args);
	}
}
