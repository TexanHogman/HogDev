package com.hogdev.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpURL;
import org.apache.webdav.lib.WebdavResource;

public class CopyToWebDav
{
    static int countA, countB = 0;
    static long last = 0;

    public static void main(String[] args)
    {
        try
        {
            while (true)
            {
                countA++;
                try
                {
                    CopyToWebDav.copyFile();
                    System.gc();
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Thread.sleep(5000);
            }
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void copyFile() throws Exception
    {
        WebdavResource wdr = null;
        try
        {
            String url = "http://virtual.thehogges.com:8080/TheHoggesWebMedia/webcam/";
            HttpURL hrl = new HttpURL(url);
            // webdav server login username & password
            hrl.setUserinfo("rick", "liberty");
            System.out.println("User/Password set.");
            wdr = new WebdavResource(hrl);
            // wdr.setDebug(3);
            String wdrpath = wdr.getPath();
            String filename = "HoggeCam1.jpg";
            String file = "C:\\WebResources\\TheHoggesWeb\\Media\\webcam\\" + filename;
            String path = wdrpath + filename;
            System.out.println("wdrpath=" + wdrpath);
            System.out.println("path=" + path);
            while (true)
            {
                File f = new File(file);
                if(f.lastModified() > last)
                {
                    last = f.lastModified();
                    InputStream is = new FileInputStream(f);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
    
                    while (is.available() > 0)
                        os.write(is.read());
    
                    is.close();
                    long start = System.currentTimeMillis();
                    boolean rslt = wdr.putMethod(path + ".tmp", os.toByteArray());
                    long write = System.currentTimeMillis() - start;
                    os.close();
    
                    start = System.currentTimeMillis();
                    
                    rslt |= wdr.deleteMethod(path);
                    
                    long delete = System.currentTimeMillis() - start;
    
                    start = System.currentTimeMillis();
                    
                    rslt |= wdr.moveMethod(path + ".tmp", path);
                    
                    long move = System.currentTimeMillis() - start;
    
                    System.out.println("Iteration [" + countA + ":" + ++countB + "] result1 = " + rslt + " Memory usage ["
                            + Runtime.getRuntime().totalMemory() + "] Times [" + write + ":" + delete + ":" + move + "]");
                }
                else
                {
                    System.out.println("Iteration [" + countA + ":" + ++countB + "] result1 = Image unchanged Memory usage ["
                            + Runtime.getRuntime().totalMemory() + "]");
                }
                Thread.sleep(5000);
            }
        }
        finally
        {
            if (wdr != null) wdr.close();
        }
    }
}
