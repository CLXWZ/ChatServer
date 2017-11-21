package com.chatsystem.network.codec;

import com.chatsystem.network.packet.NetPacket;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 消息编码器
 */
public class NetMsgEncoder extends ProtocolEncoderAdapter
{
	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput output) throws Exception
	{
		NetPacket packet = (NetPacket)obj;

		if (null == packet)
		{
			throw new Exception("[NetMsgEncoder] 数据报为空！");
		}

		output.write(packet.ToIoBuffer());

		//回收packet
		packet.release();
	}
}
