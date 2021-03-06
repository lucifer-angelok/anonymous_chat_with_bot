package org.lucifer.abchat.service;

import org.lucifer.abchat.domain.Bot;
import org.lucifer.abchat.domain.Chat;
import org.lucifer.abchat.domain.Cospeaker;
import org.lucifer.abchat.dto.ChatDTO;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.dto.UserDTO;

import java.util.Date;
import java.util.List;

public interface ChatService extends  BaseService<Chat> {
    Cospeaker enter(UserDTO usr);

    boolean cospeakerEntered(ChatDTO chat);

    void message(MessageDTO msg);

    List<MessageDTO> receive(ChatDTO ch);

    boolean bot(ChatDTO ch);

    boolean noBot(ChatDTO ch);

    Bot findBotInChat(Long chatId);

    Long countMessages(Date startDate, Date endDate);

    Integer countCorrectAnswersPercentage(Date startDate, Date endDate);

    Integer countFooledByBotPercentage(Date startDate, Date endDate);

    Long countBotChats(Date startDate, Date endDate);

    Long countChats(Date startDate, Date endDate);
}
