# Tomotoru Offline Server

The Java HTTP server embedded in offline APKs of **Tomotoru** (*Hello Kitty Happy Life*). It starts with the Android app, serves the bundled game assets over loopback, and saves player progress in the app’s private storage. The offline APK needs no separate server.

## Build an offline APK

Follow the [tools repository’s Quick Start](https://github.com/bringbacktomotoru/tools#quick-start) to install the build tools and initialize the `tomoffline` submodule. From that checkout, build the offline variant:

```bash
python -m tools.build.build_apk --mode offline
```

The tools repository pins this project as a submodule. Its builder compiles the Android sources as Java 8, merges them into `classes.dex`, and calls `OfflineServer.start(Context)` during application startup. This repository’s [releases](https://github.com/bringbacktomotoru/offline/releases) provide source snapshots; download the APKs from [Builds](https://github.com/bringbacktomotoru/builds/releases).

## Runtime and testing

| Environment | Entry point | Assets and saves |
| --- | --- | --- |
| Android APK | `OfflineServer.start(Context)` | Reads bundled assets and writes under the app-private `files/tomotoru_saves/` directory. |
| Desktop JVM | `StandaloneMain` | Reads assets and writes saves in local directories. Used by the tools repository’s integration tests and excluded from APK builds. |

Both entry points listen on `127.0.0.1:18080` by default. `GET /health` returns `{"status":"healthy","embedded":true}` once the server is listening. The desktop runner accepts `--port`, `--sxr-dir`, `--saves-dir`, and `--debug`.

After following the [test setup](https://github.com/bringbacktomotoru/tools#run-tests) in the tools checkout, run its [Java integration tests](https://github.com/bringbacktomotoru/tools/blob/main/tests/test_embedded_java_server.py):

```bash
python -m pytest -v tests/test_embedded_java_server.py
```

The suite compiles the Java sources and exercises the desktop runner, including asset delivery, game protocol responses, and save persistence across restarts.

See the [project wiki](https://github.com/bringbacktomotoru/offline/wiki) for startup configuration, HTTP routes, and troubleshooting.

## Related repositories

- [Game](https://github.com/bringbacktomotoru/game) — preserved Android client code and resources.
- [Tools](https://github.com/bringbacktomotoru/tools) — asset utilities, APK builder, and integration tests.
- [Server](https://github.com/bringbacktomotoru/server) — JavaScript implementation for running a separate Tomotoru game server.
