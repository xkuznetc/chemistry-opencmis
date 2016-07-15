package cz.muni.fi.editor.cmisserver.query;

import org.antlr.runtime.RecognitionException;
import org.apache.chemistry.opencmis.server.support.TypeManager;
import org.apache.lucene.search.Query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public interface QueryParser
{
    Query parseQuery(String statement, TypeManager typeManager) throws RecognitionException;
}
