package com.chatsystem.session;

import com.chatsystem.msg.MsgError;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.msgprocess.MsgMgr;
import com.chatsystem.network.NetSession;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.tools.ObjPool;
import com.chatsystem.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.type.ErrorType;

/**
 * 表示一个玩家连接
 */
public class PlayerSession
{
	private static final Logger logger = LoggerFactory.getLogger(PlayerSession.class);

	private static ObjPool<PlayerSession> pool = new ObjPool<PlayerSession>(PlayerSession::createPlayerSession);
	private static final int maxMsgPerTick = 10;

	private User myUser = null;
	private NetSession netSession = null;

	private PlayerSession()
	{

	}

	private static PlayerSession createPlayerSession ()
	{
		return new PlayerSession();
	}

	public static PlayerSession create (User user, NetSession session)
	{
		try
		{
			PlayerSession playerSession = pool.dequeue();
			if (null == playerSession)
			{
				return null;
			}

			if (!playerSession.init(user, session))
			{
				playerSession.release();
				return null;
			}

			return playerSession;
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public boolean init (User user, NetSession session)
	{
		myUser = user;
		netSession = session;
		session.auth(user.getAccountId());
		return true;
	}

	public void update (long curTick)
	{
		//更新消息
		updateMsg();
	}

	//更新消息
	private void updateMsg ()
	{
		int num = 0;
		NetPacket packet = null;
		while (num < maxMsgPerTick && null != (packet = netSession.dequeuePacket()))
		{
			++num;
			MsgMgr.getInstance().handleMsg(this, packet);
		}
	}

	public void destroy ()
	{
		myUser = null;
		netSession = null;
	}

	public void release ()
	{
		destroy();
		pool.enqueue(this);
	}

	public void sendPacket (NetPacket packet)
	{
		if (null == packet)
		{
			logger.error("[SendPacket] packet为空.");
			return;
		}

		netSession.sendPacket(packet);
	}

	public void sendPbMsg (int msgId, Object obj)
	{
		NetPacket packet = NetPacket.getNewNetPacket();
		if (null == packet)
		{
			logger.error("[SendPbMsg] packet为空.");
			return;
		}

		packet.init(msgId, obj);
		netSession.sendPacket(packet);
	}

	public void sendErrorMsg (MsgError.ERROR_TYPE errorType)
	{
		NetPacket packet = NetPacket.getNewNetPacket();
		if (null == packet)
		{
			logger.error("[SendErrorMsg] packet为空.");
			return;
		}

		MsgError.MsError msg = MsgError.MsError.newBuilder().setError(errorType).build();
		packet.init(MsgIds.MSG_ID.M_ErrorCode_VALUE, msg);
		netSession.sendPacket(packet);
	}

//	public void SendErrorMsg (ErrorType errorType)
//	{
//		NetPacket packet = NetPacket.GetNewNetPacket();
//
//		if (null == packet)
//		{
//			logger.error("[SendErrorMsg] packet为空.");
//			return;
//		}
//
//		MsError msg = MsError.newBuilder().setError(errorType).build();
//
//		packet.Init(MsgId.M_ErrorCode_VALUE, msg);
//
//		NetSession.SendPacket(packet);
//	}

	public User getMyUser()
	{
		return myUser;
	}

	public NetSession getNetSession()
	{
		return netSession;
	}
}
