package recordToResource.daoAndServiceUtils.Generator;

import java.io.File;

public interface GeneratorFromFile {
    void generateResourceNullRecordsFromFile (File file);
    void generateResourceNullRecordsFromDir(File dir);
    void generateResourceFromDir(File dir);
    void generateResourceFromFile(File file);
}
