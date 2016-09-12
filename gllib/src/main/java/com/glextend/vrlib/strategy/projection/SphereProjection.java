package com.glextend.vrlib.strategy.projection;

import android.app.Activity;

import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.objects.GLObject3DHelper;
import com.glextend.vrlib.objects.GLSphere3D;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.plugins.GLPanoramaPlugin;

/**
 * Created by delo on 16/6/25.
 */
public class SphereProjection extends AbsProjectionStrategy {

    private GLAbsObject3D object3D;

    public SphereProjection() {

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
    public void on(Activity activity) {
        object3D = new GLSphere3D();
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
    public GLAbsPlugin buildMainPlugin(GLMainPluginBuilder builder) {
        return new GLPanoramaPlugin(builder);
    }
}
