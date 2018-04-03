package com.queenjar.helper.java.basedata;

/**
 * <pre>
 * </pre>
 * <pre>
 * Created by QueenJar
 * Wechat: queenjar
 * </pre>
 */

public class ShortHelper {
    private ShortHelper() {
    }

    public static int toUnsignedInt(short var0) {
        return var0 & '\uffff';
    }
}
