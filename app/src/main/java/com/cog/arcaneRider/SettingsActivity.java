package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.cog.arcaneRider.adapter.CountryCodePicker;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity (R.layout.activity_settings)
public class SettingsActivity extends AppCompatActivity {

    Validator validator;
    public String userID, firstName, lastName, email, mobileNumber, countryCode, profileImage, profileImageNew, status, message;
    ProgressDialog progressDialog;

    SharedPreferences.Editor editor;

    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;

    @ViewById(R.id.backButton)
    ImageButton backButton;

    @ViewById(R.id.editButton)
    ImageButton editButton;

    @ViewById(R.id.editCancelButton)
    ImageButton editCancel;


    @ViewById(R.id.save_button)
    Button saveButton;

    @NotEmpty (message = "Enter First Name")
    @Length (max = 15)
    @ViewById(R.id.edtFirstName)
    EditText inputFirstName;

    @NotEmpty (message = "Enter Last Name")
    @Length (max = 15)
    @ViewById(R.id.edtLastName)
    EditText inputLastName;

    @NotEmpty
    @ViewById(R.id.edtCountryCode)
    EditText inputCountryCode;

    @NotEmpty
    @ViewById(R.id.edtMobile)
    EditText inputMobileNumber;

    @ViewById(R.id.edtEmail)
    EditText inputEmail;

    @ViewById(R.id.ccp)
    CountryCodePicker ccp;

    @AfterViews
    void settingsActivity() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in settings" + userID);
        //Change Font to Whole View
        displayDetails();
    }

    @Click(R.id.editButton)
    void editProfile() {
    Intent i=new Intent(SettingsActivity.this,EditProfileActivity_.class);
        startActivity(i);
        finish();
    }


    @Click(R.id.backButton)
    void goBack(){
        finish();
    }

    public void displayDetails() {
        showDialog();
        final String url = Constants.LIVE_URL + "editProfile/user_id/"+userID;
        System.out.println("RiderProfileURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if(status.equals("Success")){
                            firstName=jsonObject.optString("firstname");
                            lastName=jsonObject.optString("lastname");
                            email=jsonObject.optString("email");
                            mobileNumber=jsonObject.optString("mobile");
                            profileImage=jsonObject.optString("profile_pic");
                            countryCode=jsonObject.optString("country_code");
//                            savepreferences();

                            try {
                                if(firstName.equals("null")||(firstName.equals(null)))
                                    inputFirstName.setHint("First Name");
                                else {
                                    firstName = firstName.replaceAll("%20", " ");
                                    inputFirstName.setText(firstName);
                                }

                                if(lastName.equals("null")||(lastName.equals(null)))
                                    inputLastName.setHint("Last Name");
                                else {
                                    lastName = lastName.replaceAll("%20", " ");
                                    inputLastName.setText(lastName);
                                }
                                if(email.equals("null")||(email.equals(null)))
                                    inputEmail.setHint("Email");
                                else
                                    inputEmail.setText(email);

                                if(mobileNumber.equals("null")||mobileNumber.equals(null))
                                    inputMobileNumber.setHint("Mobile number");
                                else
                                    inputMobileNumber.setText(mobileNumber);

                                if(countryCode.equals("null")||countryCode.equals(null))
                                    inputCountryCode.setHint("CC");
                                else
                                inputCountryCode.setText(countryCode);

                                Glide.with(getApplicationContext()).load(profileImage).asBitmap().error(R.drawable.account_circle_grey).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        edtProfileImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });


                            } catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            dismissDialog();
                        } else {
                            dismissDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    dismissDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NoConnectionError){
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }  if(volleyError instanceof TimeoutError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    dismissDialog();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void showDialog(){
        progressDialog = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
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

    public void onBackPressed(){
        finish();
    }

}
