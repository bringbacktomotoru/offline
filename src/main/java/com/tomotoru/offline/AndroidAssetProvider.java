package com.tomotoru.offline;

import android.content.res.AssetManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Android AssetManager-backed provider for assets/sxr/* files.
 * Kept in its own class so desktop JVM tests never touch android.* classes.
 */
public final class AndroidAssetProvider implements AssetProvider {
    private final AssetManager assets;

    public AndroidAssetProvider(AssetManager assets) {
        this.assets = assets;
    }

    @Override
    public byte[] readAssetBytes(String filename) throws IOException {
        String safeName = new File(filename).getName();
        InputStream is = null;
        try {
            is = assets.open("sxr/" + safeName);
            return FileAssetProvider.readAll(is);
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
