package com.yalantis.waves.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glTexParameteri;

public class TextureHelper {
	public static int loadTexture(final Context context, final int resourceId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;	// No pre-scaling
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		return loadTexture(bitmap);
	}

	public static int loadTexture(final Bitmap bitmap) {
		final int[] textureHandle = new int[1];

		glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			// Bind to the texture in OpenGL
			glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

//			Buffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//			bitmap.copyPixelsToBuffer(byteBuffer);
//			glTexImage2D(GL_TEXTURE_2D, 0, GLES20.GL_RGB, bitmap.getWidth(), bitmap.getHeight(),
//					0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		} else {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public static int loadTexture2(final Bitmap bitmap) {
		final int[] textureHandle = new int[1];

		glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0) {
			// Bind to the texture in OpenGL
			glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

			// Set filtering
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			// Load the bitmap into the bound texture.
			GLUtils.texSubImage2D(GL_TEXTURE_2D, 0, 0, 0, bitmap);

//			Buffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
//			bitmap.copyPixelsToBuffer(byteBuffer);
//			glTexImage2D(GL_TEXTURE_2D, 0, GLES20.GL_RGB, bitmap.getWidth(), bitmap.getHeight(),
//					0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		} else {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}
}
