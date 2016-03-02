package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.domain.MessageLink;
import org.lucifer.abchat.service.MessageLinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MessageLinkServiceImpl extends BaseServiceImpl<MessageLink> implements MessageLinkService {
}
