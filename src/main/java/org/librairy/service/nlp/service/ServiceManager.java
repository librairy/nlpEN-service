package org.librairy.service.nlp.service;

import org.librairy.service.nlp.annotators.StanfordAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordLemmaTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    Map<String,IXAService> ixaServices;
    Map<String,CoreNLPService> coreServices;

    @PostConstruct
    public void setup(){
        ixaServices     = new ConcurrentHashMap<>();
        coreServices    = new ConcurrentHashMap<>();
    }

    public IXAService getIXAService(Thread thread) {

        String id = "thread"+thread.getId();
        if (!ixaServices.containsKey(id)){
            LOG.info("Initializing IXA service for thread: " + id);
            IXAService ixaService = new IXAService(resourceFolder);
            ixaService.setup();
            ixaServices.put(id,ixaService);
        }
        return ixaServices.get(id);

    }

    public CoreNLPService getCoreService(Thread thread) {

        String id = "thread"+thread.getId();
        if (!coreServices.containsKey(id)){
            LOG.info("Initializing CoreNLP service for thread: " + id);
            CoreNLPService coreService = new CoreNLPService();
            coreService.setAnnotator(new StanfordAnnotatorEN());
            coreService.setTokenizer(new StanfordLemmaTokenizer());
            coreServices.put(id,coreService);
        }
        return coreServices.get(id);

    }

}
