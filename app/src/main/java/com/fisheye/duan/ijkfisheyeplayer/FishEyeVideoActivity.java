package com.fisheye.duan.ijkfisheyeplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.xnxs.mediaplayer.widget.media.RawDataSourceProvider;
import com.xnxs.mediaplayer.widget.media.VRVideoView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2016/9/10.
 */
public class FishEyeVideoActivity extends AppCompatActivity {

    @Bind(R.id.gl_video_view)
    VRVideoView gl_video_view;

    private String mFilePath;

    private  AndroidMediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        setContentView(R.layout.activity_fisheye_video);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");

        if (intent != null && intent.getData() != null) {
            String filename = intent.getData().getPath();
            if (filename != null) { //用户主动触发打开操作
                mFilePath = filename;
            }
        }
        // init UI
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        gl_video_view.setMediaController(mMediaController);
        gl_video_view.setOnPreparedListener(onPreparedListener);
        // prefer mVideoPath
        if (mFilePath != null && mFilePath.length() > 8){
            gl_video_view.setVideoPath(mFilePath);
        }
        else{
            // 获取指定文件对应的AssetFileDescriptor
            gl_video_view.setDataSource(new RawDataSourceProvider(getResources().openRawResourceFd(R.raw.ts)));
        }

    }

    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(IMediaPlayer mp) {
            gl_video_view.start();
            SharedPreferences.Editor editor = FishEyeVideoActivity.this.getSharedPreferences("user",MODE_PRIVATE).edit();
            editor.putString("url", mFilePath);
            editor.commit();
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        gl_video_view.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }


    @Override
    protected void onResume() {
        super.onResume();
        gl_video_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gl_video_view.onPause();
    }

    @OnClick(R.id.change_mode)
    void change_mode_click(){ //改变模式
    }
}
