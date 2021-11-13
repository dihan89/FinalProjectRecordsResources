package recordToResource.daoAndServiceUtils.dao;

import recordToResource.model.Record;
import recordToResource.model.Resource;
import recordToResource.model.User;

import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.util.List;


public interface RecordDao {
    void add(User user, Resource resource, Date date, Time time, Duration duration);

    boolean addUser(User user, Record record);

    boolean delete(User user,
                   Resource resource, Date date, Time time);

    List<Record> findRecordsByResource(Resource resource);

    List<Record> findAllRecords();

    List<Record> findAllRecordsByUser(User user);

    List<Record> findEmptyRecordsByResourceAndDate(
            Resource resource, Date date);

    boolean addToRecord(List<Record> records, int index, User user);

    boolean delete(Record record);

    void createNullRecords(Date date, Time timeStartGroup, Integer count,
                           Duration duration, Resource resource);

    boolean deleteUserFromRecord(Record record);
}
