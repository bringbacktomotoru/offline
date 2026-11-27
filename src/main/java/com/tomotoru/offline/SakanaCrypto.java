package com.tomotoru.offline;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Exact Java implementation of the SakanaGL / Tomotoru wire encryption protocol.
 *
 * Mirrors tools/server/crypto.py:
 * - Cipher: Blowfish/ECB/NoPadding, key = "test"
 * - Framing: [flag: 1B (0=raw, 1=zlib if >= 10240B)] [size: 4B LE] [payload] [0..7B zero pad]
 * - Transport: RFC 4648 Base64 ASCII string (pure Java for API 19+ and desktop JVM parity)
 */
public final class SakanaCrypto {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final byte[] BLOWFISH_KEY = "test".getBytes(UTF8);
    private static final int BLOCK_SIZE = 8;
    private static final int COMPRESS_THRESHOLD = 10240;

    private static final char[] B64_ENC =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] B64_DEC = new int[128];

    static {
        for (int i = 0; i < B64_DEC.length; i++) {
            B64_DEC[i] = -1;
        }
        for (int i = 0; i < B64_ENC.length; i++) {
            B64_DEC[B64_ENC[i]] = i;
        }
    }

    private SakanaCrypto() {}

    public static String base64Encode(byte[] data) {
        StringBuilder sb = new StringBuilder(((data.length + 2) / 3) * 4);
        int i = 0;
        while (i < data.length) {
            int b0 = data[i++] & 0xFF;
            int b1 = (i < data.length) ? (data[i++] & 0xFF) : -1;
            int b2 = (i < data.length) ? (data[i++] & 0xFF) : -1;

            sb.append(B64_ENC[b0 >> 2]);
            if (b1 < 0) {
                sb.append(B64_ENC[(b0 & 0x03) << 4]);
                sb.append("==");
            } else if (b2 < 0) {
                sb.append(B64_ENC[((b0 & 0x03) << 4) | (b1 >> 4)]);
                sb.append(B64_ENC[(b1 & 0x0F) << 2]);
                sb.append('=');
            } else {
                sb.append(B64_ENC[((b0 & 0x03) << 4) | (b1 >> 4)]);
                sb.append(B64_ENC[((b1 & 0x0F) << 2) | (b2 >> 6)]);
                sb.append(B64_ENC[b2 & 0x3F]);
            }
        }
        return sb.toString();
    }

    public static byte[] base64Decode(String input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream((input.length() * 3) / 4);
        int[] buf = new int[4];
        int count = 0;
        int pad = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
                continue;
            }
            if (c == '=') {
                buf[count++] = 0;
                pad++;
            } else if (c < 128 && B64_DEC[c] >= 0) {
                buf[count++] = B64_DEC[c];
            } else {
                throw new IllegalArgumentException("Invalid Base64 char: " + c);
            }
            if (count == 4) {
                int triple = (buf[0] << 18) | (buf[1] << 12) | (buf[2] << 6) | buf[3];
                out.write((triple >> 16) & 0xFF);
                if (pad < 2) {
                    out.write((triple >> 8) & 0xFF);
                }
                if (pad < 1) {
                    out.write(triple & 0xFF);
                }
                count = 0;
                pad = 0;
            }
        }
        return out.toByteArray();
    }

    private static byte[] zlibCompress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        byte[] buf = new byte[4096];
        try {
            while (!deflater.finished()) {
                int n = deflater.deflate(buf);
                if (n == 0) {
                    break;
                }
                baos.write(buf, 0, n);
            }
        } finally {
            deflater.end();
        }
        return baos.toByteArray();
    }

    private static byte[] zlibDecompress(byte[] data) throws Exception {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length * 2);
        byte[] buf = new byte[4096];
        try {
            while (!inflater.finished()) {
                int n = inflater.inflate(buf);
                if (n == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary()) {
                        break;
                    }
                } else {
                    baos.write(buf, 0, n);
                }
            }
        } finally {
            inflater.end();
        }
        return baos.toByteArray();
    }

    private static byte[] padBlock(byte[] data) {
        int rem = data.length % BLOCK_SIZE;
        if (rem == 0) {
            return data;
        }
        byte[] padded = new byte[data.length + (BLOCK_SIZE - rem)];
        System.arraycopy(data, 0, padded, 0, data.length);
        return padded;
    }

    public static String encryptJson(String jsonString) throws Exception {
        byte[] jsonBytes = jsonString.getBytes(UTF8);
        byte flag;
        byte[] body;
        if (jsonBytes.length >= COMPRESS_THRESHOLD) {
            flag = 1;
            body = zlibCompress(jsonBytes);
        } else {
            flag = 0;
            body = jsonBytes;
        }

        byte[] framed = new byte[5 + body.length];
        framed[0] = flag;
        ByteBuffer.wrap(framed, 1, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(body.length);
        System.arraycopy(body, 0, framed, 5, body.length);

        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(BLOWFISH_KEY, "Blowfish"));
        byte[] encrypted = cipher.doFinal(padBlock(framed));
        return base64Encode(encrypted);
    }

    public static String decryptToJson(String encoded) throws Exception {
        byte[] raw = base64Decode(encoded.trim());
        if (raw.length == 0 || (raw.length % BLOCK_SIZE) != 0) {
            throw new IllegalArgumentException("Invalid Blowfish ciphertext length: " + raw.length);
        }

        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(BLOWFISH_KEY, "Blowfish"));
        byte[] decrypted = cipher.doFinal(raw);

        if (decrypted.length < 5) {
            throw new IllegalArgumentException("Decrypted payload too short for SakanaGL header");
        }

        int flag = decrypted[0] & 0xFF;
        int size = ByteBuffer.wrap(decrypted, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

        if ((flag != 0 && flag != 1) || size < 0 || 5 + size > decrypted.length) {
            int sizeBe = ByteBuffer.wrap(decrypted, 1, 4).order(ByteOrder.BIG_ENDIAN).getInt();
            if ((flag == 0 || flag == 1) && sizeBe >= 0 && 5 + sizeBe <= decrypted.length) {
                size = sizeBe;
            } else {
                throw new IllegalArgumentException(
                        "Invalid SakanaGL frame header: flag=" + flag + ", size=" + size);
            }
        }

        byte[] body = new byte[size];
        System.arraycopy(decrypted, 5, body, 0, size);
        if (flag == 1) {
            body = zlibDecompress(body);
        }
        return new String(body, UTF8);
    }
}
