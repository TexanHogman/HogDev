package com.hogdev.enterprise.actions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;

import com.hogdev.enterprise.Constants;

/**
 * @version 1.0
 * @author
 */
public class UploaderAction extends Action

{
    static Logger logger = Logger.getLogger(UploaderAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String[] no = {".exe", ".bat", ".vb", ".dll"};
        
        DynaActionForm dForm = (DynaActionForm) form;

        // Process the FormFile
        FormFile myFile = (FormFile) dForm.get("file");
        String contentType = myFile.getContentType();
        String fileName = myFile.getFileName();
        
        int fileSize = myFile.getFileSize();
        
        logger.info("Upload fileName= " + fileName + " size= " + fileSize);
		String strBaseDir = (String)System.getProperty(Constants.UPLOAD_DIR);
		if (strBaseDir == null)
		{
	        ActionErrors actionMessages = new ActionErrors();
		    logger.error("Upload dir name not set");
	        actionMessages.add(Globals.ERROR_KEY, new ActionMessage("common.config.error"));
	        saveErrors(request, actionMessages);
	        return mapping.findForward("home");
		}
		File dir = new File(strBaseDir);
		
		if(!dir.exists() || !dir.isDirectory())
		{
	        ActionErrors actionMessages = new ActionErrors();
		    logger.error("Upload dir is invalid");
	        actionMessages.add(Globals.ERROR_KEY, new ActionMessage("common.config.error"));
	        saveErrors(request, actionMessages);
	        return mapping.findForward("home");
		}
        if(fileName == null || fileName.length() == 0)
        {
	        ActionErrors actionMessages = new ActionErrors();
		    logger.warn("File name not provided");
	        actionMessages.add("file", new ActionMessage("upload.filerequired"));
	        saveErrors(request, actionMessages);
	        return mapping.findForward("success");
        }
		
        for(int i = 0; i < no.length; i++)
        {
            if(fileName.toLowerCase().endsWith(no[i]))
            {
    	        ActionErrors actionMessages = new ActionErrors();
    	        logger.warn("Disallowed file extension on: " + fileName);
    	        actionMessages.add("file", new ActionMessage("upload.disallowed"));
    	        saveErrors(request, actionMessages);
    	        return mapping.findForward("success");
            }
        }

        byte[] fileData = myFile.getFileData();
        File file = new File(dir, fileName);
        if(file.exists())
        {
	        ActionErrors actionMessages = new ActionErrors();
		    logger.warn("File already exists");
	        actionMessages.add("file", new ActionMessage("upload.fileexists"));
	        saveErrors(request, actionMessages);
	        return mapping.findForward("success");
        }
        
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        os.write(fileData);
        os.close();
        
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add(Globals.MESSAGE_KEY, new ActionMessage("upload.success"));
        saveMessages(request, actionMessages);
        
        return mapping.findForward("success");
    }
}