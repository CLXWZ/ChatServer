package com.chatsystem.tools;

/**
 * 字符串工具类
 */
public class StringUtils 
{
	public static boolean isNumberic(String str)
	{
		char[] arr = str.toCharArray();
		for (char c : arr)
		{
			if (c < '0' || c > '9')
			{
				return false;
			}
		}
		
		return true;
	}
}
