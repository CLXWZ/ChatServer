package com.chatsystem.tools;

import java.security.MessageDigest;

/**
 * 数学相关工具类
 */
public class MathUtils
{
	public static String md5(String str)
	{
		MessageDigest md;
		StringBuilder sb = new StringBuilder();
		try
		{
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] data = md.digest();

			int index;
			for (byte b : data)
			{
				index = b;
				if (index < 0)
				{
					index += 256;
				}
				if (index < 16)
				{
					sb.append("0");
				}
				sb.append(Integer.toHexString(index));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return sb.toString();
	}
}
