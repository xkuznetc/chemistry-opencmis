package cz.muni.fi.editor.cmisserver.types;

import cz.muni.fi.editor.cmisserver.fileshare.RepositoryConfiguration;
import cz.muni.fi.editor.cmisserver.types.vendor.TypeUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.*;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.IOUtils;
import org.apache.chemistry.opencmis.commons.impl.XMLConverter;
import org.apache.chemistry.opencmis.commons.impl.XMLUtils;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.TypeDefinitionContainerImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.support.TypeDefinitionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditorTypeManagerImpl implements JSPTypeManager
{
    private static final Logger LOG = LoggerFactory.getLogger(EditorTypeManagerImpl.class);
    private static final String NAMESPACE = "http://chemistry.apache.org/opencmis/fileshare";

    private final Object LOCK = new Object();

    private final Map<String, TypeDefinitionContainer> tdcMap = new HashMap<>();
    private final Map<String, TypeDefinition> tdMap = new HashMap<>();

    private TypeDefinitionFactory typeDefinitionFactory;
    private RepositoryConfiguration repositoryConfiguration;
    private DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>()
    {
        @Override
        public boolean accept(Path entry) throws IOException
        {
            return entry.getFileName().toString().endsWith(".xml");
        }
    };

    public EditorTypeManagerImpl()
    {
        typeDefinitionFactory = TypeDefinitionFactory.newInstance();
        typeDefinitionFactory.setDefaultNamespace(NAMESPACE);
        typeDefinitionFactory.setDefaultControllableAcl(false);
        typeDefinitionFactory.setDefaultQueryable(true);
        typeDefinitionFactory.setDefaultFulltextIndexed(false);
        typeDefinitionFactory.setDefaultTypeMutability(typeDefinitionFactory.createTypeMutability(false, false, false));

        TypeDefinitionContainer folder = createFolderType(CmisVersion.CMIS_1_1);
        TypeDefinitionContainer document = createDocumentTypeDefinition(CmisVersion.CMIS_1_1);

        // add folder and document as base types for hierarchy
        tdcMap.put(folder.getTypeDefinition().getId(), folder);
        tdcMap.put(document.getTypeDefinition().getId(), document);
        tdMap.put(folder.getTypeDefinition().getId(), folder.getTypeDefinition());
        tdMap.put(document.getTypeDefinition().getId(), document.getTypeDefinition());
    }

    public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration)
    {
        this.repositoryConfiguration = repositoryConfiguration;
    }

    public void init()
    {
        LOG.info(String.format("Config path : '%s'", repositoryConfiguration.getConfigurationPath()));
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(repositoryConfiguration.getConfigurationPath(), filter))
        {
            for (Path xmltype : ds)
            {
                this.loadFromPath(xmltype);
                LOG.info(String.format("XML type from path '%s' added.", xmltype));
            }
        }
        catch (IOException | XMLStreamException ex)
        {
            throw new CmisRuntimeException(ex.getMessage(), BigInteger.ZERO, ex);
        }
    }

    @Override
    public TypeDefinition getTypeDefinition(CallContext context, String typeId)
    {
        TypeDefinitionContainer tdc = tdcMap.get(typeId);
        if (tdc == null)
        {
            throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
        }

        return typeDefinitionFactory.copy(tdc.getTypeDefinition(), true, context.getCmisVersion());
    }

    @Override
    public TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions, BigInteger maxItems, BigInteger skipCount)
    {
        return typeDefinitionFactory.createTypeDefinitionList(tdMap, typeId, includePropertyDefinitions,
                maxItems, skipCount, context.getCmisVersion());
    }

    @Override
    public List<TypeDefinitionContainer> getTypeDescendants(CallContext context, String typeId, BigInteger depth, Boolean includePropertyDefinitions)
    {
        return typeDefinitionFactory.createTypeDescendants(this.tdMap, typeId, depth, includePropertyDefinitions,
                context.getCmisVersion());
    }

    @Override
    public TypeDefinition createType(String repositoryId, TypeDefinition type)
    {
        synchronized (LOCK){
            try
            {
                this.saveXmlTypeDefinition(type);
            }
            catch (IOException e)
            {
                throw new CmisRuntimeException("error while creating td",e);
            }

            addTypeDefinition(type, true);

            return type;
        }
    }

    @Override
    public Collection<TypeDefinition> getInternalTypeDefinitions()
    {
        return tdMap.values();
    }

    @Override
    public TypeDefinitionContainer getTypeById(String typeId)
    {
        return tdcMap.get(typeId);
    }

    @Override
    public TypeDefinition getTypeByQueryName(String typeQueryName)
    {
        for (TypeDefinitionContainer tdc : tdcMap.values())
        {
            if (tdc.getTypeDefinition().getQueryName().equals(typeQueryName))
            {
                return tdc.getTypeDefinition();
            }
        }

        return null;
    }

    @Override
    public Collection<TypeDefinitionContainer> getTypeDefinitionList()
    {
        return Collections.unmodifiableCollection(tdcMap.values());
    }

    @Override
    public List<TypeDefinitionContainer> getRootTypes()
    {
        List<TypeDefinitionContainer> result = new ArrayList<>();
        for (TypeDefinitionContainer tdc : tdcMap.values())
        {
            if (isRootType(tdc))
            {
                result.add(tdc);
            }
        }

        return result;
    }

    @Override
    public String getPropertyIdForQueryName(TypeDefinition typeDefinition, String propQueryName)
    {
        for (PropertyDefinition<?> pd : typeDefinition.getPropertyDefinitions().values())
        {
            if (pd.getQueryName().equals(propQueryName))
            {
                return pd.getId();
            }
        }

        return null;
    }

    @Override
    public void addTypeDefinition(TypeDefinition typeDefinition, boolean addInheritedProperties)
    {
        // create copy of new type definition
        MutableTypeDefinition newType = typeDefinitionFactory.copy(typeDefinition, true);
        // create new container
        TypeDefinitionContainer tdc = new TypeDefinitionContainerImpl(newType);

        if (null != typeDefinition.getParentTypeId())
        {
            //fetch parent
            TypeDefinitionContainer parentContainer = tdcMap.get(newType.getParentTypeId());

            //check if we should inherit properties from parent
            if (addInheritedProperties)
            {
                //copy properties from parent
                for (PropertyDefinition<?> pd : parentContainer.getTypeDefinition().getPropertyDefinitions().values())
                {
                    MutablePropertyDefinition<?> mpd = typeDefinitionFactory.copy(pd);
                    mpd.setIsInherited(true);
                    newType.addPropertyDefinition(mpd);
                }
            }

            //add container as children as children to parent
            parentContainer.getChildren().add(tdc);
        }

        this.tdcMap.put(tdc.getTypeDefinition().getId(), tdc);
        this.tdMap.put(newType.getId(), newType);
    }

    @Override
    public void updateTypeDefinition(TypeDefinition typeDefinition)
    {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void deleteTypeDefinition(String typeId)
    {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void loadFromPath(Path path) throws IOException, XMLStreamException
    {
        loadXmlTypeDefinitionFromStream(Files.newInputStream(path));
    }

    private boolean isRootType(TypeDefinitionContainer typeDefinitionContainer)
    {
        return typeDefinitionContainer.getTypeDefinition().getId().equals(typeDefinitionContainer.getTypeDefinition().getBaseTypeId().value());
    }

    private void loadXmlTypeDefinitionFromStream(InputStream stream) throws IOException, XMLStreamException
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("Stream is null!");
        }

        TypeDefinition type = null;

        XMLStreamReader parser = null;
        try
        {
            parser = XMLUtils.createParser(stream);
            if (!XMLUtils.findNextStartElemenet(parser))
            {
                return;
            }

            type = XMLConverter.convertTypeDefinition(parser);
        }
        finally
        {
            if (parser != null)
            {
                parser.close();
            }
            IOUtils.closeQuietly(stream);
        }

        addTypeDefinition(type, true);
    }

    private void saveXmlTypeDefinition(TypeDefinition typeDefinition) throws IOException {
        if (typeDefinition == null) {
            throw new IllegalArgumentException("null");
        }

        String fileName = typeDefinition.getId().replaceAll(":", "_") + ".xml";
        Path newFile = repositoryConfiguration.getConfigurationPath().resolve(fileName);
        if (Files.exists(newFile)) {
            throw new IOException(String.format("Type with id '%s' converted to filename '%s' at path '%s' already exists.",
                    typeDefinition.getId(), fileName, repositoryConfiguration.getConfigurationPath()));
        } else {
            Files.createFile(newFile);
        }

        OutputStream fos = null;
        try {
            fos = Files.newOutputStream(newFile);
            TypeUtils.writeToXML(typeDefinition, fos);
        } catch (XMLStreamException ex) {
            LOG.error(ex.getMessage());
            Files.delete(newFile);
            throw new IOException(ex);
        }
    }

    private TypeDefinitionContainer createFolderType(CmisVersion cmisVersion)
    {
        MutableFolderTypeDefinition folderTypeDefinition = typeDefinitionFactory.createBaseFolderTypeDefinition(cmisVersion);

        ((MutablePropertyIdDefinition) folderTypeDefinition.getPropertyDefinitions().get(PropertyIds.OBJECT_ID))
                .setIsOrderable(Boolean.TRUE);
        ((MutablePropertyIdDefinition) folderTypeDefinition.getPropertyDefinitions().get(PropertyIds.BASE_TYPE_ID))
                .setIsOrderable(Boolean.TRUE);

        return new TypeDefinitionContainerImpl(folderTypeDefinition);
    }

    private TypeDefinitionContainer createDocumentTypeDefinition(CmisVersion cmisVersion)
    {
        MutableDocumentTypeDefinition documentTypeDefinition = typeDefinitionFactory
                .createBaseDocumentTypeDefinition(cmisVersion);
        ((MutablePropertyIdDefinition) documentTypeDefinition.getPropertyDefinitions().get(PropertyIds.OBJECT_ID))
                .setIsOrderable(Boolean.TRUE);
        ((MutablePropertyIdDefinition) documentTypeDefinition.getPropertyDefinitions().get(PropertyIds.BASE_TYPE_ID))
                .setIsOrderable(Boolean.TRUE);


        return new TypeDefinitionContainerImpl(documentTypeDefinition);
    }
}
