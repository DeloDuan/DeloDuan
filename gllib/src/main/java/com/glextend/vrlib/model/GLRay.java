package com.glextend.vrlib.model;

/**
 * Created by  on 16/8/5.
 */
public class GLRay {
    private GLVector3D mOrig;
    private GLVector3D mDir;

    public GLRay(GLVector3D mOrig, GLVector3D mDir) {
        this.mOrig = mOrig;
        this.mDir = mDir;
    }

    public GLVector3D getOrig() {
        return mOrig;
    }

    public void setOrig(GLVector3D mOrig) {
        this.mOrig = mOrig;
    }

    public GLVector3D getDir() {
        return mDir;
    }

    public void setDir(GLVector3D mDir) {
        this.mDir = mDir;
    }

    @Override
    public String toString() {
        return "GLRay{" +
                ", mDir=" + mDir +
                ", mOrig=" + mOrig +
                '}';
    }
}
