package org.luaj.vm2.core;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class BoundLuaFunction extends LuaFunction {
    protected final String name;
    protected final JavaFunction function;

    public BoundLuaFunction(String name, JavaFunction function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public LuaValue call() {
        return invoke(NONE).arg(1);
    }

    @Override
    public LuaValue call(LuaValue arg) {
        return invoke(arg).arg(1);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return invoke(varargsOf(arg1,arg2)).arg(1);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return invoke(varargsOf(arg1,arg2,arg3)).arg(1);
    }

    public Varargs invoke(Varargs args) {
        return function.invoke(args).eval();
    }

    @Override
    public Varargs onInvoke(Varargs args) {
        return invoke(args);
    }
}