package com.isaac.nlp;

import com.isaac.phrases.ArgumentPhrase;
import com.isaac.phrases.ChunkedPhrase;
import com.isaac.phrases.POSTagPhrase;
import com.isaac.utils.Common;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.chunk.ChunkedToken;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.postag.PostaggedToken;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import edu.knowitall.tool.tokenize.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class OpenIEParser {

    private final OpenIE openie;

    public OpenIEParser () {
        openie = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer())), new ClearSrl(),
                false, true);
    }

    /** @return {@link List} of sentence tokenized from paragraph */
    public List<String> sentenceTokenize (String paragraph) { return Common.sentenceTokenize(paragraph); }

    /** @return {@link List} of {@link Token} */
    public List<Token> tokenize (String sentence) { return Common.seq2List(openie.tokenizer().tokenize(sentence)); }

    public List<String> tokenize2String (String sentence) {
        return tokenize(sentence).stream().map(Token::string).collect(Collectors.toList());
    }

    /** @return {@link List} of {@link PostaggedToken} */
    public List<PostaggedToken> posTag (String sentence) {
        return Common.seq2List(openie.postagger().postagTokenized(openie.tokenizer().tokenize(sentence)));
    }

    public List<String> posTag2String (String sentence) {
        return posTag(sentence).stream().map(PostaggedToken::toString)
                .map(s -> s.substring(s.lastIndexOf("/") + 1, s.lastIndexOf("@")))
                .collect(Collectors.toList());
    }

    public List<POSTagPhrase> getPosTagPhrases (String sentence) {
        return posTag(sentence).stream().map(PostaggedToken::toString).map(s ->
                new POSTagPhrase(s.substring(0, s.lastIndexOf("/")), s.substring(s.lastIndexOf("/") + 1,
                        s.lastIndexOf("@"))))
                .collect(Collectors.toList());
    }

    /** @return {@link List} of {@link ChunkedToken} */
    public List<ChunkedToken> chunk (String sentence) { return Common.seq2List(openie.chunkerOIE().chunk(sentence)); }

    public List<String> chunk2String (String sentence) {
        return chunk(sentence).stream().map(ChunkedToken::chunk).collect(Collectors.toList());
    }

    public List<ChunkedPhrase> getChunkedPhrases (String sentence) {
        List<String> tokens = tokenize2String(sentence);
        List<String> chunkedTokens = chunk2String(sentence);
        List<ChunkedPhrase> phrases = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        String type = "";
        for (int i = 0; i < chunkedTokens.size(); i++) {
            if (chunkedTokens.get(i).startsWith("I")) tmp.add(tokens.get(i));
            if (chunkedTokens.get(i).startsWith("B")) {
                if (!tmp.isEmpty()) {
                    phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
                    tmp.clear();
                }
                tmp.add(tokens.get(i));
                type = chunkedTokens.get(i).substring(2);
            }
            if (chunkedTokens.get(i).length() == 1) { // "0"
                if (!tmp.isEmpty()) {
                    phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
                    tmp.clear();
                }
                tmp.add(tokens.get(i));
                type = chunkedTokens.get(i);
            }
            if (i == chunkedTokens.size() - 1) phrases.add(new ChunkedPhrase(String.join(" ", tmp), type));
        }
        return phrases;
    }

    public List<ArgumentPhrase> extractUseTriples (String sentence) {
        return extract2InstanceUseTriples(sentence).stream().map(ArgumentPhrase::new).collect(Collectors.toList());
    }

    public List<ArgumentPhrase> extract (String sentence) {
        return extract2Instance(sentence).stream().map(ArgumentPhrase::new).collect(Collectors.toList());
    }

    public List<Instance> extract2InstanceUseTriples (String sentence) { return extract2Instance(sentence, true); }

    public List<Instance> extract2Instance (String sentence) { return extract2Instance(sentence, false); }

    /** @return a list of {@link Instance} extracted by OpenIE (argument1-relation-argument2s) */
    private List<Instance> extract2Instance (String sentence, boolean useTriples) {
        return Common.seq2List(openie.extract(sentence, openie.chunkerOIE(), useTriples));
    }

    /** OpenIE Getter */
    public OpenIE getOpenie () { return openie; }
}
