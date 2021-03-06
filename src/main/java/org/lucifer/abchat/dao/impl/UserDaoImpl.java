package org.lucifer.abchat.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.lucifer.abchat.dao.UserDao;
import org.lucifer.abchat.domain.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class UserDaoImpl extends BaseDaoImpl<User> implements UserDao {
    public boolean logIn(User user) {
        Session session = getSession();
        Query query = session.createQuery(
                "select count(*) from User where login='"
                        + user.getLogin() + "' and password='"
                        + user.getPassword() + "'");
        long result = (Long) query.uniqueResult();
        return result != 0;
    }

    public boolean register(User user) {
        Session session = getSession();
        Query query = session.createQuery(
                "select count(login) from User where login=:login");
        query.setParameter("login", user.getLogin());
        long result = (Long) query.uniqueResult();
        return result == 0;
    }

    public User findByLogin(String userLogin) {
        Session session = getSession();
        Query query = session.createQuery(
                "from User where login=:login");
        query.setParameter("login", userLogin);
        User result = (User) query.uniqueResult();
        return result;
    }

    public List<User> top() {
        Session session = getSession();
        Query query = session.createQuery(
                "from User order by score desc");
        final int topCount = 10;
        final int start = 0;
        query.setFirstResult(start);
        query.setMaxResults(topCount);
        List<User> result = (List<User>) query.list();
        return result;
    }

    public boolean isAdmin(User user) {
        Session session = getSession();
        Query query = session.createQuery(
                "select count(*) from User where login='"
                        + user.getLogin() + "' and password='"
                        + user.getPassword() + "' and role='admin'");
        long result = (Long) query.uniqueResult();
        return result != 0;
    }

    @Override
    public Long countBetween(Date startDate, Date endDate) {
        Session session = getSession();
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        Query query = session.createQuery(
                "select count(*) " +
                        "from User where registerDate between :startDate " +
                        "AND :endDate");
        query.setDate("startDate", start);
        query.setDate("endDate", end);
        return (Long) query.uniqueResult();
    }

    @Override
    public Long countActive(Date startDate, Date endDate) {
        Session session = getSession();
        java.sql.Date start = new java.sql.Date(startDate.getTime());
        java.sql.Date end = new java.sql.Date(endDate.getTime());
        Query query = session.createQuery(
                "select count(*) " +
                        "from User " +
                        "where activityDate between :startDate and :endDate");
        query.setDate("startDate", start);
        query.setDate("endDate", end);
        return (Long) query.uniqueResult();
    }
}
