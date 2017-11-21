package com.chatsystem;

import com.chatsystem.ban.BanMgr;
import com.chatsystem.chat.ChatMgr;
import com.chatsystem.chat.dispatcher.ChatDispatchMgr;
import com.chatsystem.chat.relation.RelationMgr;
import com.chatsystem.chat.room.ChatRoomMgr;
import com.chatsystem.command.CommandMgr;
import com.chatsystem.db.DBMgr;
import com.chatsystem.db.dao.UserDao;
import com.chatsystem.executor.ExecutorServiceMgr;
import com.chatsystem.msgprocess.MsgMgr;
import com.chatsystem.session.SessionMgr;
import com.chatsystem.tools.DateTimeUtil;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import com.chatsystem.user.UserMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 服务器程序
 */
public class Server
{
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	private static Server instance = new Server();
	private UserDao userDao = new UserDao();  //用户信息数据库访问接口
	private Queue<String> commandQueue = new ConcurrentLinkedQueue<>();   //服务器命令队列
	private int curFrameRate = 0;
	private int lastFrameRate = 0;
	private long lastFrameTime = 0;

	public static Server getInstance()
	{
		return instance;
	}

	private Server()
	{

	}

	public boolean init()
	{
		logger.info("[Init] 服务器初始化开始.");

		if (!ExecutorServiceMgr.getInstance().init())
		{
			logger.error("[Init] ExecutorServiceMgr初始化失败.");
			return false;
		}

		if (!ChatDispatchMgr.getInstance().init())
		{
			logger.error("[Init] ChatDispatchMgr初始化失败.");
			return false;
		}

		if (!DBMgr.getInstance().init("localhost", 27017, "chatsys"))
		{
			logger.error("[Init] DBMgr初始化失败.");
			return false;
		}

		if (!MsgMgr.getInstance().init())
		{
			logger.error("[Init] MsgMgr初始化失败.");
			return false;
		}

		if (!SessionMgr.getInstance().init(6666))
		{
			logger.error("[Init] SessionMgr初始化失败.");
			return false;
		}

		if (!UserMgr.getInstance().init(userDao))
		{
			logger.error("[Init] UserMgr初始化失败.");
			return false;
		}

		if (!ChatMgr.getInstance().init())
		{
			logger.error("[Init] ChatMgr初始化失败.");
			return false;
		}
		
		if (!RelationMgr.getInstance().init())
		{
			logger.error("[Init] RelationMgr初始化失败.");
			return false;
		}
		
		if (!ChatRoomMgr.getInstance().init())
		{
			logger.error("[Init] ChatRoomMgr初始化失败.");
			return false;
		}

		if (!CommandMgr.getInstance().init())
		{
			logger.error("[Init] CommandMgr初始化失败.");
			return false;
		}

		if (!BanMgr.getInstance().init())
		{
			logger.error("[Init] BanMgr初始化失败.");
			return false;
		}

		initCommand();

		logger.info("[Init] 服务器初始化完成.");

		return true;
	}

	private void initCommand()
	{
		CommandMgr.getInstance().registerCommand("broadcast", Server::broadcastMsg);
		CommandMgr.getInstance().registerCommand("banlogin", Server::banLogin);
		CommandMgr.getInstance().registerCommand("banchat", Server::banChat);
		CommandMgr.getInstance().registerCommand("frame", Server::frame);
	}

	public void update()
	{
		try
		{
			ExecutorServiceMgr.getInstance().update();  //更新辅助线程
		}
		catch (Exception ex)
		{
			logger.error("[Update] ExecutorServiceMgr 出现异常, message : ");
			ex.printStackTrace();
		}

		try
		{
			DBMgr.getInstance().update();  //更新数据库回调
		}
		catch(Exception ex)
		{
			logger.error("[Update] DBMgr 出现异常, message : ");
			ex.printStackTrace();
		}

		try
		{
			SessionMgr.getInstance().update();  //更新用户连接
		}
		catch (Exception ex)
		{
			logger.error("[Update] SessionMgr 出现异常, message : ");
			ex.printStackTrace();
		}

		try
		{
			UserMgr.getInstance().update();  //更新用户
		}
		catch (Exception ex)
		{
			logger.error("[Update] PlayerMgr 出现异常, message : ");
			ex.printStackTrace();
		}

		try
		{
			updateCommand();  //更新服务器命令
		}
		catch(Exception ex)
		{
			logger.error("[Update] updateCommand 出现异常, message : ");
			ex.printStackTrace();
		}

		try
		{
			updateFrame();  //更新服务器帧率
		}
		catch(Exception ex)
		{
			logger.error("[Update] updateFrame 出现异常, message : ");
			ex.printStackTrace();
		}
	}

	private void updateCommand()
	{
		String command = null;
		while (null != (command = commandQueue.poll()))
		{
			processCommand(command);
		}
	}

	private void updateFrame()
	{
		++curFrameRate;
		long curTime = DateTimeUtil.getNowTimeInMillis();
		if (curTime - lastFrameTime >= 1000)
		{
			lastFrameRate = curFrameRate;
			lastFrameTime = curTime;
			curFrameRate = 0;
		}
	}

	public void destroy()
	{
		DBMgr.getInstance().destroy();
	}

	public int getFrameRate()
	{
		return lastFrameRate;
	}

	public void enqueueCommand(String command)
	{
		commandQueue.offer(command);
	}

	private void processCommand(String str)
	{
		str = str.trim().toLowerCase();
		String[] strs = str.split(" ");
		if (null == strs || strs.length <= 0)
		{
			return;
		}

		String command = strs[0];
		String[] args = new String[0];
		if (strs.length > 1)
		{
			args = Arrays.copyOfRange(strs, 1, strs.length);
		}

		CommandMgr.getInstance().process(command, args);
	}

	//广播全服消息
	private static void broadcastMsg(String[] args)
	{
		String msg = getMsg(args, 0);
		UserMgr.getInstance().broadcastSystemMsg(msg);
	}

	private static String getMsg(String[] args, int startIndex)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = startIndex; i < args.length; ++i)
		{
			if (i == startIndex)
			{
				sb.append(args[i]);
			}
			else
			{
				sb.append(" " + args[i]);
			}
		}

		return sb.toString();
	}

	//禁止用户登录
	private static void banLogin(String[] args)
	{
		boolean isBan = args[0].equals("1") ? true : false;
		long id = Long.parseLong(args[1]);
		UserInfo info = UserMgr.getInstance().findUserInfoById(id);
		if (null == info)
		{
			return;
		}

		if (isBan)
		{
			BanMgr.getInstance().addBanLogin(info.getId());  //加入禁止登录名单

			User user = UserMgr.getInstance().findOnlineUser(id);
			if (null != user)
			{
				SessionMgr.getInstance().requestKick(user.getPlayerSession());  //如果这个用户在线，踢掉他
			}
		}
		else
		{
			BanMgr.getInstance().removeBanLogin(info.getId());  //移出禁止登录名单
		}
	}

	//禁止用户聊天
	private static void banChat(String[] args)
	{
		boolean isBan = args[0].equals("1") ? true : false;
		long id = Long.parseLong(args[1]);
		UserInfo info = UserMgr.getInstance().findUserInfoById(id);
		if (null == info)
		{
			return;
		}

		if (isBan)
		{
			BanMgr.getInstance().addBanChat(info.getId());  //加入禁止聊天名单
		}
		else
		{
			BanMgr.getInstance().removeBanChat(info.getId());  //移出禁止聊天名单
		}
	}

	//输出服务器帧率
	private static void frame(String[] args)
	{
		System.out.println("***************************");
		System.out.println(String.format("*FrameRate: %d", Server.getInstance().getFrameRate()));
		System.out.println(String.format("*ServerUserCount: %d", UserMgr.getInstance().getUserCount()));
		System.out.println(String.format("*OnlineUserCount: %d", UserMgr.getInstance().getOnlineUserCount()));
		System.out.println("***************************");
	}
}
