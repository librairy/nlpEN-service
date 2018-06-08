package org.librairy.service.nlp.service;

import org.librairy.service.nlp.client.SpotlightClient;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class DBpediaService  {

    private static final Logger LOG = LoggerFactory.getLogger(DBpediaService.class);
    private final String endpoint;
    private final Double threshold;

    private SpotlightClient client;

    public DBpediaService(String endpoint, Double threshold) {
        this.endpoint = endpoint;
        this.threshold = threshold;
        this.client = new SpotlightClient();
        LOG.debug("DBpedia Service linked to '" + endpoint + "'");
    }

    public List<Annotation> annotations (String text){
        List<Annotation> annotations = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(,|.$)").matcher(text);
        while (matcher.find()){
            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            List<Annotation> partialAnnotations = getDBpediaReferences(partialContent);
            annotations.addAll(partialAnnotations);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated  in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs");
        }
        return annotations;
    }

    private List<Annotation> getDBpediaReferences(String text){
        try{
            List<Annotation> annotations = new ArrayList<>();

            Map<String,String> params = new HashMap<>();
            params.put("policy","whitelist");
            params.put("text", text);
            params.put("confidence","0.2");
            params.put("support","20");
            String url = endpoint + "/annotate";

            Document response = client.request(url,params);

            NodeList resources = response.getElementsByTagName("Resource");

            for(int i = 0; i < resources.getLength(); i++){

                Node resource = resources.item(i);

                if (resource.getNodeType() == Node.ELEMENT_NODE){

                    Element element = (Element) resource;

                    String score    = element.getAttribute("similarityScore");

                    if (Double.valueOf(score) > threshold){
                        String uri      = element.getAttribute("URI");
                        String target   = element.getAttribute("surfaceForm");
                        String offset   = element.getAttribute("offset");

                        Token token = new Token();
                        token.setTarget(target);
                        token.setLemma(target.toLowerCase().replace(" ","_"));
                        if (target.contains(" ")) token.setPos(PoS.NOUN);

                        Annotation annotation = new Annotation();
                        annotation.setToken(token);
                        annotation.setUri(uri);
                        annotation.setOffset(Long.valueOf(offset));
                        annotations.add(annotation);
                    }

                }
            }
            return annotations;
        }catch (Exception e){
            LOG.error("Unexpected error from DBpedia Spotlight service",e);
            return Collections.emptyList();
        }


    }

}
