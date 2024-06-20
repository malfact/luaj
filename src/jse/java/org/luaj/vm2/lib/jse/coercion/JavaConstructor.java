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

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * LuaValue that represents a particular public Java constructor.
 * <p>
 * May be called with arguments to return a JavaInstance
 * created by calling the constructor.
 * <p>
 * This class is not used directly.
 * It is returned by calls to {@code JavaClass#new(LuaValue key)}
 * when the value of key is "new".
 *
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
class JavaConstructor extends JavaMember {

    static final Map<Executable, JavaConstructor> constructors = Collections.synchronizedMap(new HashMap<>());

    static JavaConstructor forConstructor(Constructor<?> constructor) {
        JavaConstructor javaConstructor = constructors.get(constructor);
        if (javaConstructor == null)
            constructors.put(constructor, javaConstructor = new JavaConstructor(constructor));
        return javaConstructor;
    }

    // Allows for "fake" constructors
    static JavaConstructor forMethod(Method method) {
        JavaConstructor javaConstructor = constructors.get(method);
        if (javaConstructor == null)
            constructors.put(method, javaConstructor = new JavaConstructor(method));

        return javaConstructor;
    }

    public static LuaValue forConstructors(JavaConstructor[] array) {
        return new Overload(array);
    }

    final Executable constructor;

    private JavaConstructor(Executable c) {
        super(c.getParameterTypes(), c.getModifiers());
        this.constructor = c;
    }

    public Varargs invoke(Varargs args) {
        Object[] a = convertArgs(args);
        try {
            if (constructor instanceof Constructor<?> c)
                return CoerceJavaToLua.coerce(c.newInstance(a));
            else
                return CoerceJavaToLua.coerce(((Method) constructor).invoke(null, a));
        } catch (InvocationTargetException e) {
            throw new LuaError(e.getTargetException());
        } catch (Exception e) {
            return LuaValue.error("coercion error " + e);
        }
    }

    /**
     * LuaValue that represents an overloaded Java constructor.
     * <p>
     * On invocation, will pick the best method from the list, and invoke it.
     * <p>
     * This class is not used directly.
     * It is returned by calls to calls to {@link JavaClass#get(LuaValue key)}
     * when key is "new" and there is more than one public constructor.
     */
    static class Overload extends VarArgFunction {
        final JavaConstructor[] constructors;

        public Overload(JavaConstructor[] c) {
            this.constructors = c;
        }

        public Varargs invoke(Varargs args) {
            JavaConstructor best = null;
            int score = CoerceLuaToJava.SCORE_UNCOERCIBLE;
            for (JavaConstructor javaConstructor : constructors) {
                int s = javaConstructor.score(args);
                if (s < score) {
                    score = s;
                    best = javaConstructor;
                    if (score == 0)
                        break;
                }
            }

            // any match?
            if (best == null)
                LuaValue.error("no coercible public method");

            // invoke it
            return best.invoke(args);
        }
    }
}
