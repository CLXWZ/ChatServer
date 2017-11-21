package com.chatsystem.network;

import com.chatsystem.network.codec.NetProtocolCodecFactory;
import com.chatsystem.network.handler.NetMsgHandler;
import com.chatsystem.network.thread.ServerThreadFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 服务器监听类，负责接收客户端连接
 */
public class NetServer implements INetEvent
{
	private static final Logger logger = LoggerFactory.getLogger(NetServer.class);

	private NioSocketAcceptor acceptor = null;  //网络连接监听者
	private OrderedThreadPoolExecutor threadPool = null;  //io消息处理线程池
	private final ConcurrentLinkedQueue<NetSession> logonClient = new ConcurrentLinkedQueue<>();  //请求登录客户端队列
	private final ConcurrentLinkedQueue<NetSession> logoffClient = new ConcurrentLinkedQueue<>(); //请求登出客户端队列

	public boolean startListen (int listenPort)
	{
		logger.info("开始监听客户端的连接");

		acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(100);
		acceptor.setReuseAddress(true);
		acceptor.setHandler(new NetMsgHandler(this));

		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		//设置线程池
		threadPool = new OrderedThreadPoolExecutor(500);
		threadPool.setThreadFactory(new ServerThreadFactory("IOThreadPool"));
		chain.addLast("threadPool", new ExecutorFilter(threadPool));
		//设置编解码过滤器
		chain.addLast("codec", new ProtocolCodecFilter(new NetProtocolCodecFactory()));

		int recvSize = 2048;
		int sendSize = 2048;
		int timeout = 10;

		SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
		sessionConfig.setReuseAddress(true);
		sessionConfig.setReceiveBufferSize(recvSize);
		sessionConfig.setSendBufferSize(sendSize);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSoLinger(0);
		sessionConfig.setIdleTime(IdleStatus.READER_IDLE, timeout);

		try
		{
			acceptor.bind(new InetSocketAddress(listenPort));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return true;
	}

	public void stop () throws InterruptedException
	{
		acceptor.unbind();
		threadPool.shutdown();
		threadPool.awaitTermination(5, TimeUnit.SECONDS);
		acceptor.dispose(true);
	}

	public NetSession dequeueLogonClient ()
	{
		return logonClient.poll();
	}

	public NetSession dequeueLogoffClient ()
	{
		return logoffClient.poll();
	}

	@Override
	public void onConnected(NetSession session)
	{

	}

	@Override
	public void onReceiveMsg(NetSession session)
	{
		if (session.getMsgCount() < 2)
		{
			logonClient.add(session);
			session.addMsgCount();
		}
	}

	@Override
	public void onDisconnected(NetSession session)
	{
		logoffClient.add(session);
	}
}
