package org.lucifer.abchat.service.impl;


import org.lucifer.abchat.config.AppGlobal;
import org.lucifer.abchat.dao.UserDao;
import org.lucifer.abchat.domain.User;
import org.lucifer.abchat.domain.UserAnswer;
import org.lucifer.abchat.dto.UserDTO;
import org.lucifer.abchat.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    public UserDTO logIn(User user) {
        UserDao dao = (UserDao) this.dao;
        if (dao.logIn(user)) {
            return new UserDTO(user.getLogin(), user.getEmail(), user.getScore());
        }
        return new UserDTO();
    }

    public boolean logInAdmin(User user) {
        UserDao dao = (UserDao) this.dao;
        return dao.isAdmin(user);
    }

    @Override
    public Long countBetween(Date startDate, Date endDate) {
        return ((UserDao)dao).countBetween(startDate, endDate);
    }

    @Override
    public Long countActive(Date startDate, Date endDate) {
        return ((UserDao)dao).countActive(startDate, endDate);
    }

    @Override
    public void active(String userLogin) {
        User user = findByLogin(userLogin);
        user.setActivityDate(new Date());
        save(user);
    }

    public boolean register(User user) {
        UserDao dao = (UserDao) this.dao;
        if (dao.register(user)) {
            dao.save(user);
            return true;
        }
        return false;
    }

    public User findByLogin(String userLogin) {
        UserDao dao = (UserDao) this.dao;
        return dao.findByLogin(userLogin);
    }

    public void recountScore(User user) {
        Long score = 0L;
        for (UserAnswer ans : user.getAnswers()) {
            if (ans.getAnswer().equals(true)) {
                score += AppGlobal.getPointsWin();
            } else {
                score -= AppGlobal.getPointsLoose();
            }
        }
        if (score < 0) score = 0L;
        user.setScore(score);
        dao.save(user);
    }

    public List<User> top() {
        UserDao dao = (UserDao) this.dao;
        return dao.top();
    }

    public UserDTO get(String login) {
        User user = findByLogin(login);
        return new UserDTO(user.getLogin(), user.getEmail(), user.getScore());
    }
}
