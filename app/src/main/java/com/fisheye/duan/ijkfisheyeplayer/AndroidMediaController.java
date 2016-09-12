/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fisheye.duan.ijkfisheyeplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;

import com.glextend.vrlib.GLLibrary;
import com.xnxs.mediaplayer.widget.media.IMediaController;
import com.xnxs.mediaplayer.widget.media.MediaPlayerVRControl;

import java.util.ArrayList;

public class AndroidMediaController extends MediaController implements IMediaController {
    private ActionBar mActionBar;

    private MediaPlayerVRControl mediaPlayerVRControl;
    private Activity mActivity;

    private GLLibrary mMDVRLibrary;


    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AndroidMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        initView(context);
    }

    public AndroidMediaController(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mActivity = (Activity)context;
    }

    public void setSupportActionBar(@Nullable ActionBar actionBar) {
        mActionBar = actionBar;
        if (isShowing()) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    @Override
    public void show() {
        super.show();
        if (mActionBar != null)
            mActionBar.show();
    }

    @Override
    public void setShowPausePlayDelayTime(int delayTime) {
    }

    @Override
    public void showSeekSet(boolean forwardOrBack, int time) {
    }

    @Override
    public void showVolumeSet(int vol) {
    }

    @Override
    public void showBrightnessSet(int brightness) {
    }

    @Override
    public void hide() {
        super.hide();
        if (mActionBar != null)
            mActionBar.hide();
        for (View view : mShowOnceArray)
            view.setVisibility(View.GONE);
        mShowOnceArray.clear();
    }

    @Override
    public void setMediaPlayer(com.xnxs.mediaplayer.widget.media.MediaPlayerControl player) {
    }

    @Override
    public void setVRControl(MediaPlayerVRControl vrControl) {
        mediaPlayerVRControl = vrControl;
        mMDVRLibrary = mediaPlayerVRControl.bindVRLibrary(mActivity);
        attachVRControl();
    }


    private void attachVRControl() {
        mMDVRLibrary.switchInteractiveMode(mActivity, GLLibrary.INTERACTIVE_MODE_TOUCH);
        mMDVRLibrary.switchDisplayMode(mActivity, GLLibrary.DISPLAY_MODE_NORMAL);
        mMDVRLibrary.switchProjectionMode(mActivity, GLLibrary.PROJECTION_MODE_DOME180);
    }

    //----------
    // Extends
    //----------
    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    public void showOnce(@NonNull View view) {
        mShowOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }
}
