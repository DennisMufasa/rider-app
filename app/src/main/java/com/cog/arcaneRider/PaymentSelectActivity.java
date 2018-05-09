package com.cog.arcaneRider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.androidadvance.topsnackbar.TSnackbar;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_select_payment)
public class PaymentSelectActivity extends AppCompatActivity {

    @ViewById(R.id.text_cash)
    TextView inputCash;

    @ViewById(R.id.text_card)
    TextView inputCard;

    Drawable tickDrawable;

    String userID,cardStatus;
    
    boolean paymentMade=false;



    @AfterViews
    void selectPayment(){

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in Map" + userID);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        tickDrawable = getApplicationContext().getResources().getDrawable( R.drawable.check);

        //getPaymentType
        getPaymentReference();

        getRiderDetails();


    }

    @Click(R.id.text_cash)
    void cashPayment(){
        updatePayment(Constants.PAYMENT_TYPE_CASH);
    }

    @Click(R.id.text_card)
    void cardPayment(){
        if(paymentMade) {
            updatePayment(Constants.PAYMENT_TYPE_CARD);
        }
        else {
            TSnackbar snackbar =TSnackbar.make(findViewById(android.R.id.content),"Click Add payment to select Credit or Debit Card",TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.RED);
            TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
        
    }

    @Click(R.id.add_payment)
    void addPayment(){
        if(paymentMade) {
            TSnackbar snackbar =TSnackbar.make(findViewById(android.R.id.content),"You have added your card already!",TSnackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.WHITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
        else {
            Intent card=new Intent(this,CardPaymentActivity_.class);
            startActivity(card);
            finish();

        }
    }

    @Click(R.id.backButton)
    void goBack(){
        finish();
    }

    public void updatePayment(String paymentType)
    {
        if(paymentType!=null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put("Paymenttype", paymentType);
            databaseReference.updateChildren(taskMap);
        }
    }

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
                                inputCard.setCompoundDrawablesWithIntrinsicBounds( null, null, tickDrawable, null);
                                inputCash.setCompoundDrawablesWithIntrinsicBounds( null, null, null, null);
                            } else if (status.matches("cash")) {
                                inputCash.setCompoundDrawablesWithIntrinsicBounds( null, null, tickDrawable, null);
                                inputCard.setCompoundDrawablesWithIntrinsicBounds( null, null, null, null);
                                //Notification to show the trip has started
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

    //get Details of Rider
    private void getRiderDetails() {
        final String url = Constants.LIVE_URL + "editProfile/user_id/"+userID;
        System.out.println("Rider Profile in Map==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String  status = jsonObject.optString("card_status");

                        if(status.equals("0")){
                            paymentMade=false;
                        } else {
                            paymentMade=true;
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
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
