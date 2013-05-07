/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.json.JSONObject;

/**
 * @author
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AutomationAction extends DispatchAction
{
	static Logger logger = Logger.getLogger(AutomationAction.class);

	private static String POWERHOME = "http://localhost:9080";
	// 1 = time
	// 2 = sunrise
	// 3 = sunset
	// 4 = dawn
	// 5 = dusk
	private static Pattern TIMES = Pattern
			.compile("<td align=left valign=center bgcolor=\"#00ff00\" class=ht2r1c1><span class=ht2r1c1e1><b>Time:</b> </span><span class=ht2r1c1e2>(.*) &nbsp&nbsp</span><span class=ht2r1c1e3><b>Sunrise:</b> </span><span class=ht2r1c1e4>(.*) &nbsp&nbsp</span><span class=ht2r1c1e5><b>Sunset:</b> </span><span class=ht2r1c1e6>(.*) &nbsp&nbsp</span><span class=ht2r1c1e7><b>Dawn:</b> </span><span class=ht2r1c1e8>(.*) &nbsp&nbsp</span><span class=ht2r1c1e9><b>Dusk:</b> </span><span class=ht2r1c1e10>(.*)</span></td>");
	private static Pattern DEVICE_STATUS_START = Pattern
			.compile("<form method=\"get\" action=\"/ph-cgi/devicebtn\">.*");
	private static Pattern MACRO_START = Pattern
	.compile("<form method=\"get\" action=\"/ph-cgi/playmacro\">.*");
	private static Pattern MACRO = Pattern
	.compile("<option value=\"(.*)\">(.*)");
	private static Pattern DEVICE_STATUS = Pattern
			.compile(".*<font color=\"#000000\">(.*)</font>.*");
	private static Pattern DEVICE_ID = Pattern
			.compile(".*<font color=\"#000000\">(.*) \\[.*\\]</font>.*");

	public ActionForward macro(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		DynaActionForm autoForm = (DynaActionForm) form;

		StringBuilder urlString = new StringBuilder();
		urlString.append("/ph-cgi/playmacro");

		urlString.append("?id=" + URLEncoder.encode(autoForm.getString("id"), "UTF-8"));
		urlString.append("&nexturl=");
		

		toPowerHome(urlString.toString());

		return mapping.findForward("success");
	}

	public ActionForward device(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		DynaActionForm autoForm = (DynaActionForm) form;

		StringBuilder urlString = new StringBuilder();
		urlString.append("/ph-cgi/devicebtn");

		
		urlString.append("?id=" + URLEncoder.encode(autoForm.getString("id"), "UTF-8"));
		urlString.append("&nexturl=");
		urlString.append("&type=" + autoForm.getString("type"));
		urlString.append("&cmd=" + autoForm.getString("cmd"));

		toPowerHome(urlString.toString());

		return mapping.findForward("success");
	}

	private void toPowerHome(String urlString) 
	{
		logger.debug("message going to be sent and processed");
		try
		{
			URL url = new URL(POWERHOME + urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			System.out.println("Connected");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = rd.readLine();
			while (line != null)
			{
				System.out.println(line);
				line = rd.readLine();
			}
			rd.close();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		finally
		{
			logger.debug("message sent and processed");
		}
	}

	public ActionForward status(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		JSONObject json = new JSONObject();
		
		logger.debug("status request");

		try
		{
			// go get current status
			URL url = new URL(POWERHOME);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			boolean gotTimes = false;
			List<JSONObject> devices = new ArrayList<JSONObject>();
			List<JSONObject> macros = new ArrayList<JSONObject>();
			List<JSONObject> macrosOnOff = new ArrayList<JSONObject>();
			while ((line = rd.readLine()) != null)
			{
				if (!gotTimes)
				{
					Matcher matcher = TIMES.matcher(line);
					if (matcher.matches())
					{
						json.put("time", matcher.group(1));
						json.put("sunrise", matcher.group(2));
						json.put("sunset", matcher.group(3));
						json.put("dawn", matcher.group(4));
						json.put("dusk", matcher.group(5));

						gotTimes = true;
					}
				}
				Matcher matcher = MACRO_START.matcher(line);
				if (matcher.matches())
				{
					JSONObject latestOff = null;
					do
					{
						line = rd.readLine();
						Matcher info = MACRO.matcher(line);
						if (info.matches())
						{
							String id = info.group(1);
							String desc = info.group(2);
							if(id.length() > 0)
							{
								// on off will come alphabetically one after another
								if(id.endsWith("OFF"))
								{
									if(latestOff != null)
									{
										macros.add(latestOff);
										latestOff = null;
									}
									
									latestOff = new JSONObject();
									latestOff.put("id", id);
									latestOff.put("desc", desc);
								}
								else if(latestOff != null && id.endsWith("ON"))
								{
									JSONObject on = new JSONObject();
									on.put("id", id);
									on.put("desc", desc);
									macrosOnOff.add(on);
									macrosOnOff.add(latestOff);
									latestOff = null;
								}
								else
								{
									// add latest off if I get here meaning next was not on
									if(latestOff != null)
									{
										macros.add(latestOff);
										latestOff = null;
									}
									
									JSONObject macro = new JSONObject();
									macro.put("id", id);
									macro.put("desc", desc);
									macros.add(macro);
								}
							}
						}
						else
							break;
					}
					while(true);
					
					if(latestOff != null)
						macros.add(latestOff);
					
					json.put("macros", macros);
					json.put("macrosOnOff", macrosOnOff);
				}
				
				matcher = DEVICE_STATUS_START.matcher(line);
				if (matcher.matches())
				{
					JSONObject device = new JSONObject();

					line = rd.readLine();
					Matcher info = DEVICE_STATUS.matcher(line);
					if (info.matches())
						device.put("on",
								new Boolean(!info.group(1).equals("Off")));
					line = rd.readLine();
					line = rd.readLine();
					line = rd.readLine();
					line = rd.readLine();
					info = DEVICE_ID.matcher(line);
					if (info.matches())
						device.put("id", info.group(1));

					devices.add(device);
				}
			}
			json.put("devices", devices);

			rd.close();
		}
		catch (Exception e)
		{
			logger.error(e);
		}
		finally
		{
			logger.debug("status processed");
		}

		PrintWriter writer = response.getWriter();
		writer.println(json);
		writer.flush();
		writer.close();

		return null;
	}

}
