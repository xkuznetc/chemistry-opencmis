package cz.muni.fi.editor.cmisserver.types;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;

import java.util.Collection;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface JSPTypeManager extends EditorTypeManager
{
    Collection<TypeDefinition> getInternalTypeDefinitions();
}
