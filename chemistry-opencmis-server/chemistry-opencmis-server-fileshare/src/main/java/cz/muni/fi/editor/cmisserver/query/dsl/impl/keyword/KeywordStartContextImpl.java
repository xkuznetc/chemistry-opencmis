package cz.muni.fi.editor.cmisserver.query.dsl.impl.keyword;

import cz.muni.fi.editor.cmisserver.query.dsl.KeywordFieldContext;
import cz.muni.fi.editor.cmisserver.query.dsl.KeywordStartContext;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class KeywordStartContextImpl implements KeywordStartContext
{
    @Override
    public KeywordFieldContext on(String field)
    {
        return new KeyWordFieldContextImpl(escapeFields(new String[]{field}));
    }

    @Override
    public KeywordFieldContext ons(String... fields)
    {
        return new KeyWordFieldContextImpl(escapeFields(fields));
    }

    /**
     * If lucene field contains one of following:
     * <ul>
     * <li>\</li>
     * <li>+</li>
     * <li>-</li>
     * <li>!</li>
     * <li>(</li>
     * <li>)</li>
     * <li>:</li>
     * <li>^</li>
     * <li>]</li>
     * <li>}</li>
     * <li>}</li>
     * <li>~</li>
     * <li>*</li>
     * <li>?</li>
     * </ul>
     * <p>
     * then it must be properly escaped with backslash <b>\</b>. Since cmis fields in our use case contain only <b>:</b>
     * we escape only those
     *
     * @param original field which may contain <b>:</b> symbol needed to be escaped
     * @return escape fields
     */
    private String[] escapeFields(String[] original)
    {
        String[] result = new String[original.length];

        for (int i = 0; i < original.length; i++)
        {
            result[i] = original[i].replace(":", "\\:");
        }

        return result;
    }
}
