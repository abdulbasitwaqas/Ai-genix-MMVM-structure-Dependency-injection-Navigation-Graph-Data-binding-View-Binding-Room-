package com.jsbl.genix.model;

import java.io.File;

public class ParmsModel {
    private String parmsTag;
    private String parmsValue;
    private File file=null;


    public String getParmsTag() {
        return parmsTag;
    }

    public void setParmsTag(String parmsTag) {
        this.parmsTag = parmsTag;
    }

    public String getParmsValue() {
        return parmsValue;
    }

    public void setParmsValue(String parmsValue) {
        this.parmsValue = parmsValue;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
