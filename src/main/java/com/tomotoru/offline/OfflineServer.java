package com.tomotoru.offline;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.json.JSONObject;

/**
 * Entry point invoked from SakanaApplication.onCreate() inside the patched APK.
 * Reads optional assets/tomotoru_offline.json ({port, debug}) and starts the
 * embedded 127.0.0.1 HTTP server synchronously before the game engine boots.
 */
public final class OfflineServer {
    public static final int DEFAULT_PORT = 18080;
    private static final String TAG = "TomotoruOffline";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static HttpServer instance;

    private OfflineServer() {}

    public static synchronized void start(Context context) {
        ScreenshotHelper.init(context);
        if (instance != null) {
            return;
        }
        int port = DEFAULT_PORT;
        boolean debug = false;

        try {
            AssetManager am = context.getAssets();
            InputStream is = null;
            try {
                is = am.open("tomotoru_offline.json");
                byte[] cfgBytes = AssetProvider.FileAssetProvider.readAll(is);
                JSONObject cfg = new JSONObject(new String(cfgBytes, UTF8));
                port = cfg.optInt("port", DEFAULT_PORT);
                debug = cfg.optBoolean("debug", false);
            } catch (Exception ignored) {
                // Use defaults if config asset is absent
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ignored) {
                    }
                }
            }

            File savesDir = new File(context.getFilesDir(), "tomotoru_saves");
            JsonStore store = new JsonStore(savesDir);
            AssetProvider assets = new AndroidAssetProvider(am);

            HttpServer server = new HttpServer(port, store, assets, debug);
            server.start();
            instance = server;
            Log.i(TAG, "Embedded offline server started on http://127.0.0.1:"
                    + server.getPort() + " (debug=" + debug + ", saves=" + savesDir + ")");
        } catch (Throwable t) {
            Log.e(TAG, "Failed to start embedded offline server", t);
        }
    }

    public static synchronized void stop() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }
}
