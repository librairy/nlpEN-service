package org.librairy.service.nlp.service;

import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.annotators.StanfordAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordLemmaTokenizer;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CoreNLPServiceTest {


    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPServiceTest.class);

    private CoreNLPService service;

    @Before
    public void setup(){
        service = new CoreNLPService();
        service.setAnnotator(new StanfordAnnotatorEN());
        service.setTokenizer(new StanfordLemmaTokenizer());

    }

    @Test
    public void group() throws IOException {

        String text = "I have mice at my home";

//        List<PoS> filter = Collections.emptyList();
        List<PoS> filter = Arrays.asList( new PoS[]{PoS.NOUN, PoS.VERB});

        List<Token> tokens = service.group(text, filter);

        tokens.forEach(token -> LOG.info(token.toString()));

//        Assert.assertEquals(2, annotations.size());
    }
}