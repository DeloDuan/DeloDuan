package com.glextend.vrlib.strategy.interactive;

import android.app.Activity;
import android.hardware.SensorEventListener;

import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.strategy.ModeManager;
import com.glextend.vrlib.strategy.projection.ProjectionModeManager;

/**
 * Created by  on 16/3/19.
 */
public class InteractiveModeManager extends ModeManager<AbsInteractiveStrategy> implements IInteractiveMode {

    public static class Params{
        public int mMotionDelay;
        public SensorEventListener mSensorListener;
        public ProjectionModeManager projectionModeManager;
    }

    private Params mParams;

    public InteractiveModeManager(Params params) {
        super();
        mParams = params;
    }


    @Override
    public void switchMode(Activity activity) {
        super.switchMode(activity);
        if (isResumed()) onResume(activity);
    }

    @Override
    protected AbsInteractiveStrategy createStrategy() {
        return new TouchStrategy(mParams);
    }

    /**
     * handle touch touch to rotate the model
     *
     * @param distanceX x
     * @param distanceY y
     * @return true if handled.
     */
    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        return getStrategy().handleDrag(distanceX,distanceY);
    }
}
