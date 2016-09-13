package com.glextend.vrlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.glextend.vrlib.common.GLUtil;
import com.glextend.vrlib.common.UIHandler;
import com.glextend.vrlib.model.BarrelDistortionConfig;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.plugins.GLPluginManager;
import com.glextend.vrlib.strategy.display.DisplayModeManager;
import com.glextend.vrlib.strategy.interactive.InteractiveModeManager;
import com.glextend.vrlib.strategy.projection.IMDProjectionFactory;
import com.glextend.vrlib.strategy.projection.ProjectionModeManager;
import com.glextend.vrlib.texture.GL360BitmapTexture;
import com.glextend.vrlib.texture.GL360Texture;
import com.glextend.vrlib.texture.GL360VideoTexture;
import com.google.android.apps.muzei.render.GLTextureView;

import java.util.Iterator;
import java.util.List;

import static com.glextend.vrlib.common.VRUtil.notNull;

/**
 * Created by  on 16/3/12.
 */
public class GLLibrary {

    private static final String TAG = "GLLibrary";
    public static final int sMultiScreenSize = 2;

    private RectF mTextureSize = new RectF(0,0,1024,1024);
    private InteractiveModeManager mInteractiveModeManager;
    private DisplayModeManager mDisplayModeManager;
    private ProjectionModeManager mProjectionModeManager;
    private GLPluginManager mPluginManager;
    private GLScreenWrapper mScreenWrapper;
    private GLTouchHelper mTouchHelper;
    private GL360Texture mTexture;

    public static final int PROJECTION_MODE_PLANE_FIT = 207;
    public static final int PROJECTION_MODE_PLANE_CROP = 208;
    public static final int PROJECTION_MODE_PLANE_FULL = 209;

    private GLLibrary(Builder builder) {
        // init main handler
        UIHandler.init();

        // init mode manager
        initModeManager(builder);

        // init plugin manager
        initPluginManager(builder);

        // init glSurfaceViews
        initOpenGL(builder.activity, builder.screenWrapper);

        mTexture = builder.texture;
        mTouchHelper = new GLTouchHelper(builder.activity);
        mTouchHelper.setPinchEnabled(builder.pinchEnabled);
        mTouchHelper.setAdvanceGestureListener(new IAdvanceGestureListener() {
            @Override
            public void onDrag(float distanceX, float distanceY) {
                mInteractiveModeManager.handleDrag((int) distanceX,(int) distanceY);
            }
            @Override
            public void onPinch(float scale) {
                List<GL360Director> directors = mProjectionModeManager.getDirectors();
                for (GL360Director director : directors){
                    director.updateProjectionNearScale(scale);
                }
            }
        });
        mScreenWrapper.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchHelper.handleTouchEvent(event);
            }
        });
        // init picker manager
    }

    private void initModeManager(Builder builder) {
        // init ProjectionModeManager
        ProjectionModeManager.Params projectionManagerParams = new ProjectionModeManager.Params();
        projectionManagerParams.textureSize = mTextureSize;
        projectionManagerParams.directorFactory = builder.directorFactory;
        projectionManagerParams.projectionFactory = builder.projectionFactory;
        projectionManagerParams.mainPluginBuilder = new GLMainPluginBuilder()
                .setContentType(builder.contentType)
                .setTexture(builder.texture);

        mProjectionModeManager = new ProjectionModeManager( projectionManagerParams);
        mProjectionModeManager.prepare(builder.activity);

        // init DisplayModeManager
        mDisplayModeManager = new DisplayModeManager();
        mDisplayModeManager.setBarrelDistortionConfig(builder.barrelDistortionConfig);
        mDisplayModeManager.setAntiDistortionEnabled(builder.barrelDistortionConfig.isDefaultEnabled());
        mDisplayModeManager.prepare(builder.activity);

        // init InteractiveModeManager
        InteractiveModeManager.Params interactiveManagerParams = new InteractiveModeManager.Params();
        interactiveManagerParams.projectionModeManager = mProjectionModeManager;
        interactiveManagerParams.mMotionDelay = builder.motionDelay;
        interactiveManagerParams.mSensorListener = builder.sensorListener;
        mInteractiveModeManager = new InteractiveModeManager(interactiveManagerParams);
        mInteractiveModeManager.prepare(builder.activity);
    }

    private void initPluginManager(Builder builder) {
        mPluginManager = new GLPluginManager();
    }


    private void initOpenGL(Context context, GLScreenWrapper screenWrapper) {
        if (GLUtil.supportsEs2(context)) {
            screenWrapper.init(context);
            // Request an OpenGL ES 2.0 compatible context.

            GL360Renderer renderer = GL360Renderer.with(context)
                    .setPluginManager(mPluginManager)
                    .setProjectionModeManager(mProjectionModeManager)
                    .setDisplayModeManager(mDisplayModeManager)
                    .build();

            // Set the renderer to our demo renderer, defined below.
            screenWrapper.setRenderer(renderer);
            this.mScreenWrapper = screenWrapper;
        } else {
            this.mScreenWrapper.getView().setVisibility(View.GONE);
            Toast.makeText(context, "OpenGLES2 not supported.", Toast.LENGTH_SHORT).show();
        }
    }


    public void resetTouch(){
        List<GL360Director> directors = mProjectionModeManager.getDirectors();
        for (GL360Director director : directors){
            director.reset();
        }
    }

    public void resetPinch(){
        mTouchHelper.reset();
    }


    public void setAntiDistortionEnabled(boolean enabled){
        mDisplayModeManager.setAntiDistortionEnabled(enabled);
    }

    public boolean isAntiDistortionEnabled(){
        return mDisplayModeManager.isAntiDistortionEnabled();
    }

    public int getScreenSize(){
        return mDisplayModeManager.getVisibleSize();
    }

    public void addPlugin(GLAbsPlugin plugin){
        mPluginManager.add(plugin);
    }

    public void removePlugin(GLAbsPlugin plugin){
        mPluginManager.remove(plugin);
    }

    public void removePlugins(){
        mPluginManager.removeAll();
    }

    public void onTextureResize(float width, float height){
        mTextureSize.set(0,0,width,height);
    }

    public RectF getTextureSize(){
        return mTextureSize;
    }

    public void onResume(Context context){
        mInteractiveModeManager.onResume(context);
        if (mScreenWrapper != null){
            mScreenWrapper.onResume();
        }
    }

    public void onPause(Context context){
        mInteractiveModeManager.onPause(context);
        if (mScreenWrapper != null){
            mScreenWrapper.onPause();
        }
    }

    public void onDestroy(){
        Iterator<GLAbsPlugin> iterator = mPluginManager.getPlugins().iterator();
        while (iterator.hasNext()){
            GLAbsPlugin plugin = iterator.next();
            plugin.destroy();
        }

        GLAbsPlugin mainPlugin = mProjectionModeManager.getMainPlugin();
        if (mainPlugin != null){
            mainPlugin.destroy();
        }
        if (mTexture != null){
            mTexture.destroy();
            mTexture.release();
            mTexture = null;
        }
    }
    /**
     * handle touch touch to rotate the model
     * @deprecated deprecated since 2.0
     *
     * @param event
     * @return true if handled.
     */
    public boolean handleTouchEvent(MotionEvent event) {
        Log.e(TAG,"please remove the handleTouchEvent in activity!");
        return false;
    }
    public interface IOnSurfaceReadyCallback {
        void onSurfaceReady(Surface surface);
    }

    public interface IBitmapProvider {
        void onProvideBitmap(GL360BitmapTexture.Callback callback);
    }


    public interface IGestureListener {
        void onClick(MotionEvent e);
    }

    interface IAdvanceGestureListener {
        void onDrag(float distanceX, float distanceY);
        void onPinch(float scale);
    }

    public static Builder with(Activity activity){
        return new Builder(activity);
    }

    /**
     *
     */
    public static class Builder {
        private Activity activity;
        private int contentType = ContentType.DEFAULT;
        private GL360Texture texture;
        private boolean pinchEnabled; // default false.
        private BarrelDistortionConfig barrelDistortionConfig;
        private GL360DirectorFactory directorFactory;
        private int motionDelay = SensorManager.SENSOR_DELAY_GAME;
        private SensorEventListener sensorListener;
        private GLScreenWrapper screenWrapper;
        private IMDProjectionFactory projectionFactory;

        private Builder(Activity activity) {
            this.activity = activity;
        }


        public Builder asVideo(IOnSurfaceReadyCallback callback){
            texture = new GL360VideoTexture(callback);
            contentType = ContentType.VIDEO;
            return this;
        }

        public Builder asBitmap(IBitmapProvider bitmapProvider){
            notNull(bitmapProvider, "bitmap Provider can't be null!");
            texture = new GL360BitmapTexture(bitmapProvider);
            contentType = ContentType.BITMAP;
            return this;
        }
        /**
         * enable or disable the pinch gesture
         *
         * @param enabled default is false
         * @return builder
         */
        public Builder pinchEnabled(boolean enabled) {
            this.pinchEnabled = enabled;
            return this;
        }
        /**
         * sensor delay in motion mode.
         *
         * {@link android.hardware.SensorManager#SENSOR_DELAY_FASTEST}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_GAME}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_NORMAL}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_UI}
         *
         * @param motionDelay default is {@link android.hardware.SensorManager#SENSOR_DELAY_GAME}
         * @return builder
         */
        public Builder motionDelay(int motionDelay){
            this.motionDelay = motionDelay;
            return this;
        }

        public Builder sensorCallback(SensorEventListener callback){
            this.sensorListener = callback;
            return this;
        }

        public Builder directorFactory(GL360DirectorFactory directorFactory){
            this.directorFactory = directorFactory;
            return this;
        }

        public Builder projectionFactory(IMDProjectionFactory projectionFactory){
            this.projectionFactory = projectionFactory;
            return this;
        }

        public Builder barrelDistortionConfig(BarrelDistortionConfig config){
            this.barrelDistortionConfig = config;
            return this;
        }

        /**
         * build it!
         *
         * @param glViewId will find the GLSurfaceView by glViewId in the giving {@link #activity}
         *                 or find the GLTextureView by glViewId
         * @return vr lib
         */
        public GLLibrary build(int glViewId){
            View view = activity.findViewById(glViewId);
            if (view instanceof GLSurfaceView){
                return build((GLSurfaceView) view);
            } else if(view instanceof GLTextureView){
                return build((GLTextureView) view);
            } else {
                throw new RuntimeException("Please ensure the glViewId is instance of GLSurfaceView or GLTextureView");
            }
        }

        public GLLibrary build(GLSurfaceView glSurfaceView){
            return build(GLScreenWrapper.wrap(glSurfaceView));
        }

        public GLLibrary build(GLTextureView glTextureView){
            return build(GLScreenWrapper.wrap(glTextureView));
        }

        private GLLibrary build(GLScreenWrapper screenWrapper){
            notNull(texture,"You must call video/bitmap function before build");
            if (this.directorFactory == null) directorFactory = new GL360DirectorFactory.DefaultImpl();
            if (this.barrelDistortionConfig == null) barrelDistortionConfig = new BarrelDistortionConfig();
            this.screenWrapper = screenWrapper;
            return new GLLibrary(this);
        }
    }

    public interface ContentType{
        int VIDEO = 0;
        int BITMAP = 1;
        int DEFAULT = VIDEO;
    }
}
