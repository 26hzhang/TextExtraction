package com.isaac.nlp;

import com.isaac.ling.NERType;
import com.isaac.phrases.POSTagPhrase;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("unused")
public class CoreNLPParser {
    /** Models directory */
    private final String directory = "edu/stanford/nlp/models/";

    private StanfordCoreNLP pipeline;
    private AbstractSequenceClassifier<CoreLabel> nerDetector;

    public CoreNLPParser () {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        // english dependencies: english_SD.gz, universal dependencies: english_UD.gz
        props.setProperty("depparse.model", directory.concat("parser/nndep/english_UD.gz"));
        this.pipeline = new StanfordCoreNLP(props);
        String nerModelPath = directory.concat("ner/english.muc.7class.distsim.crf.ser.gz");
        this.nerDetector = CRFClassifier.getClassifierNoExceptions(nerModelPath);
    }

    /** @return {@link List} of {@link CoreMap}, each {@link CoreMap} is a tokenized sentence from paragraph/text */
    private List<CoreMap> sentenceAnnotate (String sentences) {
        Annotation annotation = new Annotation(sentences);
        pipeline.annotate(annotation);
        return annotation.get(CoreAnnotations.SentencesAnnotation.class);
    }

    /** @return {@link List} of tokenized sentences from paragraph/text */
    public List<String> sentenceTokenize (String sentences) {
        return sentenceAnnotate(sentences).stream().map(CoreMap::toString).collect(Collectors.toList());
    }

    /** @return {@link List} of {@link CoreLabel}, each {@link CoreLabel} is a tokenized word from sentence */
    public List<CoreLabel> wordTokenize (String sentence) {
        return sentenceAnnotate(sentence).get(0).get(CoreAnnotations.TokensAnnotation.class);
    }

    /** @return {@link List} of tokenized words from sentence */
    public List<String> tokenizer (String sentence) {
        return wordTokenize(sentence).stream().map(CoreLabel::word).collect(Collectors.toList());
    }

    /** @return {@link List} of lemmatized words from sentence */
    public List<String> lemmaTokenizer (String sentence) {
        return wordTokenize(sentence).stream().map(CoreLabel::lemma).collect(Collectors.toList());
    }

    /** @return {@link List} of {@link POSTagPhrase} */
    public List<POSTagPhrase> tag (String sentence) {
        return wordTokenize(sentence).stream() // get word tokens
                    .map(t -> new POSTagPhrase(t.word(), t.get(CoreAnnotations.PartOfSpeechAnnotation.class))) // get pos tags
                    .collect(Collectors.toList()); // convert to list
    }

    /** @return tagged sentence */
    public String tags2String (String sentence) {
        return String.join(" ", tag(sentence).stream().map(POSTagPhrase::toString).collect(Collectors.toList()));
    }

    /** @return {@link List} of {@link Triple} contains dependency relations */
    public List<Triple<String, String, String>> dependencyParse2Triple (String sentence) {
        return StreamSupport.stream(dependencyParse(sentence).edgeIterable().spliterator(), false)
                .map(edge -> Triple.makeTriple(edge.getSource().toString(), edge.getTarget().toString(),
                        edge.getRelation().toString()))
                .collect(Collectors.toList());
    }

    /** @return {@link SemanticGraph} of a sentence */
    public SemanticGraph dependencyParse (String sentence) {
        // SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class -- basic
        // SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class -- enhanced++
        return sentenceAnnotate(sentence).get(0).get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class);
    }

    /** @return semantic graph string */
    public String dependencyParse2DotFormat (String sentence) { return dependencyParse(sentence).toDotFormat(); }
    public String dependencyParse2String (String sentence) { return dependencyParse(sentence).toString(); }

    /** @return {@link List} of specific entities */
    public List<String> findPerson (String sentence) { return findSpecialEntity(sentence, NERType.PERSON); }
    public List<String> findLocation (String sentence) { return findSpecialEntity(sentence, NERType.LOCATION); }
    public List<String> findOrganization (String sentence) { return findSpecialEntity(sentence, NERType.ORGANIZATION); }
    public List<String> findMoney (String sentence) { return findSpecialEntity(sentence, NERType.MONEY); }
    public List<String> findPercent (String sentence) { return findSpecialEntity(sentence, NERType.PERCENT); }
    public List<String> findTime (String sentence) { return findSpecialEntity(sentence, NERType.TIME); }
    public List<String> findDate (String sentence) { return findSpecialEntity(sentence, NERType.DATE); }
    private List<String> findSpecialEntity (String sentence, NERType type) {
        Pattern pattern;
        switch (type) {
            case TIME: pattern = Pattern.compile("<TIME>(.+?)</TIME>"); break;
            case DATE: pattern = Pattern.compile("<DATE>(.+?)</DATE>"); break;
            case PERSON: pattern = Pattern.compile("<PERSON>(.+?)</PERSON>"); break;
            case LOCATION: pattern = Pattern.compile("<LOCATION>(.+?)</LOCATION>"); break;
            case ORGANIZATION: pattern = Pattern.compile("<ORGANIZATION>(.+?)</ORGANIZATION>"); break;
            case MONEY: pattern = Pattern.compile("<MONEY>(.+?)</MONEY>"); break;
            case PERCENT: pattern = Pattern.compile("<PERCENT>(.+?)</PERCENT>"); break;
            default: throw new UnsupportedOperationException("Not Implemented");
        }
        String markedSent = detectNERInlineXML(sentence);
        Matcher m = pattern.matcher(markedSent);
        List<String> entities = new ArrayList<>();
        while (m.find()) entities.add(m.group(1));
        return entities;
    }

    /** @return a string which marked the specific entity inline */
    public String detectNERInlineXML(String sentence) { return nerDetector.classifyWithInlineXML(sentence); }

    /** @return a string which detected the specific entity in different format */
    public String detectNER2String (String sentence) { return nerDetector.classifyToString(sentence); }
    public String detectNER2XMLString (String sentence) {
        return nerDetector.classifyToString(sentence,
                "xml", true);
    }
    public String detectNER2TSVString (String sentence) {
        return nerDetector.classifyToString(sentence,
                "tsv", false);
    }
    public String detectNER2TabledEntity (String sentence) { return nerDetector.classifyToString(sentence,
            "tabbedEntities", false); }
    public String detectNER2SlashTags (String sentence) { return nerDetector.classifyToString(sentence,
            "slashTags", false); }

    /** @return {@link List} of {@link Triple}, detect specific entities */
    public List<Triple<String, Integer, Integer>> detectNER2CharacterOffsets (String sentence) {
        return nerDetector.classifyToCharacterOffsets(sentence);
    }

    /** detect specific entity to core label lists */
    public List<List<CoreLabel>> detectNER2CoreLabel (String sentence) { return nerDetector.classify(sentence); }

    /** Getter and Setter */
    public StanfordCoreNLP getPipeline () { return pipeline; }
    public void setPipeline (StanfordCoreNLP pipeline) { this.pipeline = pipeline; }
    public void setPipeline (Properties props) { this.pipeline = new StanfordCoreNLP(props); }
    public void setNerDetector (String modelType) {
        String modelName;
        switch (modelType) {
            case "all": modelName = "ner/english.all.3class.distsim.crf.ser.gz"; break;
            case "conll": modelName = "ner/english.conll.4class.distsim.crf.ser.gz"; break;
            case "muc": modelName = "ner/english.muc.7class.distsim.crf.ser.gz"; break;
            default: throw new UnsupportedOperationException("Unknown Element");
        }
        this.nerDetector = CRFClassifier.getClassifierNoExceptions(directory.concat(modelName));
    }
}
