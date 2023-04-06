package com.tonygui.multimedia.player;


import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EGLUtil {
    private EGL10 egl;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private EGLContext eglContext;
    private EGLConfig eglConfig;
    public EGLUtil() {
        eglDisplay = EGL10.EGL_NO_DISPLAY;
        eglSurface = EGL10.EGL_NO_SURFACE;
        eglContext = EGL10.EGL_NO_CONTEXT;
    }

    public void start(SurfaceTexture surfaceTexture) {
        try {
            egl = (EGL10) EGLContext.getEGL();
            eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            int[] version = new int[2];
            egl.eglInitialize(eglDisplay, version);
            eglConfig = chooseConfig();
            eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, new Surface(surfaceTexture), null);
            eglContext = createContext(egl, eglDisplay, eglConfig);
            if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
                return;
            }

            if (egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext) == false) {

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private EGLConfig chooseConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] attributes = getAttributes();
        int confSize = 1;
        if (egl.eglChooseConfig(eglDisplay, attributes, configs, confSize, configsCount)) {
            return configs[0];
        }
        return null;
    }

    private int[] getAttributes() {
        return new int[]{
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,  //指定渲染api类别
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
        };
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        int[] attrs = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL10.EGL_NONE
        };
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrs);
    }

    public void swapBuffers() {
        if (eglDisplay == null || eglSurface == null){
            return;
        }
        egl.eglSwapBuffers(eglDisplay, eglSurface);
    }

    public void release() {
        egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(eglDisplay, eglSurface);
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);
    }

}
