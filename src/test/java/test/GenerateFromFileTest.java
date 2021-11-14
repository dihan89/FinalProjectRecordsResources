package test;

import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import recordToResource.Runner;
import recordToResource.daoAndServiceUtils.Generator.GeneratorFromFile;
import recordToResource.daoAndServiceUtils.Generator.GeneratorFromFileImpl;
import recordToResource.daoAndServiceUtils.dao.RecordDao;
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.File;
import java.sql.SQLException;
import java.util.function.Consumer;

@SpringBootTest(classes = Runner.class)
@EntityScan("recordToResource.model")
public class GenerateFromFileTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GeneratorFromFile generatorFromFile;

    @Test
    public void generate() {
        generatorFromFile.generateResourceFromDir(
                new File("resourcesMy"));
        generatorFromFile.generateResourceNullRecordsFromDir(
                new File("schedule"));
        Assertions.assertEquals(resourceDao.findAll().size(), 3);
        Assertions.assertEquals(recordDao.findAllRecords().size(), 71);
    }

    @BeforeAll
    public static void startServer() throws SQLException {
        Server.createTcpServer().start();
    }


    @AfterEach
    @BeforeEach
    @Transactional
    public void clear() {
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User", "Resource");
    }
}

