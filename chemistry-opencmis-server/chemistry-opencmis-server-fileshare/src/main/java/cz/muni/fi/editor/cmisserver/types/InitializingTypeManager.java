package cz.muni.fi.editor.cmisserver.types;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface InitializingTypeManager
{
    void loadFromPath(Path path) throws IOException, XMLStreamException;
}
