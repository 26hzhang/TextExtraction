package com.isaac.experiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class Query2StatementExample {
    public static void main(String[] args) {
        StanfordCoreNLP pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma"));
        QuestionToStatementTranslator translator = new QuestionToStatementTranslator();
        String question = "Where is the apple?";
        Annotation ann = new Annotation(question);
        pipeline.annotate(ann);
        List<CoreLabel> tokens = ann.get(CoreAnnotations.TokensAnnotation.class);
        List<List<CoreLabel>> statements = translator.toStatement(tokens);
        for (List<CoreLabel> statement : statements) {
            System.out.println("  -> " + StringUtils.join(statement.stream().map(CoreLabel::word), " "));
        }
    }
}
