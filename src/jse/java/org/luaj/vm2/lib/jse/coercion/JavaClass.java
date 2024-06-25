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

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.luaj.vm2.LuaValue;

/**
 * LuaValue that represents a Java class.
 * <p>
 * Will respond to get() and set() by returning field values, or java methods.
 * <p>
 * This class is not used directly.
 * It is returned by calls to {@link CoerceJavaToLua#coerce(Object)}
 * when a Class is supplied.
 *
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
public class JavaClass extends JavaInstance implements CoerceJavaToLua.Coercion {

    static final Map<Class<?>, JavaClass> classes = Collections.synchronizedMap(new HashMap<>());

    static final LuaValue NEW = valueOf("new");

    Map<LuaValue, Field> fields;
    Map<LuaValue, LuaValue> methods;
    Map<LuaValue, Class<?>> innerclasses;

    Field uservalues;

    public static JavaClass forClass(Class<?> c) {
        JavaClass j = classes.get(c);
        if (j == null)
            classes.put(c, j = new JavaClass(c));

        return j;
    }

    private static boolean isJavaOnly(AnnotatedElement element) {
        return element.getAnnotation(JavaOnly.class) != null;
    }

    private JavaClass(Class<?> c) {
        super(c);
        this.jclass = this;
    }

    public LuaValue coerce(Object javaValue) {
        return this;
    }

    Field getField(LuaValue key) {
        if (fields == null) {
            Map<LuaValue, Field> m = new HashMap<>();
            Field[] f = ((Class<?>) m_instance).getFields();
            for (Field field : f) {
                if (isJavaOnly(field))
                    continue;

                if (JavaUservalues.class.isAssignableFrom(field.getType())) {
                    uservalues = field;
                    continue;
                }

                LuaField luaField = field.getAnnotation(LuaField.class);
                String name;

                if (luaField == null || luaField.value().isEmpty())
                    name = field.getName();
                else
                    name = luaField.value();

                m.put(LuaValue.valueOf(name), field);
            }
            this.fields = m;
        }

        return this.fields.get(key);
    }

    LuaValue getMethod(LuaValue key) {
        if (methods == null) {
            Map<LuaValue, LuaValue> methods = new HashMap<>();
            List<JavaConstructor> constructorList = new ArrayList<>();
            Map<String, List<JavaMethod>> namedMethodList = new HashMap<>();

            // --- Methods ---
            for (Method method : ((Class<?>) m_instance).getMethods()) {
                if (isJavaOnly(method))
                    continue;

                LuaMethod luaMethod = method.getAnnotation(LuaMethod.class);
                String name;

                if (luaMethod == null || luaMethod.value().isEmpty())
                    name = method.getName();
                else
                    name = luaMethod.value();

                boolean staticMethod = Modifier.isStatic(method.getModifiers());
                boolean constructorMethod = name.equals(NEW.tojstring());

                if (staticMethod && constructorMethod) {
                    constructorList.add(JavaConstructor.forMethod(method));
                    continue;
                } else if (constructorMethod) {
                    // Non-static "fake" constructors are just ignored.
                    // Otherwise, the first parameter would need to be an instance.
                    continue;
                }

                List<JavaMethod> list = namedMethodList.computeIfAbsent(name, k -> new ArrayList<>());
                list.add(JavaMethod.forMethod(method));
            }

            // --- Constructors ---
            for (Constructor<?> constructor : ((Class<?>) m_instance).getConstructors()) {
                if (isJavaOnly(constructor))
                    continue;

                constructorList.add(JavaConstructor.forConstructor(constructor));
            }

            switch (constructorList.size()) {
                case 0:
                    break;
                case 1:
                    methods.put(NEW, constructorList.getFirst());
                    break;
                default:
                    methods.put(NEW, JavaConstructor.forConstructors(constructorList.toArray(new JavaConstructor[0])));
                    break;
            }

            // --- Finalize Methods ---
            for (Entry<String, List<JavaMethod>> stringListEntry : namedMethodList.entrySet()) {
                String name = stringListEntry.getKey();
                List<JavaMethod> methodList = stringListEntry.getValue();

                methods.put(LuaValue.valueOf(name),
                    methodList.size() == 1 ?
                        methodList.getFirst() :
                        JavaMethod.forMethods(methodList.toArray(new JavaMethod[0])));
            }
            this.methods = methods;
        }
        return methods.get(key);
    }

    Class<?> getInnerClass(LuaValue key) {
        if (innerclasses == null) {
            Map<LuaValue, Class<?>> innerClasses = new HashMap<>();
            for (Class<?> clazz : ((Class<?>) m_instance).getClasses()) {
                if (isJavaOnly(clazz))
                    continue;

                if (JavaUservalues.class.isAssignableFrom(clazz))
                    continue;

                String name = clazz.getName();
                String stub = name.substring(Math.max(name.lastIndexOf('$'), name.lastIndexOf('.')) + 1);
                innerClasses.put(LuaValue.valueOf(stub), clazz);
            }
            this.innerclasses = innerClasses;
        }
        return this.innerclasses.get(key);
    }

    public LuaValue getConstructor() {
        return getMethod(NEW);
    }
}
