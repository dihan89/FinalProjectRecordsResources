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

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        if (dataSource == null)
            throw new RuntimeException("DataSource is null!");
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User", "Resource");
    }
    @Test
    void add() {
        User user = new User("Ivan", "Ivanov", "89601234567");
        userDao.add(user);
        Resource resource = new Resource("Library", "Lenin Library.");
        resourceDao.add(resource);
        Date date = Date.valueOf("2023-10-21");
        Time time = Time.valueOf("15:30:00");
        Duration duration = Duration.ofMinutes(40);

        recordDao.add(user, resource, date, time, duration);

        List<Record> list =
                recordDao.findAllRecords().stream()
                        .filter(r->r.getDate().equals(date)&&r.getResource().equals(resource))
                        .collect(Collectors.toList());
        Assertions.assertEquals(list.size(), 1);

    }
    @Test
    void addUser() {
        User user = new User("Ivan", "Ivanov", "89601234567");
        userDao.add(user);
        Resource resource = new Resource("Library", "Lenin Library.");
        resourceDao.add(resource);
        Date date = Date.valueOf("2023-10-21");
        Time time = Time.valueOf("15:30:00");
        Duration duration = Duration.ofMinutes(40);

        recordDao.add(null, resource, date, time, duration);

        List<Record> list =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date);
        Integer nEmptyRecords = list.size();
        Record record = list.get(0);
        Assertions.assertNotNull(list);
        recordDao.addUser(user, record);
        Assertions.assertEquals(list.get(0).getUser(), user);
        Integer nEmptyRecords1  =
                recordDao.findEmptyRecordsByResourceAndDate(resource, date).size();
        Assertions.assertEquals(nEmptyRecords1, nEmptyRecords - 1);
    }
    @Test
    void deleteBy() {

    }
    @Test
    void delete() {

    }
    @Test
    void findRecordsByResource() {


    }
    @Test
    void findAllRecords() {

    }
    @Test
    void findAllRecordsByUser() {

    }
    @Test
    void findEmptyRecordsByResourceAndDate() {

    }
    @Test
    void addToRecord() {

    }
    @Test
    void createNullRecords() {

    }
    @Test
    void deleteUserFromRecord() {

    }

}
