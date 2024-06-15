package org.luaj.vm2.lib;

import org.luaj.vm2.LuaValue;

class TableLibFunction extends LibFunction {
	@Override
	public LuaValue call() {
		return argumentError(1, "table expected, got no value");
	}
}
