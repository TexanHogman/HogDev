// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3)
// Source File Name:   GeneralUtils.java

package com.hogdev.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class GeneralUtils
{
	static Logger logger = Logger.getLogger(GeneralUtils.class);

	public GeneralUtils()
	{
	}

	public static String getKeyboardInput()
	{
		return getKeyboardInput(null);
	}

	public static String getKeyboardInput(String strPrompt)
	{
		String strReturn = null;

		if (strPrompt != null)
		{
			System.out.print(strPrompt + " ");
		}

		try
		{
			// max input size 256
			byte[] baInput = new byte[256];
			int iRead = System.in.read(baInput);
			// dont want cr/lf
			strReturn = new String(baInput, 0, iRead - 2);
		}
		catch (IOException ioe)
		{
			System.out.println("IOException caught while processing keyboard input" + ioe);
		}
		return strReturn;

	}

	public static String stringReplace(String source, char ch, String replace)
	{
		return stringReplace(source, (new Character(ch)).toString(), replace);
	}

	public static String stringReplace(String source, String search, String replace)
	{
		if (source != null && replace != null && search != null)
		{
			int index = 0;
			int len = replace.length();
			do
			{
				index = source.indexOf(search, index);
				if (index != -1)
				{
					source = source.substring(0, index) + replace + source.substring(index + search.length());
					index += len;
				}
			}
			while (index != -1);
		}
		return source;
	}

	public static String stringFirstReplace(String source, String str, String string)
	{
		if (source != null && string != null)
		{
			int index = source.indexOf(str);
			if (index != -1)
				source = source.substring(0, index) + string + source.substring(index + str.length());
		}
		return source;
	}

	public static String stringFirstReplace(String source, String str, int iValue)
	{
		if (source != null)
		{
			int index = source.indexOf(str);
			if (index != -1)
				source = source.substring(0, index) + iValue + source.substring(index + str.length());
		}
		return source;
	}

	public static String stringFirstReplace(String source, String str, long lValue)
	{
		if (source != null)
		{
			int index = source.indexOf(str);
			if (index != -1)
				source = source.substring(0, index) + lValue + source.substring(index + str.length());
		}
		return source;
	}

	public static String stringFirstReplace(String source, char ch, String string)
	{
		if (source != null && string != null)
		{
			int index = source.indexOf(ch);
			if (index != -1)
				source = source.substring(0, index) + string + source.substring(index + 1);
		}
		return source;
	}

	public static String stringFirstReplace(String source, char ch, int iValue)
	{
		if (source != null)
		{
			int index = source.indexOf(ch);
			if (index != -1)
				source = source.substring(0, index) + iValue + source.substring(index + 1);
		}
		return source;
	}

	public static String stringFirstReplace(String source, char ch, long lValue)
	{
		if (source != null)
		{
			int index = source.indexOf(ch);
			if (index != -1)
				source = source.substring(0, index) + lValue + source.substring(index + 1);
		}
		return source;
	}

	public static ArrayList parseString(String strToParse, String strDelimiter)
	{
		StringTokenizer tokenizer = new StringTokenizer(strToParse, strDelimiter);
		int nNumberTokens = tokenizer.countTokens();
		ArrayList al = new ArrayList(nNumberTokens);
		for (int i = 0; i < nNumberTokens; i++)
			al.add(tokenizer.nextToken());

		return al;
	}

	private static ArrayList collectAll(File flDir, FileFilter filter)
	{
		logger.debug("Collecting from: " + flDir.getPath() + " : " + filter);
		
		ArrayList alAll = new ArrayList();
		File fa_Children[] = flDir.listFiles(filter);

		// sort them first
		Arrays.sort(fa_Children, new Comparator()
		{

			@Override
			public int compare(Object o1, Object o2)
			{
				return ((File) o1).getName().compareTo(((File) o2).getName());
			}
		});

		for (int i = 0; i < fa_Children.length; i++)
			if (fa_Children[i].isFile())
				alAll.add(fa_Children[i]);
			else
				alAll.addAll(collectAll(fa_Children[i], filter));

		return alAll;
	}

	public static synchronized ArrayList cacheCollectAll(final File flDir, final FileFilter filter)
	{
		final String strKey = flDir.getPath() + File.separator + filter;
		ArrayList alAll = (ArrayList) hm_.get(strKey);
		if (alAll == null)
		{
			logger.debug("Populating Cache: " + flDir.getPath() + " : " + filter);
			hm_.put(strKey, new ArrayList());
			Thread th = new Thread(new Runnable()
			{

				@Override
				public synchronized void run()
				{
					try
					{
						while (true)
						{
							logger.debug("Start Cache Refresh: " + flDir.getPath() + " : " + filter);
							GeneralUtils.hm_.put(strKey, GeneralUtils.collectAll(flDir, filter));
							logger.debug("Complete Cache Refresh: " + flDir.getName() + " : " + filter);
							String refresh = System.getProperty("cache.refresh.hours");
							int ref = 0;
							try
							{
								ref = Integer.valueOf(refresh);
							}
							catch(NumberFormatException e)
							{
								logger.warn("value of cache.refresh.hours is not number: " + refresh );
								break;
							}
							if(ref <= 0 )
							{
								logger.info("value of cache.refresh.hours is not greater than 0 therefore no refresh");
								break;
								
							}
									
							Thread.sleep(1000 * 60 * 60 * ref);
						}
					}
					catch (InterruptedException e)
					{
						logger.debug("Cache thread interuppted: " + flDir.getName() + " : " + filter);
					}
				}

			});
			th.setDaemon(true);
			th.start();
		}
		return alAll;
	}

	static HashMap hm_ = new HashMap();

}
