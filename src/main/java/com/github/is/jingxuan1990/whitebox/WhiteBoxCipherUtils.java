package com.github.is.jingxuan1990.whitebox;

import java.io.UnsupportedEncodingException;

import com.github.is.jingxuan1990.whitebox.utils.Utils;

/**
 * 白盒加解密工具类。
 */
public class WhiteBoxCipherUtils {

    private static final String EMPTY = "";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final WAES ENC_WAES = new WAES(true);
    private static final WAES DEC_WAES = new WAES(false);

    /**
     * 白盒加密加密字符串。
     *
     * @param str - 要加密的字符串。
     * @return - 加密过的字符串。
     * @throws UnsupportedEncodingException 如果不支持utf8编码，则抛出此异常。
     */
    public static String encryptString(String str) throws UnsupportedEncodingException {
        if (str == null || str.length() == 0) {
            return EMPTY;
        }

        byte[] encBytes = str.getBytes(DEFAULT_CHARSET);
        // 原始长度
        int originLen = encBytes.length;
        int outsize = originLen;
        if (outsize % 16 != 0) {
            // 如果长度不足16*n，则补充成16*n
            outsize = outsize + (16 - outsize % 16);
        }

        // 存放加密后的字符(int表示)
        int[] buffer = new int[outsize];
        ENC_WAES.encrypt(encBytes, buffer, originLen);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < outsize; i++) {
            sb.append(Utils.getByte(buffer[i]));
        }

        return sb.toString();
    }

    /**
     * 白盒解密字符串。
     *
     * @param encString - 加密字符串。
     * @return - 解密后的字符串。
     * @throws UnsupportedEncodingException 如果不支持utf8编码，则抛出此异常。
     */
    public static String decryptString(String encString) throws UnsupportedEncodingException {
        if (encString == null || encString.length() == 0) {
            return EMPTY;
        }

        int outsize = encString.getBytes(DEFAULT_CHARSET).length / 2;
        if (outsize % 16 != 0) {
            return EMPTY;
        }

        int[] ibuffer = new int[outsize];
        int[] obuffer = new int[outsize + 1];
        char[] pstr = encString.toCharArray();

        int h, l;
        for (int i = 0; i < outsize; i++) {

            if (pstr[i * 2] >= '0' && pstr[i * 2] <= '9') {
                h = (pstr[i * 2] - '0') * 16;
            } else if (pstr[i * 2] >= 'a' && pstr[i * 2] <= 'f') {
                h = (pstr[i * 2] - 'a' + 10) * 16;
            } else {
                h = (pstr[i * 2] - 'A' + 10) * 16;
            }

            if (pstr[i * 2 + 1] >= '0' && pstr[i * 2 + 1] <= '9') {
                l = pstr[i * 2 + 1] - '0';
            } else if (pstr[i * 2 + 1] >= 'a' && pstr[i * 2 + 1] <= 'f') {
                l = pstr[i * 2 + 1] - 'a' + 10;
            } else {
                l = pstr[i * 2 + 1] - 'A' + 10;
            }

            ibuffer[i] = (char) (h + l);
        }

        byte[] ibytes = Utils.ints2Bytes(ibuffer);
        DEC_WAES.decrypt(ibytes, obuffer, outsize);
        byte[] oBytes = Utils.ints2Bytes(obuffer);

        return Utils.unpad(new String(oBytes, DEFAULT_CHARSET));
    }
}
