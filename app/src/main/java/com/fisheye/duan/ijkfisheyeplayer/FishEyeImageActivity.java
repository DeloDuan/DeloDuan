package com.fisheye.duan.ijkfisheyeplayer;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/9/10.
 */
public class FishEyeImageActivity  extends AppCompatActivity {

    @Bind(R.id.gl_image_view)
    GLSurfaceView mGLSurfaceView;

    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisheye_image);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");
        if (intent != null && intent.getData() != null) {
            String filename = intent.getData().getPath();
            if (filename != null) { //用户主动触发打开操作
                mFilePath = filename;
            }
        }
        //init gl
    }

    @OnClick(R.id.change_mode)
    void change_mode_click(){ //改变模式

    }

}
