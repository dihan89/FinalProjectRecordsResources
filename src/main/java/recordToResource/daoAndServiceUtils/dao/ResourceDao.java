package recordToResource.daoAndServiceUtils.dao;

import recordToResource.model.Resource;

import java.util.List;


public interface ResourceDao {
    boolean add(Resource resource);

    void delete(Resource resource);

    Resource findByName(String name);

    List<Resource> findAll();
}
