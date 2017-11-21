package com.chatsystem.network.codec;

import com.chatsystem.network.MsgProtocol;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.network.pb.PbMsgParser;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息解码器
 */
public class NetMsgDecoder extends CumulativeProtocolDecoder
{
	private static final Logger logger = LoggerFactory.getLogger(NetMsgDecoder.class);

	@Override
	protected boolean doDecode(IoSession session, IoBuffer ioBuffer, ProtocolDecoderOutput output) throws Exception
	{
		if (ioBuffer.remaining() < MsgProtocol.flagSize + MsgProtocol.lengthSize + MsgProtocol.msgCodeSize)
		{
			logger.error("[NetMsgDecoder] 数据包长度不足！ ip: {}", session.getRemoteAddress());
			return false;
		}

		ioBuffer.mark();

		byte flag = ioBuffer.get();

		if (0x11 == flag)
		{
			int length = ioBuffer.getInt();

			if (length <= 0 || length > MsgProtocol.maxPackLength)
			{
				logger.error("[NetMsgDecoder] 数据包长度异常！ ip：{}  length：{}", session.getRemoteAddress(), length);
				session.close(true);
				return false;
			}

			if (ioBuffer.remaining() >= length)
			{
				int msgId = ioBuffer.getInt();
				byte[] body = new byte[length - MsgProtocol.msgCodeSize];
				ioBuffer.get(body);

				Object msg = PbMsgParser.getInstance().deserialize(msgId, body);
				if (null == msg)  //解析ProtoBuf消息失败
				{
					logger.error("[NetMsgDecoder] 解析ProtoBuf消息失败");
					session.close(true);
					return false;
				}

				NetPacket packet = NetPacket.getNewNetPacket();
				if (null == packet) //获取消息包失败
				{
					logger.error("[NetMsgDecoder] 获取消息包失败");
					session.close(true);
					return false;
				}

				packet.init(msgId, msg);
				output.write(packet);

				return true;
			}
			else
			{
				logger.info("[NetMsgDecoder] 数据包尚不完整! ip：{}", session.getRemoteAddress());
				ioBuffer.reset();
				return false;
			}
		}
		else
		{
			logger.error("[NetMsgDecoder] 消息包flag错误！ ip：{}", session.getRemoteAddress());
			session.close(true);
			return false;
		}
	}
}
