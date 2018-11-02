package org.librairy.service.nlp.service;

import org.junit.Before;
import org.junit.Test;
import org.librairy.service.nlp.annotators.StanfordPipeAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordAnnotatorWrapper;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Group;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class NLPServiceTest {


    private static final Logger LOG = LoggerFactory.getLogger(NLPServiceTest.class);

    private NLPServiceImpl service;

    @Before
    public void setup(){

        ServiceManager serviceManager = new ServiceManager();
        serviceManager.setup();
        serviceManager.setResourceFolder("src/main/bin");
        serviceManager.setEndpoint("http://localhost:2222/rest");
        serviceManager.setThreshold(0.8);

        service = new NLPServiceImpl();
        service.setServiceManager(serviceManager);

    }

    @Test
    public void group() throws IOException {

        String text = "Barack Hussein Obama II is an American politician who is the 44th and current President of the United States. He is the first African American to hold the office and the first president born outside the continental United States. Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he was president of the Harvard Law Review. He was a community organizer in Chicago before earning his law degree. He worked as a civil rights attorney and taught constitutional law at the University of Chicago Law School between 1992 and 2004. While serving three terms representing the 13th District in the Illinois Senate from 1997 to 2004, he ran unsuccessfully in the Democratic primary for the United States House of Representatives in 2000 against incumbent Bobby Rush. In 2004, Obama received national attention during his campaign to represent Illinois in the United States Senate with his victory in the March Democratic Party primary, his keynote address at the Democratic National Convention in July, and his election to the Senate in November. He began his presidential campaign in 2007 and, after a close primary campaign against Hillary Clinton in 2008, he won sufficient delegates in the Democratic Party primaries to receive the presidential nomination. He then defeated Republican nominee John McCain in the general election, and was inaugurated as president on January 20, 2009. Nine months after his inauguration, Obama was controversially named the 2009 Nobel Peace Prize laureate. During his first two years in office, Obama signed into law economic stimulus legislation in response to the Great Recession in the form of the American Recovery and Reinvestment Act of 2009 and the Tax Relief, Unemployment Insurance Reauthorization, and Job Creation Act of 2010. Other major domestic initiatives in his first term included the Patient Protection and Affordable Care Act, often referred to as";

//        List<PoS> filter = Collections.emptyList();
        List<PoS> filter = Arrays.asList( new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADVERB, PoS.ADJECTIVE});

        List<Annotation> annotations = service.annotations(text, filter, true, true);

        LOG.info("annotations -> " + annotations);

        List<Group> groups = service.groups(text, filter, true, true);

        LOG.info("groups -> " + groups);

//        Assert.assertEquals(2, annotations.size());
    }
}