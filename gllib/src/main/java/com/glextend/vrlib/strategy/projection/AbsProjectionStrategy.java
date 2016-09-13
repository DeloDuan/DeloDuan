package com.glextend.vrlib.strategy.projection;

import android.content.Context;

import com.glextend.vrlib.GL360DirectorFactory;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.strategy.IModeStrategy;

/**
 * Created by  on 16/6/25.
 */
public abstract class AbsProjectionStrategy implements IModeStrategy, IProjectionMode {
    @Override
    public void onResume(Context context) {
    }
    @Override
    public void onPause(Context context) {
    }
    protected GL360DirectorFactory hijackDirectorFactory(){ return null; }

    abstract GLAbsPlugin buildMainPlugin(GLMainPluginBuilder builder);
}
