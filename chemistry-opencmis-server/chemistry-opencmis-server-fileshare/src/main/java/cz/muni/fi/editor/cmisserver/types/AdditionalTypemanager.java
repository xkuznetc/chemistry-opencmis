package cz.muni.fi.editor.cmisserver.types;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.server.CallContext;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface AdditionalTypemanager
{
    TypeDefinition getTypeDefinition(CallContext context, String typeId);
    TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions,
                                       BigInteger maxItems, BigInteger skipCount);
    List<TypeDefinitionContainer> getTypeDescendants(CallContext context, String typeId, BigInteger depth,
                                                     Boolean includePropertyDefinitions);

    TypeDefinition createType(String repositoryId, TypeDefinition type);
}
