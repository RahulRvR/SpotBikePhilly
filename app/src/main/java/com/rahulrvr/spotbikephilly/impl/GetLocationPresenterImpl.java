package com.rahulrvr.spotbikephilly.impl;

import android.content.Context;

import com.rahulrvr.spotbikephilly.interfaces.GetLocationInteractor;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationView;
import com.rahulrvr.spotbikephilly.pojo.Feature;

import java.util.List;

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
    public void onError() {
        mView.onError();
    }
}
