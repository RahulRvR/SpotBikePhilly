package com.rahulrvr.spotbikephilly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import com.crashlytics.android.Crashlytics;
import com.devspark.robototextview.widget.RobotoTextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class MapsActivity extends AppCompatActivity {

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @OnClick(R.id.txtTotalDocks)
    public void onitem(View view) {
        bikeInfoWindow.setVisibility(View.VISIBLE);
        searchBikeWindow.animate().translationYBy(searchBikeWindow.getHeight()).start();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(bikeInfoWindow.isShown()) {
            bikeInfoWindow.setVisibility(View.GONE);
            searchBikeWindow.animate().translationYBy(-searchBikeWindow.getHeight()).start();
            fab.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }
    }
}
