package org.lucifer.abchat.service.impl;

import org.lucifer.abchat.config.AppGlobal;
import org.lucifer.abchat.ChatDao;
import org.lucifer.abchat.CospeakerDao;
import org.lucifer.abchat.MessageDao;
import org.lucifer.abchat.UserAnswerDao;
import org.lucifer.abchat.domain.*;
import org.lucifer.abchat.dto.ChatDTO;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.dto.UserDTO;
import org.lucifer.abchat.service.*;
import org.lucifer.abchat.utils.preprocessor.StringPreProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChatServiceImpl extends BaseServiceImpl<Chat> implements ChatService {
    public static final double FIFTY_PERCENT = 0.5;
    private static final int FULL_CHATROOM = 2;

    @Autowired
    private CospeakerDao cospeakerDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserAnswerDao userAnswerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private BotService botService;

    @Autowired
    private SemanticNetworkService semanticNetworkService;

    @Autowired
    private BotStrategyService botStrategyService;

    public Cospeaker enter(UserDTO usr) {
        if (Math.random() < AppGlobal.getBotProbability()) {                                              //give bot for user
            return giveBot(usr);
        }
        if (!oneInChat(usr)) {
            return putToRandom(usr);
        }
        return createNewRoom(usr);
    }

    public boolean cospeakerEntered(ChatDTO c) {
        ChatDao dao = (ChatDao) this.dao;
        Chat chat = dao.findById(c.getChatId());
        if (chat.getCospeakers().size() == FULL_CHATROOM) {
            return true;
        } else {
            boolean botAfter = AppGlobal.isAddBotAfter();
            long retries = AppGlobal.getRetries();
            if (c.getRetries() >= retries && botAfter) {
                assignBot(chat);
                return true;
            } else {
                return false;
            }
        }
    }

    public void message(MessageDTO msg) {
        Chat chat = dao.findById(msg.getChatId());
        userService.active(msg.getUserLogin());
        messageDao.save(new Message(chat, msg.getMessage(), getSource(msg),
                getTarget(msg)));                                               //message is ready
        if (bothUsers(chat)) {
            botService.remember(msg.getStimulus(), msg.getMessage());
        } else {
            boolean semantic = findBotInChat(msg.getChatId()).getStrategy().getSemantic();
            String newMessage = StringPreProcessor.littleKyrilic(msg.getMessage());
            msg.setMessage(!newMessage.equals("") ? newMessage : null);
            if (semantic) {
                semanticNetworkService.create(msg);
            }
            Message botMessage = botService.message(msg);
            if (botMessage != null) messageDao.save(botMessage);
        }
    }

    public List<MessageDTO> receive(ChatDTO ch) {                               //return list of message for user
        ChatDao dao = (ChatDao) this.dao;
        Chat chat = dao.findById(ch.getChatId());
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        Set<Message> messages = null;
        for (Cospeaker csp : cospeakers) {
            if (csp.getBot() == null) {
                if (csp.getUser().getLogin().equals(ch.getUserLogin())) {
                    messages = csp.getReceived();
                }
            }
        }
        return messagesToDTO(messages, ch.getMaxMessageId());
    }

    public boolean bot(ChatDTO ch) {
        Chat chat = dao.findById(ch.getChatId());
        User user = userService.findByLogin(ch.getUserLogin());
        if (bothUsers(chat)) {
            userAnswerDao.save(new UserAnswer(user, chat, false));
            userService.recountScore(user);
            return false;
        }
        userAnswerDao.save(new UserAnswer(user, chat, true));
        userService.recountScore(user);
        return true;
    }

    public boolean noBot(ChatDTO ch) {
        Chat chat = dao.findById(ch.getChatId());
        User user = userService.findByLogin(ch.getUserLogin());
        if (bothUsers(chat)) {
            userAnswerDao.save(new UserAnswer(user, chat, true));
            userService.recountScore(user);
            return true;
        }
        userAnswerDao.save(new UserAnswer(user, chat, false));
        userService.recountScore(user);
        return false;
    }

    public Bot findBotInChat(Long chatId) {
        Chat chat = dao.findById(chatId);
        for (Cospeaker csp : chat.getCospeakers()) {
            if (csp.getBot() != null) {
                return csp.getBot();
            }
        }
        return null;
    }

    @Override
    public Long countMessages(Date startDate, Date endDate) {
        return messageDao.countBetween(startDate, endDate);
    }

    @Override
    public Integer countCorrectAnswersPercentage(Date startDate, Date endDate) {
        Long countAnswers = userAnswerDao.countBetween(startDate, endDate);
        Long correct = userAnswerDao.correctAnswers(startDate, endDate);
        return (int) (correct.doubleValue() * 100 / countAnswers);
    }

    @Override
    public Integer countFooledByBotPercentage(Date startDate, Date endDate) {
        Long countAnswers = userAnswerDao.countBotChats(startDate, endDate);
        Long fooled = userAnswerDao.countFooledByBot(startDate, endDate);
        return (int) (fooled.doubleValue() * 100 / countAnswers);
    }

    protected boolean bothUsers(Chat c) {
        if (c.getCospeakers().size() < FULL_CHATROOM) return false;
        Set<Cospeaker> cospeakers = c.getCospeakers();
        boolean both = true;
        for (Cospeaker csp : cospeakers) {
            if (csp.getUser() == null) {
                both = false;
            }
        }
        return both;
    }

    private void assignBot(Chat chat) {
        Bot bot = new Bot();
        bot.setStrategy(botStrategyService.getRandomStrategy());
        Cospeaker botCospeaker = new Cospeaker(null, bot);
        botCospeaker.setChat(chat);
        cospeakerDao.save(botCospeaker);
    }

    private Cospeaker giveBot(UserDTO usr) {
        User user = userService.findByLogin(usr.getLogin());
        Cospeaker userCospeaker = new Cospeaker(user, null);                    //cospeaker for entering user
        Chat chat = new Chat();
        userCospeaker.setChat(chat);
        cospeakerDao.save(userCospeaker);
        assignBot(chat);
        return userCospeaker;
    }

    private boolean oneInChat(UserDTO usr) {
        ChatDao dao = (ChatDao) this.dao;
        List<Long> free = dao.freeRooms();                                      //id of free rooms (1 cospeaker)
        User user = userService.findByLogin(usr.getLogin());
        Cospeaker userCospeaker = new Cospeaker(user, null);                    //cospeaker for entering user
        boolean oneInChat = true;                                               //if user only 1 in free chatrooms create new
        for (Long fid : free) {
            Chat room = dao.findById(fid);
            Set<Cospeaker> cospeakerSet = room.getCospeakers();
            for (Cospeaker c : cospeakerSet) {
                if (!c.equals(userCospeaker)) {
                    oneInChat = false;
                }
            }
        }
        return oneInChat;
    }

    private List<MessageDTO> messagesToDTO(Set<Message> messages, Long maxId) {
        List<MessageDTO> list = new ArrayList<>();                   //place messages in DTO object and send to user
        for (Message m : messages) {
            if (m.getId() > maxId) {
                MessageDTO md = new MessageDTO();
                md.setMessage(m.getMessage());
                md.setId(m.getId());
                list.add(md);
            }
        }
        return list;
    }

    private Cospeaker createNewRoom(UserDTO usr) {
        User user = userService.findByLogin(usr.getLogin());
        Cospeaker userCospeaker = new Cospeaker(user, null);                    //cospeaker for entering user
        Chat chat = new Chat();                                                 //new chatroom for only users
        userCospeaker.setChat(chat);
        cospeakerDao.save(userCospeaker);
        return userCospeaker;
    }

    private Cospeaker putToRandom(UserDTO usr) {
        ChatDao dao = (ChatDao) this.dao;
        List<Long> free = dao.freeRooms();                                      //id of free rooms (1 cospeaker)
        if (free.size() == 0) return null;
        User user = userService.findByLogin(usr.getLogin());
        Cospeaker userCospeaker = new Cospeaker(user, null);                    //cospeaker for entering user
        Chat randomRoom;
        do {
            Long randomRoomId = free.get((int) (Math.random() * free.size()));
            randomRoom = dao.findById(randomRoomId);
        } while (randomRoom.getCospeakers().contains(userCospeaker));
        userCospeaker.setChat(randomRoom);
        cospeakerDao.save(userCospeaker);
        return userCospeaker;
    }

    private Cospeaker getSource(MessageDTO msg) {
        Chat chat = dao.findById(msg.getChatId());
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {                                      //find target and source for msg
            if (csp.getBot() == null) {
                if (csp.getUser().getLogin().equals(msg.getUserLogin())) {
                    return csp;
                }
            }
        }
        return null;
    }

    private Cospeaker getTarget(MessageDTO msg) {
        Chat chat = dao.findById(msg.getChatId());
        Set<Cospeaker> cospeakers = chat.getCospeakers();
        for (Cospeaker csp : cospeakers) {                                      //find target and source for msg
            if (csp.getBot() == null) {
                if (!csp.getUser().getLogin().equals(msg.getUserLogin())) {
                    return csp;
                }
            } else {
                return csp;
            }
        }
        return null;
    }
}
