package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public interface PhraseFieldContext extends FieldContext
{
    KeywordEndContext matching(Object value);
}
