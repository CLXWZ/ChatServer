package com.chatsystem.db.dao;

import com.chatsystem.db.DBMgr;
import com.chatsystem.msg.MsgChat.CHAT_TYPE;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 用户信息数据库接口
 */
public class UserDao
{
	private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

	//获取全部用户基本信息
	public ArrayList<UserInfo> getAllUserInfos ()
	{
		ArrayList<Document> allUsers = DBMgr.getInstance().find("users", null);
		if (null == allUsers)
		{
			return null;
		}

		ArrayList<UserInfo> userInfos = new ArrayList<>();
		for (Document doc : allUsers)
		{
			userInfos.add(new UserInfo(doc));
		}

		return userInfos;
	}

	//创建新用户
	public void createUser (UserInfo userInfo, Consumer<Boolean> callback)
	{
		DBMgr.getInstance().insertOne("users", userInfo.toBson(), callback);
		return;
	}

	//获取用户数据
	public void getUserFromDB(long userId, Consumer<ArrayList<Document>> callback)
	{
		DBMgr.getInstance().findAsync("users", new Document("id", userId), callback);
		return;
	}

	//存盘
	public void saveUser(User user)
	{
		DBMgr.getInstance().replaceOne("users", new Document("id", user.getId()), user.toBson(), null);
	}
	
	public void saveOfflineMsg(UserInfo userInfo, long fromId, String fromUserName, String fromRoomName, CHAT_TYPE chatType, String text)
	{
		DBMgr.getInstance().findAsync("users", new Document("id", userInfo.getId()), (docList) ->
		{
			if (null == docList)
			{
				logger.error("[saveOfflineMsg] 获取用户数据失败. AccountId = {}", userInfo.getAccountId());
				return;
			}

			Document doc = docList.get(0);
			ArrayList<Document> offlineMsgs = (ArrayList<Document>)doc.get("offlineMsg");
			if (null != offlineMsgs)
			{
				offlineMsgs.add(new Document().append("FromId", fromId).
						append("FromUserName", fromUserName).
	                    append("FromRoomName", fromRoomName).
	                    append("ChatType", chatType.getNumber()).
	                    append("Msg", text));
			}
			doc.append("offlineMsg", offlineMsgs);
			
			DBMgr.getInstance().replaceOne("users", new Document("id", doc.getLong("id")), doc, null);
		});
	}
}
