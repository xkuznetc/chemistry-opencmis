package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public class QueryBuilderFactory
{
    public QueryBuilder qb(){
        return new QueryBuilderImpl();
    }
}
