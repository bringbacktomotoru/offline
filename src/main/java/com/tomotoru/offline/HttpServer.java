package com.tomotoru.offline;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Lightweight, zero-dependency embedded HTTP/1.1 server for Tomotoru Offline.
 *
 * Always sends an exact Content-Length header (never chunked) so SakanaGL's
 * URLRequest.java (which calls ByteBuffer.allocateDirect(this.contentlength))
 * works reliably on every request.
 */
public class HttpServer {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final int port;
    private final JsonStore store;
    private final AssetProvider assets;
    private final boolean debug;
    private final Map<String, String> sessions = new ConcurrentHashMap<String, String>();

    private ServerSocket serverSocket;
    private ExecutorService pool;
    private Thread acceptThread;
    private volatile boolean running;

    public HttpServer(int port, JsonStore store, AssetProvider assets, boolean debug) {
        this.port = port;
        this.store = store;
        this.assets = assets;
        this.debug = debug;
    }

    public int getPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : port;
    }

    public synchronized void start() throws IOException {
        if (running) {
            return;
        }
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new java.net.InetSocketAddress(InetAddress.getByName("127.0.0.1"), port), 50);
        running = true;
        pool = Executors.newFixedThreadPool(8);
        acceptThread = new Thread(new Runnable() {
            @Override
            public void run() {
                acceptLoop();
            }
        }, "TomotoruOfflineHttpAccept");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public synchronized void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
        if (pool != null) {
            pool.shutdownNow();
        }
    }

    private void acceptLoop() {
        while (running) {
            try {
                final Socket client = serverSocket.accept();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        handleClient(client);
                    }
                });
            } catch (IOException e) {
                if (running) {
                    System.err.println("[TomotoruOffline] Accept error: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket socket) {
        try {
            socket.setSoTimeout(15000);
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            OutputStream out = socket.getOutputStream();

            String requestLine = readLine(in);
            if (requestLine == null || requestLine.length() == 0) {
                return;
            }
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                sendText(out, 400, "Bad Request", "text/plain", "Bad Request");
                return;
            }
            String method = parts[0].toUpperCase(Locale.US);
            String rawPath = parts[1];
            int qIdx = rawPath.indexOf('?');
            String path = qIdx >= 0 ? rawPath.substring(0, qIdx) : rawPath;

            Map<String, String> headers = new HashMap<String, String>();
            String line;
            while ((line = readLine(in)) != null && line.length() > 0) {
                int colon = line.indexOf(':');
                if (colon > 0) {
                    String k = line.substring(0, colon).trim().toLowerCase(Locale.US);
                    String v = line.substring(colon + 1).trim();
                    headers.put(k, v);
                }
            }

            byte[] body = readBody(in, headers);
            String hostHeader = headers.get("host");
            if (hostHeader == null || hostHeader.length() == 0) {
                hostHeader = "127.0.0.1:" + getPort();
            }
            String baseUrl = "http://" + hostHeader;
            String contentType = headers.get("content-type");
            if (contentType == null) {
                contentType = "";
            }

            dispatch(method, path, baseUrl, contentType, body, out);
        } catch (Exception e) {
            System.err.println("[TomotoruOffline] Request error: " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void dispatch(
            String method,
            String path,
            String baseUrl,
            String contentType,
            byte[] body,
            OutputStream out)
            throws Exception {
        if ("/health".equals(path)) {
            JSONObject obj = new JSONObject();
            obj.put("status", "healthy");
            obj.put("embedded", true);
            sendJson(out, 200, "OK", obj.toString(), "HEAD".equals(method));
            return;
        }

        if ("/setting.json".equals(path) || "/dl/setting.json".equals(path)) {
            int pkgVersion = 1027;
            byte[] pkgBytes = assets.readAssetBytes("pkg.json");
            if (pkgBytes != null) {
                try {
                    JSONObject pkg = new JSONObject(new String(pkgBytes, UTF8));
                    pkgVersion = pkg.optInt("version", 1027);
                } catch (Exception ignored) {
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("release_pkg_version", pkgVersion);
            obj.put("min_pkg_version", pkgVersion);
            obj.put("min_sk_ver_ios", 0);
            obj.put("min_sk_ver_android", 0);
            obj.put("mode", 0);
            obj.put("debug_friendcode", "");
            obj.put("server_time", System.currentTimeMillis() / 1000L);
            sendJson(out, 200, "OK", obj.toString(), "HEAD".equals(method));
            return;
        }

        if ("/pkg.json".equals(path) || "/dl/pkg.json".equals(path)) {
            byte[] pkgBytes = assets.readAssetBytes("pkg.json");
            if (pkgBytes == null) {
                sendJson(out, 404, "Not Found", "{\"error\":\"pkg.json not found\"}", "HEAD".equals(method));
                return;
            }
            JSONObject pkg = new JSONObject(new String(pkgBytes, UTF8));
            JSONObject mainSxr = pkg.optJSONObject("mainsxr");
            if (mainSxr != null) {
                mainSxr.put("url", baseUrl + "/dl/main.sxr");
            }
            JSONArray sxrList = pkg.optJSONArray("sxrlist");
            if (sxrList != null) {
                for (int i = 0; i < sxrList.length(); i++) {
                    JSONObject item = sxrList.optJSONObject(i);
                    if (item != null) {
                        String name = item.optString("sxrname", "");
                        int ver = item.optInt("version", 0);
                        String hex = String.format(Locale.US, "%04x", ver);
                        item.put("url", baseUrl + "/dl/" + name + "-" + hex + ".sxr");
                    }
                }
            }
            sendJson(out, 200, "OK", pkg.toString(), "HEAD".equals(method));
            return;
        }

        if ("/conf/gamesetting.conf".equals(path)) {
            JSONObject obj = new JSONObject();
            obj.put("version", 1027);
            obj.put("language", "JP");
            obj.put("timezone", "UTC");
            obj.put("debug", false);
            sendJson(out, 200, "OK", obj.toString(), "HEAD".equals(method));
            return;
        }

        if (path.startsWith("/conf/")) {
            JSONObject obj = new JSONObject();
            obj.put("version", 1027);
            obj.put("config", path.substring("/conf/".length()));
            sendJson(out, 200, "OK", obj.toString(), "HEAD".equals(method));
            return;
        }

        if (path.startsWith("/dl/")) {
            String filename = path.substring("/dl/".length());
            byte[] fileBytes = assets.readAssetBytes(filename);
            if (fileBytes == null) {
                sendJson(out, 404, "Not Found", "{\"error\":\"File not found\"}", "HEAD".equals(method));
                return;
            }
            sendBytes(out, 200, "OK", "application/octet-stream", fileBytes, "HEAD".equals(method));
            return;
        }

        if ("/app_kitty/entrance".equals(path)) {
            JSONObject req = parsePayload(contentType, body);
            JSONObject result = CommandHandlers.handleEntrance(store, req, debug);
            String sid = result.optString("sessionId", "");
            String uid = result.optString("uuid", req.optString("uuid", ""));
            if (sid.length() > 0 && uid.length() > 0) {
                sessions.put(sid, uid);
            }
            sendEncrypted(out, result);
            return;
        }

        if ("/app_kitty/main".equals(path)) {
            JSONObject req = parsePayload(contentType, body);
            int cmd = req.optInt("cmd", 900);
            String sid = req.optString("sid", "");
            String uid = sessions.get(sid);
            if (uid == null || uid.length() == 0) {
                uid = req.optString("uuid", "default");
                if (uid == null || uid.length() == 0) {
                    uid = "default";
                }
            }
            Object cmdParams = req.has("params") ? req.opt("params") : new JSONObject();
            if (cmdParams == null || cmdParams == JSONObject.NULL) {
                cmdParams = new JSONObject();
            }
            JSONObject result = CommandHandlers.handleMain(store, cmd, cmdParams, uid, debug);
            sendEncrypted(out, result);
            return;
        }

        if ("/app_kitty/report".equals(path)) {
            JSONObject req = parsePayload(contentType, body);
            if (debug) {
                System.out.println("[TomotoruOffline] REPORT: " + req.toString());
            }
            JSONObject resp = new JSONObject();
            resp.put("ret", CommandHandlers.RESPONSE_SUCCEED);
            resp.put("server_time", System.currentTimeMillis() / 1000L);
            sendEncrypted(out, resp);
            return;
        }

        sendText(out, 404, "Not Found", "text/plain", "Not Found");
    }

    private JSONObject parsePayload(String contentType, byte[] bodyBytes) {
        String raw = new String(bodyBytes, UTF8).trim();
        if (raw.length() == 0) {
            return new JSONObject();
        }
        String candidate = raw;
        if (contentType.toLowerCase(Locale.US).contains("multipart/form-data")) {
            String extracted = extractMultipartParams(contentType, raw);
            if (extracted != null && extracted.length() > 0) {
                candidate = extracted.trim();
            }
        }
        if (candidate.startsWith("{")) {
            try {
                return new JSONObject(candidate);
            } catch (Exception ignored) {
            }
        }
        try {
            String decrypted = SakanaCrypto.decryptToJson(candidate);
            return new JSONObject(decrypted);
        } catch (Exception ignored) {
        }
        try {
            return new JSONObject(candidate);
        } catch (Exception ignored) {
            return new JSONObject();
        }
    }

    private static String extractMultipartParams(String contentType, String body) {
        String boundary = null;
        for (String part : contentType.split(";")) {
            String trimmed = part.trim();
            if (trimmed.toLowerCase(Locale.US).startsWith("boundary=")) {
                boundary = trimmed.substring("boundary=".length()).trim();
                if (boundary.startsWith("\"") && boundary.endsWith("\"") && boundary.length() >= 2) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
                break;
            }
        }
        if (boundary == null || boundary.length() == 0) {
            return null;
        }
        String delim = "--" + boundary;
        String[] sections = body.split(java.util.regex.Pattern.quote(delim));
        for (String sec : sections) {
            if (sec.contains("name=\"params\"")) {
                int idx = sec.indexOf("\r\n\r\n");
                int skip = 4;
                if (idx < 0) {
                    idx = sec.indexOf("\n\n");
                    skip = 2;
                }
                if (idx >= 0) {
                    String val = sec.substring(idx + skip);
                    while (val.endsWith("\r") || val.endsWith("\n")) {
                        val = val.substring(0, val.length() - 1);
                    }
                    return val;
                }
            }
        }
        return null;
    }

    private void sendEncrypted(OutputStream out, JSONObject resultObj) throws Exception {
        JSONArray arr = new JSONArray();
        arr.put(resultObj);
        String encoded = SakanaCrypto.encryptJson(arr.toString());
        sendText(out, 200, "OK", "text/plain", encoded);
    }

    private static void sendJson(
            OutputStream out, int code, String reason, String json, boolean headOnly)
            throws IOException {
        sendBytes(out, code, reason, "application/json; charset=utf-8", json.getBytes(UTF8), headOnly);
    }

    private static void sendText(
            OutputStream out, int code, String reason, String contentType, String text)
            throws IOException {
        sendBytes(out, code, reason, contentType, text.getBytes(UTF8), false);
    }

    private static void sendBytes(
            OutputStream out,
            int code,
            String reason,
            String contentType,
            byte[] payload,
            boolean headOnly)
            throws IOException {
        String header = "HTTP/1.1 " + code + " " + reason + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + payload.length + "\r\n"
                + "Connection: close\r\n\r\n";
        out.write(header.getBytes(UTF8));
        if (!headOnly && payload.length > 0) {
            out.write(payload);
        }
        out.flush();
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        boolean gotAny = false;
        while ((b = in.read()) != -1) {
            gotAny = true;
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                baos.write(b);
            }
        }
        if (!gotAny) {
            return null;
        }
        return new String(baos.toByteArray(), UTF8);
    }

    private static byte[] readBody(InputStream in, Map<String, String> headers) throws IOException {
        String te = headers.get("transfer-encoding");
        if (te != null && te.toLowerCase(Locale.US).contains("chunked")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (true) {
                String sizeLine = readLine(in);
                if (sizeLine == null) {
                    break;
                }
                int semi = sizeLine.indexOf(';');
                if (semi >= 0) {
                    sizeLine = sizeLine.substring(0, semi);
                }
                int chunkSize = Integer.parseInt(sizeLine.trim(), 16);
                if (chunkSize == 0) {
                    readLine(in); // trailing CRLF
                    break;
                }
                byte[] chunk = readExact(in, chunkSize);
                baos.write(chunk);
                readLine(in); // chunk CRLF
            }
            return baos.toByteArray();
        }

        String cl = headers.get("content-length");
        if (cl != null) {
            int len = Integer.parseInt(cl.trim());
            if (len > 0) {
                return readExact(in, len);
            }
        }
        return new byte[0];
    }

    private static byte[] readExact(InputStream in, int length) throws IOException {
        byte[] buf = new byte[length];
        int offset = 0;
        while (offset < length) {
            int n = in.read(buf, offset, length - offset);
            if (n == -1) {
                break;
            }
            offset += n;
        }
        if (offset < length) {
            byte[] trimmed = new byte[offset];
            System.arraycopy(buf, 0, trimmed, 0, offset);
            return trimmed;
        }
        return buf;
    }
}
