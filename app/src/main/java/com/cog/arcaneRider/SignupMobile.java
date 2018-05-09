package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.CountryCodeDialog;
import com.cog.arcaneRider.adapter.CountryCodePicker;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@EActivity (R.layout.activity_signup_mobile)
public class SignupMobile extends AppCompatActivity implements CountryCodePicker.OnCountryChangeListener {

    public String firstName,lastName,email,passWord,mobileNumber,countrycode,registerID,signUpStatus,signUpMessage;
    ProgressDialog progressDialog;

    @ViewById(R.id.imageButton2)
    ImageButton submitArrow;

    @ViewById(R.id.imageButton3)
    ImageButton submitCircle;

    @ViewById(R.id.ccp)
    CountryCodePicker ccp;

    @NotEmpty (message = "")
    @ViewById(R.id.countryCode)
    MaterialEditText inputCountryCode;

    @NotEmpty (message = "Enter Mobile Number")
    @ViewById(R.id.mobileNumber)
    MaterialEditText inputMobileNumber;

    @ViewById(R.id.backButton)
    ImageButton backButton;

    SharedPreferences.Editor editor;

    String userID,userFirstName,userLastName,userEmail,userMobile;

    @AfterViews
    void signUpMobile() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        submitArrow.setEnabled(true);
        submitCircle.setEnabled(true);

        ccp.setOnCountryChangeListener(this);

        editor = getSharedPreferences(Constants.MY_PREFS_NAME,getApplicationContext().MODE_PRIVATE).edit();

        Intent i = getIntent();
        firstName = i.getStringExtra("firstname");
        lastName = i.getStringExtra("lastname");
        email = i.getStringExtra("email");
        passWord = i.getStringExtra("password");
    }

    @Click({R.id.imageButton3,R.id.imageButton2})
    void toSignUpMobile() {

        if (!validateCountryCode()) {

        }
        if (!validatePhone()) {

        } else if (!validateUsing_libphonenumber()) {
            inputMobileNumber.setError(getString(R.string.invalid_mobile_number));
        } else {
            submitArrow.setEnabled(false);
            submitCircle.setEnabled(false);
                callSignUp();
        }
    }

    @Click(R.id.countryCode)
    public void countryCode(View view) {
        CountryCodeDialog.openCountryCodeDialog(ccp);//Open country code dialog
    }

    @Click (R.id.backButton)
    public void goBack() {
        super.onBackPressed();
    }

    public void callSignUp(){

        showDialog();
        final String url = Constants.LIVE_URL + "signUp/regid/"+registerID+"/first_name/"+firstName+"/last_name/"+lastName+"/mobile/"+mobileNumber+"/country_code/"+countrycode+"/password/"+passWord+"/city/"+"null"+"/email/"+email;
        System.out.println("SignUpURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        signUpStatus = jsonObject.optString("status");
                        signUpMessage = jsonObject.optString("message");

                        if(signUpStatus.equals("Success")){
                            userID=jsonObject.optString("userid");
                            userFirstName=jsonObject.optString("first_name");
                            userLastName=jsonObject.optString("last_name");
                            userEmail=jsonObject.optString("email");
                            userMobile=jsonObject.optString("mobile");
                            savepreferences();
                            Toast.makeText(getApplicationContext(), R.string.successfully_registered,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupMobile.this,MapActivity.class);
                            intent.putExtra("userid",userID);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            dismissDialog();
                        } else if(signUpStatus.equals("Fail")){
                           inputMobileNumber.setError(getResources().getString(R.string.mobile_number_already_exisits));
                            submitArrow.setEnabled(true);
                            submitCircle.setEnabled(true);
                            dismissDialog();
                        } else {
                            submitArrow.setEnabled(true);
                            submitCircle.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        submitArrow.setEnabled(true);
                        submitCircle.setEnabled(true);
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        submitArrow.setEnabled(true);
                        submitCircle.setEnabled(true);
                        e.printStackTrace();
                    }
                }
                dismissDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    submitArrow.setEnabled(true);
                    submitCircle.setEnabled(true);
                    dismissDialog();
                } else if(volleyError instanceof NetworkError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    submitArrow.setEnabled(true);
                    submitCircle.setEnabled(true);
                    dismissDialog();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean validateCountryCode() {

        if (inputCountryCode.getText().toString().trim().isEmpty()) {
            inputCountryCode.setError("");
            inputMobileNumber.setError(getString(R.string.enter_country_code));
            return false;
        } else if (inputCountryCode.getText().toString().equals("CC")) {
            inputMobileNumber.setError(getString(R.string.enter_country_code));
            inputCountryCode.setError("");
            return false;
        } else {
            inputCountryCode.setError(null);
        }
        return true;
    }

    private boolean validatePhone() {
        if(inputMobileNumber.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        }
        else if (inputCountryCode.getText().toString().trim().isEmpty())
        {
            return false;
        }
        else  if (!inputMobileNumber.getText().toString().trim().isEmpty())
        {
            if (inputMobileNumber.getText().toString().substring(0, 1).matches("0")) {
                inputMobileNumber.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 15;
                inputMobileNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthofEditText)});
                inputMobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }

    private boolean validateUsing_libphonenumber() {
        if(inputMobileNumber.getText().toString().length()<=1){
            return false;
        }
        else{
            countrycode = inputCountryCode.getText().toString();
            mobileNumber = inputMobileNumber.getText().toString();
            if (validatePhone() && validateCountryCode()) {
                System.out.println("CountryCode==>" + countrycode);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    countrycode = countrycode.replace("+", "");
                }
                System.out.println("SDK_VERSION==>" + Build.VERSION.SDK_INT);
                System.out.println("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
                System.out.println("CountryCode1==>" + countrycode);
                PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
                String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countrycode));
                Phonenumber.PhoneNumber phoneNumber = null;

                try {
                    //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
                    phoneNumber = phoneNumberUtil.parse(mobileNumber, isoCode);
                } catch (NumberParseException e) {
                    System.err.println(e);
                }

                boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
                if (isValid) {
                    String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    return true;
                } else {
                    inputMobileNumber.setError(getString(R.string.enter_a_valid_mobile_number));
                    return false;
                }
            }
            return true;
        }

    }

    @Override
    public void onCountrySelected() {
        inputCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
        inputMobileNumber.setError(null);
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
            if(!isFinishing())
            {
            progressDialog.dismiss();
            progressDialog=null;
            }
        }
    }

    public void savepreferences()
    {
        editor.putString("userid", userID);
        editor.putString("username", userFirstName);
        editor.putString("userphonenum", userMobile);
        editor.commit();

        //Saving to Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("riders_location").child(userID);
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("Paymenttype","cash");

        ref.setValue(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                System.out.println("DATA SAVED SUCCESSFULLY");
                if(databaseError!=null){
                    System.out.println("DATA SAVED SUCCESSFULLY");
                }
            }
        });
    }

}
