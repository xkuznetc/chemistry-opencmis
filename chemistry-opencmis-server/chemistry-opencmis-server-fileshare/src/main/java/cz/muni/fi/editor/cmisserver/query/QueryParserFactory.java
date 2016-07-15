package cz.muni.fi.editor.cmisserver.query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public class QueryParserFactory
{
    public QueryParser getQueryParser()
    {
        return new QueryParserImpl();
    }
}
