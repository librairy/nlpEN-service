package org.librairy.service.nlp.service;

import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class DBpediaServiceIntTest {


    private static final Logger LOG = LoggerFactory.getLogger(DBpediaServiceIntTest.class);


    @Test
    public void annotation() throws IOException {

        DBpediaService service = new DBpediaService("http://localhost:2222/rest",0.8);

        String text = "The 1988 Atlantic hurricane season was a moderately active season that proved costly and deadly, with 15 tropical cyclones directly affecting land. The first cyclone to attain tropical storm status was Alberto on August 8; the final storm of the year, Tropical Storm Keith, became extratropical on November 24. The season produced 19 tropical depressions, including 12 tropical storms. There were five hurricanes, including three major hurricanes (Category 3 or higher on the Saffir–Simpson scale). Hurricane Gilbert (pictured) was at the time the strongest Atlantic hurricane on record. It tracked through the Caribbean Sea and the Gulf of Mexico and caused devastation in Mexico and many island nations, particularly Jamaica. Its passage caused damage valued at US$2.98 billion and more than 300 deaths, mostly in Mexico. Hurricane Joan, striking Nicaragua as a Category 4 hurricane, caused damage valued at about $1.87 billion and more than 200 deaths.";

        String text2 = "The Semantic Web is an extension of the World Wide Web through standards by the World Wide Web Consortium ( W3C ) . [ 1 ] The standards promote common data formats and exchange protocols on the Web , most fundamentally the Resource Description Framework ( RDF ) . According to the W3C , The Semantic Web provides a common framework that allows data to be shared and reused across application , enterprise , and community boundaries . [ 2 ] The Semantic Web is therefore regarded as an integrator across different content , information applications and systems .";


        List<Annotation> annotations = service.annotations(text);

        annotations.forEach(annotation -> LOG.info(annotation.toString()));

//        Assert.assertEquals(2, annotations.size());
    }
}