package cz.muni.fi.editor.cmisserver.query.dsl;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public interface RangeFromFieldContext extends FieldContext
{
    RangeToFieldContext from(Object value, boolean inclusive);

    RangeEndContext below(Object value, boolean inclusive);

    RangeEndContext above(Object value, boolean inclusive);

    RangeEndContext between(Object from, boolean inclusiveFrom, Object to, boolean inclusiveTo);

    interface RangeToFieldContext extends FieldContext
    {
        RangeEndContext to(Object value, boolean inclusive);
    }
}
