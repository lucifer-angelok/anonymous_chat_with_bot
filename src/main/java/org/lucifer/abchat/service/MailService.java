package org.lucifer.abchat.service;


public interface MailService {
    void send(String toAddress, String subject, String msgBody);

    boolean validateEmail(String email);
}
