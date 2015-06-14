package com.rahulrvr.spotbikephilly.api;

import com.rahulrvr.spotbikephilly.pojo.BikeLocation;

import retrofit.http.GET;
import rx.Observable;

/**
 *
 *
 */
public interface BikeLocationApi {

    @GET("/bike-share-stations/v1")
    Observable<BikeLocation> getBikeLocation();

}
