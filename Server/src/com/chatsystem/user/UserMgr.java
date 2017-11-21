package com.chatsystem.user;

import com.chatsystem.chat.dispatcher.ChatDispatchMgr;
import com.chatsystem.db.dao.UserDao;
import com.chatsystem.msg.MsgChat;
import com.chatsystem.msg.MsgIds;
import com.chatsystem.msg.MsgLogin;
import com.chatsystem.msg.MsgChat.CHAT_TYPE;
import com.chatsystem.msg.MsgError.ERROR_TYPE;
import com.chatsystem.msg.MsgIds.MSG_ID;
import com.chatsystem.msg.MsgUser.MsFindUser;
import com.chatsystem.msg.MsgUser.MsGetAllUsers;
import com.chatsystem.network.NetSession;
import com.chatsystem.network.packet.NetPacket;
import com.chatsystem.session.PlayerSession;
import com.chatsystem.tools.DateTime;
import com.chatsystem.tools.DateTimeUtil;
import com.chatsystem.tools.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 用户管理器
 */
public class UserMgr
{
	private static final Logger logger = LoggerFactory.getLogger(UserMgr.class);

	private static final UserMgr instance = new UserMgr();

	private UserDao userDao;
	private final Map<Long, User> onlineUsers = new HashMap<>();        //在线用户记录
	private final Map<Long, UserInfo> userInfos = new HashMap<>();      //玩家角色基本信息
	private final Map<String, UserInfo> accountIdPairInfos = new HashMap<>();
	private final Map<String, UserInfo> namePairInfos = new HashMap<>();
	private final Queue<Long> flushQueue = new LinkedList<Long>();
	private long curUserId;

	private UserMgr()
	{

	}

	public static UserMgr getInstance()
	{
		return instance;
	}

	public boolean init (UserDao dao)
	{
		userDao = dao;

		if (null == userDao)
		{
			logger.error("[Init] 初始化失败.");
			return false;
		}

		//从数据库中获取全部用户基本信息
		ArrayList<UserInfo> infos = userDao.getAllUserInfos();

		if (null != infos)
		{
			for (UserInfo userInfo : infos)
			{
				userInfos.put(userInfo.getId(), userInfo);
				accountIdPairInfos.put(userInfo.getAccountId(), userInfo);
				namePairInfos.put(userInfo.getName(), userInfo);
			}
		}

		curUserId = getMaxUserId();
		return true;
	}

	public void update()
	{
		saveUsers();
	}

	public boolean createUser (String accountId, String pwd, String name, NetSession session)
	{
		UserInfo userInfo = new UserInfo(genUserId(), accountId, pwd, name);
		userDao.createUser(userInfo, (res) ->
		{
			if (res)
			{
				userInfos.put(userInfo.getId(), userInfo);
				accountIdPairInfos.put(userInfo.getAccountId(), userInfo);

				//将登录结果返回给客户端
				NetPacket resPack = NetPacket.getNewNetPacket();
				if (null != resPack)
				{
					resPack.init(MSG_ID.M_SignUp_VALUE, MsgLogin.MsSignUp.newBuilder().build());
					session.sendPacket(resPack);
				}
			}
			else
			{
				logger.error("[createUser] 创建用户失败. UserId = {}, UserName = {}", userInfo.getId(), userInfo.getName());
			}
		});

		return true;
	}

	public boolean createUserEasy(String accountId, String pwd, String name, NetSession session)
	{
		UserInfo userInfo = new UserInfo(genUserId(), accountId, pwd, name);
		userDao.createUser(userInfo, (res) ->
		{
			if (res)
			{
				userInfos.put(userInfo.getId(), userInfo);
				accountIdPairInfos.put(userInfo.getAccountId(), userInfo);

				//用户登录
				UserMgr.getInstance().login(userInfo.getId(), session);
				logger.info("[UpdateLogonRequest] 登录成功 AccountId = {}", userInfo.getAccountId());
			}
			else
			{
				logger.error("[createUser] 创建用户失败. UserId = {}, UserName = {}", userInfo.getId(), userInfo.getName());
			}
		});

		return true;
	}

	public boolean login (long userId, NetSession session)
	{
		if (!userInfos.containsKey(userId))
		{
			return false;
		}

		if (onlineUsers.containsKey(userId))
		{
			return false;
		}

		userDao.getUserFromDB(userId, (docList) ->
		{
			if (null == docList)
			{
				logger.error("[login] 登录失败. UserId = {}", userId);
				return;
			}

			if (!userInfos.containsKey(userId))
			{
				logger.error("[login] 找不到用户信息. UserId = {}", userId);
				return;
			}

			User user = User.createUser(docList.get(0));
			if (null == user)
			{
				logger.error("[login] 找不到用户数据. UserId = {}", userId);
				return;
			}

			PlayerSession playerSession = PlayerSession.create(user, session);
			if (null != playerSession)
			{
				user.setPlayerSession(playerSession);
			}
			onlineUsers.put(user.getId(), user);

			ChatDispatchMgr.getInstance().addUser(user);
			user.sendUserInitInfo();  //发送初始化消息
			user.onLogin();  //用户登录操作
			saveUserOnTime(user);//处理完上线逻辑后，需要存盘
		});

		return true;
	}

	public void logout (User user)
	{
		if (null == user)
		{
			return;
		}

		if (!onlineUsers.containsKey(user.getId()))
		{
			return;
		}

		onlineUsers.remove(user.getId());
		user.onLogout();  //玩家离线操作
		ChatDispatchMgr.getInstance().removeUser(user.getId());
		saveUserOnTime(user);  //保存玩家数据
		User.release(user); //释放player
	}

	public void saveUsers ()
	{
		while (flushQueue.size() > 0)
		{
			long userId = flushQueue.peek();
			User user = findOnlineUser(userId);
			if (null != user)
			{
				if (user.getNextSaveTick() > DateTimeUtil.getNowTimeInMillis())
				{
					break;
				}

				flushQueue.poll();
				saveUserOnTime(user);
			}
			else
			{
				flushQueue.poll();
			}
		}
	}

	public void saveUserOnTime (User user)
	{
		syncFromUser(user);  //同步UserInfo
		userDao.saveUser(user);
		user.setNextSaveTick(DateTimeUtil.getNowAfterTimeInMillis(DateTime.MINUTE_FIELD, 5));
		flushQueue.add(user.getId());
	}

	protected void syncFromUser (User user)
	{
		if (!userInfos.containsKey(user.getId()))
		{
			logger.error("[SyncFromUser] 找不到用户. UserId = {}", user.getId());
			return;
		}

		UserInfo userInfo = userInfos.get(user.getId());
		userInfo.syncFromUser(user);
	}

	public long genUserId()
	{
		return ++curUserId;
	}

	public UserInfo findUserInfoByAccount(String accountId)
	{
		return accountIdPairInfos.get(accountId);
	}
	
	public UserInfo findUserInfoById(long id)
	{
		return userInfos.get(id);
	}

	public User findOnlineUser(long userId)
	{
		return onlineUsers.get(userId);
	}
	
	public boolean getAllUsers(User user, boolean isOnline)
	{
		MsGetAllUsers.Builder msGetAllUserBuilder = MsGetAllUsers.newBuilder();
		if (isOnline)
		{
			for (User u : onlineUsers.values())
			{
				msGetAllUserBuilder.addUsers(u.toPb());
			}
		}
		else
		{
			for (UserInfo u : userInfos.values())
			{
				msGetAllUserBuilder.addUsers(u.toPb());
			}
		}
		msGetAllUserBuilder.setIsOnline(isOnline);
		
		user.sendPbMsg(MSG_ID.M_GetAllUsers_VALUE, msGetAllUserBuilder.build());
		return true;
	}
	
	public void FindUser(User user, String name)
	{
		UserInfo userInfo = null;
		if (StringUtils.isNumberic(name))
		{
			userInfo = userInfos.get(Long.parseLong(name));
			if (null != userInfo)
			{
				user.sendPbMsg(MSG_ID.M_FindUser_VALUE, MsFindUser.newBuilder().setUser(userInfo.toPb()).build());
				return;
			}
		}
		
		userInfo = namePairInfos.get(name);
		if (null != userInfo)
		{
			user.sendPbMsg(MSG_ID.M_FindUser_VALUE, MsFindUser.newBuilder().setUser(userInfo.toPb()).build());
			return;
		}
		
		user.sendErrorMsg(ERROR_TYPE.NoUser);
	}

	public boolean checkNameExist(String name)
	{
		return namePairInfos.containsKey(name);
	}
	
	public void saveOfflineMsg(UserInfo userInfo, long fromId, String fromUserName, String fromRoomName, CHAT_TYPE chatType, String text)
	{
		userDao.saveOfflineMsg(userInfo, fromId, fromUserName, fromRoomName, chatType, text);
	}

	public void broadcastSystemMsg(String text)
	{
		MsgChat.MsChat msChat = MsgChat.MsChat.newBuilder().
				setChatType(CHAT_TYPE.System).
				setFromUserId(-1).
				setFromUserName("系统消息").
				setText(text).build();

		for (User user : onlineUsers.values())
		{
			user.enqueueChatMsg(msChat);
		}
	}

	public int getUserCount()
	{
		return userInfos.size();
	}

	public int getOnlineUserCount()
	{
		return onlineUsers.size();
	}

	private long getMaxUserId()
	{
		long curId = 0;
		Set<Long> userIds = userInfos.keySet();
		for (Long id : userIds)
		{
			if (id > curId)
			{
				curId = id;
			}
		}

		return curId;
	}
}
