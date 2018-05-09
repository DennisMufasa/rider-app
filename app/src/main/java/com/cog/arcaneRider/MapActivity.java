package com.cog.arcaneRider;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.CustomRatingBar;
import com.cog.arcaneRider.adapter.EasyTimer;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.cog.arcaneRider.adapter.FragmentDrawer;
import com.cog.arcaneRider.adapter.SlideUpPanelLayout;
import com.cog.arcaneRider.adapter.UberProgressDrawable;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends EasyLocationAppCompatActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationSource,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback, GoogleMap.OnCameraChangeListener, FragmentDrawer.FragmentDrawerListener, GeoQueryEventListener, DirectionCallback {

    //Log
    private static final String TAG = "GoogleMap";

    boolean doubleBackToExitPressedOnce = false;
    boolean isMarkerRotating = false;

    //CategoryLayout
    int n=3;//Total Category
    SlideUpPanelLayout slideUpPanelLayout;
    SeekBar categorySeekBar;
    TextView content;
    TextView txtHatchBack,txtSedan, txtSUV;
    int progresscount = 5;
    String seekBarValue = "5";
    Bitmap suvBitmap, sedanBitmap, hatchBackBitmap;
    Drawable suvCar, sedanCar, hatchBackCar;
    TextView estimateTime, maxPeople, minFare,perMinutePrice,perKmPrice;
    RelativeLayout seekBarLayout, carCategoryTitleLayout;
    CardView fareLayout;
    private boolean isShowTitle;
    private CharSequence[] titles = null;
    String[] strCategoryName = new String[n];
    String[] strEstimateTime = new String[n];
    String[] strMinFare = new String[n];
    String[] strMaxPeople = new String[n];
    String[] strMinutePrice = new String[n];
    String[] strKmPrice = new String[n];
    String[] categoryMarker = new String[n];
    String[] categoryLogo = new String[n];
    String carCategory;
    String calcBaseFare,calcPriceKM,calcPriceMin;
    int categoryLength;
    SharedPreferences.Editor getState;
    String tripState,strCacnelStatus;
    private Geocoder geocoder;
    private List<Address> addressList;
    DatabaseReference tollReference;
    ValueEventListener tollListener;
    //Google Maps
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private OnLocationChangedListener mMapLocationListener = null;
    LocationRequest mLocationRequest;
    Location filterLocation;
    LatLng startLatLng, destLatLng, exceptionLatLng, pickupLatLng, destinationLatLng,center,orginlat,destlat;
    Polyline mPolyline;
    Double originLAT, originLNG, destLAT, destLNG, newOriginLat, newOriginLng, newDestLat, newDestLng,calctotalDistance;

    //Views
    TextView setPickupLocation, originLocation, destinationLocation, lastTrip, lastTripTime, tripDriverName, tripCarName, tripEndDriverName, tripEndAmount, txtUserName,txtETA;
    ImageView menuButton, driverImage, cashButton, userProfileImage;
    Button headerButton, requestButton, changePayment, ratingSubmit,getFareEstimate,tollFee;
    DrawerLayout mDrawerLayout;
    FragmentDrawer drawerFragment;
    Dialog progressBar;
    ProgressBar requestBar;

    Location mCurrentLocation;

    //Layouts
    RelativeLayout locationLayout, tripHistoryLayout, requestLayout, driverLayout, dropLocationLayout;
    LinearLayout ratingLayout, categoryLayout, trip_info_layout;

    //Strings
    String userID, driverID, tripID, requestStatus, requestID, paymentType, driverProfileName, driverProfileImage, pickupCountryCode, dropCountryCode, completeAddress, category, carcategory, driverFirstname, driverLastName, driverProfileImageAccepted, driverMobileNumber,tripDriverCategory,totalDuration;


    //Firebase
    GeoQuery geoQuery;
    GeoFire geoFire;


    //Timer to run every 3 second
    EasyTimer easyTimer = new EasyTimer(3000);
    SharedPreferences state;
    //Int
    int count = 0;

    private Map<String, Marker> markers;

    //check tripStatus
    boolean tripStatus = false;

    //progressDialog
    ProgressDialog progressDialog;
    Dialog dialog,cancelDialog,faredialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));


        // setup markers
        this.markers = new HashMap<String, Marker>();

        //Full screen theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //create preference for state
        getState = getSharedPreferences(Constants.MY_STATE_KEY, getApplicationContext().MODE_PRIVATE).edit();
        state = getSharedPreferences(Constants.MY_STATE_KEY, MODE_PRIVATE);
        tripState = state.getString("tripstate", null);
        if (tripState == null) {
            //set end click to get request first time
            tripState = "endClicked";
        }



        //UserID from Shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in Map" + userID);

        //getPaymentTypeofUser
        getPaymentReference();

        setPickupLocation = (TextView) findViewById(R.id.set_pickup);
        txtETA= (TextView) findViewById(R.id.nearby_eta);
        menuButton = (ImageView) findViewById(R.id.menu_button);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        headerButton = (Button) findViewById(R.id.header_button);
        tollFee=(Button) findViewById(R.id.showtollfee);
        requestBar = (ProgressBar) findViewById(R.id.requestBar);

        /*Navigation Drawer Layout*/
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setDrawerListener(this);
        userProfileImage = (ImageView) mDrawerLayout.findViewById(R.id.rider_profile_image);
        txtUserName = (TextView) mDrawerLayout.findViewById(R.id.userName);


        /*Location Layout*/
        dropLocationLayout = (RelativeLayout) findViewById(R.id.drop_location_layout);
        locationLayout = (RelativeLayout) findViewById(R.id.location_layout);
        originLocation = (TextView) findViewById(R.id.pick_location);
        destinationLocation = (TextView) findViewById(R.id.drop_location);

        /*Trip History Layout*/
        tripHistoryLayout = (RelativeLayout) findViewById(R.id.trip_history_layout);
        lastTrip = (TextView) findViewById(R.id.last_trip);
        lastTripTime = (TextView) findViewById(R.id.last_trip_time);

        /*Request Layout*/
        requestLayout = (RelativeLayout) findViewById(R.id.request_layout);
        requestButton = (Button) findViewById(R.id.request_button);
        cashButton = (ImageView) findViewById(R.id.cashButton);
        changePayment = (Button) findViewById(R.id.payment_change);
        categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
        getFareEstimate =(Button) findViewById(R.id.fare_estimate);

        /*Driver Layout*/
        driverLayout = (RelativeLayout) findViewById(R.id.driver_layout);
        driverImage = (ImageView) findViewById(R.id.driver_image);
        tripDriverName = (TextView) findViewById(R.id.driver_name);
        tripCarName = (TextView) findViewById(R.id.car_name);
        trip_info_layout = (LinearLayout) findViewById(R.id.trip_info_layout);

        /*Trip Rating Layout*/
        ratingLayout = (LinearLayout) findViewById(R.id.trip_rating_layout);
        tripEndDriverName = (TextView) findViewById(R.id.trip_end_driver);
        tripEndAmount = (TextView) findViewById(R.id.trip_end_amount);
        ratingSubmit = (Button) findViewById(R.id.submit_button);

        /*Category Layout*/
        slideUpPanelLayout = (SlideUpPanelLayout) findViewById(R.id.slide_up_layout);
        categorySeekBar = (SeekBar) findViewById(R.id.category_seek_bar);
        fareLayout = (CardView) findViewById(R.id.fare_layout);
        estimateTime = (TextView) findViewById(R.id.estimated_time);
        maxPeople = (TextView) findViewById(R.id.number_of_people);
        minFare = (TextView) findViewById(R.id.minimum_fare);
        carCategoryTitleLayout = (RelativeLayout) findViewById(R.id.car_category_title);
        fareLayout = (CardView) findViewById(R.id.fare_layout);
        perMinutePrice = (TextView) findViewById(R.id.per_minute_price);
        perKmPrice = (TextView) findViewById(R.id.per_km_price);
        txtHatchBack = (TextView) findViewById(R.id.txt_hatchback);
        txtSedan = (TextView) findViewById(R.id.txt_sedan);
        txtSUV = (TextView) findViewById(R.id.txt_suv);

        //Map Initialization
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //Easy location to get instant updates
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(1000);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setLocationPermissionDialogTitle(getString(R.string.location_permission_dialog_title))
                .setLocationPermissionDialogMessage(getString(R.string.location_permission_dialog_message))
                .setLocationPermissionDialogNegativeButtonText(getString(R.string.not_now))
                .setLocationPermissionDialogPositiveButtonText(getString(R.string.yes))
                .setLocationSettingsDialogTitle(getString(R.string.location_services_off))
                .setLocationSettingsDialogMessage(getString(R.string.open_location_settings))
                .setLocationSettingsDialogNegativeButtonText(getString(R.string.not_now))
                .setLocationSettingsDialogPositiveButtonText(getString(R.string.yes))
                .setFallBackToLastLocationTime(1000)
                .build();
        requestLocationUpdates(easyLocationRequest);

        getCategoryInfo();
        getCarInfo(0);

        originLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity(Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        destinationLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity(Constants.DEST_REQUEST_CODE_AUTOCOMPLETE);
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check the Drawable state for Navigation Drawer
                Drawable drawable = menuButton.getBackground();
                if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.close).getConstantState())) {
                    cancelRequest();
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_pickup_location));
                } else if (destinationLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_destination_location));
                } else {
                    if (isNetworkAvailable()) {
                        dropLocationLayout.setVisibility(View.VISIBLE);
                        sendRequest();
                    } else {
                        alertSnackBar(getResources().getString(R.string.check_network_connection));
                    }
                }

            }
        });

        getFareEstimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_pickup_location));
                } else if (destinationLocation.getText().length() == 0) {
                    alertSnackBar(getResources().getString(R.string.enter_destination_location));
                } else {
                    if (isNetworkAvailable()) {
                        //Show Alert Dialog
                        if (seekBarValue != null) {
                            Integer a = Integer.parseInt(seekBarValue);
                            if (a == 5) {
                                getFareInfo(0);
                            }
                            else if (a == 50) {
                                getFareInfo(1);
                            }
                            else if (a == 95) {
                                getFareInfo(2);
                            }

                            System.out.println("origin lat"+orginlat);
                            System.out.println("dest lat"+destlat);

                            if(center!=null || destlat!=null) {
                                calctotalDistance=CalculationByDistance(center, destlat);
                            }


                            System.out.println("Minimum Fare"+calcBaseFare);
                            System.out.println("Minimum KM"+calcPriceKM);
                            System.out.println("Minimum Price"+calcPriceMin);
                            System.out.println("Pickup Latlng"+pickupLatLng);

                            if(calctotalDistance!=null)
                            {
                                String estimate=calculateEstimate(calctotalDistance,calcPriceKM,calcPriceMin,calcBaseFare);
                                System.out.println("Total Estimate"+estimate);
                                showFareEstimateDialog(originLocation.getText().toString(),destinationLocation.getText().toString(),getCategory(),estimate);
                            }



                        }


                    } else {
                        alertSnackBar(getResources().getString(R.string.check_network_connection));
                    }
                }

            }
        });



        setPickupLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show Request button only if has cars nearby
                if (setPickupLocation.getText().toString().matches("SET PICKUP LOCATION")) {
                    mDrawerLayout.isDrawerOpen(Gravity.LEFT);
                    menuButton.setBackground(getResources().getDrawable(R.drawable.close));
                    dropLocationLayout.setVisibility(View.VISIBLE);
                    requestLayout.setVisibility(View.VISIBLE);
                    categoryLayout.setVisibility(View.GONE);
                }
            }
        });


        ratingSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripHistoryLayout.setVisibility(View.GONE);
                locationLayout.setVisibility(View.VISIBLE);
                requestLayout.setVisibility(View.VISIBLE);
                ratingLayout.setVisibility(View.GONE);
            }
        });

        changePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payment = new Intent(MapActivity.this, PaymentSelectActivity_.class);
                startActivity(payment);
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfile();
            }
        });

        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callProfile();
            }
        });

        trip_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverInfoDialog();
            }
        });


         /*Seek Bar*/
        slideUpPanelLayout.showHandle();


        seekBarLayout = (RelativeLayout) findViewById(R.id.seek_bar_layout);

        categorySeekBar.setProgressDrawable(new UberProgressDrawable(categorySeekBar.getProgressDrawable(), categorySeekBar, 3, Color.BLACK));

        suvBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.suv);

        sedanBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.sedan);

        hatchBackBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.hatchback);

        suvCar = new BitmapDrawable(getResources(), suvBitmap);
        sedanCar = new BitmapDrawable(getResources(), sedanBitmap);
        hatchBackCar = new BitmapDrawable(getResources(), hatchBackBitmap);

        if (seekBarValue != null) {
            Integer a = Integer.parseInt(seekBarValue);
            categorySeekBar.incrementProgressBy(a);

            if (a == 5) {
                System.out.println("SeekbarValue==>" + a);
                getCarInfo(0);
                categorySeekBar.setThumb(hatchBackCar);
            }
            if (a == 50) {
                categorySeekBar.setThumb(sedanCar);
                System.out.println("SeekbarValue==>" + a);
                getCarInfo(1);
            }

            if (a == 95) {
                categorySeekBar.setThumb(suvCar);
                getCarInfo(2);
                System.out.println("SeekbarValue==>" + a);
            }


        } else {
            seekBarValue = "5";
            Integer a = Integer.parseInt(seekBarValue);
            categorySeekBar.incrementProgressBy(a);
            categorySeekBar.setThumb(suvCar);
            category = "Hatchback";
            setCategory(category);

            if(mMap!=null)
                mMap.clear();

            setPickupLocation.setText("NO CARS AVAILABLE");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            txtETA.setVisibility(View.GONE);
            //Geo Fire
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
            setGeofire(geoFire);
            getCarInfo(0);
            try{
                geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), 5);
                geoQuery.addGeoQueryEventListener(MapActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        categorySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= 25) {
                    progresscount = 5;
                    seekBar.setThumb(hatchBackCar);
                } else if (progress > 25 && progress <= 75) {
                    progresscount = 50;
                    seekBar.setThumb(sedanCar);
                } else if (progress > 75) {
                    progresscount = 95;
                    seekBar.setThumb(suvCar);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progresscount == 5) {
                    seekBarValue = "5";
                    seekBar.setProgress(progresscount);
                    seekBar.setThumb(hatchBackCar);
                    category = "Hatchback";
                    setCategory(category);

                    if(mMap!=null)
                        mMap.clear();

                    setPickupLocation.setText("NO CARS AVAILABLE");
                    setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    txtETA.setVisibility(View.GONE);
                    //Geo Fire
                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
                    setGeofire(geoFire);
                    getCarInfo(0);
                    try{
                        geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), 5);
                        geoQuery.addGeoQueryEventListener(MapActivity.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (progresscount == 50) {
                    seekBarValue = "50";
                    seekBar.setProgress(progresscount);
                    seekBar.setThumb(sedanCar);
                    category = "Sedan";
                    setCategory(category);

                    if(mMap!=null)
                        mMap.clear();

                    setPickupLocation.setText("NO CARS AVAILABLE");
                    setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    txtETA.setVisibility(View.GONE);
                    //Geo Fire
                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
                    setGeofire(geoFire);
                    getCarInfo(1);
                    try{
                        geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), 5);
                        geoQuery.addGeoQueryEventListener(MapActivity.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (progresscount == 95) {
                    seekBarValue = "95";
                    seekBar.setProgress(progresscount);
                    seekBar.setThumb(suvCar);
                    category = "SUV";
                    setCategory(category);

                    if(mMap!=null)
                        mMap.clear();

                    setPickupLocation.setText("NO CARS AVAILABLE");
                    setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    txtETA.setVisibility(View.GONE);
                    //Geo Fire
                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
                    setGeofire(geoFire);
                    getCarInfo(2);
                    try{
                        geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), 5);
                        geoQuery.addGeoQueryEventListener(MapActivity.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });

        if (tripState.matches("requestAccept")) {
            getAcceptState();

        }
        else if(tripState.matches("arriving")){
          getarrivingstate();
        }
        else if(tripState.matches("ontrip")){
          getOnTripState();
        }
      /*  carCategoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fareLayout.getVisibility() == View.VISIBLE) {
                    fareLayout.setVisibility(View.GONE);
                } else {
                    fareLayout.setVisibility(View.VISIBLE);
                }
            }
        });*/

    }

    public void getOnTripState() {
        tripStatus=true;
        headerButton.setText("ON TRIP");

        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        //tollFee.setVisibility(View.VISIBLE);
        setPickupLocation.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        driverID = state.getString("tripdriverid", null);
        String startaddress=state.getString("pickupposition",null);
        String endaddress=state.getString("destposition",null);
        LatLng startLatLng = getLocationFromAddress(startaddress);
        LatLng endLatLng = getLocationFromAddress(endaddress);

        pickupLatLng=startLatLng;
        destinationLatLng=endLatLng;

        if(driverID!=null ){
            displayDetails(driverID);
        }
        requestID=state.getString("tripreqid",null);
        getstatusfromfirebase();
    }

    public void getarrivingstate() {
        tripStatus=true;
        headerButton.setText("ARRIVING");
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        trip_info_layout.setVisibility(View.VISIBLE);
        setPickupLocation.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        driverID = state.getString("tripdriverid", null);
        String startaddress=state.getString("pickupposition",null);
        String endaddress=state.getString("destposition",null);
        LatLng startLatLng = getLocationFromAddress(startaddress);
        LatLng endLatLng = getLocationFromAddress(endaddress);

        pickupLatLng=startLatLng;
        destinationLatLng=endLatLng;
        getstatusfromfirebase();
        if(driverID!=null ){
            displayDetails(driverID);
        }
    }


    private void getCategoryInfo() {
            final String url = Constants.CATEGORY_LIVE_URL+"Settings/getCategory";
            System.out.println("GetCategoryURL==>"+url);
            final JsonArrayRequest infoReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    categoryLength = response.length();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                System.out.println("Response from GetCategory==>" + jsonObject);
                                strCategoryName[i] = jsonObject.optString("categoryname");
                                strMinFare[i] = jsonObject.optString("price_fare");
                                strMaxPeople[i] = jsonObject.optString("max_size");
                                strMinutePrice[i] = jsonObject.optString("price_minute");
                                strKmPrice[i] = jsonObject.optString("price_km");
                                categoryLogo[i] = jsonObject.optString("Logo");
                                categoryMarker[i] = jsonObject.optString("Marker");

                                if (strCategoryName!=null && !strCategoryName.equals("null")){
                                    if(i==0)
                                        txtHatchBack.setText(strCategoryName[i]);
                                    else if(i==1)
                                        txtSedan.setText(strCategoryName[i]);
                                    else if(i==2)
                                        txtSUV.setText(strCategoryName[i]);

                                }
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (volleyError instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    } if(volleyError instanceof TimeoutError){
                        Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        AppController.getInstance().addToRequestQueue(infoReq);
        infoReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void getCarInfo(int i){
       /*  if(strEstimateTime[i]!=(null) && !strEstimateTime[i].equals("null"))
             estimateTime.setText(strEstimateTime[i]);*/

        if(strMinFare[i]!=null && !strMinFare[i].equals("null"))
            minFare.setText(strMinFare[i]);

        if(strMaxPeople[i]!=null && !strMaxPeople[i].equals("null"))
            maxPeople.setText(strMaxPeople[i]);

        if(strMinutePrice[i]!=null && !strMinutePrice[i].equals("null"))
            perMinutePrice.setText(strMinutePrice[i]);

        if(strKmPrice[i]!=null && !strKmPrice[i].equals("null"))
            perKmPrice.setText(strKmPrice[i]);
    }

    public void getFareInfo(int i){

        if(strMinFare[i]!=null && !strMinFare[i].equals("null"))
            calcBaseFare=strMinFare[i];

        if(strMinutePrice[i]!=null && !strMinutePrice[i].equals("null"))
            calcPriceMin=strMinutePrice[i];

        if(strKmPrice[i]!=null && !strKmPrice[i].equals("null"))
            calcPriceKM=strKmPrice[i];
    }


    private void showDriverInfoDialog() {
        dialog = new Dialog(MapActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.call_driver_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton back = (ImageButton) dialog.findViewById(R.id.backButton);
        LinearLayout call = (LinearLayout) dialog.findViewById(R.id.calllayout);
        LinearLayout msg = (LinearLayout) dialog.findViewById(R.id.msglayout);
        TextView txtDriverName=(TextView ) dialog.findViewById(R.id.driver_text);
        TextView txtCancelTrip=(TextView ) dialog.findViewById(R.id.txtCancelTrip);
        TextView txtDriverCategory=(TextView ) dialog.findViewById(R.id.car_type);
        final ImageView driverimage=(ImageView) dialog.findViewById(R.id.driver_image);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);

        if (driverFirstname != null && driverLastName != null) {
            txtDriverName.setText(driverFirstname + " " + driverLastName);
        }

        if(tripDriverCategory!=null)
        {
            txtDriverCategory.setText(tripDriverCategory);
        }

        txtCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getString(R.string.cancel_trip));
                builder.setMessage(getString(R.string.cancel_dis));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        if(driverID!=null){
                            getState.putString("tripstate","endclicked");
                            getState.commit();
                            strCacnelStatus="ridercliked";
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept");
                            Map<String, Object> taskMap2 = new HashMap<String, Object>();
                            taskMap2.put("status", "5");
                            databaseReference2.updateChildren(taskMap2);

                            updatecancelTrip();
                            dialog1.dismiss();

                            dialog.cancel();
                            driverID=null;//clear existing driver


                        }

                    }

                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                    //  alertdialog2.cancel();

                });

                builder.show();

            }
        });

        if(driverProfileImage!=null)
        {
            Glide.with(getApplicationContext()).load(driverProfileImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverimage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    driverimage.setImageDrawable(circularBitmapDrawable);
                }
            });

        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Driver mobile number"+driverMobileNumber);
                /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse(driverMobileNumber));
                try {
                    startActivity(sendIntent);
                } catch (ActivityNotFoundException e) {
                    // Display some sort of error message here.
                }*/

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", driverMobileNumber, null)));
                } catch (ActivityNotFoundException e) {
                    // Display some sort of error message here.
                }


             /*   if (ridermobile != null && !ridermobile.isEmpty()) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:9597848909"));
                    startActivity(sendIntent);
                } else {
                    Toast.makeText(Map_Activity.this, "Number not register", Toast.LENGTH_SHORT).show();
                }*/

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (driverMobileNumber != null && !driverMobileNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + driverMobileNumber));
                    if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MapActivity.this, "No mobile number", Toast.LENGTH_SHORT).show();
                }
                

            }
        });

        dialog.show();


    }

    private void cancelTrip()
    {
        tripStatus=false;

        if(mPolyline!=null) {
            mPolyline.remove();
        }
        locationLayout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
        setPickupLocation.setVisibility(View.VISIBLE);
        driverLayout.setVisibility(View.GONE);

    }


    private void sendRequest() {

        if(pickupCountryCode==null&&dropCountryCode==null)
        {
            pickupCountryCode= getResources().getConfiguration().locale.getCountry();
            dropCountryCode= getResources().getConfiguration().locale.getCountry();
        }

        System.out.println("Pickup country code==>" + pickupCountryCode);
        System.out.println("Drop country code==>" + dropCountryCode);

        if(pickupCountryCode!=null&&dropCountryCode!=null) {
        if (pickupCountryCode.matches(dropCountryCode)) {
            menuButton.setBackground(getResources().getDrawable(R.drawable.menu));
            requestBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            requestBar.setVisibility(View.VISIBLE);
            progressBar = new Dialog(this);
            progressBar.setContentView(R.layout.overlay_dialog);
            progressBar.show();
            progressBar.setCancelable(false);
            progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            headerButton.setVisibility(View.VISIBLE);
            headerButton.setText("Requesting...");
            ImageButton cancelRequest = (ImageButton) progressBar.findViewById(R.id.cancel_request);
            requestLayout.setVisibility(View.GONE);
            categoryLayout.setVisibility(View.GONE);
            setPickupLocation.setVisibility(View.GONE);

            cancelRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    easyTimer.stop();
                    if (requestID != null)
                        cancelRequest(requestID);
                    requestLayout.setVisibility(View.GONE);
                    dropLocationLayout.setVisibility(View.GONE);
                    setPickupLocation.setVisibility(View.VISIBLE);
                    requestBar.setVisibility(View.GONE);
                    categoryLayout.setVisibility(View.VISIBLE);
                    headerButton.setVisibility(View.GONE);
                    if (!MapActivity.this.isFinishing() && progressBar != null) {
                        progressBar.dismiss();
                    }

                }
            });
            setRequestID(originLocation.getText().toString(), destinationLocation.getText().toString(),getCategory());
        } else {
            alertSnackBar(getResources().getString(R.string.service_not_available_for_the_destination));
        }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                getRiderDetails();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap=map;
        mMap.setLocationSource(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

       

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnCameraChangeListener(this);


        mGoogleApiClient.connect();




        //Adding style to the Map
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed"+location);

        if (mMapLocationListener != null) {
            mMapLocationListener.onLocationChanged(location);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null && count==0) {
            filterLocation=mLastLocation;
            count=count+1;
            //place marker at current position for the first time
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()),
                    17.0f));
            getCountryName(this,mLastLocation.getLatitude(),mLastLocation.getLongitude(),"ORIGIN");
            getCompleteAddressString(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            setCategory("Hatchback");
          /*  setCategory("Hatchback");
            //Geo Fire
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
            setGeofire(geoFire);
            geoQuery = getGeofire().queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 5);
            geoQuery.addGeoQueryEventListener(this);*/
        }

        mLocationRequest= new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mMapLocationListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mMapLocationListener = null;
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location location) {
        System.out.println(location.getProvider() + "," + location.getLatitude() + "," + location.getLongitude());
        mCurrentLocation=location;
        //Current lat and long of Rider
        destLatLng= new LatLng(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {
        this.finishAffinity();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
        finish();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //getlocation();
        try {
            List<Address> addressList = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addressList != null) {
                Address returnedAddress = addressList.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", "" + strReturnedAddress.toString());
                originLocation.setText(strReturnedAddress.toString());
                getCountryName(this,LATITUDE,LONGITUDE,"ORIGIN");
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Cannnot get Address!");
        }
        return strAdd;
    }

    //get distance between two location
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }




    private void openAutocompleteActivity(int REQUEST_CODE_AUTOCOMPLETE) {
        try {

            //Get country alpha2 code
            String locale = this.getResources().getConfiguration().locale.getCountry();
            System.out.println("Current Country==>"+locale);

            //Location Filter based on the Country
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_LOCALITY)
                    .build();

            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.

            if(filterLocation!=null){
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(autocompleteFilter)
                        .setBoundsBias(new LatLngBounds(new LatLng(filterLocation.getLatitude(),filterLocation.getLongitude()),new LatLng(filterLocation.getLatitude(),filterLocation.getLongitude())))
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            } else {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(autocompleteFilter)
                        .build(this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }


        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }



    public LatLng getLocationFromAddress1(String strAddress,String source) {
        System.out.println("Address"+strAddress);

        Geocoder coder = new Geocoder(MapActivity.this, Locale.ENGLISH);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            if(source.matches("ORIGIN")){

                originLAT=location.getLatitude();
                originLNG=location.getLongitude();



                if(originLAT!=null&originLNG!=null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(originLAT,
                                    originLNG),
                            17.0f));
                }


            }else {

                destLAT=location.getLatitude();
                destLNG=location.getLongitude();
            }


            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            if(source.matches("ORIGIN"))
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(newOriginLat,
                                newOriginLng),
                        17.0f));
            }
            ex.printStackTrace();
        }

        return p1;
    }


    public void setAddress(Intent data,int resultCode,String source){


        if (resultCode == RESULT_OK) {
            // Get the user's selected place from the Intent.
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.i(TAG, "Place Selected: " + place.getName());
            Log.i(TAG, "Latitude Selected: " + place.getLatLng());


            if(source.matches("ORIGIN")){
                exceptionLatLng=place.getLatLng();
                newOriginLat=exceptionLatLng.latitude;
                newOriginLng=exceptionLatLng.longitude;
                getCountryName(this,newOriginLat,newOriginLng,source);
                orginlat=new LatLng(newOriginLat,newOriginLng);
            }
            else
            {
                exceptionLatLng=place.getLatLng();
                newDestLat=exceptionLatLng.latitude;
                newDestLng=exceptionLatLng.longitude;
                getCountryName(this,newDestLat,newDestLng,source);
                destlat=new LatLng(newDestLat,newDestLng);
            }


            if(source.matches("ORIGIN")){
                // Format the place's details and display them in the TextView.
                originLocation.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                originLocation.setText(place.getAddress());

            }else {
                // Format the place's details and display them in the TextView.
                destinationLocation.setText(formatPlaceDetails(getResources(), place.getName(),
                        place.getId(), place.getAddress(), place.getPhoneNumber(),
                        place.getWebsiteUri()));
                destinationLocation.setText(place.getAddress());
            }


            System.out.println("address 1===>"+place.getName());
            System.out.println("address 2==>"+place.getAddress());

            // Display attributions if required.
            CharSequence attributions = place.getAttributions();
            if (!TextUtils.isEmpty(attributions)) {

                if(source.matches("ORIGIN")){
                    originLocation.setText(Html.fromHtml(attributions.toString()));

                }else {
                    destinationLocation.setText(Html.fromHtml(attributions.toString()));
                }

            } else {

                if(source.matches("ORIGIN")){
                    originLocation.setText(place.getAddress());

                }else {
                    destinationLocation.setText(place.getAddress());
                }

            }
            String tempadd=String.valueOf(place.getAddress());
            System.out.println("address after parshe==>"+place.getAddress());



            //Get LAT and LNG
            getLocationFromAddress1(tempadd,source);



        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.e(TAG, "Error: Status = " + status.toString());
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
            System.out.println("Canceled by user");
        }

    }
    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == Constants.ORIGIN_REQUEST_CODE_AUTOCOMPLETE) {

            setAddress(data,resultCode,"ORIGIN");

        }else if (requestCode == Constants.DEST_REQUEST_CODE_AUTOCOMPLETE) {

            setAddress(data,resultCode,"DEST");
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent trips = new Intent(this, YourTripsActivity_.class);
                startActivity(trips);
                break;
            case 1:
                callProfile();
                break;
            case 2:
                android.app.AlertDialog.Builder builder =
                        new AlertDialog.Builder(MapActivity.this);
                builder.setTitle(R.string.log_out);
                builder.setMessage(R.string.log_out_msg);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearpreferences();
                        Intent logout = new Intent(MapActivity.this, LaunchActivity_.class);
                        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(logout);
                        finish();
                    }

                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });

                builder.show();
                break;
            default:
                break;

        }
    }

    //set Request Initialization
    private void setRequestID(String startAddress, String endAddress,String category) {

        LatLng startLatLng = getLocationFromAddress(startAddress);
        LatLng endLatLng = getLocationFromAddress(endAddress);
        System.out.println("startlatlng"+startLatLng);
        System.out.println("endlatlng"+endLatLng);
        System.out.println("newendlatlng"+newDestLat);
        System.out.println("CATEFGORy"+category);

        if(startLatLng==null)
            startLatLng = new LatLng(newOriginLat, newOriginLng);
        if (endLatLng==null)
            endLatLng = new LatLng(newDestLat, newDestLng);
        if (startLatLng==endLatLng)
            Toast.makeText(MapActivity.this, "Pickup and Drop Locations cannot be same", Toast.LENGTH_SHORT).show();
        if(startLatLng!=null & endLatLng!=null) {
            //Check the Drawable state before Payment
            Drawable drawable = cashButton.getBackground();
            if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.ub__payment_type_cash).getConstantState())){
                paymentType=Constants.PAYMENT_TYPE_CASH;
            }
            else {
                paymentType=Constants.PAYMENT_TYPE_CARD;
            }

            //Trip Start and End Latitude
            pickupLatLng=startLatLng;
            destinationLatLng=endLatLng;
            getState.putString("pickupposition",startAddress);
            getState.putString("destposition",endAddress);

            //Trip Start and End Address
            try {
                startAddress=startAddress.replaceAll("/","");
                startAddress= URLEncoder.encode(startAddress,"UTF-8");
                endAddress=endAddress.replaceAll("/","");
                endAddress= URLEncoder.encode(endAddress,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            final String url = Constants.REQUEST_URL + "setRequest/userid/" + userID + "/start_lat/" + startLatLng.latitude + "/start_long/" + startLatLng.longitude + "/end_lat/" + endLatLng.latitude + "/end_long/" + endLatLng.longitude + "/payment_mode/" + paymentType + "/pickup_address/"+startAddress+"/drop_address/"+endAddress+"/category/"+category;
            System.out.println("get Request ID==>" + url);
            final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            System.out.println("response" + jsonObject);

                            requestStatus = jsonObject.optString("request_status");
                            requestID = jsonObject.optString("request_id");
                            System.out.println("requestStatus" + requestStatus);
                            System.out.println("requestID" + requestID);

                            //Save req id
                            getState.putString("tripreqid",requestID);
                            getState.commit();

                            processRequest(requestID);
                            //Set a runnable task every 3 seconds
                            AsynchTaskTimer(requestID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    System.out.println("Volley Error in Set request"+volleyError);
                    if (volleyError instanceof NoConnectionError) {

                        Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            signUpReq.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(signUpReq);
        }
        else {
            //Issue with Geo Coder
            System.out.println("Geocoder");
            dismissViews();
        }

    }

    //Sending requests to Available drivers
    private void processRequest(String requestID) {

        final String url = Constants.REQUEST_URL + "processRequest/request_id/"+requestID;


        System.out.println("process Request ID==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("response from requestID in ProcessRequest" + jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Volley Error in Process request"+volleyError);
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(signUpReq);

    }

    //get Request status
    private void getRequestID(String requestID) {

        final String url = Constants.REQUEST_URL + "getRequest/request_id/"+requestID;
        System.out.println("getRequestID==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("response from requestID" + jsonObject);
                        String responseStatus=jsonObject.getString("request_status");
                        if(responseStatus.matches("accept"))
                        {
                            tripStatus=true;
                            //Notification to show Driver has accepted the trip
                            generateNotification(getApplicationContext(),"Driver Accepted your Request");

                            getState.putString("tripstate","requestAccept");
                            getState.commit();

                            driverID=jsonObject.optString("driver_id");

                            getState.putString("tripdriverid",driverID);
                            getState.commit();

                            if(geoQuery!=null) {
                                System.out.println("removing geo query listener");
                                geoQuery.removeAllListeners();
                                for (Marker marker : markers.values()) {
                                    marker.remove();
                                }
                                markers.clear();
                            }
                            acceptViews();
                            //checktripcancelstatus();
                            getstatusfromfirebase();
                            if(!driverID.isEmpty()) {
                                displayDetails(driverID);
                            }
                            trip_info_layout.setVisibility(View.VISIBLE);

                        }
                        else if(responseStatus.matches("no_driver"))
                        {
                            //No Drivers Available
                            easyTimer.stop();
                            dismissViews();
                            noDriversAlert("No Driver","It seems Drivers are not available to pick up. Try again later");
                        }
                        else {
                            System.out.println("Running");
                        }
                    } catch (JSONException e) {
                        dismissViews();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        dismissViews();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("Error in get request"+volleyError);
                easyTimer.stop();
                dismissViews();
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(2000, 1, 1.0f));

    }

    //get Request status
    private void getTripID(String requestID) {

        final String url = Constants.REQUEST_URL + "getRequest/request_id/"+requestID;
        System.out.println("getTripID==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                            System.out.println("response for Trip ID" + jsonObject);
                            tripID=jsonObject.optString("trip_id");
                            System.out.println("tripID"+tripID);


                    } catch (JSONException e) {
                        dismissViews();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        dismissViews();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

    }

    //Get Trip status from Firebase
    private void getstatusfromfirebase() {
        if(driverID!=null) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept").child("status");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            if (status.matches("2")) {
                                headerButton.setText("ARRIVING");
                                getState.putString("tripstate","arriving");
                                getState.commit();
                                trip_info_layout.setVisibility(View.VISIBLE);
                                //Notification to show Driver has arriving
                                generateNotification(getApplicationContext(),"Driver is Arriving Now");

                            } else if (status.matches("3")) {
                                headerButton.setText("ON TRIP");
                                trip_info_layout.setVisibility(View.GONE);
                                //tollFee.setVisibility(View.VISIBLE);
                                getState.putString("tripstate","ontrip");
                                getState.commit();
                                //check toll fee from fire base
                                checkToll();
                                //Notification to show the trip has started
                                generateNotification(getApplicationContext(), "Your Trip has started");
                                if (startLatLng != null & destLatLng != null) {
                                    try{
                                        GoogleDirection.withServerKey(Constants.Google_API_KEY)
                                                .from(startLatLng)
                                                .to(destinationLatLng)
                                                .transportMode(TransportMode.DRIVING)
                                                .execute(MapActivity.this);
                                    }catch(Exception e){e.printStackTrace();}

                                }
                                if(requestID!=null) {
                                    getTripID(requestID);
                                }
                                else{
                                    String getreqid=state.getString("tripreqid",null);
                                    getTripID(getreqid);
                                }
                            } else if (status.matches("4")) {
                                databaseReference.removeEventListener(this);
                                tollReference.removeEventListener(tollListener);

                                tripStatus=false;
                                //tollFee.setVisibility(View.GONE);
                                getState.putString("tripstate","endclicked");
                                getState.commit();

                                headerButton.setText("TRIP END");
                                if(mPolyline!=null) {
                                    mPolyline.remove();
                                }
                                mMap.clear();
                                driverID=null;//clear the existing Driver
                                //Notification to show the trip has ended
                                generateNotification(getApplicationContext(), "Your Trip has ended.Thanks for riding with us");
                                driverLayout.setVisibility(View.GONE);
                                setPickupLocation.setVisibility(View.VISIBLE);
                                headerButton.setVisibility(View.GONE);
                                //Trip Summary Dialog
                                final Dialog dialogTripSummary = new Dialog(MapActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                                dialogTripSummary.setContentView(R.layout.activity_trip_summary);
                                dialogTripSummary.setCancelable(false);
                                Button close = (Button) dialogTripSummary.findViewById(R.id.footer_button);
                                final ImageView driverImage = (ImageView) dialogTripSummary.findViewById(R.id.trip_end_profile);
                                TextView driverName = (TextView) dialogTripSummary.findViewById(R.id.trip_driver_name);
                                TextView tripDate = (TextView) dialogTripSummary.findViewById(R.id.trip_date);
                                final TextView driverTripAmount = (TextView) dialogTripSummary.findViewById(R.id.trip_amount);
                                final TextView driverTripDistance = (TextView) dialogTripSummary.findViewById(R.id.trip_distance);
                                final CustomRatingBar riderRating = (CustomRatingBar) dialogTripSummary.findViewById(R.id.rider_rating);
                                final CustomRatingBar driverRating = (CustomRatingBar) dialogTripSummary.findViewById(R.id.driver_rating);

                                if(tripID!=null) {
                                    //Listener to get Total Price from Firebase
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("Price");
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null) {
                                                String status = dataSnapshot.getValue().toString();
                                                if (status != null) {
                                                    System.out.println("Price from firebase"+status);
                                                    driverTripAmount.setText("$"+status);
                                                }
                                            }

                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    //Listener to get Total Distance from Firebase
                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("Distance");
                                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null) {
                                                String status = dataSnapshot.getValue().toString();
                                                if (status != null) {
                                                    System.out.println("Distance from firebase"+status);
                                                    driverTripDistance.setText("Total Distance : "+status+" KM");
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    //Listener to get driver rating from Firebase
                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID).child("driver_rating");
                                    databaseReference2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null) {
                                                String status = dataSnapshot.getValue().toString();
                                                if (status != null) {
                                                    System.out.println("Rating from Driver"+status);
                                                    driverRating.setRating(Float.parseFloat(status));
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }


                                riderRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                        System.out.println("Rating value"+rating);
                                            updateRating(String.valueOf(rating));
                                    }
                                });

                                if(driverProfileName!=null) {
                                    driverProfileName=driverProfileName.replaceAll("%20"," ");
                                    driverName.setText(driverProfileName);
                                }
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                tripDate.setText(currentDateTimeString);
                                Glide.with(getApplicationContext()).load(driverProfileImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        driverImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        if(!isFinishing()) {
                                            dialogTripSummary.dismiss();
                                        }

                                        Intent intent=new Intent(getApplicationContext(),MapActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                                //to avoid bad token exception
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            dialogTripSummary.show();
                                        }
                                    }
                                });
                            }
                            else if(status.matches("5")){
                                if (strCacnelStatus != null) {
                                    if (strCacnelStatus.equals("drivercliked")) {
                                        strCacnelStatus = "mt";
                                        databaseReference.removeEventListener(this);
                                        Intent intent=new Intent(getApplicationContext(),MapActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                    else{
                                        generateNotification(getApplicationContext(),"Rider Cancelled the Trip");
                                        databaseReference.removeEventListener(this);
                                        getState.putString("tripstate", "endclicked");
                                        getState.commit();

                                        //cancel trip
                                        cancelTrip();
                                        driverID=null;//clear existing driver
                                        showDriverCancelDialog();
                                    }

                                }
                                else{
                                    generateNotification(getApplicationContext(),"Rider Cancelled the Trip");
                                    databaseReference.removeEventListener(this);
                                    getState.putString("tripstate", "endclicked");
                                    getState.commit();

                                    //cancel trip
                                    cancelTrip();
                                    driverID=null;//clear existing driver
                                    showDriverCancelDialog();
                                }


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void checkToll() {
        if(driverID!=null){
            tollReference = FirebaseDatabase.getInstance().getReference().child("drivers_data").child(driverID).child("accept").child("tollfee");
            tollListener=tollReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            tollFee.setText("Toll Fee: "+status);

                            //Notification to show toll was added
                            if(!status.equals("0"))
                            generateNotification(getApplicationContext(), "Toll amount was added in your trip");
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }



    private void showDriverCancelDialog() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.cancel_trip));
        builder.setMessage(getString(R.string.rider_cancel));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog1, int which) {

                  Intent intent=new Intent(getApplicationContext(),MapActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                finish();
            }

        });


        builder.show();


    }


    //Payment Type Listener
    private void getPaymentReference() {
        if(userID!=null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID).child("Paymenttype");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        if (status != null) {
                            if (status.matches("stripe")) {
                                cashButton.setBackground(getResources().getDrawable(R.mipmap.ub__payment_type_delegate));
                            } else if (status.matches("cash")) {
                                cashButton.setBackground(getResources().getDrawable(R.drawable.ub__payment_type_cash));

                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updatecancelTrip()
    {

        String url = Constants.REQUEST_URL + "updateTrips/trip_id/" + tripID + "/trip_status/cancel/accept_status/5/distance/0";
        System.out.println(" ONLINE URL is " + url);

        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                            } catch (Exception e) {
                                //stopAnim();

                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //protected static final String TAG = null;
                if (error instanceof NoConnectionError) {
                    // stopAnim();
                    // Toast.makeText(Map_Activity.this, "An unknown network error has occured", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
        movieReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
    }


    //Get Latitude and Longitude from Address
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(MapActivity.this,Locale.ENGLISH);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            System.out.println("location from address"+location);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p1;
    }
    //fareestimate
    public void showFareEstimateDialog(String pickloc,String destloc,String category,String fareestimate){
        faredialog = new Dialog(MapActivity.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        faredialog.setContentView(R.layout.getfareestimate_layout);
        faredialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        faredialog.setCancelable(false);
        ImageView close = (ImageView) faredialog.findViewById(R.id.close);
        final TextView pickuplocation = (TextView) faredialog.findViewById(R.id.pick_location_fare);
        final TextView destinatocation = (TextView) faredialog.findViewById(R.id.drop_location_fare);
        final TextView categorytxt = (TextView) faredialog.findViewById(R.id.categorytxtfare);
        final TextView estimatedfare = (TextView) faredialog.findViewById(R.id.fareestimatetxt);

        pickuplocation.setText(pickloc);
        destinatocation.setText(destloc);
        categorytxt.setText(category);
        estimatedfare.setText("$ "+fareestimate);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faredialog.cancel();
            }
        });


     faredialog.show();
    }

    //Display profile details of Driver
    public void displayDetails(String driverID) {
        final String url = Constants.LIVE_URL_DRIVER + "editProfile/user_id/"+driverID;
        System.out.println("Driver Profile==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String  status = jsonObject.optString("status");

                        if(status.equals("Success")){
                            driverFirstname=jsonObject.optString("firstname");
                            driverLastName=jsonObject.optString("lastname");
                            driverProfileImageAccepted=jsonObject.optString("profile_pic");
                            driverMobileNumber=jsonObject.optString("mobile");
                            tripDriverCategory =jsonObject.optString("category");
                            System.out.println("THE TRIP DRIVER CATEGORY"+jsonObject.optString("category"));
                            try {
                                //Set Trip Name
                                if(driverFirstname!=null || driverLastName!=null) {
                                    driverProfileName = driverFirstname + " " + driverLastName;
                                    driverProfileName = driverProfileName.replaceAll("%20", " ");
                                    tripDriverName.setText(driverProfileName);
                                    tripCarName.setText(tripDriverCategory);
                                }

                                if(driverProfileImageAccepted!=null)
                                {
                                    driverProfileImage=driverProfileImageAccepted;
                                }

                                Glide.with(getApplicationContext()).load(driverProfileImageAccepted).asBitmap().centerCrop().error(R.drawable.account_circle).placeholder(R.drawable.account_circle).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        driverImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
                                //For update marker
                                updateMarker(tripDriverCategory);

                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }

                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
    }

    //Set cancel Request
    public void cancelRequest(String requestID) {
        final String url = Constants.REQUEST_URL + "cancelRequest/request_id/"+requestID;
        System.out.println("Cancel Request==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        System.out.println("The request is Cancelled"+jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
    }

    //acceptRequest
    public void acceptViews()
    {
        easyTimer.stop();
        if (!MapActivity.this.isFinishing() && progressBar != null) {
            progressBar.dismiss();
        }
        System.out.println("INside ACCEPT VIEWS");
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        headerButton.setText("ACCEPTED");
        setPickupLocation.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        trip_info_layout.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
    }

    //dismissRequest
    public void dismissViews()
    {
        easyTimer.stop();
        if (!MapActivity.this.isFinishing() && progressBar != null) {
            progressBar.dismiss();
        }
        requestBar.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.GONE);
        dropLocationLayout.setVisibility(View.GONE);
        setPickupLocation.setVisibility(View.VISIBLE);
        driverLayout.setVisibility(View.GONE);
        trip_info_layout.setVisibility(View.GONE);
    }


    //clearAllPrefrences
    public void clearpreferences() {
        final SharedPreferences.Editor editor = getSharedPreferences(Constants.MY_PREFS_NAME,getApplicationContext().MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        //clear state
        final SharedPreferences.Editor stateeditor = getSharedPreferences(Constants.MY_STATE_KEY,getApplicationContext().MODE_PRIVATE).edit();
        stateeditor.clear();
        stateeditor.commit();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();

            this.finishAffinity();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            System.exit(0);
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //A timer to get response every X seconds
    public void AsynchTaskTimer(final String requestID) {
        System.out.println("Request ID before starting Timer"+requestID);
        if(requestID!=null) {
            easyTimer.start();
            easyTimer.setOnTaskRunListener(new EasyTimer.OnTaskRunListener() {
                @Override
                public void onTaskRun(long past_time, String rendered_time) {
                    // Change UI or do something with past_time and rendered_time.
                    // It will NOT block the UI thread.
                    getRequestID(requestID);
                }
            });
        }

    }

    //get Details of Driver
    private void getRiderDetails() {
        final String url = Constants.LIVE_URL + "editProfile/user_id/"+userID;
        System.out.println("Rider Profile in Map==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String  status = jsonObject.optString("status");

                        if(status.equals("Success")){
                            String firstName=jsonObject.optString("firstname");
                            String lastName=jsonObject.optString("lastname");
                            String profileImage=jsonObject.optString("profile_pic");
                            if(firstName!=null) {
                                firstName=firstName.replaceAll("%20"," ");
                                txtUserName.setText(firstName);
                            }
                            try {

                                Glide.with(getApplicationContext()).load(profileImage).asBitmap().error(R.drawable.account_circle_grey).centerCrop().placeholder(R.drawable.account_circle).skipMemoryCache(true).into(new BitmapImageViewTarget(userProfileImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        userProfileImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }

                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
    }

    //No Drivers Alert
    public void noDriversAlert(String title, String message) {

        android.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(MapActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        if(!((Activity) MapActivity.this).isFinishing())
        {
            //show dialog
            builder.show();
            easyTimer.stop();
        }


    }

    public void cancelRequest()
    {
        menuButton.setBackground(getResources().getDrawable(R.drawable.menu));
        dropLocationLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.VISIBLE);

    }

    /** Direction listener to draw poly line*/
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        System.out.println("Direction status"+direction.getStatus());
        System.out.println("Accepting Trip LatLng"+startLatLng+"||"+pickupLatLng);
        System.out.println("On Trip LatLng"+startLatLng+"||"+destinationLatLng);
        if(startLatLng!=null || destLatLng!=null) {
                if (direction.isOK())
                {
                    mMap.clear();
                if(headerButton.getText().toString().equals("ACCEPTED") ||headerButton.getText().toString().equals("ARRIVING")) {
                    Marker mMarker=mMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__marker_vehicle_fallback)));
                        /*mMarker.setAnchor(0.5f,0.5f);
                        mMarker.setRotation(bearingBetweenLatLngs(startLatLng,pickupLatLng));*/
                    mMarker.setAnchor(0.5f,0.5f);
                    mMarker.setRotation(convertLatLngToLocation(destLatLng).getBearing());
                    mMap.addMarker(new MarkerOptions().position(pickupLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_pickup)));
                }
                else
                {
                    Marker mMarker=mMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__marker_vehicle_fallback)).flat(true));
                    //mMarker.setRotation(star);
                    mMarker.setAnchor(0.5f,0.5f);
                    mMarker.setRotation(convertLatLngToLocation(destinationLatLng).getBearing());
                    mMap.addMarker(new MarkerOptions().position(destinationLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__ic_pin_dropoff)));
                }
                    ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                    mPolyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 3, Color.BLACK));

            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }


    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        System.out.println("location at geoquery"+location);

        if(location!=null)
        {
            //Get the ETA from Pickup point to Driver
            if(center!=null)
            {
                calculateETA(center,new LatLng(location.latitude,location.longitude));
            }
            //Car shown change it to SET PICKUP LOCATION
            setPickupLocation.setText("SET PICKUP LOCATION");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ub__button_request_circle_normal,0,R.mipmap.ub__button_request_circle_arrow_normal,0);
            setPickupLocation.setCompoundDrawablePadding(5);
            txtETA.setVisibility(View.VISIBLE);

            // Add a new marker to the map
            Marker marker = this.mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ub__marker_vehicle_fallback)));
            this.markers.put(key, marker);

        }

    }

    @Override
    public void onKeyExited(String key) {
        System.out.println("no matches found");
        System.out.println("Total Number of Markers"+markers.size());
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            if(markers.size()==0) {
                //Car not shown change it to NO CARS AVAILABLE
                setPickupLocation.setText("NO CARS AVAILABLE");
                setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                txtETA.setVisibility(View.GONE);
            }
            marker.remove();
            this.markers.remove(key);
            System.out.println("Check marker is empty"+markers.isEmpty());
        }


    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        System.out.println("Key moved");
        // Move the marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }

    }

    @Override
    public void onGeoQueryReady() {
        System.out.println("geoquery ready");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        System.out.println("geoquery error"+error);
    }

    @Override
    public void onCameraChange(final CameraPosition cameraPosition) {
        if(!tripStatus) {
            //getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude);
            center = cameraPosition.target;

            setPickupLocation.setText("NO CARS AVAILABLE");
            setPickupLocation.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            txtETA.setVisibility(View.GONE);
            /*if(setPickupLocation.getText().toString().matches("NO CARS AVAILABLE")) {
                requestLayout.setVisibility(View.GONE);
                categoryLayout.setVisibility(View.VISIBLE);
            }*/

            if(mMap!=null)
                mMap.clear();

            //Geo Fire
            geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("drivers_location").child(getCategory()));
            setGeofire(geoFire);
            geoQuery = getGeofire().queryAtLocation(new GeoLocation(center.latitude, center.longitude), 5);
            if (this.geoQuery != null) {
                this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                // radius in km
                this.geoQuery.setRadius(5);
                this.geoQuery.addGeoQueryEventListener(MapActivity.this);
            }
            else{
                Toast.makeText(MapActivity.this, "Geoquery Null", Toast.LENGTH_SHORT).show();
            }

            try {
                new GetLocationAsync(center.latitude, center.longitude).execute();
            } catch (Exception e) {
            }

        }
    }

    public void setCategory(String carcategory){
        this.carCategory=carcategory;

    }

    public String getCategory(){
        if(category!=null){
            return carCategory;
        }
        else{
            return "Hatchback";
        }

    }

    public void setGeofire(GeoFire geofire){
        this.geoFire=geofire;
    }

    public GeoFire getGeofire(){
        return geoFire;
    }

    //get Instant location updates form Driver
    private void updateMarker(String category) {
        if(driverID!=null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("drivers_location").child(category).child(driverID).child("l");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        String status = dataSnapshot.getValue().toString();
                        System.out.println("Status" + status);

                        String[] lat1ong = status.split(",");

                        String latitude = lat1ong[0];
                        String longitude = lat1ong[1];

                        String latreplace = latitude.replaceAll("\\[", "");
                        String longreplace = longitude.replaceAll("\\]", "");

                        Double laat = Double.parseDouble(latreplace);
                        Double lngg = Double.parseDouble(longreplace);


                        LatLng driverLocation = new LatLng(laat, lngg);
                        startLatLng = driverLocation;

                        System.out.println("THE LATLNG ++"+startLatLng.latitude+"Destination"+pickupLatLng);

                        if (startLatLng != null & destLatLng != null) {
                            if(headerButton.getText().toString().equals("ACCEPTED") ||headerButton.getText().toString().equals("ARRIVING")) {
                                try{
                                    GoogleDirection.withServerKey(Constants.Google_API_KEY)
                                            .from(startLatLng)
                                            .to(pickupLatLng)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(MapActivity.this);
                                }catch (Exception e){e.printStackTrace();}

                            }
                            else
                            {
                                try{
                                    GoogleDirection.withServerKey(Constants.Google_API_KEY)
                                            .from(startLatLng)
                                            .to(destinationLatLng)
                                            .transportMode(TransportMode.DRIVING)
                                            .execute(MapActivity.this);
                                }catch (Exception e){e.printStackTrace();}

                            }

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }





    @Override
    protected void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        if(geoQuery!=null) {
            this.geoQuery.removeAllListeners();
            for (Marker marker : this.markers.values()) {
                marker.remove();
            }
            this.markers.clear();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // add an event listener to start updating locations again
        if(!tripStatus) {
            if (geoQuery != null) {
                try {
                    this.geoQuery.addGeoQueryEventListener(this);
                } catch (NullPointerException e) {
                    System.out.print("Geo query event listener Null Point exception"+e);
                } catch (IllegalArgumentException e) {
                    System.out.print("Geo query event listener Illegal Argument exception"+e);
                }
            }
        }
    }


    //generateNotifications
    private static void generateNotification(Context context,String message) {

        //Some Vars
        final int NOTIFICATION_ID = 1; //this can be any int
        String title = "ARCANE";
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Building the Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(largeIcon);
        builder.setContentText(message);
        builder.setContentTitle(title);

        builder.setLights(Color.RED, 3000, 3000);
        builder.setLights(Color.RED, 3000, 3000);
        builder.setSound(uri);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.getNotification().flags= Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;;


        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
                timer.cancel();
            }
        }, 10000, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void callProfile() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        Intent settings = new Intent(this, SettingsActivity_.class);
        startActivity(settings);
    }


    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public void getAcceptState() {
        tripStatus=true;
        trip_info_layout.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.GONE);
        headerButton.setVisibility(View.VISIBLE);
        headerButton.setText("ACCEPTED");
        setPickupLocation.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);
        dropLocationLayout.setVisibility(View.GONE);
        locationLayout.setVisibility(View.GONE);
        trip_info_layout.setVisibility(View.VISIBLE);

        if(geoQuery!=null) {
            System.out.println("removing geo query listener");
            geoQuery.removeAllListeners();
            for (Marker marker : markers.values()) {
                marker.remove();
            }
            markers.clear();
        }
        //acceptViews();
        //checktripcancelstatus();
        String startaddress=state.getString("pickupposition",null);
        String endaddress=state.getString("destposition",null);
        LatLng startLatLng = getLocationFromAddress(startaddress);
        LatLng endLatLng = getLocationFromAddress(endaddress);

        pickupLatLng=startLatLng;
        destinationLatLng=endLatLng;

        driverID = state.getString("tripdriverid", null);
        getstatusfromfirebase();
        if(driverID!=null ){
            displayDetails(driverID);
        }

    }


    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            x = latitude;
            y = longitude;

        }

        @Override
        protected void onPreExecute() {
        }

        @SuppressLint("NewApi")
        @Override
        protected String doInBackground(String... params) {

            try {
                geocoder = new Geocoder(MapActivity.this);
                addressList = geocoder.getFromLocation(x, y, 1);

                str = new StringBuilder();

                if (Geocoder.isPresent()) {

                    if (addressList.size() > 0) {
                        Address returnAddress = addressList.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");

                        for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(returnAddress.getAddressLine(i)).append("\n");
                        }
                        completeAddress=strReturnedAddress.toString();
                        pickupCountryCode=addressList.get(0).getCountryCode();
                    }
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                System.out.println("Address is " + completeAddress);
                if(completeAddress!=null) {
                    originLocation.setText(completeAddress);
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }

    private float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
        Location beginLocation = convertLatLngToLocation(beginLatLng);
        Location endLocation = convertLatLngToLocation(endLatLng);
        return beginLocation.bearingTo(endLocation);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("someLoc");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public void getCountryName(Context context, double latitude, double longitude,String locationType) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                if(locationType.matches("ORIGIN")) {
                    pickupCountryCode=addresses.get(0).getCountryCode();
                } else {
                    dropCountryCode=addresses.get(0).getCountryCode();
                }
                System.out.println("Pickup==>"+pickupCountryCode);
                System.out.println("Drop==>"+dropCountryCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void alertSnackBar(String alertMessage){
        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), alertMessage, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.RED);
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxHeight(30);
        textView.setMaxLines(3);
        textView.setPaddingRelative(0,20,0,0);
        textView.setPadding(0,20,0,0);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWhite));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(0,30,0,0);
        textView.setLayoutParams(params);
        snackbar.show();
    }

    public void updateRating(String rating)
    {
        if(tripID!=null) {
            if (rating != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("trips_data").child(tripID);
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("rider_rating", rating);
                databaseReference.updateChildren(taskMap);
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected static double bearing(double lat1, double lon1, double lat2, double lon2){

        double longDiff= lon2-lon1;
        double y = Math.sin(longDiff)*Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(longDiff);

        return ( Math.toDegrees(Math.atan2(y, x)) + 360 ) % 360;
    }

    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(1000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }




    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private String calculateEstimate(double strDistance,String priceKM, String priceMin, String baseFare)
    {
        System.out.println("Inside calculation"+priceKM+priceMin+baseFare);
        try{
            Integer totalDistance= (int)strDistance * Integer.parseInt(priceKM);
            Integer totalPriceMin= Integer.parseInt(priceMin);
            Integer totalBaseFare = Integer.parseInt(baseFare);

            Integer totalEstimate = totalDistance+totalPriceMin+totalBaseFare;
            return String.valueOf(totalEstimate);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }

    //Function to Calculate ETA between two LatLng
    private void calculateETA(LatLng originAddress, LatLng destinationAddress) {

        String url=Constants.DISTANCE_MATRIX+"origins="+originAddress.latitude+","+originAddress.longitude+"&destinations="+destinationAddress.latitude+","+destinationAddress.longitude+"&sensor=false&key="+Constants.Google_DIRECTION_KEY+"&mode=transit";
        System.out.print("Calculate ETA"+url);

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    JSONArray rows= response.optJSONArray("rows");
                    JSONObject json = rows.optJSONObject(0);
                    JSONArray elements=json.optJSONArray("elements");
                    JSONObject elemenJSONObject= elements.optJSONObject(0);
                    String duration=elemenJSONObject.optString("duration");
                    if(!duration.isEmpty()) {
                        JSONObject durationJSONObject = new JSONObject(duration);
                        totalDuration = durationJSONObject.optString("text");
                        totalDuration= totalDuration.replaceAll("[^0-9]", "");
                        System.out.println("Total Duration time- " + totalDuration);
                        txtETA.setText(totalDuration+"\n"+"MIN");
                        if(!setPickupLocation.getText().toString().matches("NO CARS AVAILABLE")) {
                            estimateTime.setText(totalDuration + " MIN");
                        }
                        else {
                            estimateTime.setText("--");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError) {
                    Toast.makeText(MapActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

}