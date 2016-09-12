package com.glextend.vrlib.strategy.display;

import android.app.Activity;

/**
 * Created by  on 16/3/19.
 */
public class GlassStrategy extends AbsDisplayStrategy {

    public GlassStrategy() {}

    @Override
    public void on(Activity activity) {}

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public int getVisibleSize() {
        return 2;
    }
}
