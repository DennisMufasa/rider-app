package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.creditcard.fields.CreditCardModule;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_card_payment)
public class CardPaymentActivity extends AppCompatActivity {
    CreditCardModule creditCardModule;
    TextView reportingTextView;
    Button payButton;
    String userID, creditCardNumber, creditCardMonth, creditCardYear, creditCardCvv, stripeTokenID, status, message;
    ProgressDialog progressDialog;

    @AfterViews
    void cardPayment() {
        creditCardModule = (CreditCardModule) findViewById(R.id.credit_card_module);
        payButton = (Button) findViewById(R.id.pay_button);

        //UserID from Shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in Cardpayments" + userID);


    }

    @Click(R.id.pay_button)
    void addCard() {

        boolean complete = creditCardModule.getCreditCardController().isComplete();
        if (complete) {
            submitCard();
        } else {
            Toast.makeText(this, "Invalid Card", Toast.LENGTH_SHORT).show();
        }
    }

    public void submitCard() {
        showDialog();
        creditCardNumber = creditCardModule.getCreditCardNumberEditField().getRawCreditCardNumber().toString();
        creditCardCvv = creditCardModule.getCreditCardController().getCVV().toString();
        creditCardYear = creditCardModule.getCreditCardController().getExpirationDate().toString();

        String dateStr = creditCardYear;
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date date = null;
        try {
            date = (Date) formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        creditCardMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
        creditCardYear = String.valueOf(cal.get(Calendar.YEAR) + 1);

        // TODO: replace with your own test key
        final String publishableApiKey = BuildConfig.DEBUG ?
                "pk_test_FhElr4VVNXcfGpiGGuGr1R1L" :
                getString(R.string.com_stripe_publishable_key);

        Card card = new Card((creditCardNumber),
                Integer.valueOf(creditCardMonth),
                Integer.valueOf(creditCardYear),
                creditCardCvv
        );

        final Stripe stripe = new Stripe();
        stripe.createToken(card, publishableApiKey, new TokenCallback() {
            public void onSuccess(Token token) {
                // TODO: Send Token information to your backend to initiate a charge
                stripeTokenID = token.getId();
                System.out.println("TokenID==>" + token.getId());
                updatePayment(stripeTokenID, creditCardNumber);
            }

            public void onError(Exception error) {
                Log.d("Stripe error", error.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Stripe error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        });
    }


    @Click(R.id.backButton)
    void goBack() {
        Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
        startActivity(intent);
        finish();
    }

    private void updatePayment(String stripeTokenID, String creditCardNumber) {
        showDialog();
        final String url = Constants.LIVE_URL + "updateStripeToken/userid/" + userID + "/token/" + stripeTokenID + "/card_number/" + creditCardNumber;
        System.out.println("SignUpURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener < JSONArray > () {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");

                        if (status.equals("Success")) {
                            Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
                            intent.putExtra("paid", true);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.your_card_was_addedd_successfully, Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_while_adding_your_car, Toast.LENGTH_SHORT).show();
                            dismissDialog();
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
                if (volleyError instanceof NoConnectionError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PaymentSelectActivity_.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        creditCardModule.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        creditCardModule.onRestoreSavedInstanceState(savedInstanceState);
    }


    public void showDialog() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    public void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            if(!isFinishing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }



}