package com.chatsystem.db;

import com.chatsystem.function.Action;
import com.chatsystem.network.thread.ServerThreadFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 数据库管理器
 */
public class DBMgr
{
	private static final Logger logger = LoggerFactory.getLogger(DBMgr.class);

	private static final DBMgr instance = new DBMgr();

	private static final int maxMsgPerTick = 10;
	private MongoClient mongoClient = null;
	private MongoDatabase mongoDB = null;
	private ExecutorService executorService = null;
	private final Queue<Action> dbCallbacks = new ConcurrentLinkedQueue<>();

	private DBMgr()
	{

	}

	public static DBMgr getInstance ()
	{
		return instance;
	}

	public boolean init(String ip, int port, String dbName)
	{
		try
		{
			mongoClient = new MongoClient(ip, port);
			mongoDB = mongoClient.getDatabase(dbName);
			if (null == mongoDB)
			{
				return false;
			}

			//线程池
			executorService = Executors.newCachedThreadPool(new ServerThreadFactory("DBThreadPool"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	public void update()
	{
		int num = 0;
		Action action;
		while (num < maxMsgPerTick && null != (action = dbCallbacks.poll()))
		{
			++num;
			action.invoke();
		}
	}

	public void destroy()
	{
		mongoClient.close();
		mongoClient = null;
		mongoDB = null;
		dbCallbacks.clear();
	}

	//同步查询
	public ArrayList<Document> find(String collectionName, Document filter)
	{
		ArrayList<Document> result = new ArrayList<>();

		MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
		FindIterable<Document> iterables = filter == null ? collection.find() : collection.find(filter);
		MongoCursor<Document> cursor = iterables.iterator();
		while (cursor.hasNext())
		{
			result.add(cursor.next());
		}

		return result;
	}

	//异步查询
	public boolean findAsync(String collectionName, Document filter, Consumer<ArrayList<Document>> callback)
	{
		executorService.submit(() ->
		{
			ArrayList<Document> result = find(collectionName, filter);
			if (null != callback)
			{
				dbCallbacks.offer(() -> { callback.accept(result); });
			}
		});

		return true;
	}

	//异步插入
	public boolean insertOne(String collectionName, Document doc, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				collection.insertOne(doc);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(true);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	//异步插入
	public boolean insertMany(String collectionName, ArrayList<Document> docs, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				collection.insertMany(docs);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(true);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	//异步更新
	public boolean updateOne(String collectionName, Document filter, Document doc, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				UpdateResult result = collection.updateOne(filter, doc);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(result.getModifiedCount() > 0);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	public boolean updateMany(String collectionName, Document filter, Document doc, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				UpdateResult result = collection.updateMany(filter, doc);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(result.getModifiedCount() > 0);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	//异步删除
	public boolean deleteOne(String collectionName, Document filter, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				DeleteResult result = collection.deleteOne(filter);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(result.getDeletedCount() > 0);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	//异步删除
	public boolean deleteMany(String collectionName, Document filter, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				DeleteResult result = collection.deleteMany(filter);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(result.getDeletedCount() > 0);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}

	//异步替换
	public boolean replaceOne(String collectionName, Document filter, Document doc, Consumer<Boolean> callback)
	{
		executorService.submit(() ->
		{
			MongoCollection<Document> collection = mongoDB.getCollection(collectionName);
			try
			{
				UpdateResult result = collection.replaceOne(filter, doc);
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(result.getModifiedCount() > 0);});
				}
			}
			catch(Exception ex)
			{
				if (null != callback)
				{
					dbCallbacks.offer(() -> { callback.accept(false);});
				}
			}
		});

		return true;
	}
}
