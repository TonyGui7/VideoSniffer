package com.tonygui.multimedia.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlayerView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private TextureView mTexture;
    private Decoder mDecoder;
    private boolean needPrepareTextureView = false;
    private boolean onSizeChange = false;
    private boolean isSurfaceAvailable = false;
    private SurfaceTexture mSurface;
    private Runnable startRunnable;

    private Runnable prepareTextureRunnable = new Runnable() {
        @Override
        public void run() {
            removeAllViews();
            mTexture = new TextureView(getContext());
            mTexture.setOpaque(false);
            mTexture.setSurfaceTextureListener(PlayerView.this);
            addView(mTexture);
            mTexture.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    };

    public PlayerView(@NonNull Context context) {
        this(context, null);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void prepareTexture() {
        if (onSizeChange) {
            post(prepareTextureRunnable);
        } else {
            needPrepareTextureView = true;
        }
    }

    public void startPlay(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (mDecoder == null) {
            mDecoder = new Decoder(this);
        }
        innerStartPlay(filePath);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mTexture != null ? mTexture.getSurfaceTexture() : mSurface;
    }

    private void innerStartPlay(final String filePath) {
        if (isSurfaceAvailable) {
            mDecoder.startPlay(filePath);
        } else {
            startRunnable = new Runnable() {
                @Override
                public void run() {
                    innerStartPlay(filePath);
                }
            };
            prepareTexture();
        }
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        mSurface = surfaceTexture;
        isSurfaceAvailable = true;
        if (startRunnable != null) {
            startRunnable.run();
            startRunnable = null;
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        if (mDecoder != null) {
            mDecoder.onSurfaceChanged(i, i1);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        if (mDecoder != null) {
            mDecoder.destroy();
        }
        post(new Runnable() {
            @Override
            public void run() {
                mTexture.setSurfaceTextureListener(null);
                mTexture = null;
                removeAllViews();
            }
        });
        return  Build.VERSION.SDK_INT > 19;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        onSizeChange = true;
        // 需要保证onSizeChanged被调用
        if (needPrepareTextureView) {
            needPrepareTextureView = false;
            prepareTexture();
        }
    }
}
