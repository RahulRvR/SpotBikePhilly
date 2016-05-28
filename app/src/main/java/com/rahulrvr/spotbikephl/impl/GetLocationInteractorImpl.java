package com.rahulrvr.spotbikephl.impl;

import android.content.Context;

import com.rahulrvr.spotbikephl.BuildConfig;
import com.rahulrvr.spotbikephl.api.BikeLocationApi;
import com.rahulrvr.spotbikephl.interfaces.GetLocationInteractor;
import com.rahulrvr.spotbikephl.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephl.pojo.BikeLocation;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
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
                if(e instanceof RetrofitError) {
                    RetrofitError retrofitError = (RetrofitError)e;
                    mPresenter.onError(retrofitError.getKind());
                } else {
                    mPresenter.onError(null);
                }
            }

            @Override
            public void onNext(BikeLocation bikeLocation) {
                mPresenter.hideProgressBar();
                mPresenter.onLocationReceived(bikeLocation.getFeatures());
            }
        });

    }
}
