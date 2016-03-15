package com.yalantis.audio.lib;

/**
 * Created by admin on 10/13/15.
 */
public class AudioUtil {

    static {
        System.loadLibrary("yaudio");
    }

    public static native void initProcessor(int sampleRate, int channels, int bitsPerSample);

    public static native void disposeProcessor();

    public static native void fft(byte[] data, float[] result);
}
