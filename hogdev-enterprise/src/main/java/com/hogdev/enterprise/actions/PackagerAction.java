/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.actions;

import java.io.BufferedOutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.enterprise.forms.PackagerForm;
import com.hogdev.enterprise.utils.ResourceUtil;
import com.hogdev.util.GeneralUtils;

/**
 * @author 
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PackagerAction extends Action
{
	private static String newline = System.getProperty("line.separator");

	static Logger logger = Logger.getLogger(PackagerAction.class);

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		logger.debug(request.getRequestURL());
		String url = request.getRequestURL().toString();
		String prefix = url.substring(0, url.indexOf(request.getContextPath()));

		String baseDir = (String)System.getProperty(Constants.BASE_DIR);
		
		if (baseDir == null)
		{
			logger.error("baseDir is not set context");
			ActionErrors errors = new ActionErrors();
			errors.add("error", new ActionMessage("common.config.error"));
			saveErrors(request, errors);
			return mapping.findForward("error");
		}
//		String[] selected = request.getParameterValues("selectedResources");
		PackagerForm pform = (PackagerForm)form;
		String[] selected = pform.getSelectedResources();
		ArrayList list = new ArrayList();
		if (selected == null)
		{
			// this is now handled by the validate method 
			logger.error("turn validation on");
			ActionErrors errors = new ActionErrors();
			errors.add("error", new ActionMessage("common.config.error"));
			saveErrors(request, errors);
			return mapping.findForward("error");
		}
		else
		{
			for (int i = 0; i < selected.length; i++)
			{
				logger.debug(selected[i] + " is selected");
				ArrayList tree =
					ResourceUtil.buildResourceTree(baseDir, selected[i]);
				list.addAll(tree);
			}

			// check to see if they are all audio types
			for (int i = 0; i < list.size(); i++)
			{
				Resource res = (Resource)list.get(i);
				if (res.getType() != Constants.AUDIO)
				{
					logger.debug(
						"non audio file found ["
							+ res.getPath()
							+ "] zip not yet supported");
					ActionErrors errors = new ActionErrors();
					errors.add("selectedResources", new ActionMessage("details.message.notsupported"));
					saveErrors(request, errors);
					return (new ActionForward(mapping.getInput()));
				}
			}

			response.setContentType("audio/x-mpegurl");
			//			response.setContentType("text/plain");
			BufferedOutputStream bs =
				new BufferedOutputStream(response.getOutputStream());
			// ok now we have the list
			for (int i = 0; i < list.size(); i++)
			{
				Resource res = (Resource)list.get(i);
				String strLine =
					prefix
						+ request.getContextPath()
						+ "/retriever"
						+ res.getPath();
				strLine = encode(strLine);
				bs.write(strLine.getBytes());
				bs.write(newline.getBytes());
				logger.debug(strLine);
			}
			bs.flush();
		}

		return null;
	}

	private String encode(String str)
	{
		String strRet = GeneralUtils.stringReplace(str, " ", "%20");
		return strRet;
	}

}
