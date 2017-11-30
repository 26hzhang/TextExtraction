package com.isaac.example;

import com.isaac.nlp.ConceptNetParser;
import com.isaac.phrases.ConceptPhrase;
import edu.stanford.nlp.util.Triple;

import java.util.List;

public class ConceptNetExample {
    public static void main (String[] args) {
        ConceptNetParser conceptnet = new ConceptNetParser();
        String phrase = "plant_tree";
        System.out.println("Raw Json Response: ".concat(conceptnet.getResponse(phrase)).concat("\n"));
        List<ConceptPhrase> conceptPhrases = conceptnet.extractConceptPhrases(phrase);
        System.out.println("Number of Concept Phrases: " + conceptPhrases.size() + "\n");
        for (ConceptPhrase conceptPhrase : conceptPhrases) {
            System.out.println(conceptPhrase.toString().concat("\n"));
        }
        List<Triple<String, String, String>> triples = conceptnet.extractTriples(phrase);
        triples.forEach(t -> System.out.println("[" + t.first() + ", " + t.second() + ", " + t.third() + "]"));
    }
}
