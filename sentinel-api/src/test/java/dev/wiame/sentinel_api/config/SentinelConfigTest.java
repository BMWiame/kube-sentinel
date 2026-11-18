package dev.wiame.sentinel_api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SentinelConfigTest {

    @BeforeEach
    void resetSingleton() throws Exception {
        Field f = SentinelConfig.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @AfterEach
    void resetMode() {
        SentinelConfig.getInstance().setScoringMode("highest");
    }

    @Test
    void getInstanceReturnsSameObject() {
        SentinelConfig first = SentinelConfig.getInstance();
        SentinelConfig second = SentinelConfig.getInstance();
        assertSame(first, second);
    }

    @Test
    void defaultModeIsHighest() {
        assertEquals("highest", SentinelConfig.getInstance().getScoringMode());
    }

    @Test
    void modeIsSharedAcrossGetInstanceCalls() {
        SentinelConfig.getInstance().setScoringMode("rule");
        assertEquals("rule", SentinelConfig.getInstance().getScoringMode());
    }

    @Test
    void mayActOnOnlyAllowsDemo() {
        SentinelConfig config = SentinelConfig.getInstance();
        assertTrue(config.mayActOn("demo"));
        assertFalse(config.mayActOn("kube-system"));
        assertFalse(config.mayActOn("falco"));
        assertFalse(config.mayActOn("sentinel"));
    }

    @Test
    void getInstanceIsSameAcrossThreads() throws Exception {
        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<SentinelConfig>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                futures.add(pool.submit(() -> {
                    ready.countDown();   // "I'm in position"
                    start.await();       // wait for the starting gun
                    return SentinelConfig.getInstance();
                }));
            }

            ready.await();               // all 10 threads are waiting
            start.countDown();           // release them all at once

            SentinelConfig expected = futures.get(0).get(5, TimeUnit.SECONDS);
            for (Future<SentinelConfig> f : futures) {
                assertSame(expected, f.get(5, TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
    }
}