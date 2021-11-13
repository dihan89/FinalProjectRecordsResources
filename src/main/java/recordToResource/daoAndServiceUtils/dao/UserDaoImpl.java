package recordToResource.daoAndServiceUtils.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recordToResource.daoAndServiceUtils.ValidatorTransformer;
import recordToResource.model.User;
import javax.persistence.EntityManager;
import java.util.List;


@Service
@EntityScan("recordToResource.model")
@Transactional
@Repository("userDao")

public class UserDaoImpl implements UserDao {
    @Autowired
    private EntityManager entityManager;

    private ValidatorTransformer validatorTransformer = new ValidatorTransformer();

    @Override
    @Transactional
    public boolean add(User user) {
        user = validateAndTransformUser(user);
        if (user.getName() == null || user.getSurname() == null ||
                user.getPhone() == null|| findByPhone(user.getPhone()) !=null) {
            return false;
        }

        try {
            entityManager.persist(user);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    private User validateAndTransformUser(User user) {
        user.setName(validatorTransformer.getWordOrNull(user.getName()));
        user.setSurname(validatorTransformer.getWordOrNull(user.getSurname()));
        user.setPhone(validatorTransformer.getPhoneOrNull(user.getPhone()));
        return user;
    }

    @Override
    @Transactional
    public User get(String name,
                    String surname, String phone) {

        name = validatorTransformer.getWordOrNull(name);
        surname = validatorTransformer.getWordOrNull(surname);
        phone = validatorTransformer.getPhoneOrNull(phone);

        if (name == null || surname == null || phone == null) {
            return null;
        }

        User user = find(name, surname, phone);
        if (user == null) {
            user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setPhone(phone);
            add(user);
            System.out.println("This user is new: " + user.getId());
        } else {
            System.out.println("This user is exist: " + user.getId());
        }
        return user;
    }

    @Override
    @Transactional
    public User find(String name, String surname, String phone) {

        name = validatorTransformer.getWordOrNull(name);
        surname = validatorTransformer.getWordOrNull(surname);
        phone = validatorTransformer.getPhoneOrNull(phone);
        if (name == null || surname == null || phone == null) {
            return null;
        }

        List<User> list =
                entityManager.createNamedQuery("findUser")
                        .setParameter("name", name)
                        .setParameter("surname", surname)
                        .setParameter("phone", phone)
                        .getResultList();
        return (list.isEmpty()) ? null : list.get(0);
    }

    @Override
    @Transactional
    public void delete(User user) {
        if (entityManager.find(User.class, user.getId()) != null) {
            entityManager.remove(entityManager.find(User.class, user.getId()));
        }
    }

    @Override
    @Transactional
    public User findByPhone(String phone) {
        List<User> list =
                entityManager.createNamedQuery("findByPhone")
                        .setParameter("phone", phone)
                        .getResultList();
        return (list.isEmpty()) ? null : list.get(0);
    }
}

