package com.github.is.jingxuan1990.whitebox.constant;

public final class Constant {
    public static final int BLOCK_BIT_NUM = 128; // AES 一个block是128bit
    public static final int BLOCK_BYTE_NUM = 16; // AES 一个block是16Byte
    public static final int SECTION_NUM = 4; // AES 每一轮划分为4个section分别处理
    public static final int SECTION_BYTE_NUM = 4; // AES 每一个section里面有4个Byte
}