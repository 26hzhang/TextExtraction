package com.isaac.example.corenlp;

import com.isaac.nlp.CoreNLPParser;

public class NameEntityRecogExample {
    public static void main (String[] args) {
        String sentence = "Mary is studying in Stanford University, which is located at California, since July 2015. " +
                "John got up this morning at 9:00 am and went to a shop to spend five dollars to buy a 50% off toothbrush. " +
                "After he came back, she found his backyard was looking a little empty, so he decided he would plant something in it.";
        System.err.print("Create stanford corenlp parser... ");
        CoreNLPParser corenlp = new CoreNLPParser();
        corenlp.annotate(sentence);
        System.err.println("Done...");
        System.out.println();
        System.err.println(sentence);
        System.err.println(corenlp.detectNERInlineXML());
        System.out.println("Person: " + corenlp.findPerson());
        System.out.println("Location: " + corenlp.findLocation());
        System.out.println("Organization: " + corenlp.findOrganization());
        System.out.println("Date: " + corenlp.findDate());
        System.out.println("Time: " + corenlp.findTime());
        System.out.println("Percent: " + corenlp.findPercent());
        System.out.println("Money: " + corenlp.findMoney());
        System.out.println("MISC: " + corenlp.findMISC());

        System.err.println(corenlp.detectNERInlineXML(corenlp.getNerDetector4Class()));
        System.err.println(corenlp.detectNERInlineXML(corenlp.getNerDetector3Class()));
        System.err.println(corenlp.detectNERInlineXML(corenlp.getNerDetector7Class()));
    }
}
