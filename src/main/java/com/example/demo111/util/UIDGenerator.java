package com.example.demo111.util;

import java.util.Random;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 23:18
 */
public final class UIDGenerator {

    private UIDGenerator() {
    }

    public static Long generateId() {
        return System.currentTimeMillis() + new Random().nextInt();
    }
}
