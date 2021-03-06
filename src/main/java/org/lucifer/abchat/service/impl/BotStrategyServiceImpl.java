package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.dao.BotDao;
import org.lucifer.abchat.dao.StdMessageDao;
import org.lucifer.abchat.domain.Bot;
import org.lucifer.abchat.domain.BotStrategy;
import org.lucifer.abchat.domain.StdMessage;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.service.BotStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BotStrategyServiceImpl extends BaseServiceImpl<BotStrategy> implements BotStrategyService {
    @Autowired
    private StdMessageDao stdMessageDao;

    @Autowired
    private BotDao botDao;

    public List<MessageDTO> messages(Long id) {
        BotStrategy strategy = dao.findById(id);
        List<MessageDTO> result = new ArrayList<>();
        for (StdMessage message : strategy.getMessages()) {
            MessageDTO msg = new MessageDTO();
            msg.setId(message.getId());
            msg.setMessage(message.getMessage());
            result.add(msg);
        }
        return result;
    }

    public MessageDTO addMessage(Long id, StdMessage message) {
        BotStrategy bs = dao.findById(id);
        message.setStrategy(bs);
        stdMessageDao.save(message);
        return new MessageDTO(message.getMessage());
    }

    public MessageDTO message(Long id, Long msgId) {
        BotStrategy bs = findById(id);
        for (StdMessage sm : bs.getMessages()) {
            if (sm.getId().equals(msgId)) {
                MessageDTO mdto = new MessageDTO(sm.getMessage());
                mdto.setId(sm.getId());
                return mdto;
            }
        }
        return null;
    }

    @Override
    public MessageDTO deleteMessage(Long id) {
        StdMessage deleted = stdMessageDao.delete(id);
        return new MessageDTO(deleted.getMessage());
    }

    @Override
    public BotStrategy getRandomStrategy() {
        List<BotStrategy> strategies = getAll();
        return strategies.get((int) (Math.random() * strategies.size()));
    }

    @Override
    public BotStrategy save(BotStrategy strategy) {
        strategy.setErrorProb(strategy.getErrorProb() / 100);
        strategy.setSilenceProb(strategy.getSilenceProb() / 100);
        strategy.setCrowdRand(strategy.getCrowdRand() / 100);
        strategy.setInitiative(strategy.getInitiative() / 100);
        strategy.setTanimotoThreshold(strategy.getTanimotoThreshold() / 100);
        return super.save(strategy);
    }

    @Override
    public BotStrategy delete(Long id) {
        BotStrategy bs = findById(id);
        for (Bot b : bs.getBots()) {
            b.setStrategy(null);
            botDao.save(b);
        }
        return super.delete(id);
    }
}
