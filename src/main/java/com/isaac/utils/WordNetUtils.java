package com.isaac.utils;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("unused")
public class WordNetUtils {

    /** Initialize IDictionary */
    private static final IDictionary wndict;
    static {
        URL wnUrl = null;
        try { wnUrl = new URL("file", null, "src/main/resources/wn-dict3.1");
        } catch (IOException e) { e.printStackTrace(); }
        assert wnUrl != null;
        wndict = new Dictionary(wnUrl);
        try { wndict.open(); } catch (IOException e) { e.printStackTrace(); }
    }

    /** @return true if the target word is a verb*/
    public static boolean isVerb (String word) { return wndict.getIndexWord(word, POS.VERB) != null; }

    /** @return true if the target word is a noun */
    public static boolean isNoun (String word) { return wndict.getIndexWord(word, POS.NOUN) != null; }

    /** @return true if the target word is an adjective */
    public static boolean isAdjective (String word) { return wndict.getIndexWord(word, POS.ADJECTIVE) != null; }

    /** @return true if the target word is an adverb */
    public static boolean isAdverb (String word) { return wndict.getIndexWord(word, POS.ADVERB) != null; }


}
