package com.isaac.example.corenlp;

import com.isaac.nlp.CoreNLPParser;
import com.isaac.phrases.POSTagPhrase;
import com.isaac.utils.FileUtils;
import edu.stanford.nlp.semgraph.SemanticGraph;

import java.util.List;

public class StanfordCoreNLPExample {
    public static void main (String[] args) {
        String paragraph = FileUtils.readFirstParagraph("paragraphs.txt");
        System.err.println("Create Stanford Core NLP parser...");
        CoreNLPParser corenlp = new CoreNLPParser();
        System.err.println("Done...");
        /* Sentence tokenization */
        List<String> sentences = corenlp.sentenceTokenize(paragraph);
        String sentence = sentences.get(0);
        /* name entity detection */
        List<String> persons = corenlp.findPerson(sentence);
        List<String> locations = corenlp.findLocation(sentence);
        List<String> date = corenlp.findDate(sentence);
        List<String> organization = corenlp.findOrganization(sentence);
        System.out.println("Name Entities: " + persons + ", " + locations + ", " + date + ", " + organization);
        System.out.println("Inline Represent: " + corenlp.detectNERInlineXML(sentence));
        /* Tokenizer */
        List<String> tokens = corenlp.tokenizer(sentence);
        List<String> lemmaTokens = corenlp.lemmaTokenizer(sentence);
        System.out.println("Tokens: " + tokens + ", " + lemmaTokens);
        /* POS tag */
        List<POSTagPhrase> tags = corenlp.tag(sentence);
        String tagsStr = corenlp.tags2String(sentence);
        System.out.println("Tags: " + tags + "\nTags String: " + tagsStr);
        /* Semantic Parser */
        SemanticGraph graph = corenlp.dependencyParse(sentence);
        System.out.println(graph.toString());
        System.out.println(graph.toDotFormat());
        System.out.println(graph.toEnUncollapsedSentenceString());
    }
}
