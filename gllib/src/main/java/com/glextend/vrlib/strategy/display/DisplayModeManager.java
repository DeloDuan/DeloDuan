package com.glextend.vrlib.strategy.display;

import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.model.BarrelDistortionConfig;
import com.glextend.vrlib.strategy.ModeManager;

/**
 * Created by  on 16/3/19.
 */
public class DisplayModeManager extends ModeManager<AbsDisplayStrategy> implements IDisplayMode {


    private boolean antiDistortionEnabled;
    private BarrelDistortionConfig barrelDistortionConfig;

    public DisplayModeManager() {
        super();
    }

    @Override
    protected AbsDisplayStrategy createStrategy() {
        return new NormalStrategy();
    }

    @Override
    public int getVisibleSize() {
        return getStrategy().getVisibleSize();
    }

    public void setAntiDistortionEnabled(boolean antiDistortionEnabled) {
        this.antiDistortionEnabled = antiDistortionEnabled;
    }

    public boolean isAntiDistortionEnabled() {
        return antiDistortionEnabled;
    }

    public void setBarrelDistortionConfig(BarrelDistortionConfig barrelDistortionConfig) {
        this.barrelDistortionConfig = barrelDistortionConfig;
    }

    public BarrelDistortionConfig getBarrelDistortionConfig() {
        return barrelDistortionConfig;
    }
}
