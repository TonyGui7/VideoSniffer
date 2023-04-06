package com.tonygui.multimedia.player;

public class VertexUtil {
    public static float[] create(int width, int height, PointRect rect, float[] array) {
        // x0
        array[0] = switchX((float) rect.x / width);
        // y0
        array[1] = switchY((float) rect.y / height);

        // x1
        array[2] = switchX((float) rect.x / width);
        // y1
        array[3] = switchY(((float) rect.y + rect.h) / height);

        // x2
        array[4] = switchX(((float) rect.x + rect.w) / width);
        // y2
        array[5] = switchY((float) rect.y / height);

        // x3
        array[6] = switchX(((float) rect.x + rect.w) / width);
        // y3
        array[7] = switchY(((float) rect.y + rect.h) / height);

        return array;
    }

    private static float switchX(float x) {
        return x * 2f - 1f;
    }

    private static float switchY(float y) {
        return ((y * 2f - 2f) * -1f) - 1f;
    }

    public static class PointRect {
        public PointRect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public int x;
        public int y;
        public int w;
        public int h;
    }
}
