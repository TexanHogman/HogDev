/*
 * Created on Nov 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.util.NonHiddenFileFilter;
import com.hogdev.util.WildCardFileFilter;

/**
 * @author RHogge
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResourceUtil
{
	static Logger logger = Logger.getLogger(ResourceUtil.class);
	public final static String FILE_SEP = "/";

	public static int determineSuggestedView(ArrayList al, int iHintView)
	{
		int iRet = iHintView != Constants.UNDEFINED ? iHintView : Constants.LIST;
		boolean bAllImages = true;
		boolean bImageFound = false;
		boolean bAudioFound = false;
		for (int i = 0; i < al.size(); i++)
		{
			Resource res = (Resource)al.get(i);
			switch (res.getType())
			{
				case Constants.DIR:
				case Constants.VIDEO:
					break;
				case Constants.IMAGE:
					bImageFound = true;
					break;
				case Constants.AUDIO:
					bAudioFound = true;
					break;
				default :
					bAllImages = false;
					break;
			}
		}

		if (bAllImages && bImageFound)
			iRet = Constants.THUMBNAILS;
// use hint			
//		else if(bAudioFound)
//			iRet = Constants.DETAILS;

		logger.debug("suggested view is " + iRet);
		return iRet;
	}

	public static int determineType(File file)
	{
		int type = Constants.UNKNOWN;
		if (file.isDirectory())
			type = Constants.DIR;
		else
		{
			// get extension
			int index = file.getName().lastIndexOf('.');
			if (index != -1 && index != file.getName().length() - 1)
			{
				String ext = file.getName().substring(index + 1);
				int j =
					Arrays.binarySearch(Constants.IMAGE_EXT, ext.toUpperCase());
				if (j >= 0)
					type = Constants.IMAGE;
				else
				{
					j =
						Arrays.binarySearch(
							Constants.VIDEO_EXT,
							ext.toUpperCase());
					if (j >= 0)
						type = Constants.VIDEO;
					else
					{
						j =
							Arrays.binarySearch(
								Constants.AUDIO_EXT,
								ext.toUpperCase());
						if (j >= 0)
							type = Constants.AUDIO;
					}
				}
			}
		}
		return type;
	}
	
	public static ArrayList buildResourceTree(String baseDir, String strPath)
	{
		ArrayList list = new ArrayList();
		Resource res = buildResource(baseDir, strPath);
		if(res == null)
		{
		}
		else if(res.getType() == Constants.DIR)
		{
			String path = baseDir + res.getPath();
			File file = new File(path);
			// will be a directory
			File[] files = file.listFiles(new NonHiddenFileFilter());
			Arrays.sort(files);
			for(int i = 0; i < files.length; i++)
			{
				list.addAll(buildResourceTree(baseDir, strPath + FILE_SEP + files[i].getName()));
			}
		}
		else
		{
			list.add(res);
		}
		
		
		return list;
	}
	
	public static Resource buildResource(String baseDir, String strPath)
	{
		Resource resource = null;
	
		String strFullPath = baseDir;
		if(strPath != null && strPath.length() > 0)
			strFullPath += FILE_SEP + strPath;
		
		File file = new File(strFullPath);
		if (!file.exists())
		{
			logger.error(strPath + " does not exist");
		}
		else
		{
			// small bug fix where canonical might not match what was passed in as base dir
			File file_base_dir = new File(baseDir);
			
			String strCanPath;
			String strCanPathBase;
			try
			{
				strCanPath = file.getCanonicalPath();
				strCanPathBase = file_base_dir.getCanonicalPath();
			}
			catch (IOException e1)
			{
				logger.error(e1);
				strCanPath = baseDir + ResourceUtil.FILE_SEP + strPath;
				strCanPathBase = baseDir;
			}
			
			// real path off of canonical path
			strPath = strCanPath.substring(strCanPath.indexOf(strCanPathBase) + strCanPathBase.length());
			
			resource = new Resource();
			if(!strCanPath.equals(baseDir))
			{
				resource.setName(file.getName());
				resource.setPath(strPath);
			}
			else
			{
				resource.setPath(ResourceUtil.FILE_SEP);
			}
			resource.setDesc(resource.getPath());
			resource.setType(ResourceUtil.determineType(file));
			resource.setLastUpdate(new Date(file.lastModified()));
			resource.setSize(file.length());
			switch(resource.getType())
			{
				case Constants.DIR:
					File[] tfile = file.listFiles((FileFilter)new WildCardFileFilter("Folder.jpg"));
					for(int i = 0; i < tfile.length; i++)
					{
						if(tfile[i].isFile())
							resource.setThumbPath(strPath + FILE_SEP + tfile[0].getName());
					}
					tfile = file.listFiles((FileFilter)new WildCardFileFilter("Caption.txt"));
					for(int i = 0; i < tfile.length; i++)
					{
						if(tfile[i].isFile())
						{
							try
							{
								StringBuffer strBuffer = new StringBuffer();
								char[] buffer = new char[1024];
								FileReader reader = new FileReader(tfile[i]);
								int x = reader.read(buffer);
								while (x != -1)
								{
									strBuffer.append(buffer, 0, x);
									x = reader.read(buffer);
								}
								if(strBuffer.length() > 0)
								{
									resource.setCaption(strBuffer.toString());
								}
							}
							catch (IOException e)
							{
								logger.error(e);
							}
						}
					}
					break;
				case Constants.IMAGE:
					resource.setThumbPath(resource.getPath());
				default:
					break;
			}
		}
		return resource;
	}
}
