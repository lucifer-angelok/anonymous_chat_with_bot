package org.lucifer.abchat.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MESSAGE_LINK")
public class MessageLink extends  Identificator {
    @Column(name = "STIMULUS")
    private String stimulus;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "DATE")
    @Temporal(value= TemporalType.DATE)
    private Date date;

    public MessageLink() {
        this.date = new Date();
    }

    public MessageLink(String stimulus, String message) {
        this();
        this.stimulus = stimulus;
        this.message = message;
    }

    public String getStimulus() {
        return stimulus;
    }

    public void setStimulus(String stimulus) {
        this.stimulus = stimulus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
