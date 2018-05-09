package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.FontChangeCrawler;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@EActivity(R.layout.activity_your_trip_details)
public class YourTripDetailsActivity extends AppCompatActivity {

    String userID,tripID,strTripDate,strDriveName,strDriverImage,strCarName,strCarImage,strDistanceTraveled,
            strPickupTime,strDropTime,strPickupLocation,strDropLocation,strPaymentType,strTotalAmount,strRating;
    ProgressDialog progressDialog;

    @ViewById(R.id.trip_date)
    TextView tripDate;

    @ViewById(R.id.driver_name)
    TextView driverName;

    @ViewById(R.id.car_type)
    TextView carName;

    @ViewById(R.id.distance_traveled)
    TextView distanceTraveled;

    @ViewById(R.id.pickup_time)
    TextView pickupTime;

    @ViewById(R.id.drop_time)
    TextView dropTime;

    @ViewById(R.id.pick_location)
    TextView pickupLocation;

    @ViewById(R.id.drop_location)
    TextView dropLocation;

    @ViewById(R.id.payment_mode)
    TextView paymentMode;

    @ViewById(R.id.total_price)
    TextView totalAmount;

    @ViewById(R.id.driver_image)
    ImageView driverImage;

    @ViewById(R.id.car_image)
    ImageView carImage;

    @ViewById(R.id.ratingBar)
    RatingBar ratingBar;

    @ViewById(R.id.backButton)
    ImageButton back;

    @AfterViews void yourTripDetails(){
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs=getSharedPreferences(Constants.MY_PREFS_NAME,MODE_PRIVATE);
        userID=prefs.getString("userid",null);
        System.out.println("User ID in YourTripsDetails"+userID);

        Intent tripDetails = getIntent();
        tripID = tripDetails.getStringExtra("trip_id");
        strTripDate = tripDetails.getStringExtra("created");
        System.out.println("Trip ID in YourTripsDetails"+tripID);

        getTripDetails();
    }

    private void getTripDetails() {
        showDialog();
        final String url = Constants.REQUEST_URL + "getTrips/trip_id/"+tripID;
        System.out.println("Get Trips URL==>"+url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() <= 0) {
                    dismissDialog();
                } else {
                    for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject jsonObject = response.getJSONObject(i);

                            //strTripDate=getDate(Long.parseLong(jsonObject.optString("created")));
                            strDriveName=jsonObject.optString("driver_name");
                            strDriverImage=jsonObject.optString("driver_profile");
//                            strCarImage=jsonObject.optString("");
                            strCarName=jsonObject.optString("car_category");
                            strDistanceTraveled=jsonObject.optString("total_distance");
//                            strPickupTime=jsonObject.optString("");
//                            strDropTime=jsonObject.optString("");
                            strPickupLocation=jsonObject.optString("pickup_address");
                            strDropLocation=jsonObject.optString("drop_address");
                            strPaymentType=jsonObject.optString("payment_mode");
                            strTotalAmount=jsonObject.optString("total_price");
//                            strRating=jsonObject.optString("");

                            if(strTripDate!=null)
                                tripDate.setText(strTripDate);

                            if(strDriveName!=null) {
                                strDriveName=strDriveName.replaceAll("%20"," ");
                                driverName.setText(strDriveName);
                            }

                            if(strCarName!=null) {
                                strCarName=strCarName.replaceAll("%20"," ");
                                carName.setText(strCarName);
                            }

                            if(strDistanceTraveled!=null) {
                                if(!strDistanceTraveled.equals("null")) {
                                    strDistanceTraveled = strDistanceTraveled.replaceAll("%20", " ");
                                    distanceTraveled.setText(strDistanceTraveled + " km");
                                }
                                else
                                {
                                    distanceTraveled.setText("0 km");
                                }
                            }

                            if(strPickupTime!=null) {
                                strPickupTime=strPickupTime.replaceAll("%20"," ");
                                pickupTime.setText(strPickupTime);
                            }

                            if(strDropTime!=null) {
                                strDropTime=strDropTime.replaceAll("%20"," ");
                                dropTime.setText(strDropTime);
                            }

                            if(strPickupLocation!=null) {
                                strPickupLocation=strPickupLocation.replaceAll("%20"," ");
                                pickupLocation.setText(strPickupLocation);
                            }

                            if(strDropLocation!=null) {
                                strDropLocation=strDropLocation.replaceAll("%20"," ");
                                dropLocation.setText(strDropLocation);
                            }

                            if(strPaymentType!=null) {
                                strPaymentType=strPaymentType.replaceAll("%20"," ");
                                paymentMode.setText(strPaymentType);
                            }

                            if(strTotalAmount!=null) {
                                strTotalAmount=strTotalAmount.replaceAll("%20"," ");
                                totalAmount.setText("$ "+strTotalAmount);
                            } else {
                                totalAmount.setText("$ 0");
                            }

                            Glide.with(getApplicationContext()).load(strDriverImage).asBitmap().centerCrop().placeholder(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(driverImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    driverImage.setImageDrawable(circularBitmapDrawable);
                                }
                            });

                          /*  Glide.with(getApplicationContext()).load(strCarImage).asBitmap().centerCrop().placeholder(R.drawable.trip_car).skipMemoryCache(true).into(new BitmapImageViewTarget(carImage) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    carImage.setImageDrawable(circularBitmapDrawable);
                                }
                            });*/

//                            ratingBar.setRating(Float.parseFloat(strRating));
                            ratingBar.setRating((float) 3.6);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismissDialog();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                } if (volleyError instanceof TimeoutError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(tripListReq);
        tripListReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Click(R.id.backButton)
    void goBack(){
        finish();
    }


    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getDefault();
        cal.setTimeInMillis(time * 1000);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date currenTimeZone = (Date) cal.getTime();
        System.out.println("Trip Date==>"+sdf.format(currenTimeZone));
        return sdf.format(currenTimeZone);
    }

    public void showDialog(){
        progressDialog= new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void dismissDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            if(!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }
}
