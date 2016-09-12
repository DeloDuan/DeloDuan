package com.glextend.vrlib.texture;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.glextend.vrlib.GL360Program;
import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.common.UIHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.glextend.vrlib.common.GLUtil.glCheck;
import static com.glextend.vrlib.common.VRUtil.notNull;

/**
 * Created by  on 16/4/5.
 */
public class GL360BitmapTexture extends GL360Texture {

    private static final String TAG = "GL360BitmapTexture";
    private GLLibrary.IBitmapProvider mBitmapProvider;
    private Map<String,AsyncCallback> mCallbackList = new HashMap<>();
    private boolean mIsReady;

    public GL360BitmapTexture(GLLibrary.IBitmapProvider bitmapProvider) {
        this.mBitmapProvider = bitmapProvider;
    }

    @Override
    protected int createTextureId() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        final int textureId = textureHandle[0];

        final AsyncCallback callback = new AsyncCallback();

        // save to thread local
        mCallbackList.put(Thread.currentThread().toString(),callback);

        // call the provider
        // to load the bitmap.
        UIHandler.sharedHandler().post(new Runnable() {
            @Override
            public void run() {
                mBitmapProvider.onProvideBitmap(callback);
            }
        });

        return textureId;
    }

    @Override
    public boolean texture(GL360Program program) {
        AsyncCallback asyncCallback = mCallbackList.get(Thread.currentThread().toString());
        int textureId = getCurrentTextureId();
        if (asyncCallback != null && asyncCallback.hasBitmap()){
            Bitmap bitmap = asyncCallback.getBitmap();
            textureInThread(textureId,program,bitmap);
            asyncCallback.releaseBitmap();
            mIsReady = true;
        }

        if (isReady() && textureId != 0){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(program.getTextureUniformHandle(),0);
        }
        return true;
    }

    @Override
    public boolean isReady() {
        return mIsReady;
    }

    @Override
    public void destroy() {
        Collection<AsyncCallback> callbacks = mCallbackList.values();
        for (AsyncCallback callback:callbacks){
            callback.releaseBitmap();
        }
        mCallbackList.clear();
    }

    @Override
    public void release() {
    }

    private void textureInThread(int textureId, GL360Program program, Bitmap bitmap) {
        notNull(bitmap,"bitmap can't be null!");

        if (isEmpty(textureId)) return;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        glCheck("GL360BitmapTexture glActiveTexture");

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        glCheck("GL360BitmapTexture glBindTexture");

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        glCheck("GL360BitmapTexture texImage2D");

        GLES20.glUniform1i(program.getTextureUniformHandle(),0);
        glCheck("GL360BitmapTexture textureInThread");
    }

    private static class AsyncCallback implements Callback{
        private Bitmap bitmap;

        @Override
        public void texture(Bitmap bitmap) {
            this.bitmap = bitmap.copy(bitmap.getConfig(),true);
        }

        public Bitmap getBitmap(){
            return bitmap;
        }

        public boolean hasBitmap(){
            return bitmap != null;
        }

        synchronized public void releaseBitmap(){
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
            bitmap = null;
        }
    }

    public interface Callback {
        void texture(Bitmap bitmap);
    }
}
