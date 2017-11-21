package com.chatsystem.chat.relation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatsystem.msg.MsgError.ERROR_TYPE;
import com.chatsystem.msg.MsgRelation.RELATION_TYPE;
import com.chatsystem.user.User;
import com.chatsystem.user.UserMgr;

/**
 * 用户关系管理器
 */
public class RelationMgr 
{
	private static final Logger logger = LoggerFactory.getLogger(RelationMgr.class);
	
	private static final RelationMgr instance = new RelationMgr();

	private RelationMgr ()
	{

	}

	public static RelationMgr getInstance ()
	{
		return instance;
	}
	
	public boolean init()
	{
		return true;
	}
	
	public void destroy()
	{
		
	}
	
	public boolean addRelation(User user, RELATION_TYPE type, String groupName, long otherId)
	{
		if (type != RELATION_TYPE.Friend)
		{
			return false;
		}

		User otherUser = UserMgr.getInstance().findOnlineUser(otherId);
		if (null == otherUser)
		{
			user.sendErrorMsg(ERROR_TYPE.TargetOffline);
			return false;
		}
		
		if (!user.getUserRelationCtrl().addRelation(type, groupName, otherId))
		{
			return false;
		}
		
		if (!otherUser.getUserRelationCtrl().addRelation(type, UserRelationCtrl.DEFAULT_FRIEND_GROUP, user.getId()))
		{
			return false;
		}
		
		return true;
	}
}
