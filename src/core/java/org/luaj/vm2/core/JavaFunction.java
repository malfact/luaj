package org.luaj.vm2.core;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

@FunctionalInterface
public interface JavaFunction {

    Varargs invoke(Varargs args);
}
