/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.enterprise.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author 
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PackagerForm extends ActionForm
{
	/**
     * 
     */
    private static final long serialVersionUID = 8033298243180055655L;
    String[] selectedResources;
	String path;

	/**
	 * @return
	 */
	public String[] getSelectedResources()
	{
		return selectedResources;
	}

	/**
	 * @param strings
	 */
	public void setSelectedResources(String[] strings)
	{
		selectedResources = strings;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		ActionErrors errors = new ActionErrors();
		if(selectedResources == null || selectedResources.length == 0)
			errors.add("selectedResources", new ActionMessage("details.message.selectionreq"));
		return errors;
	}

	/**
	 * @return
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * @param string
	 */
	public void setPath(String string)
	{
		path = string;
	}
}
