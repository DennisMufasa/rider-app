package com.cog.arcaneRider;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cog.arcaneRider.adapter.AppController;
import com.cog.arcaneRider.adapter.Constants;
import com.cog.arcaneRider.adapter.CountryCodeDialog;
import com.cog.arcaneRider.adapter.CountryCodePicker;
import com.cog.arcaneRider.adapter.FontChangeCrawler;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@EActivity(R.layout.activity_edit_profile)
public class EditProfileActivity extends AppCompatActivity implements CountryCodePicker.OnCountryChangeListener, Validator.ValidationListener {

    Validator validator;
    public String userID, firstName, lastName, email, mobileNumber, countryCode, profileImage, profileImageNew = "null", status, message;
    private static final int CAMERA_CAPTURE_IMAGE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    String picturePath, profImage, updateURL;
    ProgressDialog progressDialog;

    SharedPreferences.Editor editor;

    private Uri fileUri; // file url to store image/video

    @ViewById(R.id.profileImage)
    ImageView edtProfileImage;

    @ViewById(R.id.backButton)
    ImageButton backButton;


    @ViewById(R.id.save_button)
    Button saveButton;

    @NotEmpty(message = "Enter First Name")
    @ViewById(R.id.edtFirstName)
    EditText inputFirstName;

    @NotEmpty(message = "Enter Last Name")
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
        //Change font for the whole view
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), getString(R.string.app_font));
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        //UserID from Shared preferences
        SharedPreferences prefs = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        userID = prefs.getString("userid", null);
        System.out.println("UserID in settings" + userID);
        validator = new Validator(this);
        validator.setValidationListener(this);
        ccp.setOnCountryChangeListener(this);

        inputFirstName.setSelection(inputFirstName.getText().toString().length());
        displayDetails();
    }



    @Click(R.id.edtCountryCode)
    public void countryCode(View view) {
        CountryCodeDialog.openCountryCodeDialog(ccp); //Open country code dialog
    }

    @Click(R.id.profileImage)
    public void updateProfileImage() {
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(EditProfileActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.option));

        builder.setNegativeButton(getString(R.string.camera), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Image File name");
                fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        fileUri);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE);

            }
        });
        builder.setNeutralButton(getString(R.string.gallery), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, MEDIA_TYPE_IMAGE);


            }
        });


        builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });


        builder.show();

    }


    @Click(R.id.save_button)
    void saveProfile() {
        validator.validate();
    }

    @Click(R.id.backButton)
    void goBack() {
        Intent i = new Intent(EditProfileActivity.this, SettingsActivity_.class);
        startActivity(i);
        finish();
    }

    public void displayDetails() {
        showDialog();
        final String url = Constants.LIVE_URL + "editProfile/user_id/" + userID;
        System.out.println("RiderProfileURL==>" + url);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(url, new Response.Listener < JSONArray > () {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if (status.equals("Success")) {
                            firstName = jsonObject.optString("firstname");
                            lastName = jsonObject.optString("lastname");
                            email = jsonObject.optString("email");
                            mobileNumber = jsonObject.optString("mobile");
                            profileImage = jsonObject.optString("profile_pic");
                            countryCode = jsonObject.optString("country_code");
                            //                            savepreferences();

                            try {
                                if (firstName.equals("null") || (firstName.equals(null)))
                                    inputFirstName.setHint("First Name");
                                else {
                                    firstName = firstName.replaceAll("%20", " ");
                                    inputFirstName.setText(firstName);
                                }

                                if (lastName.equals("null") || (lastName.equals(null)))
                                    inputLastName.setHint("Last Name");
                                else {
                                    lastName = lastName.replaceAll("%20", " ");
                                    inputLastName.setText(lastName);
                                }
                                if (email.equals("null") || (email.equals(null)))
                                    inputEmail.setHint("Email");
                                else
                                    inputEmail.setText(email);

                                if (mobileNumber.equals("null") || mobileNumber.equals(null))
                                    inputMobileNumber.setHint("Mobile number");
                                else
                                    inputMobileNumber.setText(mobileNumber);

                                if (countryCode.equals("null") || countryCode.equals(null))
                                    inputCountryCode.setHint("CC");
                                else
                                    inputCountryCode.setText(countryCode);

                                Glide.with(getApplicationContext()).load(profileImage).asBitmap().centerCrop().error(R.drawable.account_circle_grey).skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        edtProfileImage.setImageDrawable(circularBitmapDrawable);
                                    }
                                });

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "There is an error", Toast.LENGTH_SHORT).show();
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
                if (volleyError instanceof NoConnectionError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void updateProfile() {

        firstName = inputFirstName.getText().toString().trim();
        lastName = inputLastName.getText().toString().trim();
        countryCode = inputCountryCode.getText().toString().trim();
        mobileNumber = inputMobileNumber.getText().toString().trim();
        email = inputEmail.getText().toString().trim();

        firstName = firstName.replaceAll(" ","%20");
        lastName= lastName.replaceAll(" ","%20");

        showDialog();
        System.out.println("ProfileImage==>" + profileImage);
        System.out.println("ProfileImageNew==>" + profileImageNew);
        if (profileImageNew.equals(null) || profileImageNew.equals("null")) {
            updateURL = Constants.LIVE_URL + "updateDetails/user_id/" + userID + "/firstname/" + firstName + "/lastname/" + lastName + "/mobile/" + mobileNumber + "/country_code/" + countryCode + "/city/" + "madurai" + "/email/" + email;
        } else {
            updateURL = Constants.LIVE_URL + "updateDetails/user_id/" + userID + "/firstname/" + firstName + "/lastname/" + lastName + "/mobile/" + mobileNumber + "/country_code/" + countryCode + "/profile_pic/" + profileImageNew + "/city/" + "madurai" + "/email/" + email;
        }
        System.out.println("RiderUpdateProfileURL==>" + updateURL);
        final JsonArrayRequest signUpReq = new JsonArrayRequest(updateURL, new Response.Listener < JSONArray > () {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        status = jsonObject.optString("status");
                        message = jsonObject.optString("message");

                        if (status.equals("Success")) {
                            //                            savepreferences();
                            Intent intent = new Intent(EditProfileActivity.this, SettingsActivity_.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //                            Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
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
                if (volleyError instanceof NoConnectionError) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });

        AppController.getInstance().addToRequestQueue(signUpReq);
        signUpReq.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onCountrySelected() {
        inputCountryCode.setText(ccp.getSelectedCountryCodeWithPlus());
    }

    private boolean validateCountryCode() {

        if (inputCountryCode.getText().toString().trim().isEmpty()) {
            inputCountryCode.setError("");
            inputCountryCode.setError(getString(R.string.enter_valid_cc));
            return false;
        } else if (inputCountryCode.getText().toString().equals("CC")) {
            inputCountryCode.setError("");
            inputCountryCode.setError(getString(R.string.enter_valid_cc));
            return false;
        } else {
            inputCountryCode.setError(null);
        }
        //requestFocus(countrycode);

        return true;
    }

    private boolean validatePhone() {
        if (inputMobileNumber.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_mobile_number));
            return false;
        } else if (inputCountryCode.getText().toString().trim().isEmpty()) {
            inputMobileNumber.setError(getString(R.string.enter_valid_cc));
            return false;
        } else if (!inputMobileNumber.getText().toString().trim().isEmpty()) {
            if (inputMobileNumber.getText().toString().substring(0, 1).matches("0")) {
                inputMobileNumber.setError("Enter a valid number");
                return false;
            } else {
                int maxLengthofEditText = 15;
                inputMobileNumber.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(maxLengthofEditText)
                });
                inputMobileNumber.setError(null);
            }
            return true;
        }

        return true;
    }

    private boolean validateUsing_libphonenumber() {
        countryCode = inputCountryCode.getText().toString();
        mobileNumber = inputMobileNumber.getText().toString();
        if (validatePhone() && validateCountryCode()) {
            System.out.println("CountryCode==>" + countryCode);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                countryCode = countryCode.replace("+", "");
            }
            System.out.println("SDK_VERSION==>" + Build.VERSION.SDK_INT);
            System.out.println("SDK_VERSION_RELEASE" + Build.VERSION.RELEASE);
            System.out.println("CountryCode1==>" + countryCode);
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
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

    @Override
    public void onValidationSucceeded() {

        if (inputFirstName.getText().toString().trim().length() > 15) {
            inputFirstName.setError("15 characters only allowed");
        } else if (inputLastName.getText().toString().trim().length() > 15) {
            inputLastName.setError("15 characters only allowed");
        } else if (!validateCountryCode()) {

        } else if (!validatePhone()) {

        } else if (!validateUsing_libphonenumber()) {
            inputMobileNumber.setError(getString(R.string.invalid_mobile_number));
        } else {
            updateProfile();
        }

    }

    @Override
    public void onValidationFailed(List < ValidationError > errors) {
        for (ValidationError error: errors) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_CAPTURE_IMAGE && resultCode == RESULT_OK) {

            String selectedImagePath = getRealPathFromURI(fileUri);
            picturePath = selectedImagePath;

            edtProfileImage.setScaleType(ImageView.ScaleType.FIT_XY);
            //                edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Glide.with(getApplicationContext()).load(picturePath).asBitmap().error(R.drawable.account_circle_grey).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    edtProfileImage.setImageDrawable(circularBitmapDrawable);
                }
            });


            new ImageuploadTask(this).execute();

        } /*else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {*/
        else if (requestCode == MEDIA_TYPE_IMAGE && resultCode == RESULT_OK && null != data) {

            //            String single_path = data.getStringExtra("single_path");
            Uri selectedImage = data.getData();
            String[] filePathColumn = {
                    MediaStore.Images.Media.DATA
            };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            //            edtProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Glide.with(getApplicationContext()).load(picturePath).asBitmap().error(R.drawable.account_circle_grey).centerCrop().skipMemoryCache(true).into(new BitmapImageViewTarget(edtProfileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    edtProfileImage.setImageDrawable(circularBitmapDrawable);
                }
            });

            new ImageuploadTask(EditProfileActivity.this).execute();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    private class ImageuploadTask extends AsyncTask < String, Void, Boolean > {
        private ProgressDialog dialog;
        private EditProfileActivity activity;

        public ImageuploadTask(EditProfileActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        private Context context;

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                if(!isFinishing()) {
                    dialog.dismiss();
                }
            }
            if (success) {} else {

            }
        }

        @Override
        protected Boolean doInBackground(final String...args) {
            try {
                // ... processing ...
                Upload_Server();
                return true;
            } catch (Exception e) {
                Log.e("Schedule", "UpdateSchedule failed", e);
                return false;
            }
        }
    }
    protected void Upload_Server() {
        // TODO Auto-generated method stub
        try {

            Log.e("Image Upload", "Inside Upload");

            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;

            String pathToOurFile = picturePath;
            //	  String pathToOurFile1 = imagepathcam;

            System.out.println("Before Image Upload" + picturePath);

            String urlServer = Constants.LIVE_URL_DRIVER+"imageUpload/";
            System.out.println("URL SETVER" + urlServer);
            System.out.println("After Image Upload" + picturePath);
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
            //  FileInputStream fileInputStream1 = new FileInputStream(new File(pathToOurFile1));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("URL is " + url);
            System.out.println("connection is " + connection);
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();


            System.out.println("image" + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            DataInputStream inputStream1 = null;
            inputStream1 = new DataInputStream(connection.getInputStream());
            String str = "";
            String Str1_imageurl = "";

            while ((str = inputStream1.readLine()) != null) {
                Log.e("Debug", "Server Response " + str);

                Str1_imageurl = str;
                Log.e("Debug", "Server Response String imageurl" + str);
            }
            inputStream1.close();
            System.out.println("image url" + Str1_imageurl);

            //get the image url and store
            profImage = Str1_imageurl.trim();
            JSONArray array = new JSONArray(profImage);
            JSONObject jsonObj = array.getJSONObject(0);
            System.out.println("image name" + jsonObj.getString("image_name"));

            profileImageNew = jsonObj.optString("image_name");

            System.out.println("Profile Picture Path" + profImage);
            System.out.println("Profile Picture Path" + profileImageNew);

        } catch (Exception e) {

            e.printStackTrace();

        }
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
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void onBackPressed() {
        Intent i = new Intent(EditProfileActivity.this, SettingsActivity_.class);
        startActivity(i);
        finish();
    }

}