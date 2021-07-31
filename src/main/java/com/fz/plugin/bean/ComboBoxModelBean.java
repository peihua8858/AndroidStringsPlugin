package com.fz.plugin.bean;

import java.util.Objects;

public class ComboBoxModelBean {
    private String oriFilePath;
    private String showPath;

    public ComboBoxModelBean() {
    }

    public ComboBoxModelBean(ComboBoxModelBean o) {
        this.oriFilePath = o.oriFilePath;
        this.showPath = o.showPath;
    }

    public ComboBoxModelBean(String oriFilePath, String showPath) {
        this.oriFilePath = oriFilePath;
        this.showPath = showPath;
    }

    public String getOriFilePath() {
        return oriFilePath;
    }

    public void setOriFilePath(String oriFilePath) {
        this.oriFilePath = oriFilePath;
    }

    public String getShowPath() {
        return showPath;
    }

    public void setShowPath(String showPath) {
        this.showPath = showPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComboBoxModelBean modelBean = (ComboBoxModelBean) o;

        if (!Objects.equals(oriFilePath, modelBean.oriFilePath))
            return false;
        return Objects.equals(showPath, modelBean.showPath);
    }

    @Override
    public int hashCode() {
        int result = oriFilePath != null ? oriFilePath.hashCode() : 0;
        result = 31 * result + (showPath != null ? showPath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return showPath;
    }
}
