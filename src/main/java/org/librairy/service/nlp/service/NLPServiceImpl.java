package org.librairy.service.nlp.service;

import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.Annotation;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
@Component
public class NLPServiceImpl implements org.librairy.service.nlp.facade.model.NlpService {

    private static final Logger LOG = LoggerFactory.getLogger(NLPServiceImpl.class);

    @Autowired
    ServiceManager serviceManager;


    @Override
    public String process(String text, List<PoS> filter, Form form) throws AvroRemoteException {
        if (text.length() > 500) return serviceManager.getCoreService(Thread.currentThread()).process(text,filter,form);
        return serviceManager.getIXAService(Thread.currentThread()).process(text,filter,form);
    }

    @Override
    public List<Annotation> annotate(String text, List<PoS> filter) throws AvroRemoteException {
        return serviceManager.getIXAService(Thread.currentThread()).annotate(text,filter);
    }

}
