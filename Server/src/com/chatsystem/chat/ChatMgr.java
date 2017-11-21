package com.chatsystem.chat;

import com.chatsystem.ban.BanMgr;
import com.chatsystem.chat.room.ChatRoomMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgChat.CHAT_TYPE;
import com.chatsystem.msg.MsgError.ERROR_TYPE;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import com.chatsystem.user.UserMgr;

/**
 * 聊天管理器
 */
public class ChatMgr
{
	private static final Logger logger = LoggerFactory.getLogger(ChatMgr.class);
	
	private static final ChatMgr instance = new ChatMgr();

	private ChatMgr ()
	{

	}

	public static ChatMgr getInstance ()
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

	public void sendRoomMessage(User user, String text, int roomId)
	{
		if (BanMgr.getInstance().isBanChat(user.getId()))   //禁止聊天
		{
			user.sendErrorMsg(ERROR_TYPE.BanChat);
			return;
		}

		ChatRoomMgr.getInstance().sendRoomMessage(user, text, roomId);
	}

	public void sendPrivateMessage(User user, String text, long otherId)
	{
		if (user.getId() == otherId)
		{
			return;
		}

		if (BanMgr.getInstance().isBanChat(user.getId()))   //禁止聊天
		{
			user.sendErrorMsg(ERROR_TYPE.BanChat);
			return;
		}

		UserInfo otherInfo = UserMgr.getInstance().findUserInfoById(otherId);
		if (null == otherInfo)
		{
			return;
		}
		
		if (!user.getUserRelationCtrl().isFriend(otherId))
		{
			user.sendErrorMsg(ERROR_TYPE.TargetNoFriend);
			return;
		}
		
		User other = UserMgr.getInstance().findOnlineUser(otherId);
		if (null != other)
		{
			if (!other.getUserRelationCtrl().isFriend(user.getId()))
			{
				user.sendErrorMsg(ERROR_TYPE.TargetNoFriend);
				return;
			}
			
			MsgChat.MsChat.Builder msChatBuilder = MsgChat.MsChat.newBuilder();
			msChatBuilder.setChatType(CHAT_TYPE.PrivateChat);
			msChatBuilder.setFromUserId(user.getId());
			msChatBuilder.setFromUserName(user.getName());
			msChatBuilder.setText(text);
			other.enqueueChatMsg(msChatBuilder.build());
		}
		else
		{
			if (!otherInfo.isFriend(user.getId()))
			{
				user.sendErrorMsg(ERROR_TYPE.TargetNoFriend);
				return;
			}
			
			UserMgr.getInstance().saveOfflineMsg(otherInfo, user.getId(), user.getName(), "", CHAT_TYPE.PrivateChat, text);
		}
	}

	public void sendEasyChatMessage(User user, String text, long otherId)
	{
		if (user.getId() == otherId)
		{
			return;
		}

		UserInfo otherInfo = UserMgr.getInstance().findUserInfoById(otherId);
		if (null == otherInfo)
		{
			return;
		}

		//logger.info("发送聊天消息，UserId = {}, OtherId = {}", user.getId(), otherId);

		User other = UserMgr.getInstance().findOnlineUser(otherId);
		if (null != other)
		{
			MsgChat.MsChat.Builder msChatBuilder = MsgChat.MsChat.newBuilder();
			msChatBuilder.setChatType(CHAT_TYPE.PrivateChat);
			msChatBuilder.setFromUserId(user.getId());
			msChatBuilder.setFromUserName(user.getName());
			msChatBuilder.setText(text);
			other.enqueueChatMsg(msChatBuilder.build());
		}
		else
		{
			//UserMgr.getInstance().saveOfflineMsg(otherInfo, user.getId(), user.getName(), "", CHAT_TYPE.PrivateChat, text);
		}
	}
}