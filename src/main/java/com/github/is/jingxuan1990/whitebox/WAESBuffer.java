package com.github.is.jingxuan1990.whitebox;

import java.nio.ByteBuffer;

import com.github.is.jingxuan1990.whitebox.utils.Utils;

/**
 * 读取加密或解密的二进制文件到内存中。
 */
public class WAESBuffer {

    boolean read(WaesTablesShrankXor wtable, ByteBuffer byteBuffer) {

        WaesTablesShrankXor[] table = new WaesTablesShrankXor[2];
        table[0] = wtable;
        table[1] = new WaesTablesShrankXor();

        for (int i = 0; i < 1; i++) {
            readEx0(byteBuffer, table[i]);
            readEt1(byteBuffer, table[i]);
            readEt2_3(byteBuffer, table[i], false);
            readEt2_3(byteBuffer, table[i], true);
            readEtEx4(byteBuffer, table[i], true);
            readEtEx4(byteBuffer, table[i], false);
        }
        return true;
    }

    private void readEx0(ByteBuffer byteBuffer, WaesTablesShrankXor waesTablesShrankXor) {
        byte[] b = new byte[128];
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 15; j++) {
                for (int m = 0; m < 32; m++) {
                    // 每次读取128byte
                    byteBuffer.get(b, 0, b.length);

                    int[] ints = Utils.bytes2Ints(b);

                    waesTablesShrankXor.ex0[i][j][m] = ints;

                    b = new byte[128];
                    total += b.length;// 测试用
                }
            }
        }

//    System.out.println("ex0 total: " + total);
    }

    private void readEt1(ByteBuffer byteBuffer, WaesTablesShrankXor waesTablesShrankXor) {

        byte[] b = new byte[16];
        int total = 0;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 256; m++) {
                    // 每次读取16byte到W128b中
                    byteBuffer.get(b, 0, b.length);

                    int[] ints = Utils.bytes2Ints(b);
                    long[] longs = Utils.bytes2UnsignedInts(b);

                    W128b w128b = new W128b();
                    w128b.setBs(ints);
                    w128b.setLs(longs);

                    waesTablesShrankXor.et1[i][j][m] = w128b;

                    b = new byte[16];
                    total += b.length;// 测试用
                }
            }
        }

//    System.out.println("et1 total: " + total);
    }

    private void readEt2_3(ByteBuffer byteBuffer, WaesTablesShrankXor waesTablesShrankXor, boolean isEt3) {
        byte[] b = new byte[4];
        int total = 0;
        // AEE 128 <=> 9 x 16 x 256 x 4
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 16; j++) {
                for (int m = 0; m < 256; m++) {
                    // 每次读取4byte到W32b中
                    byteBuffer.get(b, 0, b.length);

                    int[] ints = Utils.bytes2Ints(b);
                    long l = Utils.bytes2UnsignedInt(b);

                    W32b w32b = new W32b();
                    w32b.setBs(ints);
                    w32b.setL(l);

                    if (isEt3) {
                        waesTablesShrankXor.et3[i][j][m] = w32b;
                    } else {
                        waesTablesShrankXor.et2[i][j][m] = w32b;
                    }

                    b = new byte[4];
                    total += b.length;// 测试用
                }
            }
        }

//    System.out.println(String.format("%s total: %s ", isEt3 ? "et3" : "et2", total));
    }

    private void readEtEx4(ByteBuffer byteBuffer, WaesTablesShrankXor waesTablesShrankXor, boolean isEx4t2t3) {

        byte[] b = new byte[128];
        int total = 0;

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 12; j++) {
                for (int m = 0; m < 8; m++) {
                    // 每次读取128byte
                    byteBuffer.get(b, 0, b.length);

                    int[] ints = Utils.bytes2Ints(b);

                    if (isEx4t2t3) {
                        waesTablesShrankXor.ex4t2t3[i][j][m] = ints;
                    } else {
                        waesTablesShrankXor.ex4t3t2[i][j][m] = ints;
                    }

                    b = new byte[128];
                    total += b.length;// 测试用
                }
            }
        }

//    System.out.println(String.format("%s total: %s ", isEx4t2t3 ? "ex4t2t3" : "ex4t3t2", total));
    }
}
