package com.tomotoru.offline;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Android 10-15+ (Scoped Storage) safe screenshot, gallery, and share helpers
 * called from patched static methods in com.cyberstep.sanriocharacters.SanrioActivity.
 */
public final class ScreenshotHelper {
    private static final String TAG = "TomotoruOffline";
    private static final String AUTHORITY = "com.cyberstep.sanriocharacters.fileprovider";
    private static volatile Context appContext;

    private ScreenshotHelper() {}

    public static void init(Context context) {
        if (context != null) {
            appContext = context.getApplicationContext();
        }
    }

    public static boolean checkStoragePermission(Context ctx) {
        if (ctx != null && appContext == null) {
            appContext = ctx.getApplicationContext();
        }
        // Android 10+ (API 29+): ExternalFilesDir + MediaStore require zero runtime permissions
        if (Build.VERSION.SDK_INT >= 29 || Build.VERSION.SDK_INT < 23) {
            return true;
        }
        Context target = ctx != null ? ctx : appContext;
        if (target == null) {
            return true;
        }
        try {
            return target.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")
                    == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable t) {
            return true;
        }
    }

    public static String getExternalStorageDir() {
        if (Build.VERSION.SDK_INT >= 29 && appContext != null) {
            File dir = appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir == null) {
                dir = new File(appContext.getFilesDir(), "Pictures");
            }
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir.getAbsolutePath();
        }
        File pub = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (pub != null) {
            if (!pub.exists()) {
                pub.mkdirs();
            }
            return pub.getAbsolutePath();
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static void scanPhotoGallery(String filename, Context ctx) {
        if (filename == null || filename.length() == 0) {
            return;
        }
        Context target = ctx != null ? ctx.getApplicationContext() : appContext;
        if (target == null) {
            return;
        }
        File srcFile = new File(filename);
        if (!srcFile.isFile()) {
            Log.w(TAG, "scanPhotoGallery: file not found: " + filename);
            return;
        }

        if (Build.VERSION.SDK_INT >= 29) {
            saveToMediaStore(srcFile, target);
        } else {
            try {
                String mime = filename.endsWith(".mp4") ? "video/mp4" : "image/jpeg";
                MediaScannerConnection.scanFile(
                        target, new String[] {srcFile.getAbsolutePath()}, new String[] {mime}, null);
                Log.i(TAG, "Scanned screenshot to gallery: " + filename);
            } catch (Throwable t) {
                Log.e(TAG, "MediaScannerConnection.scanFile failed", t);
            }
        }
    }

    private static void saveToMediaStore(File srcFile, Context ctx) {
        boolean isVideo = srcFile.getName().endsWith(".mp4");
        String mime = isVideo ? "video/mp4" : "image/jpeg";
        Uri collection = isVideo
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, srcFile.getName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
        values.put("relative_path", (isVideo ? "Movies" : "Pictures") + "/Tomotoru");
        values.put("is_pending", 1);

        ContentResolver resolver = ctx.getContentResolver();
        Uri itemUri = null;
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            itemUri = resolver.insert(collection, values);
            if (itemUri == null) {
                Log.e(TAG, "MediaStore insert returned null for " + srcFile.getName());
                return;
            }
            os = resolver.openOutputStream(itemUri);
            if (os == null) {
                Log.e(TAG, "MediaStore openOutputStream returned null for " + itemUri);
                return;
            }
            fis = new FileInputStream(srcFile);
            byte[] buf = new byte[16384];
            int n;
            while ((n = fis.read(buf)) != -1) {
                os.write(buf, 0, n);
            }
            os.flush();
            os.close();
            os = null;

            ContentValues done = new ContentValues();
            done.put("is_pending", 0);
            resolver.update(itemUri, done, null, null);
            Log.i(TAG, "Saved screenshot to MediaStore gallery: Pictures/Tomotoru/" + srcFile.getName());
        } catch (Throwable t) {
            Log.e(TAG, "Failed to save screenshot to MediaStore", t);
            if (itemUri != null) {
                try {
                    resolver.delete(itemUri, null, null);
                } catch (Throwable ignored) {
                }
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Throwable ignored) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public static void startExternalIntent(String filename, String postStr, Context ctx) {
        if (filename == null || ctx == null) {
            return;
        }
        try {
            File file = new File(filename);
            if (!file.isFile()) {
                Log.w(TAG, "startExternalIntent: file not found: " + filename);
                return;
            }
            Class<?> fpClass = Class.forName("android.support.v4.content.FileProvider");
            Method getUri = fpClass.getMethod("getUriForFile", Context.class, String.class, File.class);
            Uri contentUri = (Uri) getUri.invoke(null, ctx, AUTHORITY, file);

            String mime = filename.endsWith(".mp4") ? "video/*" : "image/*";
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType(mime);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            if (postStr != null && postStr.length() > 0) {
                sendIntent.putExtra(Intent.EXTRA_TEXT, postStr);
            }
            sendIntent.setClipData(ClipData.newRawUri("", contentUri));
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent chooser = Intent.createChooser(sendIntent, "Share");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ctx.startActivity(chooser);
        } catch (Throwable t) {
            Log.e(TAG, "startExternalIntent failed", t);
        }
    }
}
