/* LuaJ JSE 22 Update
 * malfact @ June 2024
 */
package org.luaj.vm2.util;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.core.*;

import java.util.Arrays;

public class LuaConstant {

    /** LuaValue constant corresponding to lua {@code #NIL} */
    public static final LuaValue NIL = LuaNil.NIL;

    /** LuaBoolean constant corresponding to lua {@code true} */
    public static final LuaBoolean TRUE = LuaBoolean.TRUE;

    /** LuaBoolean constant corresponding to lua {@code false} */
    public static final LuaBoolean FALSE = LuaBoolean.FALSE;

    /** LuaValue constant corresponding to a {@link Varargs} list of no values*/
    public static final LuaValue NONE = LuaValue.None.NONE;

    /** LuaValue number constant equal to 0 */
    public static final LuaNumber ZERO = LuaInteger.valueOf(0);

    /** LuaValue number constant equal to 1 */
    public static final LuaNumber ONE = LuaInteger.valueOf(1);

    /** LuaValue number constant equal to -1 */
    public static final LuaNumber MINUS_ONE = LuaInteger.valueOf(-1);

    /** LuaValue array constant with no values */
    public static final LuaValue[] NOVALS = {};
    /** LuaString constant with value "" */
    public static final LuaString EMPTY_STRING = LuaValue.valueOf("");

    /** Limit on lua stack size */
    private static final int MAX_STACK = 250;

    /**
     * Array of {@link LuaConstant#NIL} values to optimize filling stacks using
     * System.arraycopy(). Must not be modified.
     */
    public static final LuaValue[] NILS = new LuaValue[MAX_STACK];

    static {
        Arrays.fill(LuaConstant.NILS, LuaConstant.NIL);
    }

    /** The variable name of the environment. */
    public static LuaString ENV = LuaValue.valueOf("_ENV");

    public static class MetaTag {

        /** LuaString constant with value "__index" for use as metatag */
        public static final LuaString INDEX = LuaValue.valueOf("__index");

        /** LuaString constant with value "__newindex" for use as metatag */
        public static final LuaString NEWINDEX = LuaValue.valueOf("__newindex");

        /** LuaString constant with value "__call" for use as metatag */
        public static final LuaString CALL = LuaValue.valueOf("__call");

        /** LuaString constant with value "__mode" for use as metatag */
        public static final LuaString MODE = LuaValue.valueOf("__mode");

        /** LuaString constant with value "__metatable" for use as metatag */
        public static final LuaString METATABLE = LuaValue.valueOf("__metatable");

        /** LuaString constant with value "__add" for use as metatag */
        public static final LuaString ADD = LuaValue.valueOf("__add");

        /** LuaString constant with value "__sub" for use as metatag */
        public static final LuaString SUB = LuaValue.valueOf("__sub");

        /** LuaString constant with value "__div" for use as metatag */
        public static final LuaString DIV = LuaValue.valueOf("__div");

        /** LuaString constant with value "__mul" for use as metatag */
        public static final LuaString MUL = LuaValue.valueOf("__mul");

        /** LuaString constant with value "__pow" for use as metatag */
        public static final LuaString POW = LuaValue.valueOf("__pow");

        /** LuaString constant with value "__mod" for use as metatag */
        public static final LuaString MOD = LuaValue.valueOf("__mod");

        /** LuaString constant with value "__unm" for use as metatag */
        public static final LuaString UNM = LuaValue.valueOf("__unm");

        /** LuaString constant with value "__len" for use as metatag */
        public static final LuaString LEN = LuaValue.valueOf("__len");

        /** LuaString constant with value "__eq" for use as metatag */
        public static final LuaString EQ = LuaValue.valueOf("__eq");

        /** LuaString constant with value "__lt" for use as metatag */
        public static final LuaString LT = LuaValue.valueOf("__lt");

        /** LuaString constant with value "__le" for use as metatag */
        public static final LuaString LE = LuaValue.valueOf("__le");

        /** LuaString constant with value "__tostring" for use as metatag */
        public static final LuaString TOSTRING = LuaValue.valueOf("__tostring");

        /** LuaString constant with value "__concat" for use as metatag */
        public static final LuaString CONCAT = LuaValue.valueOf("__concat");
    }
}
