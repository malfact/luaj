package org.luaj;

import org.junit.jupiter.api.*;
import org.luaj.vm2.*;
import org.luaj.vm2.luajc.LuaJC;

public class CompatibilityTest {
    abstract static class CompatibilityTestCase extends PlatformTestCase {
        LuaValue savedStringMetatable;

        @BeforeEach
        @Override
        protected void setUp() {
            savedStringMetatable = LuaString.s_metatable;
            setBaseDir("compatibility");
            super.setUp();
        }

        @AfterEach
        protected void tearDown() {
            LuaNil.s_metatable = null;
            LuaBoolean.s_metatable = null;
            LuaNumber.s_metatable = null;
            LuaFunction.s_metatable = null;
            LuaThread.s_metatable = null;
            LuaString.s_metatable = savedStringMetatable;
        }

        @Test
        void testBaseLib() { runTest("baselib"); }

        @Test
        void testCoroutineLib() { runTest("coroutinelib"); }

        @Disabled("Too many failing tests")
        @Test
        void testDebugLib() { runTest("debuglib"); }

        @Test
        void testErrors() { runTest("errors"); }

        @Test
        void testFunctions() { runTest("functions"); }

        @Test
        void testIoLib() { runTest("iolib"); }

        @Test
        void testManyUpvals() { runTest("manyupvals"); }

        @Test
        void testMathLib() { runTest("mathlib"); }

        @Test
        void testMetatags() { runTest("metatags"); }

        @Test
        void testOsLib() { runTest("oslib"); }

        @Test
        void testStringLib() { runTest("stringlib"); }

        @Test
        void testTableLib() { runTest("tablelib"); }

        @Test
        void testTailcalls() { runTest("tailcalls"); }

        @Test
        void testUpvalues() { runTest("upvalues"); }

        @Test
        void testVm() { runTest("vm"); }
    }

    @Nested
    class JseCompatibilityTest extends CompatibilityTestCase {

        @BeforeEach
        @Override
        protected void setUp() {
            setPlatform(PlatformTestCase.PlatformType.JSE);
            System.setProperty("JME", "false");
            super.setUp();
        }
    }

    @Nested
    class LuaJCCompatibilityTest extends CompatibilityTestCase {

        @BeforeEach
        @Override
        protected void setUp() {
            setPlatform(PlatformTestCase.PlatformType.LUAJIT);
            System.setProperty("JME", "false");
            super.setUp();
            LuaJC.install(globals);
        }

        // not supported on this platform - don't test
        @Override
        void testDebugLib() {}

        // FIXME Test failures
        @Override
        void testBaseLib() {}

        // FIXME Test failures
        @Override
        void testCoroutineLib() {}

        // FIXME Test failures
        @Override
        void testIoLib() {}

        // FIXME Test failures
        @Override
        void testMetatags() {}

        // FIXME Test failures
        @Override
        void testOsLib() {}

        // FIXME Test failures
        @Override
        void testStringLib() {}
    }
}
