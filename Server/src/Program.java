import com.chatsystem.Server;

import java.util.Scanner;

/**
 * 服务器程序入口
 */
public class Program
{
	private static Server serverInstance = null;
	private static boolean isStop = false;

	public static void main(String[] args)
	{
		Thread consoleThread = new Thread(() ->
		{
			Scanner scanner = new Scanner(System.in);

			while(true)
			{
				String cmd = scanner.nextLine();
				if (null != cmd && cmd.equals("exit"))    //关闭服务器程序
				{
					break;
				}
				else
				{
					serverInstance.enqueueCommand(cmd);
				}
			}

			scanner.close();
			isStop = true;
		});

		consoleThread.start();

		Thread mainThread = new Thread(() ->
		{
			serverInstance = Server.getInstance();
			serverInstance.init();

			while (!isStop)
			{
				try
				{
					serverInstance.update();
					Thread.sleep(5);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			serverInstance.destroy();
		});

		mainThread.start();

		try
		{
			mainThread.join();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		System.exit(0);
	}
}
