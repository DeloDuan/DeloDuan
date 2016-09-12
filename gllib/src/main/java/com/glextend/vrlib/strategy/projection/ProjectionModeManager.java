package com.glextend.vrlib.strategy.projection;

import android.app.Activity;
import android.graphics.RectF;

import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.GL360DirectorFactory;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.strategy.ModeManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by  on 16/6/25.
 */
public class ProjectionModeManager extends ModeManager<AbsProjectionStrategy> implements IProjectionMode {
    public static class Params{
        public RectF textureSize;
        public GL360DirectorFactory directorFactory;
        public GLMainPluginBuilder mainPluginBuilder;
        public IMDProjectionFactory projectionFactory;
    }

    private List<GL360Director> mDirectors = new CopyOnWriteArrayList<>();

    private RectF mTextureSize;

    private GL360DirectorFactory mCustomDirectorFactory;

    private GLAbsPlugin mMainPlugin;

    private GLMainPluginBuilder mMainPluginBuilder;

    private IMDProjectionFactory mProjectionFactory;

    public ProjectionModeManager(int mode, Params projectionManagerParams) {
        super(mode);
        this.mTextureSize = projectionManagerParams.textureSize;
        this.mCustomDirectorFactory = projectionManagerParams.directorFactory;
        this.mProjectionFactory = projectionManagerParams.projectionFactory;
        this.mMainPluginBuilder = projectionManagerParams.mainPluginBuilder;
        this.mMainPluginBuilder.setProjectionModeManager(this);
    }

    public GLAbsPlugin getMainPlugin() {
        if (mMainPlugin == null){
            mMainPlugin = getStrategy().buildMainPlugin(mMainPluginBuilder);
        }
        return mMainPlugin;
    }

    @Override
    public void switchMode(Activity activity, int mode) {
        super.switchMode(activity, mode);
    }

    @Override
    public void on(Activity activity) {
        super.on(activity);

        // destroy prev main plugin
        if( mMainPlugin != null){
            mMainPlugin.destroy();
            mMainPlugin = null;
        }
        mDirectors.clear();
        GL360DirectorFactory factory = getStrategy().hijackDirectorFactory();
        factory = factory == null ? mCustomDirectorFactory : factory;

        for (int i = 0; i < GLLibrary.sMultiScreenSize; i++){
            mDirectors.add(factory.createDirector(i));
        }
    }

    @Override
    protected AbsProjectionStrategy createStrategy(int mode) {
        if (mProjectionFactory != null){
            AbsProjectionStrategy strategy = mProjectionFactory.createStrategy(mode);
            if (strategy != null) return strategy;
        }
        return new SphereProjection();
    }

    @Override
    public GLPosition getModelPosition() {
        return getStrategy().getModelPosition();
    }

    @Override
    public GLAbsObject3D getObject3D() {
        return getStrategy().getObject3D();
    }

    public List<GL360Director> getDirectors() {
        return mDirectors;
    }
}
