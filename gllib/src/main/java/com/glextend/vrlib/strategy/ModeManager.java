package com.glextend.vrlib.strategy;

import android.app.Activity;
import android.content.Context;

import com.glextend.vrlib.GLLibrary;

import java.util.Arrays;

/**
 * Created by  on 16/3/19.
 */
public abstract class ModeManager<T extends IModeStrategy> {
    private int mMode;
    private T mStrategy;
    private boolean mIsResumed;
    private GLLibrary.INotSupportCallback mCallback;

    public ModeManager(int mode) {
        this.mMode = mode;
    }

    /**
     * must call after new instance
     * @param activity activity
     */
    public void prepare(Activity activity, GLLibrary.INotSupportCallback callback){
        mCallback = callback;
        initMode(activity,mMode);
    }

    abstract protected T createStrategy(int mode);

    private void initMode(Activity activity, int mode){
        if (mStrategy != null){
            off(activity);
        }
        mStrategy = createStrategy(mode);
        if (!mStrategy.isSupport(activity)){
            if (mCallback != null) mCallback.onNotSupport(mode);
        } else {
            on(activity);
        }
    }

    public void switchMode(Activity activity, int mode){
        if (mode == getMode()) return;
        mMode = mode;
        initMode(activity,mMode);
    }

    public void on(Activity activity) {
        if (mStrategy.isSupport(activity))
            mStrategy.on(activity);
    }

    public void off(Activity activity) {
        if (mStrategy.isSupport(activity))
            mStrategy.off(activity);
    }

    protected T getStrategy() {
        return mStrategy;
    }

    public int getMode() {
        return mMode;
    }

    public void onResume(Context context) {
        mIsResumed = true;
        if (getStrategy().isSupport((Activity)context)){
            getStrategy().onResume(context);
        }
    }

    public void onPause(Context context) {
        mIsResumed = false;
        if (getStrategy().isSupport((Activity)context)){
            getStrategy().onPause(context);
        }
    }

    public boolean isResumed() {
        return mIsResumed;
    }
}
