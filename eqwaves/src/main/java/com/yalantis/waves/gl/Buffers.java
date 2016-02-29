package com.yalantis.waves.gl;

import com.yalantis.waves.Const;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * @parem bezierData four Bezier points
 * @parem triangles - number of objects
 */
public class Buffers {
    public static FloatBuffer makeInterleavedBuffer(
            float[] tData,
            int triangles) {

        int dataLength = tData.length;

        final FloatBuffer interleavedBuffer = ByteBuffer.allocateDirect(dataLength * Const.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        int tOffset = 0;

        for (int i = 0; i < triangles; i++) {
            for (int j = 0; j < Const.POINTS_PER_TRIANGLE; j++) {
                /**
                 * This doesn't seem to do make much sense for one array, but we might need to scale it up
                 */
                interleavedBuffer.put(tData, tOffset, Const.T_DATA_SIZE);
                tOffset += Const.T_DATA_SIZE;
            }
        }

        interleavedBuffer.position(0);
        return interleavedBuffer;
    }
}
