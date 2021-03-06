package org.lucifer.abchat.service;


import org.lucifer.abchat.domain.MessageLink;

import java.util.Date;

public interface MessageLinkService extends BaseService<MessageLink> {

    Long countBetween(Date startDate, Date endDate);
}
