package com.rahulrvr.spotbikephl.interfaces;

import android.content.Context;

import com.rahulrvr.spotbikephl.pojo.Feature;

import java.util.List;

import retrofit.RetrofitError;

/**
 *
 *
 */
public interface GetLocationPresenter {


    void getLocations(Context context);

    void onLocationReceived(List<Feature> list);

    void showProgressBar();

    void hideProgressBar();

    void onError(RetrofitError.Kind kind);

}
