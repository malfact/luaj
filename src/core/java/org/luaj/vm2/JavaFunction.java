package org.luaj.vm2;

@FunctionalInterface
public interface JavaFunction {

    Varargs invoke(Varargs args);
}
