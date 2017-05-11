package com.example.joyli.fooddecisionapp;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.joyli.fooddecisionapp.AndroidVersion;
import com.anupcowkur.wheelmenu.WheelMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stanford.androidlib.*;

import static com.example.joyli.fooddecisionapp.R.id.wheelMenu;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, LoadJSON.Listener, AdapterView.OnItemClickListener {

    private ListView mListView;

    public static final String URL = "https://api.yelp.com/v3/businesses/search?term=korean&latitude=43.858081&longitude=-79.290339";
    private List<HashMap<String, String>> mAndroidMapList = new ArrayList<>();

    private static final String KEY_NAME = "name";
    private static final String KEY_RATING="rating";
    private static final String KEY_LOCATION= "location";

    private WheelMenu wheelMenu;
    private TextView selectedPositionText;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private TextView txtCoordinates;
    private Button btnGetCoordinates, btnLocationUpdates;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null)
        {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    public void foodClick(View view){
        Ion.with(this)
                .load("https://api.yelp.com/v3/businesses/search?term=korean&latitude=43.858081&longitude=-79.290339")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processName(result);
                    }
                });
    }

    private void processName(String result)
    {
        try {
            JSONObject json = new JSONObject(result);
            JSONArray a = json.getJSONArray("businesses");
            for (int i = 0; i < a.length(); i++) {
                JSONObject foodName = a.getJSONObject(i);
                String name = foodName.getString("name");
                loadName(name);
            }

        } catch (JSONException jsone) {
            Log.wtf("help", jsone);

        }
    }

    public void loadName (String name)
    {
        String[] NAME = new String[10];
        for (int i =0; i<10;i++)
        {
            NAME[i]=name;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        btnGetCoordinates = (Button) findViewById(R.id.btnGetCoordinates);
        btnLocationUpdates = (Button) findViewById(R.id.btnTrackLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //RUNTIME REQUEST PERMISSION

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }


        wheelMenu = (WheelMenu) findViewById(R.id.wheelMenu);
        wheelMenu.setDivCount(12);
        wheelMenu.setWheelImage(R.drawable.wheel);
        selectedPositionText = (TextView) findViewById(R.id.selected_position_text);
        selectedPositionText.setText("selected: " + (wheelMenu.getSelectedPosition() + 1));

        wheelMenu.setWheelChangeListener(new WheelMenu.WheelChangeListener() {
            @Override
            public void onSelectionChange(int i) {
                if (i + 1 == 1) {
                    selectedPositionText.setText("selected: Korean Food");
                } else if (i + 1 == 2) {
                    selectedPositionText.setText("selected: Japanese Food");
                } else if (i + 1 == 3) {
                    selectedPositionText.setText("selected: Western Food");

                } else if (i + 1 == 4) {
                    selectedPositionText.setText("selected: Chinese Food");
                } else if (i + 1 == 5) {
                    selectedPositionText.setText("selected: Italian Food");
                } else if (i + 1 == 6) {
                    selectedPositionText.setText("selected: Thai Food");
                } else if (i + 1 == 7) {
                    selectedPositionText.setText("selected: Vietnamese Food");
                } else if (i + 1 == 8) {
                    selectedPositionText.setText("selected: Fast Food");
                } else if (i + 1 == 9) {
                    selectedPositionText.setText("selected: Cafe");
                } else if (i + 1 == 10) {
                    selectedPositionText.setText("selected: Buffet");
                } else if (i + 1 == 11) {
                    selectedPositionText.setText("selected: Dessert");
                } else {
                    selectedPositionText.setText("selected: Greek Food");
                }

            }
        });

        btnGetCoordinates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                displayLocation();

            }
        });

        btnLocationUpdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                tooglePeriodicLoctionUpdates();
            }
        });

        mListView = (ListView) findViewById(R.id.foodlist);
        mListView.setOnItemClickListener(this);
        new LoadJSON(this).execute(URL);

    }

    @Override
    public void onLoaded(List<AndroidVersion> businessList){

        for (AndroidVersion businesses : businessList) {

            HashMap<String, String> map = new HashMap<>();

            map.put(KEY_NAME, businesses.getName());
            map.put(KEY_RATING,businesses.getRating());
            map.put(KEY_LOCATION, businesses.getLocation());

            mAndroidMapList.add(map);
        }

        loadListView();
    }

    @Override
    public void onError(){
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Toast.makeText(this, mAndroidMapList.get(i).get(KEY_RATING),Toast.LENGTH_LONG).show();

    }

    private void loadListView(){
        ListAdapter adapter = new SimpleAdapter(MainActivity.this, mAndroidMapList, R.layout.food_list_item,
                new String[]{KEY_NAME,KEY_RATING,KEY_LOCATION},
                new int[]{R.id.name, R.id.rating, R.id.location});

        mListView.setAdapter(adapter);
    }
    private void tooglePeriodicLoctionUpdates() {
        if (!mRequestingLocationUpdates)
        {
            btnLocationUpdates.setText("Stop Location update");
            mRequestingLocationUpdates=true;
            startLocationUpdates();
        }

        else
        {
            btnLocationUpdates.setText("Start Location update");
            mRequestingLocationUpdates=false;
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            txtCoordinates.setText(latitude + " / " + longitude);
        } else
            txtCoordinates.setText("Couldn't get the location. Make sure you enable the location on the device");

    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Toast.makeText(getApplicationContext(), "This device is not support", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        if (mRequestingLocationUpdates)
            startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        displayLocation();
    }



}
