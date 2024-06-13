package org.luaj.vm2.core;

public enum LuaType {
    /** Type enumeration for {@code nil} */
    NIL             ("nil",             0),
    /**
     * Type enumeration for {@code light userdata}.<br>
     * For compatibility with C-based lua only
     */
    LIGHT_USERDATA  ("lightuserdata",   2),
    /** Type enumeration for {@code boolean} */
    BOOLEAN         ("boolean",         1),
    /** Type enumeration for {@code numbers} */
    NUMBER          ("number",          3),
    /** Type enumeration for {@code strings} */
    STRING          ("string",          4),
    /** Type enumeration for {@code tables} */
    TABLE           ("table",           5),
    /** Type enumeration for {@code functions} */
    FUNCTION        ("function",        6),
    /** Type enumeration for {@code userdata} */
    USERDATA        ("userdata",        7),
    /** Type enumeration for {@code threads} */
    THREAD          ("thread",          8),
    /**
     * Type enumeration for {@code unknown values}.<br>
     * For compatibility with C-based lua only
     */
    VALUE           ("value",           9),

    /**
     * Type enumeration for {@code numbers} that are {@code ints}. <br>
     * For compatibility with lua 5.1 number patch only
     */
    INT             ("",                -2),
    /**
     * Type enumeration for {@code values} that have {@code no type}.<br>
     * For lua values that have no type (e.g. weak table entries)
     */
    NONE            ("",                -1),

    ;

    public final String typeName;
    public final int ec;

    LuaType(String name, int ec) {
        this.typeName = name;
        this.ec = ec;
    }
}
