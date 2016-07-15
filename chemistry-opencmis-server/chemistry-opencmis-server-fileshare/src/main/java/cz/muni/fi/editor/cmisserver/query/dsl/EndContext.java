package cz.muni.fi.editor.cmisserver.query.dsl;

import org.apache.lucene.search.Query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public interface EndContext
{
    Query createQuery();
}
