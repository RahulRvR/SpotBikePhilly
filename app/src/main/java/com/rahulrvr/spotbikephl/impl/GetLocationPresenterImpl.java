package com.rahulrvr.spotbikephl.impl;

import android.content.Context;

import com.rahulrvr.spotbikephl.interfaces.GetLocationInteractor;
import com.rahulrvr.spotbikephl.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephl.interfaces.GetLocationView;
import com.rahulrvr.spotbikephl.pojo.Feature;

import java.util.List;

import retrofit.RetrofitError;

/**
 *
 *
 */
public class GetLocationPresenterImpl implements GetLocationPresenter {
    
    GetLocationView mView;
    GetLocationInteractor mInteractor;

    public GetLocationPresenterImpl(GetLocationView view) {
        mView = view;
        mInteractor = new GetLocationInteractorImpl(this);
    }

    @Override
    public void getLocations(Context context) {
        mInteractor.getBikeLocations(context);
    }

    @Override
    public void onLocationReceived(List<Feature> list) {
        mView.onLocationsReceived(list);
    }

    @Override
    public void showProgressBar() {
        mView.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        mView.hideProgressBar();
    }



    @Override
    public void onError(RetrofitError.Kind kind) {
        mView.onError(kind);
    }
}
