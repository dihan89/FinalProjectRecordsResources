package recordToResource.daoAndServiceUtils.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recordToResource.model.Resource;

import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;


@Service
@EntityScan("recordToResource.model")
@Transactional
@Repository("resourceDao")
public class ResourceDaoImpl implements ResourceDao {
    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean add(Resource resource) {
        if (resource == null || resource.getName() == null) {
            return false;
        }
        resource.setName(resource.getName().trim());
        if(resource.getName() == "" || findByName(resource.getName()) != null)
            return false;
        try {
            entityManager.persist(resource);
        } catch(Throwable exc){
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void delete(Resource resource) {

        if (entityManager.find(Resource.class, resource.getId()) != null) {
            entityManager.remove(entityManager.find(Resource.class, resource.getId()));
        }
//        try {
//            entityManager.remove(entityManager.find(Resource.class, resource.getId()));
//        } catch (InvalidDataAccessApiUsageException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    @Transactional
    public Resource findByName(String name) {
        List<Resource> list =
                entityManager.createNamedQuery("findResourceByName")
                        .setParameter("name", name)
                        .getResultList();
        return (list.isEmpty()) ? null : list.get(0);
    }

    @Override
    @Transactional
    public List<Resource> findAll() {
        return entityManager
                .createNamedQuery("findAllResources")
                .getResultList();
    }

}
