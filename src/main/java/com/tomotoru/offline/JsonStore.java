package com.tomotoru.offline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.json.JSONObject;

/**
 * Thread-safe atomic JSON file store for offline player saves.
 * Mirrors tools/server/persistence/json_store.py.
 */
public class JsonStore {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final File baseDir;

    public JsonStore(File baseDir) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    private File fileForKey(String key) {
        StringBuilder safe = new StringBuilder(key.length());
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if ((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '_'
                    || c == '-') {
                safe.append(c);
            } else {
                safe.append('_');
            }
        }
        return new File(baseDir, safe.toString() + ".json");
    }

    public synchronized JSONObject load(String key) {
        File file = fileForKey(key);
        if (!file.isFile()) {
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = fis.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return new JSONObject(new String(baos.toByteArray(), UTF8));
        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public synchronized void save(String key, JSONObject data) {
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        File target = fileForKey(key);
        File tmp = new File(baseDir, target.getName() + ".tmp");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tmp);
            fos.write(data.toString().getBytes(UTF8));
            fos.getFD().sync();
            fos.close();
            fos = null;
            if (!tmp.renameTo(target)) {
                // Fallback on filesystems where renameTo fails over existing file
                target.delete();
                tmp.renameTo(target);
            }
        } catch (Exception e) {
            tmp.delete();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
