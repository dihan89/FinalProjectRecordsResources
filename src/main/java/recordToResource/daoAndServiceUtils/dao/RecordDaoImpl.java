package recordToResource.daoAndServiceUtils.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recordToResource.model.Record;
import recordToResource.model.Resource;
import recordToResource.model.User;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@EntityScan("recordToResource.model")
@Transactional
public class RecordDaoImpl implements RecordDao {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public boolean add(User user, Resource resource, Date date, Time time, Duration duration) {
        if (resource == null ||
                resourceDao.findByName(resource.getName()) == null) {
            return false;
        }
        if (date == null || time == null || duration == null ||
                date.before(Date.valueOf(LocalDate.now())) ||
                (date.equals(Date.valueOf(LocalDate.now())) &&
                        time.before(Time.valueOf(LocalTime.now())))) {
            return false;
        }
        Record record = new Record();
        record.setUser(user);
        record.setResource(resource);
        record.setDate(date);
        record.setTimeStart(time);
        record.setDuration(duration);
        entityManager.persist(record);
        return true;
    }

    @Override
    @Transactional
    public boolean delete(User user,
                          Resource resource, Date date, Time time) {
        Record record = findByResourceAndTime(resource, date, time);
        if (record == null || (!record.getUser().equals(user))) {
            return false;
        }
        entityManager.remove(record);
        return true;
    }

    @Transactional
    public Record findByResourceAndTime(Resource resource,
                                        Date date, Time time) {
        List<Record> list =
                entityManager.createNamedQuery("findRecordsByDateAndResource")
                        .setParameter("resource", resource)
                        .setParameter("date", date)
                        .setParameter("time", time)
                        .getResultList();
        return (list.isEmpty()) ? null : list.get(0);
    }

    @Override
    public List<Record> findRecordsByResource(Resource resource) {
        return entityManager
                .createNamedQuery("findRecordsByResource")
                .setParameter("resource", resource)
                .getResultList();
    }

    @Override
    public List<Record> findAllRecords() {
        return entityManager
                .createNamedQuery("findAllRecords")
                .getResultList();
    }

    @Override
    @Transactional
    public boolean addUser(User user, Record record) {

        if (record == null || record.getUser() != null) {
            return false;
        }
        record.setUser(user);
        entityManager.merge(record);
        return true;
    }

    @Override
    public List<Record> findEmptyRecordsByResourceAndDate(
            Resource resource, Date date) {

        if (date.before(Date.valueOf(LocalDate.now()))) {
            return null;
        }

        return entityManager
                .createNamedQuery("findEmptyRecordsByResourceAndDate")
                .setParameter("resource", resource)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    @Transactional
    public void createNullRecords(Date date, Time timeStartGroup, Integer count,
                                  Duration duration, Resource resource) {
        long durationMinutes = duration.toMinutes();
        LocalDateTime ldt =
                LocalDateTime.of(date.toLocalDate(), timeStartGroup.toLocalTime());
        for (int i = 0; i < count; ++i) {
            Time currentTime = Time.valueOf(ldt.toLocalTime());
            Date currentDate = Date.valueOf(ldt.toLocalDate());
            add(null, resource, currentDate, currentTime, duration);
            ldt = ldt.plusMinutes(durationMinutes);
        }
    }

    @Override
    public List<Record> findRecordsByUser(User user) {
        return entityManager
                .createNamedQuery("findAllRecordsByUser")
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    @Transactional
    public boolean delete(Record record) {
        if (record == null) {
            return false;
        }
        Record record1 = entityManager.find(Record.class, record.getId());
        if (record1 == null) {
            return false;
        }
        entityManager.remove(record1);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteUserFromRecord(Record record) {
        if (record.getDate().before(Date.valueOf(LocalDate.now())) ||
                (record.getDate().equals(Date.valueOf(LocalDate.now())) &&
                        record.getTimeStart().before(Time.valueOf(LocalTime.now()))))
            return false;
        record.setUser(null);
        entityManager.merge(record);
        return true;
    }
}






