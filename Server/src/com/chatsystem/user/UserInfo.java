package com.chatsystem.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.chatsystem.tools.MathUtils;
import com.chatsystem.tools.RandomUitls;
import org.bson.Document;

import com.chatsystem.chat.relation.RelationGroup;
import com.chatsystem.chat.relation.UserRelationCtrl;
import com.chatsystem.msg.MsgRelation;
import com.chatsystem.msg.MsgUser;
import com.chatsystem.msg.MsgRelation.RELATION_TYPE;

/**
 * 用户信息
 */
public class UserInfo
{
	private long id;
	private String accountId;        //玩家ID
	private String password;         //密码
	private String name;             //昵称
	private Map<RELATION_TYPE, Map<String, RelationGroup>> relations = new HashMap<>();
	private Map<Long, RelationGroup> relationsByUserId = new HashMap<>();
	private String authKey = null;

	public UserInfo (long id, String account, String pwd, String name)
	{
		this.id = id;
		this.accountId = account;
		this.password = pwd;
		this.name = name;
		
		RelationGroup friend = RelationGroup.createRelation(MsgRelation.RELATION_TYPE.Friend, UserRelationCtrl.DEFAULT_FRIEND_GROUP);
		if (null != friend)
		{
			addGroup(friend);
		}

		RelationGroup blackList = RelationGroup.createRelation(MsgRelation.RELATION_TYPE.BlackList, UserRelationCtrl.DEFAULT_BLACK_GROUP);
		if (null != blackList)
		{
			addGroup(blackList);
		}
	}

	public UserInfo(Document doc)
	{
		fromBson(doc);
	}

	public boolean fromBson(Document doc)
	{
		this.id = doc.getLong("id");
		this.accountId = doc.getString("account");
		this.password = doc.getString("password");
		this.name = doc.getString("name");
		
		initRelation(doc);
		return true;
	}

	public Document toBson()
	{
		return new Document("id", id).
				   append("account", accountId).
				   append("password", password).
				   append("name", name);
	}

	public MsgUser.PbUser toPb()
	{
		return MsgUser.PbUser.newBuilder().setAccount(this.accountId).
				                           setId(this.id).
				                           setName(this.name).build();
	}
	
	private void initRelation(Document doc)
	{
		if (doc.containsKey("RelationCtrl"))
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
	
	public void syncFromUser(User user)
	{
		this.id = user.getId();
		this.accountId = user.getAccountId();
		this.password = user.getPassword();
		this.name = user.getName();
		this.relations = user.getUserRelationCtrl().copyRelations();
		this.relationsByUserId = user.getUserRelationCtrl().copyRelationsByUserId();
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

	public String createAuthKey()
	{
		String randomKey = String.valueOf(RandomUitls.Random(1, 1000));
		authKey = MathUtils.md5(String.format("%s%s%s", accountId, password, randomKey));
		return randomKey;
	}

	public boolean auth(String key)
	{
		if (null == authKey)
		{
			return false;
		}

		boolean result = authKey.equals(key);
		authKey = null;
		return result;
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

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
