package com.hogdev.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * A class that connects to an SMTP server and sends an e-mail message. A class
 * implementing MailSource must be passed to the Mailer to provide the content
 * and destination of the message.
 */
public class Mailer extends Thread
{
    /**
     * The instance that will provide the content of the message.
     */
    protected MailSource source;

    /**
     * The socket used to connect to the SMTP server.
     */
    protected Socket sock = null;

    /**
     * The input stream from the SMTP server.
     */
    protected BufferedReader in = null;

    /**
     * The output stream to the SMTP server.
     */
    protected PrintStream out = null;

    /**
     * Creates a Mailer with the specified data source.
     * 
     * @param source
     *            The MailSource that will provide the content of the message.
     */
    public Mailer(MailSource source)
    {
        // System.out.println( "Mailer <init>" );
        this.source = source;
    }

    /**
     * When the Mailer is started, it gathers information from the MailSource,
     * connects to the SMTP server, and sends the message.
     */
    public void run()
    {
        boolean test;
        String dest[];

        // current line read from socket
        String line;

        source.setStatus("Contacting " + source.getMailServer() + "...");
        try
        {
            sock = new Socket(source.getMailServer(), 25);
        }
        catch (Exception e)
        {
            source.setStatus("Socket connection to " + source.getMailServer()
                    + " failed!");
            source.setDone(false);
            return;
        }
        try
        {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new PrintStream(new BufferedOutputStream(sock
                    .getOutputStream()));
        }
        catch (IOException e)
        {
            source.setStatus("Could not open streams to "
                    + source.getMailServer() + "!");
            source.setDone(false);
            return;
        }
        source.setStatus("Connected to SMTP socket on "
                + source.getMailServer() + ".");

        try
        {
            line = in.readLine();
            out.println("HELO");
            out.flush();
            do
            {
                line = in.readLine();
            }
            while (!line.regionMatches(0, "250", 0, 3));

            out.println("MAIL FROM: <" + source.getFrom() + ">");
            out.flush();
            line = in.readLine();
            if (!line.regionMatches(0, "250", 0, 3))
            {
                // System.out.println("Sent: 'MAIL FROM:
                // <"+source.getFrom()+">'");
                // System.out.println("Received: '"+line+"'");
                source.setStatus("Delivery error: Sender wasn't accepted!");
                close();
                source.setDone(false);
                return;
            }

            test = false;
            dest = source.getCc();
            for (int i = 0; i < dest.length; i++)
            {
                if (dest[i] != null)
                {
                    out.println("RCPT TO: <" + dest[i] + ">");
                    out.flush();
                    line = in.readLine();
                    if (line.regionMatches(0, "250", 0, 3)) test = true;
                    else
                    {
                        source.setStatus("Delivery error: Recipient " + dest[i]
                                + " wasn't accepted!");
                        // System.out.println( "Sent (cc): 'RCPT TO: <" +
                        // dest[i] + ">'" );
                        // System.out.println( "Received: '" + line + "'" );
                    }
                }
            }
            dest = source.getTo();
            for (int i = 0; i < dest.length; i++)
            {
                if (dest[i] != null)
                {
                    out.println("RCPT TO: <" + dest[i] + ">");
                    out.flush();
                    line = in.readLine();
                    if (line.regionMatches(0, "250", 0, 3)) test = true;
                    else
                    {
                        source.setStatus("Delivery error: Recipient " + dest[i]
                                + " wasn't accepted!");
                        // System.out.println("Sent (to): 'RCPT TO:
                        // <"+dest[i]+">'");
                        // System.out.println("Received: '"+line+"'");
                    }
                }
            }
            if (!test)
            {
                close();
                source.setDone(false);
                return;
            }

            out.println("DATA");
            out.flush();
            line = in.readLine();
            if (!line.regionMatches(0, "354", 0, 3))
            {
                source.setStatus("Delivery error: Data wasn't accepted!");
                // System.out.println("Sent: 'DATA'");
                // System.out.println("Recieved: '"+line+"'");
                close();
                source.setDone(false);
                return;
            }
            out.print("To: " + dest[0]);
            for (int i = 1; i < dest.length; i++)
                out.print(", " + dest[i]);
            out.println("");
            dest = source.getCc();
            if (dest.length > 0)
            {
                out.print("CC: " + dest[0]);
                for (int i = 1; i < dest.length; i++)
                    out.print(", " + dest[i]);
                out.println("");
            }
            out.println("Subject: " + source.getSubject());
            out.println("From: \"" + source.getName() + "\" <"
                    + source.getFrom() + ">");
            out.println("");
            out.flush();
            source.data(out);
            out.println(".");
            out.flush();
            line = in.readLine();
            out.println("quit");
            out.flush();

            close();
            source.setDone(true);
        }
        catch (IOException ex)
        {
            // This exception was most likely generated from the line =
            // in.readLine() call
            source.setStatus("Read error!");
            close();
            source.setDone(false);
        }
    }

    /**
     * Closes the connection to the SMTP server.
     */
    protected void close()
    {
        try
        {
            sock.close();
        }
        catch (IOException e)
        {
            source.setStatus("Socket close error!");
        }
    }
}
