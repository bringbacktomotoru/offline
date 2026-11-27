# tomoffline

The embedded Java server for the offline APKs built by [tomotoru](https://github.com/clysnelzum/tomotoru). It runs inside the Android app on `127.0.0.1:18080` by default, serves the bundled game assets and saves progress in the app's private storage.

`tomotoru` pins this repository as a Git submodule. Its APK builder compiles the sources under `src/main/java` as Java 8 against Android, merges them into `classes.dex` and hooks `OfflineServer.start(Context)` into application startup. `StandaloneMain` runs the same server on a desktop JVM for integration tests and is excluded from the APK.

For build and test instructions, see the [tomotoru README](https://github.com/clysnelzum/tomotoru#quick-start).
