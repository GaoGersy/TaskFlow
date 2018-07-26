package com.piesat.project.datahandle.plugin;

import com.piesat.project.common.basehandle.filefilter.FileNameFilter;
import com.piesat.project.common.utils.SuperLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ScanPlugin implements Plugin<List<File>,File> {

    private File mFile;

    @Override
    public List<File> excute() {
        return scanFiles(null, mFile);
    }

    @Override
    public void setParameter(File file) {
        mFile = file;
    }

    private List<File> scanFiles(List<File> fileList, File file) {
        if (!file.exists()) {
            SuperLogger.e("文件不存在");
            return null;
        }
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];
                if (file1.isDirectory()) {
                    scanFiles(fileList, file1);
                } else {
                    if (getFileNameFilter() == null || getFileNameFilter().accept(file1.getName())) {
                        fileList.add(file1);
                    }
                }
            }
        } else {
            fileList.add(file);
        }
        return fileList;
    }

    public abstract FileNameFilter getFileNameFilter();
}
