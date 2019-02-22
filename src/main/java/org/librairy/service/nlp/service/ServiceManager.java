package org.librairy.service.nlp.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.librairy.service.nlp.annotators.StanfordAnnotatorWrapper;
import org.librairy.service.nlp.annotators.StanfordPipeAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordWordnetPipeAnnotatorEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class ServiceManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceManager.class);

    @Value("#{environment['RESOURCE_FOLDER']?:'${resource.folder}'}")
    String resourceFolder;

    @Value("#{environment['SPOTLIGHT_ENDPOINT']?:'${spotlight.endpoint}'}")
    String endpoint;

    @Value("#{environment['SPOTLIGHT_THRESHOLD']?:${spotlight.threshold}}")
    Double threshold;

    LoadingCache<String, IXAService> ixaServices;

    LoadingCache<String, CoreNLPService> coreServices;

    LoadingCache<String, CoreNLPService> wordnetServices;

    LoadingCache<String, DBpediaService> dbpediaServices;

    @PostConstruct
    public void setup(){
        ixaServices = CacheBuilder.newBuilder()
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

        coreServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, CoreNLPService>() {
                            public CoreNLPService load(String key) {
                                LOG.info("Initializing CoreNLP service for thread: " + key);
                                CoreNLPService coreService = new CoreNLPService();
                                coreService.setAnnotator(new StanfordPipeAnnotatorEN());
                                coreService.setTokenizer(new StanfordAnnotatorWrapper());
                                return coreService;
                            }
                        });

        wordnetServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, CoreNLPService>() {
                            public CoreNLPService load(String key) {
                                LOG.info("Initializing CoreNLP with Wordnet service for thread: " + key);
                                CoreNLPService coreService = new CoreNLPService();
                                coreService.setAnnotator(new StanfordWordnetPipeAnnotatorEN(resourceFolder));
                                coreService.setTokenizer(new StanfordAnnotatorWrapper());
                                return coreService;
                            }
                        });

        dbpediaServices = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(
                        new CacheLoader<String, DBpediaService>() {
                            public DBpediaService load(String key) {
                                LOG.info("Initializing DBpedia service for thread: " + key);
                                return new DBpediaService(endpoint, threshold);
                            }
                        });
    }

    public IXAService getIXAService(Thread thread) {
        try {
            return ixaServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public CoreNLPService getCoreService(Thread thread) {

        try {
            return coreServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public CoreNLPService getWordnetService(Thread thread) {

        try {
            return wordnetServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public DBpediaService getDBpediaService(Thread thread) {

        try {
            return dbpediaServices.get("thread"+thread.getId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public void setResourceFolder(String resourceFolder) {
        this.resourceFolder = resourceFolder;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
}
