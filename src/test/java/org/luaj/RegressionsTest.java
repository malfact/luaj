package org.luaj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegressionsTest extends CompilingTestCase {

    @BeforeEach
    @Override
    protected void setUp() {
        setBaseDir("regressions");
        super.setUp();
    }

    @Test
    void testModulo() { doTest("modulo"); }

    @Test
    void testConstruct() { doTest("construct"); }

    @Test
    void testBigAttrs() { doTest("bigattr"); }

    @Test
    void testControlChars() { doTest("controlchars"); }

    @Test
    void testComparators() { doTest("comparators"); }

    @Test
    void testMathRandomseed() { doTest("mathrandomseed"); }

    @Test
    void testVarargs() { doTest("varargs"); }

}
