package com.hogdev.cddb;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rick
 * Date: Jul 13, 2004
 * Time: 11:20:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class Genre {
    static Hashtable ht1 = new Hashtable();
    static Hashtable ht2 = new Hashtable();

    static {
        ht1.put(new Byte((byte) 0), "Blues");
        ht1.put(new Byte((byte) 1), "Classic Rock");
        ht1.put(new Byte((byte) 2), "Country");
        ht1.put(new Byte((byte) 3), "Dance");
        ht1.put(new Byte((byte) 4), "Disco");
        ht1.put(new Byte((byte) 5), "Funk");
        ht1.put(new Byte((byte) 6), "Grunge");
        ht1.put(new Byte((byte) 7), "Hip-Hop");
        ht1.put(new Byte((byte) 8), "Jazz");
        ht1.put(new Byte((byte) 9), "Metal");
        ht1.put(new Byte((byte) 10), "New Age");
        ht1.put(new Byte((byte) 11), "Oldies");
        ht1.put(new Byte((byte) 12), "Other");
        ht1.put(new Byte((byte) 13), "Pop");
        ht1.put(new Byte((byte) 14), "R&B");
        ht1.put(new Byte((byte) 15), "Rap");
        ht1.put(new Byte((byte) 16), "Reggae");
        ht1.put(new Byte((byte) 17), "Rock");
        ht1.put(new Byte((byte) 18), "Techno");
        ht1.put(new Byte((byte) 19), "Industrial");
        ht1.put(new Byte((byte) 20), "Alternative");
        ht1.put(new Byte((byte) 21), "Ska");
        ht1.put(new Byte((byte) 22), "Death Metal");
        ht1.put(new Byte((byte) 23), "Pranks");
        ht1.put(new Byte((byte) 24), "Soundtrack");
        ht1.put(new Byte((byte) 25), "Euro-Techno");
        ht1.put(new Byte((byte) 26), "Ambient");
        ht1.put(new Byte((byte) 27), "Trip-Hop");
        ht1.put(new Byte((byte) 28), "Vocal");
        ht1.put(new Byte((byte) 29), "Jazz+Funk");
        ht1.put(new Byte((byte) 30), "Fusion");
        ht1.put(new Byte((byte) 31), "Trance");
        ht1.put(new Byte((byte) 32), "Classical");
        ht1.put(new Byte((byte) 33), "Instrumental");
        ht1.put(new Byte((byte) 34), "Acid");
        ht1.put(new Byte((byte) 35), "House");
        ht1.put(new Byte((byte) 36), "Game");
        ht1.put(new Byte((byte) 37), "Sound Clip");
        ht1.put(new Byte((byte) 38), "Gospel");
        ht1.put(new Byte((byte) 39), "Noise");
        ht1.put(new Byte((byte) 40), "Alternative Rock");
        ht1.put(new Byte((byte) 41), "Bass");
        ht1.put(new Byte((byte) 42), "Soul");
        ht1.put(new Byte((byte) 43), "Punk");
        ht1.put(new Byte((byte) 44), "Space");
        ht1.put(new Byte((byte) 45), "Meditative");
        ht1.put(new Byte((byte) 46), "Instrumental Pop");
        ht1.put(new Byte((byte) 47), "Instrumental Rock");
        ht1.put(new Byte((byte) 48), "Ethnic");
        ht1.put(new Byte((byte) 49), "Gothic");
        ht1.put(new Byte((byte) 50), "Darkwave");
        ht1.put(new Byte((byte) 51), "Techno-Industrial");
        ht1.put(new Byte((byte) 52), "Electronic");
        ht1.put(new Byte((byte) 53), "Pop-Folk");
        ht1.put(new Byte((byte) 54), "Eurodance");
        ht1.put(new Byte((byte) 55), "Dream");
        ht1.put(new Byte((byte) 56), "Southern Rock");
        ht1.put(new Byte((byte) 57), "Comedy");
        ht1.put(new Byte((byte) 58), "Cult");
        ht1.put(new Byte((byte) 59), "Gangsta");
        ht1.put(new Byte((byte) 60), "Top 40");
        ht1.put(new Byte((byte) 61), "Christian Rap");
        ht1.put(new Byte((byte) 62), "Pop/Funk");
        ht1.put(new Byte((byte) 63), "Jungle");
        ht1.put(new Byte((byte) 64), "Native US");
        ht1.put(new Byte((byte) 65), "Cabaret");
        ht1.put(new Byte((byte) 66), "New Wave");
        ht1.put(new Byte((byte) 67), "Psychadelic");
        ht1.put(new Byte((byte) 68), "Rave");
        ht1.put(new Byte((byte) 69), "Showtunes");
        ht1.put(new Byte((byte) 70), "Trailer");
        ht1.put(new Byte((byte) 71), "Lo-Fi");
        ht1.put(new Byte((byte) 72), "Tribal");
        ht1.put(new Byte((byte) 73), "Acid Punk");
        ht1.put(new Byte((byte) 74), "Acid Jazz");
        ht1.put(new Byte((byte) 75), "Polka");
        ht1.put(new Byte((byte) 76), "Retro");
        ht1.put(new Byte((byte) 77), "Musical");
        ht1.put(new Byte((byte) 78), "Rock & Roll");
        ht1.put(new Byte((byte) 79), "Hard Rock");
        ht1.put(new Byte((byte) 80), "Folk");
        ht1.put(new Byte((byte) 81), "Folk-Rock");
        ht1.put(new Byte((byte) 82), "National Folk");
        ht1.put(new Byte((byte) 83), "Swing");
        ht1.put(new Byte((byte) 84), "Fast Fusion");
        ht1.put(new Byte((byte) 85), "Bebob");
        ht1.put(new Byte((byte) 86), "Latin");
        ht1.put(new Byte((byte) 87), "Revival");
        ht1.put(new Byte((byte) 88), "Celtic");
        ht1.put(new Byte((byte) 89), "Bluegrass");
        ht1.put(new Byte((byte) 90), "Avantgarde");
        ht1.put(new Byte((byte) 91), "Gothic Rock");
        ht1.put(new Byte((byte) 92), "Progressive Rock");
        ht1.put(new Byte((byte) 93), "Psychedelic Rock");
        ht1.put(new Byte((byte) 94), "Symphonic Rock");
        ht1.put(new Byte((byte) 95), "Slow Rock");
        ht1.put(new Byte((byte) 96), "Big Band");
        ht1.put(new Byte((byte) 97), "Chorus");
        ht1.put(new Byte((byte) 98), "Easy Listening");
        ht1.put(new Byte((byte) 99), "Acoustic");
        ht1.put(new Byte((byte) 100), "Humour");
        ht1.put(new Byte((byte) 101), "Speech");
        ht1.put(new Byte((byte) 102), "Chanson");
        ht1.put(new Byte((byte) 103), "Opera");
        ht1.put(new Byte((byte) 104), "Chamber Music");
        ht1.put(new Byte((byte) 105), "Sonata");
        ht1.put(new Byte((byte) 106), "Symphony");
        ht1.put(new Byte((byte) 107), "Booty Bass");
        ht1.put(new Byte((byte) 108), "Primus");
        ht1.put(new Byte((byte) 109), "Porn Groove");
        ht1.put(new Byte((byte) 110), "Satire");
        ht1.put(new Byte((byte) 111), "Slow Jam");
        ht1.put(new Byte((byte) 112), "Club");
        ht1.put(new Byte((byte) 113), "Tango");
        ht1.put(new Byte((byte) 114), "Samba");
        ht1.put(new Byte((byte) 115), "Folklore");
        ht1.put(new Byte((byte) 116), "Ballad");
        ht1.put(new Byte((byte) 117), "Power Ballad");
        ht1.put(new Byte((byte) 118), "Rhytmic Soul");
        ht1.put(new Byte((byte) 119), "Freestyle");
        ht1.put(new Byte((byte) 120), "Duet");
        ht1.put(new Byte((byte) 121), "Punk Rock");
        ht1.put(new Byte((byte) 122), "Drum Solo");
        ht1.put(new Byte((byte) 123), "Acapella");
        ht1.put(new Byte((byte) 124), "Euro-House");
        ht1.put(new Byte((byte) 125), "Dance Hall");
        ht1.put(new Byte((byte) 126), "Goa");
        ht1.put(new Byte((byte) 127), "Drum & Bass");
        ht1.put(new Byte((byte) 128), "Club-House");
        ht1.put(new Byte((byte) 129), "Hardcore");
        ht1.put(new Byte((byte) 130), "Terror");
        ht1.put(new Byte((byte) 131), "Indie");
        ht1.put(new Byte((byte) 132), "BritPop");
        ht1.put(new Byte((byte) 133), "Negerpunk");
        ht1.put(new Byte((byte) 134), "Polsk Punk");
        ht1.put(new Byte((byte) 135), "Beat");
        ht1.put(new Byte((byte) 136), "Christian Gangsta Rap");
        ht1.put(new Byte((byte) 137), "Heavy Metal");
        ht1.put(new Byte((byte) 138), "Black Metal");
        ht1.put(new Byte((byte) 139), "Crossover");
        ht1.put(new Byte((byte) 140), "Contemporary Christian");
        ht1.put(new Byte((byte) 141), "Christian Rock");
        ht1.put(new Byte((byte) 142), "Merengue");
        ht1.put(new Byte((byte) 143), "Salsa");
        ht1.put(new Byte((byte) 144), "Trash Metal");
        ht1.put(new Byte((byte) 145), "Anime");
        ht1.put(new Byte((byte) 146), "Jpop");
        ht1.put(new Byte((byte) 147), "Synthpop");

        Set keys = ht1.keySet();
        Iterator iter = keys.iterator();
        while(iter.hasNext())
        {
            Byte key = (Byte)iter.next();
            String value = (String)ht1.get(key);
            ht2.put(value, key);
        }
    }

    public static byte getCode(String desc)
    {
        Byte code = (Byte)ht2.get(desc);
        if(code == null)
            code = new Byte((byte)12);
        return code.byteValue();
    }
    
    public static String getDesc(byte code)
    {
        String str = (String)ht1.get(new Byte(code));
        if(str == null)
            str = "Other";
        return str;
    }
}
