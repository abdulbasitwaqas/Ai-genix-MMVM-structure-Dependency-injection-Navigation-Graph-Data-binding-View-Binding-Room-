package com.jsbl.genix.Encrpition;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendEncryptionRequest {

    @SerializedName("Text")
    @Expose
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
