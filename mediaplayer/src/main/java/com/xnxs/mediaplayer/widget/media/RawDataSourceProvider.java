package com.xnxs.mediaplayer.widget.media;

import android.content.res.AssetFileDescriptor;

import java.io.IOException;

import okio.Okio;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by Administrator on 2016/9/10.
 */
public class RawDataSourceProvider implements IMediaDataSource {

    AssetFileDescriptor mDescriptor;

    byte[]  mMediaBytes;

    long mPosition;

    public RawDataSourceProvider(AssetFileDescriptor descriptor) {
        this.mDescriptor = descriptor;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if(position + 1 >= mMediaBytes.length){
            return -1;
        }

        int length;
        if(position + size < mMediaBytes.length){
            length = size;
        }else{
            length = (int) (mMediaBytes.length - position);
            if(length > buffer.length)
                length = buffer.length ;

            length--;
        }

        System.arraycopy(mMediaBytes, (int) position, buffer, offset, length);
        mPosition = position;
        return length;
    }

    @Override
    public long getSize() throws IOException {
        long length  = mDescriptor.getLength();

        if(mMediaBytes == null){
            okio.Source source = Okio.source(mDescriptor.createInputStream());
            mMediaBytes = Okio.buffer(source).readByteArray();
        }


        return length;
    }

    @Override
    public void close() throws IOException {
        if(mDescriptor != null)
            mDescriptor.close();

        mDescriptor = null;
        mMediaBytes = null;
    }
}
