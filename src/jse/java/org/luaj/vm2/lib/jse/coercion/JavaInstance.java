/*******************************************************************************
 * Copyright (c) 2011 Luaj.org. All rights reserved.
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
package org.luaj.vm2.lib.jse.coercion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

/**
 * LuaValue that represents a Java instance.
 * <p>
 * Will respond to get() and set() by returning field values or methods.
 * <p>
 * This class is not used directly.
 * It is returned by calls to {@link CoerceJavaToLua#coerce(Object)}
 * when a subclass of Object is supplied.
 *
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
class JavaInstance extends LuaUserdata {

    JavaClass jclass;

    JavaInstance(Object instance) {
        super(instance);
    }

    public LuaValue get(LuaValue key) {
        if (jclass == null)
            jclass = JavaClass.forClass(m_instance.getClass());

        Field field = jclass.getField(key);
        if (field != null)
            try {
                return CoerceJavaToLua.coerce(field.get(m_instance));
            } catch (Exception e) {
                throw new LuaError(e);
            }
        LuaValue method = jclass.getMethod(key);
        if (method != null)
            return method;

        Class<?> innerClass = jclass.getInnerClass(key);
        if (innerClass != null)
            return JavaClass.forClass(innerClass);

        if (jclass.uservalues != null) {
            return CoerceJavaToLua.coerce(jclass.uservalues.get(CoerceLuaToJava.coerce(key, Object.class)));
        }

        return super.get(key);
    }

    public void set(LuaValue key, LuaValue value) {
        if (jclass == null)
            jclass = JavaClass.forClass(m_instance.getClass());
        Field field = jclass.getField(key);
        if (field != null) {
            if (Modifier.isFinal(field.getModifiers()))
                throw new LuaError("Field <" + key + "> is Final.");

            try {
                field.set(m_instance, CoerceLuaToJava.coerce(value, field.getType()));
                return;
            } catch (Exception e) {
                throw new LuaError(e);
            }
        }

        if (jclass.uservalues != null) {
            jclass.uservalues.set(
                CoerceLuaToJava.coerce(key, Object.class),
                CoerceLuaToJava.coerce(value, Object.class)
            );
            return;
        }

        super.set(key, value);
    }

}
