package com.yalantis.waves.util;

/**
 * Created by Artem Kholodnyi on 11/13/15.
 */
public class MathHelper {

    public static float mix(float x, float y, float a) {
        return x * (1 - a) + y * a;
    }

    public static float mix(float a, float b , double k) {
        return (float) (a * (1 - k) + b * k);
    }

    public static float smoothstep(float edge0, float edge1, float x) {
        float t = (float) clamp((x - edge0) / (edge1 - edge0), 0f, 1f);
        return t * t * (3f - 2f * t);
    }

    public static double clamp(float x, float f, double c) {
        return Math.max(f, Math.min(x, c));
    }
}
