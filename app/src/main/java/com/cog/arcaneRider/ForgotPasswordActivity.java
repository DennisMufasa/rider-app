package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

@EActivity (R.layout.activity_forgot_password)
public class ForgotPasswordActivity extends AppCompatActivity implements Validator.ValidationListener {

  Validator validator;
  String strEmail;
  ProgressDialog progressDialog;
  @NotEmpty (message = "")
  @Email (message = "Enter Valid Email")
  @ViewById (R.id.edtForgotEmail)
  EditText inputEmail;
  String signInStatus,signInMessage;

  @AfterViews
  void forgotPasswordActivity(){
    FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(),getString(R.string.app_font));
    fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

    //Validation Listener
    validator = new Validator(this);
    validator.setValidationListener(this);
  }

  @Click (R.id.back)
    void getBack(){
      Intent intent=new Intent(this,SigninActivity_.class);
      startActivity(intent);
    finish();
  }

  @Click(R.id.btnResetPassword)
  void resetPassword(){
    validator.validate();
  }

  @Override
  public void onValidationSucceeded() {
    strEmail = inputEmail.getText().toString().toLowerCase().trim();
    callForgotPassword();
  }

  private void callForgotPassword() {

    showDialog();
    final String url = Constants.LIVE_URL + "forgotPassword/email/"+strEmail;
    System.out.println("ForgotPasswordURL==>"+url);
    final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        for (int i=0;i<response.length();i++){
          try {
            JSONObject jsonObject = response.getJSONObject(i);
            signInStatus = jsonObject.optString("status");
            signInMessage = jsonObject.optString("message");

            if(signInStatus.equals("Success")){
              Intent intent = new Intent(ForgotPasswordActivity.this,LaunchActivity_.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
              finish();
              Toast.makeText(getApplicationContext(), R.string.password_reset_sent_to_your_mail,Toast.LENGTH_SHORT).show();
              dismissDialog();
            } else {
              Toast.makeText(getApplicationContext(),"Your email does not exist",Toast.LENGTH_SHORT).show();
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
        if (volleyError instanceof NoConnectionError){
          dismissDialog();
          Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
        }
      }
    });

    AppController.getInstance().addToRequestQueue(signUpReq);
    signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
  }

  @Override
  public void onValidationFailed(List<ValidationError> errors) {
    for (ValidationError error : errors) {
      View view = error.getView();
      String message = error.getCollatedErrorMessage(this);

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
      if(!isFinishing()) {
        progressDialog.dismiss();
        progressDialog = null;
      }
    }
  }

}
