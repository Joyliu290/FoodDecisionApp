package com.example.joyli.fooddecisionapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.*;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView mRestaurantTitle, mRestaurantTitle2, mRestaurantTitle3, mLocation,mLocation2,mLocation3, mReview, mReview2, mReview3;
    ImageView mMainImage,mMainImage2, mMainImage3, mRate, mLogo, mRate2, mRate3, mLogo3, mLogo2;
    int i,j;
    ProgressBar mLoading, mLoading2, mLoading3;
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
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        mLoading = (ProgressBar) findViewById(R.id.progressBar2);
        mLoading2 = (ProgressBar) findViewById(R.id.progressBar3);
        mLoading3 = (ProgressBar) findViewById(R.id.progressBar4);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //RUNTIME REQUEST PERMISSION

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

        Button pressForFoodButton = (Button)findViewById(R.id.pressMe);
        pressForFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make current restaurant data invisible
                clearAllRestaurantUI();
                // make the progress bar visibile
                mLoading.setVisibility(View.VISIBLE);
                mLoading2.setVisibility(View.VISIBLE);
                mLoading3.setVisibility(View.VISIBLE);
                // ideally, get the device's current latitude and longitude, right now just static
                double currentLatitude = displayCurrentLatitudeLocationOfDevice();
                double currentLongitude = displayCurrentLongitudeLocationOfDevice();
                String category = clickToGenerateFood();
                // call Yelp API and then load the UI
                getListOfRestaurants(currentLatitude, currentLongitude, category);
            }
        });
    }

    public void getListOfRestaurants(double latitude, double longitude, String category){
        String apiKey = getApplicationContext().getString(R.string.yelp_api_key);
        // Creating GET request objects to make network calls to Yelp API to retrieve list of businesses under the category
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder getRequestBuilder =
                HttpUrl.parse("https://api.yelp.com/v3/businesses/search").newBuilder();
        getRequestBuilder.addQueryParameter("latitude", Double.toString(latitude));
        getRequestBuilder.addQueryParameter("longitude", Double.toString(longitude));
        getRequestBuilder.addQueryParameter("categories", category);
        String getUrl = getRequestBuilder.build().toString();

        Request getRequest = new Request.Builder()
                .header("Authorization", "Bearer "+apiKey)
                .url(getUrl)
                .build();
        // Sending and receiving network calls
        client.newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    throw new IOException("GET REQUEST FAILED" + response);
                }
                else{
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray businessesJsonArray = jsonObject.getJSONArray("businesses");
                        Restaurant restaurant1 = new Restaurant(businessesJsonArray.getJSONObject(0), getApplicationContext(), mRate, mRestaurantTitle, mLocation, mReview, mMainImage);
                        Restaurant restaurant2 = new Restaurant(businessesJsonArray.getJSONObject(1), getApplicationContext(), mRate2, mRestaurantTitle2, mLocation2, mReview2, mMainImage2);
                        Restaurant restaurant3 = new Restaurant(businessesJsonArray.getJSONObject(2), getApplicationContext(), mRate3, mRestaurantTitle3, mLocation3, mReview3, mMainImage3);

                        setRestaurantData(restaurant1, restaurant2, restaurant3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setRestaurantData(final IBusiness restaurant1, final IBusiness restaurant2, final IBusiness restaurant3){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set progress bar to be invisible
                    showLogoAndMakeProgressBarInvisible();
                    restaurant1.setBusinessImageUI();
                    restaurant1.setBusinessLocationUI();
                    restaurant1.setBusinessNameUI();
                    restaurant1.setBusinessRatingUI();
                    restaurant1.setBusinessReviewCountUI();
                    restaurant2.setBusinessRatingUI();
                    restaurant2.setBusinessNameUI();
                    restaurant2.setBusinessLocationUI();
                    restaurant2.setBusinessImageUI();
                    restaurant2.setBusinessReviewCountUI();
                    restaurant3.setBusinessImageUI();
                    restaurant3.setBusinessLocationUI();
                    restaurant3.setBusinessRatingUI();
                    restaurant3.setBusinessNameUI();
                    restaurant3.setBusinessReviewCountUI();
                }
            });
    }

  public String clickToGenerateFood() {
        i=0;
        j=0;
        Random r = new Random();
        int randomNum=r.nextInt(12-1)+1;
        return chooseAFoodCategory(randomNum);
  }

  private String chooseAFoodCategory(int randomNum){
      if (randomNum == 1) {
          selectedPositionText.setText("Selected: Korean Food");
          return "korean";
      } else if (randomNum == 2) {
          selectedPositionText.setText("Selected: Japanese Food");
          return "japanese";
      } else if (randomNum == 3) {
          selectedPositionText.setText("Selected: Indian Food");
          return "indian";
      } else if (randomNum == 4) {
          selectedPositionText.setText("Selected: Chinese Food");
          return "chinese";
      } else if (randomNum == 5) {
          selectedPositionText.setText("Selected: Italian Food");
          return "italian";
      } else if (randomNum == 6) {
          selectedPositionText.setText("Selected: Thai Food");
          return "thai";
      } else if (randomNum == 7) {
          selectedPositionText.setText("Selected: Vietnamese Food");
          return "vietnamese";
      } else if (randomNum == 8) {
          selectedPositionText.setText("Selected: Mexican");
          return "mexican";
      } else if (randomNum == 9) {
          selectedPositionText.setText("Selected: Cafe");
          return "cafe";
      } else if (randomNum == 10) {
          selectedPositionText.setText("Selected: Noodles");
          return "noodles";
      } else if (randomNum == 11) {
          selectedPositionText.setText("Selected: Dessert");
          return "dessert";
      } else {
          selectedPositionText.setText("Selected: Greek Food");
          return  "greek";
      }
  }

    public double displayCurrentLatitudeLocationOfDevice() {
        //displays the latitude
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return 0.0;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            return latitude;

        } else{
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Could not get location. Make sure Location is Enabled on Device", Toast.LENGTH_LONG).show();
                }
            });
        }
        return 0.0;

    } //latitude

    public double displayCurrentLongitudeLocationOfDevice() {
        //displays the longitude
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0.0;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double longitude = mLastLocation.getLongitude();
            return longitude;

        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Could not get location. Make sure Location is Enabled on Device", Toast.LENGTH_LONG).show();
                }
            });
        }
        return 0.0;

    } //longtitude

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
        displayCurrentLatitudeLocationOfDevice();
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

        displayCurrentLatitudeLocationOfDevice();
    }

    public void clearAllRestaurantUI(){
        mRate.setImageResource(android.R.color.transparent);
        mRate2.setImageResource(android.R.color.transparent);
        mRate3.setImageResource(android.R.color.transparent);
        mMainImage.setImageResource(android.R.color.transparent);
        mMainImage2.setImageResource(android.R.color.transparent);
        mMainImage3.setImageResource(android.R.color.transparent);
        mRestaurantTitle.setText("");
        mRestaurantTitle2.setText("");
        mRestaurantTitle3.setText("");
        mReview.setText("");
        mReview2.setText("");
        mReview3.setText("");
        mLocation.setText("");
        mLocation2.setText("");
        mLocation3.setText("");
    }

    public void showLogoAndMakeProgressBarInvisible(){
        mLoading.setVisibility(View.INVISIBLE);
        mLoading2.setVisibility(View.INVISIBLE);
        mLoading3.setVisibility(View.INVISIBLE);
        mLogo.setVisibility(View.VISIBLE);
        mLogo2.setVisibility(View.VISIBLE);
        mLogo3.setVisibility(View.VISIBLE);
    }
}

