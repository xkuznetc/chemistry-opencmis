package cz.muni.fi.editor.cmisserver.lucene;

import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.lucene.document.Document;

import java.util.List;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public interface DocumentToCmisService
{
    ObjectListImpl convert(List<Document> documents);
}
