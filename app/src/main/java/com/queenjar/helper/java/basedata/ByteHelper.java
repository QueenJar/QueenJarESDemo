package com.queenjar.helper.java.basedata;

/**
 * <pre>
 * </pre>
 * <pre>
 * Created by QueenJar
 * Wechat: queenjar
 * </pre>
 */

public class ByteHelper {
    private ByteHelper() {
    }

    public static int toUnsignedInt(byte var0) {
        return var0 & 255;
    }
}
