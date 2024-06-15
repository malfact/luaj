package org.luaj.vm2.core;

@FunctionalInterface
public interface JavaFunction {

    Varargs invoke(Varargs args);
}
