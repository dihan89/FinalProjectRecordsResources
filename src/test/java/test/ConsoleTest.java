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
import recordToResource.daoAndServiceUtils.*;
import recordToResource.daoAndServiceUtils.Generator.GeneratorFromFile;
import recordToResource.daoAndServiceUtils.dao.RecordDao;
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import recordToResource.daoAndServiceUtils.dao.UserDao;
import recordToResource.model.Record;
import recordToResource.model.Resource;
import recordToResource.model.User;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;


@SpringBootTest(classes = {Runner.class})
@EntityScan("recordToResource.model")
public class ConsoleTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ResourceDao resourceDao;

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private GeneratorFromFile generatorFromFile;


    @AfterEach
    @Transactional
    public void clear() {
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User", "Resource");
    }
    public void  somethingToDO(){
        ValidatorTransformer validatorTransformer = new ValidatorTransformer();

        String userName = validatorTransformer.getWordOrNull("Dmitry");
        System.out.println("Enter your name: " + userName);
        String userSurname = validatorTransformer.getWordOrNull("Khanin");
        System.out.println("Enter your surname: " + userSurname);
        String userPhone = validatorTransformer.getPhoneOrNull("123456");
        System.out.println("Enter your phone's number" + userPhone);

        User user = userDao.get(userName, userSurname, userPhone);
        System.out.println(user);

        List<Resource> resources = resourceDao.findAll();
        ListIterator<Resource> iter = resources.listIterator();
        for (int i = 0; i < resources.size() && iter.hasNext() ; ++i)
            System.out.println(String.format("%d : %s", i + 1,
                    iter.next().getName()));
        int nResource = 2;
        System.out.println("Enter resource's  number : " + nResource);
        Resource resource = resources.get(nResource - 1);
        Date date = validatorTransformer.getDateOrNull("2022-12-10");
        System.out.println("Enter date in format YYYY-MM-DD :" + date);


        List<Record> emptyRecords =
                recordDao.findEmptyRecordsByResourceAndDate(
                        resource,date);
        ListIterator<Record> iterRec = emptyRecords.listIterator();
        for (int i = 0; i < emptyRecords.size() && iterRec.hasNext() ; ++i)
            System.out.println(String.format("%d : %s", i + 1, iterRec.next().getTimeStart().toString()));
        int nRecords = 4;
        System.out.println("Enter record's number: "+nRecords);
        recordDao.addToRecord(emptyRecords,nRecords - 1, user );
    }
    @Test
    public void createSituation(){
        if (userDao == null )
            throw new RuntimeException("userDao is null");
        if (generatorFromFile == null )
            throw new RuntimeException("generatorFromFile is null");


        somethingToDO();

        recordDao.findAllRecords().forEach(record -> {
            Assertions.assertNull(record.getUser());
            Assertions.assertNotNull(record.getResource());
            }
        );
    }
    @BeforeAll
    public static void startServer() throws SQLException {
        Server.createTcpServer().start();
    }

    @BeforeEach
    public void init() throws SQLException {
        JdbcTestUtils.deleteFromTables(
                new JdbcTemplate(dataSource),
                "Record", "User",  "Resource");
    }
}