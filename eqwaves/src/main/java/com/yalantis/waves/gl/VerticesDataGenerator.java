package com.yalantis.waves.gl;

import android.graphics.Color;

import com.yalantis.waves.Const;
import com.yalantis.waves.util.ColorUtil;

import java.nio.FloatBuffer;


/**
 * Created by Artem Kholodnyi on 11/2/15.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
public class VerticesDataGenerator implements Runnable {

    public static final float[] COLOR_YELLOW = ColorUtil.toOpenGlColor(Color.argb(0, 248, 230, 28));
    public static final float[] COLOR_PINK = ColorUtil.toOpenGlColor(Color.argb(0, 255, 100, 155));
    public static final float[] COLOR_PURPLE = ColorUtil.toOpenGlColor(Color.argb(0, 163, 104, 255));
    public static final float[] COLOR_GREEN = ColorUtil.toOpenGlColor(Color.argb(0, 171, 248, 189));
    public static final float[] COLOR_BLUE = ColorUtil.toOpenGlColor(Color.argb(0, 108, 206, 255));

    private final BezierRenderer mBezierRenderer;

    public VerticesDataGenerator(BezierRenderer bezierRenderer) {
        mBezierRenderer = bezierRenderer;
    }

    @Override
    public void run() {
        final float[] tData = genTData();


        // Run on the GL thread -- the same thread the other members of the renderer run in.
        mBezierRenderer.mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mBezierRenderer.mBezierCurves != null) {
                    for (CubicBezier bezierCurve : mBezierRenderer.mBezierCurves) {
                        bezierCurve.release();
                    }
                    mBezierRenderer.mBezierCurves = null;
                }

                final FloatBuffer buffer = Buffers.makeInterleavedBuffer(tData, mBezierRenderer.numberOfPoints);

                CubicBezier[] bezierCurves = new CubicBezier[10];

                bezierCurves[0] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        -1, 0,
                        -0.610f, 0.244f,
                        -0.8f, 0,
                        -0.75f, 0.244f,
                        COLOR_PURPLE
                );

                bezierCurves[1] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        -0.610f, 0.244f,
                        1, 0,
                        -0.4f, 0.244f,
                        -0.35f, 0,
                        COLOR_PURPLE
                );

                bezierCurves[2] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                             -1, 0,
                        -0.305f, 0.360f,
                          -0.65f, 0,
                          -0.4f, 0.360f,
                        COLOR_PINK
                );

                bezierCurves[3] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        -0.305f, 0.360f,
                         1, 0,
                        -0.2f, 0.360f,
                        -0.05f, 0,
                        COLOR_PINK
                );
                bezierCurves[4] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        -1, 0,
                        0, 0.488f,
                        -0.3f, 0,
                        -0.3f, 0.488f,
                        COLOR_YELLOW
                );

                bezierCurves[5] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        1, 0,
                        0, 0.488f,
                        0.3f, 0,
                        0.3f, 0.488f,
                        COLOR_YELLOW
                );

                bezierCurves[6] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                         1, 0,
                         0.305f, 0.360f,
                         0.65f, 0,
                         0.4f, 0.360f,
                        COLOR_GREEN
                );

                bezierCurves[7] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                         0.305f, 0.360f,
                        -1, 0,
                         0.2f, 0.360f,
                         0.05f, 0,
                        COLOR_GREEN
                );

                bezierCurves[8] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        1, 0,
                        0.610f, 0.244f,
                        0.8f, 0,
                        0.75f, 0.244f,
                        COLOR_BLUE
                );

                bezierCurves[9] = new CubicBezier(
                        mBezierRenderer,
                        buffer,
                        0.610f, 0.244f,
                        -1, 0,
                        0.4f, 0.244f,
                        0.35f, 0,
                        COLOR_BLUE
                );

                mBezierRenderer.mBezierCurves = bezierCurves;

            }
        });
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private float[] genTData() {
        //  1---2
        //  | /
        //  3
        float[] tData = new float[Const.POINTS_PER_TRIANGLE * Const.T_DATA_SIZE * mBezierRenderer.numberOfPoints];

        float step = 1f / (float)tData.length * 2f;

        for (int i = 0; i < tData.length; i += Const.POINTS_PER_TRIANGLE) {
            float t = (float) i / (float)tData.length;
            float t1 = (float) (i + 3) / (float)tData.length;

            tData[i] = t;
            tData[i+1] = t1;
            tData[i+2] = -1;

        }

        return tData;
    }


}

