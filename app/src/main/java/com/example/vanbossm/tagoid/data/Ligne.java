package com.example.vanbossm.tagoid.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ligne {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("shortName")
    @Expose
    private String shortName;

    @SerializedName("longName")
    @Expose
    private String longName;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("textColor")
    @Expose
    private String textColor;

    @SerializedName("mode")
    @Expose
    private String mode;

    @SerializedName("type")
    @Expose
    private String type;

    public Ligne() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
