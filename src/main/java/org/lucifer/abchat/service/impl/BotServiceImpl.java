package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.domain.*;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.service.BotService;
import org.lucifer.abchat.service.ChatService;
import org.lucifer.abchat.service.MessageLinkService;
import org.lucifer.abchat.service.SemanticNetworkService;
import org.lucifer.abchat.utils.preprocessor.StringPreProcessor;
import org.lucifer.abchat.utils.preprocessor.Tanimoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BotServiceImpl extends BaseServiceImpl<Bot> implements BotService {
    private static final double MINIMAL_PROB = 0.01;
    private static final String EMPTY_STRING = "";

    @Autowired
    private MessageLinkService messageLinkService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SemanticNetworkService semanticNetworkService;

    public void remember(String stimulus, String message) {
        if (stimulus == null || stimulus.trim().equals(EMPTY_STRING)) return;
        MessageLink ml = new MessageLink(stimulus, message);
        messageLinkService.save(ml);
    }

    public Message message(MessageDTO msg) {
        Bot bot = chatService.findBotInChat(msg.getChatId());

        if (bot.getStrategy() == null) return null;
        double silent = bot.getStrategy().getSilenceProb();
        if (Math.random() < silent) return null;                       //keep silence
        Long msgLimit = bot.getStrategy().getMsgLimit();
        if (bot.getCospeaker().getSended().size() >= msgLimit
                && msgLimit != 0) return null;

        Chat chat = chatService.findById(msg.getChatId());
        Cospeaker source = getSource(msg.getChatId());
        Cospeaker target = getTarget(msg.getChatId());
        Message m = new Message(chat, null, source, target);

        if (bot.getStrategy().getSemantic() && StringPreProcessor
                .canBeReplacedToLK(msg.getMessage())) {
            String snMessage = semanticNetworkService.answer(msg);
            m.setMessage(snMessage);
        }
        if (m.getMessage() == null && bot.getStrategy().getCrowdSource()) {
            crowdSourceMessage(m, bot, msg);
        }
        if (m.getMessage() == null) {
            String message = randomMessageFromStrategy(bot);
            m.setMessage(message);
            if (message == null) return null;
        }
        double mistake = bot.getStrategy().getErrorProb();
        String newMessage = StringPreProcessor.makeErrors(m.getMessage(),
                mistake);
        if (mistake >= MINIMAL_PROB) m.setMessage(newMessage);
        return m;
    }

    @Override
    public Message initiativeMessage(Long chatId) {
        Chat chat = chatService.findById(chatId);
        Bot bot = chatService.findBotInChat(chatId);
        if (bot.getStrategy() == null) return null;
        double silent = bot.getStrategy().getSilenceProb();
        if (Math.random() < silent) return null;                       //keep silence
        Long msgLimit = bot.getStrategy().getMsgLimit();
        if (bot.getCospeaker().getSended().size() >= msgLimit
                && msgLimit != 0) return null;

        Cospeaker source = getSource(chatId);
        Cospeaker target = getTarget(chatId);
        Message m = new Message(chat, null, source, target);
        if (bot.getStrategy().getCrowdSource()) {
            crowdSourceMessage(m, bot, randomMessage(bot.getCospeaker()));
        }
        if (m.getMessage() == null) {
            String message = randomMessageFromStrategy(bot);
            m.setMessage(message);
            if (message == null) return null;
        }
        double mistake = bot.getStrategy().getErrorProb();
        String newMessage = StringPreProcessor.makeErrors(m.getMessage(),
                mistake);
        if (mistake >= MINIMAL_PROB) m.setMessage(newMessage);
        return m;
    }

    private MessageDTO randomMessage(Cospeaker botCSP) {
        MessageDTO result = new MessageDTO();
        result.setChatId(botCSP.getChat().getId());
        if (botCSP.getReceived() == null || botCSP.getReceived().size() == 0) {
            result.setMessage(EMPTY_STRING);
        } else {
            Message message = (Message)botCSP.getReceived()
                    .toArray()[(int) (botCSP.getReceived().size()
                    * Math.random())];
            String messageString = message.getMessage();
            result.setMessage(messageString);
        }
        return result;
    }

    private void crowdSourceMessage(Message m, Bot bot, MessageDTO msg) {
        if (messageLinkService.count() == 0) {
            String message = randomMessageFromStrategy(bot);
            m.setMessage(message);
        } else {
            String message = findClosest(msg);
            m.setMessage(message);
        }
    }

    private String randomMessageFromStrategy(Bot bot) {
        Set<StdMessage> messages = bot.getStrategy().getMessages();
        if (messages.size() == 0) return null;
        StdMessage sm = (StdMessage) messages
                .toArray()[(int) (Math.random() * messages.size())];
        return sm.getMessage();
    }

    private String findClosest(MessageDTO msg) {
        final long numberOfPages = messageLinkService.countPages();
        Bot bot = chatService.findBotInChat(msg.getChatId());
        final double threshold = bot.getStrategy().getTanimotoThreshold();
        String stimulus = msg.getMessage();
        List<MessageLink> probableMessages = new ArrayList<>();
        for (long i = 0; i < numberOfPages; i++) {
            List<MessageLink> list = messageLinkService.getPage(i);
            for (MessageLink ml : list) {
                double concordance = Tanimoto.tanimoto(stimulus, ml.getStimulus());
                if (concordance > threshold) {
                    probableMessages.add(ml);
                }
            }
        }
        return chooseMessage(probableMessages, stimulus, bot);
    }

    private String chooseMessage(List<MessageLink> probableMessages, String stimulus, Bot bot) {
        if (probableMessages.size() == 0) return randomMessageFromStrategy(bot);
        double crowdRand = bot.getStrategy().getCrowdRand();
        if (Math.random() < crowdRand) {
            return probableMessages.get((int) (Math.random() * probableMessages.size())).getMessage();
        } else {
            return bestFit(probableMessages, stimulus);
        }
    }

    private String bestFit(List<MessageLink> probableMessages, String stimulus) {
        double bestConcordance = 0;
        String message = null;
        for (MessageLink ml : probableMessages) {
            double concordance = Tanimoto.tanimoto(stimulus, ml.getStimulus());
            if (concordance > bestConcordance) {
                bestConcordance = concordance;
                message = ml.getMessage();
            }
        }
        return message;
    }

    private Cospeaker getSource(Long chatId) {
        Chat chat = chatService.findById(chatId);
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {
            if (csp.getBot() != null) {
                return csp;
            }
        }
        return null;
    }

    private Cospeaker getTarget(Long chatId) {
        Chat chat = chatService.findById(chatId);
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {
            if (csp.getBot() == null) {
                return csp;
            }
        }
        return null;
    }

}
