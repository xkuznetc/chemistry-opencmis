package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.Repository;
import cz.muni.fi.editor.cmisserver.fileshare.RepositoryConfiguration;
import cz.muni.fi.editor.cmisserver.fileshare.UserManager;
import cz.muni.fi.editor.cmisserver.lucene.LuceneServiceFactory;
import cz.muni.fi.editor.cmisserver.types.EditorTypeManager;
import cz.muni.fi.editor.cmisserver.types.EditorTypeManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

/**
 * Created by emptak on 2/6/17.
 */
@Component
public class EditorRepositoryFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(EditorRepositoryFactory.class);
    private LuceneServiceFactory luceneServiceFactory;
    private Provider<EditorRepository> repositoryProvider;
    private UserManager userManager;
    private Provider<EditorTypeManagerImpl> editorTypeManagerProvider;
    private Provider<RepositoryConfigurationImpl> repositoryConfigurationProvider;


    public EditorRepositoryFactory(LuceneServiceFactory luceneServiceFactory, Provider<EditorRepository> repositoryProvider, UserManager userManager, Provider<EditorTypeManagerImpl> editorTypeManagerProvider, Provider<RepositoryConfigurationImpl> repositoryConfigurationProvider)
    {
        this.luceneServiceFactory = luceneServiceFactory;
        this.repositoryProvider = repositoryProvider;
        this.userManager = userManager;
        this.editorTypeManagerProvider = editorTypeManagerProvider;
        this.repositoryConfigurationProvider = repositoryConfigurationProvider;
    }

    public Repository repository(String repositoryId, Path root) throws IllegalArgumentException
    {
        if (repositoryId == null || repositoryId.trim().length() == 0)
        {
            throw new IllegalArgumentException("Invalid repository id!");
        }

        RepositoryConfigurationImpl configuration = repositoryConfigurationProvider.get();
        configuration.setRepositoryPath(root);
        try{
            checkConfiguration(configuration);
        }
        catch (IOException ex){
            throw new IllegalArgumentException(ex);
        }


        EditorTypeManagerImpl editorTypeManager = editorTypeManagerProvider.get();
        editorTypeManager.init();

        EditorRepository repository = repositoryProvider.get();
        repository.setRepositoryId(repositoryId);
        repository.setUserManager(userManager);
        repository.setRepositoryConfiguration(configuration);
        repository.setTypeManager(editorTypeManager);

        repository.initializeRepository();

        return repository;
    }

    private void checkConfiguration(RepositoryConfiguration repositoryConfiguration) throws IOException
    {
        if(!Files.exists(repositoryConfiguration.repositoryPath())){
            throw new IOException(String.format("Repository path set in WEB-INF/classes/repository.properties MUST exist. Currently %s either does not exist or there is no write access.",repositoryConfiguration.repositoryPath()));
        }
        else
        {
            final String exMsg = "Repo %s directory does not exist - creating.";
            if(!Files.exists(repositoryConfiguration.getConfigurationPath()))
            {
                LOG.info(String.format(exMsg,"configuration"));
                Files.createDirectory(repositoryConfiguration.getConfigurationPath());
            }

            if(!Files.exists(repositoryConfiguration.getIndexPath())){
                LOG.info(String.format(exMsg,"index"));
                Files.createDirectory(repositoryConfiguration.getIndexPath());
            }

            if(!Files.exists(repositoryConfiguration.getRootPath())){
                LOG.info(String.format(exMsg,"root data"));
                Files.createDirectory(repositoryConfiguration.getRootPath());
            }

            Path configFile = repositoryConfiguration.getConfigurationPath().resolve(RepositoryConfiguration.CONFIG_FILE);

            if(!Files.exists(configFile)){
                LOG.info(String.format("Configuration file %s at directory %s does not exist. Creating using default values.",repositoryConfiguration.getConfigurationPath(),RepositoryConfiguration.CONFIG_FILE));

                Files.createFile(configFile);

                Properties properties = new Properties();
                properties.setProperty("readonly","reader");
                properties.setProperty("readwrite","test");
                properties.setProperty("name","Default repository");
                properties.setProperty("description",String.format("This is default repository created using checkConfig method. You can change behaviour by modifying file at %s",configFile));

                properties.store(Files.newOutputStream(configFile),null);
            }
        }
    }
}
