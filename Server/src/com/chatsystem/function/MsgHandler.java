package com.chatsystem.function;

import com.chatsystem.session.PlayerSession;

/**
 * 服务器消息处理函数式接口
 */
@FunctionalInterface
public interface MsgHandler
{
	void process (PlayerSession playerSession, Object msg);
}
