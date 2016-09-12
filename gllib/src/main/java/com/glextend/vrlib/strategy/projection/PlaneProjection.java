package com.glextend.vrlib.strategy.projection;

import android.app.Activity;
import android.graphics.RectF;
import android.opengl.Matrix;

import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.GLLibrary;
import com.glextend.vrlib.GL360DirectorFactory;
import com.glextend.vrlib.model.GLMainPluginBuilder;
import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;
import com.glextend.vrlib.objects.GLObject3DHelper;
import com.glextend.vrlib.objects.GLPlane;
import com.glextend.vrlib.plugins.GLAbsPlugin;
import com.glextend.vrlib.plugins.GLPanoramaPlugin;

/**
 * Created by  on 16/6/26.
 */
public class PlaneProjection extends AbsProjectionStrategy {

    private GLPlane object3D;

    private PlaneScaleCalculator planeScaleCalculator;

    private static final GLPosition position = GLPosition.newInstance().setZ(-2f);

    private PlaneProjection(PlaneScaleCalculator calculator) {
        planeScaleCalculator = calculator;
    }

    @Override
    public void on(Activity activity) {
        object3D = new GLPlane(planeScaleCalculator);
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
        return position;
    }

    @Override
    public GLAbsPlugin buildMainPlugin(GLMainPluginBuilder builder) {
        return new GLPanoramaPlugin(builder);
    }

    @Override
    protected GL360DirectorFactory hijackDirectorFactory() {
        return new OrthogonalDirectorFactory();
    }

    public static PlaneProjection create(int scaleType, RectF textureSize){
        return new PlaneProjection(new PlaneScaleCalculator(scaleType,textureSize));
    }

    public static class PlaneScaleCalculator{

        private static final float sBaseValue = 1.0f;

        private RectF mTextureSize;

        private float mViewportRatio;

        private int mScaleType;

        private float mViewportWidth = sBaseValue;

        private float mViewportHeight = sBaseValue;

        private float mTextureWidth = sBaseValue;

        private float mTextureHeight = sBaseValue;

        public PlaneScaleCalculator(int scaleType, RectF textureSize) {
            this.mScaleType = scaleType;
            this.mTextureSize = textureSize;
        }

        public float getTextureRatio(){
            return mTextureSize.width() / mTextureSize.height();
        }

        public void setViewportRatio(float viewportRatio){
            this.mViewportRatio = viewportRatio;
        }

        public void calculate(){
            float viewportRatio = mViewportRatio;
            float textureRatio = getTextureRatio();

            switch (this.mScaleType){
                case GLLibrary.PROJECTION_MODE_PLANE_FULL:
                    // fullscreen
                    mViewportWidth = mViewportHeight = mTextureWidth = mTextureHeight = sBaseValue;
                    break;
                case GLLibrary.PROJECTION_MODE_PLANE_CROP:
                    if (textureRatio  > viewportRatio){
                        /**
                         * crop width of texture
                         *
                         * texture
                         * ----------------------
                         * |    |          |    |
                         * |    | viewport |    |
                         * |    |          |    |
                         * ----------------------
                         * */
                        mViewportWidth = sBaseValue * viewportRatio;
                        mViewportHeight = sBaseValue;

                        mTextureWidth = sBaseValue * textureRatio;
                        mTextureHeight = sBaseValue;
                    } else {
                        /**
                         * crop height of texture
                         *
                         * texture
                         * -----------------------
                         * |---------------------|
                         * |                     |
                         * |      viewport       |
                         * |                     |
                         * |---------------------|
                         * -----------------------
                         * */
                        mViewportWidth = sBaseValue;
                        mViewportHeight = sBaseValue / viewportRatio;

                        mTextureWidth = sBaseValue;
                        mTextureHeight = sBaseValue / textureRatio;
                    }
                    break;
                case GLLibrary.PROJECTION_MODE_PLANE_FIT:
                default:
                    if (viewportRatio > textureRatio){
                        /**
                         * fit height of viewport
                         *
                         * viewport
                         * ---------------------
                         * |    |         |    |
                         * |    | texture |    |
                         * |    |         |    |
                         * ---------------------
                         * */
                        mViewportWidth = sBaseValue * viewportRatio ;
                        mViewportHeight = sBaseValue;

                        mTextureWidth = sBaseValue * textureRatio;
                        mTextureHeight = sBaseValue;
                    } else {
                        /**
                         * fit width of viewport
                         *
                         * viewport
                         * -----------------------
                         * |---------------------|
                         * |                     |
                         * |       texture       |
                         * |                     |
                         * |---------------------|
                         * -----------------------
                         * */
                        mViewportWidth = sBaseValue;
                        mViewportHeight = sBaseValue / viewportRatio;

                        mTextureWidth = sBaseValue;
                        mTextureHeight = sBaseValue / textureRatio;
                    }
                    break;
            }
        }

        public float getViewportWidth(){
            return mViewportWidth;
        }

        public float getViewportHeight(){
            return mViewportHeight;
        }

        public float getTextureWidth(){
            return mTextureWidth;
        }

        public float getTextureHeight(){
            return mTextureHeight;
        }
    }

    private class OrthogonalDirectorFactory extends GL360DirectorFactory {
        @Override
        public GL360Director createDirector(int index) {
            return new OrthogonalDirector(new GL360Director.Builder());
        }
    }

    private class OrthogonalDirector extends GL360Director {

        private OrthogonalDirector(Builder builder) {
            super(builder);
        }

        @Override
        public void setDeltaX(float mDeltaX) {
            // nop
        }

        @Override
        public void setDeltaY(float mDeltaY) {
            // nop
        }

        @Override
        public void updateSensorMatrix(float[] sensorMatrix) {
            // nop
        }

        @Override
        protected void updateProjection(){
            planeScaleCalculator.setViewportRatio(getRatio());
            planeScaleCalculator.calculate();
            final float left = - planeScaleCalculator.getViewportWidth()/2;
            final float right = planeScaleCalculator.getViewportWidth()/2;
            final float bottom = - planeScaleCalculator.getViewportHeight()/2;
            final float top = planeScaleCalculator.getViewportHeight()/2;
            final float far = 500;
            Matrix.orthoM(getProjectionMatrix(), 0, left, right, bottom, top, getNear(), far);
        }
    }

}
