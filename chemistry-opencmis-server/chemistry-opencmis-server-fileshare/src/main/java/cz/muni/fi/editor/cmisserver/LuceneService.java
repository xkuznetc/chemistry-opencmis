package cz.muni.fi.editor.cmisserver;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.List;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 12.07.2016.
 */
public interface LuceneService
{
    /**
     * Method adds single document into lucene index
     *
     * @param document
     */
    void add(Document document);

    /**
     * Method adds multiple documents into locene index
     *
     * @param documents
     */
    void add(List<Document> documents);

    /**
     * Method return list of ids matching requested query
     *
     * @param query
     * @return
     */
    List<String> search(Query query);


    void close() throws IOException;

    void delete(Document document);
}
