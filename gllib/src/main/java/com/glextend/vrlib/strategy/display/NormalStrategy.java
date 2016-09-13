package com.glextend.vrlib.strategy.display;

import android.app.Activity;
import android.content.Context;

/**
 * Created by  on 16/3/19.
 */
public class NormalStrategy extends AbsDisplayStrategy {

    @Override
    public void on(Activity activity) {}

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public void onResume(Context context) {
    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public int getVisibleSize() {
        return 1;
    }
}
