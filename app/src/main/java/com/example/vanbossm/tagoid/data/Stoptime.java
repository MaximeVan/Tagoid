package com.example.vanbossm.tagoid.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Stoptime {

    @SerializedName("pattern")
    @Expose
    private Pattern pattern;
    @SerializedName("times")
    @Expose
    private List<Time> times = null;

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<Time> getTimes() {
        return times;
    }

    public void setTimes(List<Time> times) {
        this.times = times;
    }
}
