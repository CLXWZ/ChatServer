package com.chatsystem.ban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * 封禁相关功能管理器
 */
public class BanMgr
{
	private static final Logger logger = LoggerFactory.getLogger(BanMgr.class);

	private static final BanMgr instance = new BanMgr();
	private final ArrayList<Long> banLoginList = new ArrayList<>();  //禁止登录名单
	private final ArrayList<Long> banChatList = new ArrayList<>();   //禁止聊天名单

	private BanMgr ()
	{

	}

	public static BanMgr getInstance ()
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

	public void addBanLogin(long id)
	{
		if (banLoginList.contains(id))
		{
			return;
		}

		banLoginList.add(id);
	}

	public void removeBanLogin(long id)
	{
		if (!banLoginList.contains(id))
		{
			return;
		}

		banLoginList.remove(id);
	}

	public boolean isBanLogin(long id)
	{
		return banLoginList.contains(id);
	}

	public void addBanChat(long id)
	{
		if (banChatList.contains(id))
		{
			return;
		}

		banChatList.add(id);
	}

	public void removeBanChat(long id)
	{
		if (!banChatList.contains(id))
		{
			return;
		}

		banChatList.remove(id);
	}

	public boolean isBanChat(long id)
	{
		return banChatList.contains(id);
	}
}
