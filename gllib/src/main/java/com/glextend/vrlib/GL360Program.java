package com.glextend.vrlib;
import android.content.Context;
import android.opengl.GLES20;

import com.fisheye.duan.gllib.R;

import static com.glextend.vrlib.common.GLUtil.compileShader;
import static com.glextend.vrlib.common.GLUtil.createAndLinkProgram;
import static com.glextend.vrlib.common.GLUtil.readTextFileFromRaw;

/**
 * Created by  on 16/1/22.
 */
public class GL360Program {
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;

    private int mTextureUniformHandle;
    private int mPositionHandle;
    private int mTextureCoordinateHandle;
    private int mProgramHandle;
    private int mContentType;

    public GL360Program(int type) {
        mContentType = type;
    }

    /**
     * build the program
     *
     * 1. create a program handle
     * 2. compileShader
     * 3. link program
     * 4. get attribute handle && uniform handle
     * @param context
     */
    public void build(Context context){
        final String vertexShader = getVertexShader(context);
        final String fragmentShader = getFragmentShader(context);

        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[] {"a_Position", "a_TexCoordinate"});

        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    protected String getVertexShader(Context context){
        return readTextFileFromRaw(context, R.raw.per_pixel_vertex_shader);
    }

    protected String getFragmentShader(Context context){
        return FragmentShaderFactory.fs(context, mContentType);
    }

    public void use() {
        GLES20.glUseProgram(mProgramHandle);
    }

    public int getMVPMatrixHandle() {
        return mMVPMatrixHandle;
    }

    public int getMVMatrixHandle() {
        return mMVMatrixHandle;
    }

    public int getTextureUniformHandle() {
        return mTextureUniformHandle;
    }

    public int getPositionHandle() {
        return mPositionHandle;
    }

    public int getTextureCoordinateHandle() {
        return mTextureCoordinateHandle;
    }

    private static class FragmentShaderFactory{

        static String fs(Context context, int type){
            int resId;
            switch (type){
                case GLLibrary.ContentType.BITMAP:
                    resId = R.raw.per_pixel_fragment_shader_bitmap;
                    break;
                case GLLibrary.ContentType.VIDEO:
                default:
                    resId = R.raw.per_pixel_fragment_shader;
                    break;
            }
            return readTextFileFromRaw(context, resId);
        }
    }
}