package cz.muni.fi.editor.cmisserver.fileshare;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by emptak on 13.2.2017.
 */
public interface RepositoryValidationService
{
    void load(Path path) throws IOException;
}
