package org.librairy.service.nlp.service;

import org.apache.avro.AvroRemoteException;
import org.junit.Test;
import org.librairy.service.nlp.annotators.StanfordAnnotatorWrapper;
import org.librairy.service.nlp.annotators.StanfordPipeAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordWordnetPipeAnnotatorEN;
import org.librairy.service.nlp.facade.model.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class CoreNLPServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPServiceTest.class);


    @Test
    public void execute() throws AvroRemoteException {

        CoreNLPService coreService = new CoreNLPService();
        coreService.setAnnotator(new StanfordWordnetPipeAnnotatorEN("src/main/bin"));
        coreService.setTokenizer(new StanfordAnnotatorWrapper());


        String text = "Barack Hussein Obama II is an American politician who is the 44th and current President of the United States. He is the first African American to hold the office and the first president born outside the continental United States. Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he was president of the Harvard Law Review. He was a community organizer in Chicago before earning his law degree. He worked as a civil rights attorney and taught constitutional law at the University of Chicago Law School between 1992 and 2004. While serving three terms representing the 13th District in the Illinois Senate from 1997 to 2004, he ran unsuccessfully in the Democratic primary for the United States House of Representatives in 2000 against incumbent Bobby Rush. In 2004, Obama received national attention during his campaign to represent Illinois in the United States Senate with his victory in the March Democratic Party primary, his keynote address at the Democratic National Convention in July, and his election to the Senate in November. He began his presidential campaign in 2007 and, after a close primary campaign against Hillary Clinton in 2008, he won sufficient delegates in the Democratic Party primaries to receive the presidential nomination. He then defeated Republican nominee John McCain in the general election, and was inaugurated as president on January 20, 2009. Nine months after his inauguration, Obama was controversially named the 2009 Nobel Peace Prize laureate. During his first two years in office, Obama signed into law economic stimulus legislation in response to the Great Recession in the form of the American Recovery and Reinvestment Act of 2009 and the Tax Relief, Unemployment Insurance Reauthorization, and Job Creation Act of 2010. Other major domestic initiatives in his first term included the Patient Protection and Affordable Care Act, often referred to as";

//        String text = "She looked up the world record.";

        String tokens = coreService.tokens(text, Collections.emptyList(), Form.LEMMA);

        LOG.info("Tokens -> " + tokens);


    }
}
