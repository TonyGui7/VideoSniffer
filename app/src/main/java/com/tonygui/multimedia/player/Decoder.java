package com.tonygui.multimedia.player;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;

import com.tonygui.multimedia.jnihub.CodecListener;
import com.tonygui.multimedia.jnihub.NativeMultiMediaProcessor;

import java.util.Arrays;

public class Decoder implements SurfaceTexture.OnFrameAvailableListener {
    private int surfaceWidth = 1500;
    private int surfaceHeight = 1500;
    private Render mRender;
    private PlayerView mView;

    private SurfaceTexture glTexture;

    private final HandlerHolder renderThread = new HandlerHolder();
    private final HandlerHolder decodeThread = new HandlerHolder();

    public Decoder(PlayerView playerView) {
        mView = playerView;
    }

    public void onSurfaceChanged(int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
    }

    public void startPlay(final String filePath) {
        if (!prepareRenderThread()) {
            return;
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                renderInternal(filePath);
//            }
//        }).start();
        renderThread.handler.post(new Runnable() {
            @Override
            public void run() {
                renderInternal(filePath);
            }
        });
    }

    private void renderInternal(final String filePath) {
        if (!prepareRender()) {
            return;
        }

        //todo 先获取视频宽高
        int videoWidth = 1500;
        int videoHeight = 1500;
        mRender.setVertext(videoWidth, videoHeight);
        glTexture = new SurfaceTexture(mRender.getExternalTexture());
        glTexture.setOnFrameAvailableListener(Decoder.this);
        glTexture.setDefaultBufferSize(videoWidth, videoHeight);
        mRender.clearFrame();
        if (!prepareDecodeThread()) {
            return;
        }
        decodeThread.handler.post(new Runnable() {
            @Override
            public void run() {
                NativeMultiMediaProcessor.initCodec();
                NativeMultiMediaProcessor.softdecode(filePath, new CodecListener() {
                    @Override
                    public void onFrameAvailable(int width, int height, byte[] yPixel, byte[] uPixel, byte[] vPixel) {
                        byte[] yData = Arrays.copyOf(yPixel, yPixel.length);
                        byte[] uData = Arrays.copyOf(uPixel, uPixel.length);
                        byte[] vData = Arrays.copyOf(vPixel, vPixel.length);
                        mRender.setYUVData(width, height, yData, uData, vData);
                        renderData();
                    }
                });
            }
        });
//        NativeMultiMediaProcessor.initCodec();
//        NativeMultiMediaProcessor.softdecode(filePath, new CodecListener() {
//            @Override
//            public void onFrameAvailable(int width, int height, byte[] yPixel, byte[] uPixel, byte[] vPixel) {
//                byte[] yData = Arrays.copyOf(yPixel, yPixel.length);
//                byte[] uData = Arrays.copyOf(uPixel, uPixel.length);
//                byte[] vData = Arrays.copyOf(vPixel, vPixel.length);
//                mRender.setYUVData(width, height, yData, uData, vData);
//                renderData();
//            }
//        });
    }

    private void renderData() {
//        try {
//            glTexture.updateTexImage();
//            mRender.renderFrame();
//            mRender.swapBuffers();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        renderThread.handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    glTexture.updateTexImage();
                    mRender.renderFrame();
                    mRender.swapBuffers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean prepareRender() {
        if (mRender == null) {
            mRender = new Render(mView.getSurfaceTexture());
            mRender.updateViewPort(surfaceWidth, surfaceHeight);
        }
        return mRender != null;
    }

    public void destroy() {
        if (mRender != null) {
            mRender.destroyRender();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }

    private boolean prepareThread() {
        return createThread(renderThread, "render_thread") && createThread(decodeThread, "decode_thread");
    }

    private boolean prepareRenderThread() {
        return createThread(renderThread, "render_thread");
    }

    private boolean prepareDecodeThread() {
        return createThread(decodeThread, "decode_thread");
    }

    private boolean createThread(HandlerHolder handlerHolder, String name) {
        try {
            if (handlerHolder.handlerThread == null || !handlerHolder.handlerThread.isAlive()) {
                handlerHolder.handlerThread = new HandlerThread(name);
                handlerHolder.handlerThread.start();
                handlerHolder.handler = new Handler(handlerHolder.handlerThread.getLooper());
            }
            return true;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return false;
    }

    private static class HandlerHolder {
        private HandlerThread handlerThread;
        private Handler handler;
    }
}
