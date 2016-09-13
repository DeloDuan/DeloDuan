package com.glextend.vrlib.strategy;

import android.app.Activity;
import android.content.Context;

import com.glextend.vrlib.GLLibrary;

import java.util.Arrays;

/**
 * Created by  on 16/3/19.
 */
public abstract class ModeManager<T extends IModeStrategy> {
    private T mStrategy;
    private boolean mIsResumed;
    public ModeManager() {
    }
    /**
     * must call after new instance
     * @param activity activity
     */
    public void prepare(Activity activity){
        initMode(activity);
    }
    abstract protected T createStrategy();

    private void initMode(Activity activity){
        if (mStrategy != null){
            off(activity);
        }
        mStrategy = createStrategy();
        if (!mStrategy.isSupport(activity)){
        } else {
            on(activity);
        }
    }

    public void switchMode(Activity activity){
        initMode(activity);
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
