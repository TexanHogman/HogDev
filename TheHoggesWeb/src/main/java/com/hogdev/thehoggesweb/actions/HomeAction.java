/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.enterprise.utils.ResourceUtil;

/**
 * @author
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HomeAction extends Action
{
    static Logger logger = Logger.getLogger(HomeAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception
    {
	String baseDir = (String)System.getProperty(Constants.BASE_DIR);
	
	List folders = (List)request.getSession().getServletContext().getAttribute("whatsNew");
	ArrayList resources = new ArrayList();
	for(int i = 0; folders != null && i < folders.size(); i++)
	{
		File file =((File)folders.get(i));
		String strPath = file.getPath().substring(baseDir.length());
		Resource resource = ResourceUtil.buildResource(baseDir, strPath);
		//reset thumb to that of image
		resources.add(resource);
	}
	
	request.setAttribute(Constants.RESOURCE_LIST_KEY, resources);
	
	return mapping.findForward("success");
    }
}
