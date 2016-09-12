package com.glextend.vrlib.model;

import com.glextend.vrlib.GL360Renderer;
import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.strategy.projection.ProjectionModeManager;
import com.glextend.vrlib.texture.GL360Texture;

/**
 * Created by  on 16/8/20.
 */
public class GLMainPluginBuilder {
    private GL360Texture texture;
    private int contentType = GLLibrary.ContentType.DEFAULT;
    private ProjectionModeManager projectionModeManager;

    public GLMainPluginBuilder() {
    }

    public GL360Texture getTexture() {
        return texture;
    }

    public int getContentType() {
        return contentType;
    }

    public ProjectionModeManager getProjectionModeManager() {
        return projectionModeManager;
    }


    public GLMainPluginBuilder setContentType(int contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * set surface{@link GL360Texture} to this render
     * @param texture {@link GL360Texture} surface may used by multiple render{@link GL360Renderer}
     * @return builder
     */
    public GLMainPluginBuilder setTexture(GL360Texture texture){
        this.texture = texture;
        return this;
    }

    public GLMainPluginBuilder setProjectionModeManager(ProjectionModeManager projectionModeManager) {
        this.projectionModeManager = projectionModeManager;
        return this;
    }
}
