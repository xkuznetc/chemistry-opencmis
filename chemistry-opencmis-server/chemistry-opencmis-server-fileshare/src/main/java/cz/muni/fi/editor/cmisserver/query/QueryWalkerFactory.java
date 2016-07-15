package cz.muni.fi.editor.cmisserver.query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public class QueryWalkerFactory
{
    public QueryWalker getWalker()
    {
        return new QueryWalkerImpl();
    }
}
