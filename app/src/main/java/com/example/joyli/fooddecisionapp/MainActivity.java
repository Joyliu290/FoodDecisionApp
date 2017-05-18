package com.example.joyli.fooddecisionapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.wheelmenu.WheelMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView mRestaurantTitle, mRate;
    ImageView mMainImage;
    OkHttpClient mClient;
    List<Restaurantdb> mRestaurants;
    int i;
    ProgressBar mLoading;
    boolean waiting = false;

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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    YelpAPIFactory mApiFactory;
    YelpAPI mYelpAPI;
    Map<String, String> mParams;

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

        mRestaurantTitle = (TextView)findViewById(R.id.foodName);
        mRate=(TextView)findViewById(R.id.rating);
        mMainImage=(ImageView)findViewById(R.id.mainImage);
        mApiFactory = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI = mApiFactory.createAPI();
        mParams = new HashMap<>();
        mParams.put("term", "food");
        mClient = new OkHttpClient();
        mRestaurants = new ArrayList<>();
        mLoading=(ProgressBar)findViewById(R.id.loading);
        i=0;

        new FetchPictures().execute();
        waitForRestaurant(true);

    }


    private void tooglePeriodicLoctionUpdates() {
        if (!mRequestingLocationUpdates) {
            btnLocationUpdates.setText("Stop Location update");
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        } else {
            btnLocationUpdates.setText("Start Location update");
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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
        mLastLocation = location;
        displayLocation();
    }

    synchronized public void waitForRestaurant(boolean client){
        if (client) {
            if (mRestaurants.size()>i && mRestaurants.get(i).getPictures().size()>mRestaurants.get(i).getCurrPic()){
                //have the data
                restaurantCallback();
            }
            else {
                waiting = true;
                mLoading.setVisibility(View.VISIBLE);

            }
        }
        else {
            if (waiting){
                restaurantCallback();
                waiting = false;
                mLoading.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void restaurantCallback() {
        displayRestaurant(mRestaurants.get(i));

    }

    private void displayRestaurant(Restaurantdb r) {
        Picasso
                .with (this)
                .load(r.getPictures().get(r.getCurrPic()))
                .resize(50,50)
                .into (mMainImage);
        mRestaurantTitle.setText(r.getName());
        mRate.setText(r.getRating());

    }

    class FetchPictures extends AsyncTask<String, Restaurantdb,String> {

        List<Restaurantdb> restaurants=null;

        @Override
        protected void onProgressUpdate(Restaurantdb...values){
            super.onProgressUpdate(values);
            mRestaurants.add(values[0]);
            waitForRestaurant(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(37.7577)
                    .longitude(-122.4376).build();
            Call<SearchResponse> call = mYelpAPI.search(coordinate, mParams);
            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null) {
                Log.v("Businesses", response.body().businesses().toString());
                restaurants = new ArrayList<>();
                List<Business> businessList = response.body().businesses();
                Restaurantdb r;
                int i=0;
                for (Business b : businessList) {
                    r = new Restaurantdb(b.name(), b.url());
                    r.setRating(b.rating() + "" + catToString(b.categories()));
                    restaurants.add(r);
                    fetchPictures(r,i);
                    i++;
                }
            }
            return null;
        }

        private String catToString(ArrayList<Category> categories) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < categories.size(); i++) {
                sb.append(categories.get(i).name());
                if (i != categories.size() - 1) {
                    sb.append(", ");
                }
            }

            return sb.toString();
        }

        private void fetchPictures(Restaurantdb r, final int pos) {

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(r.getPicUrl())
                    .build();

            mClient.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                    List<String> pictures = RestaurantScraper.getPictures(response.body().string());
                    if (pictures.size()>0) {
                        restaurants.get(pos).setPictures(pictures);
                        publishProgress(restaurants.get(pos));
                    }

                }
            });

        }
    }


}
