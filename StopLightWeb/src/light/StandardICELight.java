package light;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class StandardICELight
{
	String userdomain = System.getProperty("USERDOMAIN", "CORP");
	String username = System.getProperty("USERNAME", "rhogge");
	String userdomainname = userdomain + "\\" + username;
	String password = System.getProperty("PASSWORD", "");
	final static String QUEUE_START = "<h2>Queued Builds</h2>";
	final static String QUEUE_END = "<h2>Queued Deploys</h2>";
	final static String QUEUED_ITEM = "id=\"RequestTextTableRow\"";
	final static String STATUS_LINE = "<tr><td><b>Status: </b></td><td>";
	final static String STATUS_LINE2 = "\t\t\t<td nowrap>";
	final static String ICE = "http://ice.paypal.com/ICEPanel/home/projectdetails.jsp?projectID=";
	final static String ICE_QUEUE = "http://ice.paypal.com/ICEPanel/ICEController?action=report/monitor_build.jsp";
	final static String OFF = "http://localhost:8080/stoplight.jsp?command=Off";
	final static String FLASH = "http://localhost:8080/stoplight.jsp?command=Flash";
	final static String RED = "http://localhost:8080/stoplight.jsp?command=Red+on";
	final static String RED_FLASH = "http://localhost:8080/stoplight.jsp?command=Red+flash";
	final static String YELLOW = "http://localhost:8080/stoplight.jsp?command=Yellow+on";
	final static String YELLOW_FLASH = "http://localhost:8080/stoplight.jsp?command=Yellow+flash";
	final static String GREEN = "http://localhost:8080/stoplight.jsp?command=Green+on";
	final static String GREEN_FLASH = "http://localhost:8080/stoplight.jsp?command=Green+flash";
	final static String QUEUED = "Queued";
	final static String COMPLETED = "Completed";
	final static String SUCCESS = "Success";
	final static String DORMANT = "Dormant";
	final static String FAILED = "Failed";
	final static String CANCELED = "Canceled";
	final static String AUTOCANCELED = "AutoCanceled";
	final static String UNKNOWN = "Unknown";
	final static String OBSOLETE = "Obsolete";
	final static String RUNNING = "Running";
	final static int SECS_SLEEP = 60;
	final static int QUEUE_DAMNGOOD = 5;
	final static int QUEUE_GOOD = 10;
	final static int QUEUE_FAIR = 20;
	final static int QUEUE_BAD = 30;
	final static int QUEUE_SUCKS = 40;

	private static Logger logger = Logger.getLogger(StandardICELight.class);
	protected long lastCommand = 0;
	protected final static Object lock = new Object();

	public void off()
	{
		lastCommand = System.currentTimeMillis();
		logger.debug("off()");
		try
		{
			sendGet(OFF);
		}
		catch (Exception e)
		{
			logger.error(e);
		}
	}

	public void iceQueue()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("iceQueue()");
			final long myRun = lastCommand;
			Thread th = new Thread(new Runnable()
			{
				public void run()
				{
					String lastQueuedStatus = "";
					String queuedStatus = "";

					while (true)
					{
						synchronized (lock)
						{
							if (myRun != lastCommand)
								break;

							logger.debug("checking latest Queue of ICE");
							try
							{
								// go get current status
								URL url = new URL(ICE_QUEUE);
								HttpURLConnection conn = (HttpURLConnection) url.openConnection();
								String userPassword = userdomainname + ":" + password;
								String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
								conn.setRequestProperty("Authorization", "Basic " + encoding);
								BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
								String line;
								boolean inQueue = false;
								int iQueued = 0;
								while ((line = rd.readLine()) != null)
								{
									if (!inQueue)
									{
										if (line.indexOf(QUEUE_START) >= 0)
											inQueue = true;
									}
									else
									{
										if (line.indexOf(QUEUE_END) >= 0)
											inQueue = false;
									}

									if (inQueue)
									{
										if (line.indexOf(QUEUED_ITEM) >= 0)
											iQueued++;
									}
								}

								if (iQueued <= QUEUE_DAMNGOOD)
									queuedStatus = GREEN_FLASH;
								else if (iQueued <= QUEUE_GOOD)
									queuedStatus = GREEN;
								else if (iQueued <= QUEUE_FAIR)
									queuedStatus = YELLOW;
								else if (iQueued <= QUEUE_BAD)
									queuedStatus = RED;
								else if (iQueued <= QUEUE_SUCKS)
									queuedStatus = RED_FLASH;
								else if (iQueued > QUEUE_SUCKS)
									queuedStatus = FLASH;

								logger.debug("number of items currently in queue is: " + iQueued);
								if (!queuedStatus.equals(lastQueuedStatus))
								{
									sendGet(OFF);
									sendGet(queuedStatus);
									lastQueuedStatus = queuedStatus;
								}

								rd.close();
							}
							catch (Exception e)
							{
								logger.error(e);
							}
							finally
							{
								try
								{
									// sleep
									Thread.sleep(SECS_SLEEP * 1000);
								}
								catch (InterruptedException ignore)
								{
								}
							}
						}
					}
				}
			});
			th.setDaemon(true);
			th.start();
		}
	}

	public void iceProject(final String id)
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("iceProject() id = " + id);

			if (id == null || id.trim().length() == 0)
				return;

			lastCommand = System.currentTimeMillis();
			final long myRun = lastCommand;
			Thread th = new Thread(new Runnable()
			{
				public void run()
				{
					String status = "";
					String laststatus = "";
					boolean done = false;

					while (!done)
					{
						synchronized (lock)
						{
							if (myRun != lastCommand)
								break;

							logger.debug("checking latest status for project: " + id);
							try
							{
								// go get current status
								URL url = new URL(ICE + id);
								HttpURLConnection conn = (HttpURLConnection) url.openConnection();
								String userPassword = userdomainname + ":" + password;
								String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
								conn.setRequestProperty("Authorization", "Basic " + encoding);
								BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
								String line;
								while ((line = rd.readLine()) != null)
								{
									int i = line.indexOf(STATUS_LINE);
									if (i >= 0)
									{
										int x = i + STATUS_LINE.length();
										status = line.substring(x, line.indexOf("<", x));

										// do additional check
										if (status.equals(COMPLETED) || status.equals(DORMANT))
										{
											// at this point we can not be sure
											// of the status
											String more;
											while ((more = rd.readLine()) != null)
											{
												int j = more.indexOf(STATUS_LINE2);
												if (j >= 0)
												{
													int y = j + STATUS_LINE2.length();
													status = more.substring(y, more.indexOf("<", y));
													break;
												}
											}
										}

										if (!status.equals(laststatus))
										{
											logger.debug("updating status for project: " + id + " to " + status);
											sendGet(OFF);
											if (status.equals(SUCCESS))
											{
												done = true;
												sendGet(GREEN);
											}
											else if (status.equals(QUEUED))
												sendGet(YELLOW);
											else if (status.equals(FAILED) || status.equals(CANCELED) || status.equals(AUTOCANCELED) || status.equals(UNKNOWN) || status.equals(OBSOLETE))
											{
												done = true;
												sendGet(RED);
											}
											else if (status.equals(RUNNING))
												sendGet(GREEN_FLASH);

											laststatus = status;
										}
										break;
									}
								}
								rd.close();
							}
							catch (Exception e)
							{
								logger.error(e);
							}
							finally
							{
								if (done)
								{
									logger.debug("final state for project: " + id);
								}
								else
								{
									try
									{
										// sleep
										Thread.sleep(SECS_SLEEP * 1000);
									}
									catch (InterruptedException ignore)
									{
									}
								}
							}
						}
					}
				}
			});
			th.setDaemon(true);
			th.start();
		}
	}

	private void sendGet(String lightURL) throws MalformedURLException, IOException
	{
		logger.debug("updating light");

		URL lurl = new URL(lightURL);
		HttpURLConnection lconn = (HttpURLConnection) lurl.openConnection();
		lconn.getInputStream();
	}

}
