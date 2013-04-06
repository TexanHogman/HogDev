package com.hogdev.util.search;

import gnu.getopt.Getopt;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

public class MP3Indexer
{
    String indexDir = "index";

    String dataDir = ".";
    boolean verbose = false;

    public boolean isVerbose()
    {
	return verbose;
    }

    public void setVerbose(boolean verbose)
    {
	this.verbose = verbose;
    }

    public String getDataDir()
    {
	return dataDir;
    }

    public void setDataDir(String dataDir)
    {
	this.dataDir = dataDir;
    }

    public String getIndexDir()
    {
	return indexDir;
    }

    public void setIndexDir(String indexDir)
    {
	this.indexDir = indexDir;
    }

    public static void main(String args[])
    {
	Getopt g = new Getopt("MP3Indexer", args, "d:i:v");
	g.setOpterr(true);
	MP3Indexer indexer = new MP3Indexer();
	//
	int c;
	while ((c = g.getopt()) != -1)
	{
	    switch (c)
	    {
	    case 'd':
		indexer.setDataDir(g.getOptarg());
		break;
	    case 'i':
		indexer.setIndexDir(g.getOptarg());
		break;
	    case 'v':
		indexer.setVerbose(true);
		break;
	    default:
		System.out.print("getopt() returned " + c + "\n");
	    }
	}
	indexer.index();
    }

    public void index()
    {
	File index = new File(indexDir);
	File data = new File(dataDir);

	if (!data.exists() || !data.canRead())
	{
	    if (verbose)
		System.out.println("Document directory '" + data.getAbsolutePath()
			+ "' does not exist or is not readable, please check the path");
	    return;
	}
	Date start = new Date();
	try
	{
	    IndexWriter writer = new IndexWriter(index, new StandardAnalyzer(), true);
	    if (verbose)
		System.out.println("Indexing to directory '" + index + "'...");
	    indexDocs(writer, data);
	    if (verbose)
		System.out.println("Optimizing...");
	    writer.optimize();
	    writer.close();
	    Date end = new Date();
	    if (verbose)
		System.out.println((end.getTime() - start.getTime()) + " total milliseconds");
	}
	catch (IOException e)
	{
	    if (verbose)
		System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
	}
    }

    private void indexDocs(IndexWriter writer, File file) throws IOException
    {
	if (file.canRead())
	    if (file.isDirectory())
	    {
		File files[] = file.listFiles(new FileFilter()
		{
		    public boolean accept(File file)
		    {
			if (file.isHidden())
			    return false;
			else if (file.isDirectory() || file.getName().toLowerCase().endsWith(".mp3"))
			    return true;
			else
			    return false;
		    }
		});
		if (files != null && files.length > 0)
		{
		    for (int i = 0; i < files.length; i++)
			indexDocs(writer, files[i]);

		}
	    }
	    else
	    {
		if (verbose)
		    System.out.println("adding " + file);
		try
		{
		    writer.addDocument(MP3Document.Document(file));
		}
		catch (FileNotFoundException fnfe)
		{
		}
	    }
    }

}