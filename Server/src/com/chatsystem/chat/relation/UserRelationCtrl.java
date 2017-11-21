package com.chatsystem.chat.relation;

import com.chatsystem.msg.MsgRelation;
import com.chatsystem.msg.MsgError.ERROR_TYPE;
import com.chatsystem.msg.MsgIds.MSG_ID;
import com.chatsystem.msg.MsgRelation.MsAddRelation;
import com.chatsystem.msg.MsgRelation.MsAddRelationGroup;
import com.chatsystem.msg.MsgRelation.MsDeleteRelation;
import com.chatsystem.msg.MsgRelation.MsMoveRelation;
import com.chatsystem.msg.MsgRelation.PbRelationInit;
import com.chatsystem.msg.MsgRelation.RELATION_TYPE;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import com.chatsystem.user.UserMgr;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用户好友控制器
 */
public class UserRelationCtrl
{
	private static final Logger logger = LoggerFactory.getLogger(UserRelationCtrl.class);
	
	public static final String DEFAULT_FRIEND_GROUP = "我的好友";
	public static final String DEFAULT_BLACK_GROUP = "黑名单";
	private final User owner;
	private final Map<RELATION_TYPE, Map<String, RelationGroup>> relations = new HashMap<>();
	private final Map<Long, RelationGroup> relationsByUserId = new HashMap<>();

	public UserRelationCtrl(User user)
	{
		owner = user;
	}
	
	public boolean init(Document doc)
	{
		if (null == doc || !doc.containsKey("RelationCtrl"))
		{
			RelationGroup friend = RelationGroup.createRelation(MsgRelation.RELATION_TYPE.Friend, DEFAULT_FRIEND_GROUP);
			if (null == friend)
			{
				return false;
			}
			addGroup(friend);

			RelationGroup blackList = RelationGroup.createRelation(MsgRelation.RELATION_TYPE.BlackList, DEFAULT_BLACK_GROUP);
			if (null == blackList)
			{
				return false;
			}
			addGroup(blackList);
		}
		else
		{
			Document relationCtrlDoc = (Document)doc.get("RelationCtrl");
			ArrayList<Document> relationArr = (ArrayList<Document>)relationCtrlDoc.get("Groups");
			for (Document bson : relationArr)
			{
				RelationGroup group = RelationGroup.createRelation(bson);
				if (null != group)
				{
					addGroup(group);
				}
			}
			
			for (Map<String, RelationGroup> map : relations.values())
			{
				for (RelationGroup group : map.values())
				{
					for (long id : group.getRelationUsers())
					{
						relationsByUserId.put(id, group);
					}
				}
			}
		}

		return true;
	}
	
	public void destroy()
	{
		relations.clear();
		relationsByUserId.clear();
	}
	
	public Document toBson()
	{
		Document doc = new Document();
		ArrayList<Document> list = new ArrayList<>();
		for (Map<String, RelationGroup> map : relations.values())
		{
			for (RelationGroup group : map.values())
			{
				list.add(group.toBson());
			}
		}
		doc.append("Groups", list);
		
		return doc;
	}

	private void addGroup(RelationGroup group)
	{
		if (!relations.containsKey(group.getRelationType()))
		{
			relations.put(group.getRelationType(), new HashMap<>());
		}
		
		Map<String, RelationGroup> groups = relations.get(group.getRelationType());
		groups.put(group.getRelationName(), group);
	}
	
	public boolean addRelation(MsgRelation.RELATION_TYPE type, String groupName, long otherId)
	{
		UserInfo otherUser = UserMgr.getInstance().findUserInfoById(otherId);
		if (null == otherUser)
		{
			return false;
		}
		
		if (owner.getId() == otherId)
		{
			owner.sendErrorMsg(ERROR_TYPE.SelfIsNotTarget);
			return false;
		}
		
		if (relationsByUserId.containsKey(otherId))
		{
			owner.sendErrorMsg(ERROR_TYPE.TargetIsFriend);
			return false;
		}
		
		Map<String, RelationGroup> groups = relations.get(type);
		if (null == groups)
		{
			return false;
		}
		
		RelationGroup group = groups.get(groupName);
		if (null == group)
		{
			return false;
		}
		
		group.addUser(otherId);
		relationsByUserId.put(otherId, group);
		owner.sendPbMsg(MSG_ID.M_AddRelation_VALUE, MsAddRelation.newBuilder().setType(type).setGroupName(groupName).
				                                                               setId(otherId).setName(otherUser.getName()).build());
		
		return true;
	}
	
	public boolean delRelation(long otherId)
	{
		UserInfo otherUser = UserMgr.getInstance().findUserInfoById(otherId);
		if (null == otherUser)
		{
			return false;
		}
		
		if (owner.getId() == otherId)
		{
			owner.sendErrorMsg(ERROR_TYPE.SelfIsNotTarget);
			return false;
		}
		
		if (!relationsByUserId.containsKey(otherId))
		{
			owner.sendErrorMsg(ERROR_TYPE.TargetNoFriend);
			return false;
		}
		
		RelationGroup group = relationsByUserId.get(otherId);
		if (null == group)
		{
			return false;
		}
		
		group.removeUser(otherId);
		relationsByUserId.remove(otherId);
		
		owner.sendPbMsg(MSG_ID.M_DelRelation_VALUE, MsDeleteRelation.newBuilder().setGroupName(group.getRelationName()).
				                                                                  setId(otherId).setName(otherUser.getName()).build());
		
		return true;
	}
	
	public boolean addRelationGroup(String groupName)
	{
		Map<String, RelationGroup> groups = relations.get(RELATION_TYPE.Friend);
		if (groups.containsKey(groupName))
		{
			return false;
		}
		
		RelationGroup relationGroup = RelationGroup.createRelation(RELATION_TYPE.Friend, groupName);
		if (null == relationGroup)
		{
			return false;
		}
		
		addGroup(relationGroup);
		
		owner.sendPbMsg(MSG_ID.M_AddRelationGroup_VALUE, MsAddRelationGroup.newBuilder().setGroupName(groupName).build());
		return true;
	}

	public boolean moveRelation(long userId, MsgRelation.RELATION_TYPE toRelation, String grouName)
	{
		RelationGroup oldGroup = relationsByUserId.get(userId);
		if (null == oldGroup)
		{
			return false;
		}
		
		Map<String, RelationGroup> map = relations.get(toRelation);
		if (null == map)
		{
			return false;
		}
		
		RelationGroup newGroup =  map.get(grouName);
		if (null == newGroup)
		{
			return false;
		}
		
		oldGroup.removeUser(userId);
		newGroup.addUser(userId);
		relationsByUserId.put(userId, newGroup);
		
		owner.sendPbMsg(MSG_ID.M_MoveRelation_VALUE, MsMoveRelation.newBuilder().setId(userId).setOldGroupName(oldGroup.getRelationName()).
				                                                                 setNewGroupName(newGroup.getRelationName()).build());
		
		return true;
	}

	//通知好友我上线了
	public void notifyLogin()
	{
		for (Entry<RELATION_TYPE, Map<String, RelationGroup>> map : relations.entrySet())
		{
			if (map.getKey() != RELATION_TYPE.Friend)
			{
				continue;
			}

			for (RelationGroup group : map.getValue().values())
			{
				group.notifyLogin(this.owner);
			}
		}
	}

	//通知好友我下线了
	public void notifyLogoff()
	{
		for (Entry<RELATION_TYPE, Map<String, RelationGroup>> map : relations.entrySet())
		{
			if (map.getKey() != RELATION_TYPE.Friend)
			{
				continue;
			}

			for (RelationGroup group : map.getValue().values())
			{
				group.notifyLogoff(this.owner);
			}
		}
	}
	
	public PbRelationInit toPb()
	{
		PbRelationInit.Builder builder = PbRelationInit.newBuilder();
		for (Map<String, RelationGroup> map : relations.values())
		{
			for (RelationGroup group : map.values())
			{
				builder.addGroups(group.toPb());
			}
		}
		
		return builder.build();
	}
	
	public boolean isFriend(long userId)
	{
		RelationGroup group = relationsByUserId.get(userId);
		if (null == group)
		{
			return false;
		}
		
		if (group.getRelationType() != RELATION_TYPE.Friend)
		{
			return false;
		}
		
		return true;
	}
	
	public Map<RELATION_TYPE, Map<String, RelationGroup>> getRelations() 
	{
		return relations;
	}
	
	public Map<RELATION_TYPE, Map<String, RelationGroup>> copyRelations()
	{
		Map<RELATION_TYPE, Map<String, RelationGroup>> copys = new HashMap<>();
		for (Entry<RELATION_TYPE, Map<String, RelationGroup>> map : relations.entrySet())
		{
			Map<String, RelationGroup> groups = new HashMap<>();
			copys.put(map.getKey(), groups);
			for (RelationGroup group : map.getValue().values())
			{
				groups.put(group.getRelationName(), group);
			}
		}
		
		return copys;
	}

	public Map<Long, RelationGroup> getRelationsByUserId() 
	{
		return relationsByUserId;
	}
	
	public Map<Long, RelationGroup> copyRelationsByUserId()
	{
		Map<Long, RelationGroup> copys = new HashMap<>();
		for (Entry<Long, RelationGroup> entry : relationsByUserId.entrySet())
		{
			copys.put(entry.getKey(), entry.getValue());
		}
		
		return copys;
	}
}
