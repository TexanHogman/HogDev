

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.util.BitSet;
import java.util.TooManyListenersException;

public class IRReceiver implements SerialPortEventListener
// public class IRReceiver implements IRActionListener, LIRCListener
{
	final static String PORT = "COM1";
	SerialPort serialPort;
	final static Object lock = new Object();

	public static void main(String[] args)
	{
		try
		{
			IRReceiver ir = new IRReceiver();
			ir.demo();
			Thread.sleep(600000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("EXITING");

	}

	public IRReceiver()
	{

	}

	public void demo()
	{
		try
		{
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(PORT);
			serialPort = (SerialPort) portId.open("IR Port", 2000);
			if (serialPort == null)
				System.out.println("Unable to open port: " + PORT);
			else
			{
				serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				serialPort.addEventListener(this);
				serialPort.notifyOnCarrierDetect(true);
				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						while (true)
						{
							synchronized (lock)
							{
								try
								{
									lock.wait();
									long lastCD = System.currentTimeMillis();
									BitSet bits = new BitSet(1000);

									int i = 0;
									while (System.currentTimeMillis() - lastCD < 100)
									{
										// invert it
										boolean cd = !serialPort.isCD();
										if (cd)
											lastCD = System.currentTimeMillis();

										bits.set(i++, cd);
									}
									minimize(bits);
								}
								catch (java.lang.InterruptedException e)
								{
									e.printStackTrace();
								}
								serialPort.notifyOnCarrierDetect(true);
							}
						}
					}

				});
				t.setDaemon(true);
				t.start();

				serialPort.setRTS(true);
			}
		}
		catch (NoSuchPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (PortInUseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TooManyListenersException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedCommOperationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void serialEvent(SerialPortEvent event)
	{
		if (event.getEventType() == SerialPortEvent.CD)
		{
			synchronized (lock)
			{
				serialPort.notifyOnCarrierDetect(false);
				lock.notifyAll();
			}
		}
	}

	public static BitSet minimize(BitSet bits)
	{
		// first drop first set group
		int end = bits.length();
		int start = bits.nextClearBit(0);
		if (start <= 0 || end <= 0 || end <= start)
			return null;
		bits = bits.get(start, end);
		end = bits.length();
		start = bits.nextSetBit(0);
		if (start <= 0 || end <= 0 || end <= start)
			return null;
		bits = bits.get(start, end);

		int s = Integer.MAX_VALUE;
		int sindex = 0;
		int index = 0;
		while (true)
		{
			index = bits.nextClearBit(sindex + 1);
			if (index == -1)
				break;
			if (index - sindex < s)
				s = index - sindex;
			sindex = index;
			index = bits.nextSetBit(sindex + 1);
			if (index == -1)
				break;
			if (index - sindex < s)
				s = index - sindex;
			sindex = index;
		}

		for (int i = 0; i < bits.length(); i++)
		{
			System.out.print(bits.get(i) ? "1" : "0");
		}
		System.out.println();
		System.out.println(s);

		// now I know my shortest span of bits
		sindex = 0;
		index = 0;
		int r = 0;
		BitSet result = new BitSet();
		int m = 0;
		while (true)
		{
			index = bits.nextClearBit(sindex + 1);
			if (index == -1)
				break;
			r = index - sindex;
			for (int j = 0; j < r / s; j++)
				result.set(m++);
			sindex = index;

			index = bits.nextSetBit(sindex + 1);
			if (index == -1)
				break;
			r = index - sindex;
			for (int j = 0; j < r / s; j++)
				result.clear(m++);
			sindex = index;
		}

		for (int i = 0; i < result.length(); i++)
		{
			System.out.print(result.get(i) ? "1" : "0");
		}
		System.out.println();
		return null;
	}

}
