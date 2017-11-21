package com.chatsystem.chat.room;

import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.user.User;
import com.chatsystem.user.UserMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天群
 */
public class ChatRoom
{
	private int roomId;
	private String roomName;
	private final List<Long> chatRoomUsers = new ArrayList<>();

	private ChatRoom()
	{

	}

	public static ChatRoom createChatRoom(int roomId, String roomName)
	{
		ChatRoom chatRoom = new ChatRoom();
		if (!chatRoom.init(roomId, roomName))
		{
			return null;
		}

		return chatRoom;
	}

	public boolean init(int roomId, String roomName)
	{
		this.roomId = roomId;
		this.roomName = roomName;
		return true;
	}

	public MsgChat.PbChatRoom toPb()
	{
		MsgChat.PbChatRoom.Builder builder = MsgChat.PbChatRoom.newBuilder();
		builder.setId(this.roomId);
		builder.setRoomName(this.roomName);
		for (long id : chatRoomUsers)
		{
			builder.addMembers(id);
		}

		return builder.build();
	}

	public boolean addUser(User user)
	{
		if (chatRoomUsers.contains(user.getId()))
		{
			return false;
		}

		chatRoomUsers.add(user.getId());
		return true;
	}

	public boolean removeUser(User user)
	{
		if (!chatRoomUsers.contains(user.getId()))
		{
			return false;
		}

		chatRoomUsers.remove(user.getId());
		return true;
	}

	public void sendChatMessage(MsgChat.MsChat pb)
	{
		for (long id : chatRoomUsers)
		{
			User user = UserMgr.getInstance().findOnlineUser(id);
			if (null == user)
			{
				continue;
			}

			user.enqueueChatMsg(pb);
		}
	}

	public boolean notifyJoinRoom(User user)
	{
		MsgChat.MsChatRoomNotify msNotify = MsgChat.MsChatRoomNotify.newBuilder().
	             setType(MsgChat.CHATROOM_NOTIFY_TYPE.Join).
	             addParams(roomName).
	             addParams(user.getName()).build();

		broadcastPbMessage(MsgIds.MSG_ID.M_ChatRoomNotify_VALUE, msNotify);
		return true;
	}

	public boolean notifyLeaveRoom(User user)
	{
		MsgChat.MsChatRoomNotify msNotify = MsgChat.MsChatRoomNotify.newBuilder().
				setType(MsgChat.CHATROOM_NOTIFY_TYPE.Leave).
				addParams(roomName).
				addParams(user.getName()).build();

		broadcastPbMessage(MsgIds.MSG_ID.M_ChatRoomNotify_VALUE, msNotify);
		return true;
	}

	public boolean broadcastPbMessage(int msgId, Object msg)
	{
		for (long id : chatRoomUsers)
		{
			User member = UserMgr.getInstance().findOnlineUser(id);
			if(null == member)
			{
				continue;
			}

			member.sendPbMsg(msgId, msg);
		}
		return true;
	}

	//是否在房间中
	public boolean isInRoom(long userId)
	{
		return chatRoomUsers.contains(userId);
	}

	public int getRoomId()
	{
		return roomId;
	}

	public void setRoomId(int roomId)
	{
		this.roomId = roomId;
	}

	public String getRoomName()
	{
		return roomName;
	}

	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}
}
