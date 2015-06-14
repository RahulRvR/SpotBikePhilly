package com.rahulrvr.spotbikephilly;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
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
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MapsActivity extends AppCompatActivity implements GetLocationView, GoogleMap.OnMarkerClickListener {

    private static final int DEFAULT_ZOOM = 15;
    private static final float DEFAULT_MILE = 0.2f; //miles

    @InjectView(R.id.txtAddress)
    RobotoTextView txtAddress;
    @InjectView(R.id.txtFreeDocks)
    RobotoTextView txtFreeDocks;
    @InjectView(R.id.txtBikeAvl)
    RobotoTextView txtBikeAvl;
    @InjectView(R.id.bikeInfoWindow)
    RelativeLayout bikeInfoWindow;
    @InjectView(R.id.searchDistance)
    RangeBar searchDistance;
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

    boolean updateUI = true;

    LatLng mSelectedCoOrdinates;
    boolean mShowAll = false;
    HashMap<Marker, Feature> mMarkers = new HashMap<Marker, Feature>();


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
        searchDistance.setRangePinsByIndices(0, 0);
        searchDistance.setEnabled(false);
        searchDistance.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {


            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int i, int i1, String s, final String s1) {

                rangeBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            setLocationsOnMap(Float.parseFloat(s1));
                        }
                        return false;
                    }
                });

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
        mMap.setOnMarkerClickListener(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCurrentLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        setMapZoom(0);
    }


    @OnClick(R.id.fab)
    public void navigate(View view) {
       String str = "daddr=" + mSelectedCoOrdinates.latitude + "," + mSelectedCoOrdinates.longitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?" + str));
        startActivity(intent);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Feature feature = mMarkers.get(marker);
        fab.setVisibility(View.VISIBLE);
        txtFreeDocks.setText(String.format(getString(R.string.free_docks),feature.getProperties().getDocksAvailable()));
        txtBikeAvl.setText(String.format(getString(R.string.bikes),feature.getProperties().getBikesAvailable()));
        txtAddress.setText(feature.getProperties().getAddressStreet());
        mSelectedCoOrdinates = new LatLng(feature.getGeometry().getCoordinates().get(1),
                feature.getGeometry().getCoordinates().get(0));
        showInfoScreen(false);
        return false;
    }


    @OnClick(R.id.exploreAll)
    public void showAll(View view) {
        //TODO show all locations
    }

    private void showInfoScreen(boolean flag) {
        if(flag) {
            bikeInfoWindow.setVisibility(View.GONE);
            searchBikeWindow.animate().translationYBy(-searchBikeWindow.getHeight()).start();
            fab.setVisibility(View.GONE);
        } else {
            bikeInfoWindow.setVisibility(View.VISIBLE);
            searchBikeWindow.animate().translationYBy(searchBikeWindow.getHeight()).start();
        }
    }



    @Override
    public void onBackPressed() {
        if (bikeInfoWindow.isShown()) {
            showInfoScreen(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationsReceived(List<Feature> locations) {
        mLocationObservable = Observable.from(locations);
        Toast.makeText(this, Integer.toString(locations.size()), Toast.LENGTH_LONG).show();
        if(updateUI) {
            searchDistance.setEnabled(true);
            searchDistance.setSeekPinByValue(DEFAULT_MILE);
            setLocationsOnMap(DEFAULT_MILE);
            updateUI = false;
        }
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
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Feature>() {
            @Override
            public void onCompleted() {
                updateSearchText(distRange);
                setMapZoom((int) distRange);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Feature feature) {
                Geometry geometry = feature.getGeometry();
                Double longitude = geometry.getCoordinates().get(0);
                Double latitude = geometry.getCoordinates().get(1);

                final LatLng point = new LatLng(latitude, longitude);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .position(point));
                mMarkers.put(marker, feature);
            }
        });
    }

    public void clearMarkers() {
        if (mMap != null) {
            mMap.clear();
            mMarkers.clear();
        }

    }

    private void updateSearchText(float dist) {
        String str = String.format(getString(R.string.search_text), mMarkers.size(), dist);
        txtTotalDocks.setText(str);
    }

    private void setMapZoom(int zoomBy) {
        final LatLng mCurrentPos = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPos, DEFAULT_ZOOM - zoomBy));
    }
}
