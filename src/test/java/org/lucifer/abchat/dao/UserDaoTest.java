package org.lucifer.abchat.dao;


import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.lucifer.abchat.config.InfrastructureContextConfiguration;
import org.lucifer.abchat.domain.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InfrastructureContextConfiguration.class/*, TestDataContextConfiguration.class*/ })
@Transactional
public class UserDaoTest {
    @PersistenceContext
    private EntityManager entityManager;

    private User user;

    @Before
    public void setupData() {

    }

    @After
    public void tearDown() {

    }
}
