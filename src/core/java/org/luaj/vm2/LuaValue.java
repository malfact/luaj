/* LuaJ JSE 22 Update
 * malfact @ June 2024
 */
package org.luaj.vm2;

/**
 * Base class for all concrete lua type values.
 * <p>
 * Establishes base implementations for all the operations on lua types. This
 * allows Java clients to deal essentially with one type for all Java values,
 * namely {@link LuaValue}.
 * <p>
 * Constructors are provided as static methods for common Java types, such as
 * {@link LuaValue#valueOf(int)} or {@link LuaValue#valueOf(String)} to allow
 * for instance pooling.
 * <p>
 * Constants are defined for the lua values {@link LuaConstant#NIL}, {@link LuaConstant#TRUE}, and
 * {@link LuaConstant#FALSE}. A constant {@link LuaConstant#NONE} is defined which is a
 * {@link Varargs} list having no values.
 * <p>
 * Operations are performed on values directly via their Java methods. For
 * example, the following code divides two numbers:
 *
 * <pre>
 * {
 * 	&#64;code
 * 	LuaValue a = LuaValue.valueOf(5);
 * 	LuaValue b = LuaValue.valueOf(4);
 * 	LuaValue c = a.div(b);
 * }
 * </pre>
 * <p>
 * Note that in this example, c will be a {@link LuaDouble}, but would be a
 * {@link LuaInteger} if the value of a were changed to 8, say. In general the
 * value of c in practice will vary depending on both the types and values of a
 * and b as well as any metatable/metatag processing that occurs.
 * <p>
 * Field access and function calls are similar, with common overloads to
 * simplify Java usage:
 *
 * <pre>
 * {
 * 	&#64;code
 * 	LuaValue globals = JsePlatform.standardGlobals();
 * 	LuaValue sqrt = globals.get("math").get("sqrt");
 * 	LuaValue print = globals.get("print");
 * 	LuaValue d = sqrt.call(a);
 * 	print.call(LuaValue.valueOf("sqrt(5):"), a);
 * }
 * </pre>
 * <p>
 * To supply variable arguments or get multiple return values, use
 * {@link #invoke(Varargs)} or {@link #invokemethod(LuaValue, Varargs)} methods:
 *
 * <pre>
 * {
 * 	&#64;code
 * 	LuaValue modf = globals.get("math").get("modf");
 * 	Varargs r = modf.invoke(d);
 * 	print.call(r.arg(1), r.arg(2));
 * }
 * </pre>
 * <p>
 * To load and run a script, {@link LoadState} is used:
 *
 * <pre>
 *  {@code
 * LoadState.load( new FileInputStream("main.lua"), "main.lua", globals ).call();
 * }
 * </pre>
 * <p>
 * although {@code require} could also be used:
 *
 * <pre>
 *  {@code
 * globals.get("require").call(LuaValue.valueOf("main"));
 * }
 * </pre>
 * <p>
 * For this to work the file must be in the current directory, or in the class
 * path, dependening on the platform. See
 * {@link org.luaj.vm2.lib.jse.JsePlatform} and
 * <p>
 * In general a {@link LuaError} may be thrown on any operation when the types
 * supplied to any operation are illegal from a lua perspective. Examples could
 * be attempting to concatenate a NIL value, or attempting arithmetic on values
 * that are not number.
 * <p>
 * There are several methods for preinitializing tables, such as:
 * <ul>
 * <li>{@link #listOf(LuaValue[])} for unnamed elements</li>
 * <li>{@link #tableOf(LuaValue[])} for named elements</li>
 * <li>{@link #tableOf(LuaValue[], LuaValue[], Varargs)} for mixtures</li>
 * </ul>
 * <p>
 * Predefined enums exist for the standard lua type constants {@link LuaType#NIL},
 * {@link LuaType#BOOLEAN}, {@link LuaType#LIGHT_USERDATA}, {@link LuaType#NUMBER},
 * {@link LuaType#STRING}, {@link LuaType#TABLE}, {@link LuaType#FUNCTION}, {@link LuaType#USERDATA},
 * {@link LuaType#THREAD}, and extended lua type constants {@link LuaType#INT},
 * {@link LuaType#NONE}, {@link LuaType#VALUE}
 * <p>
 * Predefined constants exist for all strings used as metatags: {@link LuaConstant.MetaTag#INDEX},
 * {@link LuaConstant.MetaTag#NEWINDEX}, {@link LuaConstant.MetaTag#CALL}, {@link LuaConstant.MetaTag#MODE}, {@link LuaConstant.MetaTag#METATABLE},
 * {@link LuaConstant.MetaTag#ADD}, {@link LuaConstant.MetaTag#SUB}, {@link LuaConstant.MetaTag#DIV}, {@link LuaConstant.MetaTag#MUL}, {@link LuaConstant.MetaTag#POW},
 * {@link LuaConstant.MetaTag#MOD}, {@link LuaConstant.MetaTag#UNM}, {@link LuaConstant.MetaTag#LEN}, {@link LuaConstant.MetaTag#EQ}, {@link LuaConstant.MetaTag#LT},
 * {@link LuaConstant.MetaTag#LE}, {@link LuaConstant.MetaTag#TOSTRING}, and {@link LuaConstant.MetaTag#CONCAT}.
 *
 * @see org.luaj.vm2.lib.jse.JsePlatform
 * @see LoadState
 * @see Varargs
 */
public abstract class LuaValue extends Varargs {

    /**
     * Get the type of this value.
     *
     * @return the {@link LuaType} of this value.
     * @see #isType(LuaType)
     */
    public abstract LuaType getType();

    /**
     * Check if this value is a LuaType
     *
     * @param luaType the {@link LuaType} to check against
     * @return if the type matches then {@code true} , otherwise {@code false}
     */
    public boolean isType(LuaType luaType) {
        return luaType == this.getType();
    }

    /**
     * Check if this value is an instance of a LuaValue class
     *
     * @param luaClass the child class to check
     * @return if this is an instance then {@code true}, otherwise {@code false}
     */
    public boolean isType(Class<? extends LuaValue> luaClass) {
        return luaClass.isInstance(this);
    }

    /**
     * Casts this value to the provided class
     *
     * @param luaClass The class to cast to (inherited from {@code LuaValue}
     * @return this casted
     */
    public final <T extends LuaValue> T to(Class<T> luaClass) {
        if (!luaClass.isInstance(this))
            error(getType().typeName + " is not castable to" + luaClass.getName());

        return luaClass.cast(this);
    }

    /**
     * Check if {@code this} is a {@code boolean}
     *
     * @return true if this is a {@code boolean}, otherwise false
     * @see #isboolean()
     * @see #toboolean()
     * @see #checkboolean()
     * @see #optboolean(boolean)
     * @see LuaType#BOOLEAN
     */
    public boolean isboolean() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code function} that is a closure, meaning
     * interprets lua bytecode for its execution
     *
     * @return true if this is a {@code closure}, otherwise false
     * @see #isfunction()
     * @see #checkclosure()
     * @see #optclosure(LuaClosure)
     * @see LuaType#FUNCTION
     */
    public boolean isclosure() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code function}
     *
     * @return true if this is a {@code function}, otherwise false
     * @see #isclosure()
     * @see #checkfunction()
     * @see #optfunction(LuaFunction)
     * @see LuaType#FUNCTION
     */
    public boolean isfunction() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code number} and is representable by java
     * int without rounding or truncation
     *
     * @return true if this is a {@code number} meaning derives from
     * {@link LuaNumber} or derives from {@link LuaString} and is
     * convertible to a number, and can be represented by int, otherwise
     * false
     * @see #isinttype()
     * @see #islong()
     * @see #tonumber()
     * @see #checkint()
     * @see #optint(int)
     * @see LuaType#NUMBER
     */
    public boolean isint() {
        return false;
    }

    /**
     * Check if {@code this} is a {@link LuaInteger}
     * <p>
     * No attempt to convert from string will be made by this call.
     *
     * @return true if this is a {@code LuaInteger}, otherwise false
     * @see #isint()
     * @see #isnumber()
     * @see #tonumber()
     * @see LuaType#NUMBER
     */
    public boolean isinttype() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code number} and is representable by java
     * long without rounding or truncation
     *
     * @return true if this is a {@code number} meaning derives from
     * {@link LuaNumber} or derives from {@link LuaString} and is
     * convertible to a number, and can be represented by long,
     * otherwise false
     * @see #tonumber()
     * @see #checklong()
     * @see #optlong(long)
     * @see LuaType#NUMBER
     */
    public boolean islong() {
        return false;
    }

    /**
     * Check if {@code this} is {@code #NIL}
     *
     * @return true if this is {@code #NIL}, otherwise false
     * @see LuaConstant#NIL
     * @see LuaConstant#NONE
     * @see #checknotnil()
     * @see #optvalue(LuaValue)
     * @see Varargs#isnoneornil(int)
     * @see LuaType#NIL
     * @see LuaType#NONE
     */
    public boolean isnil() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code number}
     *
     * @return true if this is a {@code number}, meaning derives from
     * {@link LuaNumber} or derives from {@link LuaString} and is
     * convertible to a number, otherwise false
     * @see #tonumber()
     * @see #checknumber()
     * @see #optnumber(LuaNumber)
     * @see LuaType#NUMBER
     */
    public boolean isnumber() {
        return false;
    } // may convert from string

    /**
     * Check if {@code this} is a {@code string}
     *
     * @return true if this is a {@code string}, meaning derives from
     * {@link LuaString} or {@link LuaNumber}, otherwise false
     * @see #tostring()
     * @see #checkstring()
     * @see #optstring(LuaString)
     * @see LuaType#STRING
     */
    public boolean isstring() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code thread}
     *
     * @return true if this is a {@code thread}, otherwise false
     * @see #checkthread()
     * @see #optthread(LuaThread)
     * @see LuaType#THREAD
     */
    public boolean isthread() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code table}
     *
     * @return true if this is a {@code table}, otherwise false
     * @see #checktable()
     * @see #opttable(LuaTable)
     * @see LuaType#TABLE
     */
    public boolean istable() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code userdata}
     *
     * @return true if this is a {@code userdata}, otherwise false
     * @see #isuserdata(Class)
     * @see #touserdata()
     * @see #checkuserdata()
     * @see #optuserdata(Object)
     * @see LuaType#USERDATA
     */
    public boolean isuserdata() {
        return false;
    }

    /**
     * Check if {@code this} is a {@code userdata} of type {@code c}
     *
     * @param c Class to test instance against
     * @return true if this is a {@code userdata} and the instance is assignable
     * to {@code c}, otherwise false
     * @see #isuserdata()
     * @see #touserdata(Class)
     * @see #checkuserdata(Class)
     * @see #optuserdata(Class, Object)
     * @see LuaType#USERDATA
     */
    public boolean isuserdata(Class c) {
        return false;
    }

    /**
     * Convert to boolean false if {@link LuaConstant#NIL} or {@link LuaConstant#FALSE}, true if
     * anything else
     *
     * @return Value cast to byte if number or string convertible to number,
     * otherwise 0
     * @see #optboolean(boolean)
     * @see #checkboolean()
     * @see #isboolean()
     * @see LuaType#BOOLEAN
     */
    public boolean toboolean() {
        return true;
    }

    /**
     * Convert to byte if numeric, or 0 if not.
     *
     * @return Value cast to byte if number or string convertible to number,
     * otherwise 0
     * @see #toint()
     * @see #todouble()
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public byte tobyte() {
        return 0;
    }

    /**
     * Convert to char if numeric, or 0 if not.
     *
     * @return Value cast to char if number or string convertible to number,
     * otherwise 0
     * @see #toint()
     * @see #todouble()
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public char tochar() {
        return 0;
    }

    /**
     * Convert to double if numeric, or 0 if not.
     *
     * @return Value cast to double if number or string convertible to number,
     * otherwise 0
     * @see #toint()
     * @see #tobyte()
     * @see #tochar()
     * @see #toshort()
     * @see #tolong()
     * @see #tofloat()
     * @see #optdouble(double)
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public double todouble() {
        return 0;
    }

    /**
     * Convert to float if numeric, or 0 if not.
     *
     * @return Value cast to float if number or string convertible to number,
     * otherwise 0
     * @see #toint()
     * @see #todouble()
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public float tofloat() {
        return 0;
    }

    /**
     * Convert to int if numeric, or 0 if not.
     *
     * @return Value cast to int if number or string convertible to number,
     * otherwise 0
     * @see #tobyte()
     * @see #tochar()
     * @see #toshort()
     * @see #tolong()
     * @see #tofloat()
     * @see #todouble()
     * @see #optint(int)
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public int toint() {
        return 0;
    }

    /**
     * Convert to long if numeric, or 0 if not.
     *
     * @return Value cast to long if number or string convertible to number,
     * otherwise 0
     * @see #isint()
     * @see #isinttype()
     * @see #toint()
     * @see #todouble()
     * @see #optlong(long)
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public long tolong() {
        return 0;
    }

    /**
     * Convert to short if numeric, or 0 if not.
     *
     * @return Value cast to short if number or string convertible to number,
     * otherwise 0
     * @see #toint()
     * @see #todouble()
     * @see #checknumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public short toshort() {
        return 0;
    }

    /**
     * Convert to human readable String for any type.
     *
     * @return String for use by human readers based on type.
     * @see #tostring()
     * @see #optjstring(String)
     * @see #checkjstring()
     * @see #isstring()
     * @see LuaType#STRING
     */
    @Override
    public String tojstring() {
        return getType().typeName + ": " + Integer.toHexString(hashCode());
    }

    /**
     * Convert to userdata instance, or null.
     *
     * @return userdata instance if userdata, or null if not {@link LuaUserdata}
     * @see #optuserdata(Object)
     * @see #checkuserdata()
     * @see #isuserdata()
     * @see LuaType#USERDATA
     */
    public Object touserdata() {
        return null;
    }

    /**
     * Convert to userdata instance if specific type, or null.
     *
     * @return userdata instance if is a userdata whose instance derives from
     * {@code c}, or null if not {@link LuaUserdata}
     * @see #optuserdata(Class, Object)
     * @see #checkuserdata(Class)
     * @see #isuserdata(Class)
     * @see LuaType#USERDATA
     */
    public Object touserdata(Class c) {
        return null;
    }

    /**
     * Convert the value to a human readable string using {@link #tojstring()}
     *
     * @return String value intended to be human readible.
     * @see #tostring()
     * @see #tojstring()
     * @see #optstring(LuaString)
     * @see #checkstring()
     * @see #toString()
     */
    @Override
    public String toString() {
        return tojstring();
    }

    /**
     * Conditionally convert to lua number without throwing errors.
     * <p>
     * In lua all numbers are strings, but not all strings are numbers. This
     * function will return the {@link LuaValue} {@code this} if it is a number
     * or a string convertible to a number, and {@link LuaConstant#NIL} for all other
     * cases.
     * <p>
     * This allows values to be tested for their "numeric-ness" without the
     * penalty of throwing exceptions, nor the cost of converting the type and
     * creating storage for it.
     *
     * @return {@code this} if it is a {@link LuaNumber} or {@link LuaString}
     * that can be converted to a number, otherwise {@link LuaConstant#NIL}
     * @see #tostring()
     * @see #optnumber(LuaNumber)
     * @see #checknumber()
     * @see #toint()
     * @see #todouble()
     */
    public LuaValue tonumber() {
        return LuaConstant.NIL;
    }

    /**
     * Conditionally convert to lua string without throwing errors.
     * <p>
     * In lua all numbers are strings, so this function will return the
     * {@link LuaValue} {@code this} if it is a string or number, and
     * {@link LuaConstant#NIL} for all other cases.
     * <p>
     * This allows values to be tested for their "string-ness" without the
     * penalty of throwing exceptions.
     *
     * @return {@code this} if it is a {@link LuaString} or {@link LuaNumber},
     * otherwise {@link LuaConstant#NIL}
     * @see #tonumber()
     * @see #tojstring()
     * @see #optstring(LuaString)
     * @see #checkstring()
     * @see #toString()
     */
    public LuaValue tostring() {
        return LuaConstant.NIL;
    }

    /**
     * Check that optional argument is a boolean and return its boolean value
     *
     * @param defval boolean value to return if {@code this} is nil or none
     * @return {@code this} cast to boolean if a {@link LuaBoolean},
     * {@code defval} if nil or none, throws {@link LuaError} otherwise
     * @throws LuaError if was not a boolean or nil or none.
     * @see #checkboolean()
     * @see #isboolean()
     * @see LuaType#BOOLEAN
     */
    public boolean optboolean(boolean defval) {
        argumentError("boolean");
        return false;
    }

    /**
     * Check that optional argument is a closure and return as
     * {@link LuaClosure}
     * <p>
     * A {@link LuaClosure} is a {@link LuaFunction} that executes lua
     * byteccode.
     *
     * @param defval {@link LuaClosure} to return if {@code this} is nil or none
     * @return {@code this} cast to {@link LuaClosure} if a function,
     * {@code defval} if nil or none, throws {@link LuaError} otherwise
     * @throws LuaError if was not a closure or nil or none.
     * @see #checkclosure()
     * @see #isclosure()
     * @see LuaType#FUNCTION
     */
    public LuaClosure optclosure(LuaClosure defval) {
        argumentError("closure");
        return null;
    }

    /**
     * Check that optional argument is a number or string convertible to number
     * and return as double
     *
     * @param defval double to return if {@code this} is nil or none
     * @return {@code this} cast to double if numeric, {@code defval} if nil or
     * none, throws {@link LuaError} otherwise
     * @throws LuaError if was not numeric or nil or none.
     * @see #optint(int)
     * @see #optinteger(LuaInteger)
     * @see #checkdouble()
     * @see #todouble()
     * @see #tonumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public double optdouble(double defval) {
        argumentError("number");
        return 0;
    }

    /**
     * Check that optional argument is a function and return as
     * {@link LuaFunction}
     * <p>
     * A {@link LuaFunction} may either be a Java function that implements
     * functionality directly in Java, or a {@link LuaClosure} which is a
     * {@link LuaFunction} that executes lua bytecode.
     *
     * @param defval {@link LuaFunction} to return if {@code this} is nil or
     *               none
     * @return {@code this} cast to {@link LuaFunction} if a function,
     * {@code defval} if nil or none, throws {@link LuaError} otherwise
     * @throws LuaError if was not a function or nil or none.
     * @see #checkfunction()
     * @see #isfunction()
     * @see LuaType#FUNCTION
     */
    public LuaFunction optfunction(LuaFunction defval) {
        argumentError("function");
        return null;
    }

    /**
     * Check that optional argument is a number or string convertible to number
     * and return as int
     *
     * @param defval int to return if {@code this} is nil or none
     * @return {@code this} cast to int if numeric, {@code defval} if nil or
     * none, throws {@link LuaError} otherwise
     * @throws LuaError if was not numeric or nil or none.
     * @see #optdouble(double)
     * @see #optlong(long)
     * @see #optinteger(LuaInteger)
     * @see #checkint()
     * @see #toint()
     * @see #tonumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public int optint(int defval) {
        argumentError("int");
        return 0;
    }

    /**
     * Check that optional argument is a number or string convertible to number
     * and return as {@link LuaInteger}
     *
     * @param defval {@link LuaInteger} to return if {@code this} is nil or none
     * @return {@code this} converted and wrapped in {@link LuaInteger} if
     * numeric, {@code defval} if nil or none, throws {@link LuaError}
     * otherwise
     * @throws LuaError if was not numeric or nil or none.
     * @see #optdouble(double)
     * @see #optint(int)
     * @see #checkint()
     * @see #toint()
     * @see #tonumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public LuaInteger optinteger(LuaInteger defval) {
        argumentError("integer");
        return null;
    }

    /**
     * Check that optional argument is a number or string convertible to number
     * and return as long
     *
     * @param defval long to return if {@code this} is nil or none
     * @return {@code this} cast to long if numeric, {@code defval} if nil or
     * none, throws {@link LuaError} otherwise
     * @throws LuaError if was not numeric or nil or none.
     * @see #optdouble(double)
     * @see #optint(int)
     * @see #checkint()
     * @see #toint()
     * @see #tonumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public long optlong(long defval) {
        argumentError("long");
        return 0;
    }

    /**
     * Check that optional argument is a number or string convertible to number
     * and return as {@link LuaNumber}
     *
     * @param defval {@link LuaNumber} to return if {@code this} is nil or none
     * @return {@code this} cast to {@link LuaNumber} if numeric, {@code defval}
     * if nil or none, throws {@link LuaError} otherwise
     * @throws LuaError if was not numeric or nil or none.
     * @see #optdouble(double)
     * @see #optlong(long)
     * @see #optint(int)
     * @see #checkint()
     * @see #toint()
     * @see #tonumber()
     * @see #isnumber()
     * @see LuaType#NUMBER
     */
    public LuaNumber optnumber(LuaNumber defval) {
        argumentError("number");
        return null;
    }

    /**
     * Check that optional argument is a string or number and return as Java
     * String
     *
     * @param defval {@link LuaString} to return if {@code this} is nil or none
     * @return {@code this} converted to String if a string or number,
     * {@code defval} if nil or none, throws {@link LuaError} if some
     * other type
     * @throws LuaError if was not a string or number or nil or none.
     * @see #tojstring()
     * @see #optstring(LuaString)
     * @see #checkjstring()
     * @see #toString()
     * @see LuaType#STRING
     */
    public String optjstring(String defval) {
        argumentError("string");
        return null;
    }

    /**
     * Check that optional argument is a string or number and return as
     * {@link LuaString}
     *
     * @param defval {@link LuaString} to return if {@code this} is nil or none
     * @return {@code this} converted to {@link LuaString} if a string or
     * number, {@code defval} if nil or none, throws {@link LuaError} if
     * some other type
     * @throws LuaError if was not a string or number or nil or none.
     * @see #tojstring()
     * @see #optjstring(String)
     * @see #checkstring()
     * @see #toString()
     * @see LuaType#STRING
     */
    public LuaString optstring(LuaString defval) {
        argumentError("string");
        return null;
    }

    /**
     * Check that optional argument is a table and return as {@link LuaTable}
     *
     * @param defval {@link LuaTable} to return if {@code this} is nil or none
     * @return {@code this} cast to {@link LuaTable} if a table, {@code defval}
     * if nil or none, throws {@link LuaError} if some other type
     * @throws LuaError if was not a table or nil or none.
     * @see #checktable()
     * @see #istable()
     * @see LuaType#TABLE
     */
    public LuaTable opttable(LuaTable defval) {
        argumentError("table");
        return null;
    }

    /**
     * Check that optional argument is a thread and return as {@link LuaThread}
     *
     * @param defval {@link LuaThread} to return if {@code this} is nil or none
     * @return {@code this} cast to {@link LuaTable} if a thread, {@code defval}
     * if nil or none, throws {@link LuaError} if some other type
     * @throws LuaError if was not a thread or nil or none.
     * @see #checkthread()
     * @see #isthread()
     * @see LuaType#THREAD
     */
    public LuaThread optthread(LuaThread defval) {
        argumentError("thread");
        return null;
    }

    /**
     * Check that optional argument is a userdata and return the Object instance
     *
     * @param defval Object to return if {@code this} is nil or none
     * @return Object instance of the userdata if a {@link LuaUserdata},
     * {@code defval} if nil or none, throws {@link LuaError} if some
     * other type
     * @throws LuaError if was not a userdata or nil or none.
     * @see #checkuserdata()
     * @see #isuserdata()
     * @see #optuserdata(Class, Object)
     * @see LuaType#USERDATA
     */
    public Object optuserdata(Object defval) {
        argumentError("object");
        return null;
    }

    /**
     * Check that optional argument is a userdata whose instance is of a type
     * and return the Object instance
     *
     * @param c      Class to test userdata instance against
     * @param defval Object to return if {@code this} is nil or none
     * @return Object instance of the userdata if a {@link LuaUserdata} and
     * instance is assignable to {@code c}, {@code defval} if nil or
     * none, throws {@link LuaError} if some other type
     * @throws LuaError if was not a userdata whose instance is assignable to
     *                  {@code c} or nil or none.
     * @see #checkuserdata(Class)
     * @see #isuserdata(Class)
     * @see #optuserdata(Object)
     * @see LuaType#USERDATA
     */
    public Object optuserdata(Class c, Object defval) {
        argumentError(c.getName());
        return null;
    }

    /**
     * Perform argument check that this is not nil or none.
     *
     * @param defval {@link LuaValue} to return if {@code this} is nil or none
     * @return {@code this} if not nil or none, else {@code defval}
     * @see LuaConstant#NIL
     * @see LuaConstant#NONE
     * @see #isnil()
     * @see Varargs#isnoneornil(int)
     * @see LuaType#NIL
     * @see LuaType#NONE
     */
    public LuaValue optvalue(LuaValue defval) {
        return this;
    }

    /**
     * Check that the value is a {@link LuaBoolean}, or throw {@link LuaError}
     * if not
     *
     * @return boolean value for {@code this} if it is a {@link LuaBoolean}
     * @throws LuaError if not a {@link LuaBoolean}
     * @see #optboolean(boolean)
     * @see LuaType#BOOLEAN
     */
    public boolean checkboolean() {
        argumentError("boolean");
        return false;
    }

    /**
     * Check that the value is a {@link LuaClosure} , or throw {@link LuaError}
     * if not
     * <p>
     * {@link LuaClosure} is a subclass of {@link LuaFunction} that interprets
     * lua bytecode.
     *
     * @return {@code this} cast as {@link LuaClosure}
     * @throws LuaError if not a {@link LuaClosure}
     * @see #checkfunction()
     * @see #optclosure(LuaClosure)
     * @see #isclosure()
     * @see LuaType#FUNCTION
     */
    public LuaClosure checkclosure() {
        argumentError("function");
        return null;
    }

    /**
     * Check that the value is numeric and return the value as a double, or
     * throw {@link LuaError} if not numeric
     * <p>
     * Values that are {@link LuaNumber} and values that are {@link LuaString}
     * that can be converted to a number will be converted to double.
     *
     * @return value cast to a double if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkint()
     * @see #checkinteger()
     * @see #checklong()
     * @see #optdouble(double)
     * @see LuaType#NUMBER
     */
    public double checkdouble() {
        argumentError("number");
        return 0;
    }

    /**
     * Check that the value is a function , or throw {@link LuaError} if not
     * <p>
     * A {@link LuaFunction} may either be a Java function that implements
     * functionality directly in Java, or a {@link LuaClosure} which is a
     * {@link LuaFunction} that executes lua bytecode.
     *
     * @return {@code this} if it is a lua function or closure
     * @throws LuaError if not a function
     * @see #checkclosure()
     */
    public LuaFunction checkfunction() {
        argumentError("function");
        return null;
    }

    /**
     * Check that the value is a Globals instance, or throw {@link LuaError} if
     * not
     * <p>
     * {@link Globals} are a special {@link LuaTable} that establish the default
     * global environment.
     *
     * @return {@code this} if if an instance fof {@link Globals}
     * @throws LuaError if not a {@link Globals} instance.
     */
    public Globals checkglobals() {
        argumentError("globals");
        return null;
    }

    /**
     * Check that the value is numeric, and convert and cast value to int, or
     * throw {@link LuaError} if not numeric
     * <p>
     * Values that are {@link LuaNumber} will be cast to int and may lose
     * precision. Values that are {@link LuaString} that can be converted to a
     * number will be converted, then cast to int, so may also lose precision.
     *
     * @return value cast to a int if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkinteger()
     * @see #checklong()
     * @see #checkdouble()
     * @see #optint(int)
     * @see LuaType#NUMBER
     */
    public int checkint() {
        argumentError("number");
        return 0;
    }

    /**
     * Check that the value is numeric, and convert and cast value to int, or
     * throw {@link LuaError} if not numeric
     * <p>
     * Values that are {@link LuaNumber} will be cast to int and may lose
     * precision. Values that are {@link LuaString} that can be converted to a
     * number will be converted, then cast to int, so may also lose precision.
     *
     * @return value cast to a int and wrapped in {@link LuaInteger} if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkint()
     * @see #checklong()
     * @see #checkdouble()
     * @see #optinteger(LuaInteger)
     * @see LuaType#NUMBER
     */
    public LuaInteger checkinteger() {
        argumentError("integer");
        return null;
    }

    /**
     * Check that the value is numeric, and convert and cast value to long, or
     * throw {@link LuaError} if not numeric
     * <p>
     * Values that are {@link LuaNumber} will be cast to long and may lose
     * precision. Values that are {@link LuaString} that can be converted to a
     * number will be converted, then cast to long, so may also lose precision.
     *
     * @return value cast to a long if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkint()
     * @see #checkinteger()
     * @see #checkdouble()
     * @see #optlong(long)
     * @see LuaType#NUMBER
     */
    public long checklong() {
        argumentError("long");
        return 0;
    }

    /**
     * Check that the value is numeric, and return as a LuaNumber if so, or
     * throw {@link LuaError}
     * <p>
     * Values that are {@link LuaString} that can be converted to a number will
     * be converted and returned.
     *
     * @return value as a {@link LuaNumber} if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkint()
     * @see #checkinteger()
     * @see #checkdouble()
     * @see #checklong()
     * @see #optnumber(LuaNumber)
     * @see LuaType#NUMBER
     */
    public LuaNumber checknumber() {
        argumentError("number");
        return null;
    }

    /**
     * Check that the value is numeric, and return as a LuaNumber if so, or
     * throw {@link LuaError}
     * <p>
     * Values that are {@link LuaString} that can be converted to a number will
     * be converted and returned.
     *
     * @param msg String message to supply if conversion fails
     * @return value as a {@link LuaNumber} if numeric
     * @throws LuaError if not a {@link LuaNumber} or is a {@link LuaString}
     *                  that can't be converted to number
     * @see #checkint()
     * @see #checkinteger()
     * @see #checkdouble()
     * @see #checklong()
     * @see #optnumber(LuaNumber)
     * @see LuaType#NUMBER
     */
    public LuaNumber checknumber(String msg) {
        throw new LuaError(msg);
    }

    /**
     * Convert this value to a Java String.
     * <p>
     * The string representations here will roughly match what is produced by
     * the C lua distribution, however hash codes have no relationship, and
     * there may be differences in number formatting.
     *
     * @return String representation of the value
     * @see #checkstring()
     * @see #optjstring(String)
     * @see #tojstring()
     * @see #isstring
     * @see LuaType#STRING
     */
    public String checkjstring() {
        argumentError("string");
        return null;
    }

    /**
     * Check that this is a lua string, or throw {@link LuaError} if it is not.
     * <p>
     * In lua all numbers are strings, so this will succeed for anything that
     * derives from {@link LuaString} or {@link LuaNumber}. Numbers will be
     * converted to {@link LuaString}.
     *
     * @return {@link LuaString} representation of the value if it is a
     * {@link LuaString} or {@link LuaNumber}
     * @throws LuaError if {@code this} is not a {@link LuaTable}
     * @see #checkjstring()
     * @see #optstring(LuaString)
     * @see #tostring()
     * @see #isstring()
     * @see LuaType#STRING
     */
    public LuaString checkstring() {
        argumentError("string");
        return null;
    }

    /**
     * Check that this is a {@link LuaTable}, or throw {@link LuaError} if it is
     * not
     *
     * @return {@code this} if it is a {@link LuaTable}
     * @throws LuaError if {@code this} is not a {@link LuaTable}
     * @see #istable()
     * @see #opttable(LuaTable)
     * @see LuaType#TABLE
     */
    public LuaTable checktable() {
        argumentError("table");
        return null;
    }

    /**
     * Check that this is a {@link LuaThread}, or throw {@link LuaError} if it
     * is not
     *
     * @return {@code this} if it is a {@link LuaThread}
     * @throws LuaError if {@code this} is not a {@link LuaThread}
     * @see #isthread()
     * @see #optthread(LuaThread)
     * @see LuaType#THREAD
     */
    public LuaThread checkthread() {
        argumentError("thread");
        return null;
    }

    /**
     * Check that this is a {@link LuaUserdata}, or throw {@link LuaError} if it
     * is not
     *
     * @return {@code this} if it is a {@link LuaUserdata}
     * @throws LuaError if {@code this} is not a {@link LuaUserdata}
     * @see #isuserdata()
     * @see #optuserdata(Object)
     * @see #checkuserdata(Class)
     * @see LuaType#USERDATA
     */
    public Object checkuserdata() {
        argumentError("userdata");
        return null;
    }

    /**
     * Check that this is a {@link LuaUserdata}, or throw {@link LuaError} if it
     * is not
     *
     * @return {@code this} if it is a {@link LuaUserdata}
     * @throws LuaError if {@code this} is not a {@link LuaUserdata}
     * @see #isuserdata(Class)
     * @see #optuserdata(Class, Object)
     * @see #checkuserdata()
     * @see LuaType#USERDATA
     */
    public Object checkuserdata(Class c) {
        argumentError("userdata");
        return null;
    }

    /**
     * Check that this is not the value {@link LuaConstant#NIL}, or throw {@link LuaError}
     * if it is
     *
     * @return {@code this} if it is not {@link LuaConstant#NIL}
     * @throws LuaError if {@code this} is {@link LuaConstant#NIL}
     * @see #optvalue(LuaValue)
     */
    public LuaValue checknotnil() {
        return this;
    }

    /**
     * Return true if this is a valid key in a table index operation.
     *
     * @return true if valid as a table key, otherwise false
     * @see #isnil()
     * @see #isinttype()
     */
    public boolean isvalidkey() {
        return true;
    }

    /**
     * Throw a {@link LuaError} with a particular message
     *
     * @param message String providing message details
     * @throws LuaError in all cases
     */
    public static LuaValue error(String message) {
        throw new LuaError(message);
    }

    /**
     * Assert a condition is true, or throw a {@link LuaError} if not Returns no
     * value when b is true, throws {@link #error(String)} with {@code msg} as
     * argument and does not return if b is false.
     *
     * @param b   condition to test
     * @param msg String message to produce on failure
     * @throws LuaError if b is not true
     */
    public static void assert_(boolean b, String msg) {
        if (!b)
            throw new LuaError(msg);
    }

    /**
     * Throw a {@link LuaError} indicating an invalid argument was supplied to a
     * function
     *
     * @param expected String naming the type that was expected
     * @throws LuaError in all cases
     */
    protected LuaValue argumentError(String expected) {
        throw new LuaError("bad argument: " + expected + " expected, got " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} indicating an invalid argument was supplied to a
     * function
     *
     * @param iarg index of the argument that was invalid, first index is 1
     * @param msg  String providing information about the invalid argument
     * @throws LuaError in all cases
     */
    public static LuaValue argumentError(int iarg, String msg) {
        throw new LuaError("bad argument #" + iarg + ": " + msg);
    }

    /**
     * Throw a {@link LuaError} indicating an invalid type was supplied to a
     * function
     *
     * @param expected String naming the type that was expected
     * @throws LuaError in all cases
     */
    protected LuaValue typeError(String expected) {
        throw new LuaError(expected + " expected, got " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} indicating an operation is not implemented
     *
     * @throws LuaError in all cases
     */
    protected LuaValue unimplementedError(String fun) {
        throw new LuaError("'" + fun + "' not implemented for " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} indicating an illegal operation occurred,
     * typically involved in managing weak references
     *
     * @throws LuaError in all cases
     */
    protected LuaValue illegalOperationError(String op, String typename) {
        throw new LuaError("illegal operation '" + op + "' for " + typename);
    }

    /**
     * Throw a {@link LuaError} based on the len operator, typically due to an
     * invalid operand type
     *
     * @throws LuaError in all cases
     */
    protected LuaValue lengthError() {
        throw new LuaError("attempt to get length of " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} based on an arithmetic error such as add, or
     * pow, typically due to an invalid operand type
     *
     * @throws LuaError in all cases
     */
    protected LuaValue arithmeticError() {
        throw new LuaError("attempt to perform arithmetic on " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} based on an arithmetic error such as add, or
     * pow, typically due to an invalid operand type
     *
     * @param fun String description of the function that was attempted
     * @throws LuaError in all cases
     */
    protected LuaValue arithmeticError(String fun) {
        throw new LuaError("attempt to perform arithmetic '" + fun + "' on " + getType().typeName);
    }

    /**
     * Throw a {@link LuaError} based on a comparison error such as greater-than
     * or less-than, typically due to an invalid operand type
     *
     * @param rhs String description of what was on the right-hand-side of the
     *            comparison that resulted in the error.
     * @throws LuaError in all cases
     */
    protected LuaValue comparisonError(String rhs) {
        throw new LuaError("attempt to compare " + getType().typeName + " with " + rhs);
    }

    /**
     * Throw a {@link LuaError} based on a comparison error such as greater-than
     * or less-than, typically due to an invalid operand type
     *
     * @param rhs Right-hand-side of the comparison that resulted in the error.
     * @throws LuaError in all cases
     */
    protected LuaValue comparisonError(LuaValue rhs) {
        throw new LuaError("attempt to compare " + getType().typeName + " with " + rhs.getType().typeName);
    }

    /**
     * Get a value in a table including metatag processing using {@link LuaConstant.MetaTag#INDEX}.
     *
     * @param key the key to look up, must not be {@link LuaConstant#NIL} or null
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found and
     * no metatag
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#INDEX} metatag, or key is {@link LuaConstant#NIL}
     * @see #get(int)
     * @see #get(String)
     * @see #rawget(LuaValue)
     */
    public LuaValue get(LuaValue key) {
        return gettable(this, key);
    }

    /**
     * Get a value in a table including metatag processing using {@link LuaConstant.MetaTag#INDEX}.
     *
     * @param key the key to look up
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#INDEX} metatag
     * @see #get(LuaValue)
     * @see #rawget(int)
     */
    public LuaValue get(int key) {
        return get(LuaInteger.valueOf(key));
    }

    /**
     * Get a value in a table including metatag processing using {@link LuaConstant.MetaTag#INDEX}.
     *
     * @param key the key to look up, must not be null
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#INDEX} metatag
     * @see #get(LuaValue)
     * @see #rawget(String)
     */
    public LuaValue get(String key) {
        return get(valueOf(key));
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use, must not be {@link LuaConstant#NIL} or null
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table, or key is {@link LuaConstant#NIL},
     *                  or there is no {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(LuaValue key, LuaValue value) {
        settable(this, key, value);
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(int key, LuaValue value) {
        set(LuaInteger.valueOf(key), value);
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use
     * @param value the value to use, must not be null
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(int key, String value) {
        set(key, valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use, must not be {@link LuaConstant#NIL} or null
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(String key, LuaValue value) {
        set(valueOf(key), value);
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(String key, double value) {
        set(valueOf(key), valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(String key, int value) {
        set(valueOf(key), valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing using
     * {@link LuaConstant.MetaTag#NEWINDEX}.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use, must not be null
     * @throws LuaError if {@code this} is not a table, or there is no
     *                  {@link LuaConstant.MetaTag#NEWINDEX} metatag
     */
    public void set(String key, String value) {
        set(valueOf(key), valueOf(value));
    }

    public void set(String key, JavaFunction javaFunction) {
        set(key, new BoundLuaFunction(key, javaFunction));
    }

    /**
     * Get a value in a table without metatag processing.
     *
     * @param key the key to look up, must not be {@link LuaConstant#NIL} or null
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found
     * @throws LuaError if {@code this} is not a table, or key is {@link LuaConstant#NIL}
     */
    public LuaValue rawget(LuaValue key) {
        return unimplementedError("rawget");
    }

    /**
     * Get a value in a table without metatag processing.
     *
     * @param key the key to look up
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found
     * @throws LuaError if {@code this} is not a table
     */
    public LuaValue rawget(int key) {
        return rawget(valueOf(key));
    }

    /**
     * Get a value in a table without metatag processing.
     *
     * @param key the key to look up, must not be null
     * @return {@link LuaValue} for that key, or {@link LuaConstant#NIL} if not found
     * @throws LuaError if {@code this} is not a table
     */
    public LuaValue rawget(String key) {
        return rawget(valueOf(key));
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use, must not be {@link LuaConstant#NIL} or null
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table, or key is {@link LuaConstant#NIL}
     */
    public void rawset(LuaValue key, LuaValue value) {
        unimplementedError("rawset");
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(int key, LuaValue value) {
        rawset(valueOf(key), value);
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(int key, String value) {
        rawset(key, valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use, can be {@link LuaConstant#NIL}, must not be null
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(String key, LuaValue value) {
        rawset(valueOf(key), value);
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(String key, double value) {
        rawset(valueOf(key), valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(String key, int value) {
        rawset(valueOf(key), valueOf(value));
    }

    /**
     * Set a value in a table without metatag processing.
     *
     * @param key   the key to use, must not be null
     * @param value the value to use, must not be null
     * @throws LuaError if {@code this} is not a table
     */
    public void rawset(String key, String value) {
        rawset(valueOf(key), valueOf(value));
    }

    /**
     * Set list values in a table without invoking metatag processing
     * <p>
     * Primarily used internally in response to a SETLIST bytecode.
     *
     * @param key0   the first key to set in the table
     * @param values the list of values to set
     * @throws LuaError if this is not a table.
     */
    public void rawsetlist(int key0, Varargs values) {
        for (int i = 0, n = values.narg(); i < n; i++)
            rawset(key0 + i, values.arg(i + 1));
    }

    /**
     * Preallocate the array part of a table to be a certain size,
     * <p>
     * Primarily used internally in response to a SETLIST bytecode.
     *
     * @param i the number of array slots to preallocate in the table.
     * @throws LuaError if this is not a table.
     */
    public void presize(int i) {
        typeError("table");
    }

    /**
     * Find the next key,value pair if {@code this} is a table, return
     * {@link LuaConstant#NIL} if there are no more, or throw a {@link LuaError} if not a
     * table.
     * <p>
     * To iterate over all key-value pairs in a table you can use
     *
     * <pre>
     *  {@code
     * LuaValue k = LuaValue.NIL;
     * while ( true ) {
     *    Varargs n = table.next(k);
     *    if ( (k = n.arg1()).isnil() )
     *       break;
     *    LuaValue v = n.arg(2)
     *    process( k, v )
     * }}
     * </pre>
     *
     * @param index {@link LuaInteger} value identifying a key to start from, or
     *              {@link LuaConstant#NIL} to start at the beginning
     * @return {@link Varargs} containing {key,value} for the next entry, or
     * {@link LuaConstant#NIL} if there are no more.
     * @throws LuaError if {@code this} is not a table, or the supplied key is
     *                  invalid.
     * @see LuaTable
     * @see #inext(LuaValue)
     * @see #valueOf(int)
     * @see Varargs#arg1()
     * @see Varargs#arg(int)
     * @see #isnil()
     */
    public Varargs next(LuaValue index) {
        return typeError("table");
    }

    /**
     * Find the next integer-key,value pair if {@code this} is a table, return
     * {@link LuaConstant#NIL} if there are no more, or throw a {@link LuaError} if not a
     * table.
     * <p>
     * To iterate over integer keys in a table you can use
     *
     * <pre>
     *  {@code
     *   LuaValue k = LuaValue.NIL;
     *   while ( true ) {
     *      Varargs n = table.inext(k);
     *      if ( (k = n.arg1()).isnil() )
     *         break;
     *      LuaValue v = n.arg(2)
     *      process( k, v )
     *   }
     * }
     * </pre>
     *
     * @param index {@link LuaInteger} value identifying a key to start from, or
     *              {@link LuaConstant#NIL} to start at the beginning
     * @return {@link Varargs} containing {@code (key,value)} for the next
     * entry, or {@link LuaConstant#NONE} if there are no more.
     * @throws LuaError if {@code this} is not a table, or the supplied key is
     *                  invalid.
     * @see LuaTable
     * @see #next(LuaValue)
     * @see #valueOf(int)
     * @see Varargs#arg1()
     * @see Varargs#arg(int)
     * @see #isnil()
     */
    public Varargs inext(LuaValue index) {
        return typeError("table");
    }

    /**
     * Load a library instance by calling it with and empty string as the
     * modname, and this Globals as the environment. This is normally used to
     * iniitalize the library instance and which may install itself into these
     * globals.
     *
     * @param library The callable {@link LuaValue} to load into {@code this}
     * @return {@link LuaValue} returned by the initialization call.
     */
    public LuaValue load(LuaValue library) {
        return library.call(LuaConstant.EMPTY_STRING, this);
    }

    // varargs references
    @Override
    public LuaValue arg(int index) {
        return index == 1 ? this : LuaConstant.NIL;
    }

    @Override
    public int narg() {
        return 1;
    }

    @Override
    public LuaValue arg1() {
        return this;
    }

    /**
     * Get the metatable for this {@link LuaValue}
     * <p>
     * For {@link LuaTable} and {@link LuaUserdata} instances, the metatable
     * returned is this instance metatable. For all other types, the class
     * metatable value will be returned.
     *
     * @return metatable, or null if it there is none
     * @see LuaBoolean#s_metatable
     * @see LuaNumber#s_metatable
     * @see LuaNil#s_metatable
     * @see LuaFunction#s_metatable
     * @see LuaThread#s_metatable
     */
    public LuaValue getmetatable() {
        return null;
    }

    /**
     * Set the metatable for this {@link LuaValue}
     * <p>
     * For {@link LuaTable} and {@link LuaUserdata} instances, the metatable is
     * per instance. For all other types, there is one metatable per type that
     * can be set directly from java
     *
     * @param metatable {@link LuaValue} instance to serve as the metatable, or
     *                  null to reset it.
     * @return {@code this} to allow chaining of Java function calls
     * @see LuaBoolean#s_metatable
     * @see LuaNumber#s_metatable
     * @see LuaNil#s_metatable
     * @see LuaFunction#s_metatable
     * @see LuaThread#s_metatable
     */
    public LuaValue setmetatable(LuaValue metatable) {
        return argumentError("table");
    }

    /**
     * Call {@code this} with 0 arguments, including metatag processing, and
     * return only the first return value.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return only its
     * first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a method call, use {@link #method(LuaValue)}
     * instead.
     *
     * @return First return value {@code (this())}, or {@link LuaConstant#NIL} if there
     * were none.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call(LuaValue)
     * @see #call(LuaValue, LuaValue)
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke()
     * @see #method(String)
     * @see #method(LuaValue)
     */
    public LuaValue call() {
        return callmt().call(this);
    }

    /**
     * Call {@code this} with 1 argument, including metatag processing, and
     * return only the first return value.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return only its
     * first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a method call, use {@link #method(LuaValue)}
     * instead.
     *
     * @param arg First argument to supply to the called function
     * @return First return value {@code (this(arg))}, or {@link LuaConstant#NIL} if there
     * were none.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #call(LuaValue, LuaValue)
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke(Varargs)
     * @see #method(String, LuaValue)
     * @see #method(LuaValue, LuaValue)
     */
    public LuaValue call(LuaValue arg) {
        return callmt().call(this, arg);
    }

    /**
     * Convenience function which calls a luavalue with a single, string
     * argument.
     *
     * @param arg String argument to the function. This will be converted to a
     *            LuaString.
     * @return return value of the invocation.
     * @see #call(LuaValue)
     */
    public LuaValue call(String arg) {
        return call(valueOf(arg));
    }

    /**
     * Call {@code this} with 2 arguments, including metatag processing, and
     * return only the first return value.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return only its
     * first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a method call, use {@link #method(LuaValue)}
     * instead.
     *
     * @param arg1 First argument to supply to the called function
     * @param arg2 Second argument to supply to the called function
     * @return First return value {@code (this(arg1,arg2))}, or {@link LuaConstant#NIL} if
     * there were none.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #call(LuaValue)
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke(LuaValue, Varargs)
     * @see #method(String, LuaValue, LuaValue)
     * @see #method(LuaValue, LuaValue, LuaValue)
     */
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return callmt().call(this, arg1, arg2);
    }

    /**
     * Call {@code this} with 3 arguments, including metatag processing, and
     * return only the first return value.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return only its
     * first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a method call, use {@link #method(LuaValue)}
     * instead.
     *
     * @param arg1 First argument to supply to the called function
     * @param arg2 Second argument to supply to the called function
     * @param arg3 Second argument to supply to the called function
     * @return First return value {@code (this(arg1,arg2,arg3))}, or
     * {@link LuaConstant#NIL} if there were none.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #call(LuaValue)
     * @see #call(LuaValue, LuaValue)
     * @see #invoke(LuaValue, LuaValue, Varargs)
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     */
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return callmt().invoke(new LuaValue[]{this, arg1, arg2, arg3}).arg1();
    }

    /**
     * Call named method on {@code this} with 0 arguments, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument. and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use {@link #call()} instead.
     *
     * @param name Name of the method to look up for invocation
     * @return All values returned from {@code this:name()} as a {@link Varargs}
     * instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke()
     * @see #method(LuaValue)
     * @see #method(String, LuaValue)
     * @see #method(String, LuaValue, LuaValue)
     */
    public LuaValue method(String name) {
        return this.get(name).call(this);
    }

    /**
     * Call named method on {@code this} with 0 arguments, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use {@link #call()} instead.
     *
     * @param name Name of the method to look up for invocation
     * @return All values returned from {@code this:name()} as a {@link Varargs}
     * instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke()
     * @see #method(String)
     * @see #method(LuaValue, LuaValue)
     * @see #method(LuaValue, LuaValue, LuaValue)
     */
    public LuaValue method(LuaValue name) {
        return this.get(name).call(this);
    }

    /**
     * Call named method on {@code this} with 1 argument, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use {@link #call(LuaValue)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param arg  Argument to supply to the method
     * @return All values returned from {@code this:name(arg)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call(LuaValue)
     * @see #invoke(Varargs)
     * @see #method(String)
     * @see #method(LuaValue)
     * @see #method(String, LuaValue, LuaValue)
     */
    public LuaValue method(String name, LuaValue arg) {
        return this.get(name).call(this, arg);
    }

    /**
     * Call named method on {@code this} with 1 argument, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use {@link #call(LuaValue)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param arg  Argument to supply to the method
     * @return All values returned from {@code this:name(arg)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call(LuaValue)
     * @see #invoke(Varargs)
     * @see #method(String, LuaValue)
     * @see #method(LuaValue)
     * @see #method(LuaValue, LuaValue, LuaValue)
     */
    public LuaValue method(LuaValue name, LuaValue arg) {
        return this.get(name).call(this, arg);
    }

    /**
     * Call named method on {@code this} with 2 arguments, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use
     * {@link #call(LuaValue, LuaValue)} instead.
     *
     * @param name Name of the method to look up for invocation
     * @param arg1 First argument to supply to the method
     * @param arg2 Second argument to supply to the method
     * @return All values returned from {@code this:name(arg1,arg2)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call(LuaValue, LuaValue)
     * @see #invoke(LuaValue, Varargs)
     * @see #method(String, LuaValue)
     * @see #method(LuaValue, LuaValue, LuaValue)
     */
    public LuaValue method(String name, LuaValue arg1, LuaValue arg2) {
        return this.get(name).call(this, arg1, arg2);
    }

    /**
     * Call named method on {@code this} with 2 arguments, including metatag
     * processing, and return only the first return value.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return only
     * its first return value, dropping any others. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * If the return value is a {@link Varargs}, only the 1st value will be
     * returned. To get multiple values, use {@link #invoke()} instead.
     * <p>
     * To call {@code this} as a plain call, use
     * {@link #call(LuaValue, LuaValue)} instead.
     *
     * @param name Name of the method to look up for invocation
     * @param arg1 First argument to supply to the method
     * @param arg2 Second argument to supply to the method
     * @return All values returned from {@code this:name(arg1,arg2)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call(LuaValue, LuaValue)
     * @see #invoke(LuaValue, Varargs)
     * @see #method(LuaValue, LuaValue)
     * @see #method(String, LuaValue, LuaValue)
     */
    public LuaValue method(LuaValue name, LuaValue arg1, LuaValue arg2) {
        return this.get(name).call(this, arg1, arg2);
    }

    /**
     * Call {@code this} with 0 arguments, including metatag processing, and
     * retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue)} instead.
     *
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke(Varargs)
     * @see #invokemethod(String)
     * @see #invokemethod(LuaValue)
     */
    public Varargs invoke() {
        return invoke(LuaConstant.NONE);
    }

    /**
     * Call {@code this} with variable arguments, including metatag processing,
     * and retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue)} instead.
     *
     * @param args Varargs containing the arguments to supply to the called
     *             function
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #varargsOf(LuaValue[])
     * @see #call(LuaValue)
     * @see #invoke()
     * @see #invoke(LuaValue, Varargs)
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invoke(Varargs args) {
        return callmt().invoke(this, args);
    }

    /**
     * Call {@code this} with variable arguments, including metatag processing,
     * and retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue, Varargs)} instead.
     *
     * @param arg     The first argument to supply to the called function
     * @param varargs Varargs containing the remaining arguments to supply to
     *                the called function
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #varargsOf(LuaValue[])
     * @see #call(LuaValue, LuaValue)
     * @see #invoke(LuaValue, Varargs)
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invoke(LuaValue arg, Varargs varargs) {
        return invoke(varargsOf(arg, varargs));
    }

    /**
     * Call {@code this} with variable arguments, including metatag processing,
     * and retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue, Varargs)} instead.
     *
     * @param arg1    The first argument to supply to the called function
     * @param arg2    The second argument to supply to the called function
     * @param varargs Varargs containing the remaining arguments to supply to
     *                the called function
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #varargsOf(LuaValue[])
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke(LuaValue, LuaValue, Varargs)
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invoke(LuaValue arg1, LuaValue arg2, Varargs varargs) {
        return invoke(varargsOf(arg1, arg2, varargs));
    }

    /**
     * Call {@code this} with variable arguments, including metatag processing,
     * and retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue, Varargs)} instead.
     *
     * @param args Array of arguments to supply to the called function
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #varargsOf(LuaValue[])
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke(LuaValue, LuaValue, Varargs)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(LuaValue, LuaValue[])
     */
    public Varargs invoke(LuaValue[] args) {
        return invoke(varargsOf(args));
    }

    /**
     * Call {@code this} with variable arguments, including metatag processing,
     * and retain all return values in a {@link Varargs}.
     * <p>
     * If {@code this} is a {@link LuaFunction}, call it, and return all values.
     * Otherwise, look for the {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a method call, use
     * {@link #invokemethod(LuaValue, Varargs)} instead.
     *
     * @param args    Array of arguments to supply to the called function
     * @param varargs Varargs containing additional arguments to supply to the
     *                called function
     * @return All return values as a {@link Varargs} instance.
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #varargsOf(LuaValue[])
     * @see #call(LuaValue, LuaValue, LuaValue)
     * @see #invoke(LuaValue, LuaValue, Varargs)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(LuaValue, LuaValue[])
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invoke(LuaValue[] args, Varargs varargs) {
        return invoke(varargsOf(args, varargs));
    }

    /**
     * Call named method on {@code this} with 0 arguments, including metatag
     * processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke()} instead.
     *
     * @param name Name of the method to look up for invocation
     * @return All values returned from {@code this:name()} as a {@link Varargs}
     * instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke()
     * @see #method(String)
     * @see #invokemethod(LuaValue)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, LuaValue[])
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invokemethod(String name) {
        return get(name).invoke(this);
    }

    /**
     * Call named method on {@code this} with 0 arguments, including metatag
     * processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke()} instead.
     *
     * @param name Name of the method to look up for invocation
     * @return All values returned from {@code this:name()} as a {@link Varargs}
     * instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke()
     * @see #method(LuaValue)
     * @see #invokemethod(String)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, LuaValue[])
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invokemethod(LuaValue name) {
        return get(name).invoke(this);
    }

    /**
     * Call named method on {@code this} with 1 argument, including metatag
     * processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke(Varargs)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param args {@link Varargs} containing arguments to supply to the called
     *             function after {@code this}
     * @return All values returned from {@code this:name(args)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke(Varargs)
     * @see #method(String)
     * @see #invokemethod(String)
     * @see #invokemethod(LuaValue)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(LuaValue, LuaValue[])
     * @see #invokemethod(LuaValue, Varargs)
     */
    public Varargs invokemethod(String name, Varargs args) {
        return get(name).invoke(varargsOf(this, args));
    }

    /**
     * Call named method on {@code this} with variable arguments, including
     * metatag processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke(Varargs)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param args {@link Varargs} containing arguments to supply to the called
     *             function after {@code this}
     * @return All values returned from {@code this:name(args)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke(Varargs)
     * @see #method(String)
     * @see #invokemethod(String)
     * @see #invokemethod(LuaValue)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, LuaValue[])
     */
    public Varargs invokemethod(LuaValue name, Varargs args) {
        return get(name).invoke(varargsOf(this, args));
    }

    /**
     * Call named method on {@code this} with 1 argument, including metatag
     * processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke(Varargs)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param args Array of {@link LuaValue} containing arguments to supply to
     *             the called function after {@code this}
     * @return All values returned from {@code this:name(args)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke(Varargs)
     * @see #method(String)
     * @see #invokemethod(String)
     * @see #invokemethod(LuaValue)
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, LuaValue[])
     * @see #invokemethod(LuaValue, Varargs)
     * @see LuaValue#varargsOf(LuaValue[])
     */
    public Varargs invokemethod(String name, LuaValue[] args) {
        return get(name).invoke(varargsOf(this, varargsOf(args)));
    }

    /**
     * Call named method on {@code this} with variable arguments, including
     * metatag processing, and retain all return values in a {@link Varargs}.
     * <p>
     * Look up {@code this[name]} and if it is a {@link LuaFunction}, call it
     * inserting {@code this} as an additional first argument, and return all
     * return values as a {@link Varargs} instance. Otherwise, look for the
     * {@link LuaConstant.MetaTag#CALL} metatag and call that.
     * <p>
     * To get a particular return value, us {@link Varargs#arg(int)}
     * <p>
     * To call {@code this} as a plain call, use {@link #invoke(Varargs)}
     * instead.
     *
     * @param name Name of the method to look up for invocation
     * @param args Array of {@link LuaValue} containing arguments to supply to
     *             the called function after {@code this}
     * @return All values returned from {@code this:name(args)} as a
     * {@link Varargs} instance
     * @throws LuaError if not a function and {@link LuaConstant.MetaTag#CALL} is not defined, or
     *                  the invoked function throws a {@link LuaError} or the
     *                  invoked closure throw a lua {@code error}
     * @see #call()
     * @see #invoke(Varargs)
     * @see #method(String)
     * @see #invokemethod(String)
     * @see #invokemethod(LuaValue)
     * @see #invokemethod(String, LuaValue[])
     * @see #invokemethod(String, Varargs)
     * @see #invokemethod(LuaValue, Varargs)
     * @see LuaValue#varargsOf(LuaValue[])
     */
    public Varargs invokemethod(LuaValue name, LuaValue[] args) {
        return get(name).invoke(varargsOf(this, varargsOf(args)));
    }

    /**
     * Get the metatag value for the {@link LuaConstant.MetaTag#CALL} metatag, if it exists.
     *
     * @return {@link LuaValue} value if metatag is defined
     * @throws LuaError if {@link LuaConstant.MetaTag#CALL} metatag is not defined.
     */
    protected LuaValue callmt() {
        return checkmetatag(LuaConstant.MetaTag.CALL, "attempt to call ");
    }

    /**
     * Unary not: return inverse boolean value {@code (~this)} as defined by lua
     * not operator
     *
     * @return {@link LuaConstant#TRUE} if {@link LuaConstant#NIL} or {@link LuaConstant#FALSE}, otherwise
     * {@link LuaConstant#FALSE}
     */
    public LuaValue not() {
        return LuaConstant.FALSE;
    }

    /**
     * Unary minus: return negative value {@code (-this)} as defined by lua
     * unary minus operator
     *
     * @return boolean inverse as {@link LuaBoolean} if boolean or nil, numeric
     * inverse as {@link LuaNumber} if numeric, or metatag processing
     * result if {@link LuaConstant.MetaTag#UNM} metatag is defined
     * @throws LuaError if {@code this} is not a table or string, and has no
     *                  {@link LuaConstant.MetaTag#UNM} metatag
     */
    public LuaValue neg() {
        return checkmetatag(LuaConstant.MetaTag.UNM, "attempt to perform arithmetic on ").call(this);
    }

    /**
     * Length operator: return lua length of object {@code (#this)} including
     * metatag processing as java int
     *
     * @return length as defined by the lua # operator or metatag processing
     * result
     * @throws LuaError if {@code this} is not a table or string, and has no
     *                  {@link LuaConstant.MetaTag#LEN} metatag
     */
    public LuaValue len() {
        return checkmetatag(LuaConstant.MetaTag.LEN, "attempt to get length of ").call(this);
    }

    /**
     * Length operator: return lua length of object {@code (#this)} including
     * metatag processing as java int
     *
     * @return length as defined by the lua # operator or metatag processing
     * result converted to java int using {@link #toint()}
     * @throws LuaError if {@code this} is not a table or string, and has no
     *                  {@link LuaConstant.MetaTag#LEN} metatag
     */
    public int length() {
        return len().toint();
    }

    /**
     * Get raw length of table or string without metatag processing.
     *
     * @return the length of the table or string.
     * @throws LuaError if {@code this} is not a table or string.
     */
    public int rawlen() {
        typeError("table or string");
        return 0;
    }

    // object equality, used for key comparison
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Equals: Perform equality comparison with another value including metatag
     * processing using {@link LuaConstant.MetaTag#EQ}.
     *
     * @param val The value to compare with.
     * @return {@link LuaConstant#TRUE} if values are comparable and {@code (this == rhs)},
     * {@link LuaConstant#FALSE} if comparable but not equal, {@link LuaValue} if
     * metatag processing occurs.
     * @see #eq_b(LuaValue)
     * @see #raweq(LuaValue)
     * @see #neq(LuaValue)
     * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
     * @see LuaConstant.MetaTag#EQ
     */
    public LuaValue eq(LuaValue val) {
        return eq_b(val) ? LuaConstant.TRUE : LuaConstant.FALSE;
    }

    /**
     * Equals: Perform equality comparison with another value including metatag
     * processing using {@link LuaConstant.MetaTag#EQ}, and return java boolean
     *
     * @param val The value to compare with.
     * @return true if values are comparable and {@code (this == rhs)}, false if
     * comparable but not equal, result converted to java boolean if
     * metatag processing occurs.
     * @see #eq(LuaValue)
     * @see #raweq(LuaValue)
     * @see #neq_b(LuaValue)
     * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
     * @see LuaConstant.MetaTag#EQ
     */
    public boolean eq_b(LuaValue val) {
        return this == val;
    }

    /**
     * Notquals: Perform inequality comparison with another value including
     * metatag processing using {@link LuaConstant.MetaTag#EQ}.
     *
     * @param val The value to compare with.
     * @return {@link LuaConstant#TRUE} if values are comparable and {@code (this != rhs)},
     * {@link LuaConstant#FALSE} if comparable but equal, inverse of
     * {@link LuaValue} converted to {@link LuaBoolean} if metatag
     * processing occurs.
     * @see #eq(LuaValue)
     * @see #raweq(LuaValue)
     * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
     * @see LuaConstant.MetaTag#EQ
     */
    public LuaValue neq(LuaValue val) {
        return eq_b(val) ? LuaConstant.FALSE : LuaConstant.TRUE;
    }

    /**
     * Notquals: Perform inequality comparison with another value including
     * metatag processing using {@link LuaConstant.MetaTag#EQ}.
     *
     * @param val The value to compare with.
     * @return true if values are comparable and {@code (this != rhs)}, false if
     * comparable but equal, inverse of result converted to boolean if
     * metatag processing occurs.
     * @see #eq_b(LuaValue)
     * @see #raweq(LuaValue)
     * @see #eqmtcall(LuaValue, LuaValue, LuaValue, LuaValue)
     * @see LuaConstant.MetaTag#EQ
     */
    public boolean neq_b(LuaValue val) {
        return !eq_b(val);
    }

    /**
     * Equals: Perform direct equality comparison with another value without
     * metatag processing.
     *
     * @param val The value to compare with.
     * @return true if {@code (this == rhs)}, false otherwise
     * @see #eq(LuaValue)
     * @see #raweq(LuaUserdata)
     * @see #raweq(LuaString)
     * @see #raweq(double)
     * @see #raweq(int)
     * @see LuaConstant.MetaTag#EQ
     */
    public boolean raweq(LuaValue val) {
        return this == val;
    }

    /**
     * Equals: Perform direct equality comparison with a {@link LuaUserdata}
     * value without metatag processing.
     *
     * @param val The {@link LuaUserdata} to compare with.
     * @return true if {@code this} is userdata and their metatables are the
     * same using == and their instances are equal using
     * {@link #equals(Object)}, otherwise false
     * @see #eq(LuaValue)
     * @see #raweq(LuaValue)
     */
    public boolean raweq(LuaUserdata val) {
        return false;
    }

    /**
     * Equals: Perform direct equality comparison with a {@link LuaString} value
     * without metatag processing.
     *
     * @param val The {@link LuaString} to compare with.
     * @return true if {@code this} is a {@link LuaString} and their byte
     * sequences match, otherwise false
     */
    public boolean raweq(LuaString val) {
        return false;
    }

    /**
     * Equals: Perform direct equality comparison with a double value without
     * metatag processing.
     *
     * @param val The double value to compare with.
     * @return true if {@code this} is a {@link LuaNumber} whose value equals
     * val, otherwise false
     */
    public boolean raweq(double val) {
        return false;
    }

    /**
     * Equals: Perform direct equality comparison with a int value without
     * metatag processing.
     *
     * @param val The double value to compare with.
     * @return true if {@code this} is a {@link LuaNumber} whose value equals
     * val, otherwise false
     */
    public boolean raweq(int val) {
        return false;
    }

    /**
     * Perform equality testing metatag processing
     *
     * @param lhs   left-hand-side of equality expression
     * @param lhsmt metatag value for left-hand-side
     * @param rhs   right-hand-side of equality expression
     * @param rhsmt metatag value for right-hand-side
     * @return true if metatag processing result is not {@link LuaConstant#NIL} or
     * {@link LuaConstant#FALSE}
     * @throws LuaError if metatag was not defined for either operand
     * @see #equals(Object)
     * @see #eq(LuaValue)
     * @see #raweq(LuaValue)
     * @see LuaConstant.MetaTag#EQ
     */
    public static final boolean eqmtcall(LuaValue lhs, LuaValue lhsmt, LuaValue rhs, LuaValue rhsmt) {
        LuaValue h = lhsmt.rawget(LuaConstant.MetaTag.EQ);
        return h.isnil() || h != rhsmt.rawget(LuaConstant.MetaTag.EQ) ? false : h.call(lhs, rhs).toboolean();
    }

    /**
     * Add: Perform numeric add operation with another value including metatag
     * processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the add with
     * @return value of {@code (this + rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#ADD} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue add(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.ADD, rhs);
    }

    /**
     * Add: Perform numeric add operation with another value of double type with
     * metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the add with
     * @return value of {@code (this + rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #add(LuaValue)
     */
    public LuaValue add(double rhs) {
        return arithmtwith(LuaConstant.MetaTag.ADD, rhs);
    }

    /**
     * Add: Perform numeric add operation with another value of int type with
     * metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the add with
     * @return value of {@code (this + rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #add(LuaValue)
     */
    public LuaValue add(int rhs) {
        return add((double) rhs);
    }

    /**
     * Subtract: Perform numeric subtract operation with another value of
     * unknown type, including metatag processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the subtract with
     * @return value of {@code (this - rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#SUB} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue sub(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.SUB, rhs);
    }

    /**
     * Subtract: Perform numeric subtract operation with another value of double
     * type with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the subtract with
     * @return value of {@code (this - rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #sub(LuaValue)
     */
    public LuaValue sub(double rhs) {
        return arithmeticError("sub");
    }

    /**
     * Subtract: Perform numeric subtract operation with another value of int
     * type with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the subtract with
     * @return value of {@code (this - rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #sub(LuaValue)
     */
    public LuaValue sub(int rhs) {
        return arithmeticError("sub");
    }

    /**
     * Reverse-subtract: Perform numeric subtract operation from an int value
     * with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param lhs The left-hand-side value from which to perform the subtraction
     * @return value of {@code (lhs - this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #sub(LuaValue)
     * @see #sub(double)
     * @see #sub(int)
     */
    public LuaValue subFrom(double lhs) {
        return arithmtwith(LuaConstant.MetaTag.SUB, lhs);
    }

    /**
     * Reverse-subtract: Perform numeric subtract operation from a double value
     * without metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     * <p>
     * For metatag processing {@link #sub(LuaValue)} must be used
     *
     * @param lhs The left-hand-side value from which to perform the subtraction
     * @return value of {@code (lhs - this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #sub(LuaValue)
     * @see #sub(double)
     * @see #sub(int)
     */
    public LuaValue subFrom(int lhs) {
        return subFrom((double) lhs);
    }

    /**
     * Multiply: Perform numeric multiply operation with another value of
     * unknown type, including metatag processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the multiply with
     * @return value of {@code (this * rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#MUL} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue mul(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.MUL, rhs);
    }

    /**
     * Multiply: Perform numeric multiply operation with another value of double
     * type with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the multiply with
     * @return value of {@code (this * rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #mul(LuaValue)
     */
    public LuaValue mul(double rhs) {
        return arithmtwith(LuaConstant.MetaTag.MUL, rhs);
    }

    /**
     * Multiply: Perform numeric multiply operation with another value of int
     * type with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the multiply with
     * @return value of {@code (this * rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #mul(LuaValue)
     */
    public LuaValue mul(int rhs) {
        return mul((double) rhs);
    }

    /**
     * Raise to power: Raise this value to a power including metatag processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The power to raise this value to
     * @return value of {@code (this ^ rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#POW} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue pow(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.POW, rhs);
    }

    /**
     * Raise to power: Raise this value to a power of double type with metatag
     * processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The power to raise this value to
     * @return value of {@code (this ^ rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #pow(LuaValue)
     */
    public LuaValue pow(double rhs) {
        return arithmeticError("pow");
    }

    /**
     * Raise to power: Raise this value to a power of int type with metatag
     * processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The power to raise this value to
     * @return value of {@code (this ^ rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #pow(LuaValue)
     */
    public LuaValue pow(int rhs) {
        return arithmeticError("pow");
    }

    /**
     * Reverse-raise to power: Raise another value of double type to this power
     * with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param lhs The left-hand-side value which will be raised to this power
     * @return value of {@code (lhs ^ this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #pow(LuaValue)
     * @see #pow(double)
     * @see #pow(int)
     */
    public LuaValue powWith(double lhs) {
        return arithmtwith(LuaConstant.MetaTag.POW, lhs);
    }

    /**
     * Reverse-raise to power: Raise another value of double type to this power
     * with metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param lhs The left-hand-side value which will be raised to this power
     * @return value of {@code (lhs ^ this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #pow(LuaValue)
     * @see #pow(double)
     * @see #pow(int)
     */
    public LuaValue powWith(int lhs) {
        return powWith((double) lhs);
    }

    /**
     * Divide: Perform numeric divide operation by another value of unknown
     * type, including metatag processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the divulo with
     * @return value of {@code (this / rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#DIV} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue div(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.DIV, rhs);
    }

    /**
     * Divide: Perform numeric divide operation by another value of double type
     * without metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     * <p>
     * For metatag processing {@link #div(LuaValue)} must be used
     *
     * @param rhs The right-hand-side value to perform the divulo with
     * @return value of {@code (this / rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #div(LuaValue)
     */
    public LuaValue div(double rhs) {
        return arithmeticError("div");
    }

    /**
     * Divide: Perform numeric divide operation by another value of int type
     * without metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     * <p>
     * For metatag processing {@link #div(LuaValue)} must be used
     *
     * @param rhs The right-hand-side value to perform the divulo with
     * @return value of {@code (this / rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #div(LuaValue)
     */
    public LuaValue div(int rhs) {
        return arithmeticError("div");
    }

    /**
     * Reverse-divide: Perform numeric divide operation into another value with
     * metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param lhs The left-hand-side value which will be divided by this
     * @return value of {@code (lhs / this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #div(LuaValue)
     * @see #div(double)
     * @see #div(int)
     */
    public LuaValue divInto(double lhs) {
        return arithmtwith(LuaConstant.MetaTag.DIV, lhs);
    }

    /**
     * Modulo: Perform numeric modulo operation with another value of unknown
     * type, including metatag processing.
     * <p>
     * Each operand must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param rhs The right-hand-side value to perform the modulo with
     * @return value of {@code (this % rhs)} if both are numeric, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either operand is not a number or string convertible
     *                  to number, and neither has the {@link LuaConstant.MetaTag#MOD} metatag
     *                  defined
     * @see #arithmt(LuaValue, LuaValue)
     */
    public LuaValue mod(LuaValue rhs) {
        return arithmt(LuaConstant.MetaTag.MOD, rhs);
    }

    /**
     * Modulo: Perform numeric modulo operation with another value of double
     * type without metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     * <p>
     * For metatag processing {@link #mod(LuaValue)} must be used
     *
     * @param rhs The right-hand-side value to perform the modulo with
     * @return value of {@code (this % rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #mod(LuaValue)
     */
    public LuaValue mod(double rhs) {
        return arithmeticError("mod");
    }

    /**
     * Modulo: Perform numeric modulo operation with another value of int type
     * without metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     * <p>
     * For metatag processing {@link #mod(LuaValue)} must be used
     *
     * @param rhs The right-hand-side value to perform the modulo with
     * @return value of {@code (this % rhs)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #mod(LuaValue)
     */
    public LuaValue mod(int rhs) {
        return arithmeticError("mod");
    }

    /**
     * Reverse-modulo: Perform numeric modulo operation from another value with
     * metatag processing
     * <p>
     * {@code this} must derive from {@link LuaNumber} or derive from
     * {@link LuaString} and be convertible to a number
     *
     * @param lhs The left-hand-side value which will be modulo'ed by this
     * @return value of {@code (lhs % this)} if this is numeric
     * @throws LuaError if {@code this} is not a number or string convertible to
     *                  number
     * @see #mod(LuaValue)
     * @see #mod(double)
     * @see #mod(int)
     */
    public LuaValue modFrom(double lhs) {
        return arithmtwith(LuaConstant.MetaTag.MOD, lhs);
    }

    /**
     * Perform metatag processing for arithmetic operations.
     * <p>
     * Finds the supplied metatag value for {@code this} or {@code op2} and
     * invokes it, or throws {@link LuaError} if neither is defined.
     *
     * @param tag The metatag to look up
     * @param op2 The other operand value to perform the operation with
     * @return {@link LuaValue} resulting from metatag processing
     * @throws LuaError if metatag was not defined for either operand
     * @see #add(LuaValue)
     * @see #sub(LuaValue)
     * @see #mul(LuaValue)
     * @see #pow(LuaValue)
     * @see #div(LuaValue)
     * @see #mod(LuaValue)
     * @see LuaConstant.MetaTag#ADD
     * @see LuaConstant.MetaTag#SUB
     * @see LuaConstant.MetaTag#MUL
     * @see LuaConstant.MetaTag#POW
     * @see LuaConstant.MetaTag#DIV
     * @see LuaConstant.MetaTag#MOD
     */
    protected LuaValue arithmt(LuaValue tag, LuaValue op2) {
        LuaValue h = this.metatag(tag);
        if (h.isnil()) {
            h = op2.metatag(tag);
            if (h.isnil())
                error("attempt to perform arithmetic " + tag + " on " + getType().typeName + " and " + op2.getType().typeName);
        }
        return h.call(this, op2);
    }

    /**
     * Perform metatag processing for arithmetic operations when the
     * left-hand-side is a number.
     * <p>
     * Finds the supplied metatag value for {@code this} and invokes it, or
     * throws {@link LuaError} if neither is defined.
     *
     * @param tag The metatag to look up
     * @param op1 The value of the left-hand-side to perform the operation with
     * @return {@link LuaValue} resulting from metatag processing
     * @throws LuaError if metatag was not defined for either operand
     * @see #add(LuaValue)
     * @see #sub(LuaValue)
     * @see #mul(LuaValue)
     * @see #pow(LuaValue)
     * @see #div(LuaValue)
     * @see #mod(LuaValue)
     * @see LuaConstant.MetaTag#ADD
     * @see LuaConstant.MetaTag#SUB
     * @see LuaConstant.MetaTag#MUL
     * @see LuaConstant.MetaTag#POW
     * @see LuaConstant.MetaTag#DIV
     * @see LuaConstant.MetaTag#MOD
     */
    protected LuaValue arithmtwith(LuaValue tag, double op1) {
        LuaValue h = metatag(tag);
        if (h.isnil())
            error("attempt to perform arithmetic " + tag + " on number and " + getType().typeName);
        return h.call(LuaValue.valueOf(op1), this);
    }

    /**
     * Less than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this < rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LT} metatag is defined.
     * @see #gteq_b(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lt(LuaValue rhs) {
        return comparemt(LuaConstant.MetaTag.LT, rhs);
    }

    /**
     * Less than: Perform numeric comparison with another value of double type,
     * including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this < rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq_b(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lt(double rhs) {
        return comparisonError("number");
    }

    /**
     * Less than: Perform numeric comparison with another value of int type,
     * including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this < rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq_b(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lt(int rhs) {
        return comparisonError("number");
    }

    /**
     * Less than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this < rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LT} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lt_b(LuaValue rhs) {
        return comparemt(LuaConstant.MetaTag.LT, rhs).toboolean();
    }

    /**
     * Less than: Perform numeric comparison with another value of int type,
     * including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this < rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lt_b(int rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Less than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this < rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LT} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lt_b(double rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Less than or equals: Perform numeric or string comparison with another
     * value of unknown type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this <= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LE} metatag is defined.
     * @see #gteq_b(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lteq(LuaValue rhs) {
        return comparemt(LuaConstant.MetaTag.LE, rhs);
    }

    /**
     * Less than or equals: Perform numeric comparison with another value of
     * double type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this <= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq_b(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lteq(double rhs) {
        return comparisonError("number");
    }

    /**
     * Less than or equals: Perform numeric comparison with another value of int
     * type, including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this <= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq_b(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue lteq(int rhs) {
        return comparisonError("number");
    }

    /**
     * Less than or equals: Perform numeric or string comparison with another
     * value of unknown type, including metatag processing, and returning java
     * boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this <= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LE} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lteq_b(LuaValue rhs) {
        return comparemt(LuaConstant.MetaTag.LE, rhs).toboolean();
    }

    /**
     * Less than or equals: Perform numeric comparison with another value of int
     * type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this <= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lteq_b(int rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Less than or equals: Perform numeric comparison with another value of
     * double type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this <= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean lteq_b(double rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Greater than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this > rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LE} metatag is defined.
     * @see #gteq_b(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gt(LuaValue rhs) {
        return rhs.comparemt(LuaConstant.MetaTag.LE, this);
    }

    /**
     * Greater than: Perform numeric comparison with another value of double
     * type, including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this > rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq_b(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gt(double rhs) {
        return comparisonError("number");
    }

    /**
     * Greater than: Perform numeric comparison with another value of int type,
     * including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this > rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq_b(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gt(int rhs) {
        return comparisonError("number");
    }

    /**
     * Greater than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this > rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LE} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gt_b(LuaValue rhs) {
        return rhs.comparemt(LuaConstant.MetaTag.LE, this).toboolean();
    }

    /**
     * Greater than: Perform numeric comparison with another value of int type,
     * including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this > rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LE} metatag is
     *                  defined.
     * @see #gteq(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gt_b(int rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Greater than: Perform numeric or string comparison with another value of
     * unknown type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this > rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LE} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gt_b(double rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Greater than or equals: Perform numeric or string comparison with another
     * value of unknown type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this >= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LT} metatag is defined.
     * @see #gteq_b(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gteq(LuaValue rhs) {
        return rhs.comparemt(LuaConstant.MetaTag.LT, this);
    }

    /**
     * Greater than or equals: Perform numeric comparison with another value of
     * double type, including metatag processing, and returning
     * {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this >= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq_b(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gteq(double rhs) {
        return comparisonError("number");
    }

    /**
     * Greater than or equals: Perform numeric comparison with another value of
     * int type, including metatag processing, and returning {@link LuaValue}.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return {@link LuaConstant#TRUE} if {@code (this >= rhs)}, {@link LuaConstant#FALSE} if not, or
     * {@link LuaValue} if metatag processing occurs
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq_b(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public LuaValue gteq(int rhs) {
        return valueOf(todouble() >= rhs);
    }

    /**
     * Greater than or equals: Perform numeric or string comparison with another
     * value of unknown type, including metatag processing, and returning java
     * boolean.
     * <p>
     * To be comparable, both operands must derive from {@link LuaString} or
     * both must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this >= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if either both operands are not a strings or both are
     *                  not numbers and no {@link LuaConstant.MetaTag#LT} metatag is defined.
     * @see #gteq(LuaValue)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gteq_b(LuaValue rhs) {
        return rhs.comparemt(LuaConstant.MetaTag.LT, this).toboolean();
    }

    /**
     * Greater than or equals: Perform numeric comparison with another value of
     * int type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this >= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq(int)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gteq_b(int rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Greater than or equals: Perform numeric comparison with another value of
     * double type, including metatag processing, and returning java boolean.
     * <p>
     * To be comparable, this must derive from {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return true if {@code (this >= rhs)}, false if not, and boolean
     * interpreation of result if metatag processing occurs.
     * @throws LuaError if this is not a number and no {@link LuaConstant.MetaTag#LT} metatag is
     *                  defined.
     * @see #gteq(double)
     * @see #comparemt(LuaValue, LuaValue)
     */
    public boolean gteq_b(double rhs) {
        comparisonError("number");
        return false;
    }

    /**
     * Perform metatag processing for comparison operations.
     * <p>
     * Finds the supplied metatag value and invokes it, or throws
     * {@link LuaError} if none applies.
     *
     * @param tag The metatag to look up
     * @param op1 The operand with which to to perform the operation
     * @return {@link LuaValue} resulting from metatag processing
     * @throws LuaError if metatag was not defined for either operand, or if the
     *                  operands are not the same type, or the metatag values
     *                  for the two operands are different.
     * @see #gt(LuaValue)
     * @see #gteq(LuaValue)
     * @see #lt(LuaValue)
     * @see #lteq(LuaValue)
     */
    public LuaValue comparemt(LuaValue tag, LuaValue op1) {
        LuaValue h;
        if (!(h = metatag(tag)).isnil() || !(h = op1.metatag(tag)).isnil())
            return h.call(this, op1);
        if (LuaConstant.MetaTag.LE.raweq(tag) && (!(h = metatag(LuaConstant.MetaTag.LT)).isnil() || !(h = op1.metatag(LuaConstant.MetaTag.LT)).isnil()))
            return h.call(op1, this).not();
        return error("bad argument: attempt to compare " + tag + " on " + getType().typeName + " and " + op1.getType().typeName);
    }

    /**
     * Perform string comparison with another value of any type using string
     * comparison based on byte values.
     * <p>
     * Only strings can be compared, meaning each operand must derive from
     * {@link LuaString}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return int < 0 for {@code (this < rhs)}, int > 0 for
     * {@code (this > rhs)}, or 0 when same string.
     * @throws LuaError if either operand is not a string
     */
    public int strcmp(LuaValue rhs) {
        error("attempt to compare " + getType().typeName);
        return 0;
    }

    /**
     * Perform string comparison with another value known to be a
     * {@link LuaString} using string comparison based on byte values.
     * <p>
     * Only strings can be compared, meaning each operand must derive from
     * {@link LuaString}.
     *
     * @param rhs The right-hand-side value to perform the comparison with
     * @return int < 0 for {@code (this < rhs)}, int > 0 for
     * {@code (this > rhs)}, or 0 when same string.
     * @throws LuaError if this is not a string
     */
    public int strcmp(LuaString rhs) {
        error("attempt to compare " + getType().typeName);
        return 0;
    }

    /**
     * Concatenate another value onto this value and return the result using
     * rules of lua string concatenation including metatag processing.
     * <p>
     * Only strings and numbers as represented can be concatenated, meaning each
     * operand must derive from {@link LuaString} or {@link LuaNumber}.
     *
     * @param rhs The right-hand-side value to perform the operation with
     * @return {@link LuaValue} resulting from concatenation of
     * {@code (this .. rhs)}
     * @throws LuaError if either operand is not of an appropriate type, such as
     *                  nil or a table
     */
    public LuaValue concat(LuaValue rhs) {
        return this.concatmt(rhs);
    }

    /**
     * Reverse-concatenation: concatenate this value onto another value whose
     * type is unknwon and return the result using rules of lua string
     * concatenation including metatag processing.
     * <p>
     * Only strings and numbers as represented can be concatenated, meaning each
     * operand must derive from {@link LuaString} or {@link LuaNumber}.
     *
     * @param lhs The left-hand-side value onto which this will be concatenated
     * @return {@link LuaValue} resulting from concatenation of
     * {@code (lhs .. this)}
     * @throws LuaError if either operand is not of an appropriate type, such as
     *                  nil or a table
     * @see #concat(LuaValue)
     */
    public LuaValue concatTo(LuaValue lhs) {
        return lhs.concatmt(this);
    }

    /**
     * Reverse-concatenation: concatenate this value onto another value known to
     * be a {@link LuaNumber} and return the result using rules of lua string
     * concatenation including metatag processing.
     * <p>
     * Only strings and numbers as represented can be concatenated, meaning each
     * operand must derive from {@link LuaString} or {@link LuaNumber}.
     *
     * @param lhs The left-hand-side value onto which this will be concatenated
     * @return {@link LuaValue} resulting from concatenation of
     * {@code (lhs .. this)}
     * @throws LuaError if either operand is not of an appropriate type, such as
     *                  nil or a table
     * @see #concat(LuaValue)
     */
    public LuaValue concatTo(LuaNumber lhs) {
        return lhs.concatmt(this);
    }

    /**
     * Reverse-concatenation: concatenate this value onto another value known to
     * be a {@link LuaString} and return the result using rules of lua string
     * concatenation including metatag processing.
     * <p>
     * Only strings and numbers as represented can be concatenated, meaning each
     * operand must derive from {@link LuaString} or {@link LuaNumber}.
     *
     * @param lhs The left-hand-side value onto which this will be concatenated
     * @return {@link LuaValue} resulting from concatenation of
     * {@code (lhs .. this)}
     * @throws LuaError if either operand is not of an appropriate type, such as
     *                  nil or a table
     * @see #concat(LuaValue)
     */
    public LuaValue concatTo(LuaString lhs) {
        return lhs.concatmt(this);
    }

    /**
     * Convert the value to a {@link Buffer} for more efficient concatenation of
     * multiple strings.
     *
     * @return Buffer instance containing the string or number
     */
    public Buffer buffer() {
        return new Buffer(this);
    }

    /**
     * Concatenate a {@link Buffer} onto this value and return the result using
     * rules of lua string concatenation including metatag processing.
     * <p>
     * Only strings and numbers as represented can be concatenated, meaning each
     * operand must derive from {@link LuaString} or {@link LuaNumber}.
     *
     * @param rhs The right-hand-side {@link Buffer} to perform the operation
     *            with
     * @return LuaString resulting from concatenation of {@code (this .. rhs)}
     * @throws LuaError if either operand is not of an appropriate type, such as
     *                  nil or a table
     */
    public Buffer concat(Buffer rhs) {
        return rhs.concatTo(this);
    }

    /**
     * Perform metatag processing for concatenation operations.
     * <p>
     * Finds the {@link LuaConstant.MetaTag#CONCAT} metatag value and invokes it, or throws
     * {@link LuaError} if it doesn't exist.
     *
     * @param rhs The right-hand-side value to perform the operation with
     * @return {@link LuaValue} resulting from metatag processing for
     * {@link LuaConstant.MetaTag#CONCAT} metatag.
     * @throws LuaError if metatag was not defined for either operand
     */
    public LuaValue concatmt(LuaValue rhs) {
        LuaValue h = metatag(LuaConstant.MetaTag.CONCAT);
        if (h.isnil() && (h = rhs.metatag(LuaConstant.MetaTag.CONCAT)).isnil())
            error("attempt to concatenate " + getType().typeName + " and " + rhs.getType().typeName);
        return h.call(this, rhs);
    }

    /**
     * Perform boolean {@code and} with another operand, based on lua rules for
     * boolean evaluation. This returns either {@code this} or {@code rhs}
     * depending on the boolean value for {@code this}.
     *
     * @param rhs The right-hand-side value to perform the operation with
     * @return {@code this} if {@code this.toboolean()} is false, {@code rhs}
     * otherwise.
     */
    public LuaValue and(LuaValue rhs) {
        return this.toboolean() ? rhs : this;
    }

    /**
     * Perform boolean {@code or} with another operand, based on lua rules for
     * boolean evaluation. This returns either {@code this} or {@code rhs}
     * depending on the boolean value for {@code this}.
     *
     * @param rhs The right-hand-side value to perform the operation with
     * @return {@code this} if {@code this.toboolean()} is true, {@code rhs}
     * otherwise.
     */
    public LuaValue or(LuaValue rhs) {
        return this.toboolean() ? this : rhs;
    }

    /**
     * Perform end-condition test in for-loop processing.
     * <p>
     * Used in lua-bytecode to Java-bytecode conversion.
     *
     * @param limit the numerical limit to complete the for loop
     * @param step  the numberical step size to use.
     * @return true if limit has not been reached, false otherwise.
     */
    public boolean testfor_b(LuaValue limit, LuaValue step) {
        return step.gt_b(0) ? lteq_b(limit) : gteq_b(limit);
    }

    /**
     * Convert this value to a string if it is a {@link LuaString} or
     * {@link LuaNumber}, or throw a {@link LuaError} if it is not
     *
     * @return {@link LuaString} corresponding to the value if a string or
     * number
     * @throws LuaError if not a string or number
     */
    public LuaString strvalue() {
        typeError("string or number");
        return null;
    }

    /**
     * Return this value as a strong reference, or null if it was weak and is no
     * longer referenced.
     *
     * @return {@link LuaValue} referred to, or null if it was weak and is no
     * longer referenced.
     * @see WeakTable
     */
    public LuaValue strongvalue() {
        return this;
    }

    /**
     * Convert java boolean to a {@link LuaValue}.
     *
     * @param b boolean value to convert
     * @return {@link LuaConstant#TRUE} if not or {@link LuaConstant#FALSE} if false
     */
    public static LuaBoolean valueOf(boolean b) {
        return b ? LuaConstant.TRUE : LuaConstant.FALSE;
    }

    /**
     * Convert java int to a {@link LuaValue}.
     *
     * @param i int value to convert
     * @return {@link LuaInteger} instance, possibly pooled, whose value is i
     */
    public static LuaInteger valueOf(int i) {
        return LuaInteger.valueOf(i);
    }

    /**
     * Convert java double to a {@link LuaValue}. This may return a
     * {@link LuaInteger} or {@link LuaDouble} depending on the value supplied.
     *
     * @param d double value to convert
     * @return {@link LuaNumber} instance, possibly pooled, whose value is d
     */
    public static LuaNumber valueOf(double d) {
        return LuaDouble.valueOf(d);
    }

    /**
     * Convert java string to a {@link LuaValue}.
     *
     * @param s String value to convert
     * @return {@link LuaString} instance, possibly pooled, whose value is s
     */
    public static LuaString valueOf(String s) {
        return LuaString.valueOf(s);
    }

    /**
     * Convert bytes in an array to a {@link LuaValue}.
     *
     * @param bytes byte array to convert
     * @return {@link LuaString} instance, possibly pooled, whose bytes are
     * those in the supplied array
     */
    public static LuaString valueOf(byte[] bytes) {
        return LuaString.valueOf(bytes);
    }

    /**
     * Convert bytes in an array to a {@link LuaValue}.
     *
     * @param bytes byte array to convert
     * @param off   offset into the byte array, starting at 0
     * @param len   number of bytes to include in the {@link LuaString}
     * @return {@link LuaString} instance, possibly pooled, whose bytes are
     * those in the supplied array
     */
    public static LuaString valueOf(byte[] bytes, int off, int len) {
        return LuaString.valueOf(bytes, off, len);
    }

    /**
     * Construct an empty {@link LuaTable}.
     *
     * @return new {@link LuaTable} instance with no values and no metatable.
     */
    public static LuaTable tableOf() {
        return new LuaTable();
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied array values.
     *
     * @param varargs  {@link Varargs} containing the values to use in
     *                 initialization
     * @param firstarg the index of the first argument to use from the varargs,
     *                 1 being the first.
     * @return new {@link LuaTable} instance with sequential elements coming
     * from the varargs.
     */
    public static LuaTable tableOf(Varargs varargs, int firstarg) {
        return new LuaTable(varargs, firstarg);
    }

    /**
     * Construct an empty {@link LuaTable} preallocated to hold array and hashed
     * elements
     *
     * @param narray Number of array elements to preallocate
     * @param nhash  Number of hash elements to preallocate
     * @return new {@link LuaTable} instance with no values and no metatable,
     * but preallocated for array and hashed elements.
     */
    public static LuaTable tableOf(int narray, int nhash) {
        return new LuaTable(narray, nhash);
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied array values.
     *
     * @param unnamedValues array of {@link LuaValue} containing the values to
     *                      use in initialization
     * @return new {@link LuaTable} instance with sequential elements coming
     * from the array.
     */
    public static LuaTable listOf(LuaValue[] unnamedValues) {
        return new LuaTable(null, unnamedValues, null);
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied array values.
     *
     * @param unnamedValues array of {@link LuaValue} containing the first
     *                      values to use in initialization
     * @param lastarg       {@link Varargs} containing additional values to use
     *                      in initialization to be put after the last
     *                      unnamedValues element
     * @return new {@link LuaTable} instance with sequential elements coming
     * from the array and varargs.
     */
    public static LuaTable listOf(LuaValue[] unnamedValues, Varargs lastarg) {
        return new LuaTable(null, unnamedValues, lastarg);
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied named values.
     *
     * @param namedValues array of {@link LuaValue} containing the keys and
     *                    values to use in initialization in order
     *                    {@code {key-a, value-a, key-b, value-b, ...} }
     * @return new {@link LuaTable} instance with non-sequential keys coming
     * from the supplied array.
     */
    public static LuaTable tableOf(LuaValue[] namedValues) {
        return new LuaTable(namedValues, null, null);
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied named values and
     * sequential elements. The named values will be assigned first, and the
     * sequential elements will be assigned later, possibly overwriting named
     * values at the same slot if there are conflicts.
     *
     * @param namedValues   array of {@link LuaValue} containing the keys and
     *                      values to use in initialization in order
     *                      {@code {key-a, value-a, key-b, value-b, ...} }
     * @param unnamedValues array of {@link LuaValue} containing the sequenctial
     *                      elements to use in initialization in order
     *                      {@code {value-1, value-2, ...} }, or null if there
     *                      are none
     * @return new {@link LuaTable} instance with named and sequential values
     * supplied.
     */
    public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues) {
        return new LuaTable(namedValues, unnamedValues, null);
    }

    /**
     * Construct a {@link LuaTable} initialized with supplied named values and
     * sequential elements in an array part and as varargs. The named values
     * will be assigned first, and the sequential elements will be assigned
     * later, possibly overwriting named values at the same slot if there are
     * conflicts.
     *
     * @param namedValues   array of {@link LuaValue} containing the keys and
     *                      values to use in initialization in order
     *                      {@code {key-a, value-a, key-b, value-b, ...} }
     * @param unnamedValues array of {@link LuaValue} containing the first
     *                      sequenctial elements to use in initialization in
     *                      order {@code {value-1, value-2, ...} }, or null if
     *                      there are none
     * @param lastarg       {@link Varargs} containing additional values to use
     *                      in the sequential part of the initialization, to be
     *                      put after the last unnamedValues element
     * @return new {@link LuaTable} instance with named and sequential values
     * supplied.
     */
    public static LuaTable tableOf(LuaValue[] namedValues, LuaValue[] unnamedValues, Varargs lastarg) {
        return new LuaTable(namedValues, unnamedValues, lastarg);
    }

    /**
     * Construct a LuaUserdata for an object.
     *
     * @param o The java instance to be wrapped as userdata
     * @return {@link LuaUserdata} value wrapping the java instance.
     */
    public static LuaUserdata userdataOf(Object o) {
        return new LuaUserdata(o);
    }

    /**
     * Construct a LuaUserdata for an object with a user supplied metatable.
     *
     * @param o         The java instance to be wrapped as userdata
     * @param metatable The metatble to associate with the userdata instance.
     * @return {@link LuaUserdata} value wrapping the java instance.
     */
    public static LuaUserdata userdataOf(Object o, LuaValue metatable) {
        return new LuaUserdata(o, metatable);
    }

    /**
     * Constant limiting metatag loop processing
     */
    private static final int MAXTAGLOOP = 100;

    /**
     * Return value for field reference including metatag processing, or
     * {@link LuaConstant#NIL} if it doesn't exist.
     *
     * @param t   {@link LuaValue} on which field is being referenced, typically
     *            a table or something with the metatag {@link LuaConstant.MetaTag#INDEX}
     *            defined
     * @param key {@link LuaValue} naming the field to reference
     * @return {@link LuaValue} for the {@code key} if it exists, or
     *         {@link LuaConstant#NIL}
     * @throws LuaError if there is a loop in metatag processing
     */
    /**
     * get value from metatable operations, or NIL if not defined by metatables
     */
    protected static LuaValue gettable(LuaValue t, LuaValue key) {
        LuaValue tm;
        int loop = 0;
        do {
            if (t.istable()) {
                LuaValue res = t.rawget(key);
                if (!res.isnil() || (tm = t.metatag(LuaConstant.MetaTag.INDEX)).isnil())
                    return res;
            } else if ((tm = t.metatag(LuaConstant.MetaTag.INDEX)).isnil())
                t.indexerror(key.tojstring());
            if (tm.isfunction())
                return tm.call(t, key);
            t = tm;
        } while (++loop < MAXTAGLOOP);
        error("loop in gettable");
        return LuaConstant.NIL;
    }

    /**
     * Perform field assignment including metatag processing.
     *
     * @param t     {@link LuaValue} on which value is being set, typically a
     *              table or something with the metatag
     *              {@link LuaConstant.MetaTag#NEWINDEX} defined
     * @param key   {@link LuaValue} naming the field to assign
     * @param value {@link LuaValue} the new value to assign to {@code key}
     * @return true if assignment or metatag processing succeeded, false
     * otherwise
     * @throws LuaError if there is a loop in metatag processing
     */
    protected static boolean settable(LuaValue t, LuaValue key, LuaValue value) {
        LuaValue tm;
        int loop = 0;
        do {
            if (t.istable()) {
                if (!t.rawget(key).isnil() || (tm = t.metatag(LuaConstant.MetaTag.NEWINDEX)).isnil()) {
                    t.rawset(key, value);
                    return true;
                }
            } else if ((tm = t.metatag(LuaConstant.MetaTag.NEWINDEX)).isnil())
                throw new LuaError("table expected for set index ('" + key + "') value, got " + t.getType().typeName);
            if (tm.isfunction()) {
                tm.call(t, key, value);
                return true;
            }
            t = tm;
        } while (++loop < MAXTAGLOOP);
        error("loop in settable");
        return false;
    }

    /**
     * Get particular metatag, or return {@link LuaConstant#NIL} if it doesn't
     * exist
     *
     * @param tag Metatag name to look up, typically a string such as
     *            {@link LuaConstant.MetaTag#INDEX} or {@link LuaConstant.MetaTag#NEWINDEX}
     * @return {@link LuaValue} for tag {@code reason}, or {@link LuaConstant#NIL}
     */
    public LuaValue metatag(LuaValue tag) {
        LuaValue mt = getmetatable();
        if (mt == null)
            return LuaConstant.NIL;
        return mt.rawget(tag);
    }

    /**
     * Get particular metatag, or throw {@link LuaError} if it doesn't exist
     *
     * @param tag    Metatag name to look up, typically a string such as
     *               {@link LuaConstant.MetaTag#INDEX} or {@link LuaConstant.MetaTag#NEWINDEX}
     * @param reason Description of error when tag lookup fails.
     * @return {@link LuaValue} that can be called
     * @throws LuaError when the lookup fails.
     */
    protected LuaValue checkmetatag(LuaValue tag, String reason) {
        LuaValue h = this.metatag(tag);
        if (h.isnil())
            throw new LuaError(reason + "a " + getType().typeName + " value");
        return h;
    }

    /**
     * Construct a Metatable instance from the given LuaValue
     */
    protected static Metatable metatableOf(LuaValue mt) {
        if (mt != null && mt.istable()) {
            LuaValue mode = mt.rawget(LuaConstant.MetaTag.MODE);
            if (mode.isstring()) {
                String m = mode.tojstring();
                boolean weakkeys = m.indexOf('k') >= 0;
                boolean weakvalues = m.indexOf('v') >= 0;
                if (weakkeys || weakvalues) {
                    return new WeakTable(weakkeys, weakvalues, mt);
                }
            }
            return (LuaTable) mt;
        } else if (mt != null) {
            return new NonTableMetatable(mt);
        } else {
            return null;
        }
    }

    /**
     * Throw {@link LuaError} indicating index was attempted on illegal type
     *
     * @throws LuaError when called.
     */
    private void indexerror(String key) {
        error("attempt to index ? (a " + getType().typeName + " value) with key '" + key + "'");
    }

    /**
     * Construct a {@link Varargs} around an array of {@link LuaValue}s.
     *
     * @param v The array of {@link LuaValue}s
     * @return {@link Varargs} wrapping the supplied values.
     * @see LuaValue#varargsOf(LuaValue, Varargs)
     * @see LuaValue#varargsOf(LuaValue[], int, int)
     */
    public static Varargs varargsOf(final LuaValue[] v) {
        switch (v.length) {
            case 0:
                return LuaConstant.NONE;
            case 1:
                return v[0];
            case 2:
                return new Varargs.PairVarargs(v[0], v[1]);
            default:
                return new Varargs.ArrayVarargs(v, LuaConstant.NONE);
        }
    }

    /**
     * Construct a {@link Varargs} around an array of {@link LuaValue}s.
     *
     * @param v The array of {@link LuaValue}s
     * @param r {@link Varargs} contain values to include at the end
     * @return {@link Varargs} wrapping the supplied values.
     * @see LuaValue#varargsOf(LuaValue[])
     * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
     */
    public static Varargs varargsOf(final LuaValue[] v, Varargs r) {
        switch (v.length) {
            case 0:
                return r;
            case 1:
                return r.narg() > 0 ? (Varargs) new Varargs.PairVarargs(v[0], r) : (Varargs) v[0];
            case 2:
                return r.narg() > 0 ? (Varargs) new Varargs.ArrayVarargs(v, r)
                    : (Varargs) new Varargs.PairVarargs(v[0], v[1]);
            default:
                return new Varargs.ArrayVarargs(v, r);
        }
    }

    /**
     * Construct a {@link Varargs} around an array of {@link LuaValue}s.
     *
     * @param v      The array of {@link LuaValue}s
     * @param offset number of initial values to skip in the array
     * @param length number of values to include from the array
     * @return {@link Varargs} wrapping the supplied values.
     * @see LuaValue#varargsOf(LuaValue[])
     * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
     */
    public static Varargs varargsOf(final LuaValue[] v, final int offset, final int length) {
        switch (length) {
            case 0:
                return LuaConstant.NONE;
            case 1:
                return v[offset];
            case 2:
                return new Varargs.PairVarargs(v[offset + 0], v[offset + 1]);
            default:
                return new Varargs.ArrayPartVarargs(v, offset, length, LuaConstant.NONE);
        }
    }

    /**
     * Construct a {@link Varargs} around an array of {@link LuaValue}s.
     * <p>
     * Caller must ensure that array contents are not mutated after this call or
     * undefined behavior will result.
     *
     * @param v      The array of {@link LuaValue}s
     * @param offset number of initial values to skip in the array
     * @param length number of values to include from the array
     * @param more   {@link Varargs} contain values to include at the end
     * @return {@link Varargs} wrapping the supplied values.
     * @see LuaValue#varargsOf(LuaValue[], Varargs)
     * @see LuaValue#varargsOf(LuaValue[], int, int)
     */
    public static Varargs varargsOf(final LuaValue[] v, final int offset, final int length, Varargs more) {
        switch (length) {
            case 0:
                return more;
            case 1:
                return more.narg() > 0 ? (Varargs) new Varargs.PairVarargs(v[offset], more) : (Varargs) v[offset];
            case 2:
                return more.narg() > 0 ? (Varargs) new Varargs.ArrayPartVarargs(v, offset, length, more)
                    : (Varargs) new Varargs.PairVarargs(v[offset], v[offset + 1]);
            default:
                return new Varargs.ArrayPartVarargs(v, offset, length, more);
        }
    }

    /**
     * Construct a {@link Varargs} around a set of 2 or more {@link LuaValue}s.
     * <p>
     * This can be used to wrap exactly 2 values, or a list consisting of 1
     * initial value followed by another variable list of remaining values.
     *
     * @param v First {@link LuaValue} in the {@link Varargs}
     * @param r {@link LuaValue} supplying the 2rd value, or {@link Varargs}s
     *          supplying all values beyond the first
     * @return {@link Varargs} wrapping the supplied values.
     */
    public static Varargs varargsOf(LuaValue v, Varargs r) {
        switch (r.narg()) {
            case 0:
                return v;
            default:
                return new Varargs.PairVarargs(v, r);
        }
    }

    /**
     * Construct a {@link Varargs} around a set of 3 or more {@link LuaValue}s.
     * <p>
     * This can be used to wrap exactly 3 values, or a list consisting of 2
     * initial values followed by another variable list of remaining values.
     *
     * @param v1 First {@link LuaValue} in the {@link Varargs}
     * @param v2 Second {@link LuaValue} in the {@link Varargs}
     * @param v3 {@link LuaValue} supplying the 3rd value, or {@link Varargs}s
     *           supplying all values beyond the second
     * @return {@link Varargs} wrapping the supplied values.
     */
    public static Varargs varargsOf(LuaValue v1, LuaValue v2, Varargs v3) {
        switch (v3.narg()) {
            case 0:
                return new Varargs.PairVarargs(v1, v2);
            default:
                return new Varargs.ArrayPartVarargs(new LuaValue[]{v1, v2}, 0, 2, v3);
        }
    }

    /**
     * Construct a {@link TailcallVarargs} around a function and arguments.
     * <p>
     * The tail call is not yet called or processing until the client invokes
     * {@link TailcallVarargs#eval()} which performs the tail call processing.
     * <p>
     * This method is typically not used directly by client code. Instead use
     * one of the function invocation methods.
     *
     * @param func {@link LuaValue} to be called as a tail call
     * @param args {@link Varargs} containing the arguments to the call
     * @return {@link TailcallVarargs} to be used in tailcall oprocessing.
     * @see LuaValue#call()
     * @see LuaValue#invoke()
     * @see LuaValue#method(LuaValue)
     * @see LuaValue#invokemethod(LuaValue)
     */
    public static Varargs tailcallOf(LuaValue func, Varargs args) {
        return new TailcallVarargs(func, args);
    }

    /**
     * Callback used during tail call processing to invoke the function once.
     * <p>
     * This may return a {@link TailcallVarargs} to be evaluated by the client.
     * <p>
     * This should not be called directly, instead use one of the call
     * invocation functions.
     *
     * @param args the arguments to the call invocation.
     * @return Varargs the return values, possible a TailcallVarargs.
     * @see LuaValue#call()
     * @see LuaValue#invoke()
     * @see LuaValue#method(LuaValue)
     * @see LuaValue#invokemethod(LuaValue)
     */
    public Varargs onInvoke(Varargs args) {
        return invoke(args);
    }

    /**
     * Hook for implementations such as LuaJC to load the environment of the
     * main chunk into the first upvalue location. If the function has no
     * upvalues or is not a main chunk, calling this will be no effect.
     *
     * @param env The environment to load into the first upvalue, if there is
     *            one.
     */
    public void initupvalue1(LuaValue env) {
    }

    /**
     * Varargs implemenation with no values.
     * <p>
     * This is an internal class not intended to be used directly. Instead use
     * the predefined constant {@link LuaConstant#NONE}
     *
     * @see LuaConstant#NONE
     */
    public static final class None extends LuaNil {
        public static final None NONE = new None();

        private None() {
        }

        @Override
        public LuaValue arg(int i) {
            return LuaConstant.NIL;
        }

        @Override
        public int narg() {
            return 0;
        }

        @Override
        public LuaValue arg1() {
            return LuaConstant.NIL;
        }

        @Override
        public String tojstring() {
            return "none";
        }

        @Override
        public Varargs subargs(final int start) {
            return start > 0 ? this : argumentError(1, "start must be > 0");
        }

        @Override
        void copyto(LuaValue[] dest, int offset, int length) {
            for (; length > 0; length--)
                dest[offset++] = LuaConstant.NIL;
        }
    }

    /**
     * Create a {@code Varargs} instance containing arguments starting at index
     * {@code start}
     *
     * @param start the index from which to include arguments, where 1 is the
     *              first argument.
     * @return Varargs containing argument { start, start+1, ... , narg-start-1
     * }
     */
    @Override
    public Varargs subargs(final int start) {
        if (start == 1)
            return this;
        if (start > 1)
            return LuaConstant.NONE;
        return argumentError(1, "start must be > 0");
    }

}