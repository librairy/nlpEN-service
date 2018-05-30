package org.librairy.service.nlp.service;

import com.google.common.base.Strings;
import edu.stanford.nlp.pipeline.Annotation;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.annotators.StanfordAnnotatorEN;
import org.librairy.service.nlp.annotators.StanfordLemmaTokenizer;
import org.librairy.service.nlp.facade.model.Form;
import org.librairy.service.nlp.facade.model.PoS;
import org.librairy.service.nlp.facade.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        return tokenize(text, filter, form).stream()
                .filter(token -> filter.isEmpty() || filter.contains(token.getPos()))
                .map(token -> (form.equals(Form.LEMMA) ? token.getLemma() : token.getTarget()))
                .collect(Collectors.joining(" "));
    }

    public List<org.librairy.service.nlp.facade.model.Token> group(String text, List<PoS> filter) throws AvroRemoteException {
        if (Strings.isNullOrEmpty(text)) return Collections.emptyList();

        Map<Token, Long> grouped = tokenize(text, filter, Form.LEMMA).stream()
                .filter(token -> filter.isEmpty() || filter.contains(token.getPos()))
                .collect(Collectors.groupingBy(token -> token, Collectors.counting()));

        return grouped.entrySet().stream().map( entry -> {
            org.librairy.service.nlp.facade.model.Token token = entry.getKey();
            token.setFreq(entry.getValue());
            return token;
        }).collect(Collectors.toList());
    }

    private List<Token> tokenize(String text, List<PoS> filter, Form form) throws AvroRemoteException {
        if (Strings.isNullOrEmpty(text)) return Collections.emptyList();

        List<Token> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile(".{1,1000}(,|.$)").matcher(text);
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
                tokens.addAll(tokenList);
            }catch (Exception e){
                LOG.error("Error tokenizing", e);
            }

        }
        return tokens;
    }
}
