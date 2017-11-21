package com.chatsystem.network;

public interface MsgProtocol
{
	//默认flag值
	byte defaultFlag = 0x11;
	//最大包长度
	int maxPackLength = 40480;
	//标识占的byte数
	int flagSize = 1;
	//包长度占用byte数
	int lengthSize = 4;
	//消息号占用的byte数
	int msgCodeSize = 4;
}
