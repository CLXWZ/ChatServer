package com.chatsystem.user;

import com.chatsystem.chat.relation.UserRelationCtrl;
import com.chatsystem.chat.room.ChatRoomMgr;
import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgError;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.msg.MsgUser;
import com.chatsystem.msg.MsgChat.CHAT_TYPE;
import com.chatsystem.msg.MsgChat.MsChat;
import com.chatsystem.msg.MsgIds.MSG_ID;
import com.chatsystem.msg.MsgLogin.MsUserInit;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.session.PlayerSession;
import com.chatsystem.session.SessionMgr;
import com.chatsystem.tools.ObjPool;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * 用户类
 */
public class User
{
	private static final Logger logger = LoggerFactory.getLogger(User.class);

	private static final ObjPool<User> pool = new ObjPool<User>(User::create);
	private long id;
	private String accountId;        //玩家ID
	private String password;         //密码
	private String name;             //昵称
	private PlayerSession playerSession;
	private long nextSaveTick;       //下一次的存盘时间
	private Queue<MsgChat.MsChat> chatMsgs = new LinkedList<>();  //聊天信息
    private ArrayList<MsChat> offlineMsgs = new ArrayList<>();  //离线消息
	
	private final UserRelationCtrl userRelationCtrl = new UserRelationCtrl(this);

	private static User create()
	{
		return new User();
	}

	public static User createUser(long id, String account, String pwd, String name)
	{
		try
		{
			User user = pool.dequeue();
			if (null == user)
			{
				return null;
			}

			if (!user.init(id, account, pwd, name))
			{
				user.destroy();
				pool.enqueue(user);
				return null;
			}

			return user;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static User createUser(Document doc)
	{
		try
		{
			User user = pool.dequeue();
			if (null == user)
			{
				return null;
			}

			if (!user.init(doc))
			{
				user.destroy();
				pool.enqueue(user);
				return null;
			}

			return user;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void release(User user)
	{
		if (null != user)
		{
			user.destroy();
			pool.enqueue(user);
		}
	}

	public boolean init (long id, String account, String pwd, String name)
	{
		this.id = id;
		this.accountId = account;
		this.password = pwd;
		this.name = name;

		//初始化好友控制器
		if (!userRelationCtrl.init(null))
		{
			return false;
		}

		return true;
	}

	public boolean init(Document doc)
	{
		if (!fromBson(doc))
		{
			return false;
		}

		//初始化好友控制器
		if (!userRelationCtrl.init(doc))
		{
			return false;
		}

		return true;
	}

	public void update()
	{
		MsgChat.MsChat chatMsg = null;
		while (null != (chatMsg = chatMsgs.poll()))
		{
			sendPbMsg(MsgIds.MSG_ID.M_Chat_VALUE, chatMsg);
		}
	}

	public void destroy()
	{
		this.id = 0;
		this.accountId = null;
		this.password = null;
		this.name = null;
		this.playerSession = null;
		chatMsgs.clear();
		offlineMsgs.clear();
		
		userRelationCtrl.destroy();
	}

	public boolean fromBson(Document doc)
	{
		this.id = doc.getLong("id");
		this.accountId = doc.getString("account");
		this.password = doc.getString("password");
		this.name = doc.getString("name");
		
		//离线消息
		ArrayList<Document> offlineMsgDoc = (ArrayList<Document>)doc.get("offlineMsg");
		if (null != offlineMsgDoc)
		{
			for (Document d : offlineMsgDoc)
			{
				offlineMsgs.add(MsChat.newBuilder().setChatType(CHAT_TYPE.valueOf(d.getInteger("ChatType"))).
						                            setFromUserId(d.getLong("FromId")).
						                            setFromUserName(d.getString("FromUserName")).
						                            setFromRoomName(d.getString("FromRoomName")).
						                            setText(d.getString("Msg")).build());
			}
		}
		
		return true;
	}

	public Document toBson()
	{
		return new Document("id", id).
				append("account", accountId).
				append("password", password).
				append("name", name).
				append("RelationCtrl", userRelationCtrl.toBson()).
				append("offlineMsg", new ArrayList<Document>());
	}

	public void onLogin()
	{
		//通知好友我上线了
		userRelationCtrl.notifyLogin();

		//发送离线消息
		for (MsChat msg : offlineMsgs)
		{
			sendPbMsg(MSG_ID.M_Chat_VALUE, msg);
		}
		offlineMsgs.clear();
	}

	public void onLogout()
	{
		//通知好友我下线了
		userRelationCtrl.notifyLogoff();
	}

	public void enqueueChatMsg(MsgChat.MsChat pb)
	{	
		chatMsgs.offer(pb);
	}

	public MsgUser.PbUser toPb()
	{
		return MsgUser.PbUser.newBuilder().setAccount(this.accountId).
				                           setId(this.id).
				                           setName(this.name).build();
	}

	public void sendUserInitInfo()
	{
		sendPbMsg(MsgIds.MSG_ID.M_UserInit_VALUE, MsUserInit.newBuilder().setUser(toPb()).
				                                                          setRelation(userRelationCtrl.toPb()).
				                                                          setChatRoom(ChatRoomMgr.getInstance().chatRoomInit(this.id)).build());
	}

	public void sendPacket(NetPacket packet)
	{
		if (null != playerSession)
		{
			playerSession.sendPacket(packet);
		}
	}

	public void sendPbMsg(int msgId, Object msg)
	{
		if (null != playerSession)
		{
			playerSession.sendPbMsg(msgId, msg);
		}
	}

	public void sendErrorMsg(MsgError.ERROR_TYPE err)
	{
		if (null != playerSession)
		{
			playerSession.sendErrorMsg(err);
		}
	}

	public long getId()
	{
		return id;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public String getPassword()
	{
		return password;
	}

	public String getName()
	{
		return name;
	}

	public PlayerSession getPlayerSession()
	{
		return playerSession;
	}

	public void setPlayerSession(PlayerSession playerSession)
	{
		this.playerSession = playerSession;
		SessionMgr.getInstance().addPlayerSession(this, playerSession);
	}

	public long getNextSaveTick()
	{
		return nextSaveTick;
	}

	public void setNextSaveTick(long nextSaveTick)
	{
		this.nextSaveTick = nextSaveTick;
	}
	
	public UserRelationCtrl getUserRelationCtrl() 
	{
		return userRelationCtrl;
	}
}
