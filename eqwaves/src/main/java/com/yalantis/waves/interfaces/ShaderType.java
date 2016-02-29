package com.yalantis.waves.interfaces;

import android.opengl.GLES20;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Started by Artem Kholodnyi on 11/1/15 12:25 PM
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({GLES20.GL_VERTEX_SHADER, GLES20.GL_FRAGMENT_SHADER})
public @interface ShaderType {
}
