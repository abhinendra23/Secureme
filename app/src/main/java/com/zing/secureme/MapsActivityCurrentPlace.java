package com.zing.secureme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zing.secureme.Model.History;
import com.zing.secureme.Model.Person;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.zing.secureme.R.*;

/**
 * Activity that displays a map showing the place at the device's current location.
 */
public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback {

    FirebaseAuth auth;
    FirebaseUser user;

    Person person;
    History history;
    long generationCodeValue = 0;
    long number=0;
    HashMap<String, History> myHistory;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference generationCodeReference;
    String place;
    Dialog dialog;
    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location  and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private String mPlaceName;
    private String mPlaceAddress;
    private List<String> mPlaceAttributions;
    private LatLng mPlaceLatLng;
    private String prevPlaceName;


    ////////////////////////////////////////////////////////////////////////////////
    //////////// Important Values for your localization and bluetooth //////////////
    ////////////////////////////////////////////////////////////////////////////////
    // Selected current place
    private LatLng markerLatLng;
    private String markerSnippet;
    private String markerPlaceName;

    // New Bluetooth Devices Number
    private int btDevicesCount;
    private int crowdThreshold = 10;
    ////////////////////////////////////////////////////////////////////////////////
    Handler handler;
    TripModel currentTrip;
    String currentTripID;
    private String username;
    boolean hasTasks;

    public static final String DURATION = "DURATION";
    public static final String NUMDEVICES = "NUMDEVICES";
    public static final String SCORE = "SCORE";
    public static final String NUMPLACES = "NUMPLACES";
    Vibrator vibrator;
    Button okay,cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firebaseDatabase = FirebaseDatabase.getInstance();
        generationCodeReference = firebaseDatabase.getReference("GenerationCode");
        //Get initial value of Code Generator on app startup
        generationCodeReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                generationCodeValue = (long) Objects.requireNonNull(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialog = new Dialog(MapsActivityCurrentPlace.this);
        dialog.setContentView(layout.new_layout);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(drawable.dialog_bk));
        }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        okay = dialog.findViewById(id.btn_okay);
        cancel = dialog.findViewById(id.btn_cancel);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivityCurrentPlace.this,"Okay",Toast.LENGTH_LONG);
                dialog.dismiss();
                mp.stop();
                vibrator.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivityCurrentPlace.this,"Cancel",Toast.LENGTH_LONG);
                dialog.dismiss();
                mp.stop();
                vibrator.cancel();
            }
        });
        place = getIntent().getStringExtra("Place");
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        // Retrieve the content view that renders the map.
        setContentView(layout.activity_maps);

        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(string.google_maps_key));
        mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);

        IntentFilter filter = new IntentFilter("ServiceBroadcast");
        registerReceiver(receiver, filter);

        currentTripID = null;
        mPlaceName = null;
        mPlaceAddress = null;
        mPlaceAttributions = new ArrayList<>();
        mPlaceLatLng = null;
        prevPlaceName = null;

        username = getIntent().getStringExtra(StartingActivity.USERNAME);
        hasTasks = false;

        getLocationPermission();
    }

    /**
     * USed to calculate score
     * @param data
     * @return
     */
    private double calculateScore(TripModel data)
    {
        int timesCoughed = 0;
        int totalDevices = 0;
        double score = 0.0;
        if(data.getTripReadings() != null)
        {
            for(ReadingModel r : data.getTripReadings())
            {
                timesCoughed += r.isCoughDetected() ? 1 : 0;
                totalDevices += r.getNumDevicesDetected();
            }
        }
        score = ((double)(4*timesCoughed) + (double)totalDevices)/5;
        return score;
    }

    /**
     * Writes to file
     * @param data
     * @param context
     */
    private void writeToFile(TripModel data, Context context){
        InputStreamReader inputStreamReader= null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter= null;
        OutputStreamWriter outputStreamWriter= null;
        InputStream inputStream = null;
        try {
            File file = context.getFileStreamPath("user.json");
            Gson gson = new GsonBuilder().setDateFormat("MMM d, yyyy HH:mm:ss").create();;
            if(file.exists())
            {
                inputStream = context.openFileInput("user.json");
                if ( inputStream != null ) {
                    inputStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    UserModel user = gson.fromJson(bufferedReader, new TypeToken<UserModel>(){}.getType());
                    List<TripModel> tripsList = user.getTrips();
                    if(tripsList == null)
                    {
                        tripsList = new ArrayList<>();
                    }
                    tripsList.add(data);
                    user.setTrips(tripsList);
                    double sumScore = 0.0;
                    for(TripModel trip : tripsList)
                    {
                        sumScore += trip.getScore();
                    }
                    user.setScore(sumScore/tripsList.size());
                    outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user.json", Context.MODE_PRIVATE));
                    bufferedWriter = new BufferedWriter(outputStreamWriter);
                    gson.toJson(user, bufferedWriter);
                }
            }
            else
            {
                outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user.json", Context.MODE_PRIVATE));
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                UserModel user = new UserModel();
                user.setId(UUID.randomUUID().toString());
                user.setUsername(username);
                List<TripModel> tripsList = new ArrayList<>();
                tripsList.add(data);
                user.setTrips(tripsList);
                user.setScore(data.getScore());
                gson.toJson(user, bufferedWriter);
            }

        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.getMessage());
        }
        finally {
            try
            {
                if(bufferedReader != null)
                {
                    bufferedReader.close();
                }
                if(bufferedWriter != null)
                {
                    bufferedWriter.close();
                }
                if(outputStreamWriter != null)
                {
                    outputStreamWriter.close();
                }
                if(inputStreamReader != null)
                {
                    inputStreamReader.close();
                }
                if(inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
//        IntentFilter filter = new IntentFilter("ServiceBroadcast");
//        this.registerReceiver(receiver, filter);
    }
    @Override
    protected void onPause()
    {
        super.onPause();
//        if(receiver !=  null)
//            this.unregisterReceiver(receiver);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            handler.removeCallbacks(serviceRunnable);
            Intent stopTripIntent = new Intent(this, MainActivity.class);
            stopTripIntent.putExtra(StartingActivity.USERNAME, username);
            startActivity(stopTripIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister broadcast listeners
        if(receiver !=  null)
            this.unregisterReceiver(receiver);
        if(handler != null)
            handler.removeCallbacks(serviceRunnable);
    }
    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Called when the trip ends
     * @param view
     */
    public void onEndTripClick(View view)
    {
        Toast.makeText(this, "Stopping trip...", Toast.LENGTH_SHORT).show();
        if(currentTripID != null)
        {
            currentTrip.setEndTime(new Date());
            handler.removeCallbacks(serviceRunnable);
            double score = 0.0;
            currentTrip.setScore(score);
            long millis = Math.abs(currentTrip.getEndTime().getTime() - currentTrip.getStartTime().getTime());
            String _duration = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            score = ((double)number)/5;
            String _score = Double.toString(score);
            String _numDevices = Long.toString(number);
            String _numPlaces = currentTrip.getTripReadings() != null ? Integer.toString(currentTrip.getTripReadings().size()) : "0";
            writeToFile(currentTrip, MapsActivityCurrentPlace.this);
            Intent stopTripIntent = new Intent(this, EndTrip.class);
            stopTripIntent.putExtra(StartingActivity.USERNAME, username);
            stopTripIntent.putExtra(DURATION, _duration);
            stopTripIntent.putExtra(NUMDEVICES, _numDevices);
            stopTripIntent.putExtra(NUMPLACES, _numPlaces);
            stopTripIntent.putExtra(SCORE, _score);

            final int[] id = {0};
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    person = snapshot.getValue(Person.class);
                    myHistory = person != null ? person.getMyPost() : null;


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            firebaseDatabase = FirebaseDatabase.getInstance();
            generationCodeReference = firebaseDatabase.getReference("GenerationCode");

            //Get initial value of Code Generator on app startup
            generationCodeReference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    generationCodeValue = (long) Objects.requireNonNull(dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            history = new History(place,_duration,_numDevices,_score);

            HashMap<String, Object> hashMap = new HashMap<>();

            History tempPost = null;
            try {
                tempPost = (History) history.clone();

            } catch (Exception ignored) {

            }

            firebaseDatabase = FirebaseDatabase.getInstance();
            String historyId = String.valueOf(generationCodeValue);
            hashMap.put("/Users/" + user.getUid() + "/myHistory/" + generationCodeValue + "_history", tempPost);

            firebaseDatabase.getReference().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                @SuppressLint("ShowToast")
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(),"History Added",Toast.LENGTH_LONG);
                        generationCodeValue = generationCodeValue + 1;
                        // increase Value of generationCode Everytime a new machine is entered.
                        generationCodeReference.setValue(generationCodeValue);
                    }else{
                        Log.i("abc","its visible");
                    }
                }

            });
            startActivity(stopTripIntent);
        }
        else
        {
            Intent stopTripIntent = new Intent(this, MainActivity.class);
            stopTripIntent.putExtra(StartingActivity.USERNAME, username);
            startActivity(stopTripIntent);
        }
        finish();
    }

    /**
     * Receiver to receive broadcasts from bluetooth.
     */
    private MediaPlayer mp;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive()");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                btDevicesCount = bundle.getInt(BluetoothService.EXTRA_DEVICE_COUNT);
                number+= btDevicesCount;
                if(btDevicesCount >= crowdThreshold)
                {
                    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    mp = MediaPlayer.create(getApplicationContext(), raw.alarm_sound);
                    if (vibrator != null && vibrator.hasVibrator()) {
                        vibrator.vibrate(5000);
                        dialog.show();

                        mp.start();
                        Toast.makeText(getApplicationContext(),"Device in Threshold",Toast.LENGTH_LONG);
                    }

                }

                // add marker
                if(prevPlaceName == null || !prevPlaceName.equals(mPlaceName))
                {
                    markCurrentPlace();
                }
            }
        }
    };

    /**
     * Periodically calls the service
     */
    Runnable serviceRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Handlers", "Called on main thread");
            // Repeat this the same runnable code block again another 2 seconds
            // 'this' is referencing the Runnable object
            Intent serverIntent = new Intent(MapsActivityCurrentPlace.this, BluetoothService.class);
            hasTasks = true;
            startService(serverIntent);
            handler.postDelayed(this, 120000);
        }
    };

    /**
     * After permission result for bt
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult()");
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == Activity.RESULT_OK)
                {
                    String tripID = UUID.randomUUID().toString();
                    currentTrip = new TripModel();
                    currentTrip.setTripId(tripID);
                    currentTrip.setStartTime(new Date());
                    currentTripID = tripID;
                    handler = new Handler();
                    handler.post(serviceRunnable);
                }
                else
                {
                    Toast.makeText(this, "Bluetooth is required for this task", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(layout.custom_info_contents,
                        (FrameLayout) findViewById(id.map), false);

                TextView title = infoWindow.findViewById(id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        //start Trip
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            String tripID = UUID.randomUUID().toString();
            currentTrip = new TripModel();
            currentTrip.setTripId(tripID);
            currentTrip.setStartTime(new Date());
            currentTripID = tripID;
            handler = new Handler();
            // Define the code block to be executed
            // Start the initial runnable task by posting through the handler
            handler.post(serviceRunnable);
        }
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void markCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                    mPlacesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();
                        if(likelyPlaces.getPlaceLikelihoods().size() > 0)
                        {
                            double maxLikelihoodVal = 0.0;
                            PlaceLikelihood maxLikelihood = null;
                            for(PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods())
                            {
                                if(placeLikelihood.getLikelihood() > maxLikelihoodVal)
                                {
                                    maxLikelihoodVal = placeLikelihood.getLikelihood();
                                    maxLikelihood = placeLikelihood;
                                }
                            }
                            if(maxLikelihood == null)
                            {
                                return;
                            }
                            mPlaceName = maxLikelihood.getPlace().getName();
                            mPlaceAddress = maxLikelihood.getPlace().getAddress();
                            mPlaceAttributions = maxLikelihood.getPlace().getAttributions();
                            mPlaceLatLng = maxLikelihood.getPlace().getLatLng();
                        }

                        markerLatLng = mPlaceLatLng;
                        markerSnippet = mPlaceAddress;
                        markerPlaceName = mPlaceName;

                        if (mPlaceAttributions != null) {
                            markerSnippet = markerSnippet + "\n" + mPlaceAttributions;
                        }
                        // Add a marker for the selected place, with an info window
                        // showing information about that place.
                        mMap.addMarker(new MarkerOptions()
                                .title(markerPlaceName)
                                .position(markerLatLng)
                                .snippet(markerSnippet));

                        String readingId = UUID.randomUUID().toString();
                        ReadingModel reading = new ReadingModel();
                        reading.setId(readingId);
                        reading.setTimestamp(new Date());
                        reading.setNumDevicesDetected(btDevicesCount);
                        reading.setLatlng(Double.toString(mPlaceLatLng.latitude)+","+Double.toString(mPlaceLatLng.longitude));
                        reading.setPlaceAddress(mPlaceAddress);
                        reading.setPlaceName(mPlaceName);
                        if(currentTrip.getTripReadings() == null)
                        {
                            currentTrip.setTripReadings(new ArrayList<ReadingModel>());
                        }
                        currentTrip.getTripReadings().add(reading);
                        currentTrip.setNumDevices(currentTrip.getNumDevices()+btDevicesCount);
                        prevPlaceName = mPlaceName;
                        hasTasks = false;
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
