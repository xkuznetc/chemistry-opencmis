package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.Repository;
import cz.muni.fi.editor.cmisserver.fileshare.RepositoryManager;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Code taken from default implementation
 * Created by emptak on 2/6/17.
 */
public class EditorRepositoryManager implements RepositoryManager
{
    private Logger LOG = LoggerFactory.getLogger(EditorRepositoryManager.class);

    private final Map<String, Repository> repositories = new HashMap<>();

    @Override
    public void addRepository(Repository fileShareRepository)
    {
        if (fileShareRepository == null || fileShareRepository.getRepositoryId() == null)
        {
            return;
        }

        repositories.put(fileShareRepository.getRepositoryId(), fileShareRepository);
        LOG.info("Repo {} added. Verification {}", fileShareRepository.getRepositoryId(), repositories.containsKey(fileShareRepository.getRepositoryId()));
    }

    @Override
    public Repository getRepository(String repositoryId)
    {
        Repository result = repositories.get(repositoryId);
        if (result == null)
        {
            throw new CmisObjectNotFoundException(String.format("Unknown repository '%s'!", repositoryId));
        }

        return result;
    }

    @Override
    public Collection<Repository> getRepositories()
    {

        LOG.info("Following repositories are available {}", repositories.keySet());
        return repositories.values();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(128);

        for (Repository repository : repositories.values())
        {
            sb.append('[');
            sb.append(repository.getRepositoryId());
            sb.append(" -> ");
            sb.append(repository.getRootDirectory().getAbsolutePath());
            sb.append(']');
        }

        return sb.toString();
    }
}
