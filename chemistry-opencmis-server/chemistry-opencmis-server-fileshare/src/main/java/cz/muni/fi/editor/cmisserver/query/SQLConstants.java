package cz.muni.fi.editor.cmisserver.query;

/**
 * @author Dominik Szalai - emptulik at gmail.com on 13.07.2016.
 */
public class SQLConstants
{
    // column
    public static final int COL = 12;

    public static class COMPARATORS
    {
        public static final int EQ = 19;
        public static final int GT = 24;
        public static final int GTEQ = 25;
        public static final int LT = 40;
        public static final int LTEQ = 41;
        public static final int NEQ = 42;
    }


    public static class LITERALS
    {
        public static final int BOOL_LIT = 10;
        public static final int NUMBER_LIT = 48;
        public static final int STRING_LIT = 62;
        public static final int TIME_LIT = 66;
    }

    public static class LOGIC
    {
        public static final int AND = 4;
        public static final int OR = 50;
    }


}
