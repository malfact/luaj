package org.luaj.vm2.core;

import org.luaj.vm2.util.JavaFunction;
import org.luaj.vm2.util.LuaConstant;

public class BoundLuaFunction extends LuaFunction {
    protected final String name;
    protected final JavaFunction function;

    public BoundLuaFunction(String name, JavaFunction function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public LuaValue call() {
        return invoke(LuaConstant.NONE).get(1);
    }

    @Override
    public LuaValue call(LuaValue arg) {
        return invoke(arg).get(1);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2) {
        return invoke(varargsOf(arg1,arg2)).get(1);
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
        return invoke(varargsOf(arg1,arg2,arg3)).get(1);
    }

    public Varargs invoke(Varargs args) {
        return function.invoke(args).eval();
    }

    @Override
    public Varargs onInvoke(Varargs args) {
        return invoke(args);
    }
}
