package com.glextend.vrlib.plugins;

import android.content.Context;

import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.model.GLPosition;

/**
 * Created by  on 16/7/21.
 */
public abstract class GLAbsPlugin {

    private boolean mIsInit;

    GLPosition position = GLPosition.sOriginalPosition;

    public final void setup(Context context){
        if (!mIsInit){
            init(context);
            mIsInit = true;
        }
    }

    abstract protected void init(Context context);

    abstract public void beforeRenderer(int totalWidth, int totalHeight);

    abstract public void renderer(int index, int itemWidth, int itemHeight, GL360Director director);

    abstract public void destroy();

    protected GLPosition getModelPosition(){
        return position;
    }

    public void setModelPosition(GLPosition position) {
        this.position = position;
    }

    abstract protected boolean removable();

}
