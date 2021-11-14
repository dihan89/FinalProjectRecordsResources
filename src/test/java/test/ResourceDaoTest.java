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
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import recordToResource.daoAndServiceUtils.dao.UserDao;
import recordToResource.model.Resource;
import recordToResource.model.User;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest(classes = {Runner.class})
@EntityScan("recordToResource.model")
public class ResourceDaoTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ResourceDao resourceDao;

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
        Resource resource = new Resource("Library", "Lenin Library.");
        boolean isOk = resourceDao.add(resource);
        Resource resource1 = entityManager.find(Resource.class, resource.getId());
        Assertions.assertEquals(resource, resource1);
        Assertions.assertTrue(isOk);
    }

    @Test
    void delete() {
        Resource resource = new Resource("Library", "Lenin Library.");
        boolean isOk = resourceDao.add(resource);
        resourceDao.delete(resource);
        Assertions.assertNull(entityManager.find(Resource.class, resource.getId()));

        resourceDao.delete(resource);
    }
    @Test
    void findByName() {
        Resource resource = new Resource("Library", "Lenin Library.");
        boolean isOk = resourceDao.add(resource);

        Resource resource1 = resourceDao.findByName(resource.getName());
        Assertions.assertNotNull(resource1);
        Assertions.assertEquals(resource, resource1);

        Assertions.assertNull(resourceDao.findByName("SomeName"));
    }

    @Test
    void findAll() {
        Resource resource1 = new Resource("Library", "Lenin Library.");
        boolean isOk = resourceDao.add(resource1);

        Resource resource2 = new Resource("ChessClub", "");
        isOk = resourceDao.add(resource2);

        List<Resource> list = resourceDao.findAll();
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 2);
        Assertions.assertTrue(list.contains(resource1));
        Assertions.assertTrue(list.contains(resource2));
    }

    @Test
    void uniqueNameTest() {
        Resource resource1 = new Resource("Library", "Lenin Library.");
        Resource resource2 = new Resource("Library", "");

        Assertions.assertTrue(resourceDao.add(resource1));
        Assertions.assertFalse(resourceDao.add(resource2));
    }
}
