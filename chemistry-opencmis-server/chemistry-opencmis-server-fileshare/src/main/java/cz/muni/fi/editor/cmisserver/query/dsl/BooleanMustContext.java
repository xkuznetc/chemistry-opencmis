package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 15.07.2016.
 */
public interface BooleanMustContext extends BooleanStartContext
{
    BooleanStartContext not();
}
