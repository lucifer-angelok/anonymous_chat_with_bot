package org.lucifer.abchat.dao;

import org.lucifer.abchat.domain.UserAnswer;

import java.util.Date;

public interface UserAnswerDao extends BaseDao<UserAnswer> {
    Long correctAnswers(Date startDate, Date endDate);

    Long countBetween(Date startDate, Date endDate);

    Long countFooledByBot(Date startDate, Date endDate);

    Long countBotChats(Date startDate, Date endDate);
}