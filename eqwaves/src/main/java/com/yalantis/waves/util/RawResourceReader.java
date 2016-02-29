package com.yalantis.waves.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Artem Kholodnyi on 11/1/15 12:17 PM
 *
 */
public class RawResourceReader {

    /**
     * Reads a raw resource text file into a String
     * @param context
     * @param resId
     * @return
     */
    @Nullable
    public static String readTextFileFromRawResource(@NonNull final Context context,
                                                     @RawRes final int resId) {

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(resId))
        );

        String line;
        final StringBuilder body = new StringBuilder();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                body.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }
}
