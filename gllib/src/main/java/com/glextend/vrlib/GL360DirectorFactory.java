package com.glextend.vrlib;

/**
 * Created by  on 16/3/13.
 */
public abstract class GL360DirectorFactory {
    abstract public GL360Director createDirector(int index);

    public static class DefaultImpl extends GL360DirectorFactory {
        @Override
        public GL360Director createDirector(int index) {
            switch (index){
                // case 1:   return GL360Director.builder().setEyeX(-2.0f).setLookX(-2.0f).build();
                default:  return GL360Director.builder().build();
            }
        }
    }
}
