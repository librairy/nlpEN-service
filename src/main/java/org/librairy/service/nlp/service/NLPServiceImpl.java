package org.librairy.service.nlp.service;

import com.google.common.base.CharMatcher;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.Group;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.utils.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class NLPServiceImpl implements org.librairy.service.nlp.facade.model.NlpService {

    private static final Logger LOG = LoggerFactory.getLogger(NLPServiceImpl.class);

    @Autowired
    ServiceManager serviceManager;

    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public String tokens(String text, List<PoS> filter, Form form, boolean multigrams) throws AvroRemoteException {
        List<Annotation> annotations = serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter);
        if (multigrams){
            List<Annotation> refAnnotations = serviceManager.getDBpediaService(Thread.currentThread()).annotations(text).stream().filter(a -> a.getToken().getTarget().contains(" ")).collect(Collectors.toList());
            annotations = AnnotationUtils.merge(annotations, refAnnotations);
        }
        return annotations.stream().map(a -> form.equals(Form.LEMMA)? a.getToken().getLemma() : a.getToken().getTarget().contains(" ")? a.getToken().getLemma() : a.getToken().getTarget()).collect(Collectors.joining(" "));
    }

    @Override
    public List<Annotation> annotations(String text, List<PoS> filter, boolean multigrams, boolean references) throws AvroRemoteException {
        List<Annotation> annotations = addExtra(serviceManager.getIXAService(Thread.currentThread()).annotations(text, filter), text, multigrams, references);
        return annotations.stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.toList());
    }

    @Override
    public List<Group> groups(String text, List<PoS> filter, boolean multigrams, boolean references) throws AvroRemoteException {
        List<Annotation> annotations = addExtra(serviceManager.getCoreService(Thread.currentThread()).annotations(text, filter), text, multigrams, references).stream().filter(a -> CharMatcher.javaLetterOrDigit().matchesAnyOf(a.getToken().getLemma())).collect(Collectors.toList());

        Map<Annotation, Long> grouped = annotations.stream().collect(Collectors.groupingBy(a -> a, Collectors.counting()));

        return grouped.entrySet().stream().map( entry -> {
            org.librairy.service.nlp.facade.model.Annotation annotation = entry.getKey();
            Group group = new Group();
            group.setLemma(annotation.getToken().getLemma());
            group.setUri(annotation.getUri());
            group.setPos(annotation.getToken().getPos());
            group.setFreq(entry.getValue());
            return group;
        }).sorted((a,b) -> -a.getFreq().compareTo(b.getFreq())).collect(Collectors.toList());
    }

    private List<Annotation> addExtra(List<Annotation> annotations, String text, Boolean multigrams, Boolean references){
        if (multigrams || references){
            List<Annotation> refAnnotations = serviceManager.getDBpediaService(Thread.currentThread()).annotations(text);
            if (multigrams && references){
                //nothing to filter
            }else if (multigrams){
                refAnnotations = refAnnotations.stream().filter(a -> a.getToken().getTarget().contains(" ")).map(a -> {
                    a.setUri(null);
                    return a;
                }).collect(Collectors.toList());
            }else if (references){
                refAnnotations = refAnnotations.stream().filter(a -> !a.getToken().getTarget().contains(" ")).collect(Collectors.toList());
            }
            return AnnotationUtils.merge(annotations, refAnnotations).stream().filter(a -> a.getToken().getPos() != null).collect(Collectors.toList());
        }
        return annotations;
    }
}
