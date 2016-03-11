package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.domain.*;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.service.BotService;
import org.lucifer.abchat.service.ChatService;
import org.lucifer.abchat.service.MessageLinkService;
import org.lucifer.abchat.service.SemanticNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BotServiceImpl extends BaseServiceImpl<Bot> implements BotService {
    @Autowired
    private MessageLinkService messageLinkService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private SemanticNetworkService semanticNetworkService;

    public void remember(String stimulus, String message) {
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
        Cospeaker source = getSource(msg);
        Cospeaker target = getTarget(msg);
        Message m = new Message(chat, null, source, target);

        if (bot.getStrategy().getSemantic()) {
            String snMessage = semanticNetworkService.answer(msg);
            m.setMessage(snMessage);
        }
        if (m.getMessage() == null && bot.getStrategy().getCrowdSource()) {
            crowdSourceMessage(m, bot, msg);
        }
        if (m.getMessage() == null){
            String message = randomMessageFromStrategy(bot);
            m.setMessage(message);
            if (message == null) return null;
        }
        return m;
    }

    protected double tanimoto(String s1, String s2) {
        String[] words1 = s1.split("( )*([.,!?:;])( )*| ");
        String[] words2 = s2.split("( )*([.,!?:;])( )*| ");
        List<Integer> userdPositions = new ArrayList<Integer>();
        int equal = 0;
        for (int i = 0; i < words1.length; i++) {
            boolean found = false;
            for (int j = 0; j < words2.length; j++) {
                if (!found && words1[i].toLowerCase().equals(words2[j].toLowerCase()) && !userdPositions.contains(j)) {
                    found = true;
                    equal++;
                    userdPositions.add(j);
                }
            }
        }
        double tanimoto = ((double) equal / (words1.length + words2.length - equal));
        return (deepTanimoto(s1, s2) + tanimoto) / 2;
    }

    protected double deepTanimoto(String s1, String s2) {
        char[] chars1 = s1.toLowerCase().toCharArray();
        char[] chars2 = s2.toLowerCase().toCharArray();
        List<Integer> userdPositions = new ArrayList<Integer>();
        int equal = 0;
        for (int i = 0; i < chars1.length; i++) {
            boolean found = false;
            for (int j = 0; j < chars2.length; j++) {
                if (!found && chars1[i] == chars2[j]) {
                    found = true;
                    equal++;
                    userdPositions.add(j);
                }
            }
        }
        return (double) equal / (chars1.length + chars2.length - equal);
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
        StdMessage sm = (StdMessage)messages.toArray()[(int)(Math.random() * messages.size())];
        return sm.getMessage();
    }

    private String findClosest(MessageDTO msg) {
        final int perPage = 10;
        final long numberOfPages = (long)Math.ceil((double) messageLinkService.count() / perPage);
        Bot bot = chatService.findBotInChat(msg.getChatId());
        final double threshold = bot.getStrategy().getTanimotoThreshold();
        String stimulus = msg.getMessage();
        List<MessageLink> probableMessages = new ArrayList<>();
        for (long i = 0; i < numberOfPages; i++) {
            List<MessageLink> list = messageLinkService.getPage(i);
            for (MessageLink ml : list) {
                double concordance = tanimoto(stimulus, ml.getStimulus());
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
            double concordance = tanimoto(stimulus, ml.getStimulus());
            if (concordance > bestConcordance) {
                bestConcordance = concordance;
                message = ml.getMessage();
            }
        }
        return message;
    }

    private Cospeaker getSource(MessageDTO msg) {
        Chat chat = chatService.findById(msg.getChatId());
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {
            if (csp.getBot() != null) {
                return csp;
            }
        }
        return null;
    }

    private Cospeaker getTarget(MessageDTO msg) {
        Chat chat = chatService.findById(msg.getChatId());
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {
            if (csp.getBot() == null) {
                return csp;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println(new BotServiceImpl().tanimoto("как делы?", "как дела?"));
    }
}
