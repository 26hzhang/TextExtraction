package com.isaac.utils;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Common {

    private static final String directory = "src/main/resources/open-nlp-models/";
    private static InputStream modelIn;
    private static final SentenceDetectorME sentenceTokenizer = initializeOpenNLPSentenceDetector();

    public static List<String> sentenceTokenize (String paragraph) {
        return Arrays.asList(sentenceTokenizer.sentDetect(paragraph));
    }

    /** @return {@link List}, convert Scala {@link Seq} to Java {@link List} */
    public static <T> List<T> seq2List (Seq<T> sequence) { return JavaConversions.seqAsJavaList(sequence); }

    /** @return word level {@link TokenizerME} of OpenNLP */
    public static TokenizerME initializeOpenNLPTokenizer () {
        TokenizerModel model = null;
        try {
            modelIn = new FileInputStream(directory.concat("en-token.bin"));
            model = new TokenizerModel(modelIn);
            modelIn.close();
        } catch (IOException e) { e.printStackTrace(); }
        assert model != null;
        return new TokenizerME(model);
    }

    /** @return {@link ChunkerME} of OpenNLP */
    public static ChunkerME initializeOpenNLPChunker () {
        ChunkerModel model =null;
        try {
            modelIn = new FileInputStream(directory.concat("en-chunker.bin"));
            model = new ChunkerModel(modelIn);
            modelIn.close();
        } catch (IOException e) { e.printStackTrace(); }
        assert model != null;
        return new ChunkerME(model);
    }

    /** @return {@link POSTaggerME} of OpenNLP */
    public static POSTaggerME initializeOpenNLPPOSTagger () {
        POSModel model = null;
        try {
            modelIn = new FileInputStream(directory.concat("en-pos-maxent.bin"));
            model = new POSModel(modelIn);
            modelIn.close();
        } catch (IOException e) { e.printStackTrace(); }
        assert model != null;
        return new POSTaggerME(model);
    }

    /** @return sentence level tokenizer, {@link SentenceDetectorME} */
    private static SentenceDetectorME initializeOpenNLPSentenceDetector () {
        SentenceModel model = null;
        try {
            modelIn = new FileInputStream(directory.concat("en-sent.bin"));
            model = new SentenceModel(modelIn);
            modelIn.close();
        } catch (IOException e) { e.printStackTrace(); }
        assert model != null;
        return new SentenceDetectorME(model);
    }

    /** @return person entity detector, {@link NameFinderME} */
    public static NameFinderME initializeOpenNLPPersonDetector () { return initializeOpenNLPDetector("en-ner-person.bin"); }

    /** @return location entity detector, {@link NameFinderME} */
    public static NameFinderME initializeOpenNLPLocationDetector () { return initializeOpenNLPDetector("en-ner-location.bin"); }

    /** @return date entity detector, {@link NameFinderME} */
    public static NameFinderME initializeOpenNLPDateDetector () { return initializeOpenNLPDetector("en-ner-date.bin"); }

    /** @return time entity detector, {@link NameFinderME} */
    public static NameFinderME initializeOpenNLPTimeDetector () { return initializeOpenNLPDetector("en-ner-time.bin"); }

    /** @return organization entity detector, {@link NameFinderME} */
    public static NameFinderME initializeOpenNLPOrganizationDetector () { return initializeOpenNLPDetector("en-ner-organization.bin"); }

    /** @return name entity detector */
    private static NameFinderME initializeOpenNLPDetector (String modelName) {
        TokenNameFinderModel model = null;
        try {
            modelIn = new FileInputStream(directory.concat(modelName));
            model = new TokenNameFinderModel(modelIn);
            modelIn.close();
        } catch (IOException e) { e.printStackTrace(); }
        assert model != null;
        return new NameFinderME(model);
    }

}
