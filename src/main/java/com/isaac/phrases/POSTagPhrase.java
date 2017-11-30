package com.isaac.phrases;

@SuppressWarnings("unused")
public class POSTagPhrase {
    private String word;
    private String tag;

    public POSTagPhrase (String word, String tag) {
        this.word = word;
        this.tag = tag;
    }

    @Override public String toString() { return word.concat("/").concat(tag); }

    /** Getters and Setters */
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
