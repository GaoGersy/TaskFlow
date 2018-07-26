package com.piesat.project.datahandle.plugin;

public interface Plugin<R,T> {
    R excute() throws Exception;
    void setParameter(T t);
}
