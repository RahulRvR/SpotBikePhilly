package com.rahulrvr.spotbikephilly.interfaces;

import com.rahulrvr.spotbikephilly.pojo.Feature;

import java.util.List;

/**
 *
 *
 */
public interface GetLocationView {

    void onLocationsReceived(List<Feature> locations);

    void showProgressBar();

    void hideProgressBar();

    void onError();
}
