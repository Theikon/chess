package chess.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.InvalidPropertiesFormatException;
import java.util.Locale;
import java.util.Properties;

public class Vault {

    private static final Properties ACCESSOR;

    private static final File SINK;

    static {
        ACCESSOR = new Properties();
        final String path = "." + File.separator + ".session";
        File sink;
        try {
            sink = new File(path).getCanonicalFile();
            if (sink.isFile()) {
                if (!(sink.canWrite() && sink.canRead())) {
                    sink = null;
                } else {
                    try {
                        ACCESSOR.loadFromXML(new FileInputStream(sink));
                    } catch (final InvalidPropertiesFormatException ex) {
                        if (!sink.delete()) {
                            System.err.println("Could not delete possibly corrupt "
                                    + "resource at \"" + path + "\"");
                        }
                    }
                }
            } else {
                if (!sink.createNewFile()) {
                    sink = null;
                }
            }
        } catch (final IOException | SecurityException ex) {
            System.err.println("Could not access resource at \"" + path + "\"");
            sink = null;
        }
        SINK = sink;
    }

    public static boolean isPersistent() {
        return SINK != null;
    }

    public static String[] list() {
        return ACCESSOR.stringPropertyNames().toArray(new String[0]);
    }

    public static String get(final String key, final String def) {
        assert key != null : "key == null";
        assert def != null : "def == null";
        return ACCESSOR.getProperty(key, def);
    }

    public static int getInt(final String key, final int def) {
        assert key != null : "key == null";
        try {
            return Integer.parseInt(ACCESSOR.getProperty(key, String.valueOf(def)));
        } catch (final NumberFormatException ex) {
            return def;
        }
    }

    public static void put(final String key, final String val) {
        assert key != null : "key == null";
        assert val != null : "val == null";
        ACCESSOR.setProperty(key, val);
    }

    public static void save() {
        if (isPersistent()) {
            try {
                ACCESSOR.storeToXML(new FileOutputStream(SINK), null, "UTF-8");
            } catch (final IOException | ClassCastException ex) {
                // ignore as there is nothing else to try
            }
        }
    }
}
