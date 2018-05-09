package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.cog.arcaneRider.adapter.OnLoadMoreListener;
import com.cog.arcaneRider.adapter.YourTrips;
import com.cog.arcaneRider.adapter.YourTripsListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

@EActivity (R.layout.activity_your_trips)
public class YourTripsActivity extends AppCompatActivity {

    private ArrayList<YourTrips> tripsListItems= new ArrayList<YourTrips>();
    private YourTripsListAdapter tripsListsAdapter;
    ProgressDialog progressDialog;
    String userID,tripID,pickupAddress,dropAddress,tripDate,date;
    Handler handler;
    JSONArray jsonArray;

    @ViewById(R.id.tripRecyclerView)
    RecyclerView tripLists;

    @ViewById(R.id.trip_history)
    TextView tripDetails;

    @ViewById(R.id.backButton)
    ImageButton back;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @AfterViews
    void yourTripsActivity()
    {

        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("User ID in YourTrips" + userID);

        LinearLayoutManager verticalLayoutmanager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        tripLists.setLayoutManager(verticalLayoutmanager);
        tripsListsAdapter = new YourTripsListAdapter(this, tripsListItems, tripLists);
        tripLists.setAdapter(tripsListsAdapter);
        handler = new Handler();

        displayYourTrips();

        tripLists.addOnItemTouchListener(
                new RecyclerItemClickListener(this, tripLists, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        Intent tripDetails = new Intent(getApplicationContext(), YourTripDetailsActivity_.class);
                        tripID=tripsListItems.get(position).getTripID();
                        tripDetails.putExtra("created",tripsListItems.get(position).getTripDate());
                        tripDetails.putExtra("trip_id",tripID);
                        startActivity(tripDetails);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        /*tripsListsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                tripsListItems.add(0,null);
                tripsListsAdapter.notifyItemInserted(tripsListItems.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreData();
                    }
                }, 2000);

            }
        });*/

    }

    @Click(R.id.backButton)
    void goBack(){
        finish();
    }

    private void displayYourTrips() {
        showDialog();
//        final String url = Constants.REQUEST_URL + "yourtrips/userid/"+userID;
        final String url = Constants.REQUEST_URL+"yourtrips/userid/"+userID;
        System.out.println("Your Trips URL==>"+url);
        final JsonArrayRequest tripListReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() <= 0) {
                    tripDetails.setVisibility(View.VISIBLE);
                    tripLists.setVisibility(View.GONE);
                    dismissDialog();
                } else {
                    //jsonArray=response;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            System.out.println("response from Your Trips" + jsonObject);
//                        if(responseStatus.matches("")){
                            YourTrips trips = new YourTrips();
                            if(!jsonObject.optString("total_price").isEmpty()) {
                                trips.setTripID(jsonObject.optString("trip_id"));
                                pickupAddress = jsonObject.optString("pickup_address");
                                dropAddress = jsonObject.optString("drop_address");
                                trips.setDriverImage(jsonObject.optString("driver_profile"));
                                trips.setCarImage(jsonObject.optString(null));
                                trips.setCarType(jsonObject.optString("car_category"));
                                tripDate = getDate(Long.parseLong(jsonObject.optString("created")));
                                trips.setTripDate(tripDate);
                                trips.setTripAmount(jsonObject.optString("total_price"));
                                trips.setPickupLocation(pickupAddress);
                                trips.setDropLocation(dropAddress);
                                tripsListItems.add(0, trips); // Reverse the list items in the Array
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismissDialog();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    dismissDialog();

                    tripsListsAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                } if(volleyError instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(tripListReq);
        tripListReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void loadMoreData() {

        //   remove progress item
        tripsListItems.remove(tripsListItems.size() - 1);
        tripsListsAdapter.notifyItemRemoved(tripsListItems.size());
        //add items one by one
        int start = tripsListItems.size();
        int end = start + 5;

        for (int i = start + 1; i <= end; i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                System.out.println("response in LoadMore" + jsonObject);
                YourTrips trips = new YourTrips();
                trips.setTripID(jsonObject.optString("trip_id"));
                pickupAddress = jsonObject.optString("pickup_address");
                dropAddress = jsonObject.optString("drop_address");
                trips.setDriverImage(jsonObject.optString("driver_profile"));
                trips.setCarImage(jsonObject.optString(null));
                trips.setCarType(jsonObject.optString("car_category"));
                tripDate = getDate(Long.parseLong(jsonObject.optString("created")));
                trips.setTripDate(tripDate);
                trips.setTripAmount(jsonObject.optString("total_price"));
                trips.setPickupLocation(pickupAddress);
                trips.setDropLocation(dropAddress);
                tripsListItems.add(0,trips); // Reverse the list items in the Array
            } catch (JSONException e) {
                e.printStackTrace();
                dismissDialog();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            tripsListsAdapter.notifyItemInserted(tripsListItems.size());
        }
        tripsListsAdapter.setLoaded();
    }



    public void onBackPressed(){
        finish();
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

    private String getDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Calendar objCalendar =
                Calendar.getInstance(TimeZone.getDefault());
        objCalendar.setTimeInMillis(time*1000);//edit
        String date = dateFormat.format(objCalendar.getTime());
        objCalendar.clear();
        System.out.println("Trip Date==>"+date);
        return date;
    }


    static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true;
            }
            return false;
        }

        @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent (boolean disallowIntercept){}
    }

}