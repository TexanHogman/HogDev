/*
 * Created on Oct 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.taglibs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.config.ModuleConfig;

import com.hogdev.enterprise.Constants;


/**
 * @author rhogge
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class CheckLogonTag extends TagSupport
{
	/**
     * 
     */
    private static final long serialVersionUID = -2525981288791257249L;

    /**
	 * The key of the session-scope bean we look for.
	 */
	private String name = Constants.USER_KEY;

	/**
	 * The page to which we should forward for the user to log on.
	 */
	private String page = "/logon.jsp";

	/**
	 * Defer our checking until the end of this tag is encountered.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doStartTag() throws JspException
	{
		return (SKIP_BODY);
	}

	/**
	 * Perform our logged-in user check by looking for the existence of
	 * a session scope bean under the specified name.  If this bean is not
	 * present, control is forwarded to the specified logon page.
	 *
	 * @exception JspException if a JSP exception has occurred
	 */
	public int doEndTag() throws JspException
	{
		// Is there a valid user logged on?
		boolean valid = false;
		HttpSession session = pageContext.getSession();
		if ((session != null) && (session.getAttribute(name) != null))
		{
			valid = true;
		}
		// Forward control based on the results
		if (valid)
		{
			return (EVAL_PAGE);
		}
		else
		{
			ModuleConfig config =
				(ModuleConfig)pageContext.getServletContext().getAttribute(
					org.apache.struts.Globals.MODULE_KEY);
			try
			{
				pageContext.forward(config.getPrefix() + page);
			}
			catch (ServletException e)
			{
				throw new JspException(e.toString());
			}
			catch (IOException e)
			{
				throw new JspException(e.toString());
			}
			return (SKIP_PAGE);
		}
	}
	
	/**
	 * Release any acquired resources.
	 */
	public void release()
	{
		super.release();
		this.name = Constants.USER_KEY;
		this.page = "/logon.jsp";
	}
	
	/**
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return
	 */
	public String getPage()
	{
		return page;
	}

	/**
	 * @param string
	 */
	public void setName(String string)
	{
		name = string;
	}

	/**
	 * @param string
	 */
	public void setPage(String string)
	{
		page = string;
	}

}