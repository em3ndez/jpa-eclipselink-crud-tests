package com.example.crud.crypto;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public final class Randomness {
    private static final int NUM_SEEDS = 16;
    private static final SecureRandom SEED_PROVIDER = new SecureRandom();
    private static final Pattern DB_OBJECT_ID_PATTERN = Pattern.compile("[0-9a-f]{24}", Pattern.CASE_INSENSITIVE);
    private static final String BASE64_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final String OBJECT_ID_CHAR = "0123456789abcdef";
    private static final int[] BASE64_NUM = new int[256]; //maps base64 characters to 0-63, others to 0.

    static {
        for(int i = 0; i < 64; ++i) {
            BASE64_NUM[BASE64_CHAR.charAt(i)] = i;
        }
    }

    private static final ThreadLocal<ReseedingSecureRandom> SECURE_RANDOM = new ThreadLocal<ReseedingSecureRandom>() {
        @Override
        protected ReseedingSecureRandom initialValue() {
            return new ReseedingSecureRandom();
        }
    };

    // Use the MersenneTwisterFast implementation for its performance and long period. The implementation is
    // not thread-safe so we keep an instance in thread-local storage.
    private static final ThreadLocal<MersenneTwisterFast> MERSENNE_TWISTER = new ThreadLocal<MersenneTwisterFast>() {
        @Override
        protected MersenneTwisterFast initialValue() {
            int[] seedInts = new int[NUM_SEEDS];
            for (int i = 0; i < NUM_SEEDS; i++) {
                seedInts[i] = SEED_PROVIDER.nextInt();
            }

            return new MersenneTwisterFast(seedInts);
        }
    };

    private Randomness() {}

    /** Unsecure! */
    public static MersenneTwisterFast getMersenneTwister() {
        return MERSENNE_TWISTER.get();
    }

    public static int randomIntSecure(int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }

        // NOTE: This is completely different from how java.util.Random#nextInt(int) does it
        return Math.abs(SECURE_RANDOM.get().nextInt()) % n;
    }

    public static int[] randomIntsSecure(int count) {
        int[] ints = new int[count];
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = SECURE_RANDOM.get().nextInt();
        }
        return ints;
    }

    public static int[] randomIntsUnsecure(int count) {
        MersenneTwisterFast mt = getMersenneTwister();
        int[] ints = new int[count];
        for (int i = 0; i < ints.length; ++i) {
            ints[i] = mt.nextInt();
        }
        return ints;
    }

    public static byte[] randomBytesSecure(int size /* in bytes */) {
        byte[] bytes = new byte[size];
        SECURE_RANDOM.get().nextBytes(bytes);

        return bytes;
    }

    public static byte[] randomBytesUnsecure(int size /* in bytes */) {
        byte[] bytes = new byte[size];
        getMersenneTwister().nextBytes(bytes);

        return bytes;
    }

    public static String randomHexStringSecure(int lengthBytes) {
        StringBuilder str = new StringBuilder();
        ReseedingSecureRandom sr = SECURE_RANDOM.get();
        for (int i = 0; i < lengthBytes; i++) {
            str.append(Integer.toHexString(sr.nextInt(16)));
        }

        return str.toString();
    }

    public static boolean isValidDBObjectId(String stringId) {
        return stringId != null && DB_OBJECT_ID_PATTERN.matcher(stringId).matches()
                && stringId.toLowerCase().equals(stringId);
    }

    public static String toValidDBObjectId(String stringId) {
        if (isValidDBObjectId(stringId)) {
            return stringId;
        }
        if (stringId.length() < 24) {
            throw new  AssertionError("Can't convert to valid DBObjectId " + stringId);
        }
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < 24; ++i) {
            byte bt = (byte) stringId.charAt(i);
            String hs = Integer.toHexString(bt & 0x0F).toLowerCase();
            hexString.append(hs);
        }
        return hexString.toString();
    }

    public static String treeNodeId() {
        return treeNodeId('\u0000');
    }

    public static String treeNodeId(char prefix) {
        return treeNodeId(prefix, getMersenneTwister());
    }

    public static String treeNodeId(char prefix, MersenneTwisterFast twister) {
        final int prefixLength = prefix == '\u0000' ? 0 : 1;
        final int numChars = prefixLength + 16; // 16 * 6 is 96 bits of entropy
        char[] chars = new char[numChars];
        if (prefixLength == 1) {
            chars[0] = prefix;
        }
        for (int i = 0; i < 16; ++i) {
            chars[prefixLength + i] = BASE64_CHAR.charAt(twister.nextInt(64));
        }

        return new String(chars);
    }

    /** Not secure! */
    public static String alphaString(int length) {
        MersenneTwisterFast mt = getMersenneTwister();
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = BASE64_CHAR.charAt(mt.nextInt(52));
        }
        return new String(chars);
    }

    public static String alphaNumericStringSecure(int length) {
        ReseedingSecureRandom sr = SECURE_RANDOM.get();
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = BASE64_CHAR.charAt(sr.nextInt(62));
        }
        return new String(chars);
    }

    public static String elementId() {
        MersenneTwisterFast mt = getMersenneTwister();
        char[] chars = new char[24];
        for (int i = 0; i < 24; ++i) {
            chars[i] = OBJECT_ID_CHAR.charAt(mt.nextInt(16));
        }
        return new String(chars);
    }

    public static String alphaNumericString(int length) {
        MersenneTwisterFast mt = getMersenneTwister();
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = BASE64_CHAR.charAt(mt.nextInt(62));
        }
        return new String(chars);
    }

    /** Returns a feature id without the ordinal */
    public static String featureId() {
        return "F" + alphaNumericString(14);
    }

    public static char toBase64Char(int position) {
        return BASE64_CHAR.charAt(position % 64);
    }

    public static int fromBase64Char(char character) {
        return BASE64_NUM[character];
    }
}
