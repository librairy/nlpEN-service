package org.librairy.service.nlp.annotators;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordLemmaTokenizer {

    public List<Token> tokenize(Annotation annotation)
    {
        // Iterate over all of the sentences found
        return annotation.get(CoreAnnotations.SentencesAnnotation.class)
                .parallelStream()
                .flatMap(sentence -> sentence.get(CoreAnnotations.TokensAnnotation.class).stream())
                .map(coreLabel -> {
                    Token token = new Token();
                    token.setPos(translateFrom(coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class).toLowerCase()));
                    token.setWord(coreLabel.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase());
                    //token.setStopWord(coreLabel.get(StopWordAnnotatorWrapper.class).first);
                    token.setStopWord(false);
                    return token;
                })
                .collect(Collectors.toList());
    }

    private PoS translateFrom(String posTag){
        if (posTag.startsWith("nn")) return PoS.NOUN;
        if (posTag.startsWith("rb")) return PoS.ADVERB;
        if (posTag.startsWith("in")) return PoS.PREPOSITION;
        if (posTag.startsWith("pr")) return PoS.PRONOUN;
        if (posTag.startsWith("jj")) return PoS.ADJECTIVE;
        if (posTag.startsWith("dt")) return PoS.ARTICLE;
        if (posTag.startsWith("cc")) return PoS.CONJUNCTION;
        if (posTag.startsWith("uh")) return PoS.INTERJECTION;
        if (posTag.startsWith("vb")) return PoS.VERB;
        return PoS.PREPOSITION;
    }
}
