package org.librairy.service.nlp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class IXAServiceTest {


    private static final Logger LOG = LoggerFactory.getLogger(IXAServiceTest.class);

    private IXAService service;

    @Before
    public void setup(){
        service = new IXAService("src/main/bin");
        service.setup();
    }

    @Test
    public void annotation() throws IOException {

        String text = "The 1988 Atlantic hurricane season was a moderately active season that proved costly and deadly, with 15 tropical cyclones directly affecting land. The first cyclone to attain tropical storm status was Alberto on August 8; the final storm of the year, Tropical Storm Keith, became extratropical on November 24. The season produced 19 tropical depressions, including 12 tropical storms. There were five hurricanes, including three major hurricanes (Category 3 or higher on the Saffir–Simpson scale). Hurricane Gilbert (pictured) was at the time the strongest Atlantic hurricane on record. It tracked through the Caribbean Sea and the Gulf of Mexico and caused devastation in Mexico and many island nations, particularly Jamaica. Its passage caused damage valued at US$2.98 billion and more than 300 deaths, mostly in Mexico. Hurricane Joan, striking Nicaragua as a Category 4 hurricane, caused damage valued at about $1.87 billion and more than 200 deaths.";

        List<PoS> filter = Collections.emptyList();

        List<Annotation> annotations = service.annotations(text, filter);

        annotations.forEach(annotation -> LOG.info(annotation.toString()));

//        Assert.assertEquals(2, annotations.size());
    }
}