package com.github.is.jingxuan1990.whitebox;

/**
 * 用java的int表示无符号byte(0-255)
 */
public class WaesTablesShrankXor {

    private static final int KEY_LEN = KeyLength.KEY128.getLength();

    // t4 fot t1a,t1b | 15 128 xor | 128 = 32 x 4
    // length = 2 * 15 * 32 * 128 = 122880
    public int[][][][] ex0 = new int[2][8 + 4 + 2 + 1][32][128];

    // t1a,t1b | 128 = 16 x 8
    // length = 2 * 16 * 256 = 8192
    public W128b[][][] et1 = new W128b[2][16][256];

    // rounds | 128 = 16 x 8
    public W32b[][][] et2 = new W32b[KEY_LEN / 32 + 5][16][256];

    // rounds | 128 = 16 x 8
    public W32b[][][] et3 = new W32b[KEY_LEN / 32 + 5][16][256];

    // rounds | 12 section | 32 = 8 x 4
    public int[][][][] ex4t2t3 = new int[KEY_LEN / 32 + 5][12][8][128];
    public int[][][][] ex4t3t2 = new int[KEY_LEN / 32 + 5][12][8][128];

}
