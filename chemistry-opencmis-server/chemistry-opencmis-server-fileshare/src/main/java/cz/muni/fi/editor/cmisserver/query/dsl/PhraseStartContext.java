package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public interface PhraseStartContext extends StartContext
{
    KeywordFieldContext on(String field);
    KeywordFieldContext ons(String field);

    // todo wildcard ?

}
