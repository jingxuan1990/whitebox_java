package com.github.is.jingxuan1990.whitebox;

/**
 * 128 bit的无符号bytes。
 */
public class W128b {

    // java int表示无符号byte。
    private int[] b;
    // java long表示无符号int
    private long[] l;

    public W128b() {
        b = new int[16];
        l = new long[4];
    }

    public void setBs(int[] ints) {
        this.b = ints;
    }

    public int[] getBs() {
        return this.b;
    }

    public void setLs(long[] ls) {
        this.l = ls;
    }

    public long[] getLs() {
        return this.l;
    }

    public void setB(int index, int value) {
        this.b[index] = value;
    }

    public int getB(int index) {
        return this.b[index];
    }

    public long getL(int index) {
        return this.l[index];
    }

    public void setL(int index, long value) {
        this.l[index] = value;
    }

}
