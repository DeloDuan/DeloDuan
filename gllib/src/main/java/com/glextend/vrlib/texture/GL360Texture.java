package com.glextend.vrlib.texture;

import com.glextend.vrlib.GL360Program;

/**
 * Created by  on 16/1/25.
 * copied from surfaceTexture
 * Created by nitro888 on 15. 4. 5..
 * https://github.com/Nitro888/NitroAction360
 */
public abstract class GL360Texture {
    private static final int TEXTURE_EMPTY = 0;
    private static final String TAG = "GL360Texture";
    private int mTextureId = TEXTURE_EMPTY;
    public GL360Texture() {
    }

    // may called from multi thread
    public void create() {
        int glTexture = createTextureId();

        if (glTexture != TEXTURE_EMPTY){
            mTextureId = glTexture;
        }
    }

    abstract public boolean isReady();

    abstract public void destroy();

    abstract public void release();

    public int getCurrentTextureId(){
        return mTextureId;
    }

    final protected boolean isEmpty(int textureId){
        return textureId == TEXTURE_EMPTY;
    }

    abstract protected int createTextureId();

    abstract public boolean texture(GL360Program program);
}
