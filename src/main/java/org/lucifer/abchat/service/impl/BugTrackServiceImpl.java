package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.domain.BugTrack;
import org.lucifer.abchat.service.BugTrackService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BugTrackServiceImpl extends BaseServiceImpl<BugTrack> implements BugTrackService {
}
