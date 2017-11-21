package com.chatsystem.msgprocess;

import com.chatsystem.msg.MsgLogin;
import com.chatsystem.msg.MsgLogin.McAuth;
import com.chatsystem.msg.MsgUser.McFindUser;
import com.chatsystem.msg.MsgUser.McGetAllUsers;
import com.chatsystem.session.PlayerSession;
import com.chatsystem.user.UserMgr;

/**
 * 用户相关处理方法
 */
public class MsgUserAction 
{
	//获取用户信息
	public static void handleGetAllUsers(PlayerSession playerSession, Object msg)
	{
		McGetAllUsers mcGetAllUsers = (McGetAllUsers)msg;
		if (null == mcGetAllUsers)
		{
			return;
		}
		
		UserMgr.getInstance().getAllUsers(playerSession.getMyUser(), mcGetAllUsers.getIsOnline());
	}
	
	//搜索用户
	public static void handleFindUser(PlayerSession playerSession, Object msg)
	{
		McFindUser mcFindUser = (McFindUser)msg;
		if (null == mcFindUser)
		{
			return;
		}
		
		UserMgr.getInstance().FindUser(playerSession.getMyUser(), mcFindUser.getName());
	}
}
