package com.fisheye.duan.ijkfisheyeplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.url_editText)
    EditText url_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        url_editText.setText("/storage/emulated/0/tencent/QQfile_recv/ts.mp4");
//        url_editText.setText("/storage/emulated/0/tencent/QQ_Images/yuyan.jpg");
        ///mnt/sdcard/Download/ts.mp4
        String url = getPreferencesUrl();
        if(url.length() > 0){
            url_editText.setText(url);
        }
        url_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url_editText.setHint(null);
            }
        });
    }

    private String getPreferencesUrl(){
        SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        String name=preferences.getString("url", "");
        return name;
    }

    @OnClick({R.id.video_play, R.id.image_show})
    public void openUrlButtonClick(View button){
        Intent intent;
        switch (button.getId()){
            case R.id.video_play:
                intent =new Intent(MainActivity.this,FishEyeVideoActivity.class);  //播放器1
				intent.putExtra("path", url_editText.getText().toString());
				MainActivity.this.startActivity(intent);
                break;
            case R.id.image_show:
                intent=new Intent(MainActivity.this,FishEyeImageActivity.class);  //图片展示
                intent.putExtra("path", url_editText.getText().toString());
                MainActivity.this.startActivity(intent);
                break;
        }
    }

}
