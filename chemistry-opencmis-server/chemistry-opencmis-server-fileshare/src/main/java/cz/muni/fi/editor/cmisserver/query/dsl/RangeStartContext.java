package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public interface RangeStartContext extends StartContext
{
    RangeFromFieldContext on(String field);
    RangeFromFieldContext ons(String field);
}
