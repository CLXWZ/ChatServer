package com.chatsystem.function;

/**
 * Pb消息反序列化函数式接口
 */
@FunctionalInterface
public interface PbDeserializer
{
	Object deserialize(byte[] bytes);
}
