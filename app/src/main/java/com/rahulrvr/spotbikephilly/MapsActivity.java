package com.rahulrvr.spotbikephilly;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rahulrvr.spotbikephilly.impl.GetLocationPresenterImpl;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephilly.interfaces.GetLocationView;
import com.rahulrvr.spotbikephilly.pojo.Feature;
import com.rahulrvr.spotbikephilly.pojo.Geometry;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MapsActivity extends AppCompatActivity implements GetLocationView {

    private static final int DEFAULT_ZOOM = 15;
    private static final int MAX_DISTANCE_SEARCH = 3; //miles

    @InjectView(R.id.txtAddress)
    RobotoTextView txtAddress;
    @InjectView(R.id.txtFreeDocks)
    RobotoTextView txtFreeDocks;
    @InjectView(R.id.txtBikeAvl)
    RobotoTextView txtBikeAvl;
    @InjectView(R.id.bikeInfoWindow)
    RelativeLayout bikeInfoWindow;
    @InjectView(R.id.searchDistance)
    SeekBar searchDistance;
    @InjectView(R.id.txtTotalDocks)
    RobotoTextView txtTotalDocks;
    @InjectView(R.id.exploreAll)
    Switch exploreAll;
    @InjectView(R.id.searchBikeWindow)
    RelativeLayout searchBikeWindow;
    @InjectView(R.id.mainLayout)
    FrameLayout mainLayout;

    private Observable<Feature> mLocationObservable;

    GetLocationPresenter mPresenter;
    Location mCurrentLocation;
    boolean mShowAll = false;
    HashMap<Marker, Object> mMarkers = new HashMap<Marker, Object>();


    @InjectView(R.id.fab)
    FloatingActionButton fab;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        setUpMapIfNeeded();
        mPresenter = new GetLocationPresenterImpl(this);

        searchDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setLocationsOnMap(getMilesFromProgress(seekBar.getProgress()));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mPresenter.getLocations(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCurrentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        setMapZoom(0);
    }


    @OnClick(R.id.fab)
    public void navigate(View view) {
        //TODO change coordinates
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        startActivity(intent);

    }

    @OnClick(R.id.txtTotalDocks)
    public void onitem(View view) {
        //TODO remove this and add it to on marker click
        bikeInfoWindow.setVisibility(View.VISIBLE);
        searchBikeWindow.animate().translationYBy(searchBikeWindow.getHeight()).start();
        fab.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.exploreAll)
    public void showAll(View view) {
        //TODO show all locations
    }

    @Override
    public void onBackPressed() {
        if (bikeInfoWindow.isShown()) {
            bikeInfoWindow.setVisibility(View.GONE);
            searchBikeWindow.animate().translationYBy(-searchBikeWindow.getHeight()).start();
            fab.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationsReceived(List<Feature> locations) {
        mLocationObservable = Observable.from(locations);
        Toast.makeText(this, Integer.toString(locations.size()), Toast.LENGTH_LONG).show();
        searchDistance.setProgress(getProgressFromMiles(0.3f));
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void onError() {

    }

    private void setLocationsOnMap(final float distRange) {
        clearMarkers();
        mLocationObservable.filter(new Func1<Feature, Boolean>() {
            @Override
            public Boolean call(Feature feature) {
                Geometry geometry = feature.getGeometry();
                Double longitude = geometry.getCoordinates().get(0);
                Double latitude = geometry.getCoordinates().get(1);

                Location location = new Location("loc");
                location.setLongitude(longitude);
                location.setLatitude(latitude);
                float distance = mCurrentLocation.distanceTo(location);
                double distInMiles = distance / 1609.34;
                return distInMiles <= distRange || mShowAll;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Feature>() {
            @Override
            public void call(Feature feature) {
                Geometry geometry = feature.getGeometry();
                Double longitude = geometry.getCoordinates().get(0);
                Double latitude = geometry.getCoordinates().get(1);

                final LatLng point = new LatLng(latitude, longitude);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .position(point));
                mMarkers.put(marker, feature);
                setMapZoom((int) distRange);

            }
        });
    }

    private float getMilesFromProgress(int progress) {
        float miles = progress * MAX_DISTANCE_SEARCH;
        miles = miles / 100;
        return miles;
    }

    private int getProgressFromMiles(float miles) {
        return (int) ((miles * 100) / MAX_DISTANCE_SEARCH);
    }

    public void clearMarkers() {
        if (mMap != null) {
            mMap.clear();
            mMarkers.clear();
        }

    }

    private void setMapZoom(int zoomBy) {
        final LatLng mCurrentPos = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPos, DEFAULT_ZOOM - zoomBy));
    }
}
