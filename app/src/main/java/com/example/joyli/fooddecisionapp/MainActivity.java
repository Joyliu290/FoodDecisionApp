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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.wheelmenu.WheelMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.joyli.fooddecisionapp.R.id.wheelMenu;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private WheelMenu wheelMenu;
    private TextView selectedPositionText;

    private static final int MY_PERMISSION_REQUEST_CODE=7171;
    private static final int PLAY_SERVICE_RESOLUTION_REQUEST=7172;
    private TextView txtCoordinates;
    private Button btnGetCoordinates, btnLocationUpdates;
    private boolean mRequestingLocationUpdates=false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCoordinates = (TextView)findViewById(R.id.txtCoordinates);
        btnGetCoordinates=(Button)findViewById(R.id.btnGetCoordinates);
        btnLocationUpdates=(Button)findViewById(R.id.btnTrackLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED )
        { //RUNTIME REQUEST PERMISSION

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            },MY_PERMISSION_REQUEST_CODE);

        }

        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        wheelMenu = (WheelMenu) findViewById(R.id.wheelMenu);
        wheelMenu.setDivCount(12);
        wheelMenu.setWheelImage(R.drawable.wheel);
        selectedPositionText =(TextView) findViewById (R.id.selected_position_text);
        selectedPositionText.setText("selected: " + (wheelMenu.getSelectedPosition()+1));

        wheelMenu.setWheelChangeListener(new WheelMenu.WheelChangeListener() {
            @Override
            public void onSelectionChange(int i) {
                if (i+1==1)
                {
                    selectedPositionText.setText("selected: Korean Food");
                }

                else if (i+1==2)
                {
                    selectedPositionText.setText("selected: Japanese Food");
                }

                else if (i+1==3)
                {
                    selectedPositionText.setText("selected: Western Food");

                }

                else if (i+1==4)
                {
                    selectedPositionText.setText("selected: Chinese Food");
                }

                else if (i+1==5)
                {
                    selectedPositionText.setText("selected: Italian Food");
                }

                else if (i+1==6)
                {
                    selectedPositionText.setText("selected: Thai Food");
                }

                else if (i+1==7)
                {
                    selectedPositionText.setText("selected: Vietnamese Food");
                }

                else if (i+1==8)
                {
                    selectedPositionText.setText("selected: Fast Food");
                }

                else if (i+1==9)
                {
                    selectedPositionText.setText("selected: Cafe");
                }

                else if (i+1==10)
                {
                    selectedPositionText.setText("selected: Buffet");
                }

                else if (i+1==11)
                {
                    selectedPositionText.setText("selected: Dessert");
                }

                else
                {
                    selectedPositionText.setText("selected: Greek Food");
                }

            }
        });


    }

    private void createLocationRequest() {
        mLocationRequest= new LocationRequest();
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
        if (resultCode!= ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RESOLUTION_REQUEST).show();

            }

            else
            {
                Toast.makeText(getApplicationContext(),"This device is not support", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
