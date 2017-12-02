package com.isaac.phrases;

@SuppressWarnings("unused")
public class ChunkedPhrase {
    private String phrase;
    private String type;

    public ChunkedPhrase (String phrase, String type) {
        this.phrase = phrase;
        this.type = type;
    }

    @Override public String toString () {
        return "[".concat(phrase).concat(" (").concat(type).concat(")]");
    }

    /** Getters and Setters */
    public String getPhrase () { return phrase; }
    public void setPhrase (String phrase) { this.phrase = phrase; }
    public String getType () { return type; }
    public void setType (String type) { this.type = type; }

}
