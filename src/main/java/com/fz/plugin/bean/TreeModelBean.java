package com.fz.plugin.bean;

import java.io.File;
import java.io.Serializable;

public class TreeModelBean implements Serializable {
    private String fileName;
    private File file;
    private String lang;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
