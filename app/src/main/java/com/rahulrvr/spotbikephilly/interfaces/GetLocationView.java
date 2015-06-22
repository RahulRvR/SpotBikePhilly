package com.rahulrvr.spotbikephilly.interfaces;

import com.rahulrvr.spotbikephilly.pojo.Feature;

import java.util.List;

import retrofit.RetrofitError;

/**
 *
 *
 */
public interface GetLocationView {

    void onLocationsReceived(List<Feature> locations);

    void showProgressBar();

    void hideProgressBar();

    void onError(RetrofitError.Kind kind);
}
