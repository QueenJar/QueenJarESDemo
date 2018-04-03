package com.queenjar.helper.java.basedata;

/**
 * <pre>
 * </pre>
 * <pre>
 * Created by QueenJar
 * Wechat: queenjar
 * </pre>
 */

public class IntegerHelper {
    private IntegerHelper() {
    }

    public static long toUnsignedLong(int var0) {
        return (long) var0 & 4294967295L;
    }
}
