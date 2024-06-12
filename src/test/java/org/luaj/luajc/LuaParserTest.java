package org.luaj.luajc;

import org.luaj.CompilerTest;
import org.luaj.vm2.parser.LuaParser;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.fail;

public class LuaParserTest extends CompilerTest {

    @Override
    protected void doTest(String name) {
        try {
            LuaParser parser = new LuaParser(inputStreamOfLua(name), UTF_8.toString());
            parser.Chunk();
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

}
