package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.RepositoryManager;
import cz.muni.fi.editor.cmisserver.fileshare.RepositoryValidationService;
import cz.muni.fi.editor.cmisserver.fileshare.UserManager;
import cz.muni.fi.editor.cmisserver.lucene.LuceneServiceFactory;
import cz.muni.fi.editor.cmisserver.types.EditorTypeManager;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emptak on 2/6/17.
 */
@Configuration
public class CmisConfiguration
{
    @Bean
    public UserManager userManager()
    {
        return new EditorUserManager();
    }

    @Bean
    public TypeManager typeManager()
    {
        return new EditorTypeManager();
    }

    @Bean
    public RepositoryManager repositoryManager()
    {
        return new EditorRepositoryManager();
    }

    @Bean
    public LuceneServiceFactory luceneServiceFactory()
    {
        return new LuceneServiceFactory();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EditorCmisService callContextAwareCmisService()
    {
        return new EditorCmisService(repositoryManager());
    }

    @Bean
    public EditorRepositoryFactory editorRepositoryFactory()
    {
        return new EditorRepositoryFactory(luceneServiceFactory(), typeManager());
    }

    @Bean
    public RepositoryValidationService repositoryValidationService()
    {
        return new RepositoryValidationServiceImpl();
    }


    private Path getPath(String value, String... more)
    {
        return FileSystems.getDefault().getPath(value, more);
    }
}
