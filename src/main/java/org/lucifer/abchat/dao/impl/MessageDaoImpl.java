package org.lucifer.abchat.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.lucifer.abchat.dao.MessageDao;
import org.lucifer.abchat.domain.Message;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MessageDaoImpl extends BaseDaoImpl<Message> implements MessageDao {

    @Override
    public Long countBetween(Date startDate, Date endDate) {
        Session session = getSession();
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        Query query = session.createQuery(
                "select count(*) " +
                        "from Message where date between :startDate " +
                        "AND :endDate " +
                        "AND source.user is not null");
        query.setDate("startDate", start);
        query.setDate("endDate", end);
        return (Long) query.uniqueResult();
    }
}
