package recordToResource.daoAndServiceUtils.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recordToResource.daoAndServiceUtils.dao.RecordDao;
import recordToResource.daoAndServiceUtils.dao.ResourceDao;
import recordToResource.daoAndServiceUtils.ValidatorTransformer;
import recordToResource.model.Resource;

import java.io.*;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.util.Arrays;

@Service
@EntityScan("recordToResource.model")
@Transactional
@Repository("generatorFromFile")
public class GeneratorFromFileImpl implements GeneratorFromFile {
    private final ValidatorTransformer validatorTransformer = new ValidatorTransformer();

    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private RecordDao recordDao;

    @Override
    @Transactional
    public void generateResourceNullRecordsFromFile (File file){
        if (!file.isFile())
            return;
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String nameResource = reader.readLine();
            Resource resource = resourceDao.findByName(nameResource);
            if (resource == null)
                return;
            String str;
            while ((str = reader.readLine()) != null) {
                String[] elems = str.split(" ");
                if (elems.length != 4)
                    throw new IllegalArgumentException(
                            "File is not correct: " + file.getName() +
                                    " " + str);
                Date date = validatorTransformer.getDateOrNull(elems[0]);
                Time timeStart = validatorTransformer.getTimeOrNull(elems[1]);
                Duration duration;
                int count = 0;
                if (date != null && timeStart != null) {
                    duration = Duration.ofMinutes(Long.parseLong(elems[2]));
                    count = Integer.parseInt(elems[3]);
                    recordDao.createNullRecords(
                            date, timeStart, count, duration, resource);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    @Transactional
    public void generateResourceNullRecordsFromDir(File dir){
        if (dir != null && !dir.isDirectory())
            return;
        File[] files = dir.listFiles();
        Arrays.stream(files).parallel().forEach(f->{
            if (f.isDirectory()) generateResourceNullRecordsFromDir(f);
            else generateResourceNullRecordsFromFile(f);
        });
    }
    @Override
    @Transactional
    public void generateResourceFromDir(File dir){
        System.out.println("Generator!");
        if (!dir.isDirectory())
            return;
        File[] files = dir.listFiles();
        Arrays.stream(files).parallel().forEach(f->{
            if (f.isDirectory()) generateResourceFromDir(f);
            else generateResourceFromFile(f);
        });
    }

    @Override
    @Transactional
    public void generateResourceFromFile(File file) {

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {
            String nameResource = reader.readLine();
            if (nameResource == null)
                return;
            Resource resource = new Resource();
            String description = reader.readLine();
            resource.setName(nameResource);
            resource.setDescription(description);
            if (resourceDao.findByName(nameResource) == null)
                resourceDao.add(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
