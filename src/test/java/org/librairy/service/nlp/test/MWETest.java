package org.librairy.service.nlp.test;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import edu.mit.jmwe.data.Token;
import edu.mit.jmwe.detect.Consecutive;
import edu.mit.jmwe.detect.IMWEDetector;
import edu.mit.jmwe.index.IMWEIndex;
import edu.mit.jmwe.index.MWEIndex;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.JMWEAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class MWETest {

    private static final Logger LOG = LoggerFactory.getLogger(MWETest.class);


    @Test
    public void validateSentence() throws IOException {

        // get handle to file containing MWE index data, // e.g., mweindex_wordnet3.0_Semcor1.6.data
        File idxData = new File("src/main/bin/mwe/mweindex_wordnet3.0_Semcor1.6.txt");

        // construct an MWE index and open it
        IMWEIndex index = new MWEIndex(idxData);
        index.open();


        // make a basic detector
        IMWEDetector detector = new Consecutive(index);

        // construct a test sentence:
        // "She looked up the world record."
        List <IToken > sentence = new ArrayList<IToken >();
        sentence.add(new Token("She", "DT", 0l));
        sentence.add(new Token("looked", "VBT", 1l, "look"));
        sentence.add(new Token("up", "RP", 2l));
        sentence.add(new Token("the", "DT", 3l));
        sentence.add(new Token("world", "NN", 4l));
        sentence.add(new Token("record", "NN", 5l));
        sentence.add(new Token(".", ".", 6l));

        // run detector and print out results
        List<IMWE<IToken>> mwes = detector.detect(sentence);
        for(IMWE <IToken > mwe : mwes){
            System.out.println(mwe);
        }

    }

    @Test
    public void validateText(){


        String index    = new File("src/main/bin/mwe/mweindex_wordnet3.0_Semcor1.6.txt").getAbsolutePath();

        String text     = "She looked up the world record.";

        // creates the properties for Stanford CoreNLP: tokenize, ssplit, pos, lemma, jmwe
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, jmwe");
        props.setProperty("customAnnotatorClass.jmwe", "edu.stanford.nlp.pipeline.JMWEAnnotator");
        props.setProperty("customAnnotatorClass.jmwe.verbose", "false");
        props.setProperty("customAnnotatorClass.jmwe.underscoreReplacement", "-");
        props.setProperty("customAnnotatorClass.jmwe.indexData", index);
        props.setProperty("customAnnotatorClass.jmwe.detector", "CompositeConsecutiveProperNouns");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // put the text in the document annotation
        Annotation doc = new Annotation(text);

        // run the CoreNLP pipeline on the document
        pipeline.annotate(doc);

        // loop over the sentences
        List<CoreMap> sentences = doc.get(CoreAnnotations.SentencesAnnotation.class);
        System.out.println();
        for(CoreMap sentence: sentences) {
            System.out.println("Sentence: "+sentence);
            // loop over all discovered jMWE token and perform some action
            for (IMWE<IToken> token: sentence.get(JMWEAnnotator.JMWEAnnotation.class)) {
                System.out.println("IMWE<IToken>: "+token+", token.isInflected(): "+token.isInflected()+", token.getForm(): "+token.getForm());
            }
            System.out.println();
        }


    }

}
