package com.glextend.vrlib;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.glextend.vrlib.common.Fps;
import com.glextend.vrlib.plugins.GLPluginManager;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.strategy.display.DisplayModeManager;
import com.glextend.vrlib.strategy.projection.ProjectionModeManager;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.glextend.vrlib.common.GLUtil.glCheck;

/**
 * Created by  on 16/1/22.
 * @see Builder
 * @see #with(Context)
 */
public class GL360Renderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GL360Renderer";
    private DisplayModeManager mDisplayModeManager;
    private ProjectionModeManager mProjectionModeManager;
    private GLPluginManager mPluginManager;
    private Fps mFps = new Fps();
    private int mWidth;
    private int mHeight;

    // private MDBarrelDistortionPlugin mBarrelDistortionPlugin;

    // final
    private final Context mContext;

    private GL360Renderer(Builder params) {
        mContext = params.context;
        mDisplayModeManager = params.displayModeManager;
        mProjectionModeManager = params.projectionModeManager;
        mPluginManager = params.pluginManager;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // enable depth testing
        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        glCheck("GL360Renderer onDrawFrame 1");

        int size = mDisplayModeManager.getVisibleSize();

        int width = (int) (this.mWidth * 1.0f / size);
        int height = mHeight;


        List<GL360Director> directors = mProjectionModeManager.getDirectors();

        // main plugin
        GLAbsPlugin mainPlugin = mProjectionModeManager.getMainPlugin();
        if (mainPlugin != null) {
            mainPlugin.setup(mContext);
            mainPlugin.beforeRenderer(this.mWidth, this.mHeight);
        }

        for (GLAbsPlugin plugin : mPluginManager.getPlugins()) {
            plugin.setup(mContext);
            plugin.beforeRenderer(this.mWidth, this.mHeight);
        }

        for (int i = 0; i < size; i++) {
            if (i >= directors.size()) break;

            GL360Director director = directors.get(i);
            GLES20.glViewport(width * i, 0, width, height);
            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(width * i, 0, width, height);

            if (mainPlugin != null) {
                mainPlugin.renderer(i, width, height, director);
            }

            for (GLAbsPlugin plugin : mPluginManager.getPlugins()) {
                plugin.renderer(i, width, height, director);
            }

            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }

    }

    public static Builder with(Context context) {
        Builder builder = new Builder();
        builder.context = context;
        return builder;
    }

    public static class Builder {
        private Context context;
        private DisplayModeManager displayModeManager;
        private ProjectionModeManager projectionModeManager;
        public GLPluginManager pluginManager;

        private Builder() {
        }

        public GL360Renderer build() {
            return new GL360Renderer(this);
        }

        public Builder setPluginManager(GLPluginManager pluginManager) {
            this.pluginManager = pluginManager;
            return this;
        }

        public Builder setDisplayModeManager(DisplayModeManager displayModeManager) {
            this.displayModeManager = displayModeManager;
            return this;
        }

        public Builder setProjectionModeManager(ProjectionModeManager projectionModeManager) {
            this.projectionModeManager = projectionModeManager;
            return this;
        }
    }
}
