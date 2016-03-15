package com.yalantis.waves.util;

import android.opengl.GLES20;
import android.util.Log;

import com.yalantis.waves.interfaces.ShaderType;

import timber.log.Timber;

public class ShaderHelper {
	private static final String TAG = "ShaderHelper";
	
	/** 
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType The shader type.
	 * @param shaderSource The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(@ShaderType final int shaderType, final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) {
			GLES20.glShaderSource(shaderHandle, shaderSource);

			GLES20.glCompileShader(shaderHandle);

			final int[] compilationStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compilationStatus, 0);

			if (compilationStatus[0] == 0) {
				Timber.e("Error compiling shader %s", GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle,
                                           final int fragmentShaderHandle,
                                           final String[] attributes) {

		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			// Bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
}
