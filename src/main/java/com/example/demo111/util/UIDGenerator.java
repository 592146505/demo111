package com.example.demo111.util;

import java.util.Random;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 16:18
 */
public final class UIDGenerator {

    private UIDGenerator() {
    }

    public static Long generateId() {
        return System.currentTimeMillis() + new Random().nextInt();
    }
}
