package com.isaac.example.openie;

import com.isaac.nlp.OpenIEParser;
import com.isaac.phrases.ArgumentPhrase;
import com.isaac.phrases.ChunkedPhrase;
import com.isaac.phrases.POSTagPhrase;
import com.isaac.utils.FileUtils;
import edu.knowitall.tool.chunk.ChunkedToken;
import edu.knowitall.tool.postag.PostaggedToken;
import edu.knowitall.tool.tokenize.Token;

import java.util.List;
import java.util.stream.Collectors;

public class OpenIEExample {
    public static void main (String[] args) {
        String singleSent = "The U.S. president Barack Obama gave his speech on Tuesday at White House to thousands of people";
        System.err.println("Create OpenIE Parser...");
        OpenIEParser openie = new OpenIEParser();
        System.err.println("Done...");
        List<Token> tokens = openie.tokenize(singleSent);
        System.out.println(tokens);
        List<String> tokensStr = openie.tokenize2String(singleSent);
        System.out.println(tokensStr);
        List<PostaggedToken> tags = openie.posTag(singleSent);
        System.out.println(tags);
        List<String> tagsStr = openie.posTag2String(singleSent);
        System.out.println(tagsStr);
        List<ChunkedToken> chunks = openie.chunk(singleSent);
        System.out.println(chunks);
        List<String> chunksStr = openie.chunk2String(singleSent);
        System.out.println(chunksStr);
        List<ChunkedPhrase> chunkedPhrases = openie.getChunkedPhrases(singleSent);
        List<String> list = chunkedPhrases.stream().map(ChunkedPhrase::toString).collect(Collectors.toList());
        System.out.println(String.join(", ", list).concat("\n"));
        // Extract information
        System.err.println("Information Extraction Demo...");
        String paragraph = FileUtils.readNthParagraph("paragraphs.txt", 3);
        List<String> sentences = openie.sentenceTokenize(paragraph);
        for (String sentence : sentences) {
            List<ArgumentPhrase> argumentPhrases = openie.extract(sentence);
            argumentPhrases.forEach(arg -> System.out.println(arg.toString()));
            System.out.println();
            List<ChunkedPhrase> chunked = openie.getChunkedPhrases(sentence);
            chunked.forEach(c -> System.out.println(c.toString()));
            List<POSTagPhrase> posTagPhrases = openie.getPosTagPhrases(sentence);
            System.out.println("\n" + posTagPhrases.toString());
        }
    }
}
