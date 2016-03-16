package org.lucifer.abchat.dao;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lucifer.abchat.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Before
    public void setupData() {
        User u = new User();
        u.setRole(User.ROLE_ADMIN);
        u.setId(1L);
        u.setScore(10L);
        u.setEmail("example@mail.com");
        u.setLogin("login");
        u.setPassword("password");
        userDao.save(u);
    }

    @After
    public void tearDown() {
        userDao.delete(1L);
    }

    @Test
    public void testCount() {
        assertEquals(userDao.count().equals(1), true);
    }
}
