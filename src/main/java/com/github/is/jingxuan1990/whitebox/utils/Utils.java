package com.github.is.jingxuan1990.whitebox.utils;

import java.util.Arrays;

public class Utils {

    private static final String EMPTY = "";

    /**
     * java int -> unsigned char/ BYTE(int表示)
     */
    public static int toUnsignedByte(int value) {
        return value & 0xFF;
    }

    /**
     * java byte -> unsigned char/ BYTE(int表示)
     */
    public static int toUnsignedByte(byte value) {
        return (int) value & 0xFF;
    }

    /**
     * java char -> unsigned char/ BYTE(int表示)
     */
    public static int toUnsignedByte(char value) {
        return (int) value & 0xFF;
    }

    /**
     * java long -> unsigned char/ BYTE(int表示)
     */
    public static int toUnsignedByte(long value) {
        return (int) value & 0xFF;
    }

    public static String getByte(int b) {
        char[] ch = new char[2];
        ch[0] = (char) ((b) / 16);
        ch[1] = (char) ((b) % 16);

        for (int i = 0; i < 2; i++) {
            if (ch[i] >= 0 && ch[i] <= 9) {
                ch[i] = (char) ('0' + ch[i]);
            } else {
                ch[i] = (char) ('A' + ch[i] - 10);
            }
        }

        return new String(ch);
    }

    /**
     * Java bytes转为int（无符号byte）。
     */
    public static int[] bytes2Ints(byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            ints[i] = Utils.toUnsignedByte(bytes[i]);
        }

        return ints;
    }

    /**
     * int[](无符号byte)转成有符号的bytes。
     */
    public static byte[] ints2Bytes(int[] ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0; i < ints.length; i++) {
            bytes[i] = (byte) ints[i];
        }
        return bytes;
    }

    /**
     * 4字节的有符号byte转为无符号int。
     */
    public static long bytes2UnsignedInt(byte[] b) {
        long l = ((long) b[0] & 0xff) | (((long) b[1] & 0xff) << 8) | (((long) b[2] & 0xff) << 16)
                        | (((long) b[3] & 0xff) << 24);
        return toUnsignedInt(l);
    }

    /**
     * 把int[4](int表示无符号的bytes)转成ints[4]
     */
    public static long[] unsignedBytes2UnsignedInts(int[] ints) {
        byte[] bytes = Utils.ints2Bytes(ints);
        return Utils.bytes2UnsignedInts(bytes);
    }

    /**
     * 4个int(无符号byte)转成long(无符号int)。
     */
    public static long unsignedBytes2UnsignedInt(int[] ints) {
        byte[] bytes = Utils.ints2Bytes(ints);
        return Utils.bytes2UnsignedInt(bytes);
    }

    /**
     * 一个long（无符号int）转成4个int（无符号byte）
     */
    public static int[] unsignedInt2UnsignedBytes(long l) {
        byte[] bytes = long2byte(l);
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = toUnsignedByte(bytes[i]);
        }
        return ints;
    }

    /**
     * 16字节的bytes转成4个无符号的int ---> long[4]。(效率较低，不可在高并发下使用)
     */
    public static long[] bytes2UnsignedInts(byte[] bytes) {
        byte[] bytes1 = Arrays.copyOfRange(bytes, 0, 4);
        byte[] bytes2 = Arrays.copyOfRange(bytes, 4, 8);
        byte[] bytes3 = Arrays.copyOfRange(bytes, 8, 12);
        byte[] bytes4 = Arrays.copyOfRange(bytes, 12, 16);

        long[] longs = new long[4];
        longs[0] = bytes2UnsignedInt(bytes1);
        longs[1] = bytes2UnsignedInt(bytes2);
        longs[2] = bytes2UnsignedInt(bytes3);
        longs[3] = bytes2UnsignedInt(bytes4);
        return longs;
    }

    /**
     * 转成无符号int。
     */
    public static long toUnsignedInt(long value) {
        return value & 0x00000000ffffffffL;
    }

    public static void long2byte(byte[] b, long a) {
        b[0] = (byte) (a & 0xFF);
        b[1] = (byte) ((a >>> 8) & 0xFF);
        b[2] = (byte) ((a >>> 16) & 0xFF);
        b[3] = (byte) ((a >>> 24) & 0xFF);
    }

    public static byte[] long2byte(long a) {
        byte[] b = new byte[4];
        long2byte(b, a);
        return b;
    }

    /**
     * 移除尾部的char zero(0 <-> NUL)
     */
    public static String unpad(String str) {
        if (str == null || str.length() == 0) {
            return EMPTY;
        }

        char ch = str.charAt(str.length() - 1);
        if (ch != 0) {
            return str;
        }
        int index = str.indexOf(ch);
        return str.substring(0, index);
    }
}
