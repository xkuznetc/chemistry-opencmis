package cz.muni.fi.editor.cmisserver.fileshare;

import java.nio.file.Path;

/**
 * Created by kate on 17.4.2017.
 */
public interface RepositoryConfiguration
{
    String CONFIG_DIRECTORY = "conf";
    String INDEX_DIRECTORY = "index";
    String ROOT_DIRECTORY = "data";
    String CONFIG_FILE = "configuration.properties";

    Path repositoryPath();
    Path getIndexPath();
    Path getRootPath();
    Path getConfigurationPath();
}
