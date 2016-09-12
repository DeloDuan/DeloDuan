package com.glextend.vrlib.strategy;

import android.app.Activity;
import android.content.Context;

/**
 * Created by  on 16/3/19.
 */
public interface IModeStrategy {

    void on(Activity activity);

    void off(Activity activity);
    
    boolean isSupport(Activity activity);

    void onResume(Context context);

    void onPause(Context context);

}
