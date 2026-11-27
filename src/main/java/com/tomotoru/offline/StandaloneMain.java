package com.tomotoru.offline;

import java.io.File;

/**
 * Standalone desktop JVM runner for automated integration tests against the
 * exact Java server classes embedded into the APK (no Android dependencies).
 */
public final class StandaloneMain {
    private StandaloneMain() {}

    public static void main(String[] args) throws Exception {
        int port = 18080;
        File savesDir = new File("saves");
        File sxrDir = new File("sxr");
        boolean debug = false;

        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i]) && i + 1 < args.length) {
                port = Integer.parseInt(args[++i]);
            } else if ("--saves-dir".equals(args[i]) && i + 1 < args.length) {
                savesDir = new File(args[++i]);
            } else if ("--sxr-dir".equals(args[i]) && i + 1 < args.length) {
                sxrDir = new File(args[++i]);
            } else if ("--debug".equals(args[i])) {
                debug = true;
            }
        }

        JsonStore store = new JsonStore(savesDir);
        AssetProvider assets = new AssetProvider.FileAssetProvider(sxrDir);
        final HttpServer server = new HttpServer(port, store, assets, debug);
        server.start();
        System.out.println("READY http://127.0.0.1:" + server.getPort());
        System.out.flush();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.stop();
            }
        }));

        Thread.sleep(Long.MAX_VALUE);
    }
}
