package test;

import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import recordToResource.Runner;
import recordToResource.daoAndServiceUtils.dao.UserDao;
import recordToResource.model.User;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest(classes = {Runner.class})
@EntityScan("recordToResource.model")
public class UserDaoTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    public static void startServer() throws SQLException {
        Server.createTcpServer().start();
    }

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User", "Resource");
    }

    @Test
    void add() {
        User user = new User("Ivan", "Ivanov", "89601234567");
        boolean isOk = userDao.add(user);
        User user1 = entityManager.find(User.class, user.getId());
        Assertions.assertEquals(user, user1);
        Assertions.assertTrue(isOk);
    }

    @Test
    void delete() {
        User user1 = new User("Ivan", "Ivanov", "89601234567");
        User user2 = new User("Petr", "Petrov", "89601111111");
        userDao.add(user1);
        userDao.add(user2);
        userDao.delete(user1);
        Assertions.assertNull(entityManager.find(User.class, user1.getId()));
        Assertions.assertNotNull(entityManager.find(User.class, user2.getId()));
        Assertions.assertNotNull(user1.getId());
        Assertions.assertNotEquals(user1.getId(), 0);

        userDao.delete(user1);
    }

    @Test
    void findByPhone() {
        String phone = "89601234567";
        User user = new User("Ivan", "Ivanov", phone);
        userDao.add(user);
        User foundUser = userDao.findByPhone(phone);
        Assertions.assertNotNull(userDao.findByPhone(phone));
        Assertions.assertEquals(foundUser, user);

        Assertions.assertNull(userDao.findByPhone("1234567890"));
    }

    @Test
    void get() {
        User user = new User("Ivan", "Ivanov", "89601234567");
        userDao.add(user);

        User user1 = userDao.get("Ivan", "Ivanov", "89601234567");
        Assertions.assertEquals(user, user1);
        Assertions.assertEquals(user.getId(), user1.getId());
        Assertions.assertEquals(user.getName(), user1.getName());

        User user2 = userDao.get("Ivan", "Ivanov", "89601234560");
        Assertions.assertNotNull(user2);
        Assertions.assertNotEquals(user1, user2);

        Assertions.assertEquals(user, entityManager.find(User.class, user1.getId()));
        Assertions.assertNotEquals(user, entityManager.find(User.class, user2.getId()));

    }

    @Test
    void find() {
        User user = new User("Ivan", "Ivanov", "89601234567");
        userDao.add(user);
        User user1 = userDao.find("Ivan", "Ivanov", "89601234567");
        Assertions.assertNotNull(user1);
        Assertions.assertEquals(user, user1);
        Assertions.assertNull(userDao.find("Ivan", "Ivanov", "8960000000"));
    }

    @Test
    void checkUniquePhones() {
        User user1 = new User("Ivan", "Ivanov", "89601234567");
        Assertions.assertTrue(userDao.add(user1));
        User user2 = new User("Ivan", "Ivanov", "89601234567");
        Assertions.assertFalse(userDao.add(user2));
    }
}

