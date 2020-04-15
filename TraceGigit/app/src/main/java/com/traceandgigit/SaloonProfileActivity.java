package com.traceandgigit;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class SaloonProfileActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private boolean isLocationDetected = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imagePlaceHolder;
    private EditText saloonName,saloonAddress,saloonNumber,saloonEmailID;
    public static  String object_id = SignUpActivity.object_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_profile);
        mProgressDialog = new ProgressDialog(this);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        TextView uploadTextView = findViewById(R.id.uploadPicture);
        imagePlaceHolder = findViewById(R.id.profilePic);
        saloonName = findViewById(R.id.saloonName);
        saloonAddress = findViewById(R.id.saloonAddress);
        saloonNumber = findViewById(R.id.saloonNumber);
        saloonEmailID = findViewById(R.id.saloonEmail);


        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            if (user.get(Constants.SHOP_NAME) != null) {
                saloonName.setText(user.get(Constants.SHOP_NAME).toString());
            }
            if (user.get(Constants.SHOP_ADDRESS) != null) {
                saloonAddress.setText(user.get(Constants.SHOP_ADDRESS).toString());
            }
            if (user.get(Constants.SHOP_PHONE) != null) {
                saloonNumber.setText(user.get(Constants.SHOP_PHONE).toString());
            }
            saloonEmailID.setText(user.getEmail());
        } else {
            // show the signup or login screen
        }


//        ParseUser user = ApplicationController.getCurrentUser(this);
//        if (user != null) {
//            if (user.get(Constants.SHOP_NAME) != null) {
//                saloonName.setText(user.get(Constants.SHOP_NAME).toString());
//            }
//            if (user.get(Constants.SHOP_ADDRESS) != null) {
//                saloonAddress.setText(user.get(Constants.SHOP_ADDRESS).toString());
//            }
//            if (user.get(Constants.SHOP_PHONE) != null) {
//                saloonNumber.setText(user.get(Constants.SHOP_PHONE).toString());
//            }
//            saloonEmailID.setText(user.getEmail());
//        }




        uploadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = false;
                String updated_email = saloonEmailID.getText().toString();
                String updated_saloonname = saloonName.getText().toString();
                if (updated_email != null && !updated_email.isEmpty() && !saloonNumber.getText().toString().isEmpty()
                        && saloonNumber.getText().toString().length() == 10
                        && saloonNumber.getText().toString() != null && updated_saloonname != null
                        && !updated_saloonname.isEmpty())
                {
                    ParseUser user = ParseUser.getCurrentUser();
                    user.setEmail(updated_email);
                    user.put("shop_phone", saloonNumber.getText().toString());
                    user.put("shop_name", updated_saloonname);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e==null){
                                Toast.makeText(SaloonProfileActivity.this, "succefully updated details", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SaloonProfileActivity.this, "unable to updated details " + e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                    if (!isLocationDetected) {
                        getGPSLocation();
                    }else{
                        Intent intent = new Intent(SaloonProfileActivity.this,OwnerActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(SaloonProfileActivity.this, "unable to updated details, please check the details you entered", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isLocationDetected) {
                    getGPSLocation();
                }else{
                    Intent intent = new Intent(SaloonProfileActivity.this,OwnerActivity.class);
                    startActivity(intent);
                }
            }
        });
        getGPSLocation();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                showImageAndUpload(imageBitmap);
            }
        }
    }

    private void showImageAndUpload(Bitmap imageBitmap) {
        imagePlaceHolder.setImageBitmap(imageBitmap);
        ParseUser user = ApplicationController.getCurrentUser(this);
        user.put(Constants.PROFILE_PIC,imageBitmap);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
    }

    private void getGPSLocation() {
        showProgressDialog("Please Wait, we are fetching your Location...");
        FetchAddressService addressService = new FetchAddressService(this.getLocalClassName(), this);
        Location gpsLocation = addressService.getLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = 0.0;
        double longitude = 0.0;
        if(gpsLocation != null){
            hideProgressDialog();
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            ConvertToAddress.getAddress(latitude,longitude,this,new GeocoderHandler());
            Log.d("LOCATION","Lat is "+ latitude+" and long is "+longitude);
            isLocationDetected = true;
            saveSaloonLocation(latitude,longitude);
        }else{
            hideProgressDialog();
            showSettingsAlert();
        }
    }

    private void saveSaloonLocation(double latitude, double longitude) {
        showProgressDialog("Please Wait, Saving your Location...");
        ParseUser user = ParseUser.getCurrentUser();
        user.put(Constants.LAT_LONG,latitude+","+longitude);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressDialog();
                if (e != null) {
                    Log.e("Exception",e.toString());
                }
            }
        });
    }

    private void showProgressDialog(String message) {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    private void showProgressDialog() {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Log.d("Location",locationAddress);
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SaloonProfileActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        SaloonProfileActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

}
