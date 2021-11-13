package recordToResource.daoAndServiceUtils.dao;

import recordToResource.model.User;

public interface UserDao {

    boolean add(User user);

    void delete(User user);

    User get(String name,
             String surname, String phone);

    User findByPhone(String phone);

    User find(String name,
              String surname, String phone);

}
