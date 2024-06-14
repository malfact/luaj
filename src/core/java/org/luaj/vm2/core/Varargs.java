/*
 * LuaJ JSE 22 Update
 * malfact @ June 2024
 */
package org.luaj.vm2.core;

import org.luaj.vm2.*;
import org.luaj.vm2.util.LuaConstant;

/**
 * Class to encapsulate varargs values, either as part of a variable argument list, or multiple return values.
 * <p>
 * To construct varargs, use one of the static methods such as
 * {@code LuaValue.varargsOf(LuaValue,LuaValue)}
 * <p>
 * <p>
 * Any LuaValue can be used as a stand-in for Varargs, for both calls and return values.
 * When doing so, {@code count()} will return {@code 1} and {@code get(1)} will return this.
 * This simplifies the case when calling or implementing varargs functions with only
 * 1 argument or 1 return value.
 * <p>
 * Varargs can also be derived from other varargs by appending to the front with a call
 * such as  {@code LuaValue.varargsOf(LuaValue,Varargs)}
 * or by taking a portion of the args using {@code Varargs.subargs(int start)}
 * <p>
 *
 * @see LuaValue#varargsOf(LuaValue[])
 * @see LuaValue#varargsOf(LuaValue, Varargs)
 * @see LuaValue#varargsOf(LuaValue[], Varargs)
 * @see LuaValue#varargsOf(LuaValue, LuaValue, Varargs)
 * @see LuaValue#varargsOf(LuaValue[], int, int)
 * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
 * @see LuaValue#subargs(int)
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public abstract class Varargs {

    public abstract LuaValue get(int i);

    /**
     * Get the number of values stored in this value.
     *
     * @return The number of stored values, otherwise {@code 0}.
     */
    public abstract int count();

    /**
     * Evaluate any pending tail call and return result.
     *
     * @return the evaluated tail call result
     */
    public Varargs eval() {
        return this;
    }

    /**
     * Return true if this is a TailcallVarargs
     *
     * @return true if a tail call, false otherwise
     */
    public boolean isTailCall() {
        return false;
    }

    // -----------------------------------------------------------------------
    // utilities to get specific arguments and type-check them.
    // -----------------------------------------------------------------------

    /**
     * Tests if a value exists at argument i.
     *
     * @param i the index of the argument to test, 1 is the first argument
     * @return true if the argument exists, false otherwise
     */
    public boolean isvalue(int i) {
        return i > 0 && i <= count();
    }

    /**
     * Return argument i as a LuaValue if it exists, or throw an error.
     *
     * @param i the index of the argument to test, 1 is the first argument
     * @return LuaValue value if the argument exists
     * @throws LuaError if the argument does not exist.
     */
    public LuaValue checkvalue(int i) {
        return i <= count() ? get(i) : LuaValue.argumentError(i, "value expected");
    }

    /**
     * Performs test on argument i as a LuaValue when a user-supplied assertion passes, or throw an error.
     * Returns normally if the value of {@code test} is {@code true}, otherwise throws and argument error with
     * the supplied message, {@code msg}.
     *
     * @param test user supplied assertion to test against
     * @param i    the index to report in any error message
     * @param msg  the error message to use when the test fails
     * @throws LuaError if the the value of {@code test} is {@code false}
     */
    public void argcheck(boolean test, int i, String msg) {
        if (!test) LuaValue.argumentError(i, msg);
    }

    /**
     * Return true if there is no argument or nil at argument i.
     *
     * @param i the index of the argument to test, 1 is the first argument
     * @return true if argument i contains either no argument or nil
     */
    public boolean isnoneornil(int i) {
        return i > count() || get(i).isnil();
    }

    /**
     * Convert the list of varargs values to a human readable java String.
     *
     * @return String value in human readable form such as {1,2}.
     */
    public String tojstring() {
        Buffer sb = new Buffer();
        sb.append("(");
        for (int i = 1, n = count(); i <= n; i++) {
            if (i > 1) sb.append(",");
            sb.append(get(i).tojstring());
        }
        sb.append(")");
        return sb.tojstring();
    }

    /**
     * Convert the value or values to a java String using Varargs.tojstring()
     *
     * @return String value in human readable form.
     * @see Varargs#tojstring()
     */
    public String toString() {
        return tojstring();
    }

    /**
     * Create a {@code Varargs} instance containing arguments starting at index {@code start}
     *
     * @param start the index from which to include arguments, where 1 is the first argument.
     * @return Varargs containing argument { start, start+1,  ... , narg-start-1 }
     */
    abstract public Varargs subargs(final int start);

    /**
     * Implementation of Varargs for use in the Varargs.subargs() function.
     *
     * @see Varargs#subargs(int)
     */
    static class SubVarargs extends Varargs {
        private final Varargs v;
        private final int start;
        private final int end;

        public SubVarargs(Varargs varargs, int start, int end) {
            this.v = varargs;
            this.start = start;
            this.end = end;
        }

        @Override
        public LuaValue get(int i) {
            i += start - 1;
            return i >= start && i <= end ? v.get(1) : LuaConstant.NIL;
        }

        @Override
        public int count() {
            return end + 1 - start;
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public Varargs subargs(final int start) {
            if (start == 1)
                return this;

            final int newstart = this.start + start - 1;
            if (start > 0) {
                if (newstart >= this.end)
                    return LuaConstant.NONE;
                if (newstart == this.end)
                    return v.get(this.end);
                if (newstart == this.end - 1)
                    return new Varargs.PairVarargs(v.get(this.end - 1), v.get(this.end));
                return new SubVarargs(v, newstart, this.end);
            }
            return new SubVarargs(v, newstart, this.end);
        }
    }

    /**
     * Varargs implemenation backed by two values.
     * <p>
     * This is an internal class not intended to be used directly.
     * Instead use the corresponding static method on LuaValue.
     *
     * @see LuaValue#varargsOf(LuaValue, Varargs)
     */
    static final class PairVarargs extends Varargs {
        private final LuaValue v1;
        private final Varargs v2;

        /**
         * Construct a Varargs from an two LuaValue.
         * <p>
         * This is an internal class not intended to be used directly.
         * Instead use the corresponding static method on LuaValue.
         *
         * @see LuaValue#varargsOf(LuaValue, Varargs)
         */
        PairVarargs(LuaValue v1, Varargs v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        public LuaValue get(int i) {
            return i == 1 ? v1 : v2.get(i - 1);
        }

        public int count() {
            return 1 + v2.count();
        }

        public Varargs subargs(final int start) {
            if (start == 1)
                return this;
            if (start == 2)
                return v2;
            if (start > 2)
                return v2.subargs(start - 1);

            return LuaValue.argumentError(1, "start must be > 0");
        }
    }

    /**
     * Varargs implemenation backed by an array of LuaValues
     * <p>
     * This is an internal class not intended to be used directly.
     * Instead use the corresponding static methods on LuaValue.
     *
     * @see LuaValue#varargsOf(LuaValue[])
     * @see LuaValue#varargsOf(LuaValue[], Varargs)
     */
    static final class ArrayVarargs extends Varargs {
        private final LuaValue[] v;
        private final Varargs r;

        /**
         * Construct a Varargs from an array of LuaValue.
         * <p>
         * This is an internal class not intended to be used directly.
         * Instead use the corresponding static methods on LuaValue.
         *
         * @see LuaValue#varargsOf(LuaValue[])
         * @see LuaValue#varargsOf(LuaValue[], Varargs)
         */
        ArrayVarargs(LuaValue[] v, Varargs r) {
            this.v = v;
            this.r = r;
        }

        @Override
        public LuaValue get(int i) {
            return i < 1 ? LuaConstant.NIL : i <= v.length ? v[i - 1] : r.get(i - v.length);
        }

        public int count() {
            return v.length + r.count();
        }

        public Varargs subargs(int start) {
            if (start <= 0)
                LuaValue.argumentError(1, "start must be > 0");
            if (start == 1)
                return this;
            if (start > v.length)
                return r.subargs(start - v.length);
            return LuaValue.varargsOf(v, start - 1, v.length - (start - 1), r);
        }

        void copyto(LuaValue[] dest, int offset, int length) {
            int n = Math.min(v.length, length);
            System.arraycopy(v, 0, dest, offset, n);
            r.copyto(dest, offset + n, length - n);
        }
    }

    /**
     * Varargs implemenation backed by an array of LuaValues
     * <p>
     * This is an internal class not intended to be used directly.
     * Instead, use the corresponding static methods on LuaValue.
     *
     * @see LuaValue#varargsOf(LuaValue[], int, int)
     * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
     */
    static final class ArrayPartVarargs extends Varargs {
        private final int offset;
        private final LuaValue[] v;
        private final int length;
        private final Varargs more;

        /**
         * Construct a Varargs from an array of LuaValue.
         * <p>
         * This is an internal class not intended to be used directly.
         * Instead use the corresponding static methods on LuaValue.
         *
         * @see LuaValue#varargsOf(LuaValue[], int, int)
         */
        ArrayPartVarargs(LuaValue[] v, int offset, int length) {
            this.v = v;
            this.offset = offset;
            this.length = length;
            this.more = LuaConstant.NONE;
        }

        /**
         * Construct a Varargs from an array of LuaValue and additional arguments.
         * <p>
         * This is an internal class not intended to be used directly.
         * Instead use the corresponding static method on LuaValue.
         *
         * @see LuaValue#varargsOf(LuaValue[], int, int, Varargs)
         */
        public ArrayPartVarargs(LuaValue[] v, int offset, int length, Varargs more) {
            this.v = v;
            this.offset = offset;
            this.length = length;
            this.more = more;
        }

        @Override
        public LuaValue get(int i) {
            return i < 1 ? LuaConstant.NIL : i <= length ? v[offset + i - 1] : more.get(i - length);
        }

        public int count() {
            return length + more.count();
        }

        public Varargs subargs(int start) {
            if (start <= 0)
                LuaValue.argumentError(1, "start must be > 0");
            if (start == 1)
                return this;
            if (start > length)
                return more.subargs(start - length);
            return LuaValue.varargsOf(v, offset + start - 1, length - (start - 1), more);
        }

        void copyto(LuaValue[] dest, int offset, int length) {
            int n = Math.min(this.length, length);
            System.arraycopy(this.v, this.offset, dest, offset, n);
            more.copyto(dest, offset + n, length - n);
        }
    }

    /**
     * Copy values in a varargs into a destination array.
     * Internal utility method not intended to be called directly from user code.
     */
    void copyto(LuaValue[] dest, int offset, int length) {
        for (int i = 0; i < length; ++i)
            dest[offset + i] = get(i + 1);
    }

    /**
     * Return Varargs that cannot be using a shared array for the storage, and is flattened.
     * Internal utility method not intended to be called directly from user code.
     *
     * @return Varargs containing same values, but flattened and with a new array if needed.
     */
    public Varargs dealias() {
        int n = count();
        switch (n) {
            case 0:
                return LuaConstant.NONE;
            case 1:
                return get(1);
            case 2:
                return new PairVarargs(get(1), get(2));
            default:
                LuaValue[] v = new LuaValue[n];
                copyto(v, 0, n);
                return new ArrayVarargs(v, LuaConstant.NONE);
        }
    }
}
