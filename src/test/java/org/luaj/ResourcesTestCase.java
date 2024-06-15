package org.luaj;

import org.junit.jupiter.api.BeforeEach;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.InputStream;

public class ResourcesTestCase {

    public String baseDir;

    protected Globals globals;

    @BeforeEach
    protected void setUp() {
        globals = JsePlatform.standardGlobals();
    }

    protected void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    protected InputStream inputStreamOfFile(String file) {
        return getClass().getClassLoader().getResourceAsStream(baseDir + "/" + file);
    }

    protected InputStream inputStreamOfLua(String name) {
        return inputStreamOfFile(name + ".lua");
    }

    protected InputStream inputStreamOfResult(String name) {
        return inputStreamOfFile(name + ".out");
    }

    protected InputStream inputStreamOfBytecode(String name) {
        return inputStreamOfFile(name + ".lc");
    }
}
