package com.tonygui.multimedia.player;

public class TexCoordsUtil {

    public static float[] create(int width, int height, VertexUtil.PointRect rect, float[] array) {
        // x0
        array[0] = (float) rect.x / width;
        // y0
        array[1] = (float) rect.y / height;

        // x1
        array[2] = (float) rect.x / width;
        // y1
        array[3] = ((float) rect.y + rect.h) / height;

        // x2
        array[4] = ((float) rect.x + rect.w) / width;
        // y2
        array[5] = (float) rect.y / height;

        // x3
        array[6] = ((float) rect.x + rect.w) / width;
        // y3
        array[7] = ((float) rect.y + rect.h) / height;

        return array;
    }
}
