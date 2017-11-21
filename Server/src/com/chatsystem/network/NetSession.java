package com.chatsystem.network;

import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.tools.ObjPool;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 表示一个客户端连接
 */
public class NetSession
{
	public enum SESSION_STATE
	{
		Normal,  //表示正常状态
		Kick,  //表示服务器正在踢掉此连接
		Closed,  //表示此连接已关闭
	}

	private static final ObjPool<NetSession> pool = new ObjPool<>(NetSession::create);  //对象池

	public static final AttributeKey ATTRIBUTE_KEY_NET_SESSION = new AttributeKey(NetSession.class, "net.session");

	private IoSession session = null;  //客户端连接

	private ConcurrentLinkedQueue<NetPacket> packQueue = new ConcurrentLinkedQueue<>();

	private int msgCount;  //接受的消息数量

	private String accountId = null;  //表示NetSession所属的User

	private SESSION_STATE state = SESSION_STATE.Normal;

	private Object sessionLock = new Object();  //对象锁

	private static NetSession create()
	{
		return new NetSession();
	}

	public void init (IoSession ioSession)
	{
		this.session = ioSession;
		this.session.setAttribute(ATTRIBUTE_KEY_NET_SESSION, this);
	}

	public void clear ()
	{
		session = null;
		msgCount = 0;
		accountId = null;
		state = SESSION_STATE.Normal;
		for (NetPacket packet : packQueue)
		{
			packet.release();
		}
		packQueue.clear();
	}

	//获取一条新连接
	public static NetSession getNewNetSession ()
	{
		try
		{
			return pool.dequeue();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	//回收NetSession
	public void release ()
	{
		clear();
		pool.enqueue(this);
	}

	public void setState (SESSION_STATE sessionState)
	{
		synchronized (sessionLock)
		{
			this.state = sessionState;
		}
	}

	public SESSION_STATE getState ()
	{
		synchronized (sessionLock)
		{
			return this.state;
		}
	}

	public void addMsgCount ()
	{
		++msgCount;
	}

	public int getMsgCount ()
	{
		return msgCount;
	}

	public void setMsgCount(int count)
	{
		msgCount = count;
	}

	public void auth (String val)
	{
		this.accountId = val;
	}

	public boolean isAuth ()
	{
		return this.accountId != null;
	}

	public String getAccountId ()
	{
		return this.accountId;
	}

	public void enqueuePacket (NetPacket packet)
	{
		if (null == packet)
		{
			return;
		}

		packQueue.offer(packet);
	}

	public NetPacket dequeuePacket ()
	{
		return packQueue.poll();
	}

	//发送消息
	public void sendPacket (NetPacket packet)
	{
		if (SESSION_STATE.Closed == state)
		{
			return;
		}

		if (null == session)
		{
			return;
		}

		session.write(packet);
	}

	//关闭连接
	public void closeSession ()
	{
		if (SESSION_STATE.Closed == state)
		{
			return;
		}

		try
		{
			session.close(true);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public String getSessionAddress ()
	{
		return session.getRemoteAddress().toString();
	}

	public IoSession getSession()
	{
		return session;
	}
}
