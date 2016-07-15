package cz.muni.fi.editor.cmisserver.query.dsl;

import org.apache.lucene.search.Query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public interface BooleanStartContext extends StartContext, EndContext
{
    enum Type{
        MUST,MUST_NOT,SHOULD
    }
    BooleanStartContext should(Query query);
    BooleanMustContext must(Query query);
}
