package org.librairy.service.nlp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.librairy.service.nlp.annotators.StanfordAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordLemmaTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    LoadingCache<String, IXAService> ixaModels;

    LoadingCache<String, CoreNLPService> coreModels;

    @PostConstruct
    public void setup(){
        ixaModels = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, IXAService>() {
                            public IXAService load(String key) {
                                LOG.info("Initializing IXA service for thread: " + key);
                                IXAService ixaService = new IXAService(resourceFolder);
                                ixaService.setup();
                                return ixaService;
                            }
                        });

        coreModels = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, CoreNLPService>() {
                            public CoreNLPService load(String key) {
                                LOG.info("Initializing CoreNLP service for thread: " + key);
                                CoreNLPService coreService = new CoreNLPService();
                                coreService.setAnnotator(new StanfordAnnotatorEN());
                                coreService.setTokenizer(new StanfordLemmaTokenizer());
                                return coreService;
                            }
                        });
    }

    public IXAService getIXAService(Thread thread) {
        try {
            return ixaModels.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CoreNLPService getCoreService(Thread thread) {

        try {
            return coreModels.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

}
