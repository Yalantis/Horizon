package com.yalantis.waves.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Artem Kholodnyi on 1/27/16.
 */
public class ColorUtil {

    public static float[] toOpenGlColor(@ColorInt int color) {
        return new float[] {
                Color.red(color) / 255f,
                Color.green(color) / 255f,
                Color.blue(color) / 255f,
                Color.alpha(color) / 255f
        };
    }
}
