package com.chatsystem.chat.room;

import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgError;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 聊天群管理器
 */
public class ChatRoomMgr
{
	private static final Logger logger = LoggerFactory.getLogger(ChatRoomMgr.class);

	private static final ChatRoomMgr instance = new ChatRoomMgr();

	private int chatRoomId = 0;
	private final Map<Integer, ChatRoom> chatRooms = new HashMap<>();
	private final Map<Long, ArrayList<ChatRoom>> chatRoomsByUserId = new HashMap<>();
	private ChatRoomMgr ()
	{

	}

	public static ChatRoomMgr getInstance ()
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

	public boolean createChatRoom(User user, String name)
	{
		ChatRoom room = ChatRoom.createChatRoom(++chatRoomId, name);
		if (null == room)
		{
			return false;
		}

		chatRooms.put(room.getRoomId(), room);
		addToChatRoom(room.getRoomId(), user);

		user.sendPbMsg(MsgIds.MSG_ID.M_CreateChatRoom_VALUE, MsgChat.MsCreateChatRoom.newBuilder().setRoom(room.toPb()).build());
		return true;
	}

	public boolean joinChatRoom(User user, int roomId)
	{
		if (!chatRooms.containsKey(roomId))
		{
			return false;
		}

		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		if (room.isInRoom(user.getId()))
		{
			user.sendErrorMsg(MsgError.ERROR_TYPE.IsInChatGroup);
			return false;
		}

		if (!addToChatRoom(roomId, user))
		{
			return false;
		}

		user.sendPbMsg(MsgIds.MSG_ID.M_JoinChatRoom_VALUE, MsgChat.MsJoinChatRoom.newBuilder().setRoom(room.toPb()).build());
		room.notifyJoinRoom(user);  //通知其他成员新成员加入
		return true;
	}

	public boolean leaveChatRoom(User user, int roomId)
	{
		if (!chatRooms.containsKey(roomId))
		{
			return false;
		}

		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		if (!room.isInRoom(user.getId()))
		{
			user.sendErrorMsg(MsgError.ERROR_TYPE.NotInChatGroup);
			return false;
		}

		if (!removeFromChatRoom(roomId, user))
		{
			return false;
		}

		user.sendPbMsg(MsgIds.MSG_ID.M_LeaveChatRoom_VALUE, MsgChat.MsLeaveChatRoom.newBuilder().setRoom(room.toPb()).build());
		room.notifyLeaveRoom(user);
		return true;
	}

	public boolean getAllChatRooms(User user)
	{
		MsgChat.MsGetAllChatRoom.Builder builder = MsgChat.MsGetAllChatRoom.newBuilder();
		for (ChatRoom room : chatRooms.values())
		{
			builder.addRooms(room.toPb());
		}

		user.sendPbMsg(MsgIds.MSG_ID.M_GetAllChatRoom_VALUE, builder.build());
		return true;
	}

	public boolean sendRoomMessage(User user, String text, int roomId)
	{
		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		MsgChat.MsChat msChat = MsgChat.MsChat.newBuilder().
				setChatType(MsgChat.CHAT_TYPE.RoomChat).
				setFromUserId(user.getId()).
				setFromUserName(user.getName()).
				setFromRoomName(room.getRoomName()).
				setText(text).build();

		room.sendChatMessage(msChat);
		return true;
	}

	public boolean broadcastPbMessage(int roomId, int msgId, Object msg)
	{
		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		room.broadcastPbMessage(msgId, msg);
		return true;
	}

	public MsgChat.PbChatRoomInit chatRoomInit(long userId)
	{
		MsgChat.PbChatRoomInit.Builder builder = MsgChat.PbChatRoomInit.newBuilder();
		ArrayList<ChatRoom> rooms = chatRoomsByUserId.get(userId);
		if (null == rooms)
		{
			return builder.build();
		}

		for (ChatRoom room : rooms)
		{
			builder.addRooms(room.toPb());
		}

		return builder.build();
	}

	private boolean addToChatRoom(int roomId, User user)
	{
		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		if (!room.addUser(user))
		{
			return false;
		}

		if (!chatRoomsByUserId.containsKey(user.getId()))
		{
			chatRoomsByUserId.put(user.getId(), new ArrayList<>());
		}
		ArrayList<ChatRoom> list = chatRoomsByUserId.get(user.getId());
		list.add(room);
		return true;
	}

	private boolean removeFromChatRoom(int roomId, User user)
	{
		ChatRoom room = chatRooms.get(roomId);
		if (null == room)
		{
			return false;
		}

		if (!room.removeUser(user))
		{
			return false;
		}

		if (!chatRoomsByUserId.containsKey(user.getId()))
		{
			return false;
		}

		ArrayList<ChatRoom> list = chatRoomsByUserId.get(user.getId());
		list.remove(room);
		return true;
	}
}
