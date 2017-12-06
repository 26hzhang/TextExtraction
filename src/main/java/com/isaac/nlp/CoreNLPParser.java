package com.isaac.nlp;

import com.isaac.ling.NERType;
import com.isaac.phrases.POSTagPhrase;
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Dictionaries.Animacy;
import edu.stanford.nlp.coref.data.Dictionaries.MentionType;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
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

    private StanfordCoreNLP pipeline;
    private AbstractSequenceClassifier<CoreLabel> nerDetector3Class;
    private AbstractSequenceClassifier<CoreLabel> nerDetector4Class;
    private AbstractSequenceClassifier<CoreLabel> nerDetector7Class;

    private String sentences;
    private Annotation annotation;

    public CoreNLPParser () {
        String directory = "edu/stanford/nlp/models/"; // model directory
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,dcoref,natlog,openie");
        // english dependencies: english_SD.gz, universal dependencies: english_UD.gz
        // props.setProperty("parse.model", directory.concat("parser/nndep/english_UD.gz"));
        // props.setProperty("depparse.model", directory.concat("parser/nndep/english_UD.gz"));
        this.pipeline = new StanfordCoreNLP(props);
        String nerModelPath3Class = directory.concat("ner/english.all.3class.distsim.crf.ser.gz");
        this.nerDetector3Class = CRFClassifier.getClassifierNoExceptions(nerModelPath3Class);
        String nerModelPath4Class = directory.concat("ner/english.conll.4class.distsim.crf.ser.gz");
        this.nerDetector4Class = CRFClassifier.getClassifierNoExceptions(nerModelPath4Class);
        String nerModelPath7Class = directory.concat("ner/english.muc.7class.distsim.crf.ser.gz");
        this.nerDetector7Class = CRFClassifier.getClassifierNoExceptions(nerModelPath7Class);
    }

    /** annotate sentence, apply stanford corenlp parser to given sentences */
    public void annotate (String sentences) {
        this.sentences = sentences;
        annotation = new Annotation(sentences);
        pipeline.annotate(annotation);
    }

    /** @return {@link List} of {@link CoreMap}, each {@link CoreMap} is a tokenized sentence from paragraph/text */
    private List<CoreMap> sentenceAnnotate () {
        return annotation.get(CoreAnnotations.SentencesAnnotation.class);
    }

    /** @return {@link List} of {@link CoreLabel}, each {@link CoreLabel} is a tokenized word from sentence */
    private List<List<CoreLabel>> wordAnnotate() {
        return sentenceAnnotate().stream().map(e -> e.get(CoreAnnotations.TokensAnnotation.class)).collect(Collectors.toList());
    }

    /** @return {@link List} of tokenized sentences from paragraph/text */
    public List<String> sentenceTokenizer() {
        return sentenceAnnotate().stream().map(CoreMap::toString).collect(Collectors.toList());
    }

    /** @return {@link List} of tokenized words from sentence */
    public List<List<String>> wordTokenizer() {
        return wordAnnotate().stream().map(e -> e.stream().map(CoreLabel::word).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /** @return {@link List} of lemmatized words from sentence */
    public List<List<String>> lemmaTokenizer () {
        return wordAnnotate().stream().map(e -> e.stream().map(CoreLabel::lemma).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /** @return {@link List} of {@link POSTagPhrase} */
    public List<List<POSTagPhrase>> posTagger() {
        return wordAnnotate().stream().map(e ->
                        e.stream().map(t -> new POSTagPhrase(t.word(), t.get(CoreAnnotations.PartOfSpeechAnnotation.class)))
                        .collect(Collectors.toList())).collect(Collectors.toList());
    }

    /** @return tagged sentence */
    public List<String> posTags2String () {
        return posTagger().stream()
                .map(e -> String.join(" ", e.stream().map(POSTagPhrase::toString).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /*------------------------ Dependency Parser -----------------------*/
    /** @return {@link SemanticGraph} */
    public List<SemanticGraph> dependencyParse () {
        // SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class -- basic
        // SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class -- enhanced
        // SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class -- enhanced++
        return sentenceAnnotate().stream()
                .map(e -> e.get(SemanticGraphCoreAnnotations.EnhancedDependenciesAnnotation.class))
                .collect(Collectors.toList());
    }

    /** @return List of List of {@link SemanticGraphEdge} */
    public List<List<SemanticGraphEdge>> dependencyParse2Edge () {
        return dependencyParse().stream().map(e -> StreamSupport.stream(e.edgeIterable().spliterator(), false)
                .collect(Collectors.toList())).collect(Collectors.toList());
    }

    /** @return {@link List} of {@link Triple} contains dependency relations */
    public List<List<Triple<String, String, String>>> dependencyParse2Triple () {
        return dependencyParse2Edge().stream()
                .map(l -> l.stream()
                        .map(e -> Triple.makeTriple(e.getSource().toString(), e.getTarget().toString(),
                                e.getRelation().toString()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /** @return semantic graph string */
    public List<String> dependencyParse2DotFormat () {
        return dependencyParse().stream().map(SemanticGraph::toDotFormat).collect(Collectors.toList());
    }
    public List<String> dependencyParse2String () {
        return dependencyParse().stream().map(SemanticGraph::toString).collect(Collectors.toList());
    }
    /*------------------------------------------------------------------*/

    /*--------------- Anaphora Resolution (Coreference) ----------------*/
    /**
     * The function is used to find all the coreference in given text. It returns a list of list of {@link CorefMention},
     * each list of {@link CorefMention} is a list of related component in given text. Plus, the {@link CorefMention} contains
     * much useful information:
     *      CorefMention.animacy ({@link Animacy}) -- indicates if it is a animate argument (value: ANIMATE, INANIMATE)
     *      CorefMention.sentNum ({@link Integer}) -- indicates which sentence the mention appears
     *      CorefMention.headIndex ({@link Integer}) -- indicates the head word in this mention phrase in sentence
     *      CorefMention.startIndex ({@link Integer}) -- indicates the start offset (include) of the mention phrase in sentence
     *      CorefMention.endIndex ({@link Integer}) -- indicates the end offset (exclude) of the mention phrases in sentence
     *      CorefMention.mentionSpan ({@link String}) -- the mention phrase
     *      CorefMention.mentionType ({@link MentionType}) -- indicates the type of mention phrase
     *                                                        (value: NOMINAL, PRONOMINAL, etc.)
     *      CorefMention.mentionID ({@link Integer}) -- the id of mention phrase
     *      CorefMention.corefClusterID ({@link Integer}) -- the chain id where the mention phrase in
     * @return a list of coreference chains (each chain is a list of coreference mentions ({@link CorefMention}))
     */
    public List<List<CorefMention>> corefChains () {
        return annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values().stream()
                .map(CorefChain::getMentionsInTextualOrder).collect(Collectors.toList());
    }

    public String corefMention2String (CorefMention m) {
        return "Phrase: " + m.mentionSpan + ", " +
                "sentNum: " + m.sentNum + ", " +
                "headIndex: " + m.headIndex + ", " +
                "start: " + m.startIndex + ", " +
                "end: " + m.endIndex + ", " +
                "Animacy: " + m.animacy.toString() + ", " +
                "mentionType: " + m.mentionType.toString() + ", " +
                "mentionId: " + m.mentionID + ", " +
                "clusterId: " + m.corefClusterID;
    }

    /** convert corefChains to String */
    public List<String> corefChains2String () {
        return annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class).values().stream()
                .map(CorefChain::toString).collect(Collectors.toList());
    }

    /** @return list of list of {@link Mention} */
    public List<List<Mention>> mentions () {
        return sentenceAnnotate().stream().map(e -> e.get(CorefCoreAnnotations.CorefMentionsAnnotation.class))
                .collect(Collectors.toList());
    }
    /*------------------------------------------------------------------*/

    /*-------------------- Named Entity Recognition --------------------*/
    /** @return {@link List} of specific entities */
    public List<String> findPerson () { return findSpecialEntity(NERType.PERSON); }
    public List<String> findLocation () { return findSpecialEntity(NERType.LOCATION); }
    public List<String> findOrganization () { return findSpecialEntity(NERType.ORGANIZATION); }
    public List<String> findMoney () { return findSpecialEntity(NERType.MONEY); }
    public List<String> findPercent () { return findSpecialEntity(NERType.PERCENT); }
    public List<String> findTime () { return findSpecialEntity(NERType.TIME); }
    public List<String> findDate () { return findSpecialEntity(NERType.DATE); }
    public List<String> findMISC () { return findSpecialEntity(NERType.MISC); }
    private List<String> findSpecialEntity (NERType type) {
        Pattern pattern;
        switch (type) {
            case TIME: pattern = Pattern.compile("<TIME>(.+?)</TIME>"); break;
            case DATE: pattern = Pattern.compile("<DATE>(.+?)</DATE>"); break;
            case PERSON: pattern = Pattern.compile("<PERSON>(.+?)</PERSON>"); break;
            case LOCATION: pattern = Pattern.compile("<LOCATION>(.+?)</LOCATION>"); break;
            case ORGANIZATION: pattern = Pattern.compile("<ORGANIZATION>(.+?)</ORGANIZATION>"); break;
            case MONEY: pattern = Pattern.compile("<MONEY>(.+?)</MONEY>"); break;
            case PERCENT: pattern = Pattern.compile("<PERCENT>(.+?)</PERCENT>"); break;
            case MISC: pattern = Pattern.compile("<MISC>(.+?)</MISC>"); break;
            default: throw new UnsupportedOperationException("Not Implemented");
        }
        String markedSent = detectNERInlineXML();
        Matcher m = pattern.matcher(markedSent);
        List<String> entities = new ArrayList<>();
        while (m.find()) entities.add(m.group(1));
        return entities;
    }

    /** @return a string which marked the specific entity inline */
    public String detectNERInlineXML() {
        String class3 = detectNERInlineXML(nerDetector3Class); // get person
        String class4 = detectNERInlineXML(nerDetector4Class); // special: MISC
        String class7 = detectNERInlineXML(nerDetector7Class); // class7 lost person info, append from class3
        // append person
        Matcher personM = Pattern.compile("<PERSON>(.+?)</PERSON>").matcher(class3);
        while (personM.find()) {
            String person = personM.group(1);
            class7 = class7.replace(person, "<PERSON>".concat(person).concat("</PERSON>"));
        }
        // append MISC
        Matcher miscM = Pattern.compile("<MISC>(.+?)</MISC>").matcher(class4);
        while (miscM.find()) {
            String misc = miscM.group(1);
            class7 = class7.replace(misc, "<MISC>".concat(misc).concat("</MISC>"));
        }
        return class7;
    }

    /** @return a string which detected the specific entity in different format */
    public String detectNERInlineXML(AbstractSequenceClassifier<CoreLabel> nerDetector) {
        return nerDetector.classifyWithInlineXML(sentences);
    }

    /*public String detectNER2String (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToString(sentences);
    }
    public String detectNER2XMLString (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToString(sentence, "xml", true);
    }
    public String detectNER2TSVString (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToString(sentence, "tsv", false);
    }
    public String detectNER2TabledEntity (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToString(sentence, "tabbedEntities", false);
    }
    public String detectNER2SlashTags (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToString(sentence, "slashTags", false);
    }
    /** @return {@link List} of {@link Triple}, detect specific entities */
    /*public List<Triple<String, Integer, Integer>> detectNER2CharacterOffsets (
                                    AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classifyToCharacterOffsets(sentence);
    }
    /** detect specific entity to core label lists */
    /*public List<List<CoreLabel>> detectNER2CoreLabel (AbstractSequenceClassifier<CoreLabel> nerDetector, String sentence) {
        return nerDetector.classify(sentence);
    }*/
    /*------------------------------------------------------------------*/

    /*----------------------- Getters and Setters ----------------------*/
    /** Getters */
    public StanfordCoreNLP getPipeline () { return pipeline; }
    public AbstractSequenceClassifier<CoreLabel> getNerDetector3Class() { return nerDetector3Class; }
    public AbstractSequenceClassifier<CoreLabel> getNerDetector4Class() { return nerDetector4Class; }
    public AbstractSequenceClassifier<CoreLabel> getNerDetector7Class() { return nerDetector7Class; }

    /** Setters */
    public void setPipeline (StanfordCoreNLP pipeline) { this.pipeline = pipeline; }
    public void setPipeline (Properties props) { this.pipeline = new StanfordCoreNLP(props); }
    /*------------------------------------------------------------------*/
}
