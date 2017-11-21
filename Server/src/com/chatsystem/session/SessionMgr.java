package com.chatsystem.session;

import com.chatsystem.ban.BanMgr;
import com.chatsystem.msg.MsgError;
import com.chatsystem.msg.MsgLogin;
import com.chatsystem.network.NetServer;
import com.chatsystem.network.NetSession;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.tools.DateTimeUtil;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import com.chatsystem.user.UserMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chatsystem.msg.MsgIds.MSG_ID;
import com.chatsystem.msg.MsgLogin.McLogin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 客户端连接管理器
 */
public class SessionMgr
{
	private static final Logger logger = LoggerFactory.getLogger(SessionMgr.class);

	private static final SessionMgr instance = new SessionMgr();

	private static final int maxLogonRequestPerTick = 10;
	private static final int maxLogoffRequestPerTick = 20;
	private final NetServer netServer = new NetServer();  //网络连接层
	private Map<String, PlayerSession> mapSession = new HashMap<String, PlayerSession>();  //客户端连接
	private ConcurrentLinkedQueue<PlayerSession> queueKickRequest = new ConcurrentLinkedQueue<>();  //需要被踢出的客户端列表

	private SessionMgr ()
	{

	}

	public static SessionMgr getInstance ()
	{
		return instance;
	}

	public boolean init (int port)
	{
		return netServer.startListen(port);
	}

	public void update () throws Exception
	{
		//处理登录请求
		updateLogonRequest();
		//处理登出请求
		updateLogoffRequest();
		//处理踢人请求
		updateKickRequest();
		//更新Session
		updateSession();
	}

	public void destroy () throws InterruptedException
	{
		netServer.stop();
	}

	private void updateLogonRequest () throws Exception
	{
		int num = 0;
		NetSession session = null;

		while (num < maxLogonRequestPerTick && null != (session = netServer.dequeueLogonClient()))
		{
			++num;

			if (session.getState() != NetSession.SESSION_STATE.Normal)
			{
				logger.info("收到登录消息，但是连接已关闭");
				continue;
			}

			NetPacket packet = session.dequeuePacket();
			if (null == packet)
			{
				logger.error("[UpdateLogonRequest] 登录消息包为空, session IP: {}", session.getSessionAddress());
				session.closeSession();
				continue;
			}

			if (MSG_ID.M_Login_VALUE == packet.getId())
			{
				McLogin loginMsg = (McLogin)packet.getMsg();
				if (null == loginMsg)
				{
					logger.error("[UpdateLogonRequest] 登录消息包错误, session IP: {}", session.getSessionAddress());
					session.closeSession();
					continue;
				}

				UserInfo userInfo = UserMgr.getInstance().findUserInfoByAccount(loginMsg.getAccountId());
				if (null == userInfo)   //用户不存在
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.NoAccount).build());
					session.sendPacket(resPack);
					continue;
				}

				if (BanMgr.getInstance().isBanLogin(userInfo.getId()))  //禁止登录
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.BanLogin).build());
					session.sendPacket(resPack);
					continue;
				}

				//发送认证
				String randomKey = userInfo.createAuthKey();
				NetPacket resPack = NetPacket.getNewNetPacket();
				resPack.init(MSG_ID.M_Login_VALUE, MsgLogin.MsLogin.newBuilder().setRandomKey(randomKey).build());
				session.sendPacket(resPack);
			}
			else if (MSG_ID.M_Auth_VALUE == packet.getId())
			{
				MsgLogin.McAuth authMsg = (MsgLogin.McAuth)packet.getMsg();
				if (null == authMsg)
				{
					logger.error("[UpdateLogonRequest] 登录消息包错误, session IP: {}", session.getSessionAddress());
					session.closeSession();
					return;
				}

				UserInfo userInfo = UserMgr.getInstance().findUserInfoByAccount(authMsg.getAccountId());
				if (null == userInfo)   //用户不存在
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.NoAccount).build());
					session.sendPacket(resPack);
					continue;
				}

				//认证
				if (userInfo.auth(authMsg.getAuthKey()))
				{
					//用户登录
					UserMgr.getInstance().login(userInfo.getId(), session);
					logger.info("[UpdateLogonRequest] 登录成功 AccountId = {}", userInfo.getAccountId());
				}
				else
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.AuthError).build());
					session.sendPacket(resPack);
					continue;
				}
			}
			else if (MSG_ID.M_SignUp_VALUE == packet.getId())
			{
				MsgLogin.McSignUp signupMsg = (MsgLogin.McSignUp)packet.getMsg();
				if (null == signupMsg)
				{
					logger.error("[UpdateLogonRequest] 登录消息包错误, session IP: {}", session.getSessionAddress());
					session.closeSession();
					continue;
				}

				UserInfo userInfo = UserMgr.getInstance().findUserInfoByAccount(signupMsg.getAccountId());
				if (null != userInfo)  //用户已存在
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.HasAccount).build());
					session.sendPacket(resPack);
					continue;
				}

				if (UserMgr.getInstance().checkNameExist(signupMsg.getName()))  //昵称已存在
				{
					NetPacket resPack = NetPacket.getNewNetPacket();
					resPack.init(MSG_ID.M_ErrorCode_VALUE, MsgError.MsError.newBuilder().setError(MsgError.ERROR_TYPE.HasName).build());
					session.sendPacket(resPack);
					continue;
				}

				//创建用户
				UserMgr.getInstance().createUser(signupMsg.getAccountId(), signupMsg.getPassword(), signupMsg.getName(), session);
			}
			else if (MSG_ID.M_EasyLogin_VALUE == packet.getId())  //测试用
			{
				MsgLogin.McEasyLogin easyLoginMsg = (MsgLogin.McEasyLogin)packet.getMsg();
				if (null == easyLoginMsg)
				{
					logger.error("[UpdateLogonRequest] 登录消息包错误, session IP: {}", session.getSessionAddress());
					session.closeSession();
					continue;
				}

				session.setMsgCount(2);

				UserInfo userInfo = UserMgr.getInstance().findUserInfoByAccount(easyLoginMsg.getAccountId());
				if (null != userInfo)  //用户已存在
				{
					//用户登录
					UserMgr.getInstance().login(userInfo.getId(), session);
					logger.info("[UpdateLogonRequest] 登录成功 AccountId = {}", userInfo.getAccountId());
				}
				else
				{
					//创建用户
					UserMgr.getInstance().createUserEasy(easyLoginMsg.getAccountId(), easyLoginMsg.getPassword(), easyLoginMsg.getName(), session);
				}
			}
			else
			{
				logger.error("[UpdateLogonRequest] 登录消息包错误, session IP: {}", session.getSessionAddress());
				session.closeSession();
				continue;
			}
		}
	}

	private void updateLogoffRequest () throws Exception
	{
		int num = 0;
		NetSession session = null;

		while (num < maxLogoffRequestPerTick && null != (session = netServer.dequeueLogoffClient()))
		{
			++num;
			if (mapSession.containsKey(session.getAccountId()))
			{
				PlayerSession playerSession = mapSession.get(session.getAccountId());
				removePlayerSession(playerSession);
			}
		}
	}

	private void updateKickRequest ()
	{
		PlayerSession playerSession = null;
		while (null != (playerSession = queueKickRequest.poll()))
		{
			kickPlayerSession(playerSession);
		}
	}

	private void updateSession ()
	{
		long curTick = DateTimeUtil.getNowTimeInMillis();
		for (PlayerSession playerSession : mapSession.values())
		{
			playerSession.update(curTick);
		}
	}

	public void requestKick (PlayerSession playerSession)
	{
		if(null == playerSession || null == playerSession.getNetSession())
		{
			logger.error("[RequestKick] PlayerSession或NetSession为null.");
			return;
		}

		if (!mapSession.containsKey(playerSession.getNetSession().getAccountId()))
		{
			logger.error("[RequestKick] MapSession找不到PlayerSession.");
			return;
		}

		if (playerSession.getNetSession().getState() != NetSession.SESSION_STATE.Closed)
		{
			playerSession.getNetSession().setState(NetSession.SESSION_STATE.Kick);
			queueKickRequest.add(playerSession);
		}
	}

	public void addPlayerSession(User user, PlayerSession session)
	{
		mapSession.put(user.getAccountId(), session);
	}

	private void removePlayerSession (PlayerSession playerSession)
	{
		if (mapSession.containsKey(playerSession.getMyUser().getAccountId()))
		{
			mapSession.remove(playerSession.getMyUser().getAccountId());
		}

		//做一些游戏逻辑上的离线操作
		UserMgr.getInstance().logout(playerSession.getMyUser());
		NetSession netSession = playerSession.getNetSession();
		playerSession.release();
		netSession.release();
	}

	private void kickPlayerSession (PlayerSession playerSession)
	{
		playerSession.getNetSession().closeSession();
	}
}
