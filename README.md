# Text Extraction

![Authour](https://img.shields.io/badge/Author-Zhang%20Hao%20(Isaac%20Changhau)-blue.svg) ![](https://img.shields.io/badge/Java-1.8-brightgreen.svg) ![](https://img.shields.io/badge/WordNet-3.1-brightgreen.svg) ![](https://img.shields.io/badge/StanfordCoreNLP-3.8.0-yellowgreen.svg) ![](https://img.shields.io/badge/OpenNLP-1.8.3-yellowgreen.svg) ![](https://img.shields.io/badge/OpenIE-4.2.1-yellowgreen.svg) ![](https://img.shields.io/badge/ClausIE-1.0-yellowgreen.svg)

It is a wrapper and combiner of [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/), [OpenNLP](https://opennlp.apache.org/), [OpenIE](http://openie.allenai.org/), [ClausIE](https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/software/clausie/), [WordNet](https://wordnet.princeton.edu/) and [ConceptNet API](https://github.com/commonsense/conceptnet5/wiki/API) to make those tools easily to use for tackling some NLP tasks, like **Named Entity Recognition**, **POS Tagging**, **Chunking**, **Information Extraction**, **Dependency Parising**, **Concept Extraction** and etc.

## Dependencies
- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
- [Stanford CoreNLP](https://stanfordnlp.github.io/CoreNLP/), GitHub page: [[link]](https://github.com/stanfordnlp/CoreNLP), maven snippets: [[link]](https://mvnrepository.com/artifact/edu.stanford.nlp/stanford-corenlp/3.8.0). [**CoreNLP online demo!**](http://corenlp.run/).
- [OpenIE](http://openie.allenai.org/), GitHub page: [[link]](https://github.com/knowitall/openie), maven snippets: [[link]](https://mvnrepository.com/artifact/edu.washington.cs.knowitall.openie/openie). [**OpenIE online demo!**](http://openie.allenai.org/).
- [OpenNLP](https://opennlp.apache.org/), GitHub page: [[link]](https://github.com/apache/opennlp), maven snippets: [[link]](https://mvnrepository.com/artifact/org.apache.opennlp/opennlp-tools).
- [WordNet](https://wordnet.princeton.edu/), GitHub page: [[link]](https://github.com/wordnet/wordnet), Java Interface: [[MIT-JWI]](http://projects.csail.mit.edu/jwi/), and its maven snippets: [[link]](https://mvnrepository.com/artifact/edu.mit/jwi). [**WordNet online demo!**](http://wordnetweb.princeton.edu/perl/webwn).
- [ClausIE](https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/software/clausie/), GitHub page and maven snippets: [[link]](https://github.com/IsaacChanghau/ClausIE) (since [Max Planck Institute](https://www.mpi-inf.mpg.de/home/) only provide the source codes and binaries, so I mavenized it and put the codes and settings in my ClausIE repository). [**ClausIE online demo!**](https://gate.d5.mpi-inf.mpg.de/ClausIEGate/ClausIEGate).
- [ConceptNet](http://conceptnet.io/), GitHub page: [[link]](https://github.com/commonsense/conceptnet5), and its [wiki](https://github.com/commonsense/conceptnet5/wiki) (which gives a introduction about how to use its API to access ConceptNet resources). [**ConceptNet online demo!**](http://conceptnet.io/).

## Examples
### Stanford CoreNLP Parser
For named entity recognition, first construct a `corenlp` parser, and annotate the text you want to process. [Stanford Named Entity Recognizer (NER)](https://nlp.stanford.edu/software/CRF-NER.shtml) provides three different models to tackle NER tasks, one is **3 class** model (`Location`, `Person`, `Organization`), one is **4 class** model (`Location`, `Person`, `Organization`, `Misc`), while another is **7 class** model (`Location`, `Person`, `Organization`, `Money`, `Percent`, `Date`, `Time`), since each of them has different coverage, so in the `CoreNLPParser`, three NER detectors are created based on those models. All the NER detection methods are based on the mixture of three models' results. Besides, it's also available to use those detectors alone, lile: `corenlp.detectNERInlineXML(corenlp.getNerDetector7Class())`.
```java
public class NameEntityRecogExample {
    public static void main (String[] args) {
        String text = "Mary is studying in Stanford University, which is located at California, since July 2015. " + 
            "She got up this morning at 9:00 am and went to a shop to spend five dollars to buy a 50% off toothbrush. " + 
            "After she came back, she found her backyard was looking a little empty, so she decided she would plant something in it.";
        // create corenlp parser
        CoreNLPParser corenlp = new CoreNLPParser();
        corenlp.annotate(text); // annotate the given text
        System.out.println(corenlp.detectNERInlineXML()); // print detected NER in text with inline XML.
        System.out.println("Person: " + corenlp.findPerson()); // find person
        System.out.println("Location: " + corenlp.findLocation()); // find location entity
        System.out.println("Organization: " + corenlp.findOrganization()); // find organization entity
        System.out.println("Date: " + corenlp.findDate()); // find date entity
        System.out.println("Time: " + corenlp.findTime()); // find time entity
        System.out.println("Percent: " + corenlp.findPercent()); // find percent entity
        System.out.println("Money: " + corenlp.findMoney()); // find money entity
        System.out.println("MISC: " + corenlp.findMISC()); // find MISC (anything else) entity
        System.out.println();
        // singleton NER model in stanford corenlp
        // model: english.all.3class.distsim.crf.ser.gz
        System.out.println(corenlp.detectNERInlineXML(corenlp.getNerDetector3Class()));
        // model: english.conll.4class.distsim.crf.ser.gz
        System.out.println(corenlp.detectNERInlineXML(corenlp.getNerDetector4Class()));
        // model: english.muc.7class.distsim.crf.ser.gz
        System.out.println(corenlp.detectNERInlineXML(corenlp.getNerDetector7Class()));
    }
}
```
Here is the output:
```$xslt
<PERSON>Mary</PERSON> is studying in <ORGANIZATION>Stanford University</ORGANIZATION>, which is located at <LOCATION>California</LOCATION>, since <DATE>July 2015</DATE>. <PERSON>John</PERSON> got up <TIME>this morning</TIME> at 9:00 am and went to a shop to spend <MONEY>five dollars</MONEY> to buy a <PERCENT>50%</PERCENT> off toothbrush. After he came back, she found his backyard was looking a little empty, so he decided he would plant something in it.
Person: [Mary, John]
Location: [California]
Organization: [Stanford University]
Date: [July 2015]
Time: [this morning]
Percent: [50%]
Money: [five dollars]
MISC: []

<PERSON>Mary</PERSON> is studying in <ORGANIZATION>Stanford University</ORGANIZATION>, which is located at <LOCATION>California</LOCATION>, since July 2015. <PERSON>John</PERSON> got up this morning at 9:00 am and went to a shop to spend five dollars to buy a 50% off toothbrush. After he came back, she found his backyard was looking a little empty, so he decided he would plant something in it.
<PERSON>Mary</PERSON> is studying in <ORGANIZATION>Stanford University</ORGANIZATION>, which is located at <LOCATION>California</LOCATION>, since July 2015. <PERSON>John</PERSON> got up this morning at 9:00 am and went to a shop to spend five dollars to buy a 50% off toothbrush. After he came back, she found his backyard was looking a little empty, so he decided he would plant something in it.
Mary is studying in <ORGANIZATION>Stanford University</ORGANIZATION>, which is located at <LOCATION>California</LOCATION>, since <DATE>July 2015</DATE>. John got up <TIME>this morning</TIME> at 9:00 am and went to a shop to spend <MONEY>five dollars</MONEY> to buy a <PERCENT>50%</PERCENT> off toothbrush. After he came back, she found his backyard was looking a little empty, so he decided he would plant something in it.
```

For Tokenize and POS tasks, the CoreNLPParser makes it much easier to got the results with less codes:
```java
public class StanfordCoreNLPExample {
    public static void main (String[] args) {
        String text = "..."; // same as before
        // create corenlp parser
        CoreNLPParser corenlp = new CoreNLPParser();
        corenlp.annotate(text); // annotate the given text
        // Sentence Level tokenization
        List<String> sentences = corenlp.sentenceTokenizer(); // each string in the list is a sentence
        // Word Level Tokenizer
        List<List<String>> tokens = corenlp.wordTokenizer(); // each List<String> is tokenized word in each sentence
        List<List<String>> lemmaTokens = corenlp.lemmaTokenizer(); // each List<String> is tokenized word lemma in each sentence
        /* POS posTagger */
        List<List<POSTagPhrase>> tags = corenlp.posTagger(); // POSTagPhrase contains two elements: word and pos tag
        List<String> tagsStr = corenlp.posTags2String(); // return a list of string, each string is the sentence with each token marked by its pos tag
        // Example: Mary/NNP is/VBZ studying/VBG in/IN Stanford/NNP University/NNP ,/, which/WDT is/VBZ located/JJ at/IN California/NNP ,/, since/IN July/NNP 2015/CD ./.
    }
}
```
Moreover, for [`dependency parsing`](https://nlp.stanford.edu/software/stanford-dependencies.shtml), [`anaphora (coreference) resolution]`](https://stanfordnlp.github.io/CoreNLP/coref.html) and other useful functions, see details in the codes.

### Apache OpenNLP and UW OpenIE Parser
**Apache OpenNLP Parser**:
```java
public class OpenNLPExample {
    public static void main (String[] args) {
        String singleSent = "Most large cities in the US had morning and afternoon newspapers, but New York doesn't have on Thursday, Stanford University locates in California.";
        String paragraph = FileUtils.readNthParagraph("paragraphs.txt", 2);
        System.err.println("Create OpenNLP Parser...");
        OpenNLPParser opennlp = new OpenNLPParser();
        System.err.println("Done...");
        // Name Entity detection
        List<String> persons = opennlp.findPerson(singleSent);
        System.out.println("Persons: " + persons);
        List<String> dates = opennlp.findDate(singleSent);
        System.out.println("Dates: " + dates);
        List<String> times = opennlp.findTime(singleSent);
        System.out.println("Time: " + times);
        List<String> locations = opennlp.findLocation(singleSent);
        System.out.println("Locations: " + locations);
        List<String> organizations = opennlp.findOrganization(singleSent);
        System.out.println("Organization: " + organizations);
        // Tokenize, pos tagging, chunking
        List<String> sentences = opennlp.sentenceTokenize(paragraph); // segment paragraph into sentences
        for (String sentence : sentences) {
            List<String> tokens = opennlp.tokenize(sentence);
            List<String> tags = opennlp.tag(sentence);
            List<String> chunks = opennlp.chunk(sentence);
            for (int i = 0; i < tokens.size(); i++)
                System.out.println(tokens.get(i) + "\t" + tags.get(i) + "\t" + chunks.get(i));
            List<ChunkedPhrase> chunkedPhrases = opennlp.chunkedPhrases(sentence);
            chunkedPhrases.forEach(System.out::println);
        }
    }
}
```

**UW OpenIE Parser**:
```java
public class OpenIEExample {
    public static void main (String[] args) {
        String singleSent = "The U.S. president Barack Obama gave his speech on Tuesday at White House to thousands of people";
        System.err.println("Create OpenIE Parser...");
        OpenIEParser openie = new OpenIEParser();
        System.err.println("Done...");
        List<Token> tokens = openie.tokenize(singleSent);
        System.out.println(tokens);
        List<String> tokensStr = openie.tokenize2String(singleSent);
        System.out.println(tokensStr);
        List<PostaggedToken> tags = openie.posTag(singleSent);
        System.out.println(tags);
        List<String> tagsStr = openie.posTag2String(singleSent);
        System.out.println(tagsStr);
        List<ChunkedToken> chunks = openie.chunk(singleSent);
        System.out.println(chunks);
        List<String> chunksStr = openie.chunk2String(singleSent);
        System.out.println(chunksStr);
        List<ChunkedPhrase> chunkedPhrases = openie.getChunkedPhrases(singleSent);
        List<String> list = chunkedPhrases.stream().map(ChunkedPhrase::toString).collect(Collectors.toList());
        System.out.println(String.join(", ", list).concat("\n"));
        // Extract information
        System.err.println("Information Extraction Demo...");
        String paragraph = FileUtils.readNthParagraph("paragraphs.txt", 3);
        List<String> sentences = openie.sentenceTokenize(paragraph);
        for (String sentence : sentences) {
            List<ArgumentPhrase> argumentPhrases = openie.extract(sentence);
            argumentPhrases.forEach(arg -> System.out.println(arg.toString()));
            System.out.println();
            List<ChunkedPhrase> chunked = openie.getChunkedPhrases(sentence);
            chunked.forEach(c -> System.out.println(c.toString()));
            List<POSTagPhrase> posTagPhrases = openie.getPosTagPhrases(sentence);
            System.out.println("\n" + posTagPhrases.toString());
        }
    }
}
```

### ConceptNet and ClausIE Parser
**ConceptNet Parser**: it is a simple _http_ requester and component extractor, which send request to conceptnet api and got response (JSON format data), then using [Gson](https://github.com/google/gson) to extract useful information and store in `ConceptPhrase` (includes, _entity1-relation-entity2_, _weight_, _example_).
```java
public class ConceptNetExample {
    public static void main (String[] args) {
        ConceptNetParser conceptnet = new ConceptNetParser();
        String phrase = "plant_tree";
        System.out.println("Raw Json Response: ".concat(conceptnet.getResponse(phrase)).concat("\n"));
        List<ConceptPhrase> conceptPhrases = conceptnet.extractConceptPhrases(phrase);
        System.out.println("Number of Concept Phrases: " + conceptPhrases.size() + "\n");
        for (ConceptPhrase conceptPhrase : conceptPhrases) System.out.println(conceptPhrase.toString().concat("\n"));
        List<Triple<String, String, String>> triples = conceptnet.extractTriples(phrase);
        triples.forEach(t -> System.out.println("[" + t.first() + ", " + t.second() + ", " + t.third() + "]"));
    }
}
```

**ClausIE Parser**: _TODO_

## References
- [Stanford CoreNLP CorefExample](https://stanfordnlp.github.io/CoreNLP/coref.html).
- [NextCenturyCorporation/EVEREST-TripletExtraction](https://github.com/NextCenturyCorporation/EVEREST-TripletExtraction).
- [usc-isi-i2/Web-Karma](https://github.com/usc-isi-i2/Web-Karma).
- [allenai/openie-standalone](https://github.com/allenai/openie-standalone).
- [tutorialspoint/OpenNLP Tutorial](https://www.tutorialspoint.com/opennlp/index.htm).
- [Stanford Named Entity Recognizer (NER)](https://nlp.stanford.edu/software/CRF-NER.shtml).
- [Stanford CoreNLP -- CorefAnnotator](https://stanfordnlp.github.io/CoreNLP/coref.html).
- [Stanford Dependencies](https://nlp.stanford.edu/software/stanford-dependencies.shtml).
- [commonsense/conceptnet5 wiki](https://github.com/commonsense/conceptnet5/wiki).
