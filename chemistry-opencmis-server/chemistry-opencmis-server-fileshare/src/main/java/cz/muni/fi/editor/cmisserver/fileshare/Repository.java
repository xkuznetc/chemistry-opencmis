package cz.muni.fi.editor.cmisserver.fileshare;

import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.chemistry.opencmis.commons.spi.Holder;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by emptak on 2/6/17.
 */
public interface Repository
{
    String getRepositoryId();

    File getRootDirectory();

    RepositoryInfo getRepositoryInfo(CallContext context);

    TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions,
                                       BigInteger maxItems, BigInteger skipCount);

    List<TypeDefinitionContainer> getTypeDescendants(CallContext context, String typeId, BigInteger depth,
                                                     Boolean includePropertyDefinitions);

    TypeDefinition getTypeDefinition(CallContext context, String typeId);

    ObjectData create(CallContext context, Properties properties, String folderId, ContentStream contentStream,
                      VersioningState versioningState, ObjectInfoHandler objectInfos);

    String createDocument(CallContext context, Properties properties, String folderId,
                          ContentStream contentStream, VersioningState versioningState);

    String createDocumentFromSource(CallContext context, String sourceId, Properties properties,
                                    String folderId, VersioningState versioningState);

    String createFolder(CallContext context, Properties properties, String folderId);

    ObjectData moveObject(CallContext context, Holder<String> objectId, String targetFolderId,
                          ObjectInfoHandler objectInfos);

    void changeContentStream(CallContext context, Holder<String> objectId, Boolean overwriteFlag,
                             ContentStream contentStream, boolean append);

    void deleteObject(CallContext context, String objectId);

    FailedToDeleteData deleteTree(CallContext context, String folderId, Boolean continueOnFailure);

    ObjectData updateProperties(CallContext context, Holder<String> objectId, Properties properties,
                                ObjectInfoHandler objectInfos);

    List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(CallContext context,
                                                                List<BulkUpdateObjectIdAndChangeToken> objectIdAndChangeToken, Properties properties,
                                                                ObjectInfoHandler objectInfos);

    ObjectData getObject(CallContext context, String objectId, String versionServicesId, String filter,
                         Boolean includeAllowableActions, Boolean includeAcl, ObjectInfoHandler objectInfos);

    AllowableActions getAllowableActions(CallContext context, String objectId);

    Acl getAcl(CallContext context, String objectId);

    ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length);

    ObjectInFolderList getChildren(CallContext context, String folderId, String filter, String orderBy,
                                   Boolean includeAllowableActions, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount,
                                   ObjectInfoHandler objectInfos);

    List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth,
                                                 String filter, Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos,
                                                 boolean foldersOnly);

    ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos);

    List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter,
                                            Boolean includeAllowableActions, Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos);

    ObjectData getObjectByPath(CallContext context, String folderPath, String filter,
                               boolean includeAllowableActions, boolean includeACL, ObjectInfoHandler objectInfos);

    ObjectList query(String repositoryId, String statement, Boolean searchAllVersions, Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension);

    TypeDefinition createType(String repositoryId, TypeDefinition type);
}
