package org.librairy.service.nlp.annotators;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import edu.mit.jmwe.data.IMWEDesc;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.MWEPOS;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.JMWEAnnotator;
import edu.stanford.nlp.util.CoreMap;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.librairy.service.nlp.facade.utils.AnnotationUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class StanfordAnnotatorWrapper {


    private final Escaper escaper = Escapers.builder()
            .addEscape('\'',"")
            .addEscape('\"',"")
            .addEscape('(',"")
            .addEscape(')',"")
            .addEscape('[',"")
            .addEscape(']',"")
            .build();

    public List<org.librairy.service.nlp.facade.model.Annotation> tokenize(Annotation annotation)
    {

        List<CoreMap> sentenceAnnotation = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // Iterate over all of the sentences found
        List<org.librairy.service.nlp.facade.model.Annotation> unigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.get(CoreAnnotations.TokensAnnotation.class).stream())
                .map(coreLabel -> {
                    Token token = new Token();
                    token.setPos(translateFrom(coreLabel.get(CoreAnnotations.PartOfSpeechAnnotation.class).toLowerCase()));
                    token.setLemma(coreLabel.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase());
                    token.setTarget(coreLabel.originalText());

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(Long.valueOf(coreLabel.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class)));

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma())))
                .collect(Collectors.toList());

        List<org.librairy.service.nlp.facade.model.Annotation> multigramAnnotations = sentenceAnnotation
                .parallelStream()
                .flatMap(sentence -> sentence.containsKey(JMWEAnnotator.JMWEAnnotation.class) ? sentence.get(JMWEAnnotator.JMWEAnnotation.class).stream() : null)
                .filter(t -> t != null)
                .map(coreLabel -> {

                    IMWEDesc entry = coreLabel.getEntry();
                    Token token = new Token();

                    token.setPos(translateFrom(entry.getID().getPOS().name().toLowerCase()));
                    token.setLemma(entry.getID().getForm().toLowerCase());
                    token.setTarget(coreLabel.getForm().toLowerCase());

                    org.librairy.service.nlp.facade.model.Annotation tokenAnnotation = new org.librairy.service.nlp.facade.model.Annotation();
                    tokenAnnotation.setToken(token);
                    tokenAnnotation.setOffset(coreLabel.getOffset());

                    return tokenAnnotation;
                })
                .filter(a -> (!Strings.isNullOrEmpty(a.getToken().getLemma())))
                .collect(Collectors.toList());


        List<org.librairy.service.nlp.facade.model.Annotation> annotations = multigramAnnotations.isEmpty()? unigramAnnotations : AnnotationUtils.merge(unigramAnnotations, multigramAnnotations);

        return annotations;
    }

    /**
     * ADJECTIVE('J', new String[]{"JJ"}),
     NOUN('N', new String[]{"NN"}),
     OTHER('O', (String[])null),
     PROPER_NOUN('P', new String[]{"NNP"}),
     ADVERB('R', new String[]{"RB", "WRB"}),
     VERB('V', new String[]{"VB"});
     * @param posTag
     * @return
     */
    private PoS translateFrom(String posTag){
        switch(posTag){
            case "adjective": return PoS.ADJECTIVE;
            case "proper_noun": return PoS.NOUN;
            case "adverb": return PoS.ADVERB;
            case "verb": return PoS.VERB;
            default:
                if (posTag.startsWith("c")) return PoS.CONJUNCTION;
                if (posTag.startsWith("d")) return PoS.ARTICLE;
                if (posTag.startsWith("e")) return PoS.ARTICLE;
                if (posTag.startsWith("i")) return PoS.PREPOSITION;
                if (posTag.startsWith("j")) return PoS.ADJECTIVE;
                if (posTag.startsWith("l")) return PoS.NOUN;
                if (posTag.startsWith("m")) return PoS.ADVERB;
                if (posTag.startsWith("n")) return PoS.NOUN;
                if (posTag.startsWith("p")) return PoS.PRONOUN;
                if (posTag.startsWith("r")) return PoS.ADVERB;
                if (posTag.startsWith("u")) return PoS.INTERJECTION;
                if (posTag.startsWith("v")) return PoS.VERB;
                if (posTag.startsWith("w")) return PoS.ARTICLE;
                return PoS.CONJUNCTION;
        }
    }
}
