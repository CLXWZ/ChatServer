package com.chatsystem.tools;

import java.util.Calendar;

/**
 * 时间工具类
 */
public class DateTimeUtil
{
	public static final long MillisSecondToSecond = 1000;  //秒对毫秒转换

	//获取当前时间
	public static long getNowTimeInMillis ()
	{
		DateTime dateTime = DateTime.now();
		return dateTime.getTimeInMillis();
	}

	//获取以当前时间为基准，一段时间后的时间
	public static long getNowAfterTimeInMillis (int field, int amount)
	{
		DateTime dateTime = DateTime.now();
		dateTime.add(field, amount);
		return dateTime.getTimeInMillis();
	}

	public static long getTimeAfterTimeInMillis (long time, int field, int amount)
	{
		DateTime dateTime = new DateTime(time);
		dateTime.add(field, amount);
		return dateTime.getTimeInMillis();
	}

	//两个时间点是否在同一天内
	public static boolean isInSameDay(long leftTick, long rightTick, int seconds)
	{
		DateTime leftDateTime = new DateTime(leftTick);
		leftDateTime.add(DateTime.SECOND_FIELD, -seconds);
		DateTime rightDateTime = new DateTime(rightTick);
		rightDateTime.add(DateTime.SECOND_FIELD, -seconds);

		return leftDateTime.toCalendar().get(Calendar.DAY_OF_YEAR) == rightDateTime.toCalendar().get(Calendar.DAY_OF_YEAR);
	}

	//两个时间点是否在同一周内
	public static boolean isInSameWeek(long leftTick, long rightTick, int seconds, int weekDay)
	{
		DateTime leftDateTime = new DateTime(leftTick);
		leftDateTime.add(DateTime.SECOND_FIELD, -seconds);
		DateTime rightDateTime = new DateTime(rightTick);
		rightDateTime.add(DateTime.SECOND_FIELD, -seconds);

		int dayDiff = dateDiff(leftDateTime, rightDateTime);

		if (Math.abs(dayDiff) >= 7)  //超出一周
		{
			return false;
		}

		if (dayDiff == 0)  //在同一天内
		{
			return true;
		}

		int leftweekDay = leftDateTime.toCalendar().get(Calendar.DAY_OF_WEEK) - weekDay;
		int rightWeekDay = rightDateTime.toCalendar().get(Calendar.DAY_OF_WEEK) - weekDay;

		if (leftweekDay < 0)
		{
			leftweekDay += 7;
		}

		if (rightWeekDay < 0)
		{
			rightWeekDay += 7;
		}

		return dayDiff < 0 ? leftweekDay <= rightWeekDay : leftweekDay >= rightWeekDay;
	}

	//两个时间点相差几天
	public static int dateDiff(DateTime leftDateTime, DateTime rightDateTime)
	{
		return leftDateTime.toCalendar().get(Calendar.DAY_OF_YEAR) - rightDateTime.toCalendar().get(Calendar.DAY_OF_YEAR);
	}
}
