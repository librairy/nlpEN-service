package org.librairy.service.nlp.annotators;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import org.librairy.service.nlp.facade.model.PoS;

import java.io.Serializable;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Token implements Serializable {

    String word = "";

    PoS pos = PoS.NOUN;

    boolean stopWord;
    boolean isEntity = false;

    private final Escaper escaper = Escapers.builder()
            .addEscape('\'',"")
            .addEscape('\"',"")
            .addEscape('('," ")
            .addEscape(')'," ")
            .addEscape('['," ")
            .addEscape(']'," ")
            .build();

    public boolean isValid(){
        return !stopWord
                && word.length()>2;
    }

    public void setWord(String word){
        this.word = escaper.escape(word);
    }

    public String getWord() {
        return word;
    }

    public PoS getPos() {
        return pos;
    }

    public void setPos(PoS pos) {
        this.pos = pos;
    }

    public boolean isStopWord() {
        return stopWord;
    }

    public void setStopWord(boolean stopWord) {
        this.stopWord = stopWord;
    }
}
