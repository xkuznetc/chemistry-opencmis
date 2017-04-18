package cz.muni.fi.editor.cmisserver.fileshare.impl;

import cz.muni.fi.editor.cmisserver.fileshare.Repository;
import cz.muni.fi.editor.cmisserver.fileshare.RepositoryManager;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by emptak on 2/6/17.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditorCmisService extends AbstractCmisService implements CallContextAwareCmisService
{
    private static final Logger LOG = LoggerFactory.getLogger(EditorCmisService.class);
    private final RepositoryManager repositoryManager;
    private CallContext callContext;

    public EditorCmisService(RepositoryManager repositoryManager)
    {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public CallContext getCallContext()
    {
        return this.callContext;
    }

    @Override
    public void setCallContext(CallContext callContext)
    {
        LOG.error(callContext.toString());
        this.callContext = callContext;
    }

    @Override
    public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension)
    {
        for (Repository repo : repositoryManager.getRepositories())
        {
            if (repo.getRepositoryId().equals(repositoryId))
            {
                return repo.getRepositoryInfo(getCallContext());
            }
        }

        throw new CmisObjectNotFoundException(String.format("Unknown repository '%s'!", repositoryId));
    }

    @Override
    public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension)
    {
        List<RepositoryInfo> result = new ArrayList<>();
        for(Repository repo : repositoryManager.getRepositories()){
            result.add(repo.getRepositoryInfo(getCallContext()));
        }
        return result;
    }

    @Override
    public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        return getRepository().getTypeChildren(getCallContext(), typeId, includePropertyDefinitions, maxItems,
                skipCount);
    }

    @Override
    public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension)
    {
        return getRepository().getTypeDefinition(getCallContext(), typeId);
    }

    @Override
    public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth, Boolean includePropertyDefinitions, ExtensionsData extension)
    {
        return getRepository().getTypeDescendants(getCallContext(), typeId, depth, includePropertyDefinitions);
    }

    @Override
    public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        return getRepository().getChildren(getCallContext(), folderId, filter, orderBy, includeAllowableActions,
                includePathSegment, maxItems, skipCount, this);
    }

    @Override
    public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment, ExtensionsData extension)
    {
        return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
                includePathSegment, this, false);
    }

    @Override
    public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension)
    {
        return getRepository().getFolderParent(getCallContext(), folderId, filter, this);
    }

    @Override
    public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment, ExtensionsData extension)
    {
        return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions,
                includePathSegment, this, true);
    }

    @Override
    public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includeRelativePathSegment, ExtensionsData extension)
    {
        return getRepository().getObjectParents(getCallContext(), objectId, filter, includeAllowableActions,
                includeRelativePathSegment, this);
    }

    @Override
    public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
                                        Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
                                        BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        ObjectListImpl result = new ObjectListImpl();
        result.setHasMoreItems(false);
        result.setNumItems(BigInteger.ZERO);
        List<ObjectData> emptyList = Collections.emptyList();
        result.setObjects(emptyList);

        return result;
    }

    // --- object service ---

    @Override
    public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
                         VersioningState versioningState, List<String> policies, ExtensionsData extension)
    {
        ObjectData object = getRepository().create(getCallContext(), properties, folderId, contentStream,
                versioningState, this);

        return object.getId();
    }

    @Override
    public String createDocument(String repositoryId, Properties properties, String folderId,
                                 ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
                                 Acl removeAces, ExtensionsData extension)
    {
        return getRepository().createDocument(getCallContext(), properties, folderId, contentStream, versioningState);
    }

    @Override
    public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties,
                                           String folderId, VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
                                           ExtensionsData extension)
    {
        return getRepository().createDocumentFromSource(getCallContext(), sourceId, properties, folderId,
                versioningState);
    }

    @Override
    public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
                               Acl addAces, Acl removeAces, ExtensionsData extension)
    {
        return getRepository().createFolder(getCallContext(), properties, folderId);
    }

    @Override
    public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
                                             ExtensionsData extension)
    {
        getRepository().deleteObject(getCallContext(), objectId);
    }

    @Override
    public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
                                         UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension)
    {
        return getRepository().deleteTree(getCallContext(), folderId, continueOnFailure);
    }

    @Override
    public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension)
    {
        return getRepository().getAllowableActions(getCallContext(), objectId);
    }

    @Override
    public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
                                          BigInteger length, ExtensionsData extension)
    {
        return getRepository().getContentStream(getCallContext(), objectId, offset, length);
    }

    @Override
    public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
                                IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
                                Boolean includeAcl, ExtensionsData extension)
    {
        return getRepository().getObject(getCallContext(), objectId, null, filter, includeAllowableActions, includeAcl,
                this);
    }

    @Override
    public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
                                      IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
                                      Boolean includeAcl, ExtensionsData extension)
    {
        return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions, includeAcl,
                this);
    }

    @Override
    public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension)
    {
        ObjectData object = getRepository().getObject(getCallContext(), objectId, null, filter, false, false, this);
        return object.getProperties();
    }

    @Override
    public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
                                             BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        return Collections.emptyList();
    }

    @Override
    public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
                           ExtensionsData extension)
    {
        getRepository().moveObject(getCallContext(), objectId, targetFolderId, this);
    }

    @Override
    public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
                                 Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension)
    {
        getRepository().changeContentStream(getCallContext(), objectId, overwriteFlag, contentStream, false);
    }

    @Override
    public void appendContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
                                    ContentStream contentStream, boolean isLastChunk, ExtensionsData extension)
    {
        getRepository().changeContentStream(getCallContext(), objectId, true, contentStream, true);
    }

    @Override
    public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
                                    ExtensionsData extension)
    {
        getRepository().changeContentStream(getCallContext(), objectId, true, null, false);
    }

    @Override
    public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
                                 Properties properties, ExtensionsData extension)
    {
        getRepository().updateProperties(getCallContext(), objectId, properties, this);
    }

    @Override
    public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String repositoryId,
                                                                       List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
                                                                       List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds, ExtensionsData extension)
    {
        return getRepository().bulkUpdateProperties(getCallContext(), objectIdAndChangeToken, properties, this);
    }

    // --- versioning service ---

    @Override
    public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
                                           Boolean includeAllowableActions, ExtensionsData extension)
    {
        ObjectData theVersion = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter,
                includeAllowableActions, false, this);

        return Collections.singletonList(theVersion);
    }

    @Override
    public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
                                               Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
                                               String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension)
    {
        return getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions,
                includeAcl, this);
    }

    @Override
    public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
                                                   Boolean major, String filter, ExtensionsData extension)
    {
        ObjectData object = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, false,
                false, null);

        return object.getProperties();
    }

    @Override
    public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension)
    {
        return getRepository().query(repositoryId, statement, searchAllVersions, includeAllowableActions, includeRelationships, renditionFilter, maxItems, skipCount, extension);
    }

    // --- ACL service ---

    @Override
    public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension)
    {
        return getRepository().getAcl(getCallContext(), objectId);
    }

    private Repository getRepository()
    {
        return repositoryManager.getRepository(getCallContext().getRepositoryId());
    }
}
