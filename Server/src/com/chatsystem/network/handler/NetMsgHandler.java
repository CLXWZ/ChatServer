package com.chatsystem.network.handler;

import com.chatsystem.network.NetServer;
import com.chatsystem.network.NetSession;
import com.chatsystem.network.packet.NetPacket;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当收到网络消息的处理
 */
public class NetMsgHandler extends IoHandlerAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(NetMsgHandler.class);

	private NetServer netServer = null;

	public NetMsgHandler(NetServer server)
	{
		super();
		netServer = server;
	}

	//连接建立时
	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		NetSession netSession = NetSession.getNewNetSession();
		if (null == netSession)
		{
			return;
		}

		netSession.init(session);
		netServer.onConnected(netSession);
		logger.info("建立了一个新连接 SessionId: {} NetSessionState: {} ip: {}", session.getId(), netSession.getState(), session.getRemoteAddress());
	}

	//收到消息时
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		NetPacket packet = (NetPacket)message;
		if (null == packet)
		{
			logger.error("接收消息时,packet为空  ip：{}", session.getRemoteAddress());
			return;
		}

		NetSession netSession = (NetSession)session.getAttribute(NetSession.ATTRIBUTE_KEY_NET_SESSION);
		if (null == netSession)
		{
			logger.error("接收消息时,NetSession为空  ip：{}", session.getRemoteAddress());
			return;
		}

		//logger.info("接收到新消息, SessionId={}，SessionState={}, msgCount={}, ip={}", session.getId(), netSession.getState(), netSession.getMsgCount(), session.getRemoteAddress());

		netSession.enqueuePacket(packet);
		netServer.onReceiveMsg(netSession);
	}

	//出现异常时
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		logger.error("连接异常:  {}", cause.getMessage());
	}

	//连接关闭时
	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		NetSession netSession = (NetSession)session.getAttribute(NetSession.ATTRIBUTE_KEY_NET_SESSION);
		if (null == netSession)
		{
			return;
		}

		logger.info("连接开始关闭, SessionId={}, SessionState={}, ip={}", session.getId(), netSession.getState(), session.getRemoteAddress());
		session.removeAttribute(NetSession.ATTRIBUTE_KEY_NET_SESSION);
		if (NetSession.SESSION_STATE.Normal == netSession.getState())
		{
			netSession.setState(NetSession.SESSION_STATE.Closed);
			netServer.onDisconnected(netSession);
		}
		else if (NetSession.SESSION_STATE.Kick == netSession.getState())  //连接处于待踢出状态
		{
			logger.info("[sessionClosed] Session处于Kick状态.");
			netSession.setState(NetSession.SESSION_STATE.Closed);
			netServer.onDisconnected(netSession);
		}

		logger.info("关闭了一个连接 ip: {}", session.getRemoteAddress());
	}
}
