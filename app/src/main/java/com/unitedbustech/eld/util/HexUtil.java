package com.unitedbustech.eld.util;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description 十六进制工具类
 */
public final class HexUtil {

    /**
     * byte数组转十六进制字符串
     *
     * @param bytes byte数组
     * @return 十六进制字符串数组
     */
    public static String[] bytesToHexString(@NonNull byte[] bytes) {

        if (bytes == null) {

            return null;
        }

        String[] results = new String[bytes.length];

        for (int i = 0; i < bytes.length; i++) {

            StringBuilder stringBuilder = new StringBuilder();

            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {

                stringBuilder.append(0);
            }

            stringBuilder.append(hv);

            results[i] = stringBuilder.toString().toUpperCase();
        }

        return results;
    }

    /**
     * 将使用十六进制表示的字符串转换为Long
     *
     * @param hex 16进制的字符串
     * @return Long形数字
     */
    public static long hexConvertToLong(@NonNull String hex) {

        return Long.parseLong(hex, 16);
    }

    /**
     * 将使用十六进制表示的字符串转换为整形
     *
     * @param hex 16进制的字符串
     * @return 整形数字
     */
    public static int hexConvertToInteger(@NonNull String hex) {

        return Integer.parseInt(hex, 16);
    }

    /**
     * 将使用十六进制表示的字符串转换为字符串
     *
     * @param hex 16进制的字符串
     * @return 字符串
     */
    public static String hexConvertToString(@NonNull String[] hex) {

        byte[] baKeyword = new byte[hex.length];
        for (int i = 0; i < baKeyword.length; i++) {

            try {

                baKeyword[i] = (byte) (0xff & Integer.parseInt(hex[i], 16));
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        try {

            return new String(baKeyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * 十六进制转换为int值
     * 有符号整型
     *
     * @param hex 十六进制字符串
     * @return int值
     * @throws Exception 异常
     */
    public static int hexToSignedInt(String hex) throws Exception {

        hex = hex.toLowerCase();
        if (hex.startsWith("0x")) {

            hex = hex.substring(2, hex.length());
        }
        int ri = 0;
        int length = hex.length();
        if (length > 8) {

            throw (new Exception("too lang"));
        }

        for (int i = 0; i < length; i++) {

            char c = hex.charAt(i);
            int h;
            if (('0' <= c && c <= '9')) {

                h = c - 48;
            } else if (('a' <= c && c <= 'f')) {

                h = c - 87;
            } else if ('A' <= c && c <= 'F') {

                h = c - 55;
            } else {

                throw (new Exception("not a integer "));
            }
            byte left = (byte) ((length - i - 1) * 4);
            ri |= (h << left);
        }

        return ri;
    }
}
