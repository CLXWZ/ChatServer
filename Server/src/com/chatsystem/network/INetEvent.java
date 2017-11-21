package com.chatsystem.network;

/**
 * 网络事件接口
 */
public interface INetEvent
{
	//当连接成功时
	public void onConnected (NetSession session);
	//当接收到到消息时
	public void onReceiveMsg (NetSession session);
	//当断开连接时
	public void onDisconnected (NetSession session);
}
