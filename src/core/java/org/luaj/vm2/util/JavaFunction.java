package org.luaj.vm2.util;

import org.luaj.vm2.core.Varargs;

@FunctionalInterface
public interface JavaFunction {

    Varargs invoke(Varargs args);
}
