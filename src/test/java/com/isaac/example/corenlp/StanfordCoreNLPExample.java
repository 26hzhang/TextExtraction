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
        corenlp.annotate(paragraph);
        System.err.println("Done...");
        /* Sentence tokenization */
        List<String> sentences = corenlp.sentenceTokenizer();
        sentences.forEach(System.out::println);
        System.out.println();
        /* Tokenizer */
        List<List<String>> tokens = corenlp.wordTokenizer();
        List<List<String>> lemmaTokens = corenlp.lemmaTokenizer();
        System.out.println("Tokens: " + tokens + ", " + lemmaTokens);
        /* POS posTagger */
        List<List<POSTagPhrase>> tags = corenlp.posTagger();
        List<String> tagsStr = corenlp.posTags2String();
        System.out.println("Tags: " + tags + "\nTags String: " + tagsStr);
        /* Semantic Parser */
        List<SemanticGraph> graph = corenlp.dependencyParse();
        System.out.println(graph.get(0).toString());
        System.out.println(graph.get(0).toDotFormat());
        System.out.println(graph.get(0).toEnUncollapsedSentenceString());
    }
}
