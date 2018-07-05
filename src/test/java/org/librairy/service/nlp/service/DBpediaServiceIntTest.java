package org.librairy.service.nlp.service;

import org.json.JSONObject;
import org.junit.Test;
import org.librairy.service.nlp.facade.model.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

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

    @Test
    public void corpus() throws IOException {

        String filePath = "/Users/cbadenes/Corpus/wikipedia-articles/10k-articles.jsonl.gz";

        DBpediaService service = new DBpediaService("http://localhost:2222/rest",0.8);

        BufferedReader reader = new BufferedReader(new InputStreamReader( new GZIPInputStream( new FileInputStream(filePath))));
        String line = null;
        ConcurrentHashMap<String,Integer> ngrams = new ConcurrentHashMap<>();
        AtomicInteger counter = new AtomicInteger();

        ParallelExecutor executor = new ParallelExecutor();
        while((line = reader.readLine()) != null){
            final String json = line;
            executor.execute(() -> {
                if (counter.incrementAndGet() % 100 == 0) LOG.info(counter.get() + " articles analyzed");

                JSONObject article = new JSONObject(json);
                String text = article.getString("text");


                List<Annotation> annotations = service.annotations(text);

                annotations.stream().filter(a -> a.getToken().getTarget().contains(" ")).forEach( a -> {
                    Integer freq = 0;
                    if (ngrams.containsKey(a.getToken().getTarget())) freq = ngrams.get(a.getToken().getTarget());
                    freq += 1;
                    ngrams.put(a.getToken().getTarget(), freq);
                });

            });

        }

        LOG.info("Corpus analyzed. Waiting to finish..");

        executor.stop();

        LOG.info("Saving results..");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("target/ngrams.csv")));

        ngrams.entrySet().stream().sorted((a,b) -> -a.getValue().compareTo(b.getValue())).forEach(entry -> {
            try {
                writer.write(entry.getKey()+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.close();



    }
}