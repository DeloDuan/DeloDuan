package com.xnxs.mediaplayer.widget.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.asha.vrlib.MDVRLibrary;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by duanchunlin on 2016/8/4.
 */
public class GLSurfaceRenderView extends GLSurfaceView implements IRenderView, MediaPlayerVRControl {

    private int mDisPlayMode = MediaPlayerVRControl.VIDEO_TYPE_UNKNOWN;

    private MeasureHelper mMeasureHelper;
    private SurfaceCallback mSurfaceCallback;
    private MDVRLibrary mMDVRLibrary;
    private Activity mContext;

    public GLSurfaceRenderView(Context context) {
        super(context);
        initView(context);
    }

    public GLSurfaceRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mMeasureHelper = new MeasureHelper(this);
        mSurfaceCallback = new SurfaceCallback(this);

        getHolder().addCallback(mSurfaceCallback);
        //noinspection deprecation
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
    }

    public void pause() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onPause(getContext());
        }
    }


    public void resume() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onResume(getContext());
        }
    }

    public void onDestroy() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onDestroy();
        }
        mMDVRLibrary = null;
        mDisPlayMode = MediaPlayerVRControl.VIDEO_TYPE_UNKNOWN;
    }

    @Override
    public MDVRLibrary bindVRLibrary(Activity activity) {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onDestroy();
        }
        mContext = activity;
        mMDVRLibrary = MDVRLibrary.with(activity)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .asVideo(mSurfaceCallback)
                .build(this);
        return mMDVRLibrary;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return true;
    }
    //--------------------
    // Layout & Measure
    //--------------------
    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            getHolder().setFixedSize(videoWidth, videoHeight);
            if (mMDVRLibrary != null) {
                mMDVRLibrary.onTextureResize(videoWidth, videoHeight);
            }
            requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        Log.e("", "SurfaceView doesn't support rotation (" + degree + ")!\n");
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    //-------------------------

    @Override
    public void addRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.addRenderCallback(callback);
    }

    @Override
    public void removeRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.removeRenderCallback(callback);
        onDestroy();
    }

    private static final class InternalSurfaceHolder implements IRenderView.ISurfaceHolder {
        private GLSurfaceRenderView mGLSurfaceView;
        private Surface mVRSurface;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void bindToMediaPlayer(IMediaPlayer mp) {
            if (mp != null && mVRSurface != null) {
                mp.setSurface(mVRSurface);
                if (mp instanceof IjkMediaPlayer) {
                    ((IjkMediaPlayer) mp).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0); //开启耗时。可考虑缓冲设置。
                    ((IjkMediaPlayer) mp).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
                }
            }
        }

        public InternalSurfaceHolder(@NonNull GLSurfaceRenderView glSurfaceView, @Nullable Surface surface) {
            mGLSurfaceView = glSurfaceView;
            mVRSurface = surface;
        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return mGLSurfaceView;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            if (mGLSurfaceView == null) {
                return null;
            }
            return mGLSurfaceView.getHolder();
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return null;
        }

        @Override
        public void onDestroy() {
            if(mGLSurfaceView!=null){
                mGLSurfaceView.onDestroy();
            }
        }

        @Override
        public void onResume() {
            if(mGLSurfaceView!=null){
                mGLSurfaceView.resume();
            }
        }

        @Override
        public void onPause() {
            if(mGLSurfaceView!=null){
                mGLSurfaceView.pause();
            }
        }

        @Nullable
        @Override
        public Surface openSurface() {
            if (mVRSurface != null) {
                return mVRSurface;
            }
            if (mGLSurfaceView == null)
                return null;
            return mGLSurfaceView.getHolder().getSurface();
        }
    }

    private static final class SurfaceCallback implements SurfaceHolder.Callback, MDVRLibrary.IOnSurfaceReadyCallback {
        private SurfaceHolder mSurfaceHolder;
        private boolean mIsFormatChanged;
        private int mFormat;
        private int mWidth;
        private int mHeight;

        private boolean mIsSurfaceCreate = false;

        private WeakReference<GLSurfaceRenderView> mWeakSurfaceView;
        private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();

        public SurfaceCallback(@NonNull GLSurfaceRenderView surfaceView) {
            mWeakSurfaceView = new WeakReference<GLSurfaceRenderView>(surfaceView);
        }

        public void addRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.put(callback, callback);
            ISurfaceHolder surfaceHolder = null;
            if (mSurfaceHolder != null) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight);
            }
            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                callback.onSurfaceChanged(surfaceHolder, mFormat, mWidth, mHeight);
            }
        }

        public void removeRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.remove(callback);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            mSurfaceHolder = holder;
//            mIsFormatChanged = false;
//            mFormat = 0;
//            mWidth = 0;
//            mHeight = 0;
//            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
//            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
//                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
//            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            mIsFormatChanged = false;
            mFormat = 0;
            mWidth = 0;
            mHeight = 0;
            mIsSurfaceCreate = false;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(surfaceHolder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            if (mIsSurfaceCreate) {
                mSurfaceHolder = holder;
                mIsFormatChanged = true;
                mFormat = format;
                mWidth = width;
                mHeight = height;
                ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceChanged(surfaceHolder, format, width, height);
                }
            }
        }

        @Override
        public void onSurfaceReady(Surface surface) {
            mIsFormatChanged = false;
            mFormat = 0;
            mWidth = 0;
            mHeight = 0;
            mIsSurfaceCreate = true;
            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), surface);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
            }
        }
    }
}
