package com.tonygui.multimedia.player;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Render {
    private final String VERTEX_SHADER = "attribute vec4 v_Position;\n" +
            "attribute vec2 vTexCoordinateAlpha;\n" +
            "attribute vec2 vTexCoordinateRgb;\n" +
            "varying vec2 v_TexCoordinateAlpha;\n" +
            "varying vec2 v_TexCoordinateRgb;\n" +
            "\n" +
            "void main() {\n" +
            "    v_TexCoordinateAlpha = vTexCoordinateAlpha;\n" +
            "    v_TexCoordinateRgb = vTexCoordinateRgb;\n" +
            "    gl_Position = v_Position;\n" +
            "}";

    private final String FRAGMENT_SHADER = "precision mediump float;\n" +
            "uniform sampler2D sampler_y;\n" +
            "uniform sampler2D sampler_u;\n" +
            "uniform sampler2D sampler_v;\n" +
            "varying vec2 v_TexCoordinateAlpha;\n" +
            "varying vec2 v_TexCoordinateRgb;\n" +
            "uniform mat3 convertMatrix;\n" +
            "uniform vec3 offset;\n" +
            "\n" +
            "void main() {\n" +
            "   highp vec3 yuvColorAlpha;\n" +
            "   highp vec3 yuvColorRGB;\n" +
            "   highp vec3 rgbColorAlpha;\n" +
            "   highp vec3 rgbColorRGB;\n" +
            "   yuvColorAlpha.x = texture2D(sampler_y,v_TexCoordinateAlpha).r;\n" +
            "   yuvColorRGB.x = texture2D(sampler_y,v_TexCoordinateRgb).r;\n" +
            "   yuvColorAlpha.y = texture2D(sampler_u,v_TexCoordinateAlpha).r;\n" +
            "   yuvColorAlpha.z = texture2D(sampler_v,v_TexCoordinateAlpha).r;\n" +
            "   yuvColorRGB.y = texture2D(sampler_u,v_TexCoordinateRgb).r;\n" +
            "   yuvColorRGB.z = texture2D(sampler_v,v_TexCoordinateRgb).r;\n" +
            "   yuvColorAlpha += offset;\n" +
            "   yuvColorRGB += offset;\n" +
            "   rgbColorAlpha = convertMatrix * yuvColorAlpha; \n" +
            "   rgbColorRGB = convertMatrix * yuvColorRGB; \n" +
            "   gl_FragColor=vec4(rgbColorRGB, rgbColorAlpha.r);\n" +
            "}";


    private boolean surfaceSizeChanged = false;
    private int surfaceWidth;
    private int surfaceHeight;

    private GlFloatArray vertexArray = new GlFloatArray();
    private GlFloatArray alphaArray = new GlFloatArray();
    private GlFloatArray rgbArray = new GlFloatArray();

    private int shaderProgram = 0;

    //顶点位置
    private int avPosition = 0;

    //rgb纹理位置
    private int rgbPosition = 0;

    //alpha纹理位置
    private int alphaPosition = 0;

    private EGLUtil eglUtil = new EGLUtil();

    //shader  yuv变量
    private int samplerY = 0;
    private int samplerU = 0;
    private int samplerV = 0;
    private int[] textureId = new int[3];
    private int convertMatrixUniform = 0;
    private int convertOffsetUniform = 0;

    //YUV数据
    private int widthYUV = 0;
    private int heightYUV = 0;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;

    // 像素数据向GPU传输时默认以4字节对齐
    private int unpackAlign = 4;

    // YUV offset
    private float[] YUV_OFFSET = new float[]{
            0f, -0.501960814f, -0.501960814f
    };

    // RGB coefficients
    private float[] YUV_MATRIX = new float[]{
            1f, 1f, 1f,
            0f, -0.3441f, 1.772f,
            1.402f, -0.7141f, 0f
    };


    public Render(SurfaceTexture texture) {
        eglUtil.start(texture);
        initRender();
    }

    public void initRender() {
        shaderProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //获取顶点坐标字段
        avPosition = GLES20.glGetAttribLocation(shaderProgram, "v_Position");
        //获取纹理坐标字段
        rgbPosition = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinateRgb");
        alphaPosition = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinateAlpha");

        //获取yuv字段
        samplerY = GLES20.glGetUniformLocation(shaderProgram, "sampler_y");
        samplerU = GLES20.glGetUniformLocation(shaderProgram, "sampler_u");
        samplerV = GLES20.glGetUniformLocation(shaderProgram, "sampler_v");
        convertMatrixUniform = GLES20.glGetUniformLocation(shaderProgram, "convertMatrix");
        convertOffsetUniform = GLES20.glGetUniformLocation(shaderProgram, "offset");
        //创建3个纹理
        GLES20.glGenTextures(textureId.length, textureId, 0);

        //绑定纹理
        for (int index = 0; index < textureId.length; index++) {
            int id = textureId[index];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
    }

    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        return createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
    }

    private int compileShader(int shaderType, String shaderSource) {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0) {
            GLES20.glShaderSource(shaderHandle, shaderSource);
            GLES20.glCompileShader(shaderHandle);
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shaderHandle;
    }

    private int createAndLinkProgram(int vertexShaderHandle, int fragmentShaderHandle) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);
            GLES20.glLinkProgram(programHandle);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }
        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return programHandle;
    }


    public void renderFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (surfaceSizeChanged && surfaceWidth > 0 && surfaceHeight > 0) {
            surfaceSizeChanged = false;
            GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
        }
        draw();
    }

    public void clearFrame() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        eglUtil.swapBuffers();
        if (y != null) {
            y.clear();
        }
        if (u != null) {
            u.clear();
        }
        if (v != null) {
            v.clear();
        }
        y = null;
        u = null;
        v = null;
    }

    public void destroyRender() {
        releaseTexture();
        eglUtil.release();
    }

    public void setVertext(int videoWidth, int videoHeight) {
        int width = videoWidth;
        int height = videoHeight / 2;
        vertexArray.setArray(VertexUtil.create(width, height, new VertexUtil.PointRect(0, 0, width, height), vertexArray.floatArray));
        float[] alpha = TexCoordsUtil.create(videoWidth, videoHeight, new VertexUtil.PointRect(0, height, width, height), alphaArray.floatArray);
        float[] rgb = TexCoordsUtil.create(videoWidth, videoHeight, new VertexUtil.PointRect(0, 0, width, height), rgbArray.floatArray);
        alphaArray.setArray(alpha);
        rgbArray.setArray(rgb);
    }

    public int getExternalTexture() {
        return textureId[0];
    }

    public void releaseTexture() {
        GLES20.glDeleteTextures(textureId.length, textureId, 0);
    }

    public void swapBuffers() {
        eglUtil.swapBuffers();
    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v) {
        widthYUV = width;
        heightYUV = height;
        this.y = ByteBuffer.wrap(y);
        this.u = ByteBuffer.wrap(u);
        this.v = ByteBuffer.wrap(v);

        // 当视频帧的u或者v分量的宽度不能被4整除时，用默认的4字节对齐会导致存取最后一行时越界，所以在向GPU传输数据前指定对齐方式
        if ((widthYUV / 2) % 4 != 0) {
            this.unpackAlign = (widthYUV / 2) % 2 == 0 ? 2 : 1;
        }
    }

    private void draw() {
        if (widthYUV > 0 && heightYUV > 0 && y != null && u != null && v != null) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
            GLES20.glUseProgram(shaderProgram);
            vertexArray.setVertexAttribPointer(avPosition);
            alphaArray.setVertexAttribPointer(alphaPosition);
            rgbArray.setVertexAttribPointer(rgbPosition);

//            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, unpackAlign);

            //激活纹理0来绑定y数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widthYUV, heightYUV, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);

            //激活纹理1来绑定u数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widthYUV / 2, heightYUV / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);

            //激活纹理2来绑定v数据
            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, widthYUV / 2, heightYUV / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);

            //给fragment_shader里面yuv变量设置值   0 1 标识纹理x
            GLES20.glUniform1i(samplerY, 0);
            GLES20.glUniform1i(samplerU, 1);
            GLES20.glUniform1i(samplerV, 2);

            GLES20.glUniform3fv(convertOffsetUniform, 1, FloatBuffer.wrap(YUV_OFFSET));
            GLES20.glUniformMatrix3fv(convertMatrixUniform, 1, false, YUV_MATRIX, 0);

            if (y != null) {
                y.clear();
            }
            if (u != null) {
                u.clear();
            }
            if (v != null) {
                v.clear();
            }
            y = null;
            u = null;
            v = null;
            //绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(avPosition);
            GLES20.glDisableVertexAttribArray(rgbPosition);
            GLES20.glDisableVertexAttribArray(alphaPosition);
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    /**
     * 显示区域大小变化
     */
    public void updateViewPort(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        surfaceSizeChanged = true;
        surfaceWidth = width;
        surfaceHeight = height;
    }

}
