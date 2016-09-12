package com.glextend.vrlib.strategy.interactive;

import android.content.Context;

/**
 * Created by  on 16/3/19.
 */
public interface IInteractiveMode {

    void onResume(Context context);

    void onPause(Context context);

    boolean handleDrag(int distanceX, int distanceY);
}
