package org.librairy.service.nlp.annotators;

import edu.stanford.nlp.pipeline.Annotation;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public interface StanfordAnnotator {

    Annotation annotate(String text);
}
