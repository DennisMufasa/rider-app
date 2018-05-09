package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@EActivity (R.layout.activity_signup_email)
public class SignupEmail extends AppCompatActivity implements Validator.ValidationListener {

    public String firstName,lastName,email,emailStatus;
    Validator validator;
    ProgressDialog progressDialog;


    @NotEmpty (message = "")
    @Email (message = "Enter Valid Email")
    @ViewById (R.id.view)
    MaterialEditText inputEmail;


    @AfterViews
    void signUpEmail() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent i = getIntent();
        firstName = i.getStringExtra("firstname");
        lastName = i.getStringExtra("lastname");
    }

    @Click({R.id.imageButton3,R.id.imageButton2})
    void toSignUpName() {
        validator.validate();
    }

    @Click (R.id.backButton)
    public void goBack() {
        super.onBackPressed();
    }

    @Override
    public void onValidationSucceeded() {

        email = inputEmail.getText().toString().toLowerCase().trim();
        if(!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showDialog();

            final String url = Constants.LIVE_URL + "emailExist/email/"+email;
            System.out.println("EmailExistURL==>"+url);
            final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i=0;i<response.length();i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            emailStatus = jsonObject.optString("status");

                            if(emailStatus.equals("Success")){
                                Intent intent = new Intent(SignupEmail.this,SignupPassword_.class);
                                intent.putExtra("firstname",firstName);
                                intent.putExtra("lastname",lastName);
                                intent.putExtra("email",email);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                dismissDialog();
                            } else {
                                dismissDialog();
                                inputEmail.setError(getResources().getString(R.string.email_already_exists));
                            }
                        } catch (JSONException e) {
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
                    if (volleyError instanceof NoConnectionError){
                        dismissDialog();
                        Toast.makeText(getApplicationContext(), R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AppController.getInstance().addToRequestQueue(signUpReq);
            signUpReq.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        } else {
            inputEmail.setError(getString(R.string.invalid_email_address));
        }
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
