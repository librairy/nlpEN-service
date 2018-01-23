package org.librairy.service.nlp.service;

import org.librairy.service.nlp.facade.model.PoS;

import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class PoSTranslator {

    public static PoS toPoSTag(String termPoS){
        switch (termPoS.toUpperCase()){
            case "G":     return PoS.ADJECTIVE;
            case "A":     return PoS.ADVERB;
            case "D":     return PoS.ARTICLE;
            case "C":     return PoS.CONJUNCTION;
            case "O":     return PoS.INTERJECTION;
            case "N":
            case "R":     return PoS.NOUN;
            case "P":     return PoS.PREPOSITION;
            case "Q":     return PoS.PRONOUN;
            case "V":     return PoS.VERB;
            default:      throw new RuntimeException("Term PoS not handled: " + termPoS);
        }
    }

    public static List<String> toTermPoS(PoS type){
        switch (type){
            case ADJECTIVE:     return Arrays.asList(new String[]{"G"});
            case ADVERB:        return Arrays.asList(new String[]{"A"});
            case ARTICLE:       return Arrays.asList(new String[]{"D"});
            case CONJUNCTION:   return Arrays.asList(new String[]{"C"});
            case INTERJECTION:  return Arrays.asList(new String[]{"O"});
            case NOUN:          return Arrays.asList(new String[]{"N","R"});
            case PREPOSITION:   return Arrays.asList(new String[]{"P"});
            case PRONOUN:       return Arrays.asList(new String[]{"Q"});
            case VERB:          return Arrays.asList(new String[]{"V"});
            default:            return Arrays.asList(new String[]{});
        }
    }
}
