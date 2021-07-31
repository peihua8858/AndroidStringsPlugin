package com.fz.plugin.bean;

import java.io.Serializable;

public class ElementBean implements Serializable {
    private String key;
    private String value;

    public ElementBean() {
    }

    public ElementBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
