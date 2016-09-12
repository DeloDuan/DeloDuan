package com.glextend.vrlib.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by  on 16/7/22.
 */
public class GLPluginManager {

    private static final String TAG = "GLPluginManager";

    private List<GLAbsPlugin> mList;

    public GLPluginManager() {
        mList = new CopyOnWriteArrayList<>();
    }

    public void add(GLAbsPlugin plugin){
        mList.add(plugin);
    }

    public List<GLAbsPlugin> getPlugins() {
        return mList;
    }

    public void remove(GLAbsPlugin plugin) {
        if (plugin != null){
            mList.remove(plugin);
        }
    }

    public void removeAll() {
        Iterator<GLAbsPlugin> iterator = mList.iterator();
        while (iterator.hasNext()){
            GLAbsPlugin plugin = iterator.next();
            if (plugin.removable()){
                mList.remove(plugin);
            }
        }
    }
}
