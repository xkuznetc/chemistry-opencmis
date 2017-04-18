package cz.muni.fi.editor.cmisserver.fileshare.impl;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by emptak on 2/6/17.
 */
@Configuration
@ComponentScan("cz.muni.fi.editor")
public class CmisConfiguration
{

/*    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Description("Bean used as property configuration for each repository")
    public RepositoryConfiguration repositoryProperties(){
        return new RepositoryConfigurationImpl();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Description("Bean used as property configuration for each repository")
    public Repository repository(){
        return new EditorRepository();
    }*/

/*    @Bean
    public EditorRepositoryFactory editorRepositoryFactory(LuceneServiceFactory luceneServiceFactory)
    {
        return new EditorRepositoryFactory(luceneServiceFactory);
    }

    @Bean
    public LuceneServiceFactory luceneServiceFactory()
    {
        return new LuceneServiceFactory();
    }

    @Bean
    public UserManager userManager()
    {
        return new EditorUserManager();
    }

    @Bean
    public RepositoryManager repositoryManager()
    {
        return new EditorRepositoryManager();
    }*/



    /*

    @Bean
    public TypeManager typeManager()
    {
        return new EditorTypeManagerImpl();
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
    }*/
}
