package org.librairy.service.nlp.controllers;


import org.apache.avro.AvroRemoteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.librairy.service.nlp.facade.AvroClient;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.service.IXAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AvroController.class,IXAService.class})
@WebAppConfiguration
public class AvroTest {

    private static final Logger LOG = LoggerFactory.getLogger(AvroTest.class);

    @Test
    public void processTest() throws InterruptedException, IOException {

        AvroClient client = new AvroClient();


        String host     = "localhost";
        Integer port    = 65111;

        client.open(host,port);

        List<String> texts = Arrays.asList(new String[]{
                "first text",
                "second text",
                "third text"});


        texts.forEach(text -> {
            try {
                client.process(text, Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADVERB, PoS.ADJECTIVE}), Form.RAW);
            } catch (AvroRemoteException e) {
                e.printStackTrace();
            }
        });

        client.close();
    }

    @Test
    public void annotateTest() throws InterruptedException, IOException {


        AvroClient client = new AvroClient();


        String host     = "localhost";
        Integer port    = 65111;

        client.open(host,port);

        List<String> texts = Arrays.asList(new String[]{
                "first text",
                "second text",
                "third text"});


        texts.forEach(text -> {
            try {
                client.annotate(text, Arrays.asList(new PoS[]{PoS.NOUN, PoS.VERB, PoS.ADVERB, PoS.ADJECTIVE}));
            } catch (AvroRemoteException e) {
                e.printStackTrace();
            }
        });

        client.close();
    }

}