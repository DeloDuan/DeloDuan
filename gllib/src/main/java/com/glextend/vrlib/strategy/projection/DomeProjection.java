package com.glextend.vrlib.strategy.projection;

import android.app.Activity;
import android.graphics.RectF;

import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.objects.GLDome3D;
import com.glextend.vrlib.objects.GLObject3DHelper;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.plugins.GLPanoramaPlugin;

/**
 * Created by  on 16/6/25.
 */
public class DomeProjection extends AbsProjectionStrategy {

    GLAbsObject3D object3D;

    private float mDegree;

    private boolean mIsUpper;

    private RectF mTextureSize;

    public DomeProjection(RectF textureSize, float degree, boolean isUpper) {
        this.mTextureSize = textureSize;
        this.mDegree = degree;
        this.mIsUpper = isUpper;
    }

    @Override
    public void on(Activity activity) {
        object3D = new GLDome3D(mTextureSize, mDegree, mIsUpper);
        GLObject3DHelper.loadObj(activity, object3D);
    }

    @Override
    public void off(Activity activity) {
    }

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public GLAbsObject3D getObject3D() {
        return object3D;
    }

    @Override
    public GLPosition getModelPosition() {
        return GLPosition.sOriginalPosition;
    }

    @Override
    public GLAbsPlugin buildMainPlugin(GLMainPluginBuilder builder) {
        return new GLPanoramaPlugin(builder);
    }
}
