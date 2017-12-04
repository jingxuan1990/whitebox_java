package com.github.is.jingxuan1990.whitebox;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.github.is.jingxuan1990.whitebox.constant.Constant;
import com.github.is.jingxuan1990.whitebox.utils.Utils;

/**
 * 白盒AES加解密算法。
 */
public class WAES extends WAESBase {

    private WaesTablesShrankXor enKeyTable, deKeyTable;

    private static final int[] shiftRow = {0, 5, 10, 15, 4, 9, 14, 3, 8, 13, 2, 7, 12, 1, 6, 11};

    private static final int[] shiftRowInv = {0, 13, 10, 7, 4, 1, 14, 11, 8, 5, 2, 15, 12, 9, 6, 3};

    WAES(boolean isEncrypt) {
        this.baseInit();
//    System.out.println(isEncrypt ? "加密初始化" : "解密初始化");
        long start = System.currentTimeMillis();
        this.initKey(isEncrypt);
        long end = System.currentTimeMillis();
//    System.out.println("初始化耗时：" + (end - start) + "毫秒");
    }

    private void initKey(boolean isEncrypt) {
        String keyFile = isEncrypt ? "/enc_key" : "/dec_key";
        try {

            InputStream inputStream = WAES.class.getResourceAsStream(keyFile);
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            ByteBuffer byteBuffer = ByteBuffer.allocate(inputStream.available());

            byte[] buffer = new byte[512];
            int readLen = 0;
            while ((readLen = dataInputStream.read(buffer)) != -1) {
                byteBuffer.put(buffer, 0, readLen);
            }

            byteBuffer.flip();

            if (isEncrypt) {
                this.loadKeyFromBuffer(true, byteBuffer);
            } else {
                this.loadKeyFromBuffer(false, byteBuffer);
            }
        } catch (Exception e) {
            String noteMsg = isEncrypt ? "enc_key" : "dec_key";
            throw new RuntimeException("初始化" + (isEncrypt ? "加密表" : "解密表") + "失败，请确保" + noteMsg + "文件在ROOT CLASSPATH目录下。", e);
        }
    }

    private void loadKeyFromBuffer(boolean isEncrypt, ByteBuffer byteBuffer) {
        WAESBuffer wfile = new WAESBuffer();
        WaesTablesShrankXor waesTablesShrankXor = new WaesTablesShrankXor();
        if (isEncrypt && wfile.read(waesTablesShrankXor, byteBuffer)) {
            this.initEnKeyTable = true;
            this.enKeyTable = waesTablesShrankXor;
            return;
        }

        if (!isEncrypt && wfile.read(waesTablesShrankXor, byteBuffer)) {
            this.initDeKeyTable = true;
            this.deKeyTable = waesTablesShrankXor;
        }

    }

    @Override
    protected void cypherBlock(W128b in, List<Integer> out, boolean isEncrypt) {
        int BLOCK_BYTE_NUM = Constant.BLOCK_BYTE_NUM;

        WaesTablesShrankXor keyTable = isEncrypt ? this.enKeyTable : this.deKeyTable;
        int[] shiftRowOp = isEncrypt ? shiftRow : shiftRowInv;

        // 初始化
        W128b state = new W128b();

        W128b[] t1ares = new W128b[BLOCK_BYTE_NUM];
        for (int i = 0; i < BLOCK_BYTE_NUM; i++) {
            t1ares[i] = new W128b();
        }

        W128b[] t1bres = new W128b[BLOCK_BYTE_NUM];
        for (int i = 0; i < BLOCK_BYTE_NUM; i++) {
            t1bres[i] = new W128b();
        }

        W32b[] t2res = new W32b[BLOCK_BYTE_NUM];
        for (int i = 0; i < BLOCK_BYTE_NUM; i++) {
            t2res[i] = new W32b();
        }

        // input decoding
        for (int i = 0; i < BLOCK_BYTE_NUM; i++) {
            W128CP(keyTable.et1[0][i][in.getB(i)], t1ares[i]);
        }

        for (int i = 0; i < 8; i++) {
            W128bShrankXor(t1ares[i * 2], t1ares[i * 2], t1ares[i * 2 + 1], keyTable.ex0[0][i]);
        }

        for (int i = 0; i < 4; i++) {
            W128bShrankXor(t1ares[i * 4], t1ares[i * 4], t1ares[i * 4 + 2], keyTable.ex0[0][8 + i]);
        }

        for (int i = 0; i < 2; i++) {
            W128bShrankXor(t1ares[i * 8], t1ares[i * 8], t1ares[i * 8 + 4], keyTable.ex0[0][12 + i]);
        }

        W128bShrankXor(t1ares[0], t1ares[0], t1ares[8], keyTable.ex0[0][14]);

        W128CP(t1ares[0], state);

        // round 1 - 9
        for (int r = 0; r < this.m_nRounds - 1; r++) {
            // apply t2 tables
            for (int j = 0; j < BLOCK_BYTE_NUM; j += 4) {
                long l1 = keyTable.et2[r][j][state.getB(shiftRowOp[j])].getL();
                t2res[j].setL(l1);
                t2res[j].setBs(Utils.unsignedInt2UnsignedBytes(l1));

                long l2 = keyTable.et2[r][j + 1][state.getB(shiftRowOp[j + 1])].getL();
                t2res[j + 1].setL(l2);
                t2res[j + 1].setBs(Utils.unsignedInt2UnsignedBytes(l2));

                long l3 = keyTable.et2[r][j + 2][state.getB(shiftRowOp[j + 2])].getL();
                t2res[j + 2].setL(l3);
                t2res[j + 2].setBs(Utils.unsignedInt2UnsignedBytes(l3));

                long l4 = keyTable.et2[r][j + 3][state.getB(shiftRowOp[j + 3])].getL();
                t2res[j + 3].setL(l4);
                t2res[j + 3].setBs(Utils.unsignedInt2UnsignedBytes(l4));

                // not xor table now
                W32bShrankXor(t2res[j], t2res[j], t2res[j + 1], keyTable.ex4t2t3[r][j / 4 * 3]);
                W32bShrankXor(t2res[j + 2], t2res[j + 2], t2res[j + 3], keyTable.ex4t2t3[r][j / 4 * 3 + 1]);
                W32bShrankXor(t2res[j], t2res[j], t2res[j + 2], keyTable.ex4t2t3[r][j / 4 * 3 + 2]);
            }

            t2resState(state, t2res);

            // apply t3 tables;
            for (int j = 0; j < BLOCK_BYTE_NUM; j += 4) {
                long l1 = keyTable.et3[r][j][state.getB(j)].getL();
                t2res[j].setL(l1);
                t2res[j].setBs(Utils.unsignedInt2UnsignedBytes(l1));

                long l2 = keyTable.et3[r][j + 1][state.getB(j + 1)].getL();
                t2res[j + 1].setL(l2);
                t2res[j + 1].setBs(Utils.unsignedInt2UnsignedBytes(l2));

                long l3 = keyTable.et3[r][j + 2][state.getB(j + 2)].getL();
                t2res[j + 2].setL(l3);
                t2res[j + 2].setBs(Utils.unsignedInt2UnsignedBytes(l3));

                long l4 = keyTable.et3[r][j + 3][state.getB(j + 3)].getL();
                t2res[j + 3].setL(l4);
                t2res[j + 3].setBs(Utils.unsignedInt2UnsignedBytes(l4));

                // not xor table now
                W32bShrankXor(t2res[j], t2res[j], t2res[j + 1], keyTable.ex4t3t2[r][j / 4 * 3]);
                W32bShrankXor(t2res[j + 2], t2res[j + 2], t2res[j + 3], keyTable.ex4t3t2[r][j / 4 * 3 + 1]);
                W32bShrankXor(t2res[j], t2res[j], t2res[j + 2], keyTable.ex4t3t2[r][j / 4 * 3 + 2]);
            }

            t2resState(state, t2res);
        }

        // final round
        for (int j = 0; j < BLOCK_BYTE_NUM; j++) {
            W128CP(keyTable.et1[1][j][state.getB(shiftRowOp[j])], t1bres[j]);
        }

        for (int i = 0; i < 8; i++) {
            W128bShrankXor(t1bres[i * 2], t1bres[i * 2], t1bres[i * 2 + 1], keyTable.ex0[1][i]);
        }

        for (int i = 0; i < 4; i++) {
            W128bShrankXor(t1bres[i * 4], t1bres[i * 4], t1bres[i * 4 + 2], keyTable.ex0[1][8 + i]);
        }

        for (int i = 0; i < 2; i++) {
            W128bShrankXor(t1bres[i * 8], t1bres[i * 8], t1bres[i * 8 + 4], keyTable.ex0[1][12 + i]);
        }

        W128bShrankXor(t1bres[0], t1bres[0], t1bres[8], keyTable.ex0[1][14]);
        W128CP(t1bres[0], state);

        // out
        for (int i = 0; i < 16; i++) {
            out.add(state.getB(i));
        }
    }

    private static void W128CP(W128b s, W128b t) {
        t.setLs(Arrays.copyOf(s.getLs(), s.getLs().length));
        t.setBs(Arrays.copyOf(s.getBs(), s.getBs().length));
    }

    private static void W128bShrankXor(W128b x, W128b a, W128b b, int[][] t) {
        int byteHigh, byteLow, bit4High, bit4Low;
        for (int i = 0; i < 16; i++) {
            byteHigh = Utils.toUnsignedByte((a.getB(i) & 0xf0) ^ (b.getB(i) >> 4));
            byteLow = Utils.toUnsignedByte((a.getB(i) << 4) ^ (b.getB(i) & 0x0f));
            bit4High = Utils.toUnsignedByte(lookShrankXorTable(t[i * 2], byteHigh));
            bit4Low = Utils.toUnsignedByte(lookShrankXorTable(t[i * 2 + 1], byteLow));
            x.setB(i, Utils.toUnsignedByte((bit4High << 4) ^ (bit4Low & 0x0f)));
        }

        // 重新计算x的l
        long[] ls = Utils.unsignedBytes2UnsignedInts(x.getBs());
        x.setLs(ls);
    }

    private static void W32bShrankXor(W32b x, W32b a, W32b b, int[][] t) {
        int byteHigh, byteLow, bit4High, bit4Low;
        for (int i = 0; i < 4; i++) {
            byteHigh = Utils.toUnsignedByte((a.getB(i) & 0xf0) ^ (b.getB(i) >> 4));
            byteLow = Utils.toUnsignedByte((a.getB(i) << 4) ^ (b.getB(i) & 0x0f));
            bit4High = Utils.toUnsignedByte(lookShrankXorTable(t[i * 2], byteHigh));
            bit4Low = Utils.toUnsignedByte(lookShrankXorTable(t[i * 2 + 1], byteLow));
            x.setB(i, Utils.toUnsignedByte((bit4High << 4) ^ (bit4Low & 0x0f)));
        }

        // 重新计算x的l
        long l = Utils.unsignedBytes2UnsignedInt(x.getBs());
        x.setL(l);
    }

    private static int lookShrankXorTable(int[] t, int index) {
        int comboByte = t[index / 2];
        if (0 == index % 2) {
            comboByte = comboByte >> 4;
        }
        return comboByte;
    }

    private void t2resState(W128b state, W32b[] t2res) {
        for (int j = 0; j < m_nSection; j++) {
            long t2resL = t2res[j * 4].getL();
            state.setL(j, t2resL);
            int[] t2resInts = Utils.unsignedInt2UnsignedBytes(t2resL);
            int count = 0;
            for (int m = j * 4; m < j * 4 + 4; m++) {
                state.setB(m, t2resInts[count]);
                count++;
            }
        }
    }
}
