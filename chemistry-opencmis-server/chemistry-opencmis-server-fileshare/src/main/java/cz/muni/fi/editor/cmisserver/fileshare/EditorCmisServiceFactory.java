package cz.muni.fi.editor.cmisserver.fileshare;

import cz.muni.fi.editor.cmisserver.fileshare.impl.CmisConfiguration;
import cz.muni.fi.editor.cmisserver.fileshare.impl.EditorCmisService;
import cz.muni.fi.editor.cmisserver.fileshare.impl.EditorRepositoryFactory;
import cz.muni.fi.editor.cmisserver.types.InitializingTypeManager;
import cz.muni.fi.editor.cmisserver.types.JSPTypeManager;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CmisServiceWrapperManager;
import org.apache.chemistry.opencmis.server.support.wrapper.ConformanceCmisServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by emptak on 2/6/17.
 */
public class EditorCmisServiceFactory extends AbstractServiceFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(EditorCmisServiceFactory.class);
    private static final String PREFIX_LOGIN = "login.";
    private static final String PREFIX_REPOSITORY = "repository.";
    private static final String EMPTY_PASSWORD = "";
//    private static final String PREFIX_TYPE = "type.";
//    private static final String SUFFIX_READWRITE = ".readwrite";
//    private static final String SUFFIX_READONLY = ".readonly";
//    private static final String SUFFIX_INDEX = ".index";

    /**
     * Default maxItems value for getTypeChildren()}.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_TYPES = BigInteger.valueOf(50);

    /**
     * Default depth value for getTypeDescendants().
     */
    private static final BigInteger DEFAULT_DEPTH_TYPES = BigInteger.valueOf(-1);

    /**
     * Default maxItems value for getChildren() and other methods returning
     * lists of objects.
     */
    private static final BigInteger DEFAULT_MAX_ITEMS_OBJECTS = BigInteger.valueOf(200);

    /**
     * Default depth value for getDescendants().
     */
    private static final BigInteger DEFAULT_DEPTH_OBJECTS = BigInteger.TEN;

    /**
     * Each thread gets its own {@link EditorCmisService} instance.
     */
    private ThreadLocal<CallContextAwareCmisService> threadLocalService = new ThreadLocal<>();

    private CmisServiceWrapperManager wrapperManager;

    private ApplicationContext ctx;
    private UserManager userManager;
    private TypeManager typeManager;
    private RepositoryManager repositoryManager;
    private RepositoryValidationService repositoryValidationService;
    private EditorRepositoryFactory editorRepositoryFactory;

    public RepositoryManager getRepositoryManager()
    {
        return repositoryManager;
    }

    public UserManager getUserManager()
    {
        return userManager;
    }

    // used in jsp
    public JSPTypeManager getTypeManager()
    {
        return (JSPTypeManager) typeManager;
    }

    @Override
    public void init(Map<String, String> parameters)
    {
        wrapperManager = new CmisServiceWrapperManager();
        wrapperManager.addWrappersFromServiceFactoryParameters(parameters);
        wrapperManager.addOuterWrapper(ConformanceCmisServiceWrapper.class, DEFAULT_MAX_ITEMS_TYPES,
                DEFAULT_DEPTH_TYPES, DEFAULT_MAX_ITEMS_OBJECTS, DEFAULT_DEPTH_OBJECTS);


        ctx = new AnnotationConfigApplicationContext(CmisConfiguration.class);
        LOG.info("Spring running with id {}", ctx.getId());
        userManager = ctx.getBean(UserManager.class);
        typeManager = ctx.getBean(TypeManager.class);
        repositoryManager = ctx.getBean(RepositoryManager.class);
        editorRepositoryFactory = ctx.getBean(EditorRepositoryFactory.class);
        repositoryValidationService = ctx.getBean(RepositoryValidationService.class);

        readConfiguration(parameters);
    }

    @Override
    public void destroy()
    {
        ((ConfigurableApplicationContext) ctx).close();
    }

    @Override
    public CmisService getService(CallContext context)
    {
        userManager.authenticate(context);
        CallContextAwareCmisService service = threadLocalService.get();
        if (service == null)
        {
            service = (CallContextAwareCmisService) wrapperManager.wrap(ctx.getBean(EditorCmisService.class));

            threadLocalService.set(service);
        }

        service.setCallContext(context);

        return service;
    }

    private void readConfiguration(Map<String, String> parameters)
    {
        List<String> keys = new ArrayList<>(parameters.keySet());
        Collections.sort(keys);

        for (String key : keys)
        {
            if (key.startsWith(PREFIX_LOGIN))
            {
                // get logins
                String usernameAndPassword = replaceSystemProperties(parameters.get(key));
                if (usernameAndPassword == null)
                {
                    continue;
                }

                String username = usernameAndPassword;
                String password = EMPTY_PASSWORD;

                int x = usernameAndPassword.indexOf(':');
                if (x > -1)
                {
                    username = usernameAndPassword.substring(0, x);
                    password = usernameAndPassword.substring(x + 1);
                }

                LOG.info("Adding login '{}'.", username);

                userManager.addLogin(username, password);
            }
            else if (key.startsWith(PREFIX_REPOSITORY))
            {
                Path repositoryPath = FileSystems.getDefault().getPath(parameters.get(key));
                try
                {
                    repositoryValidationService.load(repositoryPath);
                }
                catch (IOException ex)
                {
                    throw new CmisRuntimeException(ex.getMessage(), BigInteger.ZERO, ex);
                }

                repositoryManager.addRepository(editorRepositoryFactory.repository(
                        getRepositoryId(key),
                        repositoryPath
                ));

            }
            // else value is class and we don't process it
        }
//

//            else if (key.startsWith(PREFIX_TYPE))
//            {
//                // load type definition
//                String typeFile = replaceSystemProperties(parameters.get(key).trim());
//                if (typeFile.length() == 0)
//                {
//                    continue;
//                }
//
//                LOG.info("Loading type definition: {}", typeFile);
//
//                if (typeFile.charAt(0) == '/')
//                {
//                    try
//                    {
//                        ((InitializingTypeManager) typeManager).loadTypeDefinitionFromResource(typeFile);
//                        continue;
//                    }
//                    catch (IllegalArgumentException e)
//                    {
//                        // resource not found -> try it as a regular file
//                    }
//                    catch (Exception e)
//                    {
//                        LOG.warn("Could not load type defintion from resource '{}': {}", typeFile, e.getMessage(), e);
//                        continue;
//                    }
//                }
//
//                try
//                {
//                    ((InitializingTypeManager) typeManager).loadTypeDefinitionFromFile(typeFile);
//                }
//                catch (Exception e)
//                {
//                    LOG.warn("Could not load type defintion from file '{}': {}", typeFile, e.getMessage(), e);
//                }
//            }
//            else if (key.startsWith(PREFIX_REPOSITORY) && getCharacterOccurenceCount(".", key) == 1)
//            {
//                repositories.add(key);
//                LOG.info("Marking '{}' as repo", key);
//            }
//        }
//
//        for (String repository : repositories)
//        {
//            List<String> readWriteUsers = getUsersFromValue(parameters.get(repository + SUFFIX_READWRITE));
//            List<String> readOnlyUsers = getUsersFromValue(parameters.get(repository + SUFFIX_READONLY));
//            String indexPath = parameters.get(repository + SUFFIX_INDEX);
//            String root = parameters.get(repository);
//            String repoID = substringAfter(".", repository);
//
//            Repository repo = editorRepositoryFactory.repository(
//                    repoID,
//                    FileSystems.getDefault().getPath(root),
//                    FileSystems.getDefault().getPath(indexPath),
//                    readWriteUsers,
//                    readOnlyUsers
//            );
//
//            LOG.info("=======================================");
//            LOG.info("Read write users are: {}", readWriteUsers);
//            LOG.info("Read only users are: {}", readOnlyUsers);
//            LOG.info("Index for this repo is located at: {}", indexPath);
//            LOG.info("Repo is located at: {}", root);
//            LOG.info("REPOSITORY ++ {} ++ INITIALIZED.", repoID);
//
//            repositoryManager.addRepository(repo);
//            LOG.info("REPOSITORY ++ {} ++ added.", repository);
//
//        }
    }

    private String getRepositoryId(String repositoryKey)
    {
        return repositoryKey.substring(repositoryKey.indexOf(".")+1);
    }

//    private int getCharacterOccurenceCount(String character, String input)
//    {
//        return input.length() - input.replace(character, "").length();
//    }

    /**
     * Finds all substrings in curly braces and replaces them with the value of
     * the corresponding system property.
     */
    private String replaceSystemProperties(String s)
    {
        if (s == null)
        {
            return null;
        }

        StringBuilder result = new StringBuilder(128);
        StringBuilder property = null;
        boolean inProperty = false;

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (inProperty)
            {
                if (c == '}')
                {
                    String value = System.getProperty(property.toString());
                    if (value != null)
                    {
                        result.append(value);
                    }
                    inProperty = false;
                }
                else
                {
                    property.append(c);
                }
            }
            else
            {
                if (c == '{')
                {
                    property = new StringBuilder(32);
                    inProperty = true;
                }
                else
                {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }
}
