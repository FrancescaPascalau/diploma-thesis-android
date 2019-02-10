package francesca.pascalau.thesis.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import francesca.pascalau.thesis.R;
import francesca.pascalau.thesis.common.PermissionUtils;
import francesca.pascalau.thesis.common.Position;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final LatLng CENTER = new LatLng(46, 23);
    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 10;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static ArrayList<LatLng> trackedLocations = new ArrayList<>();
    private static HashMap<String, ArrayList<Position>> trackedLocationsMap = new HashMap<>();
    private static int id = 0;
    private Polygon mMutablePolygon;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference locationsRef = database.getReference("locations");
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // To save the map
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);

        // Location stuff
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        Button locationStartButton = findViewById(R.id.location_start);

        locationStartButton.setOnClickListener(v -> onLocationStartButton());

        Button locationStopButton = findViewById(R.id.location_stop);

        locationStopButton.setOnClickListener(v -> onLocationStopButton());

        Button goBackButton = findViewById(R.id.go_back);

        goBackButton.setOnClickListener(v -> onGoBackButton());

        locationsRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, ArrayList<Position>>> genericTypeIndicator =
                        new GenericTypeIndicator<HashMap<String, ArrayList<Position>>>() {
                        };

                HashMap<String, ArrayList<Position>> value =
                        dataSnapshot.getValue(genericTypeIndicator);
                Log.e(TAG, "Value is: " + value);

                if (value != null) {
                    trackedLocationsMap = value;
                    Log.e(TAG, "Tracked Map is: " + trackedLocationsMap);
                    drawPreviousAreas();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        // Override the default content description on the view, for accessibility mode.
        mMap.setContentDescription(getString(R.string.polygon_demo_description));
    }

    /**
     * Enables the periodic location request
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient.connect();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Location update resumed .....................");
            //startLocationUpdates();
        } else {
            Log.d(TAG, "google api client not connected .....................");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
            //startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        trackedLocations.add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        Log.d(TAG, "Current Location: " + mCurrentLocation.getLatitude() + " --- " + mCurrentLocation.getLongitude() + " Updated at: " + mLastUpdateTime);
        Toast.makeText(this, trackedLocations.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            //startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


    /**
     * Methods used for drawing surfaces on Google Map
     * Creates a List of LatLngs that form a rectangle with the given dimensions.
     */
    private List<LatLng> createRectangle(LatLng center, double halfWidth, double halfHeight) {
        return Arrays.asList(new LatLng(center.latitude - halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
                new LatLng(center.latitude + halfHeight, center.longitude - halfWidth),
                new LatLng(center.latitude - halfHeight, center.longitude - halfWidth));
    }

    /**
     * Configure the Location Tracker start/stop buttons
     */
    private void onLocationStartButton() {
        id++;
        startLocationUpdates();
        findViewById(R.id.location_start).setVisibility(View.GONE);
        findViewById(R.id.location_stop).setVisibility(View.VISIBLE);
    }

    private void onLocationStopButton() {
        Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show();
        if (trackedLocations != null) {
            // We add the first location to the list in order to start and end with the same position
            trackedLocations.add(trackedLocations.get(0));
            Log.e(TAG, trackedLocations.toString());

            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .addAll(trackedLocations)
            );

            Integer area = (int) SphericalUtil.computeArea(polygon.getPoints());
            polygon.setTag(area);
            mMap.addMarker(new MarkerOptions()
                    .title("Area: " + area.toString() + " m\u00B2")
                    .position(trackedLocations.get(0)));
            Log.e(TAG, "computeArea " + area);

            // Add to the collective hash map
            Log.e(TAG, area + " -- " + trackedLocations);
            ArrayList<Position> locations = new ArrayList<>();
            for (LatLng location : trackedLocations) {
                locations.add(new Position(location.latitude, location.longitude));
            }
            String uniqueID = UUID.randomUUID().toString();
            trackedLocationsMap.put(uniqueID, locations);
            Log.e(TAG, " Here " + trackedLocationsMap);

            sendLocations();
            stopLocationUpdates();
            trackedLocations = new ArrayList<>();

            findViewById(R.id.location_start).setVisibility(View.VISIBLE);
            findViewById(R.id.location_stop).setVisibility(View.GONE);
        }
    }

    private void onGoBackButton() {
        if (trackedLocations.isEmpty()) {
            finish();
        } else {
            Log.e(TAG, "Location tracking still working");
            Toast.makeText(this, "Location tracking still working, please stop it first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawPreviousAreas() {
        for (Map.Entry<String, ArrayList<Position>> surface : trackedLocationsMap.entrySet()) {
            ArrayList<Position> oldPositions = surface.getValue();
            ArrayList<LatLng> positions = new ArrayList<>();
            for (Position position : oldPositions) {
                positions.add(new LatLng(position.getLatitude(), position.getLongitude()));
            }

            if (positions != null) {
                Polygon polygon = mMap.addPolygon(new PolygonOptions()
                        .clickable(true)
                        .addAll(positions)
                );

                Integer area = (int) SphericalUtil.computeArea(polygon.getPoints());
                polygon.setTag(area);
                mMap.addMarker(new MarkerOptions()
                        .title("Area: " + area.toString() + " m\u00B2")
                        .position(positions.get(0)));
            }
        }
    }

    private void sendLocations() {

        if (auth.getCurrentUser() != null) {
            locationsRef.setValue(trackedLocationsMap);
            Log.e(TAG, "Sent Locations.....");
        } else {
            Log.e(TAG, "Could not sent locations (user not logged in)");
            Toast.makeText(this, "Could not sent locations (user not logged in)", Toast.LENGTH_SHORT).show();
        }

    }
}