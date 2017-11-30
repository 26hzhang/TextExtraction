package com.isaac.example.corenlp;

import com.isaac.nlp.CoreNLPParser;

public class NameEntityRecogExample {
    public static void main (String[] args) {
        System.err.print("Create stanford corenlp parser... ");
        CoreNLPParser corenlp = new CoreNLPParser();
        System.err.println("Done...");
        String sentence1 = "Mary is studying in Stanford University, which is located at California, since July 2015. ";
        System.err.println(sentence1);
        System.out.println(corenlp.detectNERInlineXML(sentence1));
        System.out.println("Person: " + corenlp.findPerson(sentence1));
        System.out.println("Location: " + corenlp.findLocation(sentence1));
        System.out.println("Organization: " + corenlp.findOrganization(sentence1));
        System.out.println("Date: " + corenlp.findDate(sentence1));
        String sentence2 = "I got up this morning at 9 am, went to a shop to spend five dollars to buy a 50% off toothbrush.";
        System.err.println(sentence2);
        System.out.println(corenlp.detectNERInlineXML(sentence2));
        System.out.println("Time: " + corenlp.findTime(sentence2));
        System.out.println("Percent: " + corenlp.findPercent(sentence2));
        System.out.println("Money: " + corenlp.findMoney(sentence2));
    }
}
