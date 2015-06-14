package com.rahulrvr.spotbikephilly.interfaces;

import android.content.Context;

import com.rahulrvr.spotbikephilly.pojo.Feature;

import java.util.List;

/**
 *
 *
 */
public interface GetLocationPresenter {


    void getLocations(Context context);

    void onLocationReceived(List<Feature> list);

    void showProgressBar();

    void hideProgressBar();

    void onError();

}
