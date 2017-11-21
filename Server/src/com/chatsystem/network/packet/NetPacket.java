package com.chatsystem.network.packet;

import com.chatsystem.network.MsgProtocol;
import com.chatsystem.network.pb.PbMsgParser;
import com.chatsystem.tools.ObjPool;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网络消息包
 */
public class NetPacket
{
	private static final Logger logger = LoggerFactory.getLogger(NetPacket.class);

	private static final ObjPool<NetPacket> pool = new ObjPool<>(NetPacket::create);  //对象池
	private int msgId = 0;  //消息编号
	private Object msg = null;  //消息体

	private static NetPacket create()
	{
		return new NetPacket();
	}

	public void init (int id, Object obj)
	{
		this.msgId = id;
		this.msg = obj;
	}

	public void clear ()
	{
		msgId = 0;
		msg = null;
	}

	public static NetPacket getNewNetPacket ()
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

	public void release ()
	{
		clear();
		pool.enqueue(this);
	}

	public int getId()
	{
		return msgId;
	}

	public Object getMsg ()
	{
		return msg;
	}

	public IoBuffer ToIoBuffer ()
	{
		if (0 == msgId|| null == msg)
		{
			logger.error("[ToIoBuffer] 协议号或协议体为空");
			return null;
		}

		byte[] data = PbMsgParser.getInstance().serialize(msgId, msg);

		if (null == data)
		{
			logger.error("[ToIoBuffer] 未注册协议序列化函数, MsgId = {}", msgId);
			return null;
		}

		//包长度
		int length = MsgProtocol.flagSize + MsgProtocol.lengthSize + MsgProtocol.msgCodeSize + data.length;

		IoBuffer buffer = IoBuffer.allocate(length);
		buffer.put(MsgProtocol.defaultFlag);
		buffer.putInt(MsgProtocol.msgCodeSize + data.length);
		buffer.putInt(msgId);
		buffer.put(data);
		buffer.flip();

		return buffer;
	}
}
