package cz.muni.fi.editor.cmisserver.fileshare;


import java.util.Collection;

/**
 * Created by emptak on 2/6/17.
 */
public interface RepositoryManager
{
    void addRepository(Repository fileShareRepository);

    Repository getRepository(String repositoryId);

    Collection<Repository> getRepositories();
}
