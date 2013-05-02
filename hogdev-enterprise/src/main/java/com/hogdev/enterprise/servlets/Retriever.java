package com.hogdev.enterprise.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @version 1.0
 * @author
 */
public class Retriever extends BaseRelative
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5097431218604988789L;
	static Logger logger = Logger.getLogger(Retriever.class);

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// String path = request.getParameter(Constants.PATH_KEY);
		String path = request.getPathInfo();
		if (path == null)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		path = baseDir + path;

		File file = new File(path);
		if (!file.exists() || !file.isFile())
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		int len = (int)file.length();



		String eTag = genEtag(path);
		if (eTag != null)
			response.setHeader("ETag", "\"" + eTag + "\"");

		// Now see if this was a range request.
		String range = request.getHeader("Range");

		boolean rangeRequest = false;

		int rangeStart = 0;
		int rangeEnd = len - 1;

		// If it is a range request then find the requested range boundaries.
		try
		{
			if (range != null)
			{
				rangeRequest = true;

				int bytesEI = range.indexOf("bytes=");
				int dashI = range.lastIndexOf("-");

				rangeStart = Integer.parseInt((range.substring(bytesEI + 6, dashI)).trim());

				if (dashI != (range.length() - 1))
					rangeEnd = Integer.parseInt((range.substring(dashI + 1)).trim());
			}
		}
		catch (Exception ignore)
		{
			// I blew up on parseInt so just return entire file
			rangeRequest = false;
		}

		OutputStream os = response.getOutputStream();
//        response.addDateHeader("Last-Modified", file.lastModified());
        response.setHeader("Accept-Ranges", "bytes");
		response.setContentType(getMimeType(file));
        
		if(!rangeRequest)
		{
	        response.setContentLength(len);
			
		}
		else
		{
	        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
	        response.setContentLength((rangeEnd + 1) - rangeStart);
	        response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + len);
		}

		getResource(file, os, rangeStart, rangeEnd);
	}

	protected String getMimeType(File file)
	{
		// it seems like mime type are always lower case and if not I will get a
		// null
		String content = getServletContext().getMimeType(file.getName().toLowerCase());
		if (content == null)
			content = "text/plain";

		logger.debug("Content type for " + file.getName() + " = " + content);

		return content;
	}

	protected void getResource(File file, OutputStream os) throws IOException
	{
		getResource(file, os, 0, (int)file.length() - 1);
	}

	protected void getResource(File file, OutputStream os, int start, int end) throws IOException
	{
		InputStream is = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		int length = end - start + 1;
		byte[] data = new byte[length];
		bis.read(data, start, length);
		bos.write(data);
		bos.flush();
		is.close();
		os.flush();
	}

	// Build a unique ETag for this file.
	protected String genEtag(String input)
	{
		String eTag = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(input.getBytes());

			BigInteger number = new BigInteger(1, md.digest());

			StringBuffer sb = new StringBuffer('0');

			sb.append(number.toString(16));

			eTag = sb.toString();
		}
		catch (NoSuchAlgorithmException ignore)
		{
		}
		return eTag;
	}

}
