/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2;

import org.junit.jupiter.api.Test;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    private final int sampleint = 77;
    private final long samplelong = 123400000000L;
    private final double sampledouble = 55.25;
    private final String samplestringstring = "abcdef";
    private final String samplestringint = String.valueOf(sampleint);
    private final String samplestringlong = String.valueOf(samplelong);
    private final String samplestringdouble = String.valueOf(sampledouble);
    private final Object sampleobject = new Object();
    private final MyData sampledata = new MyData();

    private final LuaValue somenil = LuaConstant.NIL;
    private final LuaValue sometrue = LuaConstant.TRUE;
    private final LuaValue somefalse = LuaConstant.FALSE;
    private final LuaValue zero = LuaConstant.ZERO;
    private final LuaValue intint = LuaValue.valueOf(sampleint);
    private final LuaValue longdouble = LuaValue.valueOf(samplelong);
    private final LuaValue doubledouble = LuaValue.valueOf(sampledouble);
    private final LuaValue stringstring = LuaValue.valueOf(samplestringstring);
    private final LuaValue stringint = LuaValue.valueOf(samplestringint);
    private final LuaValue stringlong = LuaValue.valueOf(samplestringlong);
    private final LuaValue stringdouble = LuaValue.valueOf(samplestringdouble);
    private final LuaTable table = LuaValue.tableOf();
    private final LuaFunction somefunc = new ZeroArgFunction() {
        @Override
        public LuaValue call() {
            return LuaConstant.NONE;
        }
    };
    private final LuaThread thread = new LuaThread(new Globals(), somefunc);
    private final LuaClosure someclosure = new LuaClosure(new Prototype(), new LuaTable());
    private final LuaUserdata userdataobj = LuaValue.userdataOf(sampleobject);
    private final LuaUserdata userdatacls = LuaValue.userdataOf(sampledata);

    public static final class MyData {
        public MyData() {
        }
    }

    // ===================== type checks =======================

    @Test
    void testIsBoolean() {
        assertFalse(somenil.isboolean());
        assertTrue(sometrue.isboolean());
        assertTrue(somefalse.isboolean());
        assertFalse(zero.isboolean());
        assertFalse(intint.isboolean());
        assertFalse(longdouble.isboolean());
        assertFalse(doubledouble.isboolean());
        assertFalse(stringstring.isboolean());
        assertFalse(stringint.isboolean());
        assertFalse(stringlong.isboolean());
        assertFalse(stringdouble.isboolean());
        assertFalse(thread.isboolean());
        assertFalse(table.isboolean());
        assertFalse(userdataobj.isboolean());
        assertFalse(userdatacls.isboolean());
        assertFalse(somefunc.isboolean());
        assertFalse(someclosure.isboolean());
    }

    @Test
    void testIsClosure() {
        assertFalse(somenil.isclosure());
        assertFalse(sometrue.isclosure());
        assertFalse(somefalse.isclosure());
        assertFalse(zero.isclosure());
        assertFalse(intint.isclosure());
        assertFalse(longdouble.isclosure());
        assertFalse(doubledouble.isclosure());
        assertFalse(stringstring.isclosure());
        assertFalse(stringint.isclosure());
        assertFalse(stringlong.isclosure());
        assertFalse(stringdouble.isclosure());
        assertFalse(thread.isclosure());
        assertFalse(table.isclosure());
        assertFalse(userdataobj.isclosure());
        assertFalse(userdatacls.isclosure());
        assertFalse(somefunc.isclosure());
        assertTrue(someclosure.isclosure());
    }

    @Test
    void testIsFunction() {
        assertFalse(somenil.isfunction());
        assertFalse(sometrue.isfunction());
        assertFalse(somefalse.isfunction());
        assertFalse(zero.isfunction());
        assertFalse(intint.isfunction());
        assertFalse(longdouble.isfunction());
        assertFalse(doubledouble.isfunction());
        assertFalse(stringstring.isfunction());
        assertFalse(stringint.isfunction());
        assertFalse(stringlong.isfunction());
        assertFalse(stringdouble.isfunction());
        assertFalse(thread.isfunction());
        assertFalse(table.isfunction());
        assertFalse(userdataobj.isfunction());
        assertFalse(userdatacls.isfunction());
        assertTrue(somefunc.isfunction());
        assertTrue(someclosure.isfunction());
    }

    @Test
    void testIsInt() {
        assertFalse(somenil.isint());
        assertFalse(sometrue.isint());
        assertFalse(somefalse.isint());
        assertTrue(zero.isint());
        assertTrue(intint.isint());
        assertFalse(longdouble.isint());
        assertFalse(doubledouble.isint());
        assertFalse(stringstring.isint());
        assertTrue(stringint.isint());
        assertFalse(stringdouble.isint());
        assertFalse(thread.isint());
        assertFalse(table.isint());
        assertFalse(userdataobj.isint());
        assertFalse(userdatacls.isint());
        assertFalse(somefunc.isint());
        assertFalse(someclosure.isint());
    }

    @Test
    void testIsIntType() {
        assertFalse(somenil.isinttype());
        assertFalse(sometrue.isinttype());
        assertFalse(somefalse.isinttype());
        assertTrue(zero.isinttype());
        assertTrue(intint.isinttype());
        assertFalse(longdouble.isinttype());
        assertFalse(doubledouble.isinttype());
        assertFalse(stringstring.isinttype());
        assertFalse(stringint.isinttype());
        assertFalse(stringlong.isinttype());
        assertFalse(stringdouble.isinttype());
        assertFalse(thread.isinttype());
        assertFalse(table.isinttype());
        assertFalse(userdataobj.isinttype());
        assertFalse(userdatacls.isinttype());
        assertFalse(somefunc.isinttype());
        assertFalse(someclosure.isinttype());
    }

    @Test
    void testIsLong() {
        assertFalse(somenil.islong());
        assertFalse(sometrue.islong());
        assertFalse(somefalse.islong());
        assertTrue(intint.isint());
        assertTrue(longdouble.islong());
        assertFalse(doubledouble.islong());
        assertFalse(stringstring.islong());
        assertTrue(stringint.islong());
        assertTrue(stringlong.islong());
        assertFalse(stringdouble.islong());
        assertFalse(thread.islong());
        assertFalse(table.islong());
        assertFalse(userdataobj.islong());
        assertFalse(userdatacls.islong());
        assertFalse(somefunc.islong());
        assertFalse(someclosure.islong());
    }

    @Test
    void testIsNil() {
        assertTrue(somenil.isnil());
        assertFalse(sometrue.isnil());
        assertFalse(somefalse.isnil());
        assertFalse(zero.isnil());
        assertFalse(intint.isnil());
        assertFalse(longdouble.isnil());
        assertFalse(doubledouble.isnil());
        assertFalse(stringstring.isnil());
        assertFalse(stringint.isnil());
        assertFalse(stringlong.isnil());
        assertFalse(stringdouble.isnil());
        assertFalse(thread.isnil());
        assertFalse(table.isnil());
        assertFalse(userdataobj.isnil());
        assertFalse(userdatacls.isnil());
        assertFalse(somefunc.isnil());
        assertFalse(someclosure.isnil());
    }

    @Test
    void testIsNumber() {
        assertFalse(somenil.isnumber());
        assertFalse(sometrue.isnumber());
        assertFalse(somefalse.isnumber());
        assertTrue(zero.isnumber());
        assertTrue(intint.isnumber());
        assertTrue(longdouble.isnumber());
        assertTrue(doubledouble.isnumber());
        assertFalse(stringstring.isnumber());
        assertTrue(stringint.isnumber());
        assertTrue(stringlong.isnumber());
        assertTrue(stringdouble.isnumber());
        assertFalse(thread.isnumber());
        assertFalse(table.isnumber());
        assertFalse(userdataobj.isnumber());
        assertFalse(userdatacls.isnumber());
        assertFalse(somefunc.isnumber());
        assertFalse(someclosure.isnumber());
    }

    @Test
    void testIsString() {
        assertFalse(somenil.isstring());
        assertFalse(sometrue.isstring());
        assertFalse(somefalse.isstring());
        assertTrue(zero.isstring());
        assertTrue(longdouble.isstring());
        assertTrue(doubledouble.isstring());
        assertTrue(stringstring.isstring());
        assertTrue(stringint.isstring());
        assertTrue(stringlong.isstring());
        assertTrue(stringdouble.isstring());
        assertFalse(thread.isstring());
        assertFalse(table.isstring());
        assertFalse(userdataobj.isstring());
        assertFalse(userdatacls.isstring());
        assertFalse(somefunc.isstring());
        assertFalse(someclosure.isstring());
    }

    @Test
    void testIsThread() {
        assertFalse(somenil.isthread());
        assertFalse(sometrue.isthread());
        assertFalse(somefalse.isthread());
        assertFalse(intint.isthread());
        assertFalse(longdouble.isthread());
        assertFalse(doubledouble.isthread());
        assertFalse(stringstring.isthread());
        assertFalse(stringint.isthread());
        assertFalse(stringdouble.isthread());
        assertTrue(thread.isthread());
        assertFalse(table.isthread());
        assertFalse(userdataobj.isthread());
        assertFalse(userdatacls.isthread());
        assertFalse(somefunc.isthread());
        assertFalse(someclosure.isthread());
    }

    @Test
    void testIsTable() {
        assertFalse(somenil.istable());
        assertFalse(sometrue.istable());
        assertFalse(somefalse.istable());
        assertFalse(intint.istable());
        assertFalse(longdouble.istable());
        assertFalse(doubledouble.istable());
        assertFalse(stringstring.istable());
        assertFalse(stringint.istable());
        assertFalse(stringdouble.istable());
        assertFalse(thread.istable());
        assertTrue(table.istable());
        assertFalse(userdataobj.istable());
        assertFalse(userdatacls.istable());
        assertFalse(somefunc.istable());
        assertFalse(someclosure.istable());
    }

    @Test
    void testIsUserdata() {
        assertFalse(somenil.isuserdata());
        assertFalse(sometrue.isuserdata());
        assertFalse(somefalse.isuserdata());
        assertFalse(intint.isuserdata());
        assertFalse(longdouble.isuserdata());
        assertFalse(doubledouble.isuserdata());
        assertFalse(stringstring.isuserdata());
        assertFalse(stringint.isuserdata());
        assertFalse(stringdouble.isuserdata());
        assertFalse(thread.isuserdata());
        assertFalse(table.isuserdata());
        assertTrue(userdataobj.isuserdata());
        assertTrue(userdatacls.isuserdata());
        assertFalse(somefunc.isuserdata());
        assertFalse(someclosure.isuserdata());
    }

    @Test
    void testIsUserdataObject() {
        assertFalse(somenil.isuserdata(Object.class));
        assertFalse(sometrue.isuserdata(Object.class));
        assertFalse(somefalse.isuserdata(Object.class));
        assertFalse(longdouble.isuserdata(Object.class));
        assertFalse(doubledouble.isuserdata(Object.class));
        assertFalse(stringstring.isuserdata(Object.class));
        assertFalse(stringint.isuserdata(Object.class));
        assertFalse(stringdouble.isuserdata(Object.class));
        assertFalse(thread.isuserdata(Object.class));
        assertFalse(table.isuserdata(Object.class));
        assertTrue(userdataobj.isuserdata(Object.class));
        assertTrue(userdatacls.isuserdata(Object.class));
        assertFalse(somefunc.isuserdata(Object.class));
        assertFalse(someclosure.isuserdata(Object.class));
    }

    @Test
    void testIsUserdataMyData() {
        assertFalse(somenil.isuserdata(MyData.class));
        assertFalse(sometrue.isuserdata(MyData.class));
        assertFalse(somefalse.isuserdata(MyData.class));
        assertFalse(longdouble.isuserdata(MyData.class));
        assertFalse(doubledouble.isuserdata(MyData.class));
        assertFalse(stringstring.isuserdata(MyData.class));
        assertFalse(stringint.isuserdata(MyData.class));
        assertFalse(stringdouble.isuserdata(MyData.class));
        assertFalse(thread.isuserdata(MyData.class));
        assertFalse(table.isuserdata(MyData.class));
        assertFalse(userdataobj.isuserdata(MyData.class));
        assertTrue(userdatacls.isuserdata(MyData.class));
        assertFalse(somefunc.isuserdata(MyData.class));
        assertFalse(someclosure.isuserdata(MyData.class));
    }

    // ===================== Coerce to Java =======================

    @Test
    void testToBoolean() {
        assertFalse(somenil.toboolean());
        assertTrue(sometrue.toboolean());
        assertFalse(somefalse.toboolean());
        assertTrue(zero.toboolean());
        assertTrue(intint.toboolean());
        assertTrue(longdouble.toboolean());
        assertTrue(doubledouble.toboolean());
        assertTrue(stringstring.toboolean());
        assertTrue(stringint.toboolean());
        assertTrue(stringlong.toboolean());
        assertTrue(stringdouble.toboolean());
        assertTrue(thread.toboolean());
        assertTrue(table.toboolean());
        assertTrue(userdataobj.toboolean());
        assertTrue(userdatacls.toboolean());
        assertTrue(somefunc.toboolean());
        assertTrue(someclosure.toboolean());
    }

    @Test
    void testToByte() {
        assertEquals((byte) 0, somenil.tobyte());
        assertEquals((byte) 0, somefalse.tobyte());
        assertEquals((byte) 0, sometrue.tobyte());
        assertEquals((byte) 0, zero.tobyte());
        assertEquals((byte) sampleint, intint.tobyte());
        assertEquals((byte) samplelong, longdouble.tobyte());
        assertEquals((byte) sampledouble, doubledouble.tobyte());
        assertEquals((byte) 0, stringstring.tobyte());
        assertEquals((byte) sampleint, stringint.tobyte());
        assertEquals((byte) samplelong, stringlong.tobyte());
        assertEquals((byte) sampledouble, stringdouble.tobyte());
        assertEquals((byte) 0, thread.tobyte());
        assertEquals((byte) 0, table.tobyte());
        assertEquals((byte) 0, userdataobj.tobyte());
        assertEquals((byte) 0, userdatacls.tobyte());
        assertEquals((byte) 0, somefunc.tobyte());
        assertEquals((byte) 0, someclosure.tobyte());
    }

    @Test
    void testToChar() {
        assertEquals((char) 0, somenil.tochar());
        assertEquals((char) 0, somefalse.tochar());
        assertEquals((char) 0, sometrue.tochar());
        assertEquals((char) 0, zero.tochar());
        assertEquals((char) sampleint, (int) intint.tochar());
        assertEquals((char) samplelong, (int) longdouble.tochar());
        assertEquals((char) sampledouble, (int) doubledouble.tochar());
        assertEquals((char) 0, stringstring.tochar());
        assertEquals((char) sampleint, (int) stringint.tochar());
        assertEquals((char) samplelong, (int) stringlong.tochar());
        assertEquals((char) sampledouble, (int) stringdouble.tochar());
        assertEquals((char) 0, thread.tochar());
        assertEquals((char) 0, table.tochar());
        assertEquals((char) 0, userdataobj.tochar());
        assertEquals((char) 0, userdatacls.tochar());
        assertEquals((char) 0, somefunc.tochar());
        assertEquals((char) 0, someclosure.tochar());
    }

    @Test
    void testToDouble() {
        assertEquals(0., somenil.todouble());
        assertEquals(0., somefalse.todouble());
        assertEquals(0., sometrue.todouble());
        assertEquals(0., zero.todouble());
        assertEquals(sampleint, intint.todouble());
        assertEquals(samplelong, longdouble.todouble());
        assertEquals(sampledouble, doubledouble.todouble());
        assertEquals(0, stringstring.todouble());
        assertEquals(sampleint, stringint.todouble());
        assertEquals(samplelong, stringlong.todouble());
        assertEquals(sampledouble, stringdouble.todouble());
        assertEquals(0., thread.todouble());
        assertEquals(0., table.todouble());
        assertEquals(0., userdataobj.todouble());
        assertEquals(0., userdatacls.todouble());
        assertEquals(0., somefunc.todouble());
        assertEquals(0., someclosure.todouble());
    }

    @Test
    void testToFloat() {
        assertEquals(0.f, somenil.tofloat());
        assertEquals(0.f, somefalse.tofloat());
        assertEquals(0.f, sometrue.tofloat());
        assertEquals(0.f, zero.tofloat());
        assertEquals(sampleint, intint.tofloat());
        assertEquals(samplelong, longdouble.tofloat());
        assertEquals((float) sampledouble, doubledouble.tofloat());
        assertEquals(0, stringstring.tofloat());
        assertEquals(sampleint, stringint.tofloat());
        assertEquals(samplelong, stringlong.tofloat());
        assertEquals((float) sampledouble, stringdouble.tofloat());
        assertEquals(0.f, thread.tofloat());
        assertEquals(0.f, table.tofloat());
        assertEquals(0.f, userdataobj.tofloat());
        assertEquals(0.f, userdatacls.tofloat());
        assertEquals(0.f, somefunc.tofloat());
        assertEquals(0.f, someclosure.tofloat());
    }

    @Test
    void testToInt() {
        assertEquals(0, somenil.toint());
        assertEquals(0, somefalse.toint());
        assertEquals(0, sometrue.toint());
        assertEquals(0, zero.toint());
        assertEquals(sampleint, intint.toint());
        assertEquals((int) samplelong, longdouble.toint());
        assertEquals((int) sampledouble, doubledouble.toint());
        assertEquals(0, stringstring.toint());
        assertEquals(sampleint, stringint.toint());
        assertEquals((int) samplelong, stringlong.toint());
        assertEquals((int) sampledouble, stringdouble.toint());
        assertEquals(0, thread.toint());
        assertEquals(0, table.toint());
        assertEquals(0, userdataobj.toint());
        assertEquals(0, userdatacls.toint());
        assertEquals(0, somefunc.toint());
        assertEquals(0, someclosure.toint());
    }

    @Test
    void testToLong() {
        assertEquals(0L, somenil.tolong());
        assertEquals(0L, somefalse.tolong());
        assertEquals(0L, sometrue.tolong());
        assertEquals(0L, zero.tolong());
        assertEquals(sampleint, intint.tolong());
        assertEquals(samplelong, longdouble.tolong());
        assertEquals((long) sampledouble, doubledouble.tolong());
        assertEquals(0, stringstring.tolong());
        assertEquals(sampleint, stringint.tolong());
        assertEquals(samplelong, stringlong.tolong());
        assertEquals((long) sampledouble, stringdouble.tolong());
        assertEquals(0L, thread.tolong());
        assertEquals(0L, table.tolong());
        assertEquals(0L, userdataobj.tolong());
        assertEquals(0L, userdatacls.tolong());
        assertEquals(0L, somefunc.tolong());
        assertEquals(0L, someclosure.tolong());
    }

    @Test
    void testToShort() {
        assertEquals((short) 0, somenil.toshort());
        assertEquals((short) 0, somefalse.toshort());
        assertEquals((short) 0, sometrue.toshort());
        assertEquals((short) 0, zero.toshort());
        assertEquals((short) sampleint, intint.toshort());
        assertEquals((short) samplelong, longdouble.toshort());
        assertEquals((short) sampledouble, doubledouble.toshort());
        assertEquals((short) 0, stringstring.toshort());
        assertEquals((short) sampleint, stringint.toshort());
        assertEquals((short) samplelong, stringlong.toshort());
        assertEquals((short) sampledouble, stringdouble.toshort());
        assertEquals((short) 0, thread.toshort());
        assertEquals((short) 0, table.toshort());
        assertEquals((short) 0, userdataobj.toshort());
        assertEquals((short) 0, userdatacls.toshort());
        assertEquals((short) 0, somefunc.toshort());
        assertEquals((short) 0, someclosure.toshort());
    }

    @Test
    void testToString() {
        assertEquals("nil", somenil.tojstring());
        assertEquals("false", somefalse.tojstring());
        assertEquals("true", sometrue.tojstring());
        assertEquals("0", zero.tojstring());
        assertEquals(String.valueOf(sampleint), intint.tojstring());
        assertEquals(String.valueOf(samplelong), longdouble.tojstring());
        assertEquals(String.valueOf(sampledouble), doubledouble.tojstring());
        assertEquals(samplestringstring, stringstring.tojstring());
        assertEquals(String.valueOf(sampleint), stringint.tojstring());
        assertEquals(String.valueOf(samplelong), stringlong.tojstring());
        assertEquals(String.valueOf(sampledouble), stringdouble.tojstring());
        assertEquals("thread: ", thread.tojstring().substring(0, 8));
        assertEquals("table: ", table.tojstring().substring(0, 7));
        assertEquals(sampleobject.toString(), userdataobj.tojstring());
        assertEquals(sampledata.toString(), userdatacls.tojstring());
        assertEquals("function: ", somefunc.tojstring().substring(0, 10));
        assertEquals("function: ", someclosure.tojstring().substring(0, 10));
    }

    @Test
    void testToUserdata() {
        assertNull(somenil.touserdata());
        assertNull(somefalse.touserdata());
        assertNull(sometrue.touserdata());
        assertNull(zero.touserdata());
        assertNull(intint.touserdata());
        assertNull(longdouble.touserdata());
        assertNull(doubledouble.touserdata());
        assertNull(stringstring.touserdata());
        assertNull(stringint.touserdata());
        assertNull(stringlong.touserdata());
        assertNull(stringdouble.touserdata());
        assertNull(thread.touserdata());
        assertNull(table.touserdata());
        assertEquals(sampleobject, userdataobj.touserdata());
        assertEquals(sampledata, userdatacls.touserdata());
        assertNull(somefunc.touserdata());
        assertNull(someclosure.touserdata());
    }

    // ===================== Optional argument conversion =======================

    private void throwsError(LuaValue obj, String method, Class<?> argtype, Object argument) {
        try {
            obj.getClass().getMethod(method, argtype).invoke(obj, argument);
        } catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof LuaError))
                fail("not a LuaError: " + e.getTargetException());
            return; // pass
        } catch (Exception e) {
            fail("bad exception: " + e);
        }
        fail("failed to throw LuaError as required");
    }

    @Test
    void testOptBoolean() {
        assertTrue(somenil.optboolean(true));
        assertFalse(somenil.optboolean(false));
        assertTrue(sometrue.optboolean(false));
        assertFalse(somefalse.optboolean(true));
        throwsError(zero, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(intint, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(longdouble, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(doubledouble, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(somefunc, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(someclosure, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(stringstring, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(stringint, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(stringlong, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(stringdouble, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(thread, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(table, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(userdataobj, "optboolean", boolean.class, Boolean.FALSE);
        throwsError(userdatacls, "optboolean", boolean.class, Boolean.FALSE);
    }

    @Test
    void testOptClosure() {
        assertEquals(someclosure, somenil.optclosure(someclosure));
        assertNull(somenil.optclosure(null));
        throwsError(sometrue, "optclosure", LuaClosure.class, someclosure);
        throwsError(somefalse, "optclosure", LuaClosure.class, someclosure);
        throwsError(zero, "optclosure", LuaClosure.class, someclosure);
        throwsError(intint, "optclosure", LuaClosure.class, someclosure);
        throwsError(longdouble, "optclosure", LuaClosure.class, someclosure);
        throwsError(doubledouble, "optclosure", LuaClosure.class, someclosure);
        throwsError(somefunc, "optclosure", LuaClosure.class, someclosure);
        assertEquals(someclosure, someclosure.optclosure(someclosure));
        assertEquals(someclosure, someclosure.optclosure(null));
        throwsError(stringstring, "optclosure", LuaClosure.class, someclosure);
        throwsError(stringint, "optclosure", LuaClosure.class, someclosure);
        throwsError(stringlong, "optclosure", LuaClosure.class, someclosure);
        throwsError(stringdouble, "optclosure", LuaClosure.class, someclosure);
        throwsError(thread, "optclosure", LuaClosure.class, someclosure);
        throwsError(table, "optclosure", LuaClosure.class, someclosure);
        throwsError(userdataobj, "optclosure", LuaClosure.class, someclosure);
        throwsError(userdatacls, "optclosure", LuaClosure.class, someclosure);
    }

    @Test
    void testOptDouble() {
        assertEquals(33., somenil.optdouble(33.));
        throwsError(sometrue, "optdouble", double.class, 33.);
        throwsError(somefalse, "optdouble", double.class, 33.);
        assertEquals(0., zero.optdouble(33.));
        assertEquals(sampleint, intint.optdouble(33.));
        assertEquals(samplelong, longdouble.optdouble(33.));
        assertEquals(sampledouble, doubledouble.optdouble(33.));
        throwsError(somefunc, "optdouble", double.class, 33.);
        throwsError(someclosure, "optdouble", double.class, 33.);
        throwsError(stringstring, "optdouble", double.class, 33.);
        assertEquals(sampleint, stringint.optdouble(33.));
        assertEquals(samplelong, stringlong.optdouble(33.));
        assertEquals(sampledouble, stringdouble.optdouble(33.));
        throwsError(thread, "optdouble", double.class, 33.);
        throwsError(table, "optdouble", double.class, 33.);
        throwsError(userdataobj, "optdouble", double.class, 33.);
        throwsError(userdatacls, "optdouble", double.class, 33.);
    }

    @Test
    void testOptFunction() {
        assertEquals(somefunc, somenil.optfunction(somefunc));
        assertNull(somenil.optfunction(null));
        throwsError(sometrue, "optfunction", LuaFunction.class, somefunc);
        throwsError(somefalse, "optfunction", LuaFunction.class, somefunc);
        throwsError(zero, "optfunction", LuaFunction.class, somefunc);
        throwsError(intint, "optfunction", LuaFunction.class, somefunc);
        throwsError(longdouble, "optfunction", LuaFunction.class, somefunc);
        throwsError(doubledouble, "optfunction", LuaFunction.class, somefunc);
        assertEquals(somefunc, somefunc.optfunction(null));
        assertEquals(someclosure, someclosure.optfunction(null));
        assertEquals(somefunc, somefunc.optfunction(somefunc));
        assertEquals(someclosure, someclosure.optfunction(somefunc));
        throwsError(stringstring, "optfunction", LuaFunction.class, somefunc);
        throwsError(stringint, "optfunction", LuaFunction.class, somefunc);
        throwsError(stringlong, "optfunction", LuaFunction.class, somefunc);
        throwsError(stringdouble, "optfunction", LuaFunction.class, somefunc);
        throwsError(thread, "optfunction", LuaFunction.class, somefunc);
        throwsError(table, "optfunction", LuaFunction.class, somefunc);
        throwsError(userdataobj, "optfunction", LuaFunction.class, somefunc);
        throwsError(userdatacls, "optfunction", LuaFunction.class, somefunc);
    }

    @Test
    void testOptInt() {
        assertEquals(33, somenil.optint(33));
        throwsError(sometrue, "optint", int.class, 33);
        throwsError(somefalse, "optint", int.class, 33);
        assertEquals(0, zero.optint(33));
        assertEquals(sampleint, intint.optint(33));
        assertEquals((int) samplelong, longdouble.optint(33));
        assertEquals((int) sampledouble, doubledouble.optint(33));
        throwsError(somefunc, "optint", int.class, 33);
        throwsError(someclosure, "optint", int.class, 33);
        throwsError(stringstring, "optint", int.class, 33);
        assertEquals(sampleint, stringint.optint(33));
        assertEquals((int) samplelong, stringlong.optint(33));
        assertEquals((int) sampledouble, stringdouble.optint(33));
        throwsError(thread, "optint", int.class, 33);
        throwsError(table, "optint", int.class, 33);
        throwsError(userdataobj, "optint", int.class, 33);
        throwsError(userdatacls, "optint", int.class, 33);
    }

    @Test
    void testOptInteger() {
        assertEquals(LuaValue.valueOf(33), somenil.optinteger(LuaValue.valueOf(33)));
        throwsError(sometrue, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(somefalse, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        assertEquals(zero, zero.optinteger(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(sampleint), intint.optinteger(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf((int) samplelong), longdouble.optinteger(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf((int) sampledouble), doubledouble.optinteger(LuaValue.valueOf(33)));
        throwsError(somefunc, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(someclosure, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(stringstring, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        assertEquals(LuaValue.valueOf(sampleint), stringint.optinteger(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf((int) samplelong), stringlong.optinteger(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf((int) sampledouble), stringdouble.optinteger(LuaValue.valueOf(33)));
        throwsError(thread, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(table, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(userdataobj, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
        throwsError(userdatacls, "optinteger", LuaInteger.class, LuaValue.valueOf(33));
    }

    @Test
    void testOptLong() {
        assertEquals(33L, somenil.optlong(33));
        throwsError(sometrue, "optlong", long.class, 33L);
        throwsError(somefalse, "optlong", long.class, 33L);
        assertEquals(0L, zero.optlong(33));
        assertEquals(sampleint, intint.optlong(33));
        assertEquals(samplelong, longdouble.optlong(33));
        assertEquals((long) sampledouble, doubledouble.optlong(33));
        throwsError(somefunc, "optlong", long.class, 33L);
        throwsError(someclosure, "optlong", long.class, 33L);
        throwsError(stringstring, "optlong", long.class, 33L);
        assertEquals(sampleint, stringint.optlong(33));
        assertEquals(samplelong, stringlong.optlong(33));
        assertEquals((long) sampledouble, stringdouble.optlong(33));
        throwsError(thread, "optlong", long.class, 33L);
        throwsError(table, "optlong", long.class, 33L);
        throwsError(userdataobj, "optlong", long.class, 33L);
        throwsError(userdatacls, "optlong", long.class, 33L);
    }

    @Test
    void testOptNumber() {
        assertEquals(LuaValue.valueOf(33), somenil.optnumber(LuaValue.valueOf(33)));
        throwsError(sometrue, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(somefalse, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        assertEquals(zero, zero.optnumber(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(sampleint), intint.optnumber(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(samplelong), longdouble.optnumber(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(sampledouble), doubledouble.optnumber(LuaValue.valueOf(33)));
        throwsError(somefunc, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(someclosure, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(stringstring, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        assertEquals(LuaValue.valueOf(sampleint), stringint.optnumber(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(samplelong), stringlong.optnumber(LuaValue.valueOf(33)));
        assertEquals(LuaValue.valueOf(sampledouble), stringdouble.optnumber(LuaValue.valueOf(33)));
        throwsError(thread, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(table, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(userdataobj, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
        throwsError(userdatacls, "optnumber", LuaNumber.class, LuaValue.valueOf(33));
    }

    @Test
    void testOptTable() {
        assertEquals(table, somenil.opttable(table));
        assertNull(somenil.opttable(null));
        throwsError(sometrue, "opttable", LuaTable.class, table);
        throwsError(somefalse, "opttable", LuaTable.class, table);
        throwsError(zero, "opttable", LuaTable.class, table);
        throwsError(intint, "opttable", LuaTable.class, table);
        throwsError(longdouble, "opttable", LuaTable.class, table);
        throwsError(doubledouble, "opttable", LuaTable.class, table);
        throwsError(somefunc, "opttable", LuaTable.class, table);
        throwsError(someclosure, "opttable", LuaTable.class, table);
        throwsError(stringstring, "opttable", LuaTable.class, table);
        throwsError(stringint, "opttable", LuaTable.class, table);
        throwsError(stringlong, "opttable", LuaTable.class, table);
        throwsError(stringdouble, "opttable", LuaTable.class, table);
        throwsError(thread, "opttable", LuaTable.class, table);
        assertEquals(table, table.opttable(table));
        assertEquals(table, table.opttable(null));
        throwsError(userdataobj, "opttable", LuaTable.class, table);
        throwsError(userdatacls, "opttable", LuaTable.class, table);
    }

    @Test
    void testOptThread() {
        assertEquals(thread, somenil.optthread(thread));
        assertNull(somenil.optthread(null));
        throwsError(sometrue, "optthread", LuaThread.class, thread);
        throwsError(somefalse, "optthread", LuaThread.class, thread);
        throwsError(zero, "optthread", LuaThread.class, thread);
        throwsError(intint, "optthread", LuaThread.class, thread);
        throwsError(longdouble, "optthread", LuaThread.class, thread);
        throwsError(doubledouble, "optthread", LuaThread.class, thread);
        throwsError(somefunc, "optthread", LuaThread.class, thread);
        throwsError(someclosure, "optthread", LuaThread.class, thread);
        throwsError(stringstring, "optthread", LuaThread.class, thread);
        throwsError(stringint, "optthread", LuaThread.class, thread);
        throwsError(stringlong, "optthread", LuaThread.class, thread);
        throwsError(stringdouble, "optthread", LuaThread.class, thread);
        throwsError(table, "optthread", LuaThread.class, thread);
        assertEquals(thread, thread.optthread(thread));
        assertEquals(thread, thread.optthread(null));
        throwsError(userdataobj, "optthread", LuaThread.class, thread);
        throwsError(userdatacls, "optthread", LuaThread.class, thread);
    }

    @Test
    void testOptJavaString() {
        assertEquals("xyz", somenil.optjstring("xyz"));
        assertNull(somenil.optjstring(null));
        throwsError(sometrue, "optjstring", String.class, "xyz");
        throwsError(somefalse, "optjstring", String.class, "xyz");
        assertEquals(String.valueOf(zero), zero.optjstring("xyz"));
        assertEquals(String.valueOf(intint), intint.optjstring("xyz"));
        assertEquals(String.valueOf(longdouble), longdouble.optjstring("xyz"));
        assertEquals(String.valueOf(doubledouble), doubledouble.optjstring("xyz"));
        throwsError(somefunc, "optjstring", String.class, "xyz");
        throwsError(someclosure, "optjstring", String.class, "xyz");
        assertEquals(samplestringstring, stringstring.optjstring("xyz"));
        assertEquals(samplestringint, stringint.optjstring("xyz"));
        assertEquals(samplestringlong, stringlong.optjstring("xyz"));
        assertEquals(samplestringdouble, stringdouble.optjstring("xyz"));
        throwsError(thread, "optjstring", String.class, "xyz");
        throwsError(table, "optjstring", String.class, "xyz");
        throwsError(userdataobj, "optjstring", String.class, "xyz");
        throwsError(userdatacls, "optjstring", String.class, "xyz");
    }

    @Test
    void testOptLuaString() {
        assertEquals(LuaValue.valueOf("xyz"), somenil.optstring(LuaValue.valueOf("xyz")));
        assertNull(somenil.optstring(null));
        throwsError(sometrue, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        throwsError(somefalse, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        assertEquals(LuaValue.valueOf("0"), zero.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringint, intint.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringlong, longdouble.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringdouble, doubledouble.optstring(LuaValue.valueOf("xyz")));
        throwsError(somefunc, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        throwsError(someclosure, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        assertEquals(stringstring, stringstring.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringint, stringint.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringlong, stringlong.optstring(LuaValue.valueOf("xyz")));
        assertEquals(stringdouble, stringdouble.optstring(LuaValue.valueOf("xyz")));
        throwsError(thread, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        throwsError(table, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        throwsError(userdataobj, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
        throwsError(userdatacls, "optstring", LuaString.class, LuaValue.valueOf("xyz"));
    }

    @Test
    void testOptUserdata() {
        assertEquals(sampleobject, somenil.optuserdata(sampleobject));
        assertEquals(sampledata, somenil.optuserdata(sampledata));
        assertNull(somenil.optuserdata(null));
        throwsError(sometrue, "optuserdata", Object.class, sampledata);
        throwsError(somefalse, "optuserdata", Object.class, sampledata);
        throwsError(zero, "optuserdata", Object.class, sampledata);
        throwsError(intint, "optuserdata", Object.class, sampledata);
        throwsError(longdouble, "optuserdata", Object.class, sampledata);
        throwsError(doubledouble, "optuserdata", Object.class, sampledata);
        throwsError(somefunc, "optuserdata", Object.class, sampledata);
        throwsError(someclosure, "optuserdata", Object.class, sampledata);
        throwsError(stringstring, "optuserdata", Object.class, sampledata);
        throwsError(stringint, "optuserdata", Object.class, sampledata);
        throwsError(stringlong, "optuserdata", Object.class, sampledata);
        throwsError(stringdouble, "optuserdata", Object.class, sampledata);
        throwsError(table, "optuserdata", Object.class, sampledata);
        assertEquals(sampleobject, userdataobj.optuserdata(sampledata));
        assertEquals(sampleobject, userdataobj.optuserdata(null));
        assertEquals(sampledata, userdatacls.optuserdata(sampleobject));
        assertEquals(sampledata, userdatacls.optuserdata(null));
    }

    private void throwsErrorOptUserdataClass(LuaValue obj, Class<?> arg1, Object arg2) {
        try {
            obj.getClass().getMethod("optuserdata", Class.class, Object.class).invoke(obj, arg1, arg2);
        } catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof LuaError))
                fail("not a LuaError: " + e.getTargetException());
            return; // pass
        } catch (Exception e) {
            fail("bad exception: " + e);
        }
        fail("failed to throw LuaError as required");
    }

    @Test
    void testOptUserdataClass() {
        assertEquals(sampledata, somenil.optuserdata(MyData.class, sampledata));
        assertEquals(sampleobject, somenil.optuserdata(Object.class, sampleobject));
        assertNull(somenil.optuserdata(null));
        throwsErrorOptUserdataClass(sometrue, Object.class, sampledata);
        throwsErrorOptUserdataClass(zero, MyData.class, sampledata);
        throwsErrorOptUserdataClass(intint, MyData.class, sampledata);
        throwsErrorOptUserdataClass(longdouble, MyData.class, sampledata);
        throwsErrorOptUserdataClass(somefunc, MyData.class, sampledata);
        throwsErrorOptUserdataClass(someclosure, MyData.class, sampledata);
        throwsErrorOptUserdataClass(stringstring, MyData.class, sampledata);
        throwsErrorOptUserdataClass(stringint, MyData.class, sampledata);
        throwsErrorOptUserdataClass(stringlong, MyData.class, sampledata);
        throwsErrorOptUserdataClass(stringlong, MyData.class, sampledata);
        throwsErrorOptUserdataClass(stringdouble, MyData.class, sampledata);
        throwsErrorOptUserdataClass(table, MyData.class, sampledata);
        throwsErrorOptUserdataClass(thread, MyData.class, sampledata);
        assertEquals(sampleobject, userdataobj.optuserdata(Object.class, sampleobject));
        assertEquals(sampleobject, userdataobj.optuserdata(null));
        assertEquals(sampledata, userdatacls.optuserdata(MyData.class, sampledata));
        assertEquals(sampledata, userdatacls.optuserdata(Object.class, sampleobject));
        assertEquals(sampledata, userdatacls.optuserdata(null));
        // should fail due to wrong class
        try {
            Object o = userdataobj.optuserdata(MyData.class, sampledata);
            fail("did not throw bad type error");
            assertInstanceOf(MyData.class, o);
        } catch (LuaError le) {
            assertEquals("org.luaj.vm2.TypeTest$MyData expected, got userdata", le.getMessage());
        }
    }

    @Test
    void testOptValue() {
        assertEquals(zero, somenil.optvalue(zero));
        assertEquals(stringstring, somenil.optvalue(stringstring));
        assertEquals(sometrue, sometrue.optvalue(LuaConstant.TRUE));
        assertEquals(somefalse, somefalse.optvalue(LuaConstant.TRUE));
        assertEquals(zero, zero.optvalue(LuaConstant.TRUE));
        assertEquals(intint, intint.optvalue(LuaConstant.TRUE));
        assertEquals(longdouble, longdouble.optvalue(LuaConstant.TRUE));
        assertEquals(somefunc, somefunc.optvalue(LuaConstant.TRUE));
        assertEquals(someclosure, someclosure.optvalue(LuaConstant.TRUE));
        assertEquals(stringstring, stringstring.optvalue(LuaConstant.TRUE));
        assertEquals(stringint, stringint.optvalue(LuaConstant.TRUE));
        assertEquals(stringlong, stringlong.optvalue(LuaConstant.TRUE));
        assertEquals(stringdouble, stringdouble.optvalue(LuaConstant.TRUE));
        assertEquals(thread, thread.optvalue(LuaConstant.TRUE));
        assertEquals(table, table.optvalue(LuaConstant.TRUE));
        assertEquals(userdataobj, userdataobj.optvalue(LuaConstant.TRUE));
        assertEquals(userdatacls, userdatacls.optvalue(LuaConstant.TRUE));
    }

    // ===================== Required argument conversion =======================

    private void throwsErrorReq(LuaValue obj, String method) {
        try {
            obj.getClass().getMethod(method).invoke(obj);
        } catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof LuaError))
                fail("not a LuaError: " + e.getTargetException());
            return; // pass
        } catch (Exception e) {
            fail("bad exception: " + e);
        }
        fail("failed to throw LuaError as required");
    }

    @Test
    void testCheckBoolean() {
        throwsErrorReq(somenil, "checkboolean");
        assertTrue(sometrue.checkboolean());
        assertFalse(somefalse.checkboolean());
        throwsErrorReq(zero, "checkboolean");
        throwsErrorReq(intint, "checkboolean");
        throwsErrorReq(longdouble, "checkboolean");
        throwsErrorReq(doubledouble, "checkboolean");
        throwsErrorReq(somefunc, "checkboolean");
        throwsErrorReq(someclosure, "checkboolean");
        throwsErrorReq(stringstring, "checkboolean");
        throwsErrorReq(stringint, "checkboolean");
        throwsErrorReq(stringlong, "checkboolean");
        throwsErrorReq(stringdouble, "checkboolean");
        throwsErrorReq(thread, "checkboolean");
        throwsErrorReq(table, "checkboolean");
        throwsErrorReq(userdataobj, "checkboolean");
        throwsErrorReq(userdatacls, "checkboolean");
    }

    @Test
    void testCheckClosure() {
        throwsErrorReq(somenil, "checkclosure");
        throwsErrorReq(sometrue, "checkclosure");
        throwsErrorReq(somefalse, "checkclosure");
        throwsErrorReq(zero, "checkclosure");
        throwsErrorReq(intint, "checkclosure");
        throwsErrorReq(longdouble, "checkclosure");
        throwsErrorReq(doubledouble, "checkclosure");
        throwsErrorReq(somefunc, "checkclosure");
        assertEquals(someclosure, someclosure.checkclosure());
        assertEquals(someclosure, someclosure.checkclosure());
        throwsErrorReq(stringstring, "checkclosure");
        throwsErrorReq(stringint, "checkclosure");
        throwsErrorReq(stringlong, "checkclosure");
        throwsErrorReq(stringdouble, "checkclosure");
        throwsErrorReq(thread, "checkclosure");
        throwsErrorReq(table, "checkclosure");
        throwsErrorReq(userdataobj, "checkclosure");
        throwsErrorReq(userdatacls, "checkclosure");
    }

    @Test
    void testCheckDouble() {
        throwsErrorReq(somenil, "checkdouble");
        throwsErrorReq(sometrue, "checkdouble");
        throwsErrorReq(somefalse, "checkdouble");
        assertEquals(0., zero.checkdouble());
        assertEquals(sampleint, intint.checkdouble());
        assertEquals(samplelong, longdouble.checkdouble());
        assertEquals(sampledouble, doubledouble.checkdouble());
        throwsErrorReq(somefunc, "checkdouble");
        throwsErrorReq(someclosure, "checkdouble");
        throwsErrorReq(stringstring, "checkdouble");
        assertEquals(sampleint, stringint.checkdouble());
        assertEquals(samplelong, stringlong.checkdouble());
        assertEquals(sampledouble, stringdouble.checkdouble());
        throwsErrorReq(thread, "checkdouble");
        throwsErrorReq(table, "checkdouble");
        throwsErrorReq(userdataobj, "checkdouble");
        throwsErrorReq(userdatacls, "checkdouble");
    }

    @Test
    void testCheckFunction() {
        throwsErrorReq(somenil, "checkfunction");
        throwsErrorReq(sometrue, "checkfunction");
        throwsErrorReq(somefalse, "checkfunction");
        throwsErrorReq(zero, "checkfunction");
        throwsErrorReq(intint, "checkfunction");
        throwsErrorReq(longdouble, "checkfunction");
        throwsErrorReq(doubledouble, "checkfunction");
        assertEquals(somefunc, somefunc.checkfunction());
        assertEquals(someclosure, someclosure.checkfunction());
        assertEquals(somefunc, somefunc.checkfunction());
        assertEquals(someclosure, someclosure.checkfunction());
        throwsErrorReq(stringstring, "checkfunction");
        throwsErrorReq(stringint, "checkfunction");
        throwsErrorReq(stringlong, "checkfunction");
        throwsErrorReq(stringdouble, "checkfunction");
        throwsErrorReq(thread, "checkfunction");
        throwsErrorReq(table, "checkfunction");
        throwsErrorReq(userdataobj, "checkfunction");
        throwsErrorReq(userdatacls, "checkfunction");
    }

    @Test
    void testCheckInt() {
        throwsErrorReq(somenil, "checkint");
        throwsErrorReq(sometrue, "checkint");
        throwsErrorReq(somefalse, "checkint");
        assertEquals(0, zero.checkint());
        assertEquals(sampleint, intint.checkint());
        assertEquals((int) samplelong, longdouble.checkint());
        assertEquals((int) sampledouble, doubledouble.checkint());
        throwsErrorReq(somefunc, "checkint");
        throwsErrorReq(someclosure, "checkint");
        throwsErrorReq(stringstring, "checkint");
        assertEquals(sampleint, stringint.checkint());
        assertEquals((int) samplelong, stringlong.checkint());
        assertEquals((int) sampledouble, stringdouble.checkint());
        throwsErrorReq(thread, "checkint");
        throwsErrorReq(table, "checkint");
        throwsErrorReq(userdataobj, "checkint");
        throwsErrorReq(userdatacls, "checkint");
    }

    @Test
    void testCheckInteger() {
        throwsErrorReq(somenil, "checkinteger");
        throwsErrorReq(sometrue, "checkinteger");
        throwsErrorReq(somefalse, "checkinteger");
        assertEquals(zero, zero.checkinteger());
        assertEquals(LuaValue.valueOf(sampleint), intint.checkinteger());
        assertEquals(LuaValue.valueOf((int) samplelong), longdouble.checkinteger());
        assertEquals(LuaValue.valueOf((int) sampledouble), doubledouble.checkinteger());
        throwsErrorReq(somefunc, "checkinteger");
        throwsErrorReq(someclosure, "checkinteger");
        throwsErrorReq(stringstring, "checkinteger");
        assertEquals(LuaValue.valueOf(sampleint), stringint.checkinteger());
        assertEquals(LuaValue.valueOf((int) samplelong), stringlong.checkinteger());
        assertEquals(LuaValue.valueOf((int) sampledouble), stringdouble.checkinteger());
        throwsErrorReq(thread, "checkinteger");
        throwsErrorReq(table, "checkinteger");
        throwsErrorReq(userdataobj, "checkinteger");
        throwsErrorReq(userdatacls, "checkinteger");
    }

    @Test
    void testCheckLong() {
        throwsErrorReq(somenil, "checklong");
        throwsErrorReq(sometrue, "checklong");
        throwsErrorReq(somefalse, "checklong");
        assertEquals(0L, zero.checklong());
        assertEquals(sampleint, intint.checklong());
        assertEquals(samplelong, longdouble.checklong());
        assertEquals((long) sampledouble, doubledouble.checklong());
        throwsErrorReq(somefunc, "checklong");
        throwsErrorReq(someclosure, "checklong");
        throwsErrorReq(stringstring, "checklong");
        assertEquals(sampleint, stringint.checklong());
        assertEquals(samplelong, stringlong.checklong());
        assertEquals((long) sampledouble, stringdouble.checklong());
        throwsErrorReq(thread, "checklong");
        throwsErrorReq(table, "checklong");
        throwsErrorReq(userdataobj, "checklong");
        throwsErrorReq(userdatacls, "checklong");
    }

    @Test
    void testCheckNumber() {
        throwsErrorReq(somenil, "checknumber");
        throwsErrorReq(sometrue, "checknumber");
        throwsErrorReq(somefalse, "checknumber");
        assertEquals(zero, zero.checknumber());
        assertEquals(LuaValue.valueOf(sampleint), intint.checknumber());
        assertEquals(LuaValue.valueOf(samplelong), longdouble.checknumber());
        assertEquals(LuaValue.valueOf(sampledouble), doubledouble.checknumber());
        throwsErrorReq(somefunc, "checknumber");
        throwsErrorReq(someclosure, "checknumber");
        throwsErrorReq(stringstring, "checknumber");
        assertEquals(LuaValue.valueOf(sampleint), stringint.checknumber());
        assertEquals(LuaValue.valueOf(samplelong), stringlong.checknumber());
        assertEquals(LuaValue.valueOf(sampledouble), stringdouble.checknumber());
        throwsErrorReq(thread, "checknumber");
        throwsErrorReq(table, "checknumber");
        throwsErrorReq(userdataobj, "checknumber");
        throwsErrorReq(userdatacls, "checknumber");
    }

    @Test
    void testCheckTable() {
        throwsErrorReq(somenil, "checktable");
        throwsErrorReq(sometrue, "checktable");
        throwsErrorReq(somefalse, "checktable");
        throwsErrorReq(zero, "checktable");
        throwsErrorReq(intint, "checktable");
        throwsErrorReq(longdouble, "checktable");
        throwsErrorReq(doubledouble, "checktable");
        throwsErrorReq(somefunc, "checktable");
        throwsErrorReq(someclosure, "checktable");
        throwsErrorReq(stringstring, "checktable");
        throwsErrorReq(stringint, "checktable");
        throwsErrorReq(stringlong, "checktable");
        throwsErrorReq(stringdouble, "checktable");
        throwsErrorReq(thread, "checktable");
        assertEquals(table, table.checktable());
        assertEquals(table, table.checktable());
        throwsErrorReq(userdataobj, "checktable");
        throwsErrorReq(userdatacls, "checktable");
    }

    @Test
    void testCheckThread() {
        throwsErrorReq(somenil, "checkthread");
        throwsErrorReq(sometrue, "checkthread");
        throwsErrorReq(somefalse, "checkthread");
        throwsErrorReq(zero, "checkthread");
        throwsErrorReq(intint, "checkthread");
        throwsErrorReq(longdouble, "checkthread");
        throwsErrorReq(doubledouble, "checkthread");
        throwsErrorReq(somefunc, "checkthread");
        throwsErrorReq(someclosure, "checkthread");
        throwsErrorReq(stringstring, "checkthread");
        throwsErrorReq(stringint, "checkthread");
        throwsErrorReq(stringlong, "checkthread");
        throwsErrorReq(stringdouble, "checkthread");
        throwsErrorReq(table, "checkthread");
        assertEquals(thread, thread.checkthread());
        assertEquals(thread, thread.checkthread());
        throwsErrorReq(userdataobj, "checkthread");
        throwsErrorReq(userdatacls, "checkthread");
    }

    @Test
    void testCheckJavaString() {
        throwsErrorReq(somenil, "checkjstring");
        throwsErrorReq(sometrue, "checkjstring");
        throwsErrorReq(somefalse, "checkjstring");
        assertEquals(String.valueOf(zero), zero.checkjstring());
        assertEquals(String.valueOf(intint), intint.checkjstring());
        assertEquals(String.valueOf(longdouble), longdouble.checkjstring());
        assertEquals(String.valueOf(doubledouble), doubledouble.checkjstring());
        throwsErrorReq(somefunc, "checkjstring");
        throwsErrorReq(someclosure, "checkjstring");
        assertEquals(samplestringstring, stringstring.checkjstring());
        assertEquals(samplestringint, stringint.checkjstring());
        assertEquals(samplestringlong, stringlong.checkjstring());
        assertEquals(samplestringdouble, stringdouble.checkjstring());
        throwsErrorReq(thread, "checkjstring");
        throwsErrorReq(table, "checkjstring");
        throwsErrorReq(userdataobj, "checkjstring");
        throwsErrorReq(userdatacls, "checkjstring");
    }

    @Test
    void testCheckLuaString() {
        throwsErrorReq(somenil, "checkstring");
        throwsErrorReq(sometrue, "checkstring");
        throwsErrorReq(somefalse, "checkstring");
        assertEquals(LuaValue.valueOf("0"), zero.checkstring());
        assertEquals(stringint, intint.checkstring());
        assertEquals(stringlong, longdouble.checkstring());
        assertEquals(stringdouble, doubledouble.checkstring());
        throwsErrorReq(somefunc, "checkstring");
        throwsErrorReq(someclosure, "checkstring");
        assertEquals(stringstring, stringstring.checkstring());
        assertEquals(stringint, stringint.checkstring());
        assertEquals(stringlong, stringlong.checkstring());
        assertEquals(stringdouble, stringdouble.checkstring());
        throwsErrorReq(thread, "checkstring");
        throwsErrorReq(table, "checkstring");
        throwsErrorReq(userdataobj, "checkstring");
        throwsErrorReq(userdatacls, "checkstring");
    }

    @Test
    void testCheckUserdata() {
        throwsErrorReq(somenil, "checkuserdata");
        throwsErrorReq(sometrue, "checkuserdata");
        throwsErrorReq(somefalse, "checkuserdata");
        throwsErrorReq(zero, "checkuserdata");
        throwsErrorReq(intint, "checkuserdata");
        throwsErrorReq(longdouble, "checkuserdata");
        throwsErrorReq(doubledouble, "checkuserdata");
        throwsErrorReq(somefunc, "checkuserdata");
        throwsErrorReq(someclosure, "checkuserdata");
        throwsErrorReq(stringstring, "checkuserdata");
        throwsErrorReq(stringint, "checkuserdata");
        throwsErrorReq(stringlong, "checkuserdata");
        throwsErrorReq(stringdouble, "checkuserdata");
        throwsErrorReq(table, "checkuserdata");
        assertEquals(sampleobject, userdataobj.checkuserdata());
        assertEquals(sampleobject, userdataobj.checkuserdata());
        assertEquals(sampledata, userdatacls.checkuserdata());
        assertEquals(sampledata, userdatacls.checkuserdata());
    }

    private void throwsErrorReqCheckUserdataClass(LuaValue obj, Class<?> arg) {
        try {
            obj.getClass().getMethod("checkuserdata", Class.class).invoke(obj, arg);
        } catch (InvocationTargetException e) {
            if (!(e.getTargetException() instanceof LuaError))
                fail("not a LuaError: " + e.getTargetException());
            return; // pass
        } catch (Exception e) {
            fail("bad exception: " + e);
        }
        fail("failed to throw LuaError as required");
    }

    @Test
    void testCheckUserdataClass() {
        throwsErrorReqCheckUserdataClass(somenil, Object.class);
        throwsErrorReqCheckUserdataClass(somenil, MyData.class);
        throwsErrorReqCheckUserdataClass(sometrue, Object.class);
        throwsErrorReqCheckUserdataClass(zero, MyData.class);
        throwsErrorReqCheckUserdataClass(intint, MyData.class);
        throwsErrorReqCheckUserdataClass(longdouble, MyData.class);
        throwsErrorReqCheckUserdataClass(somefunc, MyData.class);
        throwsErrorReqCheckUserdataClass(someclosure, MyData.class);
        throwsErrorReqCheckUserdataClass(stringstring, MyData.class);
        throwsErrorReqCheckUserdataClass(stringint, MyData.class);
        throwsErrorReqCheckUserdataClass(stringlong, MyData.class);
        throwsErrorReqCheckUserdataClass(stringlong, MyData.class);
        throwsErrorReqCheckUserdataClass(stringdouble, MyData.class);
        throwsErrorReqCheckUserdataClass(table, MyData.class);
        throwsErrorReqCheckUserdataClass(thread, MyData.class);
        assertEquals(sampleobject, userdataobj.checkuserdata(Object.class));
        assertEquals(sampleobject, userdataobj.checkuserdata());
        assertEquals(sampledata, userdatacls.checkuserdata(MyData.class));
        assertEquals(sampledata, userdatacls.checkuserdata(Object.class));
        assertEquals(sampledata, userdatacls.checkuserdata());
        // should fail due to wrong class
        try {
            Object o = userdataobj.checkuserdata(MyData.class);
            fail("did not throw bad type error");
            assertInstanceOf(MyData.class, o);
        } catch (LuaError le) {
            assertEquals("org.luaj.vm2.TypeTest$MyData expected, got userdata", le.getMessage());
        }
    }

    @Test
    void testCheckValue() {
        throwsErrorReq(somenil, "checknotnil");
        assertEquals(sometrue, sometrue.checknotnil());
        assertEquals(somefalse, somefalse.checknotnil());
        assertEquals(zero, zero.checknotnil());
        assertEquals(intint, intint.checknotnil());
        assertEquals(longdouble, longdouble.checknotnil());
        assertEquals(somefunc, somefunc.checknotnil());
        assertEquals(someclosure, someclosure.checknotnil());
        assertEquals(stringstring, stringstring.checknotnil());
        assertEquals(stringint, stringint.checknotnil());
        assertEquals(stringlong, stringlong.checknotnil());
        assertEquals(stringdouble, stringdouble.checknotnil());
        assertEquals(thread, thread.checknotnil());
        assertEquals(table, table.checknotnil());
        assertEquals(userdataobj, userdataobj.checknotnil());
        assertEquals(userdatacls, userdatacls.checknotnil());
    }

}