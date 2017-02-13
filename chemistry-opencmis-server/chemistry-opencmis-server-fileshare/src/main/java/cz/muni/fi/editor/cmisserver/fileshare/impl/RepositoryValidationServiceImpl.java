package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.RepositoryValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by emptak on 13.2.2017.
 */
public class RepositoryValidationServiceImpl implements RepositoryValidationService
{
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryValidationServiceImpl.class);

    @Override
    public void load(Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            throw new IOException(String.format("Repository '%s' location does not exist.", path));
        }
        if (!Files.isDirectory(path))
        {
            throw new IOException(String.format("Repository '%s' location is not a directory.", path));
        }

        Path configurationDir = path.resolve("conf");
        Path indexDir = path.resolve("index");
        Path dataDir = path.resolve("data");

        if (!Files.exists(indexDir))
        {
            Files.createDirectory(indexDir);
        }

        if (!Files.exists(dataDir))
        {
            Files.createDirectory(dataDir);
        }

        if (!Files.exists(configurationDir))
        {
            throw new IOException(String.format("Configuration directory 'conf' is missing. Create it in '%s' directory first before starting CMIS.", path));
        }
        else
        {
            if (!Files.exists(configurationDir.resolve("configuration.properties")))
            {
                throw new IOException("Configuration directory does not contain 'configuration.properties' files. Create it before starting CMIS.");
            }
            else
            {
                // ok
            }
        }
    }
}

