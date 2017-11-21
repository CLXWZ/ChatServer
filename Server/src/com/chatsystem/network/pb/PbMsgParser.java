package com.chatsystem.network.pb;

import com.chatsystem.function.PbDeserializer;
import com.chatsystem.function.PbSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * ProtoBuf解析器
 */
public class PbMsgParser
{
	private static final PbMsgParser instance = new PbMsgParser();

	private PbMsgParser ()
	{

	}

	public static PbMsgParser getInstance ()
	{
		return instance;
	}

	private final Map<Integer, PbSerializer> serializers = new HashMap<>();  //序列化方法集合
	private final Map<Integer, PbDeserializer> deserializers = new HashMap<>();  //反序列化方法集合

	public void registerSerializer (int msgId, PbSerializer serializer)
	{
		if (serializers.containsKey(msgId))
		{
			return;
		}

		serializers.put(msgId, serializer);
	}

	public void registerDeserializer (int msgId, PbDeserializer deserializer)
	{
		if (deserializers.containsKey(msgId))
		{
			return;
		}

		deserializers.put(msgId, deserializer);
	}

	public void unRegisterSerializer (int msgId)
	{
		if (!serializers.containsKey(msgId))
		{
			return;
		}

		serializers.remove(msgId);
	}

	public void unRegisterDeserializer (int msgId)
	{
		if (!deserializers.containsKey(msgId))
		{
			return;
		}

		deserializers.remove(msgId);
	}

	//将指定消息序列化
	public byte[] serialize (int msgId, Object msg)
	{
		if (!serializers.containsKey(msgId))
		{
			return null;
		}

		return serializers.get(msgId).serialize(msg);
	}

	//将指定消息反序列化
	public Object deserialize (int msgId, byte[] bytes)
	{
		if (!deserializers.containsKey(msgId))
		{
			return null;
		}

		return deserializers.get(msgId).deserialize(bytes);
	}
}
