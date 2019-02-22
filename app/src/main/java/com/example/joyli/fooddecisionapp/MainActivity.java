package com.example.joyli.fooddecisionapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.*;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    TextView mRestaurantTitle, mRestaurantTitle2, mRestaurantTitle3, mLocation,mLocation2,mLocation3, mReview, mReview2, mReview3;
    ImageView mMainImage,mMainImage2, mMainImage3, mRate, mLogo, mRate2, mRate3, mLogo3, mLogo2;
    private ArrayList<Restaurantdb> mRestaurants= new ArrayList<>();
    private List<Restaurantdb> mRestaurants2 = new ArrayList<>();
    private List<Restaurantdb> mRestaurants3 = new ArrayList<>();
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
        mRestaurants = new ArrayList<>();
        mRestaurants2 = new ArrayList<>();
        mRestaurants3 = new ArrayList<>();
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
                double currentLatitude = 43.653225;
                double currentLongitude = -79.383186;
                String category = clickToGenerateFood();
                RestaurantCategorySearch searchBusinessesBasedOnCategory = new RestaurantCategorySearch(getApplicationContext(), category, currentLatitude, currentLongitude);
                JSONObject jsonObject = searchBusinessesBasedOnCategory.getBusinessesInfo();

                RestaurantInformation restaurantInformation1 = new RestaurantInformation(jsonObject);
                restaurantInformation1.getBusinessImageURL();
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
          selectedPositionText.setText("Selected: Fast Food");
          return "fast_food";
      } else if (randomNum == 9) {
          selectedPositionText.setText("Selected: Cafe");
          return "cafe";
      } else if (randomNum == 10) {
          selectedPositionText.setText("Selected: Buffet");
          return "buffet";
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

    public String yelpBusinessPage(Restaurantdb r)
    {
        String url;
        url=r.getUrl();
        return url;
    }

    public void clickOnFirstRestaurantLogo(View view){
        String url=yelpBusinessPage(mRestaurants.get(i));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void clickForMapForFirstRestaurant(View view){
        Double EndLatitude=0.0, EndLongitude=0.0;
        EndLatitude=mRestaurants.get(i).getLatitude();
        EndLongitude=mRestaurants.get(i).getLongitude();
        Intent intent =null, chooser=null;
        intent = new Intent (Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?+saddr="+Double.toString(displayCurrentLatitudeLocationOfDevice())+ ","
                + Double.toString(displayCurrentLongitudeLocationOfDevice()) + "&daddr=" + Double.toString(EndLatitude) + "," + Double.toString(EndLongitude)));
        chooser=Intent.createChooser(intent,"Launch Maps");
        startActivity(chooser);
    }

    public void clickOnSecondRestaurantLogo(View view){
        String url = yelpBusinessPage(mRestaurants2.get(i));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void clickForMapForSecondRestaurant(View view){
        Double EndLatitude=0.0, EndLongitude=0.0;
        EndLatitude=mRestaurants2.get(i).getLatitude();
        EndLongitude=mRestaurants2.get(i).getLongitude();
        Intent intent =null, chooser=null;
        intent = new Intent (Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?+saddr="+Double.toString(displayCurrentLatitudeLocationOfDevice())+","
                + Double.toString(displayCurrentLongitudeLocationOfDevice())+"&daddr="+Double.toString(EndLatitude)+","+Double.toString(EndLongitude)));
        chooser=Intent.createChooser(intent,"Launch Maps");
        startActivity(chooser);
    }

    public void clicksOnThirdRestaurantLogo(View view){
        String url = yelpBusinessPage(mRestaurants3.get(i));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void clickForMapForThirdRestaurant(View view){
        Double EndLatitude=0.0, EndLongitude=0.0;
        EndLatitude=mRestaurants3.get(i).getLatitude();
        EndLongitude=mRestaurants3.get(i).getLongitude();
        Intent intent =null, chooser=null;
        intent = new Intent (Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?+saddr="+Double.toString(displayCurrentLatitudeLocationOfDevice())+","
                + Double.toString(displayCurrentLongitudeLocationOfDevice())+"&daddr="+Double.toString(EndLatitude)+","+Double.toString(EndLongitude)));
        chooser=Intent.createChooser(intent,"Launch Maps");
        startActivity(chooser);
    }

    public void goClick (View view){
        float []endResult={0.0f,0.0f,0.0f};
        String title = "Food Decision App";
        Double EndLatitude=0.0, EndLongitude=0.0;
        EndLatitude=mRestaurants3.get(i).getLatitude();
        EndLongitude=mRestaurants3.get(i).getLongitude();
        //Double distance = distance(StartLatitude,StartLongitude,EndLatitude,EndLongitude);
        Location.distanceBetween(displayCurrentLatitudeLocationOfDevice(),displayCurrentLongitudeLocationOfDevice(),EndLatitude,EndLongitude, endResult);
        String subject = "You are "+endResult[0]+"m away from " +endResult[1] + " " + endResult[2]+ mRestaurants3.get(i).getName();

        NotificationManager notif = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder(getApplicationContext()).setContentTitle(title).setContentText(subject).setContentTitle(title).setSmallIcon(R.mipmap.ic_launcher).build();
        notify.flags |=Notification.FLAG_AUTO_CANCEL;
        notif.notify(0,notify);
    }

}
