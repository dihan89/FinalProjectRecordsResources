package test;

import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import recordToResource.Runner;
import recordToResource.daoAndServiceUtils.dao.RecordDao;
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import recordToResource.daoAndServiceUtils.dao.UserDao;
import recordToResource.model.Record;
import recordToResource.model.Resource;
import recordToResource.model.User;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

@SpringBootTest(classes = Runner.class)
@EntityScan("recordToResource.model")
public class ElemetaryWorkWithDBTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RecordDao recordDao;



    @AfterEach
    public void clear() {
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User",  "Resource");
    }

    private void doInTransaction(Consumer<EntityManager> consumer) {
        transactionTemplate.executeWithoutResult(transactionStatus -> consumer.accept(entityManager));
    }
    @Test
    public void createUsers(){
        User userA = new User();
        userA.setSurname("Ivanov");
        userA.setName("Ivan");
        userA.setPhone("123456");

        User userB = new User();
        userB.setSurname("Ivanov");
        userB.setName("Petr");
        userB.setPhone("234567");


        Resource resourceA = new Resource();
        resourceA.setName("Hospital");
        resourceA.setDescription("This is hospital");
        Resource resourceB = new Resource();
        resourceB.setName("Library");
        resourceB.setDescription("This is library");
        Record record1 = new Record();
        record1.setUser(userA);
        record1.setResource(resourceB);
        record1.setTimeStart(Time.valueOf("12:05:59"));
        record1.setDate(Date.valueOf("2022-11-28"));
        record1.setDuration(Duration.ofMinutes(20));
        Record record2 = new Record();
        record2.setUser(userA);
        record2.setResource(resourceB);
        record2.setTimeStart(Time.valueOf(LocalTime.now()));
        record2.setDate(Date.valueOf(LocalDate.now()));
        record2.setDuration(Duration.ofMinutes(30));

        doInTransaction(em->{
            em.persist(userA);
            em.persist(userB);
            em.persist(resourceA);
            em.persist(resourceB);
            em.persist(record1);
            em.persist(record2);
            recordDao.createNullRecords
                    (Date.valueOf("2022-12-10"),
                            Time.valueOf("23:00:00"),
                            5,
                            Duration.ofMinutes(25),
                            resourceB );
        });
        List<Record> list = recordDao.findAllRecords();
        Assertions.assertEquals(list.size(),7);
        Assertions.assertEquals(userDao.findByPhone("234567"),userB);
        Assertions.assertFalse(userA.getId() == 0);
        Assertions.assertNotNull(userA.getId());
        Assertions.assertFalse(resourceA.getId() == 0);
        Assertions.assertNotNull(resourceA.getId());


        doInTransaction(em -> {
           User user1 = em.find(User.class, userA.getId());
            User user2 = em.find(User.class, userB.getId());
            Assertions.assertEquals(user1, userA);
            Assertions.assertNotEquals(user2, userA);
        });

        doInTransaction(em -> {
            User user1 = userDao.findByPhone("123456");
            System.out.println("Vot "+ user1);
            Assertions.assertEquals(resourceDao.findAll().size(),2);

        });
    }
    @BeforeEach
    public void startServer() throws SQLException {
        Server.createTcpServer().start();
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User",  "Resource");
    }
}
