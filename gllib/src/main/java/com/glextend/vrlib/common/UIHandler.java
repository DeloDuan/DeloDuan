package com.glextend.vrlib.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by  on 16/8/4.
 */
public class UIHandler {

    private static Handler sMainHandler;

    public static void init(){
        if (sMainHandler == null){
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static Handler sharedHandler(){
        return sMainHandler;
    }
}
