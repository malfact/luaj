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


@SuppressWarnings("unused")
public class LuaUserdata extends LuaValue {

    public Object m_instance;
    public LuaValue m_metatable;

    public LuaUserdata(Object obj) {
        m_instance = obj;
    }

    public LuaUserdata(Object obj, LuaValue metatable) {
        m_instance = obj;
        m_metatable = metatable;
    }

    public String tojstring() {
        return String.valueOf(m_instance);
    }

    @Override
    public LuaType getType() {
        return LuaType.USERDATA;
    }

    public int hashCode() {
        return m_instance.hashCode();
    }

    public Object userdata() {
        return m_instance;
    }

    public boolean isuserdata() {
        return true;
    }

    public boolean isuserdata(Class<?> c) {
        return c.isAssignableFrom(m_instance.getClass());
    }

    public Object touserdata() {
        return m_instance;
    }

    public <T> T touserdata(Class<T> c) {
        if (c.isInstance(m_instance))
            return c.cast(m_instance);
        return null;
    }

    public Object optuserdata(Object defval) {
        return m_instance;
    }

    // isn't this just the same as checkuserdata?
    // Ideally I believe this should return the
    // defval instead of erroring, but that
    // change needs testing
    public <T> T optuserdata(Class<T> c, T defval) {
        if (c.isInstance(m_instance))
            return c.cast(m_instance);

        typeError(c.getName());
        return null;
    }

    public LuaValue getmetatable() {
        return m_metatable;
    }

    public LuaValue setmetatable(LuaValue metatable) {
        this.m_metatable = metatable;
        return this;
    }

    public Object checkuserdata() {
        return m_instance;
    }

    public <T> T checkuserdata(Class<T> c) {
        if (c.isInstance(m_instance))
            return c.cast(m_instance);

        typeError(c.getName());

        return null;
    }

    public LuaValue get(LuaValue key) {
        return m_metatable != null ? gettable(this, key) : LuaConstant.NIL;
    }

    public void set(LuaValue key, LuaValue value) {
        if (m_metatable == null || !settable(this, key, value))
            error("cannot set " + key + " for userdata");
    }

    public boolean equals(Object val) {
        if (this == val)
            return true;
        if (!(val instanceof LuaUserdata u))
            return false;
        return m_instance.equals(u.m_instance);
    }

    // equality w/ metatable processing
    public LuaValue eq(LuaValue val) {
        return eq_b(val) ? LuaConstant.TRUE : LuaConstant.FALSE;
    }

    public boolean eq_b(LuaValue val) {
        if (val.raweq(this)) return true;
        if (m_metatable == null || !val.isuserdata()) return false;
        LuaValue valmt = val.getmetatable();
        return valmt != null && eqmtcall(this, m_metatable, val, valmt);
    }

    // equality w/o metatable processing
    public boolean raweq(LuaValue val) {
        return val.raweq(this);
    }

    public boolean raweq(LuaUserdata val) {
        return this == val || (m_metatable == val.m_metatable && m_instance.equals(val.m_instance));
    }

    // __eq metatag processing
    public boolean eqmt(LuaValue val) {
        return m_metatable != null && val.isuserdata() && eqmtcall(this, m_metatable, val, val.getmetatable());
    }
}
