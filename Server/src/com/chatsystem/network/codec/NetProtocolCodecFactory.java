package com.chatsystem.network.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 对象工厂，提供了获取编码器和解码器的函数接口
 */
public class NetProtocolCodecFactory implements ProtocolCodecFactory
{
	private final NetMsgEncoder msgEncoder;
	private final NetMsgDecoder msgDecoder;

	public NetProtocolCodecFactory()
	{
		msgEncoder = new NetMsgEncoder();
		msgDecoder = new NetMsgDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception
	{
		return msgDecoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception
	{
		return msgEncoder;
	}
}
