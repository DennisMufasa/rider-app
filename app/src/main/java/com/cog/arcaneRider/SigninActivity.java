package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@EActivity(R.layout.activity_signin)
public class SigninActivity extends AppCompatActivity implements ValidationListener {

    String eMail,passWord,signInStatus,signInMessage;

    ProgressDialog progressDialog;
    Validator validator;

    @NotEmpty (message = "")
    @Email (message = "Enter Valid Email")
    @ViewById (R.id.userEmail)
    EditText inputEmail;

    @NotEmpty (message = "Enter Password")
    @ViewById (R.id.userPassword)
    EditText inputPassword;

    SharedPreferences.Editor editor;

    String userID,userFirstName,userLastName,userEmail,userMobile;


    @AfterViews
    void signinActivity() {
        //Change Font to Whole View
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(),getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        editor = getSharedPreferences(Constants.MY_PREFS_NAME,getApplicationContext().MODE_PRIVATE).edit();

        //Validation Listener
        validator = new Validator(this);
        validator.setValidationListener(SigninActivity.this);

    }

    @Click({R.id.imageButton3,R.id.imageButton2})
    public void signIn(){
        validator.validate();
    }

    @Click (R.id.backButton)
    public void goBack() {
        finish();
    }

    @Click(R.id.forgotPassword)
    public void forgotPassword(){
        Intent i = new Intent(this,ForgotPasswordActivity_.class);
        startActivity(i);
        finish();
    }

    @Click(R.id.createAccount)
    public void signUp(){
        Intent i = new Intent(this,SignupName_.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onValidationSucceeded() {
        eMail = inputEmail.getText().toString().toLowerCase().trim();
        passWord = inputPassword.getText().toString();
        passWord=passWord.replaceAll(" ","%20");
        if(!TextUtils.isEmpty(eMail) && android.util.Patterns.EMAIL_ADDRESS.matcher(eMail).matches()){
            callSignIn();
        } else {
            inputEmail.setError(getResources().getString(R.string.invalid_email_address));
        }
    }

    private void callSignIn() {


        showDialog();
        final String url = Constants.LIVE_URL + "signIn/password/"+passWord+"/email/"+eMail;
        System.out.println("SignUpURL==>"+url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        signInStatus = jsonObject.optString("status");
                        signInMessage = jsonObject.optString("message");

                        if(signInStatus.equals("Success")){
                            userID=jsonObject.optString("userid");
                            userFirstName=jsonObject.optString("first_name");
                            userLastName=jsonObject.optString("last_name");
                            userEmail=jsonObject.optString("email");
                            userMobile=jsonObject.optString("mobile");
                            savepreferences();
                            Intent intent = new Intent(getApplicationContext(),MapActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), R.string.logged_in_successfully,Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.invalid_username_or_password,Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }
                    } catch (JSONException e) {
                        dismissDialog();
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        dismissDialog();
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismissDialog();
                if (volleyError instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if(message.equals("Invalid password"))
                message="Enter Password";
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
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
    }

}