package com.example.joyli.fooddecisionapp;

import android.Manifest;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anupcowkur.wheelmenu.WheelMenu;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.analytics.HitBuilders;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.*;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    SQLdatabaseActivity myDB;
    TextView mRestaurantTitle, mRate, mRestaurantTitle2, mRate2, mRate3, mRestaurantTitle3, mLocation,mLocation2,mLocation3;
    ImageView mMainImage;
    ImageView mMainImage2, mMainImage3;
    OkHttpClient mClient, mClient2, mClient3;
    private ArrayList<Restaurantdb> mRestaurants;
    private List<Restaurantdb> mRestaurants2 = new ArrayList<>();
    private List<Restaurantdb> mRestaurants3 = new ArrayList<>();
    int i;
    ProgressBar mLoading, mLoading2, mLoading3;
    boolean waiting = false;
    Button pressMe, button_view;
    private WheelMenu wheelMenu;
    private TextView selectedPositionText;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    //private TextView txtCoordinates;
    //private Button btnGetCoordinates, btnLocationUpdates;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private int iLast=0;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    int pageNum=40;
    boolean choiceIsMade = false;

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

    YelpAPIFactory mApiFactory, mApiFactory2, mApiFactory3;
    YelpAPI mYelpAPI, mYelpAPI2, mYelpAPI3;
    Map<String, String> mParams, mParams2, mParams3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        //btnGetCoordinates = (Button) findViewById(R.id.btnGetCoordinates);
        //btnLocationUpdates = (Button) findViewById(R.id.btnTrackLocation);
        mRestaurantTitle = (TextView) findViewById(R.id.foodName);
        mRate = (TextView) findViewById(R.id.rating);
        mRate2 = (TextView) findViewById(R.id.rating2);
        mRate3 = (TextView) findViewById(R.id.rating3);
        mRestaurantTitle2 = (TextView) findViewById(R.id.foodName2);
        mRestaurantTitle3 = (TextView) findViewById(R.id.foodName3);
        mMainImage = (ImageView) findViewById(R.id.mainImage);
        mMainImage2 = (ImageView) findViewById(R.id.mainImage2);
        mMainImage3 = (ImageView) findViewById(R.id.mainImage3);
        mLocation = (TextView) findViewById(R.id.location);
        mLocation2 = (TextView) findViewById(R.id.location2);
        mLocation3 = (TextView) findViewById(R.id.location3);

        selectedPositionText = (TextView) findViewById(R.id.selected_position_text);

        mApiFactory = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI = mApiFactory.createAPI();
        mApiFactory2 = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI2 = mApiFactory2.createAPI();
        mApiFactory3 = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI3 = mApiFactory2.createAPI();
        mParams = new HashMap<>();
        mParams2 = new HashMap<>();
        mParams3 = new HashMap<>();
        Button pressMe = (Button) findViewById(R.id.pressMe);
        mClient = new OkHttpClient();
        mClient2 = new OkHttpClient();
        mClient3 = new OkHttpClient();
        mRestaurants = new ArrayList<>();
        mRestaurants2 = new ArrayList<>();
        mLoading = (ProgressBar) findViewById(R.id.loading);
        mLoading2 = (ProgressBar) findViewById(R.id.loading2);
        mLoading3 = (ProgressBar) findViewById(R.id.loading3);


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
    }

  /**      btnGetCoordinates.setOnClickListener(new View.OnClickListener() {

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
   **/

  public void clickMeClick (View view) {

        i=0;
        int j=0;

        Random r = new Random();
        int randomNum=r.nextInt(12-1)+1;

        if (randomNum == 1) {
            selectedPositionText.setText("Selected: Korean Food");
        } else if (randomNum == 2) {
            selectedPositionText.setText("Selected: Japanese Food");
        } else if (randomNum == 3) {
            selectedPositionText.setText("Selected: Indian Food");

        } else if (randomNum == 4) {
            selectedPositionText.setText("Selected: Chinese Food");
        } else if (randomNum == 5) {
            selectedPositionText.setText("Selected: Italian Food");
        } else if (randomNum == 6) {
            selectedPositionText.setText("Selected: Thai Food");
        } else if (randomNum == 7) {
            selectedPositionText.setText("Selected: Vietnamese Food");
        } else if (randomNum == 8) {
            selectedPositionText.setText("Selected: Fast Food");
        } else if (randomNum == 9) {
            selectedPositionText.setText("Selected: Cafe");
        } else if (randomNum == 10) {
            selectedPositionText.setText("Selected: Buffet");
        } else if (randomNum == 11) {
            selectedPositionText.setText("Selected: Dessert");
        } else {
            selectedPositionText.setText("Selected: Greek Food");
        }

        //parameters for yelp
        if (randomNum==1){
            mParams.put("term", "korean");
            //mParams2.put("limit", "5");
            mParams2.put("term", "korean");
            mParams3.put("term", "korean");
            choiceIsMade=true;

        }

        else if (randomNum==2){
            mParams.put("term", "japanese");
            mParams2.put("term", "japanese");
            mParams3.put("term", "japnese");
            choiceIsMade=true;
        }

        else if (randomNum==3){
            mParams.put("term", "indian");
            mParams2.put("term", "indian");
            mParams3.put("term", "indian");
            choiceIsMade=true;
        }

        else if (randomNum==4){
            mParams.put("term", "chinese");
            mParams2.put("term", "chinese");
            mParams3.put("term", "chinese");
            choiceIsMade=true;
        }

        else if (randomNum==5){
            mParams.put("term", "italian");
            mParams2.put("term", "italian");
            mParams3.put("term", "italian");
            choiceIsMade=true;
        }

        else if (randomNum==6){
            mParams.put("term", "thai");
            mParams2.put("term", "thai");
            mParams3.put("term", "thai");
            choiceIsMade=true;
        }

        else if (randomNum==7){
            mParams.put("term", "vietnamese");
            mParams2.put("term", "vietnamese");
            mParams3.put("term", "vietnamese");
            choiceIsMade=true;
        }

        else if (randomNum==8){
            mParams.put("term", "fast+food");
            mParams2.put("term", "fast+food");
            mParams3.put("term", "fast+food");
            choiceIsMade=true;
        }

        else if (randomNum==9){
            mParams.put("term", "cafe");
            mParams2.put("term", "cafe");
            mParams3.put("term", "cafe");
            choiceIsMade=true;
        }

        else if (randomNum==10){
            mParams.put("term", "buffet");
            mParams2.put("term", "buffet");
            mParams3.put("term", "buffet");
            choiceIsMade=true;
        }
        else if (randomNum==11){
            mParams.put("term", "dessert");
            mParams2.put("term", "dessert");
            mParams3.put("term", "dessert");
            choiceIsMade=true;
        }
        else {
            mParams.put("term", "greek");
            mParams2.put("term", "greek");
            mParams3.put("term", "greek");
            choiceIsMade=true;
        }

        if (choiceIsMade==true) {

            new FetchPictures().execute("0");
            waitForRestaurant(true);
            //newRestaurant();
            waitForRestaurant2(true);

            new FetchPictures().execute("5");

            while (new FetchPictures().execute(Integer.toString(i)) == new FetchPictures2().execute(Integer.toString(i))) {
                Log.v("repeat", "hi");
                i++;
            }
            new FetchPictures2().execute(Integer.toString(i));

            waitForRestaurant3(true);

            while (new FetchPictures3().execute(Integer.toString(j)) == new FetchPictures().execute("0") && new FetchPictures3().execute(Integer.toString(j)) == new FetchPictures2().execute(Integer.toString(i))) {
                Log.v("repeat", "hi");
                j++;
            }
            new FetchPictures3().execute(Integer.toString(j));


        }

  }

  public void clickMeClick2(View view){
      Intent viewlist = new Intent ("com.example.joyli.fooddecisionapp.ViewList");
      startActivity(viewlist);

  }

    public void clickMeClickSave (View view){
        myDB = new SQLdatabaseActivity(this);


        String newEntry = mRestaurantTitle.getText().toString();
        if (mRestaurantTitle.length() != 0) {
            Log.v("show", "show");
            Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();
            AddData(newEntry);

        } else {
            Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();

    }
    public void clickMeClickSave2 (View view){
        myDB = new SQLdatabaseActivity(this);


        String newEntry = mRestaurantTitle2.getText().toString();
        if (mRestaurantTitle2.length() != 0) {
            Log.v("show", "show");
            Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();
            AddData(newEntry);

        } else {
            Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();
    }
    public void clickMeClickSave3 (View view){
        myDB = new SQLdatabaseActivity(this);


        String newEntry = mRestaurantTitle3.getText().toString();
        if (mRestaurantTitle3.length() != 0) {
            Log.v("show", "show");
            Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();
            AddData(newEntry);

        } else {
            Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(getApplicationContext(), "Restaurant is Saved", Toast.LENGTH_LONG).show();
    }

    public void AddData (String newEntry) {
        boolean insertData = myDB.addData(newEntry);

        if (insertData ==true) {

            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }
        else {

            Toast.makeText(this, "Something went wrong",Toast.LENGTH_LONG).show();
        }

    }

    public CoordinateOptions yelpLocationUpdate(double latitude, double longitude){
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude).build();
        return coordinate;
    }

    private void tooglePeriodicLoctionUpdates() {
        if (!mRequestingLocationUpdates) {
           // btnLocationUpdates.setText("Stop Location update");
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        } else {
            //btnLocationUpdates.setText("Start Location update");
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public double displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return 0.0;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            return latitude;

        } else
            Toast.makeText(getApplicationContext(), "Could not get location. Make sure Location is Enabled on Device", Toast.LENGTH_LONG).show();
        return 0.0;

    }

    public double displayLocation2() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0.0;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            return longitude;

        } else
            Toast.makeText(getApplicationContext(), "Could not get location. Make sure Location is Enabled on Device", Toast.LENGTH_LONG).show();
        return 0.0;

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
                Log.v("debug", "appear");
                mLoading.setVisibility(View.VISIBLE);

            }
        }
        else {
            if (waiting){
                Log.v("debug", "hurry");
                restaurantCallback();
                waiting = false;
                mLoading.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void restaurantCallback() {displayRestaurant(mRestaurants.get(0));
    }

    private void displayRestaurant(Restaurantdb r) {

        Log.v("appear", "appear");
        Picasso
                .with (this)
                .load(r.getPictures().get(r.getCurrPic()))
                .into (mMainImage);
        mRestaurantTitle.setText(r.getName());
        mRate.setText(r.getRating());
        mLocation.setText(r.getLocation());

    }

    synchronized public void waitForRestaurant2(boolean client2){

        //Log.v("debug", "hurry");
        if (client2) {

            if (mRestaurants2.size()>i && mRestaurants2.get(i).getPictures().size()>mRestaurants2.get(i).getCurrPic()){
                //have the data
                restaurantCallback2();
            }
            else {
                waiting = true;
                Log.v("2debug", "hurry");
                mLoading2.setVisibility(View.VISIBLE);
            }
        }
        else {

            if (!waiting){
                 Log.v("2debug2", "hurry");
                restaurantCallback2();
                waiting = false;
                mLoading2.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void restaurantCallback2() {
        displayRestaurant2(mRestaurants2.get(i));
    }

    private void displayRestaurant2(Restaurantdb r2) {
        Log.v("appear", "appear");
        Picasso
                .with(this)
                .load(r2.getPictures().get(r2.getCurrPic()))
                .into (mMainImage2);
        mRestaurantTitle2.setText(r2.getName());
        mRate2.setText(r2.getRating());
        mLocation2.setText(r2.getLocation());

    }

    synchronized public void waitForRestaurant3(boolean client3){

        if (client3) {


            if (mRestaurants3.size()>i && mRestaurants3.get(i).getPictures().size()>mRestaurants3.get(i).getCurrPic()){
                //have the data
                restaurantCallback3();
            }
            else {
                Log.v("3","appear");
                waiting = true;
                mLoading3.setVisibility(View.VISIBLE);

            }
        }
        else {

            if (!waiting){
                Log.v("3","hi");
                restaurantCallback3();
                waiting = false;
                mLoading3.setVisibility(View.INVISIBLE);
            }

        }

    }

    private void restaurantCallback3() {
        displayRestaurant3(mRestaurants3.get(i));
    }

    private void displayRestaurant3(Restaurantdb r3) {
        Log.v("appear", "appear");
        Picasso
                .with(this)
                .load(r3.getPictures().get(r3.getCurrPic()))
                .into (mMainImage3);
        mRestaurantTitle3.setText(r3.getName());
        mRate3.setText(r3.getRating());
        mLocation3.setText(r3.getLocation());

    }


    class FetchPictures extends AsyncTask<String, Restaurantdb,String> {

        List<Restaurantdb> restaurants=null;

        @Override
        protected void onProgressUpdate(Restaurantdb...values){
            super.onProgressUpdate(values);
            mRestaurants.add(values[0]);
            //Log.v("mRest", Integer.toString(mRestaurants.size()));
            waitForRestaurant(false);
        }

        @Override
        protected String doInBackground(String... params) {
          /**  CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(43.8581437)
                    .longitude(-79.2902573).build();

           **/
            retrofit2.Call<SearchResponse> call = mYelpAPI.search(yelpLocationUpdate(displayLocation(),displayLocation2()), mParams);
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
                    r = new Restaurantdb("   Restaurant Name: " + b.name(), b.url()); //3 spaces
                    r.setRating("   Rating: " + b.rating());
                    r.setLocation("   Address: " + b.location().displayAddress().toString());
                    restaurants.add(r);
                    fetchPictures(r,i);
                    i++;
                }
                //Log.v("rest", Integer.toString(restaurants.size()));
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
            mParams2.put("offset",params[0]);
            retrofit2.Call<SearchResponse> call2 = mYelpAPI2.search(yelpLocationUpdate(displayLocation(),displayLocation2()), mParams2);
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
                    Log.v("Businesses", response2.body().businesses().toString());
                    r2 = new Restaurantdb("   Restaurant Name: " + b2.name(), b2.url());
                    r2.setRating("   Rating: " + b2.rating() );
                    r2.setLocation("   Address: "+ b2.location().displayAddress().toString());

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

    class FetchPictures3 extends AsyncTask<String, Restaurantdb,String> {

        List<Restaurantdb> restaurants3 = null;

        @Override
        protected void onProgressUpdate(Restaurantdb... values3) {
            super.onProgressUpdate(values3);
            mRestaurants3.add(values3[0]);
            waitForRestaurant3(false);
        }

        @Override
        protected String doInBackground(String... params) {
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(43.8581437)
                    .longitude(-79.2902573).build();
            mParams3.put("offset", params[0]);
            retrofit2.Call<SearchResponse> call3 = mYelpAPI3.search(yelpLocationUpdate(displayLocation(),displayLocation2()), mParams3);
            retrofit2.Response<SearchResponse> response3 = null;
            try {
                response3 = call3.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response3 != null) {
                Log.v("Businesses", response3.body().businesses().toString());
                restaurants3 = new ArrayList<>();
                List<Business> businessList2 = response3.body().businesses();
                Restaurantdb r3;
                int i = 0;
                for (Business b3 : businessList2) {
                    Log.v("Businesses", response3.body().businesses().toString());
                    r3 = new Restaurantdb("   Restaurant Name: " + b3.name(), b3.url());
                    r3.setRating("   Rating: " + b3.rating());
                    r3.setLocation("   Address: "+b3.location().displayAddress().toString());

                    restaurants3.add(r3);
                    fetchPictures3(r3, i);
                    i++;
                }
            }
            return null;
        }

        private void fetchPictures3(Restaurantdb r3, final int pos3) {

            okhttp3.Request request3 = new okhttp3.Request.Builder()
                    .url(r3.getPicUrl())
                    .build();

            mClient2.newCall(request3).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response3) throws IOException {

                    List<String> pictures2 = RestaurantScraper.getPictures(response3.body().string());
                    if (pictures2.size() > 0) {
                        restaurants3.get(pos3).setPictures(pictures2);
                        publishProgress(restaurants3.get(pos3));
                    }

                }
            });

        }
    }



}
