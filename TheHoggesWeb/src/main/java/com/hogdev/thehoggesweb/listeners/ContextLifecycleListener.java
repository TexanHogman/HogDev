package com.hogdev.thehoggesweb.listeners;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.hogdev.enterprise.Constants;
import com.hogdev.util.search.MP3Indexer;

public class ContextLifecycleListener implements ServletContextListener
{
    static Logger logger = Logger.getLogger(ContextLifecycleListener.class);

    public void contextDestroyed(ServletContextEvent arg0)
    {
	logger.debug("Context destroyed");
    }

    public void contextInitialized(final ServletContextEvent contextEvent)
    {
	logger.debug("Context initialized");
	Thread th = new Thread(new Runnable()
	{
	    public void run()
	    {
		String strBaseDir = (String) System.getProperty(Constants.BASE_DIR) + File.separator + "music";
		if (strBaseDir == null)
		{
		    logger.error("music dir is not available");
		    return;
		}

		File file = new File(strBaseDir);
		if (!file.exists() || !file.isDirectory())
		{
		    logger.error("music dir is invalid");
		    return;

		}
		else
		{
		    try
		    {
			strBaseDir = file.getCanonicalPath();
		    }
		    catch (IOException e)
		    {
			logger.error(e);
			// carry on
		    }
		}

		String strIndexDir = (String) System.getProperty(Constants.INDEX_DIR);
		if (strIndexDir == null)
		{
		    logger.error("index dir is not available");
		    return;
		}

		file = new File(strIndexDir);
		if (!file.exists() || !file.isDirectory())
		{
		    logger.error("index dir is invalid");
		    return;

		}
		else
		{
		    try
		    {
			strIndexDir = file.getCanonicalPath();
		    }
		    catch (IOException e)
		    {
			logger.error(e);
			// carry on
		    }
		}
		try
		{

		    // go get all folders in system and group by modified time
		    ServletContext context = contextEvent.getServletContext();

		    long sleep = 24; 
		    String strSleep = System.getProperty("routine.maintenance.hours");
		    if(strSleep != null && strSleep.length() > 0)
		    {
			try
			{
			    sleep = Long.valueOf(strSleep);
			}
			catch (NumberFormatException ignore)
			{
			    // use default
			}
		    }
		    while (true)
		    {
			// this is for the home page to display the new content
			context.setAttribute("whatsNew", getWhatsNew());

			Thread.sleep(1000 * 60 * 60 * sleep);
			
			
			// update the index for searching
			MP3Indexer indexer = new MP3Indexer();
			indexer.setDataDir(strBaseDir);
			indexer.setIndexDir(strIndexDir);
			indexer.index();
		    }

		}
		catch (InterruptedException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	    private List getWhatsNew()
	    {
		String whatsNewDays = (String) System.getProperty(Constants.WHATS_NEW_DAYS);
		final long fromWhen = System.currentTimeMillis()
			- (Integer.valueOf(whatsNewDays).intValue() * 24 * 60 * 60 * 1000);
		String strBaseDir = (String) System.getProperty(Constants.BASE_DIR);
		File flBaseDir = new File(strBaseDir);
		File[] files = flBaseDir.listFiles();
		List list = listDirWhereFileGreaterThanCheckpoint(flBaseDir, fromWhen);
		
		Collections.sort(list, new Comparator()
		{
		    public int compare(Object o1, Object o2)
		    {
			Long l1 = new Long(((File) o1).lastModified());
			Long l2 = new Long(((File) o2).lastModified());

			return (l2.compareTo(l1));
		    }

		});
		return list;

	    }
	    
	    private List listDirWhereFileGreaterThanCheckpoint(File file, long fromWhen)
	    {
		boolean added = false;
		List list = new ArrayList();
		File[] files = file.listFiles();
		for(int i = 0; i < files.length; i++)
		{
		    if(!added && !files[i].isHidden() && files[i].isFile() && files[i].lastModified() >= fromWhen)
		    {
			added = true;
			list.add(file);
		    }
		    else if(files[i].isDirectory())
		    {
			list.addAll(listDirWhereFileGreaterThanCheckpoint(files[i], fromWhen));
		    }
		}
		return list;
	    }
	});
	th.setDaemon(true);
	th.start();
    }

    class RecentDirFileFilter implements FileFilter
    {
	protected long fromWhen;

	RecentDirFileFilter(long fromWhen)
	{
	    this.fromWhen = fromWhen;
	}

	public boolean accept(File pathname)
	{
	    if(pathname.isDirectory() && pathname.lastModified() >= fromWhen)
	    {
		System.out.println("Yes on " + pathname);
		File[] files = pathname.listFiles();
		for(int i = 0; i < files.length; i++)
		{
		    if(files[i].lastModified() >= fromWhen)
		    {
			System.out.println("\tYes on " + files[i]);
			if(files[i].isFile())
				return true;
			else
			    System.out.println(files[i].isDirectory());
			    
		    }
		}
		
	    }
	    return false;
	}
    }
    
    
}
