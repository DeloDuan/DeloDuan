package com.glextend.vrlib.strategy.projection;

import android.app.Activity;

import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.GL360DirectorFactory;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.objects.GLObject3DHelper;
import com.glextend.vrlib.objects.GLStereoSphere3D;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.plugins.GLPanoramaPlugin;

/**
 * Created by  on 16/6/26.
 */
public class StereoSphereProjection extends AbsProjectionStrategy {

    private static class FixedDirectorFactory extends GL360DirectorFactory {
        @Override
        public GL360Director createDirector(int index) {
            return GL360Director.builder().build();
        }
    }

    private GLAbsObject3D object3D;

    @Override
    public void on(Activity activity) {
        object3D = new GLStereoSphere3D();
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
    protected GL360DirectorFactory hijackDirectorFactory() {
        return new FixedDirectorFactory();
    }

    @Override
    public GLAbsPlugin buildMainPlugin(GLMainPluginBuilder builder) {
        return new GLPanoramaPlugin(builder);
    }
}
