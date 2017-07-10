package com.example.joyli.fooddecisionapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
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

    //SQLdatabaseActivity myDB;
    TextView mRestaurantTitle, mRestaurantTitle2, mRestaurantTitle3, mLocation,mLocation2,mLocation3, mReview, mReview2, mReview3;
    ImageView mMainImage,mMainImage2, mMainImage3, mRate, mLogo, mRate2, mRate3, mLogo3, mLogo2;
    OkHttpClient mClient, mClient2, mClient3;
    private ArrayList<Restaurantdb> mRestaurants= new ArrayList<>();
    private List<Restaurantdb> mRestaurants2 = new ArrayList<>();
    private List<Restaurantdb> mRestaurants3 = new ArrayList<>();
    int i,j;
    //ProgressBar mLoading, mLoading2, mLoading3;
    boolean waiting = false;
    private WheelMenu wheelMenu;
    private TextView selectedPositionText;

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

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
        mRate = (ImageView) findViewById(R.id.rating);
        mRate2 = (ImageView) findViewById(R.id.rating2);
        mRate3 = (ImageView) findViewById(R.id.rating3);
        mRestaurantTitle2 = (TextView) findViewById(R.id.foodName2);
        mRestaurantTitle3 = (TextView) findViewById(R.id.foodName3);
        mMainImage = (ImageView) findViewById(R.id.mainImage);
        mMainImage2 = (ImageView) findViewById(R.id.mainImage2);
        mMainImage3 = (ImageView) findViewById(R.id.mainImage3);
        mLocation = (TextView) findViewById(R.id.location);
        mLocation2 = (TextView) findViewById(R.id.location2);
        mLocation3 = (TextView) findViewById(R.id.location3);
        mReview = (TextView)findViewById(R.id.reviews);
        mReview2 = (TextView)findViewById(R.id.reviews2);
        mReview3 = (TextView)findViewById(R.id.reviews3);
        mLogo = (ImageView)findViewById(R.id.logo);
        mLogo2 = (ImageView)findViewById(R.id.logo2);
        mLogo3 = (ImageView)findViewById(R.id.logo3);

        selectedPositionText = (TextView) findViewById(R.id.selected_position_text);

        mApiFactory = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI = mApiFactory.createAPI();
        mApiFactory2 = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI2 = mApiFactory2.createAPI();
        mApiFactory3 = new YelpAPIFactory(getString(R.string.consumerKey), getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        mYelpAPI3 = mApiFactory3.createAPI();
        mParams = new HashMap<>();
        mParams2 = new HashMap<>();
        mParams3 = new HashMap<>();
        Button pressMe = (Button) findViewById(R.id.pressMe);
        mClient = new OkHttpClient();
        mClient2 = new OkHttpClient();
        mClient3 = new OkHttpClient();
        mRestaurants = new ArrayList<>();
        mRestaurants2 = new ArrayList<>();
        mRestaurants3 = new ArrayList<>();
        //mLoading = (ProgressBar) findViewById(R.id.loading);
       // mLoading2 = (ProgressBar) findViewById(R.id.loading2);
        //mLoading3 = (ProgressBar) findViewById(R.id.loading3);


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

  public void clickMeClick (View view) {
        i=0;
        j=0;
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

           // new FetchPictures().execute("5");

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

 /** public void clickMeClick2(View view){
      Intent viewlist = new Intent ("com.example.joyli.fooddecisionapp.ViewList");
      startActivity(viewlist);

  }

   /** public void clickMeClickSave (View view){
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

    } **/

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
            if (mRestaurants.size()>i){
                //have the data
                Log.v("have the data", "have the data");
                restaurantCallback();
            }
            else {
                waiting = true;
                Log.v("no data1", "no data1");
                //mLoading.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (waiting){
                Log.v("debug", "hurry");
                restaurantCallback();
                waiting = false;
                //mLoading.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void restaurantCallback() {displayRestaurant(mRestaurants.get(i));
    }

    private void displayRestaurant(Restaurantdb r) {
        Log.v("appear", "appear");
        Picasso
                .with (this)
                .load(r.getPicUrl())
                .into (mMainImage);
        mRestaurantTitle.setText(r.getName());
        mLocation.setText(r.getLocation());

        if (r.getRating().equals("0")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_0","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
       else if (r.getRating().equals("1")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
       else if (r.getRating().equals("1.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1_half","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("2")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("2.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2_half","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("3")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("3.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3_half","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("4")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else if (r.getRating().equals("4.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4_half","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        else{
            int drawableID= this.getResources().getIdentifier ("stars_regular_5","drawable", getPackageName());
            mRate.setImageResource(drawableID);
        }
        mReview.setText(r.getReviews());
        int logoID = this.getResources().getIdentifier("yelp_trademark_rgb_outline", "drawable", getPackageName());
        mLogo.setImageResource(logoID);
    }

    public void clickMeClickLogo (View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.yelp.com"));
        startActivity(intent);

    }

    synchronized public void waitForRestaurant2(boolean client2){
        //Log.v("debug", "hurry");
        if (client2) {

            if (mRestaurants2.size()>i){
                //have the data
                Log.v("have the data", "have the data");
                restaurantCallback2();
            }
            else {
                waiting = true;
                Log.v("no data2", "no data2");
                //mLoading2.setVisibility(View.VISIBLE);
            }
        }
        else {
            Log.v("wait", "wait");

            if (!waiting){
                 Log.v("2debug2", "hurry");
                restaurantCallback2();
                waiting = false;
                //mLoading2.setVisibility(View.INVISIBLE);
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
                .load(r2.getPicUrl())
                .into (mMainImage2);
        Log.v("name2", "name2");
        mRestaurantTitle2.setText(r2.getName());
        mLocation2.setText(r2.getLocation());
        if (r2.getRating().equals("0")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_0","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("1")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("1.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1_half","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("2")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("2.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2_half","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("3")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("3.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3_half","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("4")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else if (r2.getRating().equals("4.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4_half","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        else{
            int drawableID= this.getResources().getIdentifier ("stars_regular_5","drawable", getPackageName());
            mRate2.setImageResource(drawableID);
        }
        mReview2.setText(r2.getReviews());
        int logoID = this.getResources().getIdentifier("yelp_trademark_rgb_outline", "drawable", getPackageName());
        mLogo2.setImageResource(logoID);

    }

    public void clickMeClickLogo2 (View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.yelp.com"));
        startActivity(intent);
    }

    synchronized public void waitForRestaurant3(boolean client3){
        if (client3) {
            if (mRestaurants3.size()>i){
                //have the data
                restaurantCallback3();
            }
            else {
                Log.v("no data3", "no data3");
                waiting = true;
                //mLoading3.setVisibility(View.VISIBLE);
            }
        }
        else {

            if (!waiting){
                Log.v("3","hi");
                restaurantCallback3();
                waiting = false;
                //mLoading3.setVisibility(View.INVISIBLE);
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
                .load(r3.getPicUrl())
                .into (mMainImage3);
        mRestaurantTitle3.setText(r3.getName());
        mLocation3.setText(r3.getLocation());
        if (r3.getRating().equals("0")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_0","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("1")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("1.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_1_half","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("2")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("2.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_2_half","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("3")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("3.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_3_half","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("4")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else if (r3.getRating().equals("4.5")){
            int drawableID= this.getResources().getIdentifier ("stars_regular_4_half","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        else{
            int drawableID= this.getResources().getIdentifier ("stars_regular_5","drawable", getPackageName());
            mRate3.setImageResource(drawableID);
        }
        mReview3.setText(r3.getReviews());
        int logoID = this.getResources().getIdentifier("yelp_trademark_rgb_outline", "drawable", getPackageName());
        mLogo3.setImageResource(logoID);
    }

    public void clickMeClickLogo3 (View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.yelp.com"));
        startActivity(intent);

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
                    r.setRating(b.rating().toString());
                    r.setLocation("   Address: " + b.location().displayAddress().toString());
                    r.setReview("   Based on " + b.reviewCount().toString() + " Reviews");
                    r.setPicUrl(b.imageUrl());
                    restaurants.add(r);
                    fetchPictures(r,i);
                    i++;
                }
                //Log.v("rest", Integer.toString(restaurants.size()));
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
                    publishProgress(restaurants.get(pos));
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
                    r2.setRating(b2.rating().toString());
                    r2.setLocation("   Address: " + b2.location().displayAddress().toString());
                    r2.setReview("   Based on " + b2.reviewCount().toString() + " Reviews");
                    r2.setPicUrl(b2.imageUrl());
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
                    publishProgress(restaurants2.get(pos2));
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
                List<Business> businessList3 = response3.body().businesses();
                Restaurantdb r3;
                int i = 0;
                for (Business b3 : businessList3) {
                    Log.v("Businesses", response3.body().businesses().toString());
                    r3 = new Restaurantdb("   Restaurant Name: " + b3.name(), b3.url());
                    r3.setRating(b3.rating().toString());
                    r3.setLocation("   Address: " + b3.location().displayAddress().toString());
                    r3.setReview("   Based on " + b3.reviewCount().toString() + " Reviews");
                    r3.setPicUrl(b3.imageUrl());
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

            mClient3.newCall(request3).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response3) throws IOException {
                    publishProgress(restaurants3.get(pos3));
                }
            });

        }
    }



}
