package com.isaac.nlp;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isaac.phrases.ConceptPhrase;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ConceptNetParser {
    private final String headUrl;
    private final Pattern enPattern;
    private final JsonParser jsonParser;

    public ConceptNetParser() {
        this.headUrl = "http://api.conceptnet.io/c/en/";
        this.jsonParser = new JsonParser();
        this.enPattern = Pattern.compile("/a/\\[/r/(.*?)/.*?,/c/en/(.*?)/.*?,/c/en/(.*?)/.*?]");
    }

    public List<ConceptPhrase> extractConceptPhrases (String phrase) {
        List<ConceptPhrase> phrases = new ArrayList<>();
        JsonArray tripleArray = getTripleArray(getResponse(phrase));
        for (int i = 0; i < tripleArray.size(); i++) {
            JsonObject object = tripleArray.get(i).getAsJsonObject();
            String rawTripleStr = object.get("@id").getAsString();
            Double weight = object.get("weight").getAsDouble();
            String example = object.get("surfaceText").getAsString().replaceAll("[\\[\\]]", "");
            Matcher m = enPattern.matcher(rawTripleStr);
            if (m.find()) phrases.add(new ConceptPhrase(Triple.makeTriple(m.group(2), m.group(1), m.group(3)), weight, example));
        }
        return phrases;
    }

    public List<Triple<String, String, String>> extractTriples (String phrase) {
        List<Triple<String, String, String>> triples = new ArrayList<>();
        JsonArray tripleArray = getTripleArray(getResponse(phrase));
        for (int i = 0; i < tripleArray.size(); i++) {
            String rawTripleStr = tripleArray.get(i).getAsJsonObject().get("@id").getAsString();
            Matcher m = enPattern.matcher(rawTripleStr);
            if (m.find()) triples.add(Triple.makeTriple(m.group(2), m.group(1), m.group(3))); // entity-relation-entity
        }
        return triples;
    }

    private JsonArray getTripleArray (String jsonString) {
        JsonObject conceptNetObj = jsonParser.parse(jsonString).getAsJsonObject();
        return conceptNetObj.get("edges").getAsJsonArray(); // all information stores in "edges" tag
    }

    public String getResponse (String phrase) {
        String response = "";
        try {
            URL url = new URL(headUrl.concat(phrase));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            response = CharStreams.toString(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
        } catch (IOException e) { e.printStackTrace(); }
        return response;
    }

}
