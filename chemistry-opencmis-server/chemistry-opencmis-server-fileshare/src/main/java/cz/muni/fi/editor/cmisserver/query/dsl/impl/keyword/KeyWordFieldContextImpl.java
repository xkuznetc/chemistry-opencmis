package cz.muni.fi.editor.cmisserver.query.dsl.impl.keyword;

import cz.muni.fi.editor.cmisserver.query.dsl.KeywordEndContext;
import cz.muni.fi.editor.cmisserver.query.dsl.KeywordFieldContext;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class KeyWordFieldContextImpl implements KeywordFieldContext
{
    private String[] fields;

    public KeyWordFieldContextImpl(String[] fields)
    {
        this.fields = fields;
    }

    @Override
    public KeywordEndContext matching(Object value)
    {
        return new KeywordEndContextImpl(fields, value);
    }
}
