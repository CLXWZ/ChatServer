package com.chatsystem.msgprocess;

import com.chatsystem.chat.relation.RelationMgr;
import com.chatsystem.msg.MsgRelation.McAddRelation;
import com.chatsystem.msg.MsgRelation.McAddRelationGroup;
import com.chatsystem.msg.MsgRelation.McDeleteRelation;
import com.chatsystem.msg.MsgRelation.McMoveRelation;
import com.chatsystem.msg.MsgRelation.RELATION_TYPE;
import com.chatsystem.session.PlayerSession;
import com.chatsystem.user.User;

public class MsgRelationAction 
{
	//加关系
	public static void handleAddRelation(PlayerSession playerSession, Object msg)
	{
		McAddRelation mcAddRelation = (McAddRelation)msg;
		if (null == mcAddRelation)
		{
			return;
		}
		
		RelationMgr.getInstance().addRelation(playerSession.getMyUser(), mcAddRelation.getType(), mcAddRelation.getGroupName(), mcAddRelation.getId());
	}
	
	//删除关系
	public static void handleDelRelation(PlayerSession playerSession, Object msg)
	{
		McDeleteRelation mcDeleteRelation = (McDeleteRelation)msg;
		if (null == mcDeleteRelation)
		{
			return;
		}
		
		User user = playerSession.getMyUser();
		user.getUserRelationCtrl().delRelation(mcDeleteRelation.getId());
	}
	
	//加分组
	public static void handleAddGroup(PlayerSession playerSession, Object msg)
	{
		McAddRelationGroup mcAddRelationGroup = (McAddRelationGroup)msg;
		if (null == mcAddRelationGroup)
		{
			return;
		}
		
		User user = playerSession.getMyUser();
		user.getUserRelationCtrl().addRelationGroup(mcAddRelationGroup.getGroupName());
	}
	
	public static void handleMoveRelation(PlayerSession playerSession, Object msg)
	{
		McMoveRelation mcMoveRelation = (McMoveRelation)msg;
		if (null == mcMoveRelation)
		{
			return;
		}
		
		User user = playerSession.getMyUser();
		user.getUserRelationCtrl().moveRelation(mcMoveRelation.getId(), mcMoveRelation.getNewType(), mcMoveRelation.getGroupName());
	}
}
