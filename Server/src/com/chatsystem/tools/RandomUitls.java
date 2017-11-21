package com.chatsystem.tools;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUitls
{
	public static final Random m_Random = new Random();

	public static int Random (int min, int max)
	{
		if (max < min)
		{
			int temp = min;
			min = max;
			max = temp;
		}

		int rand = m_Random.nextInt(max - min);

		return rand + min;
	}

	public static float Random (float min, float max)
	{
		if (max < min)
		{
			float temp = min;
			min = max;
			max = temp;
		}

		float rand = m_Random.nextFloat();

		return (rand) * (max - min) + min;
	}

	public static double Random (double min, double max)
	{
		if (max < min)
		{
			double temp = min;
			min = max;
			max = temp;
		}

		double rand = m_Random.nextDouble();

		return (rand) * (max - min) + min;
	}

	public static boolean Random ()
	{
		return m_Random.nextBoolean();
	}

	public static int RandomRatio ()
	{
		return Random(0, 10000);
	}
}

