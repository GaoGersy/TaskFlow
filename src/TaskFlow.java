package com.piesat.project.datahandle;

import com.piesat.project.datahandle.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskFlow {
    private List<Plugin> mPlugins;
    private File mFile;

    public static TaskFlow create() {
        return new TaskFlow();
    }

    public TaskFlow with(String path) {
        return with(new File(path));
    }

    public TaskFlow with(File file) {
        mFile = file;
        return this;
    }

    public <R,T> TaskFlow flatMap(final Plugin<List<R>,T> plugin) {
        FlatPlugin flatPlugin = new FlatPlugin<List<R>,T>() {
            @Override
            public List<R> excute() throws Exception {
                return plugin.excute();
            }

            @Override
            public void setParameter(T t) {
                plugin.setParameter(t);
            }
        };
        flatPlugin.setPlugin(plugin);
        doOnNext(flatPlugin);
        return this;
    }

    public <R,T> TaskFlow doOnNext(Plugin<R,T> plugin) {
        if (plugin==null){
            throw new RuntimeException("Plugin 不能为空");
        }
        if (mPlugins == null) {
            mPlugins = new ArrayList<>();
        }
        mPlugins.add(plugin);
        return this;
    }

    public void start(Excuter excuter) {
        if (excuter==null){
            throw new RuntimeException("Excuter 不能为空");
        }
        try {
            Object result = null;
            if (result == null) {
                result = mFile;
            }
            excute(excuter,mPlugins,result);
            excuter.onComplete();
        } catch (Exception e) {
            excuter.onError(e);
        }

    }

    private void excute(Excuter excuter,List<Plugin> plugins, Object result) throws Exception {
        for (int i = 0; i < plugins.size(); i++) {
            Plugin plugin = plugins.get(i);
            plugin.setParameter(result);
            if (plugin instanceof FlatPlugin){
                List list = (List) plugin.excute();
                List<Plugin> pluginList = plugins.subList(i+1,plugins.size());
                plugins = plugins.subList(0,i);
                for (int j = 0; j < list.size(); j++) {
                    result=list.get(j);
                    excute(excuter,pluginList,result);
                }
            }else {
                result = plugin.excute();
                excuter.onNext(result);
            }
        }
    }

    public abstract class FlatPlugin<R,T> implements Plugin<R,T>{
        private Plugin mPlugin;

        public void setPlugin(Plugin<R,T> plugin){
            mPlugin = plugin;
        }

        public Plugin<R,T> getPlugin() {
            return mPlugin;
        }
    }

    public static interface Excuter {

        void onNext(Object obj);

        void onError(Throwable e);

        void onComplete();
    }
}
