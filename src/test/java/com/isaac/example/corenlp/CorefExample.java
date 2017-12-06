package com.isaac.example.corenlp;

import java.util.List;

import com.isaac.nlp.CoreNLPParser;
import com.isaac.utils.FileUtils;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention;

public class CorefExample {
    public static void main(String[] args) {
        String paragraph = FileUtils.readNthParagraph("paragraphs.txt", 894);
        System.err.println("Create Stanford Core NLP parser...");
        CoreNLPParser corenlp = new CoreNLPParser();
        corenlp.annotate(paragraph);
        System.err.println("Done...");
        List<String> corefChainsStr = corenlp.corefChains2String();
        List<List<CorefChain.CorefMention>> chains = corenlp.corefChains();
        for (int i = 0; i < corefChainsStr.size(); i++) {
            System.out.println(corefChainsStr.get(i));
            for (CorefChain.CorefMention m : chains.get(i)) {
                System.out.println(corenlp.corefMention2String(m));
            }
            System.out.println();
        }
        List<List<Mention>> mentions = corenlp.mentions();
        for (List<Mention> ms : mentions) {
            System.out.println("---");
            System.out.println("mentions");
            for (Mention m : ms) {
                System.out.println("\t" + m);
            }
        }
        System.out.println("\n\n");
        // explore the Mention
        Mention m = mentions.get(0).get(0);
        System.out.println("Animacy: " + m.animacy);
        System.out.println("Head String: " + m.headString);
        System.out.println("NER String: " + m.nerString);
        System.out.println(m.sentNum + "\t" + m.headIndex + "\t" + m.startIndex + "\t" + m.endIndex + "\t" + m.corefClusterID +
                "\t" + m.mentionID + "\t" + m.goldCorefClusterID + "\t" + m.mentionNum + "\t" + m.originalRef + "\t" +
                m.paragraph + "\t" + m.utter);
        System.out.println("Gender: " + m.gender);
        System.out.println("Mention Type: " + m.mentionType);
        System.out.println("Generic: " + m.generic);
        System.out.println("Twin: " + m.hasTwin);
        System.out.println("direct Object: " + m.isDirectObject);
        System.out.println("indirect object: " + m.isIndirectObject);
        System.out.println("Preposition Object: " + m.isPrepositionObject);
        System.out.println("singleton: " + m.isSingleton);
        System.out.println("subject: " + m.isSubject);
        System.out.println("Person: " + m.person);
        System.out.println("appositions: " + m.appositions);
        System.out.println("dependents: " + m.dependents);

    }
}

