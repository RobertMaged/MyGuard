package com.rmsr.myguard.domain.utils;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashConverter {
    @NonNull
    private static String convertToHex(@NonNull byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    @NonNull
    public static String SHA1(@NonNull String text) {
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash).toUpperCase();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
            throw new RuntimeException(noSuchAlgorithmException.getMessage());
        }
    }

}


