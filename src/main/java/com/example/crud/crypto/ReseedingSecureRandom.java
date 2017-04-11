package com.example.crud.crypto;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

final class ReseedingSecureRandom {
    /** How frequently does reseeding happen. This is not strict */
    private static final int RESEED_AT = 100000;

    /** Atomic integer that keeps track of number of calls */
    private final AtomicInteger count_ = new AtomicInteger(0);
    /** Secure random */
    private volatile SecureRandom secureRandom_;

    ReseedingSecureRandom() {
        secureRandom_ = new SecureRandom();
    }

    void nextBytes(byte[] bytes) {
        getSecureRandom().nextBytes(bytes);
    }

    int nextInt() {
        return getSecureRandom().nextInt();
    }

    int nextInt(int n) {
        return getSecureRandom().nextInt(n);
    }

    private SecureRandom getSecureRandom() {
        int currentCount = count_.incrementAndGet();
        if ((currentCount % RESEED_AT) == 0) {
            // Reset to 0, next caller should not come into this "if" and might use the old secure random which is fine
            count_.set(0);
            secureRandom_ = new SecureRandom();
        }

        return secureRandom_;
    }
}
