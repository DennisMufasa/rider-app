package com.cog.arcaneRider;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_signup_name)
public class SignupName extends Activity implements Validator.ValidationListener {

    public String firstName,lastName;
    Validator validator;


    @NotEmpty (message = "Enter First Name")
    @ViewById (R.id.edtFirstName)
    MaterialEditText inputFirstName;

    @NotEmpty (message = "Enter Last Name")
    @ViewById (R.id.edtLastName)
    MaterialEditText inputLastName;

    @AfterViews
    void signUpName() {
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
        //Change Font to Whole View
        validator = new Validator(this);
        validator.setValidationListener(this);
    }


    @Click({R.id.imageButton3,R.id.imageButton2})
    void toSignUpEmail() {
        validator.validate();
    }

    @Click (R.id.backButton)
    void goBack(){

        super.onBackPressed();
    }

    @Override
    public void onValidationSucceeded() {
        firstName = inputFirstName.getText().toString().trim();
        lastName = inputLastName.getText().toString().trim();
/*// Sending side
        byte[] data = null;
        try {
            data = firstName.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        System.out.println("encoded FirstName "+base64);*/

            Intent i = new Intent(SignupName.this,SignupEmail_.class);
            i.putExtra("firstname",firstName);
            i.putExtra("lastname",lastName);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

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
}
