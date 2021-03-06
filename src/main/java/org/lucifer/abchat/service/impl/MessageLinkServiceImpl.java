package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.dao.MessageLinkDao;
import org.lucifer.abchat.domain.MessageLink;
import org.lucifer.abchat.service.MessageLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
@Service
public class MessageLinkServiceImpl extends BaseServiceImpl<MessageLink> implements MessageLinkService {
    @Override
    public Long countBetween(Date startDate, Date endDate) {
        return ((MessageLinkDao)dao).countBetween(startDate, endDate);
    }
}
