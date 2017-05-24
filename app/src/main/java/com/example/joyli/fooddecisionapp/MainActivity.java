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

    TextView mRestaurantTitle, mRate, mRestaurantTitle2, mRate2;
    ImageView mMainImage;
    ImageView mMainImage2;
    OkHttpClient mClient, mClient2;
    private List<Restaurantdb> mRestaurants, mRestaurants2 = new ArrayList<>();
    int i;
    ProgressBar mLoading, mLoading2;
    boolean waiting = false;
    final private String iLastKey = "ILAST_KEY";
    final private String iKey = "I_KEY";
    final private String restaurantsKey = "RESTAURANTS_KEY";

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
    private int iLast=0;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    int pageNum=5;
    boolean newSession = false;

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

    YelpAPIFactory mApiFactory, mApiFactory2;
    YelpAPI mYelpAPI, mYelpAPI2;
    Map<String, String> mParams, mParams2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        btnGetCoordinates = (Button) findViewById(R.id.btnGetCoordinates);
        btnLocationUpdates = (Button) findViewById(R.id.btnTrackLocation);
        mRestaurantTitle = (TextView)findViewById(R.id.foodName);
        mRate=(TextView)findViewById(R.id.rating);
        mRate2=(TextView)findViewById(R.id.rating2);
        mRestaurantTitle2=(TextView)findViewById(R.id.foodName2);
        mMainImage=(ImageView)findViewById(R.id.mainImage);
        mMainImage2=(ImageView)findViewById(R.id.mainImage2);

        mApiFactory = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI = mApiFactory.createAPI();
        mApiFactory2 = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI2 = mApiFactory2.createAPI();
        mParams = new HashMap<>();
        mParams2=new HashMap<>();

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


        if (i+1==1){
            mParams.put("term", "korean");
            mParams2.put("term", "korean");



        }

        else if (i+1==2){
            mParams.put("term", "japanese");
        }

        else if (i+1==3){
            mParams.put("term", "western");
        }

        else if (i+1==4){
            mParams.put("term", "chinese");
        }

        else if (i+1==5){
            mParams.put("term", "italian");
        }

        else if (i+1==6){
            mParams.put("term", "thai");
        }

        else if (i+1==7){
            mParams.put("term", "vietnamese");
        }

        else if (i+1==8){
            mParams.put("term", "fast+food");
        }

        else if (i+1==9){
            mParams.put("term", "cafe");
        }

        else if (i+1==10){
            mParams.put("term", "buffet");
        }
        else if (i+1==11){
            mParams.put("term", "dessert");
        }
        else if (i+1==12){
            mParams.put("term", "greek");
        }

        mClient = new OkHttpClient();
        mClient2 =new OkHttpClient();
        mRestaurants = new ArrayList<>();
        mRestaurants2=new ArrayList<>();
        mLoading=(ProgressBar)findViewById(R.id.loading);
        mLoading2=(ProgressBar)findViewById(R.id.loading2);
        i=0;

        new FetchPictures().execute("0");
        waitForRestaurant(true);
        new FetchPictures2().execute();
        waitForRestaurant2(true);


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
                .into (mMainImage);
        mRestaurantTitle.setText(r.getName());
        mRate.setText(r.getRating());

    }

    synchronized public void waitForRestaurant2(boolean client2){

        if (client2) {

            if (mRestaurants.size()>0 && mRestaurants.get(0).getPictures().size()>mRestaurants.get(0).getCurrPic()){
                //have the data
                restaurantCallback2();
            }
            else {
                waiting = true;
                mLoading2.setVisibility(View.VISIBLE);

            }
        }
        else {

            if (!waiting){
                Log.v("debug", "hurry");
                restaurantCallback2();
                waiting = false;
                mLoading2.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void restaurantCallback2() {
        displayRestaurant2(mRestaurants.get(i));
    }

    private void displayRestaurant2(Restaurantdb r2) {

        Picasso
                .with(this)
                .load(r2.getPictures().get(r2.getCurrPic()))
                .into (mMainImage2);
        mRestaurantTitle2.setText(r2.getName());
        mRate2.setText(r2.getRating());

    }

    private void newRestaurant(){
        if (mRestaurants.size()>i){
            i++;
            waitForRestaurant2(true);
            if (i-iLast>5&&mRestaurants.size()-i<7){
                iLast = i;
                new FetchPictures2().execute(""+pageNum);
                pageNum+=5;
            }
        }
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
        protected String doInBackground(String... params) {
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(43.8581437)
                    .longitude(-79.2902573).build();
            mParams.put("offset", params[0]);
            retrofit2.Call<SearchResponse> call = mYelpAPI.search(coordinate, mParams);
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
                    r = new Restaurantdb("   Restaurant Name: " + b.name(), b.url());
                    r.setRating("   Rating: " + b.rating() + "" );
                    restaurants.add(r);
                    fetchPictures(r,i);
                    i++;
                }
            }
            return null;
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

    class FetchPictures2 extends AsyncTask<String, Restaurantdb,String> {

        List<Restaurantdb> restaurants2=null;

        @Override
        protected void onProgressUpdate(Restaurantdb...values2){
            super.onProgressUpdate(values2);
            mRestaurants2.add(values2[0]);
            waitForRestaurant2(false);
        }

        @Override
        protected String doInBackground(String... params) {
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(43.8581437)
                    .longitude(-79.2902573).build();
            retrofit2.Call<SearchResponse> call2 = mYelpAPI2.search(coordinate, mParams2);
            retrofit2.Response<SearchResponse> response2 = null;
            try {
                response2 = call2.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response2 != null) {
                Log.v("Businesses", response2.body().businesses().toString());
                restaurants2 = new ArrayList<>();
                List<Business> businessList2 = response2.body().businesses();
                Restaurantdb r2;
                int i=0;
                for (Business b2 : businessList2) {
                    /**Log.v("Businesses", response2.body().businesses().toString()); **/
                    r2 = new Restaurantdb("   Restaurant Name: " + b2.name(), b2.url());
                    r2.setRating("   Rating: " + b2.rating() + "" );
                    restaurants2.add(r2);
                    fetchPictures2(r2,i);
                    i++;
                }
            }
            return null;
        }

        private void fetchPictures2(Restaurantdb r2, final int pos2) {

            okhttp3.Request request2 = new okhttp3.Request.Builder()
                    .url(r2.getPicUrl())
                    .build();

            mClient2.newCall(request2).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response2) throws IOException {

                    List<String> pictures2 = RestaurantScraper.getPictures(response2.body().string());
                    if (pictures2.size()>0) {
                        restaurants2.get(pos2).setPictures(pictures2);
                        publishProgress(restaurants2.get(pos2));
                    }

                }
            });

        }
    }





}
