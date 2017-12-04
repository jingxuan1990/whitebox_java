package com.github.is.jingxuan1990;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import com.github.is.jingxuan1990.whitebox.WhiteBoxCipherUtils;

/**
 * 安全组件加解密。
 */
public class SecurityCipher {

    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 白盒方式解密字符串。
     *
     * @param encString - 加密的字符串。
     * @return - 解密后的字符串。
     */
    static String decryptWBStringFromClient(String encString) {
        try {
            return WhiteBoxCipherUtils.decryptString(encString);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统不支持UTF-8编码！", e);
        }
    }

    /**
     * 白盒方式加密字符串。
     *
     * @param str - 要加密的字符串。
     * @return - 加密后的字符串。
     */
    static String encryptWBStringToClient(String str) {
        try {
            return WhiteBoxCipherUtils.encryptString(str);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统不支持UTF-8编码！", e);
        }
    }

    /**
     * 白盒方式解密buffer。
     *
     * @param encBuffer - 加密的buffer。
     * @return - 解密后的buffer。
     */
    public static byte[] decryptWBBufferFromClient(byte[] encBuffer) {
        try {
            String decString = WhiteBoxCipherUtils.decryptString(new String(encBuffer, DEFAULT_CHARSET));
            return Base64.decodeBase64(decString.getBytes(DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统不支持UTF-8编码！", e);
        }
    }

    /**
     * 白盒方式加密buffer。
     *
     * @param buffer - 要加密的buffer。
     * @return - 加密过的buffer。
     */
    public static byte[] encryptWBBufferToClient(byte[] buffer) {
        try {
            String base64String = new String(Base64.encodeBase64(buffer), DEFAULT_CHARSET);
            String encString = WhiteBoxCipherUtils.encryptString(base64String);
            return encString.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("系统不支持UTF-8编码！", e);
        }
    }

}
