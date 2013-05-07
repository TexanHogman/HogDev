/*
 * Created on Jan 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hogdev.thehoggesweb.actions;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.DynaActionForm;

import com.hogdev.enterprise.Constants;
import com.hogdev.enterprise.beans.Resource;
import com.hogdev.enterprise.utils.ResourceUtil;

/**
 * @author
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SearchAction extends Action
{
	static Logger logger = Logger.getLogger(SearchAction.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{
		String indexDir = (String) System.getProperty(Constants.INDEX_DIR);
		String baseDir = (String) System.getProperty(Constants.BASE_DIR);

		if (indexDir == null || baseDir == null)
		{
			logger.error("indexDir or baseDir is not set context");
			ActionErrors errors = new ActionErrors();
			errors.add("error", new ActionMessage("common.config.error"));
			saveErrors(request, errors);
			return mapping.findForward("home");
		}
		indexDir = new File(indexDir).getCanonicalPath();
		baseDir = new File(baseDir).getCanonicalPath();

		DynaActionForm dForm = (DynaActionForm) form;
		String criteria = (String) dForm.get("criteria");
		ArrayList al = new ArrayList();

		String value = (String) dForm.get("value");
		if (value == null || value.length() == 0)
		{
			ActionErrors actionMessages = new ActionErrors();
			logger.warn("Value name not provided");
			actionMessages.add("file", new ActionMessage("search.valuerequired"));
			saveErrors(request, actionMessages);
		}
		else
		{
			IndexReader reader = IndexReader.open(indexDir);
			Searcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();

			// TODO not sure what happens if I get a unrecognized field
			QueryParser parser = new QueryParser(criteria, analyzer);
			Query query = parser.parse(value);
			Hits hits = searcher.search(query);
			for (int i = 0; i < hits.length(); i++)
			{
				Document doc = hits.doc(i);
				String path = doc.get("path");
				String canPath = new File(path).getCanonicalPath();
				if (!canPath.toLowerCase().startsWith(baseDir.toLowerCase()))
				{
					logger.error("invalid path returned for hit");
					ActionErrors errors = new ActionErrors();
					errors.add("error", new ActionMessage("common.config.error"));
					saveErrors(request, errors);
					return mapping.findForward("home");
				}

				path = canPath.substring(baseDir.length());
				Resource resource = ResourceUtil.buildResource(baseDir, path);
				al.add(resource);
			}
		}
		Resource music = ResourceUtil.buildResource(baseDir, "music");
		music.setCaption("Search Results for: [" + criteria + " = " + value
				+ "]");
		request.setAttribute(Constants.RESOURCE_KEY, music);
		request.setAttribute(Constants.RESOURCE_UP_KEY, music);
		request.setAttribute(Constants.RESOURCE_LIST_KEY, al);

		return mapping.findForward("success");
	}
}
