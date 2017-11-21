package com.chatsystem.function;

/**
 * Pb消息序列化函数式接口
 */
@FunctionalInterface
public interface PbSerializer
{
	byte[] serialize (Object msg);
}
