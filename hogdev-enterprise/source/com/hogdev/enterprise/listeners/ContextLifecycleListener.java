package com.hogdev.enterprise.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class ContextLifecycleListener implements ServletContextListener {
	static Logger logger = Logger.getLogger(ContextLifecycleListener.class);

	public void contextDestroyed(ServletContextEvent arg0) {
		logger.debug("Context destroyed");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		logger.debug("Context initialized");
		Properties props = new Properties();
		try {
			InputStream is = getClass().getResourceAsStream("/hogdev.properties");
			if(is != null)
				props.load(is);
			props.putAll(System.getProperties());
		} catch (IOException e) {
			logger.info("Could not load properties file");
		}
		System.setProperties(props);
	}
}
