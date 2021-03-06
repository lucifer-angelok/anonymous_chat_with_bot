package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.domain.StdMessage;
import org.lucifer.abchat.service.StdMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StdMessageServiceImpl extends BaseServiceImpl<StdMessage> implements StdMessageService {
}
