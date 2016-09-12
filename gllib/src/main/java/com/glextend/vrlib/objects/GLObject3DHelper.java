package com.glextend.vrlib.objects;

import android.content.Context;

/**
 * Created by  on 16/4/24.
 */
public class GLObject3DHelper {

    public interface LoadComplete{
        void onComplete(GLAbsObject3D object3D);
    }

    public static void loadObj(final Context context, final GLAbsObject3D object3D){
        loadObj(context, object3D, null);
    }

    public static void loadObj(final Context context, final GLAbsObject3D object3D, final LoadComplete loadComplete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                object3D.executeLoad(context);
                if (loadComplete != null)
                    loadComplete.onComplete(object3D);
            }
        }).start();
    }
}
