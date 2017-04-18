package cz.muni.fi.editor.cmisserver.types;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.support.TypeManager;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by kate on 17.4.2017.
 */
public interface EditorTypeManager extends TypeManager
{
    TypeDefinition getTypeDefinition(CallContext context, String typeId);
    TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions,
                                       BigInteger maxItems, BigInteger skipCount);
    List<TypeDefinitionContainer> getTypeDescendants(CallContext context, String typeId, BigInteger depth,
                                                     Boolean includePropertyDefinitions);

    TypeDefinition createType(String repositoryId, TypeDefinition type);
    void loadFromPath(Path path) throws IOException, XMLStreamException;
}
