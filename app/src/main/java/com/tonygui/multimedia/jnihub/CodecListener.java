package com.tonygui.multimedia.jnihub;

public interface CodecListener {
    public void onFrameAvailable(int width, int height, byte[] yPixel, byte[] uPixel, byte[] vPixel);
}
