package org.luaj;

import org.junit.jupiter.api.BeforeEach;
import org.luaj.vm2.util.Globals;
import org.luaj.vm2.core.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.lib.jse.JseProcess;
import org.luaj.vm2.luajc.LuaJC;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PlatformTestCase extends ResourcesTestCase {
    public static final boolean nocompile = "true".equals(System.getProperty("nocompile"));

    public enum PlatformType {
        JSE, LUAJIT,
    }

    private PlatformType platform;

    private void initGlobals() {
        switch (platform) {
            default:
            case JSE:
            case LUAJIT:
                globals = JsePlatform.debugGlobals();
                break;
        }
    }

    @BeforeEach
    @Override
    protected void setUp() {
        initGlobals();
        globals.finder = this::inputStreamOfFile;
    }

    protected void setPlatform(PlatformType platform) {
        this.platform = platform;
    }

    protected void runTest(String testName) {
        try {
            // override print()
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            final PrintStream oldps = globals.STDOUT;
            final PrintStream ps = new PrintStream(output);

            // run the script
            try (ps) {
                globals.STDOUT = ps;
                LuaValue chunk = loadScript(testName, globals);
                chunk.call(LuaValue.valueOf(platform.toString()));

                ps.flush();
                String actualOutput = output.toString();
                String expectedOutput = getExpectedOutput(testName);
                actualOutput = actualOutput.replaceAll("\r\n", "\n");
                expectedOutput = expectedOutput.replaceAll("\r\n", "\n");
                if (!expectedOutput.equals(actualOutput))
                    Files.write(new File(testName + ".out").toPath(), actualOutput.getBytes());
                assertEquals(expectedOutput, actualOutput);
            } finally {
                globals.STDOUT = oldps;
            }
        } catch (IOException | InterruptedException ioe) {
            throw new RuntimeException(ioe.toString());
        }
    }

    private LuaValue loadScript(String name, Globals globals) throws IOException {
        InputStream script = inputStreamOfLua(name);
        try (script) {
            if (script == null)
                fail("Could not load script for test case: " + name);
            switch (this.platform) {
                case LUAJIT:
                    if (nocompile) {
                        LuaValue c = (LuaValue) Class.forName(name).newInstance();
                        return c;
                    } else {
                        LuaJC.install(globals);
                        return globals.load(script, name, "bt", globals);
                    }
                default:
                    return globals.load(script, "@" + name + ".lua", "bt", globals);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.toString());
        }
    }

    private String getExpectedOutput(final String name) throws IOException, InterruptedException {
        InputStream output = inputStreamOfResult(platform.name().toLowerCase() + "/" + name);
        if (output != null)
            try (output) {
                return readString(output);
            }
        return executeLuaProcess(name);
    }

    private String executeLuaProcess(String name) throws IOException, InterruptedException {
        InputStream script = inputStreamOfLua(name);
        try (script) {
            if (script == null)
                throw new IOException("Failed to find source file " + script);
            String luaCommand = System.getProperty("LUA_COMMAND");
            if (luaCommand == null)
                luaCommand = "lua";
            String[] args = new String[]{luaCommand, "-", platform.toString()};
            return collectProcessOutput(args, script);
        }
    }

    private static String collectProcessOutput(String[] cmd, final InputStream input) throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new JseProcess(cmd, input, baos, System.err).waitFor();
        return new String(baos.toByteArray());
    }

    private static String readString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copy(is, baos);
        return new String(baos.toByteArray());
    }

    private static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[1024];
        int r;
        while ( (r = is.read(buf)) >= 0 ) {
            os.write(buf, 0, r);
        }
    }
}
