package com.chatsystem.msgprocess;

import com.chatsystem.chat.ChatMgr;
import com.chatsystem.chat.room.ChatRoomMgr;
import com.chatsystem.msg.MsgChat;
import com.chatsystem.session.PlayerSession;
import com.chatsystem.user.User;

/**
 * 聊天相关处理方法
 */
public class MsgChatAction
{
	//聊天
	public static void handleChat(PlayerSession playerSession, Object msg)
	{
		MsgChat.McChat mcChat = (MsgChat.McChat)msg;
		if (null == mcChat)
		{
			return;
		}

		User user = playerSession.getMyUser();
		if (mcChat.getChatType() == MsgChat.CHAT_TYPE.PrivateChat)  //一对一聊天
		{
			ChatMgr.getInstance().sendPrivateMessage(user, mcChat.getText(), mcChat.getUserId());
		}
		else if (mcChat.getChatType() == MsgChat.CHAT_TYPE.RoomChat)  //聊天室聊天
		{
			ChatMgr.getInstance().sendRoomMessage(user, mcChat.getText(), mcChat.getRoomId());
		}
		else if (mcChat.getChatType() == MsgChat.CHAT_TYPE.EasyChat)  //测试用聊天
		{
			ChatMgr.getInstance().sendEasyChatMessage(user, mcChat.getText(), mcChat.getUserId());
		}
	}

	//获取全部群组
	public static void handleGetAllChatRooms(PlayerSession playerSession, Object msg)
	{
		MsgChat.McGetAllChatRoom mcGetAllChatRoom = (MsgChat.McGetAllChatRoom)msg;
		if (null == mcGetAllChatRoom)
		{
			return;
		}

		User user = playerSession.getMyUser();
		ChatRoomMgr.getInstance().getAllChatRooms(user);
	}

	//创建群组
	public static void handleCreateChatRoom(PlayerSession playerSession, Object msg)
	{
		MsgChat.McCreateChatRoom mcCreateChatRoom = (MsgChat.McCreateChatRoom)msg;
		if (null == mcCreateChatRoom)
		{
			return;
		}

		User user = playerSession.getMyUser();
		ChatRoomMgr.getInstance().createChatRoom(user, mcCreateChatRoom.getRoomName());
	}

	//加入群组
	public static void handleJoinChatRoom(PlayerSession playerSession, Object msg)
	{
		MsgChat.McJoinChatRoom mcJoinChatRoom = (MsgChat.McJoinChatRoom)msg;
		if (null == mcJoinChatRoom)
		{
			return;
		}

		User user = playerSession.getMyUser();
		ChatRoomMgr.getInstance().joinChatRoom(user, mcJoinChatRoom.getRoomId());
	}

	//离开群组
	public static void handleLeaveChatRoom(PlayerSession playerSession, Object msg)
	{
		MsgChat.McLeaveChatRoom mcLeaveChatRoom = (MsgChat.McLeaveChatRoom)msg;
		if (null == mcLeaveChatRoom)
		{
			return;
		}

		User user = playerSession.getMyUser();
		ChatRoomMgr.getInstance().leaveChatRoom(user, mcLeaveChatRoom.getRoomId());
	}
}
