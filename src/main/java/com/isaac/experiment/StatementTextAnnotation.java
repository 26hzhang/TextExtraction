package com.isaac.experiment;

import edu.stanford.nlp.ling.CoreAnnotation;

public class StatementTextAnnotation implements CoreAnnotation<String> {
    @Override
    public Class<String> getType() {
        return String.class;
    }
}
