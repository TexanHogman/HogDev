package com.hogdev.widgets;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.hogdev.util.MailSource;
import com.hogdev.util.Mailer;

public class EMailer implements MailSource, Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 7447855590874364886L;

    // SUBJECT
    private String subject;

    // RETURN ADDRESS
    private String from;

    // MESSAGE
    private String message;

    // DESTINATION
    private String[] to;

    // CARBON COPIES
    private String[] cc;

    // MAIL SERVER HOST NAME
    private String mailServer;

    // LATEST STATUS
    String strStatus;

    private boolean bDone;
    private boolean bSucceeded;

    // for notifying listeners
    private PropertyChangeSupport changes;

    /**
     **/
    public static void main(String args[])
    {
        EMailer mailer = new EMailer();
        mailer.setMailServer("10.1.193.69");
        mailer.setTo(0, "@.com");
        mailer.setFrom("@.com");
        mailer.setSubject("This is the subject");
        mailer.setMessage("This is the message");
        mailer.sendMail();

        // wait on the emailer to say it is done so I can check whether it was succesful or not
        while (!mailer.getDone())
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (Exception ex)
            {
            }
        }

        boolean bSuccess = mailer.getSucceeded();

        System.out.println("Success = " + bSuccess);
    }

    public EMailer()
    {
        try
        {
            //         mail     = new Mailer(this);

            InetAddress inet = InetAddress.getLocalHost();
            from = inet.getHostName();
            //         to = new Vector();
            to = new String[1];
            cc = new String[1];
            message = "";
            subject = "";
            mailServer = "";
            strStatus = "";
            changes = new PropertyChangeSupport(this);
        }
        catch (UnknownHostException ex)
        {
            setStatus("EMailer Can not find HostName");
        }
    }

    public void setTo(String[] s)
    {
        //      to.addElement( s );
        to = s;
    }

    public String[] getTo()
    {
        return to;
    }

    public void setTo(int iIndex, String s)
    {
        //      to.addElement( s );
        to[iIndex] = s;
    }

    public String getTo(int iIndex)
    {
        return to[iIndex];
    }

    public void setCc(String[] s)
    {
        cc = s;
    }

    public String[] getCc()
    {
        return cc;
    }

    public void setCc(int iIndex, String s)
    {
        cc[iIndex] = s;
    }

    public String getCc(int iIndex)
    {
        return cc[iIndex];
    }

    public void setFrom(String s)
    {
        from = s;
    }

    public String getFrom()
    {
        return from;
    }

    public void setName(String s)
    {
        from = s;
    }

    public String getName()
    {
        return from;
    }

    public void setSubject(String s)
    {
        subject = s;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setMessage(String s)
    {
        message = s;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMailServer(String s)
    {
        mailServer = s;
    }

    public String getMailServer()
    {
        return mailServer;
    }

    public void sendMail()
    {
        bDone = false;
        bSucceeded = false;
        Mailer mail = new Mailer(this);
        mail.start();
    }

    /**
     * This method is required by MailSource.
     */
    public void data(PrintStream out)
    {
        out.println(message);
    }

    /**
     * This method is required by MailSource.
     */
    public void setDone(boolean succeeded)
    {
        if (succeeded)
            setStatus("EMailer Message successfully sent");
        else
            setStatus("EMailer Message NOT sent");

        bDone = true;
        bSucceeded = succeeded;
    }

    /**
     * This method is required by MailSource.
     */
    public void setStatus(String s)
    {
        changes.firePropertyChange("status", strStatus, s);
        strStatus = s;
    }

    public String getStatus()
    {
        return strStatus;
    }

    public boolean getDone()
    {
        return bDone;
    }

    public boolean getSucceeded()
    {
        return bSucceeded;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        changes.removePropertyChangeListener(l);
    }
}
