package com.chatsystem.msgprocess;

import java.util.HashMap;

/**
 * 网络消息处理方法管理类
 */
public class NetMsgHandlers<T>
{
	public enum MSG_TYPE
	{
		None,
		Main,
		Assistance,
	}

	private final HashMap<Integer, T> msgHandlers = new HashMap<>();  //消息处理器集合
	private final HashMap<Integer, MSG_TYPE> msgTypes = new HashMap<>();  //消息类型集合

	public void register (int msgId, T msgHandler, MSG_TYPE msgType)
	{
		msgHandlers.put(msgId, msgHandler);
		msgTypes.put(msgId, msgType);
	}

	public void unRegister (int msgId)
	{
		if (msgHandlers.containsKey(msgId))
		{
			msgHandlers.remove(msgId);
		}

		if (msgTypes.containsKey(msgId))
		{
			msgTypes.remove(msgId);
		}
	}

	public T getMsgHandler (int msgId)
	{
		if (!msgHandlers.containsKey(msgId))
		{
			return null;
		}

		return msgHandlers.get(msgId);
	}

	public MSG_TYPE getMsgType (int msgId)
	{
		if (!msgTypes.containsKey(msgId))
		{
			return MSG_TYPE.None;
		}

		return msgTypes.get(msgId);
	}
}
