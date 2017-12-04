package com.github.is.jingxuan1990.whitebox;

import java.util.ArrayList;
import java.util.List;

import com.github.is.jingxuan1990.whitebox.constant.Constant;
import com.github.is.jingxuan1990.whitebox.exception.DataBlockException;
import com.github.is.jingxuan1990.whitebox.exception.WhiteBoxDeKeyTableException;
import com.github.is.jingxuan1990.whitebox.exception.WhiteBoxEnKeyTableException;
import com.github.is.jingxuan1990.whitebox.utils.Utils;

/**
 * 白盒加解密算法基类。
 */
public abstract class WAESBase {

    // Nb Number of columns (32-bit words) comprising the State. For this standard =
    // 4.
    static final int m_nSection = 4;

    // Nr Number of rounds, which is a function of Nk and Nb (which isfixed). For
    // this standard, Nr = 10, 12, or 14.
    int m_nRounds;

    boolean initEnKeyTable, initDeKeyTable;

    void baseInit() {
        m_nRounds = 10; // 128 aes
        initDeKeyTable = false;
        initEnKeyTable = false;
    }

    /**
     * 白盒加解密核心算法。
     *
     * @param in - 要加密的数据。
     * @param out - 加密后的数据。
     * @param isEncrypt - true： 加密 false：解密
     */
    protected abstract void cypherBlock(W128b in, List<Integer> out, boolean isEncrypt);

    /**
     * 分块加密。
     *
     * @param in - 要加密的数据，byte数组。
     * @param out - 加密的结果，这里int（0-255）表示。
     * @param length - 长度。
     */
    void encrypt(byte[] in, int[] out, long length) {

        long remain = length;
        int last = 0;
        List<Integer> list = new ArrayList<Integer>();
        while (remain > 0) {
            // 分块大小
            int size = (remain - 16) > 0 ? 16 : (int) remain;

            // 分块加密
            byte[] pin = new byte[size];
            System.arraycopy(in, last, pin, 0, size);

            // 存放加密结果
            List<Integer> pout = new ArrayList<Integer>(16);
            // 加密
            encryptBlock(pin, pout, size);

            list.addAll(pout);

            // 移动到下一块
            last += size;
            remain -= size;
        }

        for (int i = 0; i < list.size(); i++) {
            out[i] = list.get(i);
        }
    }

    private void encryptBlock(byte[] in, List<Integer> out, int blockSize) {
        if (!this.initEnKeyTable) {
            throw new WhiteBoxEnKeyTableException("白盒加密表初始化异常！");
        }

        if (blockSize > 16 || blockSize < 0) {
            throw new DataBlockException("数据块大小（0 <= blockSize <= 16）异常！");
        }

        W128b t = new W128b();
        byte2W128bXor(t, in, blockSize);
        cypherBlock(t, out, true);
    }

    /**
     * 分块解密。
     *
     * @param in - 要解密的数据，byte数组。
     * @param out - 解密的结果，这里int（0-255）表示。
     * @param length - 长度。
     */
    void decrypt(byte[] in, int[] out, long length) {

        long remain = length;
        int last = 0;
        List<Integer> list = new ArrayList<Integer>();
        while (remain > 0) {
            // 分块大小
            int size = (remain - 16) > 0 ? 16 : (int) remain;

            // 分块解密
            byte[] pin = new byte[size];
            System.arraycopy(in, last, pin, 0, size);

            // 储存解密结果
            List<Integer> pout = new ArrayList<Integer>(16);
            // 解密
            decryptBlock(pin, pout, size);

            list.addAll(pout);

            // 移动到下一块
            last += size;
            remain -= size;
        }

        for (int i = 0; i < list.size(); i++) {
            out[i] = list.get(i);
        }
    }

    private void decryptBlock(byte[] in, List<Integer> out, int blockSize) {
        if (!this.initDeKeyTable) {
            throw new WhiteBoxDeKeyTableException("白盒解密表初始化异常！");
        }

        if (blockSize > 16 || blockSize < 0) {
            throw new DataBlockException("数据块大小（0 <= blockSize <= 16）异常！");
        }

        W128b t = new W128b();
        byte2W128b(t, in, blockSize);
        cypherBlock(t, out, false);

        for (int i = 0; i < blockSize; i++) {
            out.set(i, out.get(i) ^ Constant.BLOCK_BYTE_NUM);
        }
    }

    private void byte2W128bXor(W128b t, byte[] b, int blockSize) {
        int BLOCK_BYTE_NUM = Constant.BLOCK_BYTE_NUM;
        for (int i = 0; i < blockSize; i++) {
            t.setB(i, Utils.toUnsignedByte(b[i]) ^ BLOCK_BYTE_NUM);
        }

        for (int i = blockSize; i < BLOCK_BYTE_NUM; i++) {
            t.setB(i, BLOCK_BYTE_NUM);
        }

        t.setLs(Utils.unsignedBytes2UnsignedInts(t.getBs()));
    }

    // make 16byte or less to W128b
    private void byte2W128b(W128b t, byte[] b, int blockSize) {
        int BLOCK_BYTE_NUM = Constant.BLOCK_BYTE_NUM;
        for (int i = 0; i < blockSize; i++) {
            t.setB(i, Utils.toUnsignedByte(b[i]));
        }

        for (int i = blockSize; i < BLOCK_BYTE_NUM; i++) {
            t.setB(i, 0);
        }

        t.setLs(Utils.unsignedBytes2UnsignedInts(t.getBs()));
    }
}
