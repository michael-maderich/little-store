package com.littlestore.validator;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetValidator {
    // allow 5 requests per IP per 5 minutes
    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW  = Duration.ofMinutes(5);

    // map IP → deque of request‐timestamps
    private final ConcurrentMap<String, Deque<Instant>> requests = new ConcurrentHashMap<>();

    /**
     * @return true if this request should be allowed; false if it exceeds MAX in WINDOW
     */
    public boolean allowForgotPassAttemptByIp(String ip) {
        Instant now = Instant.now();
        Deque<Instant> dq = requests.computeIfAbsent(ip, k -> new ArrayDeque<>());

        synchronized (dq) {
            // drop stale
            while (!dq.isEmpty() && dq.peekFirst().isBefore(now.minus(WINDOW))) {
                dq.removeFirst();
            }
            if (dq.size() >= MAX_REQUESTS) {
                return false;
            }
            dq.addLast(now);
            return true;
        }
    }
}
