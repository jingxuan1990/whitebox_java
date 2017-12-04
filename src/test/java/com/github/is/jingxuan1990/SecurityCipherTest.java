package com.github.is.jingxuan1990;

import org.junit.Assert;
import org.junit.Test;

/**
 * 单元测试用例。
 */
public class SecurityCipherTest {

    /**
     * 白盒字符串加解密。
     */
    @Test
    public void WBString() throws Exception {
        String str = "我是中国人，我爱我的祖国！123456789~!@#U(@!#_@!#_+@!#)(";
        String encString = SecurityCipher.encryptWBStringToClient(str);
        String decString = SecurityCipher.decryptWBStringFromClient(encString);
        Assert.assertTrue(str.equals(decString));
    }

    /**
     * 批量白盒字符串加解密。
     */
    @Test
    public void batchWBString() throws Exception {
        StringBuilder sb = new StringBuilder();
        String str = "我爱我的祖国，123456787900+——~@！@（#@！……*&%￥@‘";
        for (int i = 0; i <= 100; i++) {
            sb.append(str);
        }
        str = sb.toString();

        long bytes = str.getBytes("UTF-8").length;
        System.out.println("加密内容的大小为：" + ((double) bytes / 1024) + "KB");

        long startTim1 = System.currentTimeMillis();
        SecurityCipher.encryptWBStringToClient(str);
        long endTime1 = System.currentTimeMillis();
        System.out.println("第一次加密耗时：" + (endTime1 - startTim1) + "毫秒。");

        long encTotalTime = 0;
        long decTotalTime = 0;
        long times = 10;
        for (int i = 1; i <= times; i++) {
            long startTime2 = System.currentTimeMillis();
            String encString = SecurityCipher.encryptWBStringToClient(str);
            long endTime2 = System.currentTimeMillis();
            long encTime = (endTime2 - startTime2);
            System.out.println("加密耗时：" + encTime + "毫秒。");

            String decString = SecurityCipher.decryptWBStringFromClient(encString);
            long endTime3 = System.currentTimeMillis();
            long decTime = (endTime3 - endTime2);
            System.out.println("解密耗时：" + decTime + "毫秒。");

            System.out.println();
            encTotalTime += encTime;
            decTotalTime += decTime;

            Assert.assertTrue(str.equals(decString));
        }

        System.out.println("加密平均耗时：" + (encTotalTime / times) + "毫秒");
        System.out.println("解密平均耗时：" + (decTotalTime / times) + "毫秒");
    }

    @Test
    public void WBBuffer() throws Exception {
        byte[] buffer = {0x11, 0x01, 0x02, 0x03, 0x04, 1, 2, 3, 5};
        byte[] encBuffer = SecurityCipher.encryptWBBufferToClient(buffer);

        byte[] decBuffer = SecurityCipher.decryptWBBufferFromClient(encBuffer);

        Assert.assertArrayEquals(buffer, decBuffer);
    }

}