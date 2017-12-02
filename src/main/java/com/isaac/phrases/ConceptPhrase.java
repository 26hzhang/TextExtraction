package com.isaac.phrases;

import edu.stanford.nlp.util.Triple;

@SuppressWarnings("unused")
public class ConceptPhrase {
    private String entity1;
    private String relation;
    private String entity2;
    private Double weight;
    private String example;

    public ConceptPhrase (Triple<String, String, String> relTriple, Double weight, String example) {
        this.entity1 = relTriple.first;
        this.relation = relTriple.second;
        this.entity2 = relTriple.third;
        this.weight = weight;
        this.example = example;
    }

    public ConceptPhrase (String entity1, String relation, String entity2, Double weight, String example) {
        this.entity1 = entity1;
        this.relation = relation;
        this.entity2 = entity2;
        this.weight = weight;
        this.example = example;
    }

    @Override public String toString () {
        String triple = "Triple: [".concat(entity1).concat(", ").concat(relation).concat(", ").concat(entity2).concat("]");
        String weiStr = "Weight: ".concat(String.format("%.4f", weight));
        String exaStr = "Example: ".concat(example);
        return String.join("\n", triple, weiStr, exaStr);
    }

    /** Getters */
    public String getEntity1 () { return entity1; }
    public String getRelation () { return relation; }
    public String getEntity2 () { return entity2; }
    public Double getWeight () { return weight; }
    public String getExample () { return example; }
}
