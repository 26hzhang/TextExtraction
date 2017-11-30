package com.isaac.nlp;

import com.isaac.phrases.ChunkedPhrase;
import com.isaac.ling.NERType;
import com.isaac.utils.Common;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OpenNLP Parser performs several semantic tasks, like
 *      1. sentence segmentation,
 *      2. tokenization,
 *      3. Part-of-Speech Tagger
 *      4. Chunking
 *      5. Specific entity detection, like Person, Location, Date, Time, Organization
 *
 * Date: 22/11/2017
 * @author zhanghao
 * Email: isaac.changhau@gmail.com
 */
@SuppressWarnings("unused")
public class OpenNLPParser {
    /** specific entity detector */
    private final NameFinderME personDetector;
    private final NameFinderME locationDetector;
    private final NameFinderME dateDetector;
    private final NameFinderME timeDetector;
    private final NameFinderME organizationDetector;
    /** Tokenizer */
    private final TokenizerME tokenizer;
    /** POS Tagger */
    private final POSTaggerME posTagger;
    /** Chunker */
    private final ChunkerME chunker;

    /** Constructor */
    public OpenNLPParser () {
        personDetector = Common.initializeOpenNLPPersonDetector();
        locationDetector = Common.initializeOpenNLPLocationDetector();
        dateDetector = Common.initializeOpenNLPDateDetector();
        timeDetector = Common.initializeOpenNLPTimeDetector();
        organizationDetector = Common.initializeOpenNLPOrganizationDetector();
        tokenizer = Common.initializeOpenNLPTokenizer();
        posTagger = Common.initializeOpenNLPPOSTagger();
        chunker = Common.initializeOpenNLPChunker();
    }

    /** Sentence Segmentation */
    public List<String> sentenceTokenize(String paragraph) { return Common.sentenceTokenize(paragraph); }

    /** Word Level Tokenization */
    public List<String> tokenize (String sentence) { return Arrays.asList(tokenizer.tokenize(sentence)); }

    /** Part of Speech Tagger */
    public List<String> tag (String sentence) { return Arrays.asList(posTagger.tag(tokenizer.tokenize(sentence))); }

    /** Chunker */
    public String[] chunk (String sentence) {
        String[] tokens = tokenizer.tokenize(sentence);
        return chunker.chunk(tokens, posTagger.tag(tokens));
    }

    /** Convert chunked results to specific phrases */
    public List<ChunkedPhrase> chunkedPhrases (String sentence) {
        String[] tokens = tokenizer.tokenize(sentence);
        String[] chunks = chunker.chunk(tokens, posTagger.tag(tokens));
        List<ChunkedPhrase> phrases = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        String type = "";
        for (int i = 0; i < chunks.length; i++) { // merge each chunked phrase
            if (chunks[i].startsWith("I")) tmp.add(tokens[i]);
            if (chunks[i].startsWith("B")) {
                if (!tmp.isEmpty()) {
                    phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
                    tmp.clear();
                }
                tmp.add(tokens[i]);
                type = chunks[i].substring(2);
            }
            if (chunks[i].length() == 1) { // "0"
                if (!tmp.isEmpty()) {
                    phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
                    tmp.clear();
                }
                tmp.add(tokens[i]);
                type = chunks[i];
            }
            if (i == chunks.length - 1) phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
        }
        return phrases;
    }

    /** @return {@link List} of specific entities */
    public List<String> findPerson (String sentence) { return findSpecialEntity(sentence, NERType.PERSON); }
    public List<String> findLocation (String sentence) { return findSpecialEntity(sentence, NERType.LOCATION); }
    public List<String> findDate (String sentence) { return findSpecialEntity(sentence, NERType.DATE); }
    public List<String> findTime (String sentence) { return findSpecialEntity(sentence, NERType.TIME); }
    public List<String> findOrganization (String sentence) { return findSpecialEntity(sentence, NERType.ORGANIZATION); }
    private List<String> findSpecialEntity (String sentence, NERType type) {
        Span[] spans;
        String[] tokens = tokenizer.tokenize(sentence);
        switch (type) {
            case TIME: spans = timeDetector.find(tokens); break;
            case DATE: spans = dateDetector.find(tokens); break;
            case PERSON: spans = personDetector.find(tokens); break;
            case LOCATION: spans = locationDetector.find(tokens); break;
            case ORGANIZATION: spans = organizationDetector.find(tokens); break;
            default: throw new UnsupportedOperationException("Not Implemented...");
        }
        return Arrays.asList(Span.spansToStrings(spans, tokens));
    }

    /** Getters */
    public NameFinderME getPersonDetector() { return personDetector; }
    public NameFinderME getLocationDetector() { return locationDetector; }
    public NameFinderME getDateDetector() { return dateDetector; }
    public NameFinderME getTimeDetector() { return timeDetector; }
    public NameFinderME getOrganizationDetector() { return organizationDetector; }
    public TokenizerME getTokenizer() { return tokenizer; }
    public POSTaggerME getPosTagger() { return posTagger; }
    public ChunkerME getChunker() { return chunker; }
}
