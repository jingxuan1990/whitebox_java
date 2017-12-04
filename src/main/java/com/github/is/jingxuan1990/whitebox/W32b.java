package com.github.is.jingxuan1990.whitebox;

/**
 * 128 bit的无符号bytes。
 */
public class W32b {

    // 用java int表示无符号byte。
    private int[] b;
    // 无符号int。
    private long l;

    public W32b() {
        b = new int[4];
        l = 0L;
    }

    public void setBs(int[] ints) {
        this.b = ints;
    }

    public int[] getBs() {
        return this.b;
    }

    public void setB(int index, int value) {
        this.b[index] = value;
    }

    public int getB(int index) {
        return this.b[index];
    }

    public long getL() {
        return this.l;
    }

    public void setL(long value) {
        this.l = value;
    }
}
