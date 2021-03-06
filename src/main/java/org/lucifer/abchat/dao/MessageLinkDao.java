package org.lucifer.abchat.dao;

import org.lucifer.abchat.domain.MessageLink;

import java.util.Date;

public interface MessageLinkDao extends BaseDao<MessageLink> {
    Long countBetween(Date startDate, Date endDate);
}
