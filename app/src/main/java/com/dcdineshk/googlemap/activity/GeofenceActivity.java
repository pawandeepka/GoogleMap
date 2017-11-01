package com.dcdineshk.googlemap.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dcdineshk.googlemap.BuildConfig;
import com.dcdineshk.googlemap.R;
import com.dcdineshk.googlemap.database.GeofenceContract;
import com.dcdineshk.googlemap.database.GeofenceStorage;
import com.dcdineshk.googlemap.model.DataParser;
import com.dcdineshk.googlemap.services.GeofenceTrasitionService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GeofenceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener, ResultCallback<Status>,
        View.OnClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mGoogleMap;
    ArrayList<LatLng> mMarkerPoint;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private boolean checkClick = false;
    private static final String TAG = GeofenceActivity.class.getSimpleName();

    LatLng start;
    MarkerOptions markerOptions;

    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    private Marker geoFenceMarker;

    double latitude1, longitude1;
    Place place;
    @BindView(R.id.btn_GO)
    Button mButton_AddGEO;

    @BindView(R.id.destinationLocationEditText)
    EditText mDestinationLocation_EditText;

    View mapView;
    private LatLng latLng;
    float mRadius = 500;
    private EditText fEt_Address;
    private EditText fEt_AddressssType;
    private String mRadiusSelect;
    private Button fBtnSave1;
    SharedPreferences mSharedPreferences;
    App mApp;
    private Location lastLocation;
    public static final String SHARED_PREFERENCES_NAME = BuildConfig.APPLICATION_ID + ".SHARED_PREFERENCES_NAME";
    public static final float GEOFENCE_RADIUS_IN_METERS = 100;


    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent(context, GeofenceActivity.class);
        intent.putExtra(NOTIFICATION_MSG, msg);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mApp = (App) getApplicationContext();
        ButterKnife.bind(this);

        mGeofencePendingIntent = null;
        mMarkerPoint = new ArrayList<>();
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = fm.getView();
        fm.getMapAsync(this);
        mButton_AddGEO.setOnClickListener(this);
        mDestinationLocation_EditText.setOnClickListener(this);
        mSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);

                Log.e("Tag", "Place: "
                        + place.getLatLng().longitude
                        + place.getLatLng().latitude
                        + place.getName() + "     " + place.getAddress());
                latitude1 = place.getLatLng().latitude;
                longitude1 = place.getLatLng().longitude;
                start = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                fEt_Address.setText(place.getName());
                mMarkerPoint.add(start);

                // Already two locations
                if (mMarkerPoint.size() > 1) {
                    mMarkerPoint.clear();
                    mGoogleMap.clear();
                }

                LatLng origin = new LatLng(latLng.latitude, latLng.longitude);

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                //Place selected location marker
                markerOptions = new MarkerOptions();

                markerOptions.position(start);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                createGeofence(start, mRadius);
                reloadMapMarkers();
                Log.d("new_DATA", "" + start);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void reloadMapMarkers() {
        mGoogleMap.clear();
        try (Cursor cursor = GeofenceStorage.getCursor()) {
            while (cursor.moveToNext()) {
                long expires = Long.parseLong(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_EXPIRES)));
                if (System.currentTimeMillis() < expires) {
                    String key = cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_KEY));
                    double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_LAT)));
                    double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex(GeofenceContract.GeofenceEntry.COLUMN_NAME_LNG)));
                    addMarker(key, new LatLng(lat, lng));
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        reloadMapMarkers();
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);
            mGoogleMap.setMyLocationEnabled(true);
        }

    }

    @Override // Delete Geofencing Method on Title Click
    public void onInfoWindowClick(Marker marker) {
        final String requestId = marker.getTitle().split(":")[1];
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "GeoFence Not connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            List<String> idList = new ArrayList<>();
            idList.add(requestId);
            LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, idList).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        GeofenceStorage.removeGeofence(requestId);
                        Toast.makeText(GeofenceActivity.this, "Geofence removed!", Toast.LENGTH_SHORT).show();
                        reloadMapMarkers();
                    } else {
                        // Get the status code for the error and log it using a user-friendly message.
                        String errorMessage = GeofenceTrasitionService.getErrorString(status.getStatusCode());
                        Log.e(TAG, errorMessage);
                    }
                }
            });
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
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
    public void onConnected(Bundle bundle) {
        getLastKnownLocation();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(final Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Double lat = location.getLatitude();
        Double lat1 = location.getLongitude();

        markerOptions = new MarkerOptions();

        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geo.getFromLocation(lat, lat1, 1);
            if (addresses.isEmpty()) {
            } else {
                if (addresses.size() > 0) {
                    markerOptions.title(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    mDestinationLocation_EditText.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        Log.d("TOTAL_DATA", "" + latLng);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 100.0f; // in meters

    // Create a Geofence
    private Geofence createGeofence(LatLng latLng, float radius) {
        Log.e("createGeofence", "" + latLng);

        String id = UUID.randomUUID().toString();

        return new Geofence.Builder()
                .setRequestId(GEOFENCE_REQ_ID)
                .setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration(GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    public void countPolygonPoints() {
        if (mMarkerPoint.size() >= 2) {
            checkClick = true;
            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(mMarkerPoint);
            polygonOptions.strokeColor(Color.BLUE);
            polygonOptions.strokeWidth(7);
            polygonOptions.fillColor(Color.CYAN);
            Polygon polygon = mGoogleMap.addPolygon(polygonOptions);
        }
    }

    private final String KEY_GEOFENCE_LAT = "GEOFENCE LATITUDE";
    private final String KEY_GEOFENCE_LON = "GEOFENCE LONGITUDE";

    // Saving GeoFence marker with prefs mng
    private void saveGeofence() {
        Log.d(TAG, "saveGeofence()");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            editor.putLong(KEY_GEOFENCE_LAT, Double.doubleToRawLongBits(place.getLatLng().latitude));
            editor.putLong(KEY_GEOFENCE_LON, Double.doubleToRawLongBits(place.getLatLng().longitude));
            editor.apply();

        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(start)
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(GEOFENCE_RADIUS);
        geoFenceLimits = mGoogleMap.addCircle(circleOptions);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub
        System.out.println("Marker lat long=" + marker.getPosition());
        System.out.println("First postion check" + mMarkerPoint.get(0));
        System.out
                .println("**********All arrayPoints***********" + mMarkerPoint);
        if (mMarkerPoint.get(0).equals(marker.getPosition())) {
            System.out.println("********First Point choose************");
            countPolygonPoints();
        }
        return false;
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            saveGeofence();
            drawGeofence();
        } else {
        }
    }

    private void removeGeofenceDraw() {
        Log.d(TAG, "removeGeofenceDraw()");
        if (geoFenceMarker != null)
            if (geoFenceLimits != null)
                geoFenceLimits.remove();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_GO:
                showPopUP();
                break;
            case R.id.destinationLocationEditText:

                break;
        }
    }

    private void showPopUP() {
        final Dialog fDialog2 = new Dialog(this);
        fDialog2.setContentView(R.layout.add_address_fragment2);
        fDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fEt_Address = (EditText) fDialog2.findViewById(R.id.et_Address_Add_CA);
        fEt_AddressssType = (EditText) fDialog2.findViewById(R.id.et_AddressType_Add_CA);
        fBtnSave1 = (Button) fDialog2.findViewById(R.id.btn_save_Address_CA);

        fEt_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findPlace();
            }
        });

            fBtnSave1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mRadiusSelect = fEt_AddressssType.getText().toString();
                        if (fEt_AddressssType.equals("")){
                            fEt_AddressssType.setError("Enter radius for GeoFence");
                        }
                        setGeofencing(start, Float.parseFloat(mRadiusSelect));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fDialog2.dismiss();
                }
            });

        ImageView f_Image = (ImageView) fDialog2.findViewById(R.id.image_Address_CA);
        f_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fDialog2.dismiss();
            }
        });
        fDialog2.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void findPlace() {
        try {
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, 1);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

        if (mMarkerPoint.size() > 1) {
            mMarkerPoint.clear();
            mGoogleMap.clear();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public static final String NEW_GEOFENCE_NUMBER = BuildConfig.APPLICATION_ID + ".NEW_GEOFENCE_NUMBER";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 1000;
    private PendingIntent mGeofencePendingIntent;

    private void setGeofencing(final LatLng latLng, float radius) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Google Api not connected!", Toast.LENGTH_SHORT).show();
            return;
        }
        final String key = getNewGeofenceNumber() + "";
        final long expTime = System.currentTimeMillis() + GEOFENCE_EXPIRATION_IN_MILLISECONDS;
        addMarker(key, latLng);
        Geofence geofence = new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        latLng.latitude,
                        latLng.longitude,
                        radius
                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(geofence),
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        GeofenceStorage.saveToDb(key, latLng, expTime);
                        Toast.makeText(GeofenceActivity.this, "Geofence Added!", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = GeofenceTrasitionService.getErrorString(status.getStatusCode());
                        Log.e(TAG, errorMessage);
                    }
                }
            });
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private int getNewGeofenceNumber() {
        int number = mSharedPreferences.getInt(NEW_GEOFENCE_NUMBER, 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(NEW_GEOFENCE_NUMBER, number + 1);
        editor.commit();
        return number;
    }

    private void addMarker(String key, LatLng latLng) {
        mGoogleMap.addMarker(new MarkerOptions()
                .title("G:" + key)
                .snippet("Click here if you want delete this geofence")
                .position(latLng));
        mGoogleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(GEOFENCE_RADIUS_IN_METERS)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#80ff0000")));
    }

    private GeofencingRequest getGeofencingRequest(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTrasitionService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getLastKnownLocation() {
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            Log.i(TAG, "LasKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
        } else {
            Log.w(TAG, "No location retrieved yet");
        }
    }

    private Marker locationMarker;

    // Create a Location Marker
    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    private void writeActualLocation(Location location) {
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if (mGoogleMap != null) {
            // Remove the anterior marker
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = mGoogleMap.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }
}