package com.chatsystem.chat.relation;

import com.chatsystem.msg.MsgIds;
import com.chatsystem.msg.MsgRelation;
import com.chatsystem.msg.MsgRelation.PbRelationGroup;
import com.chatsystem.msg.MsgUser;
import com.chatsystem.user.User;
import com.chatsystem.user.UserInfo;
import com.chatsystem.user.UserMgr;

import java.util.ArrayList;
import org.bson.Document;

/**
 * 用户关系
 */
public class RelationGroup
{
	private MsgRelation.RELATION_TYPE relationType;
	private String relationName;
	private final ArrayList<Long> relationUsers = new ArrayList<>();

	private RelationGroup()
	{

	}

	public static RelationGroup createRelation(MsgRelation.RELATION_TYPE relationType, String relationName)
	{
		RelationGroup relation = new RelationGroup();
		if (!relation.init(relationType, relationName))
		{
			return null;
		}

		return relation;
	}
	
	public static RelationGroup createRelation(Document doc)
	{
		RelationGroup relation = new RelationGroup();
		if (!relation.init(doc))
		{
			return null;
		}

		return relation;
	}

	public boolean init(MsgRelation.RELATION_TYPE relationType, String relationName)
	{
		this.relationType = relationType;
		this.relationName = relationName;
		return true;
	}

	public boolean init(Document doc)
	{
		if (!fromBson(doc))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean fromBson(Document doc)
	{
		relationType = MsgRelation.RELATION_TYPE.valueOf(doc.getInteger("GroupType"));
		relationName = doc.getString("GroupName");
		ArrayList<Long> arr = (ArrayList<Long>)doc.get("Ids");
		for (Long id : arr)
		{
			relationUsers.add(id);
		}
		return true;
	}
	
	public Document toBson()
	{
		Document doc = new Document();
		doc.append("GroupType", relationType.getNumber());
		doc.append("GroupName", relationName);
		ArrayList<Long> list = new ArrayList<>();
		for (long id : relationUsers)
		{
			list.add(id);
		}
		doc.append("Ids", list);
		
		return doc;
	}
	
	public PbRelationGroup toPb()
	{
		PbRelationGroup.Builder builder = PbRelationGroup.newBuilder();
		builder.setType(this.relationType);
		builder.setGroupName(this.relationName);
		for (long id : relationUsers)
		{
			UserInfo userInfo = UserMgr.getInstance().findUserInfoById(id);
			if (null != userInfo)
			{
				builder.addUsers(userInfo.toPb());
			}
		}
		
		return builder.build();
	}
	
	public boolean addUser(long userId)
	{
		if (relationUsers.contains(userId))
		{
			return false;
		}

		relationUsers.add(userId);
		return true;
	}

	public boolean removeUser(long userId)
	{
		if (!relationUsers.contains(userId))
		{
			return false;
		}

		relationUsers.remove(userId);
		return true;
	}

	public void notifyLogin(User loginUser)
	{
		for (long id : relationUsers)
		{
			User user = UserMgr.getInstance().findOnlineUser(id);
			if (null == user)
			{
				continue;
			}

			user.sendPbMsg(MsgIds.MSG_ID.M_NotifyLogin_VALUE, MsgUser.MsNotifyLogin.newBuilder().setUser(loginUser.toPb()).build());
		}
	}

	public void notifyLogoff(User logoffUser)
	{
		for (long id : relationUsers)
		{
			User user = UserMgr.getInstance().findOnlineUser(id);
			if (null == user)
			{
				continue;
			}

			user.sendPbMsg(MsgIds.MSG_ID.M_NotifyLogoff_VALUE, MsgUser.MsNotifyLogoff.newBuilder().setUser(logoffUser.toPb()).build());
		}
	}

	public MsgRelation.RELATION_TYPE getRelationType()
	{
		return relationType;
	}

	public void setRelationType(MsgRelation.RELATION_TYPE relationType)
	{
		this.relationType = relationType;
	}

	public String getRelationName()
	{
		return relationName;
	}

	public void setRelationName(String relationName)
	{
		this.relationName = relationName;
	}
	
	public ArrayList<Long> getRelationUsers() 
	{
		return relationUsers;
	}
}
