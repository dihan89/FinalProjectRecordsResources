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
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {Runner.class})
@EntityScan("recordToResource.model")
public class RecordDaoTest {

    User user, user1;
    Resource resource, resource1;
    Date date;
    Time time, time1, time2;
    Duration duration;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    public static void startServer() throws SQLException {
        Server.createTcpServer().start();
    }

    @Transactional
    public void initialize() {
        user = new User("Ivan", "Ivanov", "89601234567");
        user1 = new User("Petr", "Petrov", "89601234568");
        userDao.add(user);
        userDao.add(user1);

        resource = new Resource("Library", "Lenin Library.");
        resource1 = new Resource("ChessClub", "");
        resourceDao.add(resource);
        resourceDao.add(resource1);

        date = Date.valueOf("2023-10-21");
        time = Time.valueOf("15:30:00");
        time1 = Time.valueOf("16:10:00");
        time2 = Time.valueOf("16:50:00");
        duration = Duration.ofMinutes(40);
    }

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        if (dataSource == null)
            throw new RuntimeException("DataSource is null!");
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User", "Resource");

        initialize();
    }

    @Test
    void add() {
        recordDao.add(user, resource, date, time, duration);

        List<Record> list =
                recordDao.findAllRecords().stream()
                        .filter(r -> r.getDate().equals(date) && r.getResource().equals(resource))
                        .collect(Collectors.toList());
        Assertions.assertEquals(list.size(), 1);

    }

    @Test
    void findEmptyRecordsByResourceAndDate() {
        recordDao.add(null, resource, date, time, duration);
        List<Record> list =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date);
        Assertions.assertEquals(list.size(), 1);
        Assertions.assertNull(list.get(0).getUser());
        Assertions.assertEquals(list.get(0).getResource(), resource);
        Assertions.assertEquals(list.get(0).getDate(), date);
        Assertions.assertEquals(list.get(0).getTimeStart(), time);
        Assertions.assertEquals(list.get(0).getDuration(), duration);
    }

    @Test
    void addUser() {
        recordDao.add(null, resource, date, time, duration);
        List<Record> list =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date);
        Integer nEmptyRecords = list.size();
        Record record = list.get(0);
        Assertions.assertNotNull(list);
        recordDao.addUser(user, record);
        Assertions.assertEquals(list.get(0).getUser(), user);
        Integer nEmptyRecords1 =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date).size();
        Assertions.assertEquals(nEmptyRecords1, nEmptyRecords - 1);
    }

    @Test
    void deleteUserFromRecord() {
        recordDao.add(null, resource, date, time, duration);
        Record record =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date).get(0);

        recordDao.addUser(user, record);
        recordDao.deleteUserFromRecord(record);
        List<Record> list =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date);

        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getUser(), null);
    }

    @Test
    void findAllRecords() {
        recordDao.add(user, resource, date, time, duration);
        recordDao.add(null, resource, date, time1, duration);
        List<Record> list =
                recordDao.findAllRecords();
        Assertions.assertEquals(list.size(), 2);
    }

    @Test
    void findAllRecordsByUser() {
        recordDao.add(user, resource, date, time, duration);
        recordDao.add(user, resource1, date, time1, duration);
        recordDao.add(user1, resource, date, time2, duration);
        List<Record> list =
                recordDao.findRecordsByUser(user);
        Assertions.assertEquals(list.size(), 2);
        Assertions.assertEquals(list.get(0).getUser(), user);
        Assertions.assertEquals(list.get(1).getUser(), user);
        Assertions.assertEquals(list.get(0).getResource(), resource);
        Assertions.assertEquals(list.get(1).getResource(), resource1);
    }

    @Test
    void findRecordsByResource() {
        recordDao.add(user, resource, date, time, duration);
        recordDao.add(user, resource1, date, time1, duration);
        recordDao.add(user1, resource, date, time2, duration);

        List<Record> listOfResource1 =
                recordDao.findRecordsByResource(resource1);
        Assertions.assertEquals(listOfResource1.size(), 1);
        Assertions.assertEquals(listOfResource1.get(0).getUser(), user);

        List<Record> listOfResource =
                recordDao.findRecordsByResource(resource);
        Assertions.assertEquals(listOfResource.size(), 2);
        Assertions.assertEquals(listOfResource.get(0).getDate(), date);
        Assertions.assertTrue(listOfResource.get(0).getUser().equals(user) ||
                listOfResource.get(0).getUser().equals(user1));
    }

    @Test
    void createNullRecords() {
        boolean isOk = recordDao.add(null, resource, date, time, duration);
        Assertions.assertTrue(isOk);
        isOk = recordDao.add(null, null, date, time, duration);
        Assertions.assertFalse(isOk);
        isOk = recordDao.add(null, null, null, time, duration);
        Assertions.assertFalse(isOk);
        isOk = recordDao.add(null, null, null, null, duration);
        Assertions.assertFalse(isOk);
        isOk = recordDao.add(null, null, null, null, null);

        Record record =
                recordDao.findEmptyRecordsByResourceAndDate(
                        resource, date).get(0);
        Assertions.assertEquals(record.getResource(), resource);
        Assertions.assertEquals(record.getDate(), date);
        Assertions.assertNull(record.getUser());
    }

    @Test
    void deleteByRecord() {
        boolean isOk = recordDao.add(user, resource, date, time, duration);
        Record record = recordDao.findAllRecords().get(0);
        Assertions.assertTrue(recordDao.delete(record));
        Assertions.assertFalse(recordDao.delete(record));
    }

    @Test
    void delete() {
        recordDao.add(user, resource, date, time, duration);
        Assertions.assertTrue(recordDao.delete(user, resource, date, time));
        Assertions.assertFalse(recordDao.delete(user, resource, date, time));
    }

//    @Test
//    void addUserToRecord() {
//        recordDao.add(user, resource, date, time, duration);
//        recordDao.add(null, resource1, date, time1, duration);
//        recordDao.add(null, resource, date, time2, duration);
//        recordDao.add(null, resource1, date, time2, duration);
//
//        List<Record> list = recordDao.findAllRecords();
//
//        Assertions.assertEquals(
//                recordDao.addUserToRecord(list, 0, user1), false);
//        Assertions.assertEquals(
//                recordDao.addUserToRecord(list, 1, user1), true);
//        Assertions.assertEquals(
//                recordDao.addUserToRecord(list, 2, user1), true);
//        Assertions.assertEquals(
//                recordDao.addUserToRecord(list, 3, user), true);
//        Assertions.assertEquals(
//                recordDao.findAllRecordsByUser(user1).size(),2
//        );
//        Assertions.assertEquals(
//                recordDao.findAllRecordsByUser(user).size(),2
//        );
//        Assertions.assertEquals(
//                recordDao.findEmptyRecordsByResourceAndDate(
//                        resource,date).size(),0);
//        Assertions.assertEquals(
//                recordDao.findEmptyRecordsByResourceAndDate(
//                        resource1,date).size(),0);
//    }


}
