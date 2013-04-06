import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import light.StandardICELight;

import org.apache.log4j.Logger;

import com.hogdev.traffic.StandardStopLight;

public class ContextLifecycleListener implements ServletContextListener
{
	private static Logger logger = Logger.getLogger(ContextLifecycleListener.class);
	
	public void contextDestroyed(ServletContextEvent event)
	{
		logger.info("Application shutting down");
		
		StandardStopLight light = (StandardStopLight)event.getServletContext().getAttribute("light");
		if(light != null)
		{
			light.shutdown();
		}
	}

	public void contextInitialized(ServletContextEvent event)
	{
		logger.info("Application starting up");
		
		String comport = System.getProperty("CM11A_COMPORT","COM1");
		try
		{
			StandardStopLight light = new StandardStopLight(comport, new String[] { "E1", "E2", "E3" });
			event.getServletContext().setAttribute("light", light);
			StandardICELight icelight = new StandardICELight();
			event.getServletContext().setAttribute("icelight", icelight);
			event.getServletContext().setAttribute("icelight.mode", "Ready");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
