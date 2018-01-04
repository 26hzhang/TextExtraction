package com.isaac.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class FileUtils {
    private static final String DATASET_DIRECTORY = "src/main/resources/dataset/";
    private static final String STOPWORDS_DIRECTORY = "src/main/resources/stopwords/";

    /** @return all stories in dataset */
    public static List<String> readAllParagraphs (String filename) {
        List<String> paragraphs = new ArrayList<>();
        try {
            paragraphs = Files.readAllLines(Paths.get(DATASET_DIRECTORY.concat(filename)), Charset.defaultCharset());
        } catch (IOException e) { e.printStackTrace(); }
        return paragraphs;
    }

    /** @return N-th stories in dataset */
    public static String readNthParagraph (String filename, int n) {
        String paragraph = "";
        BufferedReader reader = bufferReader(filename);
        try {
            for (int i = 0; i < n; i++) paragraph = reader.readLine();
            reader.close();
        } catch (IOException e) { e.printStackTrace(); }
        return paragraph;
    }

    /** @return first stories in dataset */
    public static String readFirstParagraph (String filename) { return readNthParagraph(filename, 1); }

    /** load different version of stop words list */
    public static List<String> readSmallStopWords () { return readStopWords("stopwordslist_173.txt"); }
    public static List<String> readMiddleStopWords () { return readStopWords("stopwordslist_429.txt"); }
    public static List<String> readLargeStopWords () { return readStopWords("stopwordslist_665.txt"); }

    /** @return {@link List} of stopwords */
    private static List<String> readStopWords (String filename) {
        List<String> stopwords = new ArrayList<>();
        try { stopwords = Files.readAllLines(Paths.get(STOPWORDS_DIRECTORY.concat(filename)), Charset.defaultCharset()); }
        catch (IOException e) { e.printStackTrace(); }
        return stopwords.stream().map(String::trim).collect(Collectors.toList()); // cleanup unused space
    }

    /** @return {@link BufferedReader} */
    private static BufferedReader bufferReader (String filename) {
        BufferedReader reader = null;
        try { reader = new BufferedReader(new FileReader(DATASET_DIRECTORY.concat(filename)));
        } catch (IOException e) { e.printStackTrace(); }
        return reader;
    }
}
