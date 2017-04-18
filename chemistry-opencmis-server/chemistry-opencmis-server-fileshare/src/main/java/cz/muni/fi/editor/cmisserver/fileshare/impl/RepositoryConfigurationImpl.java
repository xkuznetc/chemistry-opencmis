package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.RepositoryConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by Dominik Szalai - emptulik at gmail.com on 17.4.2017.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RepositoryConfigurationImpl implements RepositoryConfiguration
{
    private Path repositoryPath;

    public void setRepositoryPath(Path repositoryPath)
    {
        this.repositoryPath = repositoryPath;
    }

    @Override
    public Path repositoryPath()
    {
        return repositoryPath;
    }

    @Override
    public Path getIndexPath()
    {
        return repositoryPath.resolve(INDEX_DIRECTORY);
    }

    @Override
    public Path getRootPath()
    {
        return repositoryPath.resolve(ROOT_DIRECTORY);
    }

    @Override
    public Path getConfigurationPath()
    {
        return repositoryPath.resolve(CONFIG_DIRECTORY);
    }
}
