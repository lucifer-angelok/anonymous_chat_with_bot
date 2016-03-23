package org.lucifer.abchat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "BOT_STRATEGY")
public class BotStrategy extends Identificator implements Serializable {
    @Column(name = "NAME")
    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "strategy", cascade = CascadeType.ALL)
    private Set<StdMessage> messages;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "strategy")
    private Set<Bot> bots;

    @Column(name = "SILENCE_PROB")
    Double silenceProb;

    @Column(name = "MSG_LIMIT")
    Long msgLimit;

    @Column(name = "ERROR_PROB")
    Double errorProb;

    @Column(name = "TANIMOTO_THRESHOLD")
    Double tanimotoThreshold;

    @Column(name = "CROWD_RAND")
    Double crowdRand;

    @Column(name = "INITIATIVE")
    Double initiative;

    @Column(name = "SEMANTIC")
    Boolean semantic;

    @Column(name = "CROWD_SOURCE")
    Boolean crowdSource;

    public BotStrategy() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Bot> getBots() {
        return bots;
    }

    public void setBots(Set<Bot> bots) {
        this.bots = bots;
    }

    public Set<StdMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<StdMessage> messages) {
        this.messages = messages;
    }

    public Double getSilenceProb() {
        return silenceProb;
    }

    public void setSilenceProb(Double silenceProb) {
        this.silenceProb = silenceProb;
    }

    public Long getMsgLimit() {
        return msgLimit;
    }

    public void setMsgLimit(Long msgLimit) {
        this.msgLimit = msgLimit;
    }

    public Double getErrorProb() {
        return errorProb;
    }

    public void setErrorProb(Double errorProb) {
        this.errorProb = errorProb;
    }

    public Double getTanimotoThreshold() {
        return tanimotoThreshold;
    }

    public void setTanimotoThreshold(Double tanimotoThreshold) {
        this.tanimotoThreshold = tanimotoThreshold;
    }

    public Double getCrowdRand() {
        return crowdRand;
    }

    public void setCrowdRand(Double crowdRand) {
        this.crowdRand = crowdRand;
    }

    public Boolean getSemantic() {
        return semantic;
    }

    public void setSemantic(Boolean semantic) {
        this.semantic = semantic;
    }

    public Boolean getCrowdSource() {
        return crowdSource;
    }

    public void setCrowdSource(Boolean crowdSource) {
        this.crowdSource = crowdSource;
    }

    public Double getInitiative() {
        return initiative;
    }

    public void setInitiative(Double initiative) {
        this.initiative = initiative;
    }
}
