package com.isaac.utils;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.data.parse.SenseKeyParser;
import edu.mit.jwi.item.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("all")
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

    /** @return {@link ISynset} {@link Iterator} for the synsets with specific part of speech tag in wordnet */
    public static Iterator<ISynset> getSynsetIterator (POS tag) { return wndict.getSynsetIterator(tag); }

    /** @return true if the target word is a verb*/
    public static boolean isVerb (String word) { return wndict.getIndexWord(word, POS.VERB) != null; }

    /** @return true if the target word is a noun */
    public static boolean isNoun (String word) { return wndict.getIndexWord(word, POS.NOUN) != null; }

    /** @return true if the target word is an adjective */
    public static boolean isAdjective (String word) { return wndict.getIndexWord(word, POS.ADJECTIVE) != null; }

    /** @return true if the target word is an adverb */
    public static boolean isAdverb (String word) { return wndict.getIndexWord(word, POS.ADVERB) != null; }

    /** @return {@link ISenseKey} for a given senseKeyString, null if the format is incorrect */
    public static ISenseKey parseSenseKeyString (String senseKeyString) {
        return senseKeyPattern(senseKeyString) ? SenseKeyParser.getInstance().parseLine(senseKeyString) : null;
    }

    /** @return {@link ISynsetID} for a given synset id string, null if the format is incorrect */
    public static ISynsetID parseSynsetIDString (String synsetIdString) {
        return synsetIdPattern(synsetIdString) ? SynsetID.parseSynsetID(synsetIdString) : null;
    }

    /** @return {@link IWordID} for a given word id string, null if the format is incorrect */
    public static IWordID parseIWordIDString (String wordIdString) {
        return wordIdPattern(wordIdString) ? WordID.parseWordID(wordIdString) : null;
    }

    /** @return total synsets in wordnet */
    public static Integer countAllSynsets() {
        Integer count = 0;
        for (POS type : Arrays.asList(POS.NOUN, POS.VERB, POS.ADJECTIVE, POS.ADVERB)) count += countSynsetsByPOSTag(type);
        return count;
    }

    /** @return total synsets of specific POS type in wordnet */
    public static Integer countSynsetsByPOSTag(POS type) {
        Iterator<ISynset> iter = getSynsetIterator(type);
        Integer count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        return count;
    }

    /** @return {@link IWord} for a given {@link ISenseKey} */
    public static IWord getWordBySenseKey (ISenseKey senseKey) { return wndict.getWord(senseKey); }

    /** @return {@link IWord} for a given ISenseKey string */
    public static IWord getWordBySenseKeyStr (String senseKeyStr) {
        ISenseKey key = parseSenseKeyString(senseKeyStr);
        if (key == null) return null;
        return getWordBySenseKey(key);
    }

    /** @return lexical domain string for a given {@link ISenseKey} */
    public static String getLexicalFileBySenseKey(ISenseKey senseKey) { return senseKey.getLexicalFile().toString(); }

    /** @return {@link IWord} for a given {@link IWordID} */
    public static IWord getWordByWordId (IWordID wordID) { return wndict.getWord(wordID); }

    /** @return {@link IIndexWord} for a given word string and its part of speech tag */
    public static IIndexWord getIndexWord (String word, POS tag) { return wndict.getIndexWord(word, tag); }

    /** @return {@link ISynset} for a given {@link ISynsetID} */
    public static ISynset getSynsetBySynsetId (ISynsetID synsetID) { return wndict.getSynset(synsetID); }


    /** @return tag count for a given {@link IWord} */
    public static int getTagCount4IWord (IWord iWord) {
        if (wndict.getSenseEntry(iWord.getSenseKey()) != null) return wndict.getSenseEntry(iWord.getSenseKey()).getTagCount();
        else return 0;
    }

    /** @return sense number for a given {@link IWord} */
    public static int getSenseNumber4IWord (IWord iWord) {
        if (wndict.getSenseEntry(iWord.getSenseKey()) != null) return wndict.getSenseEntry(iWord.getSenseKey()).getSenseNumber();
        else return 0;
    }

    /** @return offset for a given {@link IWord} */
    public static int getOffset4IWord (IWord iWord) { return wndict.getSenseEntry(iWord.getSenseKey()).getOffset(); }

    /**
     * Detect whether the given synset id string fits the correct format
     * @param synsetId given synset id
     * @return true if the given synset id string in correct format, otherwise false
     */
    public static boolean synsetIdPattern (String synsetId) {
        Pattern pattern = Pattern.compile("(\\w{3})[-](\\d{8})[-](\\w)"); // e.g.: SID-02081903-V
        return pattern.matcher(synsetId).matches();
    }

    /**
     * Detect whether the given word id string fits the correct format
     *      e.g.: detect word id like: WID-01535377-V-02-make_clean
     * @param wordId given word id
     * @return true if the given word is string in correct format, otherwise false
     */
    public static boolean wordIdPattern (String wordId) {
        Pattern pattern = Pattern.compile("(\\w{3})[-](\\d{8})[-](\\w)[-](\\d{2})[-][_\\w]+");
        return pattern.matcher(wordId).matches();
    }

    /**
     * Detect whether the given sense key string fit the correct format
     *      e.g.: detect the sense key like: time%1:28:03::, make_clean%2:35:00::
     * @param senseKey given sense key string
     * @return true if the given sense key string in correct format, otherwise false
     */
    public static boolean senseKeyPattern (String senseKey) { // may have some problems here, I'm not sure whether it contains all the format of Sense Key
        Pattern pattern1 = Pattern.compile("[-_\\w]+[%](\\d)[:](\\d){2}[:](\\d){2}(:{2})"); // e.g.: time%1:28:03::, make_clean%2:35:00::, t-shirt%1:06:00::
        if (pattern1.matcher(senseKey).matches()) return true;
        Pattern pattern2 = Pattern.compile("[-_\\w]+[%](\\d)[:](\\d){2}[:](\\d){2}[:][-_\\w]+[:](\\d){2}"); // e.g.: amber%5:00:00:chromatic:00
        return pattern2.matcher(senseKey).matches();
    }

    /**
     * Compute the number of sense for a given word with specific POS
     * @param word given word string
     * @param tag part of speech tag
     * @return number of sense for the given word with specific POS
     */
    public static int getNumberOfSense4Word (String word, POS tag) { return wndict.getIndexWord(word, tag).getWordIDs().size(); }

    /**
     * Compute the sum of tag count for a given word with specific POS
     * @param word given word string
     * @param tag part of speech tag
     * @return sum of tag count for the given word with specific POS
     */
    public static int getSumOfTagCount4Word (String word, POS tag) {
        return wndict.getIndexWord(word, tag).getWordIDs().stream()
                .map(id -> wndict.getSenseEntry(wndict.getWord(id).getSenseKey()).getTagCount())
                .mapToInt(Integer::intValue).sum();
    }

    /**
     * Convert the given synset to string
     * @param synset the given {@link ISynset}
     * @param includeSynsetId contains synset id or not
     * @return synset string
     */
    public static String synset2String (ISynset synset, boolean includeSynsetId) {
        String str = includeSynsetId ? synset.getID().toString().concat("----{") : "{";
        return str.concat(String.join(", ", synset.getWords().stream().map(IWord::getLemma).collect(Collectors.toList())))
                .concat("}");
    }


}
