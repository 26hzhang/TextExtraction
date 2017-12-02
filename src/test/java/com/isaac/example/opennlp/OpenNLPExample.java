package com.isaac.example.opennlp;

import com.isaac.nlp.OpenNLPParser;
import com.isaac.phrases.ChunkedPhrase;
import com.isaac.utils.FileUtils;

import java.util.List;

public class OpenNLPExample {
    public static void main (String[] args) {
        String singleSent = "Most large cities in the US had morning and afternoon newspapers, but New York doesn't have on Thursday, Stanford University locates in ";
        String paragraph = FileUtils.readNthParagraph("paragraphs.txt", 2);
        System.err.println("Create OpenNLP Parser...");
        OpenNLPParser opennlp = new OpenNLPParser();
        System.err.println("Done...");
        // Name Entity detection
        List<String> persons = opennlp.findPerson(singleSent);
        System.out.println("Persons: " + persons);
        List<String> dates = opennlp.findDate(singleSent);
        System.out.println("Dates: " + dates);
        List<String> times = opennlp.findTime(singleSent);
        System.out.println("Time: " + times);
        List<String> locations = opennlp.findLocation(singleSent);
        System.out.println("Locations: " + locations);
        List<String> organizations = opennlp.findOrganization(singleSent);
        System.out.println("Organization: " + organizations);
        // Tokenize, pos tagging, chunking
        List<String> sentences = opennlp.sentenceTokenize(paragraph); // segment paragraph into sentences
        for (String sentence : sentences) {
            List<String> tokens = opennlp.tokenize(sentence);
            List<String> tags = opennlp.tag(sentence);
            List<String> chunks = opennlp.chunk(sentence);
            for (int i = 0; i < tokens.size(); i++)
                System.out.println(tokens.get(i) + "\t" + tags.get(i) + "\t" + chunks.get(i));
            List<ChunkedPhrase> chunkedPhrases = opennlp.chunkedPhrases(sentence);
            chunkedPhrases.forEach(System.out::println);
        }
    }
}
