package org.librairy.service.nlp.service;

import com.google.common.base.Strings;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.annotators.StanfordAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordLemmaTokenizer;
import org.librairy.service.nlp.annotators.Token;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CoreNLPService {

    private static final Logger LOG = LoggerFactory.getLogger(CoreNLPService.class);


    StanfordAnnotatorEN annotator;


    StanfordLemmaTokenizer tokenizer;

    public void setAnnotator(StanfordAnnotatorEN annotator) {
        this.annotator = annotator;
    }

    public void setTokenizer(StanfordLemmaTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public String process(String text, List<PoS> filter, Form form) throws AvroRemoteException {
        if (Strings.isNullOrEmpty(text)) return "";

        StringBuilder result = new StringBuilder();
        Matcher matcher = Pattern.compile(".{1,1000}(,|.$)").matcher(text);
        Map<String,StringBuilder> tokenMap = new HashMap<String,StringBuilder>();
        while (matcher.find()){

            String partialContent = matcher.group();
            Instant startAnnotation = Instant.now();
            Annotation annotation = annotator.annotate(partialContent);
            Instant endAnnotation = Instant.now();
            LOG.debug("Annotated  in: " +
                    ChronoUnit.MINUTES.between(startAnnotation,endAnnotation) + "min " +
                    (ChronoUnit.SECONDS.between(startAnnotation,endAnnotation)%60) + "secs");

            try{
                Instant startTokenizer = Instant.now();
                List<Token> tokenList = tokenizer.tokenize(annotation);
                Instant endTokenizer = Instant.now();
                LOG.debug("Parsed  to " + tokenList.size() + "  in: " +
                        ChronoUnit.MINUTES.between(startTokenizer,endTokenizer) + "min " + (ChronoUnit.SECONDS.between(startTokenizer,endTokenizer)%60) + "secs");
                String tokens = tokenList
                        .stream()
                        .filter(token -> token.isValid() && (filter.isEmpty() || filter.contains(token.getPos())))
                        .map(token -> token.getWord())
                        .collect(Collectors.joining(" "))
                        ;

                result.append(tokens);

            }catch (Exception e){
                LOG.error("Error tokenizing", e);
            }

        }
        return result.toString();
    }
}
