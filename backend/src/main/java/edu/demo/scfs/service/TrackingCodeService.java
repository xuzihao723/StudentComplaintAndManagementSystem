package edu.demo.scfs.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class TrackingCodeService {
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int LENGTH = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generatePlainCode() {
        char[] value = new char[LENGTH];
        for (int i = 0; i < value.length; i++) {
            value[i] = ALPHABET[secureRandom.nextInt(ALPHABET.length)];
        }
        return new String(value);
    }

    public String hash(String plainCode) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(plainCode.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash tracking code", ex);
        }
    }

    public boolean matches(String plainCode, String hash) {
        if (plainCode == null || hash == null) {
            return false;
        }
        return MessageDigest.isEqual(hash(plainCode).getBytes(StandardCharsets.UTF_8), hash.getBytes(StandardCharsets.UTF_8));
    }
}
