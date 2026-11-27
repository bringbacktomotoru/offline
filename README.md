# Tomotoru Offline Server

The Java HTTP server embedded in offline APKs of **Tomotoru** (*Hello Kitty Happy Life*). It starts with the Android app, serves the bundled game assets over loopback, and saves player progress in the app’s private storage. The offline APK needs no separate server.

## Build an offline APK

Follow the [game repository’s Quick Start](https://github.com/bringbacktomotoru/game#quick-start) to install the build tools, initialize the `tomoffline` submodule, and fetch the base APK and assets. From that checkout, build the offline variant:

```bash
python -m tools.build.build_apk --mode offline
```

The game repository pins this project as a submodule. Its builder compiles the Android sources as Java 8, merges them into `classes.dex`, and calls `OfflineServer.start(Context)` during application startup. This repository’s [releases](https://github.com/bringbacktomotoru/offline/releases) provide source snapshots; APK builds are handled by the game repository.

## Runtime and testing

| Environment | Entry point | Assets and saves |
| --- | --- | --- |
| Android APK | `OfflineServer.start(Context)` | Reads bundled assets and writes under the app-private `files/tomotoru_saves/` directory. |
| Desktop JVM | `StandaloneMain` | Reads assets and writes saves in local directories. Used by the game repository’s integration tests and excluded from APK builds. |

Both entry points listen on `127.0.0.1:18080` by default. `GET /health` returns `{"status":"healthy","embedded":true}` once the server is listening. The desktop runner accepts `--port`, `--sxr-dir`, `--saves-dir`, and `--debug`.

After preparing the dependencies and extracted assets in the game checkout, run its [Java integration tests](https://github.com/bringbacktomotoru/game/blob/main/tests/test_embedded_java_server.py):

```bash
python -m pytest -v tests/test_embedded_java_server.py
```

The suite compiles the Java sources and exercises the desktop runner, including asset delivery, game protocol responses, and save persistence across restarts.

See the [project wiki](https://github.com/bringbacktomotoru/offline/wiki) for startup configuration, HTTP routes, and troubleshooting.

## Related repositories

- [Game](https://github.com/bringbacktomotoru/game) — Android client analysis, asset tooling, and the APK build pipeline.
- [Server](https://github.com/bringbacktomotoru/server) — JavaScript implementation for running a separate Tomotoru game server.
