package com.chatsystem.msgprocess;

import com.chatsystem.function.MsgHandler;
import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.msg.MsgLogin;
import com.chatsystem.msg.MsgUser;
import com.chatsystem.msg.MsgError.MsError;
import com.chatsystem.msg.MsgIds.MSG_ID;
import com.chatsystem.msg.MsgLogin.MsUserInit;
import com.chatsystem.msg.MsgRelation.McAddRelation;
import com.chatsystem.msg.MsgRelation.McAddRelationGroup;
import com.chatsystem.msg.MsgRelation.McDeleteRelation;
import com.chatsystem.msg.MsgRelation.McMoveRelation;
import com.chatsystem.msg.MsgRelation.MsAddRelation;
import com.chatsystem.msg.MsgRelation.MsAddRelationGroup;
import com.chatsystem.msg.MsgRelation.MsDeleteRelation;
import com.chatsystem.msg.MsgRelation.MsMoveRelation;
import com.chatsystem.msg.MsgUser.McFindUser;
import com.chatsystem.msg.MsgUser.MsFindUser;
import com.chatsystem.msgprocess.NetMsgHandlers.MSG_TYPE;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.network.pb.PbMsgParser;
import com.chatsystem.session.PlayerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息管理器
 */
public class MsgMgr
{
	private static final Logger logger = LoggerFactory.getLogger(MsgMgr.class);

	private static final MsgMgr instance = new MsgMgr();

	private final NetMsgHandlers<MsgHandler> netMsgHandlers = new NetMsgHandlers<>();

	private MsgMgr ()
	{

	}

	public static MsgMgr getInstance ()
	{
		return instance;
	}

	public boolean init ()
	{
		//注册消息处理方法
		registerMsgHandler();
		//处理消息解析方法
		registerMsgParser();
		return true;
	}

	public void destroy ()
	{

	}

	public boolean handleMsg (PlayerSession playerSession, NetPacket packet)
	{
		MsgHandler msgHandler = netMsgHandlers.getMsgHandler(packet.getId());

		if (null == msgHandler)
		{
			logger.error("[HandleMsg] 找不到消息处理方法. MsgId = {}", packet.getId());
			return false;
		}

		MSG_TYPE msgType = netMsgHandlers.getMsgType(packet.getId());

		if (MSG_TYPE.None == msgType)
		{
			logger.error("[HandleMsg] 找不到消息处理类型. MsgId = {}", packet.getId());
			return false;
		}

		if (null == packet.getMsg())
		{
			logger.error("[HandleMsg] 消息体为空. MsgId = {}", packet.getId());
			return false;
		}

		if (MSG_TYPE.Main == msgType)  //在主线程中执行
		{
			msgHandler.process(playerSession, packet.getMsg());
		}
		else if (MSG_TYPE.Assistance == msgType)  //分发到分线程中
		{
			//暂时在主线程处理
			msgHandler.process(playerSession, packet.getMsg());
		}

		return true;
	}

	//注册消息处理方法
	private void registerMsgHandler ()
	{
		//获取全部用户
		netMsgHandlers.register(MsgIds.MSG_ID.M_GetAllUsers_VALUE, MsgUserAction::handleGetAllUsers, MSG_TYPE.Main);
		//加关系
		netMsgHandlers.register(MSG_ID.M_AddRelation_VALUE, MsgRelationAction::handleAddRelation, MSG_TYPE.Main);
		//删关系
		netMsgHandlers.register(MSG_ID.M_DelRelation_VALUE, MsgRelationAction::handleDelRelation, MSG_TYPE.Main);
		//加分组
		netMsgHandlers.register(MSG_ID.M_AddRelationGroup_VALUE, MsgRelationAction::handleAddGroup, MSG_TYPE.Main);
		//移动分组
		netMsgHandlers.register(MSG_ID.M_MoveRelation_VALUE, MsgRelationAction::handleMoveRelation, MSG_TYPE.Main);
		//聊天
		netMsgHandlers.register(MsgIds.MSG_ID.M_Chat_VALUE, MsgChatAction::handleChat, MSG_TYPE.Main);
		//搜索用户
		netMsgHandlers.register(MSG_ID.M_FindUser_VALUE, MsgUserAction::handleFindUser, MSG_TYPE.Main);
		//获取全部群组
		netMsgHandlers.register(MSG_ID.M_GetAllChatRoom_VALUE, MsgChatAction::handleGetAllChatRooms, MSG_TYPE.Main);
		//创建群组
		netMsgHandlers.register(MSG_ID.M_CreateChatRoom_VALUE, MsgChatAction::handleCreateChatRoom, MSG_TYPE.Main);
		//加入群组
		netMsgHandlers.register(MSG_ID.M_JoinChatRoom_VALUE, MsgChatAction::handleJoinChatRoom, MSG_TYPE.Main);
		//离开群组
		netMsgHandlers.register(MSG_ID.M_LeaveChatRoom_VALUE, MsgChatAction::handleLeaveChatRoom, MSG_TYPE.Main);
	}

	//注册消息解析方法
	private void registerMsgParser ()
	{
		//登录
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_Login_VALUE, (msg) -> { return ((MsgLogin.MsLogin)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_Login_VALUE, (bytes) ->
		{
			try
			{
				return MsgLogin.McLogin.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//注册
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_SignUp_VALUE, (msg) -> { return ((MsgLogin.MsSignUp)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_SignUp_VALUE, (bytes) ->
		{
			try
			{
				return MsgLogin.McSignUp.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//认证
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MSG_ID.M_Auth_VALUE, (bytes) ->
		{
			try
			{
				return MsgLogin.McAuth.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//测试用
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_EasyLogin_VALUE, (msg) -> { return ((MsgLogin.MsEasyLogin)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_EasyLogin_VALUE, (bytes) ->
		{
			try
			{
				return MsgLogin.McEasyLogin.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//初始化
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_UserInit_VALUE, (msg) -> { return ((MsUserInit)msg).toByteArray(); });

		//获取全部用户
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_GetAllUsers_VALUE, (msg) -> { return ((MsgUser.MsGetAllUsers)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_GetAllUsers_VALUE, (bytes) ->
		{
			try
			{
				return MsgUser.McGetAllUsers.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//加关系
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_AddRelation_VALUE, (msg) -> { return ((MsAddRelation)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_AddRelation_VALUE, (bytes) ->
		{
			try
			{
				return McAddRelation.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//删关系
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_DelRelation_VALUE, (msg) -> { return ((MsDeleteRelation)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_DelRelation_VALUE, (bytes) ->
		{
			try
			{
				return McDeleteRelation.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//加分组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_AddRelationGroup_VALUE, (msg) -> { return ((MsAddRelationGroup)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_AddRelationGroup_VALUE, (bytes) ->
		{
			try
			{
				return McAddRelationGroup.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//移动分组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_MoveRelation_VALUE, (msg) -> { return ((MsMoveRelation)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_MoveRelation_VALUE, (bytes) ->
		{
			try
			{
				return McMoveRelation.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//聊天
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_Chat_VALUE, (msg) -> { return ((MsgChat.MsChat)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_Chat_VALUE, (bytes) ->
		{
			try
			{
				return MsgChat.McChat.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});
		
		//搜索用户
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_FindUser_VALUE, (msg) -> { return ((MsFindUser)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_FindUser_VALUE, (bytes) ->
		{
			try
			{
				return McFindUser.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//通知上线
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_NotifyLogin_VALUE, (msg) -> { return ((MsgUser.MsNotifyLogin)msg).toByteArray(); });

		//通知下线
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_NotifyLogoff_VALUE, (msg) -> { return ((MsgUser.MsNotifyLogoff)msg).toByteArray(); });

		//获取全部群组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_GetAllChatRoom_VALUE, (msg) -> { return ((MsgChat.MsGetAllChatRoom)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_GetAllChatRoom_VALUE, (bytes) ->
		{
			try
			{
				return MsgChat.McGetAllChatRoom.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//创建群组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_CreateChatRoom_VALUE, (msg) -> { return ((MsgChat.MsCreateChatRoom)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_CreateChatRoom_VALUE, (bytes) ->
		{
			try
			{
				return MsgChat.McCreateChatRoom.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//加入群组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_JoinChatRoom_VALUE, (msg) -> { return ((MsgChat.MsJoinChatRoom)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_JoinChatRoom_VALUE, (bytes) ->
		{
			try
			{
				return MsgChat.McJoinChatRoom.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//离开群组
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_LeaveChatRoom_VALUE, (msg) -> { return ((MsgChat.MsLeaveChatRoom)msg).toByteArray(); });
		//反序列化
		PbMsgParser.getInstance().registerDeserializer(MsgIds.MSG_ID.M_LeaveChatRoom_VALUE, (bytes) ->
		{
			try
			{
				return MsgChat.McLeaveChatRoom.parseFrom(bytes);
			}
			catch (Exception ex)
			{
				logger.error(ex.getMessage());
				return null;
			}
		});

		//群组通知
		//序列化
		PbMsgParser.getInstance().registerSerializer(MSG_ID.M_ChatRoomNotify_VALUE, (msg) -> { return ((MsgChat.MsChatRoomNotify)msg).toByteArray(); });

		//错误信息
		//序列化
		PbMsgParser.getInstance().registerSerializer(MsgIds.MSG_ID.M_ErrorCode_VALUE, (msg) -> { return ((MsError)msg).toByteArray(); });
	}
}
