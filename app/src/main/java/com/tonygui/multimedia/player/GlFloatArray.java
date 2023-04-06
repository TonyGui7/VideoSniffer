package com.tonygui.multimedia.player;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlFloatArray {
    private final int SIZE = 8;
    public float[] floatArray = new float[SIZE];
    private FloatBuffer floatBuffer;

    public GlFloatArray() {
        floatBuffer = ByteBuffer.allocateDirect(SIZE * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(floatArray);
    }

    public void setArray(float[] array) {
        floatBuffer.position(0);
        floatBuffer.put(array);
    }

    public void setVertexAttribPointer(int attributeLocation) {
        floatBuffer.position(0);
        GLES20.glVertexAttribPointer(attributeLocation, 2, GLES20.GL_FLOAT, false, 0, floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);
    }
}
