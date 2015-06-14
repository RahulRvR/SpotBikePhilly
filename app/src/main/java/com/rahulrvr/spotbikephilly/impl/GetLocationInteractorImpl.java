package com.rahulrvr.spotbikephilly.impl;

import android.content.Context;

import com.rahulrvr.spotbikephilly.BuildConfig;
import com.rahulrvr.spotbikephilly.api.BikeLocationApi;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationInteractor;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephilly.pojo.BikeLocation;

import retrofit.RestAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 *
 *
 */
public class GetLocationInteractorImpl implements GetLocationInteractor{


    GetLocationPresenter mPresenter;

    public GetLocationInteractorImpl(GetLocationPresenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void getBikeLocations(Context context) {
        RestAdapter adapter = new RestAdapter.Builder().
                setEndpoint(BuildConfig.BASE_SERVER_ENDPOINT).build();

        BikeLocationApi api = adapter.create(BikeLocationApi.class);
        mPresenter.showProgressBar();
         api.getBikeLocation().observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<BikeLocation>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mPresenter.onError();
            }

            @Override
            public void onNext(BikeLocation bikeLocation) {
                mPresenter.hideProgressBar();
                mPresenter.onLocationReceived(bikeLocation.getFeatures());
            }
        });

    }
}
