package com.chatsystem.db;


import com.chatsystem.tools.MathUtils;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

/**
 * Created by User on 2017/11/17.
 */
public class MongoTest
{
	public static void main(String[] args)
	{
		//System.out.println(MathUtils.md5("tom123233"));

		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase db = mongoClient.getDatabase("chatsys");
		db.drop();
		mongoClient.close();
//		MongoCollection<Document> collection = db.getCollection("test");
//		Document doc = new Document();
//		doc.append("name", "mongo");
//		ArrayList<Integer> list = new ArrayList<>();
//		for (int  i = 1; i <= 10; ++i)
//		{
//			list.add(i);
//		}
//		doc.append("arr", list);
//		collection.insertOne(doc);
//		
//		Document fdDocument = null;
//		FindIterable<Document> iterables = collection.find();
//		MongoCursor<Document> cursor = iterables.iterator();
//		while (cursor.hasNext())
//		{
//			fdDocument = cursor.next();
//		}
//		
//		ArrayList<Integer> list2 = (ArrayList<Integer>)fdDocument.get("arr");
//		System.out.println(list2);
//		
//		ArrayList<Integer> list = (ArrayList<Integer>)fdDocument.get("arr");
//		list.add(7);
//		
//		fdDocument.append("arr", list);
//		collection.replaceOne(new Document().append("name", "mongo"), fdDocument);
//		
//		Document f2dDocument = null;
//		iterables = collection.find();
//		cursor = iterables.iterator();
//		while (cursor.hasNext())
//		{
//			f2dDocument = cursor.next();
//		}
//		
//		ArrayList<Integer> list2 = (ArrayList<Integer>)f2dDocument.get("arr");
//		System.out.println(list2);
		
//		Document doc = new Document();
//		Document relationsDoc = new Document();
//		BsonDocument relationDoc = new BsonDocument();
//		relationDoc.append("relationName", new BsonString("好友23333"));
//		relationDoc.append("relationType", new BsonInt32(1));
//		BsonArray array = new BsonArray();
//		for (int  i = 1; i <= 6; ++i)
//		{
//			array.add(new BsonInt32(i));
//		}
//		relationDoc.append("ids", array);
//		BsonArray relationArr = new BsonArray();
//		relationArr.add(relationDoc);
//		relationsDoc.append("friend", relationArr);
//		doc.append("name", "momo");
//		doc.append("relationCtrl", relationsDoc);
//		collection.insertOne(doc);
		
//		collection.replaceOne(new Document("name", "momo"),  doc);
		
		
//		FindIterable<Document> iterables = collection.find();
//		MongoCursor<Document> cursor = iterables.iterator();
//		while (cursor.hasNext())
//		{
//			Document doc = cursor.next();
//			Document relationsDoc = (Document)doc.get("relationCtrl");
//			ArrayList<Document> relationArr = (ArrayList<Document>)relationsDoc.get("friend");
//			for (Document bson : relationArr)
//			{
//				System.out.println(bson.get("relationName"));
//				System.out.println(bson.get("relationType"));
//				ArrayList<Integer> arr = (ArrayList<Integer>)bson.get("ids");
//				for (Integer id : arr)
//				{
//					System.out.println(id);
//				}
//			}
//		}
		
//		FindIterable<Document> iterables = collection.find(new Document("name", "mongo"));
//		MongoCursor<Document> cursor = iterables.iterator();
//		while (cursor.hasNext())
//		{
//			Document doc = cursor.next();
//			System.out.println(doc.get("name"));
//			System.out.println(doc.get("type"));
//		}
//
//		iterables = collection.find(new Document("name", "redis"));
//		cursor = iterables.iterator();
//		while (cursor.hasNext())
//		{
//			Document doc = cursor.next();
//			System.out.println(doc.get("name"));
//			System.out.println(doc.get("type"));
//		}
	}
}
