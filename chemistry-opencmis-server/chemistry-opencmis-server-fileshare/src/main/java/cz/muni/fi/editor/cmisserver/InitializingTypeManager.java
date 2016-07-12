package cz.muni.fi.editor.cmisserver;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface InitializingTypeManager
{
    void loadTypeDefinitionFromFile(String filename) throws IOException, XMLStreamException;

    void loadTypeDefinitionFromResource(String name) throws IOException, XMLStreamException;
}
