package com.glextend.vrlib.plugins;

import android.content.Context;

import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.GL360Program;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.strategy.projection.ProjectionModeManager;
import com.glextend.vrlib.texture.GL360Texture;

import static com.glextend.vrlib.common.GLUtil.glCheck;

/**
 * Created by  on 16/7/22.
 */
public class GLPanoramaPlugin extends GLAbsPlugin {

    private GL360Program mProgram;

    private GL360Texture mTexture;

    private ProjectionModeManager mProjectionModeManager;

    public GLPanoramaPlugin(GLMainPluginBuilder builder) {
        mTexture = builder.getTexture();
        mProgram = new GL360Program(builder.getContentType());
        mProjectionModeManager = builder.getProjectionModeManager();
    }

    @Override
    public void init(Context context) {
        mProgram.build(context);
        mTexture.create();
    }

    @Override
    public void beforeRenderer(int totalWidth, int totalHeight) {

    }

    @Override
    public void renderer(int index, int width, int height, GL360Director director) {

        GLAbsObject3D object3D = mProjectionModeManager.getObject3D();
        // check obj3d
        if (object3D == null) return;
        // Update Projection
        director.updateViewport(width, height);

        // Set our per-vertex lighting program.
        mProgram.use();
        glCheck("GLPanoramaPlugin mProgram use");

        mTexture.texture(mProgram);

        object3D.uploadVerticesBufferIfNeed(mProgram, index);

        object3D.uploadTexCoordinateBufferIfNeed(mProgram, index);

        // Pass in the combined matrix.
        director.shot(mProgram, getModelPosition());
        object3D.draw();
    }

    @Override
    public void destroy() {
        mTexture = null;
    }

    @Override
    protected GLPosition getModelPosition() {
        return mProjectionModeManager.getModelPosition();
    }

    @Override
    protected boolean removable() {
        return false;
    }

}
