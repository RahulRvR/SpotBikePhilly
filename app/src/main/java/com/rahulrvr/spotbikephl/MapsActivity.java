package com.rahulrvr.spotbikephl;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rahulrvr.spotbikephl.custom.BikeIndicator;
import com.rahulrvr.spotbikephl.impl.GetLocationPresenterImpl;
import com.rahulrvr.spotbikephl.interfaces.GetLocationPresenter;
import com.rahulrvr.spotbikephl.interfaces.GetLocationView;
import com.rahulrvr.spotbikephl.pojo.Feature;
import com.rahulrvr.spotbikephl.pojo.Geometry;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MapsActivity extends AppCompatActivity implements GetLocationView, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int DEFAULT_ZOOM = 15;
    private static final float DEFAULT_MILE = 2.0f; //miles
    private static final int REFRESH_TIME = 60000;
    private static final float MAX_DIST = 4.0f;
    private static final int DIST_CHECK = 6;

    @InjectView(R.id.txtAddress)
    TextView txtAddress;
    @InjectView(R.id.txtFreeDocks)
    TextView txtFreeDocks;
    @InjectView(R.id.txtBikeAvl)
    TextView txtBikeAvl;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;
    @InjectView(R.id.bikeIndicator)
    BikeIndicator bikeIndicator;

    private Observable<Feature> mLocationObservable;


    GetLocationPresenter mPresenter;
    Location mCurrentLocation;
    Marker mPreviousMarker = null;
    Marker mSelectedMarker = null;
    boolean updateUI = true;

    LatLng mSelectedCoOrdinates;
    boolean mShowAll = false;
    HashMap<Marker, Feature> mMarkers = new HashMap<Marker, Feature>();

    float mCurrentDistance = -1;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    MaterialDialog.Builder mBuilder;
    GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        setContentView(R.layout.activity_maps);
        ButterKnife.inject(this);
        mPresenter = new GetLocationPresenterImpl(this);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.getLocations(MapsActivity.this);
                    }
                });

            }
        }, REFRESH_TIME, REFRESH_TIME);

        txtAddress.setTypeface(
                ManagerTypeface.getTypeface(this, R.string.typeface_roboto_bold));
        txtFreeDocks.setTypeface(
                ManagerTypeface.getTypeface(this, R.string.typeface_roboto_thin));
        txtBikeAvl.setTypeface(
                ManagerTypeface.getTypeface(this, R.string.typeface_roboto_thin));

        buildGoogleApiClient();
        setUpMapIfNeeded();
        hidePanel();
        initialize();

        mBuilder = new MaterialDialog.Builder(this)
                .positiveColorRes(R.color.primary).positiveText(R.string.action_ok);
    }


    private void initialize() {
        mBuilder = new MaterialDialog.Builder(this)
                .positiveColorRes(R.color.primary).positiveText(R.string.action_ok);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar);
        TextView appName = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.appName);


        appName.setTypeface(
                ManagerTypeface.getTypeface(this, R.string.typeface_roboto_light));


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mPresenter.getLocations(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:

                View view = getLayoutInflater().inflate(R.layout.layout_help, null);
                TextView textView = (TextView) view.findViewById(R.id.version);
                textView.setText(BuildConfig.VERSION_NAME);
                new MaterialDialog.Builder(this)
                        .positiveColorRes(R.color.primary)
                        .title(R.string.app_name)
                        .customView(view, true)
                        .positiveText(R.string.action_ok)
                        .show();
                return true;
            case R.id.menu_contact_us :
                String uri = "tel:" +  getString(R.string.customer_care_number);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                return true;
        }
        return false;
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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (mPreviousMarker != null) {
            Feature feature = mMarkers.get(mPreviousMarker);
            mPreviousMarker.setIcon(BitmapDescriptorFactory.fromResource(getColoredIcon(getAvailabilityPercentage(feature))));
        }
        mSelectedMarker = marker;
        mPreviousMarker = marker;
        updateCurrentInfo(marker);
        showPanel();
        return false;
    }


    private void updateCurrentInfo(Marker marker) {
        if (marker != null) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED));
            Feature feature = mMarkers.get(marker);

            bikeIndicator.setBikePercentage(getAvailabilityPercentage(feature));

            txtFreeDocks.setText(String.format("%02d", feature.getProperties().getDocksAvailable()));
            txtBikeAvl.setText(String.format("%02d", feature.getProperties().getBikesAvailable()));
            txtAddress.setText(feature.getProperties().getAddressStreet());
            mSelectedCoOrdinates = new LatLng(feature.getGeometry().getCoordinates().get(1),
                    feature.getGeometry().getCoordinates().get(0));
            showInfoScreen(false);
        }
    }


    private void showInfoScreen(boolean flag) {
        //TODO
    }


    @Override
    public void onBackPressed() {

        if (slidingLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.HIDDEN)) {
            super.onBackPressed();
        } else {
            hidePanel();
        }
    }

    @Override
    public void onLocationsReceived(List<Feature> locations) {
        mLocationObservable = Observable.from(locations);
        setLocationsOnMap();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        if (mSelectedMarker != null) {
            updateCurrentInfo(mSelectedMarker);
        }
    }


    @Override
    public void onError(RetrofitError.Kind kind) {
        if (kind == RetrofitError.Kind.NETWORK) {
            showErrorMessage(R.string.error_title, R.string.no_connectivity_message);
        }
        progressBar.setVisibility(View.GONE);
    }


    public void clearMarkers() {
        if (mMap != null) {
            mMap.clear();
            mMarkers.clear();
            mPreviousMarker = null;
        }

    }

    private void setMapZoom() {
        if (mCurrentLocation != null) {
            final LatLng mCurrentPos = new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentPos, DEFAULT_ZOOM));
        } else {
            showErrorMessage(R.string.error_title, R.string.location_not_found);
        }
    }


    private void showErrorMessage(int title, int message) {
        if (mBuilder != null) {
            mBuilder.title(title)
                    .content(message).show();
        }
        progressBar.setVisibility(View.GONE);
    }


    private void updateLocation(Location location) {

        if(location!= null) {
            Location philly = new Location("philly");
            philly.setLongitude(-75.1652);
            philly.setLatitude(39.9526);
            float distance = location.distanceTo(philly);
            double distInMiles = distance / 1609.34;

            if (distInMiles > DIST_CHECK) {
                mCurrentLocation = philly;
            } else {
                mCurrentLocation = location;
            }
            setMapZoom();
        } else {
            showErrorMessage(R.string.error_title,R.string.location_not_found);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        updateLocation(LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void setLocationsOnMap() {
        clearMarkers();
        if (mCurrentDistance < 0) {
            mCurrentDistance = DEFAULT_MILE;
        }
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
                return distInMiles <= mCurrentDistance || mShowAll;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Feature>() {
            @Override
            public void onCompleted() {
                if (mMarkers.size() <= 0) {
                    showErrorMessage(R.string.title_no_docks, R.string.no_docks_message);
                }
                setMapZoom();
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
                int percentage = getAvailabilityPercentage(feature);
                marker.setIcon(BitmapDescriptorFactory.fromResource(getColoredIcon(percentage)));
                mMarkers.put(marker, feature);
            }
        });
    }


    private int getAvailabilityPercentage(Feature feature) {
        return (feature.getProperties().getBikesAvailable() * 100) / feature.getProperties().getTotalDocks();
    }

    private int getColoredIcon(int percentage) {
        if (percentage > 40) {
            return R.drawable.ic_bike_green;
        } else if (percentage > 10) {
            return R.drawable.ic_bike_yellow;
        } else {
            return R.drawable.ic_bike_red;
        }
    }

    private void showPanel() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void hidePanel() {
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

}
