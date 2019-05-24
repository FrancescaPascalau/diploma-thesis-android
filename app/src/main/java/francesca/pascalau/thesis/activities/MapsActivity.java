package francesca.pascalau.thesis.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import francesca.pascalau.thesis.R;
import francesca.pascalau.thesis.common.PermissionUtils;
import francesca.pascalau.thesis.common.Position;
import francesca.pascalau.thesis.common.RequestOperations;
import francesca.pascalau.thesis.common.Surface;
import francesca.pascalau.thesis.common.SurfaceFirebase;
import francesca.pascalau.thesis.common.Type;
import francesca.pascalau.thesis.common.VisionFeature;
import francesca.pascalau.thesis.common.VisionImage;
import francesca.pascalau.thesis.common.VisionLabelAnnotation;
import francesca.pascalau.thesis.common.VisionRequest;
import francesca.pascalau.thesis.common.VisionRequestBody;
import francesca.pascalau.thesis.common.VisionResponseBody;

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
    private static List<LatLng> trackedLocations = new ArrayList<>();
    private static List<SurfaceFirebase> trackedSurfaces = new ArrayList<>();
    private static int id = 0;
    private Polygon mMutablePolygon;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance("gs://cartare-teren.appspot.com");
    private DatabaseReference surfacesRef = database.getReference("surfaces");
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

        initializeMapsActivity(savedInstanceState);
    }

    private void initializeMapsActivity(Bundle savedInstanceState) {
        initializeMapFragment(savedInstanceState);

        // Location stuff
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();

        createButtons();

        surfacesRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<SurfaceFirebase>> genericTypeIndicator =
                        new GenericTypeIndicator<List<SurfaceFirebase>>() {
                        };

                List<SurfaceFirebase> value =
                        dataSnapshot.getValue(genericTypeIndicator);
                Log.e(TAG, "Value is: " + value);

                if (value != null) {
                    trackedSurfaces = value;
                    Log.e(TAG, "Tracked Surface is: " + trackedSurfaces);
                    drawPreviousAreas2();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initializeMapFragment(Bundle savedInstanceState) {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // To save the map
        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);
    }

    private void createButtons() {
        Button locationStartButton = findViewById(R.id.location_start);

        locationStartButton.setOnClickListener(v -> onLocationStartButton());

        Button locationStopButton = findViewById(R.id.location_stop);

        locationStopButton.setOnClickListener(v -> onLocationStopButton());

        Button goBackButton = findViewById(R.id.go_back);

        goBackButton.setOnClickListener(v -> onGoBackButton());
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
        trackedLocations = new ArrayList<>();
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

            BigDecimal area = BigDecimal.valueOf(SphericalUtil.computeArea(trackedLocations));

            Log.e(TAG, area + " -- " + trackedLocations);

            ArrayList<Position> positions = getPositionsFromLatLng();

            doSurfaceRequests(area, positions);

            stopLocationUpdates();

            findViewById(R.id.location_start).setVisibility(View.VISIBLE);
            findViewById(R.id.location_stop).setVisibility(View.GONE);
        }
    }

    /**
     * This method draws a polygon on the Google Maps based on the trackedLocations.
     * Adds a marker with the determined area & price to the polygon.
     */
    private void updateMap(Surface surface) {
        List<LatLng> latLngs = new ArrayList<>();
        surface.getCoordinates().forEach(coordinate ->
                latLngs.add(new LatLng(coordinate.getLatitude(), coordinate.getLongitude())));
        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(latLngs)
        );

        polygon.setTag(surface.getArea());
        mMap.addMarker(new MarkerOptions()
                .title(String.format("Area: %.2f m² - Price: %.2f €",
                        surface.getArea(),
                        surface.getPrice()
                ))
                .position(latLngs.get(0)));
    }

    /**
     * This method does asynchronous requests to backend and Firebase to save surfaces and coordinates.
     * The requests are asynchronous so the application doesn't wait until it receives a response and can go forward to the next steps
     *
     * @param area      the value of the polygon identified on the map
     * @param positions the coordinates of the calculated area
     */
    private void doSurfaceRequests(BigDecimal area, ArrayList<Position> positions) {
        String urlSaveSurface = "http://192.168.43.132:1997/v1/surfaces/save";
        RequestOperations operations = RequestOperations.getInstance(this);

        /**
         * A consumer that handles the response from backend
         */
        Consumer<String> consumer = surface -> {
            Surface mySurface = new Gson().fromJson(surface, Surface.class);

            Log.e(TAG, mySurface.toString());
            Toast.makeText(this, mySurface.toString(), Toast.LENGTH_LONG);

            /*trackedLocationsMap.put(mySurface.getIdSurface().toString(), mySurface.getCoordinates());
            trackedSurfaces.add(SurfaceFirebase.fromSurface(mySurface));
            sendLocations();*/

            /**
             *  This method has a consumer and it is called to process the response for images requests
             */
            doStreetViewImageRequest(operations, mySurface);
        };

        Surface surface = new Surface(area, positions);
        operations.postRequestForObject(urlSaveSurface, surface, consumer);
    }

    private void doStreetViewImageRequest(RequestOperations operations, Surface mySurface) {
        String getStreetViewImage = "https://maps.googleapis.com/maps/api/streetview";

        /**
         * This consumer is called from the above consumer for image response handling from url
         */
        Consumer<Bitmap> consumerBitmap = image -> {
            Log.e(TAG, "Image received." + image.getByteCount());

//            Context context = getApplicationContext();
//            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                    "://" + context.getResources().getResourcePackageName(R.drawable.bac)
//                    + '/' + context.getResources().getResourceTypeName(R.drawable.bac)
//                    + '/' + context.getResources().getResourceEntryName(R.drawable.bac));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference storageReference = storage.getReference()
                    .child(
                            String.format("%s=>%s[%s]",
                                    mySurface.getCoordinates().get(0).getLatitude(),
                                    mySurface.getCoordinates().get(0).getLongitude(),
                                    UUID.randomUUID().toString()
                            )
                    );
            storageReference.putBytes(data);

            doVisionRequests(operations, data, mySurface);
        };

        /**
         * Defining the params for the image requests
         */
        HashMap<String, String> params = new HashMap<>();
        params.put("size", "400x400");
        params.put("location", mySurface.getCoordinates().get(0).getLatitude() + ", " + mySurface.getCoordinates().get(0).getLongitude());
        params.put("fov", "120");
        params.put("heading", "0");
        params.put("pitch", "0");
        params.put("key", "AIzaSyB59qEeDUhviFleb1Jt_cgQ8uZY9aBkTIs");

        operations.getRequestForImage(getStreetViewImage, params, consumerBitmap);
    }

    private void doVisionRequests(RequestOperations operations, byte[] data, Surface mySurface) {

        String getVisionLabels = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyB59qEeDUhviFleb1Jt_cgQ8uZY9aBkTIs";
        String getPriceByType = "http://192.168.43.132:1997/v1/types/findByType/";
        String urlSaveSurface = "http://192.168.43.132:1997/v1/surfaces/save";

        Consumer<String> visionResponseConsumer = visionResponse -> {
            VisionResponseBody response = new Gson().fromJson(visionResponse, VisionResponseBody.class);

            VisionLabelAnnotation label = response.getResponses().get(0).getLabelAnnotations().get(0);

            Consumer<String> typeConsumer = typeString -> {
                Type type = new Gson().fromJson(typeString, Type.class);

                BigDecimal priceForArea = mySurface.getArea().multiply(type.getValue());
                mySurface.setPrice(priceForArea);
                mySurface.setType(type);
                operations.postRequestForObject(urlSaveSurface, mySurface);
                Toast.makeText(this, "The price is: " + priceForArea.toString() + " €", Toast.LENGTH_SHORT).show();

                updateMap(mySurface);
                trackedSurfaces.add(SurfaceFirebase.fromSurface(mySurface));
                sendLocations();
            };

            String labelDescription = label.getDescription().replaceAll(" ", "-");
            operations.getRequestForObject(getPriceByType + labelDescription, typeConsumer);
        };

        String imageContent = Base64.encodeToString(data, Base64.DEFAULT);
        VisionFeature feature = new VisionFeature(50, "LABEL_DETECTION");
        VisionImage visionImage = new VisionImage(imageContent);
        VisionRequest requests = new VisionRequest(Arrays.asList(feature), visionImage);
        VisionRequestBody body = new VisionRequestBody(Arrays.asList(requests));
        operations.postRequestForObject(getVisionLabels, body, visionResponseConsumer);
    }

    private ArrayList<Position> getPositionsFromLatLng() {
        ArrayList<Position> positions = new ArrayList<>();
        for (LatLng location : trackedLocations) {
            positions.add(new Position(location.latitude, location.longitude));
        }
        return positions;
    }

    private void onGoBackButton() {
        if (trackedLocations.isEmpty()) {
            finish();
        } else {
            Log.e(TAG, "Location tracking still working");
            Toast.makeText(this, "Location tracking still working, please stop it first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawPreviousAreas2() {
        for (SurfaceFirebase surface : trackedSurfaces) {
            List<Position> oldPositions = surface.getCoordinates();
            ArrayList<LatLng> positions = new ArrayList<>();
            for (Position position : oldPositions) {
                positions.add(new LatLng(position.getLatitude(), position.getLongitude()));
            }

            if (positions != null) {
                Polygon polygon = mMap.addPolygon(new PolygonOptions()
                        .clickable(true)
                        .addAll(positions));

                polygon.setTag(surface.getArea());

                mMap.addMarker(new MarkerOptions()
                        .title(String.format("Area: %.2f m\u00b2 - Price: %.2f \u20ac",
                                surface.getArea(),
                                surface.getPrice()))
                        .position(positions.get(0)));
            }
        }
    }

    private void sendLocations() {
        if (auth.getCurrentUser() != null && trackedSurfaces != null && !trackedSurfaces.isEmpty()) {
            surfacesRef.setValue(trackedSurfaces);
            Log.e(TAG, "Sent Locations.....");
        } else {
            Log.e(TAG, "Could not sent locations (user not logged in)");
            Toast.makeText(this, "Could not sent locations (user not logged in)", Toast.LENGTH_SHORT).show();
        }
    }
}