package com.rahulrvr.spotbikephilly.pojo;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class BikeLocation {

    @Expose
    private List<Feature> features = new ArrayList<Feature>();
    @Expose
    private String type;

    /**
     *
     * @return
     * The features
     */
    public List<Feature> getFeatures() {
        return features;
    }

    /**
     *
     * @param features
     * The features
     */
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

}
