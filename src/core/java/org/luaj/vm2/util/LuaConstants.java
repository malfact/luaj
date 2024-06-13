/* LuaJ JSE 22 Update
 * malfact @ June 2024
 */
package org.luaj.vm2.util;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.core.LuaBoolean;
import org.luaj.vm2.core.LuaNil;
import org.luaj.vm2.core.LuaNumber;
import org.luaj.vm2.core.LuaValue;

public class LuaConstants {

    /** LuaValue constant corresponding to lua {@code nil} */
    public static final LuaValue NIL = LuaNil.NIL;

    /** LuaBoolean constant corresponding to lua {@code true} */
    public static final LuaBoolean TRUE = LuaBoolean.TRUE;

    /** LuaBoolean constant corresponding to lua {@code false} */
    public static final LuaBoolean FALSE = LuaBoolean.FALSE;

    /** LuaValue number constant equal to {@code 0} */
    public static final LuaNumber ZERO = LuaInteger.valueOf(0);

    /** LuaValue number constant equal to {@code 1} */
    public static final LuaNumber ONE = LuaInteger.valueOf(1);

    /** LuaValue number constant equal to {@code -1} */
    public static final LuaNumber MINUS_ONE = LuaInteger.valueOf(-1);
}
