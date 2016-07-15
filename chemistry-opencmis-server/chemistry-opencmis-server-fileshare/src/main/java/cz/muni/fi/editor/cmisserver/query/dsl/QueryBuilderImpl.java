package cz.muni.fi.editor.cmisserver.query.dsl;

import cz.muni.fi.editor.cmisserver.query.dsl.impl.bool.BooleanContext;
import cz.muni.fi.editor.cmisserver.query.dsl.impl.keyword.KeywordStartContextImpl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class QueryBuilderImpl implements QueryBuilder
{
    @Override
    public KeywordStartContext keyword()
    {
        return new KeywordStartContextImpl();
    }

    @Override
    public RangeStartContext range()
    {
        return null;
    }

    @Override
    public BooleanStartContext bool()
    {
        return new BooleanContext();
    }
}
