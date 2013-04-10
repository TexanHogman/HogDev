package com.hogdev.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JApplet;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * JThumb is a simple utility designed to create thumbnails from jpg gif and bmp
 * files. The format of the thumbnail will be a jpg file. Please make any
 * changes desired to this source but keep me informed so I can benefit from
 * them Thanks Hogge rhogge@austin.rr.com 2001-01-19.
 */
public class JThumb extends JApplet
{
    /**
     * 
     */
    private static final long serialVersionUID = -5612541811793825082L;

    public final static String IMAGE_TYPES[] = { ".jpg", ".gif", ".bmp" };

    int iSize = 100, iWidth = -1, iHeight = -1;

    float flQuality = 0.5f;

    String strSource = ".";

    String strDest = "%F_thumb";

    String strFolder = null;

    boolean bRecurse = false;

    boolean bRotateCW = false;

    boolean bRotateCCW = false;

    boolean bHTML = false;

    Writer wr;

    MediaTracker tracker;

    public JThumb()
    {
        tracker = new MediaTracker(this);
    }

    /**
     * 
     * 
     * @param i
     */
    public void setSize(int i)
    {
        this.iSize = i;
    }

    /**
     * 
     * 
     * @param i
     */
    public void setHeight(int i)
    {
        this.iHeight = i;
    }

    /**
     * 
     * 
     * @param i
     */
    public void setWidth(int i)
    {
        this.iWidth = i;
    }

    /**
     * 
     * 
     * @param f
     */
    public void setQuality(float f)
    {
        this.flQuality = f;
    }

    /**
     * 
     * 
     * @param str
     */
    public void setSource(String str)
    {
        this.strSource = str;
    }

    /**
     * 
     * 
     * @param str
     */
    public void setDest(String str)
    {
        this.strDest = str;
    }

    /**
     * 
     * 
     * @param str
     */
    public void setFolder(String str)
    {
        this.strFolder = str;
    }

    /**
     * 
     * 
     * @param b
     */
    public void setRecurse(boolean b)
    {
        this.bRecurse = b;
    }

    /**
     * 
     * 
     * @param b
     */
    public void setRotateCW(boolean b)
    {
        this.bRotateCW = b;
    }

    /**
     * 
     * 
     * @param b
     */
    public void setRotateCCW(boolean b)
    {
        this.bRotateCCW = b;
    }

    /**
     * 
     * 
     * @param b
     */
    public void setHTML(boolean b)
    {
        this.bHTML = b;
    }

    /**
     * basically a bunch of code to handle the commnad line arguments
     * 
     * @param args
     */
    public static void main(String args[])
    {
        int i = 0;
        int j = 0;
        int iSize = 100, iWidth = -1, iHeight = -1;
        float flQuality = 0.5f;
        String strSource = ".";
        String strDest = "%F_thumb";
        boolean bRecurse = false;
        String strFolder = null;
        boolean bHTML = false;
        String arg;
        char flag;
        boolean bRun = true;
        boolean bRotateCW = false;
        boolean bRotateCCW = false;

        while (i < args.length && args[i].startsWith("-"))
        {
            arg = args[i++];

            // use this type of check for arguments that require arguments
            if (arg.equalsIgnoreCase("-manual"))
            {
                System.err.println(JThumb.getManual());
                bRun = false;

            }
            else if (arg.equalsIgnoreCase("-m"))
            {
                if (i < args.length)
                {
                    iSize = Integer.parseInt(args[i++]);
                }
                else
                {
                    System.err
                            .println("-s requires a max size.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-w"))
            {
                if (i < args.length)
                {
                    iWidth = Integer.parseInt(args[i++]);
                }
                else
                {
                    System.err
                            .println("-w requires a max width.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-h"))
            {
                if (i < args.length)
                {
                    iHeight = Integer.parseInt(args[i++]);
                }
                else
                {
                    System.err
                            .println("-w requires a max height.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-q"))
            {
                if (i < args.length)
                {
                    flQuality = Float.parseFloat(args[i++]);
                }
                else
                {
                    System.err
                            .println("-q requires a quality level.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-s"))
            {
                if (i < args.length)
                {
                    strSource = args[i++];
                }
                else
                {
                    System.err
                            .println("-s requires a source.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-d"))
            {
                if (i < args.length)
                {
                    strDest = args[i++];
                }
                else
                {
                    System.err
                            .println("-d requires a destination formatting string.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-f"))
            {
                if (i < args.length)
                {
                    strFolder = args[i++];
                }
                else
                {
                    System.err
                            .println("-f requires a destination folder.  (-? gives help on options)");
                    bRun = false;
                }
            }
            else if (arg.equalsIgnoreCase("-rotate"))
            {
                if (i < args.length)
                {
                    String strTemp = args[i++];
                    if (strTemp.equalsIgnoreCase("cw")
                            || strTemp.equalsIgnoreCase("clockwise"))
                    {
                        bRotateCW = true;
                        bRotateCCW = false;
                    }
                    else if (strTemp.equalsIgnoreCase("ccw")
                            || strTemp.equalsIgnoreCase("counterclockwise"))
                    {
                        bRotateCCW = true;
                        bRotateCW = false;
                    }
                    else
                    {
                        System.err
                                .println("-rotate requires valid direction.  (-? gives help on options)");
                        bRun = false;
                    }
                }
                else
                {
                    System.err
                            .println("-rotate requires direction.  (-? gives help on options)");
                    bRun = false;
                }
            }
            // use this type of check for a series of flag arguments
            else
            {
                for (j = 1; j < arg.length(); j++)
                {
                    flag = arg.charAt(j);
                    switch (flag)
                    {
                    case 'r':
                    case 'R':
                        bRecurse = true;
                        break;

                    case 'g':
                    case 'G':
                        bHTML = true;
                        break;

                    case '?':
                        System.err.println(JThumb.getUsage());
                        bRun = false;
                        break;

                    default:
                        System.err.println("JThumb: illegal option " + flag
                                + "(-? gives help on options)");
                        bRun = false;
                        break;
                    }
                }
            }
        }

        if (bRun)
        {
            JThumb jt = new JThumb();
            jt.setSize(iSize);
            jt.setWidth(iWidth);
            jt.setHeight(iHeight);
            jt.setQuality(flQuality);
            jt.setSource(strSource);
            jt.setDest(strDest);
            jt.setFolder(strFolder);
            jt.setRecurse(bRecurse);
            jt.setHTML(bHTML);
            jt.setRotateCW(bRotateCW);
            jt.setRotateCCW(bRotateCCW);
            jt.process();
        }

        System.exit(0);
    }

    public static String getUsage()
    {
        String strRet = "JThumb\n"
                + " desc:  image manipulation utility\n"
                + " syntax: java com.hogge.util.JThumb [-manual] [-m] [-w] [-h] [-s] [-d] [-q]\n"
                + "                                    [-r] [-f] [-g] [-rotate CW|CCW]\n"
                + " flags: \n"
                + "  -manual: display text about product\n"
                + "  -m: max pixel size (still orignal width/height ratio) [100]\n"
                + "  -w: max pixel width [-1]\n"
                + "  -h: max pixel height [-1]\n"
                + "  -s: source (file or directory) [.]\n"
                + "  -d: dest format string [%F_thumb]\n"
                + "  -q: quality level (.25 .50 .75 1.0) 1.0 is highest quality [.5]\n"
                + "  -r: recurse directories (only if source is directory)\n"
                + "  -f: destination sub-folder (created under each folder for thumbnails)\n"
                + "  -g: generate HTML pages to display thumbnails\n"
                + "  -rotate: rotate image clockwise (CW) or counterclockwise (CCW)\n";
        return (strRet);
    }

    public static String getManual()
    {
        String strRet = getUsage()
                + "\n"
                + "JThumb version 1.0 user manual\n\n "
                + "I don't expect this to be an all emcompassing manual but just to explain "
                + "the basic functionality of JThumb. I wanted an easy way to publish my digitial images, so this version is "
                + "tailored to my needs.  Please feel free to modify the source to serve your needs, but I would ask to be "
                + "notified of the changes so I can incorporate them into my code. Thanks  Hogge (rhogge@austin.rr.com)\n\n"
                + "First thing, the argument processing requires a space between the option and the value, I know this is a pain "
                + "but for now I didn't have the time to change it.  Plus since I only one using it, I can manage. "
                + "I believe the -? returns a fairly good description of the command line options.  However, it should be noted "
                + "that the default settings are indicated by the value inside the [].  The -m option specifies the maximum size in "
                + "width or height.  The code will size the larger dimension to the value of the -m option.  This option takes "
                + "precedence over -w and -h options.  -d is the destination formatting string, for now it only will substitute "
                + "the filename for the %F value in the string. -g generates the html and is very new code so it it has hard "
                + "coded values for the tags.  It will however determine how many images it can place on each row in the table. "
                + "I have found that large images 1.5M and up require quite a bit of memory to process.  I believe the JVM will "
                + "only request up to 64M of memory before throwing an OutOfMemoryException, therefore I generally use the JVM "
                + "parameter -Xmx128M to increase this value to 128M.  If you experience similiar problems try this first."
                + "\n\nFor my use I created a batch file as follows 'java -Xmx128M JThumb -g -r -f thumbnails -s %1' and placed "
                + "a shortcut on my desktop.  This allows me to drag a folder or file to the icon and have the process run.";

        return (strRet);
    }

    /**
     * Print input to defined writer
     * 
     * @param str
     */
    private void print(String str)
    {
        try
        {
            wr.write(str);
            wr.flush();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * Print input to defined writer with newline
     * 
     * @param str
     */
    private void println(String str)
    {
        try
        {
            wr.write(str + "\n");
            wr.flush();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    /**
     * 
     * 
     * @param wr
     */
    public void setWriter(Writer wr)
    {
        this.wr = wr;
    }

    /**
     * run operation
     * 
     */
    public void process()
    {
        if (wr == null)
        {
            wr = new BufferedWriter(new OutputStreamWriter(System.out));
        }

        process(new File(strSource));

    }

    /**
     * Recursive method to process file or filder
     * 
     * @param fileSrc
     * @return
     */
    private boolean process(File fileSrc)
    {
        if (!fileSrc.exists())
        {
            println("Error: Source does not exist");
            return (false);
        }

        if (fileSrc.isDirectory())
        {
            Vector vec = new Vector();
            File[] fa = fileSrc.listFiles();
            TreeSet ts = new TreeSet(new FileNameComparator());
            for (int i = 0; i < fa.length; i++)
            {
                ts.add(fa[i]);
            }
            Iterator iter = ts.iterator();
            while (iter.hasNext())
            {
                File file = (File) iter.next();
                if (file.isFile()
                        || (file.isDirectory() && bRecurse && (strFolder == null || !file
                                .getName().equals(strFolder))))
                {
                    boolean bProcessed = process(file);
                    if (bProcessed && file.isFile())
                    {
                        vec.add(file.getName());
                    }
                }
            }

            // generate html if desired
            if (bHTML && vec.size() > 0)
            {
                try
                {
                    String strHTMLFile = fileSrc.getPath() + File.separator;
                    File fHTMLFolder = null;
                    if (strFolder != null)
                    {
                        fHTMLFolder = new File(fileSrc, strFolder);

                        // create redirection page
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                new File(fileSrc, "index.html")));
                        bw.write("<HTML>");
                        bw.newLine();
                        bw.write("<HEAD>");
                        bw.newLine();
                        bw.write("<TITLE>Thumbnail Redirect</TITLE>");
                        bw.newLine();
                        bw
                                .write("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL="
                                        + strFolder + "/index.html\">");
                        bw.newLine();
                        bw.write("</HEAD>");
                        bw.newLine();
                        bw.write("Please wait while page is being loaded.");
                        bw.newLine();
                        bw.write("</BODY>");
                        bw.newLine();
                        bw.write("</HTML>");
                        bw.newLine();
                        bw.close();

                    }
                    else
                    {
                        fHTMLFolder = fileSrc;
                    }

                    BufferedWriter bw = new BufferedWriter(new FileWriter(
                            new File(fHTMLFolder, "index.html")));
                    bw
                            .write("<!doctype html public \"-//w3c//dtd html 3.2//en\">");
                    bw.newLine();
                    bw.write("<html>");
                    bw.newLine();
                    bw.write("<head>");
                    bw.newLine();

                    bw.write("<title>Thumbnails</title>");
                    bw.newLine();
                    bw.write("</head>");
                    bw.newLine();

                    bw.write("<body bgcolor=\"#7d9ec0\">");
                    bw.newLine();

                    String strCurrentFolder = fileSrc.getAbsolutePath();
                    strCurrentFolder = stringReplace(strCurrentFolder,
                            File.separator + ".", "");

                    int iIndex = strCurrentFolder.lastIndexOf(File.separator);
                    if (iIndex != -1)
                    {
                        strCurrentFolder = strCurrentFolder
                                .substring(iIndex + 1);
                    }
                    bw.write("<center><b>Thumbnails for folder "
                            + strCurrentFolder + "</b><p><hr><p>");
                    bw.newLine();
                    bw
                            .write("<table border=1 cellspacing=0 cellpadding=0 width=\"95%\">");
                    bw.newLine();

                    bw.write("<tr>");
                    bw.newLine();

                    // guess this is good?
                    int iNumPerRow = 800 / iSize;
                    for (int i = 0; i < vec.size(); i++)
                    {
                        String strFileName = (String) vec.elementAt(i);
                        String strTNFileName = strFileName;
                        if (strDest != null)
                        {
                            strTNFileName = strFileName.substring(0,
                                    strFileName.lastIndexOf('.'));
                            strTNFileName = stringReplace(strDest, "%F",
                                    strTNFileName)
                                    + ".jpg";
                            strFileName = "../" + strFileName;
                        }
                        bw.write("<td>");
                        bw.newLine();
                        bw.write("<p><center>");
                        bw.newLine();
                        bw.write("<font size=\"+0\"><a href=\"" + strFileName
                                + "\"><img src=\"" + strTNFileName
                                + "\" hspace=0 vspace=0 border=0 ALT=\""
                                + (String) vec.elementAt(i) + "\"><br>"
                                + (String) vec.elementAt(i) + "</a></font>");
                        bw.newLine();
                        bw.write("</center>");
                        bw.newLine();
                        bw.write("</td>");
                        bw.newLine();
                        if ((i + 1) % iNumPerRow == 0)
                        {
                            bw.write("<tr></tr>");
                            bw.newLine();
                        }
                    }

                    bw.write("</tr>");
                    bw.newLine();
                    bw.write("</table>");
                    bw.newLine();
                    bw.write("<br>");
                    bw.newLine();
                    bw
                            .write("<p><font size=\"-1\">Click the thumbnail to see the full image.</font><p></center>");
                    bw.newLine();

                    bw.write("</body>");
                    bw.newLine();
                    bw.write("</html>");
                    bw.newLine();

                    bw.close();
                }
                catch (IOException io)
                {
                    println(io.getMessage());
                }
            }
        }
        else
        {
            String strSource = fileSrc.getName();

            // check to see if it is one of the supported graphic types
            String strName = strSource.toLowerCase();
            boolean bValidType = false;
            for (int j = 0; !bValidType && j < IMAGE_TYPES.length; j++)
            {
                if (strName.endsWith(IMAGE_TYPES[j])) bValidType = true;
            }

            if (!bValidType)
            {
                return (false);
            }

            long lStart = System.currentTimeMillis();
            print(fileSrc.getPath() + " ==> ");
            Image img = getToolkit().getImage(fileSrc.getPath());

            try
            {
                // add to the media tracker so I can wait until it had been
                // loaded
                tracker.addImage(img, 0);
                tracker.waitForID(0);

                int Iw, Ih;

                if (bRotateCW || bRotateCCW)
                {
                    Ih = img.getWidth(this);
                    Iw = img.getHeight(this);

                    // switch width and height
                    int iTemp = iWidth;
                    iWidth = iHeight;
                    iHeight = iWidth;
                }
                else
                {
                    Iw = img.getWidth(this);
                    Ih = img.getHeight(this);
                }

                BufferedImage bi1 = new BufferedImage(Iw, Ih,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D big55 = bi1.createGraphics();

                if (bRotateCW || bRotateCCW)
                {

                    // degrees x PI / 180 (deg to rad conversion)
                    if (bRotateCW)
                    {
                        big55.translate(Iw, 0);
                        big55.rotate(90.0 * Math.PI / 180.0);
                    }
                    else if (bRotateCCW)
                    {
                        big55.translate(0, Ih);
                        big55.rotate(270.0 * Math.PI / 180.0);
                    }
                }

                big55.drawImage(img, 0, 0, this);

                // reduce the image here!!! based upon options
                Image imgTn;
                if (iSize != -1)
                {
                    if (Iw > Ih) imgTn = bi1.getScaledInstance(iSize, -1,
                            Image.SCALE_DEFAULT);
                    else imgTn = bi1.getScaledInstance(-1, iSize,
                            Image.SCALE_DEFAULT);
                }
                else
                {
                    imgTn = bi1.getScaledInstance(iWidth, iHeight,
                            Image.SCALE_DEFAULT);
                }

                // add to the media tracker so I can wait until it had been
                // loaded
                tracker.addImage(imgTn, 1);
                tracker.waitForID(1);

                int iw = imgTn.getWidth(this);
                int ih = imgTn.getHeight(this);

                BufferedImage bi = new BufferedImage(iw, ih,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D big = bi.createGraphics();
                big.clearRect(0, 0, iw, ih);
                big.drawImage(imgTn, 0, 0, this);

                // create output file name
                String strName2 = strSource.substring(0, strSource
                        .lastIndexOf('.'));
                if (strDest != null)
                {
                    strName2 = stringReplace(strDest, "%F", strName2);
                }

                // create destination folder
                File folder;
                if (strFolder != null)
                {
                    folder = new File(fileSrc.getParentFile(), strFolder);
                    if (!folder.exists() && !folder.mkdir())
                    {
                        println("Could not create: " + fileSrc.getParentFile()
                                + File.separator + strFolder);
                        return (false);
                    }
                }
                else
                {
                    folder = fileSrc.getParentFile();
                }

                strName2 = strName2 + ".jpg";
                File file = new File(folder, strName2);
                FileOutputStream out = new FileOutputStream(file);

                // encodes BufferedImage as a JPEG data stream
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
                param.setQuality(flQuality, false);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(bi);

                out.flush();
                out.close();
                print(file.getPath() + "  DONE!");
                long lElapsed = System.currentTimeMillis() - lStart;
                println(" " + String.valueOf(lElapsed) + " msecs");

                // remove from the media tracker
                tracker.removeImage(imgTn, 1);
                tracker.removeImage(img, 0);
            }
            catch (Exception e)
            {
                System.out.println(e);
                return (false);
            }
        }
        return (true);
    }

    public void resize(InputStream is, OutputStream os) throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int i = bis.read();
        while (i != -1)
        {
            baos.write(i);
            i = bis.read();
        }

        Image img = getToolkit().createImage(baos.toByteArray());
        int Iw, Ih;

        // add to the media tracker so I can wait until it had been loaded
        tracker.addImage(img, 0);
        tracker.waitForID(0);

        // here
        Iw = img.getWidth(this);
        // here
        Ih = img.getHeight(this);

        BufferedImage bi1 = new BufferedImage(Iw, Ih,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D big55 = bi1.createGraphics();

        // here
        big55.drawImage(img, 0, 0, this);

        // reduce the image here!!! based upon options
        Image imgTn = null;
        if (iSize != -1)
        {
            if (Iw > Ih) imgTn = bi1.getScaledInstance(iSize, -1,
                    Image.SCALE_DEFAULT);
            else imgTn = bi1.getScaledInstance(-1, iSize, Image.SCALE_DEFAULT);
        }
        else
        {
            imgTn = bi1.getScaledInstance(iWidth, iHeight, Image.SCALE_DEFAULT);
        }

        // add to the media tracker so I can wait until it had been loaded
        tracker.addImage(imgTn, 1);
        tracker.waitForID(1);

        int iw = imgTn.getWidth(this);
        int ih = imgTn.getHeight(this);

        BufferedImage bi = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bi.createGraphics();
        big.clearRect(0, 0, iw, ih);
        big.drawImage(imgTn, 0, 0, this);

        // encodes BufferedImage as a JPEG data stream
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
        param.setQuality(flQuality, false);
        encoder.setJPEGEncodeParam(param);
        encoder.encode(bi);
    }

    /**
     * Utility to do a string replace
     * 
     * @param source
     * @param search
     * @param replace
     * @return
     */
    public static String stringReplace(String source, String search,
            String replace)
    {
        if (source != null && replace != null && search != null)
        {
            int index = 0;
            int len = replace.length();

            do
            {
                index = source.indexOf(search, index);
                if (index != -1)
                {
                    source = source.substring(0, index) + replace
                            + source.substring(index + search.length());
                    index += len;
                }
            }
            while (index != -1);
        }
        return (source);
    }
}

class FileNameComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        if (o1 instanceof File && o2 instanceof File)
        {
            return ((File) o1).getName().compareTo(((File) o2).getName());
        }
        else return 0;
    }
}
