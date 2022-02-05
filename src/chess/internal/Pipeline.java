package chess.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class Pipeline {

    private static final InputStream STDIN;

    private static final InputOutputStream INTERMEDIARY;

    private static final Thread HOOK;

    static {
        STDIN = System.in;
        INTERMEDIARY = new InputOutputStream();
        HOOK = new Thread(() -> {
            try {
                final byte[] buf = new byte[1];
                for (int i = STDIN.read(); i != -1; i = STDIN.read()) {
                    buf[0] = (byte) i;
                    INTERMEDIARY.insert(buf);
                }
            } catch (final IOException cause) {
                throw new UncheckedIOException(cause);
            }
        });
        HOOK.setDaemon(true);
        HOOK.start();
        System.setIn(INTERMEDIARY);
    }

    public static void initialize() {
    }

    public static void send(final String str) {
        assert str != null : "str == null";
        final byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        INTERMEDIARY.insert(buf);
    }

    private Pipeline() {
    }
}
