package cz.muni.fi.editor.cmisserver;

import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;

import java.util.Collection;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface JSPTypeManager
{
    Collection<TypeDefinition> getInternalTypeDefinitions();
}
