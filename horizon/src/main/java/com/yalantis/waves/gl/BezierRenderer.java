package com.yalantis.waves.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.annotation.ColorInt;
import android.support.annotation.Size;

import com.yalantis.waves.R;
import com.yalantis.waves.util.ColorUtil;
import com.yalantis.waves.util.RawResourceReader;
import com.yalantis.waves.util.ShaderHelper;

import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Artem Kholodnyi on 1/25/16.
 */
public class BezierRenderer implements GLSurfaceView.Renderer {
    /**
     * Store the accumulated rotation.
     */
    private final float[] mAccumulatedRotation = new float[16];
    /**
     * Store the current rotation.
     */
    private final float[] mCurrentRotation = new float[16];
    public int numberOfPoints = 256;
    public CubicBezier[] mBezierCurves;
    public GLSurfaceView mGlSurfaceView;

    public float ratio;

    public int mvpMatrixHandle;
    public int mvMatrixHandle;

    public int bzDataHandle;
    public int bzCtrlDataHandle;
    public int tDataHandle;

    public int programHandle;
    public int colorHandle;
    public int ampHandle;
    private int bgColorHandle;
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];
    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];
    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];
    private float[] mTemporaryMatrix = new float[16];
    private int frames;
    private long startTime;
    private long timePassed;
    private float[] mAmps;
    private boolean mAnimating = true;
    private float[] mBgColor;
    private float[] mSteps = new float[]{0.05f, 0.05f, 0.05f, 0.05f, 0.05f};

    {
        // some starting values
        mAmps = new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
    }


    public BezierRenderer(GLSurfaceView glSurfaceView, @ColorInt int backgroundColor) {
        mGlSurfaceView = glSurfaceView;
        mBgColor = ColorUtil.toOpenGlColor(backgroundColor);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 1.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mGlSurfaceView.getContext(), R.raw.bz_vert);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mGlSurfaceView.getContext(), R.raw.bz_frag);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        programHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_BzData", "a_BzDataCtrl", "a_TData"});

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;

        final float left = -0.2f;
        final float right = 0.2f;
        final float bottom = left / ratio;
        final float top = right / ratio;
        final float near = 1.0f;
        final float far = 10.0f;

        this.ratio = ratio;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

        generateVerticesData();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        logFrame();
        drawGl();
    }

    public void logFrame() {
        frames++;
        timePassed = (System.nanoTime() - startTime) / 1_000_000;
        if (timePassed >= 1000) {
//            Timber.d("%d vertices @ %d fps", numberOfPoints * Const.POINTS_PER_TRIANGLE * 5 * 4, frames);
            frames = 0;
            startTime = System.nanoTime();
        }
    }

    private void generateVerticesData() {
        Executors.newSingleThreadExecutor().submit(new VerticesDataGenerator(this));
    }

    private void drawGl() {
        GLES20.glClearColor(mBgColor[0], mBgColor[1], mBgColor[2], mBgColor[3]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(programHandle);

        // Set program handles
        mvpMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mvMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVMatrix");
        colorHandle = GLES20.glGetUniformLocation(programHandle, "u_Color");
        ampHandle = GLES20.glGetUniformLocation(programHandle, "u_Amp");
        bzDataHandle = GLES20.glGetUniformLocation(programHandle, "u_BzData");
        bzCtrlDataHandle = GLES20.glGetUniformLocation(programHandle, "u_BzDataCtrl");
        bgColorHandle = GLES20.glGetUniformLocation(programHandle, "u_BgColor");

        tDataHandle = GLES20.glGetAttribLocation(programHandle, "a_TData");

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMM(mTemporaryMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(
                GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR,
                GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA
        ); // Screen blend mode

        GLES20.glBlendEquationSeparate(GLES20.GL_FUNC_ADD, GLES20.GL_FUNC_ADD);

        GLES20.glUniform4fv(bgColorHandle, 1, mBgColor, 0);

        if (mBezierCurves != null) {
            for (int i = 0, len = mBezierCurves.length; i < len; i++) {
                CubicBezier bezierCurve = mBezierCurves[i];
                GLES20.glUniform1f(ampHandle, mAmps[i / 2]); // each amplitude is reused two times
                bezierCurve.render(false);
                bezierCurve.render(true);
            }
        }
    }

    public void setAmplitudes(@Size(value = 5) float[] amps) {
        mAmps = amps;
    }

}
