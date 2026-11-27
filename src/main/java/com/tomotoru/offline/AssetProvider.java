package com.tomotoru.offline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for reading SXR assets (Android AssetManager on device, filesystem on JVM tests).
 */
public interface AssetProvider {
    byte[] readAssetBytes(String filename) throws IOException;

    final class FileAssetProvider implements AssetProvider {
        private final File sxrDir;

        public FileAssetProvider(File sxrDir) {
            this.sxrDir = sxrDir;
        }

        @Override
        public byte[] readAssetBytes(String filename) throws IOException {
            File f = new File(sxrDir, new File(filename).getName());
            if (!f.isFile()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(f);
            try {
                return readAll(fis);
            } finally {
                fis.close();
            }
        }

        public static byte[] readAll(InputStream is) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[16384];
            int n;
            while ((n = is.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
        }
    }
}
