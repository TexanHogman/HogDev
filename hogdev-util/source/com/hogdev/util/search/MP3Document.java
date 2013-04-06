package com.hogdev.util.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

public class MP3Document
{

	public static Document Document(File f) throws FileNotFoundException
	{

		Document doc = new Document();
		doc.add(new Field("path", f.getPath(),
				org.apache.lucene.document.Field.Store.YES,
				org.apache.lucene.document.Field.Index.UN_TOKENIZED));
		doc.add(new Field("modified", DateTools.timeToString(f.lastModified(),
				org.apache.lucene.document.DateTools.Resolution.MINUTE),
				org.apache.lucene.document.Field.Store.YES,
				org.apache.lucene.document.Field.Index.UN_TOKENIZED));
		try
		{
			MP3File mp3 = new MP3File(f);
			String title = mp3.getID3v2Tag().getSongTitle();
			doc.add(new Field("title", title,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));

			String artist = mp3.getID3v2Tag().getLeadArtist();
			doc.add(new Field("artist", artist,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));

			String album = mp3.getID3v2Tag().getAlbumTitle();
			doc.add(new Field("album", album,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));

			String genre = mp3.getID3v2Tag().getSongGenre();
			doc.add(new Field("genre", genre,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));

			String yr = mp3.getID3v2Tag().getYearReleased();
			doc.add(new Field("year", yr,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));

			String track = mp3.getID3v2Tag().getTrackNumberOnAlbum();
			doc.add(new Field("track", track,
					org.apache.lucene.document.Field.Store.YES,
					org.apache.lucene.document.Field.Index.TOKENIZED));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (TagException e)
		{
			e.printStackTrace();
		}
		return doc;
	}

	private MP3Document()
	{
	}
}