package com.hogdev.traffic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import x10.CM11ASerialController;
import x10.Command;
import x10.OperationTimedOutException;
import x10.UnitEvent;
import x10.UnitListener;
import x10.WastedException;

import com.hogdev.util.GeneralUtils;

public class StandardStopLight implements UnitListener
{
	private static Logger logger = Logger.getLogger(StandardStopLight.class);

	protected String[] deviceIds;

	protected CM11ASerialController controller;

	protected long lastCommand = 0;

	protected final static Object lock = new Object();
	boolean red, yellow, green;

	private String commPort;

	static
	{
		Properties props = new Properties();
		try
		{
			InputStream is = StandardStopLight.class.getResourceAsStream("/traffic.properties");
			if (is != null)
				props.load(is);
			props.putAll(System.getProperties());
		}
		catch (IOException e)
		{
			logger.info("Could not load properties file");
		}
		System.setProperties(props);
	}

	public static void main(String[] args)
	{
		try
		{
			StandardStopLight light = new StandardStopLight();
			light.turnOff();
			while (true)
			{
				System.out.println("Select option to operate stoplight");
				System.out.println("");
				System.out.println("0 - all off");
				System.out.println("1 - all on");
				System.out.println("2 - all flash");
				System.out.println("r - red off");
				System.out.println("t - red on");
				System.out.println("y - red flash");
				System.out.println("f - yellow off");
				System.out.println("g - yellow on");
				System.out.println("h - yellow flash");
				System.out.println("v - green off");
				System.out.println("b - green on");
				System.out.println("n - green flash");
				System.out.println("a - cycle");
				System.out.println("z - random craziness");
				System.out.println("s - stage");
				System.out.println("q - quit");
				System.out.println("");
				String selection = GeneralUtils.getKeyboardInput("Make selection:");
				if (selection.equalsIgnoreCase("q"))
				{
					light.shutdown();
					// problem with API so I need this
					System.exit(0);

					break;
				}
				else if (selection.equalsIgnoreCase("0"))
					light.turnOff();
				else if (selection.equalsIgnoreCase("1"))
					light.turnOn();
				else if (selection.equalsIgnoreCase("2"))
					light.flashAll();
				else if (selection.equalsIgnoreCase("r"))
					light.turnRedOff();
				else if (selection.equalsIgnoreCase("t"))
					light.turnRedOn();
				else if (selection.equalsIgnoreCase("y"))
					light.flashRed();
				else if (selection.equalsIgnoreCase("f"))
					light.turnYellowOff();
				else if (selection.equalsIgnoreCase("g"))
					light.turnYellowOn();
				else if (selection.equalsIgnoreCase("h"))
					light.flashYellow();
				else if (selection.equalsIgnoreCase("v"))
					light.turnGreenOff();
				else if (selection.equalsIgnoreCase("b"))
					light.turnGreenOn();
				else if (selection.equalsIgnoreCase("n"))
					light.flashGreen();
				else if (selection.equalsIgnoreCase("a"))
					light.cycle();
				else if (selection.equalsIgnoreCase("z"))
					light.goCrazy();
				else if (selection.equalsIgnoreCase("s"))
					light.stage();
				else
					System.out.println("Unrecognized option: " + selection);
				System.out.println("");
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Thanks for playing!!");
	}

	public StandardStopLight() throws IOException
	{
		this(new String[] { System.getProperty("redlight.id"), System.getProperty("yellowlight.id"), System.getProperty("greenlight.id") });
		if (logger.isDebugEnabled())
			controller.addUnitListener(this);
	}

	public StandardStopLight(String[] ids) throws IOException
	{
		this(System.getProperty("interface.serial.port"), ids);
	}

	public StandardStopLight(String port, String[] ids) throws IOException
	{
		deviceIds = ids;
		commPort = port;
		init();
	}

	private void init() throws IOException
	{
		synchronized (lock)
		{
			controller = new CM11ASerialController(commPort);
		}
	}

	public void turnOff()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnOff()");
			// this is a bug I think -- ALL_LIGHTS_OFF is not working so
			// maybe the define is wrong
			addCommand(new Command(deviceIds[0], Command.ALL_UNITS_OFF));
		}
	}

	public void turnOn()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnOn()");
			addCommand(new Command(deviceIds[0], Command.ALL_LIGHTS_ON));
		}
	}

	public void shutdown()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("shutdown()");
			try
			{
				controller.shutdown(3000);
			}
			catch (OperationTimedOutException e)
			{
			}
			catch (InterruptedException ignore)
			{
			}
			finally
			{
				controller.shutdownNow();
				controller = null;
			}
		}
	}

	public void goCrazy()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("goCrazy()");
			// start by resetting
			turnOff();

			lastCommand = System.currentTimeMillis();
			final long myRun = lastCommand;
			Thread th = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						while (true)
						{
							synchronized (lock)
							{
								if (myRun != lastCommand)
									break;

								logger.debug("crazy-again()");
								long sleep = 0;
								for (int i = 0; i < deviceIds.length; i++)
								{
									long on = Math.round((Math.random() * 1));
									if (on == 0)
									{
										addCommand(new Command(deviceIds[i], Command.OFF));
									}
									else
									{
										addCommand(new Command(deviceIds[i], Command.ON));
									}

									sleep = Math.round((Math.random() * 1000));

									// sleep at least half a second.
									Thread.sleep(sleep + 500);
								}
							}
						}
					}
					catch (InterruptedException ignore)
					{
					}
				}
			});
			th.setDaemon(true);
			th.start();
		}
	}

	public void cycle()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("cycle()");

			// start by resetting
			turnOff();

			lastCommand = System.currentTimeMillis();
			final long myRun = lastCommand;
			Thread th = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						while (true)
						{
							synchronized (lock)
							{
								if (myRun != lastCommand)
									break;

								logger.debug("cycle-again()");
								int i = 0;
								for (; i < deviceIds.length; i++)
								{
									addCommand(new Command(deviceIds[i], Command.ON));
									Thread.sleep(1000);
									addCommand(new Command(deviceIds[i], Command.OFF));
								}
								--i;
								while (--i > 0)
								{
									addCommand(new Command(deviceIds[i], Command.ON));
									Thread.sleep(1000);
									addCommand(new Command(deviceIds[i], Command.OFF));
								}
							}
						}
					}
					catch (InterruptedException ignore)
					{
					}
				}
			});
			th.setDaemon(true);
			th.start();
		}
	}

	private void flash(final int index)
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("flash()");

			// start by resetting
			turnOff();

			lastCommand = System.currentTimeMillis();
			final long myRun = lastCommand;
			Thread th = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						while (true)
						{
							synchronized (lock)
							{
								if (myRun != lastCommand)
									break;

								logger.debug("flash-again()");
								if (index == -1)
									addCommand(new Command(deviceIds[0], Command.ALL_LIGHTS_ON));
								else
									addCommand(new Command(deviceIds[index], Command.ON));

								Thread.sleep(1000);

								if (index == -1)
									addCommand(new Command(deviceIds[0], Command.ALL_UNITS_OFF));
								else
									addCommand(new Command(deviceIds[index], Command.OFF));

								Thread.sleep(500);
							}
						}
					}
					catch (InterruptedException ignore)
					{
					}
				}
			});
			th.setDaemon(true);
			th.start();
		}
	}

	public void turnRedOff()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnRedOff()");

			addCommand(new Command(deviceIds[0], Command.OFF));
		}
	}

	public void turnRedOn()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnRedOn()");

			addCommand(new Command(deviceIds[0], Command.ON));
		}
	}

	public void turnYellowOff()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnYellowOff()");

			addCommand(new Command(deviceIds[1], Command.OFF));
		}
	}

	public void turnYellowOn()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnYellowOn()");

			addCommand(new Command(deviceIds[1], Command.ON));
		}
	}

	public void turnGreenOff()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnGreenOff()");

			addCommand(new Command(deviceIds[2], Command.OFF));
		}
	}

	public void turnGreenOn()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("turnGreenOn()");

			addCommand(new Command(deviceIds[2], Command.ON));
		}
	}

	public void flashAll()
	{
		flash(-1);
	}

	public void flashRed()
	{
		flash(0);
	}

	public void flashYellow()
	{
		flash(1);
	}

	public void flashGreen()
	{
		flash(2);
	}

	public void stage()
	{
		lastCommand = System.currentTimeMillis();
		synchronized (lock)
		{
			logger.debug("stage()");
			
			// start by resetting
			turnOff();

			long sleep = 0;

			try
			{
				turnRedOn();
				sleep = Math.round((Math.random() * 6000));
				Thread.sleep(sleep);
				turnYellowOn();
				sleep = Math.round((Math.random() * 6000));
				Thread.sleep(sleep);
				turnGreenOn();
				Thread.sleep(10000);
				turnOff();
			}
			catch (InterruptedException ignore)
			{
				turnOff();

			}
		}
	}

	protected void addCommand(Command command)
	{
		synchronized (lock)
		{
			try
			{
				controller.addCommand(command);
			}
			catch (WastedException e)
			{
				logger.warn("Queue is dead, but should have been reinitializing");
				addCommand(command);
//				shutdown();
//				try
//				{
//					init();
//					addCommand(command);
//				}
//				catch (IOException e1)
//				{
//					// not sure what to do so just bail at this point.
//					throw new RuntimeException(e1);
//				}
			}

		}
	}

	protected void outputState(UnitEvent evt)
	{
		String code = String.valueOf(evt.getCommand().getHouseCode());
		code += String.valueOf(evt.getCommand().getUnitCode());
		if (code.equals(System.getProperty("redlight.id")))
		{
			if (evt.getCommand().getFunctionByte() == Command.ON)
				red = true;
			else if (evt.getCommand().getFunctionByte() == Command.OFF)
				red = false;
		}
		else if (code.equals(System.getProperty("yellowlight.id")))
		{
			if (evt.getCommand().getFunctionByte() == Command.ON)
				yellow = true;
			else if (evt.getCommand().getFunctionByte() == Command.OFF)
				yellow = false;
		}
		else if (code.equals(System.getProperty("greenlight.id")))
		{
			if (evt.getCommand().getFunctionByte() == Command.ON)
				green = true;
			else if (evt.getCommand().getFunctionByte() == Command.OFF)
				green = false;
		}
		logger.debug("RED light " + red);
		logger.debug("YELLOW light " + yellow);
		logger.debug("GREEN light " + green);
	}

	public void allLightsOff(UnitEvent event)
	{
		outputState(event);
	}

	public void allLightsOn(UnitEvent event)
	{
		outputState(event);
	}

	public void allUnitsOff(UnitEvent event)
	{
		outputState(event);
	}

	public void unitBright(UnitEvent event)
	{
		outputState(event);
	}

	public void unitDim(UnitEvent event)
	{
		outputState(event);
	}

	public void unitOff(UnitEvent event)
	{
		outputState(event);
	}

	public void unitOn(UnitEvent event)
	{
		outputState(event);
	}
}
