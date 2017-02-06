package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.Repository;
import cz.muni.fi.editor.cmisserver.lucene.LuceneServiceFactory;
import org.apache.chemistry.opencmis.server.support.TypeManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by emptak on 2/6/17.
 */
public class EditorRepositoryFactory
{
    private LuceneServiceFactory luceneServiceFactory;
    private TypeManager typeManager;

    public EditorRepositoryFactory(LuceneServiceFactory luceneServiceFactory, TypeManager typeManager)
    {
        this.luceneServiceFactory = luceneServiceFactory;
        this.typeManager = typeManager;
    }

    public Repository repository(String repositoryId, Path rootFolder, Path indexPath, List<String> readWriteUsers, List<String> readOnlyUsers)
    {
        try
        {
            return new EditorRepository(repositoryId, rootFolder, typeManager, luceneServiceFactory.getLuceneService(indexPath), readWriteUsers, readOnlyUsers);
        }
        catch (IOException ex)
        {
            throw new IllegalArgumentException(ex);
        }
    }
}
