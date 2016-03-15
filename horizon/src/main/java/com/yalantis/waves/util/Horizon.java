package com.yalantis.waves.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.annotation.ColorInt;

import com.yalantis.audio.lib.AudioUtil;
import com.yalantis.waves.gl.BezierRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by roma on 2/22/16.
 */
public class Horizon {

    private static final int NUMBER_OF_FREQ_BARS = 5;
    private static final int MAX_DECIBELS = 90;
    private static final int BITS_IN_BYTE = 8;
    private int maxVolumeDb = MAX_DECIBELS;
    private int bytesPerSample;
    private BezierRenderer mRenderer;
    private float[] previousSpectrum;

    public Horizon(GLSurfaceView glSurfaceView, @ColorInt int backgroundColor,
                   int sampleRate, int numChannels, int bitPerSample) {
        initView(glSurfaceView, backgroundColor);
        this.bytesPerSample = bitPerSample / numChannels / BITS_IN_BYTE;
        AudioUtil.initProcessor(sampleRate, numChannels, bitPerSample);
    }

    /**
     * Calculates how strong are frequencies of each group represented in the provided spectrum
     *
     * @param amplitudes   current spectrum
     * @param groupsNumber amount of groups to separate
     * @return array of each group strength. Each value meets [0;1] interval
     */
    private float[] fetchSpectrum(float[] amplitudes, int groupsNumber) {
        int approximateGroupLength = amplitudes.length / groupsNumber;
        float[] result = new float[groupsNumber];
        double tmpSum;
        double wholeSum = 0;
        for (int i = 0; i < groupsNumber; i++) {
            tmpSum = 0;
            for (int j = i * approximateGroupLength; j < (i + 1) * approximateGroupLength; j++) {
                tmpSum += amplitudes[j];
            }
            result[i] = (float) (tmpSum / approximateGroupLength);
            wholeSum += result[i];
        }
        for (int i = 0; i < groupsNumber; i++) {
            result[i] /= wholeSum;
        }
        return result;
    }

    /**
     * Changes spectrum values according to volume level
     *
     * @param buffer  - chunk of music
     * @param spectrum - current spectrum values
     */
    private void calculateVolumeLevel(byte[] buffer, float[] spectrum) {
        long currentMaxDb = getMaxDecibels(buffer);
        float coefficient = (float) currentMaxDb / maxVolumeDb;
        float maxCoefficient = 0;
        for (int i = 0; i < NUMBER_OF_FREQ_BARS; i++) {
            if (maxCoefficient < spectrum[i]) {
                maxCoefficient = spectrum[i];
            }
        }
        if (maxCoefficient > 0) {
            coefficient /= maxCoefficient;
            for (int i = 0; i < NUMBER_OF_FREQ_BARS; i++) {
                spectrum[i] *= coefficient;
            }
        }
    }

    private long getMaxDecibels(byte[] input) {
        float[] amplitudes = byteToFloat(input);
        if (amplitudes == null) return 0;
        float maxAmplitude = 2;
        for (float amplitude : amplitudes) {
            if (Math.abs(maxAmplitude) < Math.abs(amplitude)) {
                maxAmplitude = amplitude;
            }
        }
        return Math.round(20 * Math.log10(maxAmplitude)); //formula dB = 20 * log(a / a0);
    }

    private float[] byteToFloat(byte[] input) {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer floatBuffer = FloatBuffer.allocate(input.length / bytesPerSample);
        switch (bytesPerSample) {
            case 1:
                for (int i = 0; i < floatBuffer.capacity(); i++) {
                    floatBuffer.put(buffer.get(i * bytesPerSample));
                }
                return floatBuffer.array();
            case 2:
                for (int i = 0; i < floatBuffer.capacity(); i++) {
                    floatBuffer.put(buffer.getShort(i * bytesPerSample));
                }
                return floatBuffer.array();
            case 4:
                for (int i = 0; i < floatBuffer.capacity(); i++) {
                    floatBuffer.put(buffer.getInt(i * bytesPerSample));
                }
                return floatBuffer.array();

        }
        return null;
    }

    /**
     * Provides smooth lowering for waves
     *
     * @param spectrum - current spectrum values
     */
    private void interpolate(float[] spectrum) {
        for (int i = 0; i < spectrum.length; i++) {
            if (spectrum[i] < previousSpectrum[i]) {
                double interpolationCoefficient = 0.97;
                spectrum[i] = (float) (previousSpectrum[i] * interpolationCoefficient);
            }
            previousSpectrum[i] = spectrum[i];
        }
    }

    /**
     * Basic settings for component
     *
     * @param glSurfaceView   - view that will contain the component
     * @param backgroundColor - preferable background color for correct colors blending
     */
    private void initView(GLSurfaceView glSurfaceView, @ColorInt int backgroundColor) {
        // check if the system supports opengl es 2.0.
        Context context = glSurfaceView.getContext();
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            mRenderer = new BezierRenderer(glSurfaceView, backgroundColor);
            glSurfaceView.setRenderer(mRenderer);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void setMaxVolumeDb(int maxVolumeDb) {
        this.maxVolumeDb = maxVolumeDb;
    }

    public void updateView(byte[] buffer) {
        int amplitudeLength = (int) Math.pow(2,
                32 - Integer.numberOfLeadingZeros(buffer.length / bytesPerSample - 1)); //should be a power of two
        float[] amplitudes = new float[amplitudeLength];
        float[] spectrum;
        AudioUtil.fft(buffer, amplitudes);

        spectrum = fetchSpectrum(amplitudes, NUMBER_OF_FREQ_BARS);
        if (previousSpectrum == null) {
            previousSpectrum = spectrum;
        }
        calculateVolumeLevel(buffer, spectrum);
        interpolate(spectrum);
        mRenderer.setAmplitudes(spectrum);
    }
}
