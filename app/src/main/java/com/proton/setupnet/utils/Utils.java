package com.proton.setupnet.utils;

import java.util.Arrays;

public class Utils {

    /**
     * Convert hex string to byte[]
     *
     * @param hexStr the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }
        String hexString = addZroe(hexStr);
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String addZroe(String str) {
        if (str.length() == 1) {
            return "0" + str;
        }
        return str;
    }

    /**
     * 判断数是否在0~255之间
     *
     * @return
     */
    public static boolean isAvaliableRange(String num) {
        int temp = Integer.valueOf(num);
        if (temp >= 0 && temp <= 255) {
            return true;
        }
        return false;
    }


    /**
     * 合并多个byte数组
     *
     * @return
     */
    public static byte[] concatAll(byte[] first, byte[]... reset) {
        int totalLength = first.length;
        for (byte[] array : reset) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;

        for (byte[] array : reset) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
