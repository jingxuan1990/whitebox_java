package com.github.is.jingxuan1990.whitebox;

public enum KeyLength {
    KEY128(128);

    private int length;

    KeyLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
