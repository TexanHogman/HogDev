package com.hogdev.racecontrol;

import org.lirc.LIRCEvent;

public class IREvent
{
	private long time = System.currentTimeMillis();
	LIRCEvent event;

	public IREvent(long time, LIRCEvent event )
	{
		super();
		this.time = time;
		this.event = event;
	}

	public LIRCEvent getEvent()
	{
		return event;
	}

	public long getTime()
	{
		return time;
	}
}
