package com.imperialsoupgmail.tesseractexample;

/**
 * Created by Happyandhappy on 10/17/2017.
 */

public class MyNDK {
    static {
        System.loadLibrary("MyOpencvLibs");
    }

    public native static int converGray(long matAddrRgba, long matAddrGray);
}
