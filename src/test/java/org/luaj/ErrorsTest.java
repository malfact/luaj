package org.luaj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class ErrorsTest extends PlatformTestCase {

    @BeforeEach
    @Override
    protected void setUp() {
        setBaseDir("errors");
        setPlatform(PlatformTestCase.PlatformType.JSE);
        super.setUp();
    }

    @Test
    void testBaseLibArgs() {
        globals.STDIN = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };
        runTest("baselibargs");
    }

    @Test
    void testCoroutineLibArgs() { runTest("coroutinelibargs"); }

    @Disabled("Too many failing tests")
    @Test
    void testDebugLibArgs() { runTest("debuglibargs"); }

    @Test
    void testIoLibArgs() { runTest("iolibargs"); }

    @Test
    void testMathLibArgs() { runTest("mathlibargs"); }

    @Test
    void testModuleLibArgs() { runTest("modulelibargs"); }

    @Test
    void testOperators() { runTest("operators"); }

    @Test
    void testStringLibArgs() { runTest("stringlibargs"); }

    @Test
    void testTableLibArgs() { runTest("tablelibargs"); }
}
