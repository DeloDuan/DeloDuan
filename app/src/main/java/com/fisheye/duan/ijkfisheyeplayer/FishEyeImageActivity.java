package com.fisheye.duan.ijkfisheyeplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.strategy.projection.DomeProjection;
import com.glextend.vrlib.texture.GL360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Administrator on 2016/9/10.
 */
public class FishEyeImageActivity  extends AppCompatActivity  implements GLLibrary.IBitmapProvider{

    @Bind(R.id.gl_image_view)
    GLSurfaceView gl_image_view;

    private String mFilePath;
    private GLLibrary mGLLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisheye_image);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");
        if (intent != null && intent.getData() != null) {
            String filename = intent.getData().getPath();
            if (filename != null) { //用户主动触发打开操作
                mFilePath = "file://" + filename;
            }
        }
        //init gl
        bindVRLibrary();
    }

    private Target mTarget;// keep the reference for picasso.

    private void loadImage(String uri, final GL360BitmapTexture.Callback callback){
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // notify if size changed
                getVRLibrary().onTextureResize(bitmap.getWidth(),bitmap.getHeight());
                // texture
                callback.texture(bitmap);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(getApplicationContext()).load(uri).resize(3072,2048).centerInside().memoryPolicy(NO_CACHE, NO_STORE).into(mTarget);
    }


    private GLLibrary getVRLibrary() {
        return mGLLibrary;
    }

    public GLLibrary bindVRLibrary() {
        mGLLibrary = GLLibrary.with(this)
                .pinchEnabled(true)
                .asBitmap(this)
                .build(gl_image_view);
        return mGLLibrary;
    }

    @Override
    public void onProvideBitmap(GL360BitmapTexture.Callback callback) {
        loadImage(mFilePath, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLLibrary.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mGLLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLLibrary.onDestroy();
    }

    @OnClick(R.id.change_mode)
    void change_mode_click(){ //改变模式
    }
}
