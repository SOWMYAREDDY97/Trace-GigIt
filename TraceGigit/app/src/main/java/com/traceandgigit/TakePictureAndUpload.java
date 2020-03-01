package com.traceandgigit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.s3.transfermanager.internal.TransferManagerUtils;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class TakePictureAndUpload extends Activity {


    private Button takePicture, uploadPicture;
    private ImageView imagePlaceHolder;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap mBitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        takePicture = findViewById(R.id.takePicture);
        uploadPicture = findViewById(R.id.uploadPicture);
        imagePlaceHolder = findViewById(R.id.imagePlaceHolder);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPictureToParse();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagePlaceHolder.setImageBitmap(imageBitmap);
                mBitmap = imageBitmap;
            }
        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void uploadPictureToParse(){
        ParseFile file = null;
        try {
            file = new ParseFile("picturePath", readInFile(getFileFromBitMap().getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Upload the image into Parse Cloud
        file.saveInBackground();

        // Create a New Class called "ImageUpload" in Parse
        ParseObject imgupload = new ParseObject("Image");

        // Create a column named "ImageName" and set the string
        imgupload.put("Image", "picturePath");


        // Create a column named "ImageFile" and insert the image
        imgupload.put("ImageFile", file);

        // Create the class and the columns
        imgupload.saveInBackground();

        // Show a simple toast message
        Toast.makeText(this, "Image Saved, Upload another one ",Toast.LENGTH_SHORT).show();

    }

    private byte[] readInFile(String path) throws IOException {
        // TODO Auto-generated method stub
        byte[] data = null;
        File file = new File(path);
        InputStream input_stream = new BufferedInputStream(new FileInputStream(
                file));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        data = new byte[16384]; // 16K
        int bytes_read;
        while ((bytes_read = input_stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytes_read);
        }
        input_stream.close();
        return buffer.toByteArray();

    }

    private void dispatchUploadPicture(){
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:088415aa-2fb9-44ef-821a-00d76e87c635", // Identity pool ID
                Regions.US_EAST_2 // Region
        );
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
        TransferNetworkLossHandler.getInstance(this);
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        // AmazonS3 mS3 = new AmazonS3Client(credentialsProvider);

        final TransferObserver mTransferObserver =
                transferUtility.upload("traceandgigit","image_"+System.currentTimeMillis(),getFileFromBitMap(), CannedAccessControlList.PublicRead);

        mTransferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    Toast.makeText(TakePictureAndUpload.this,"Upload Success",Toast.LENGTH_LONG).show();
                } else if (state.equals(TransferState.FAILED)) {
                    Toast.makeText(TakePictureAndUpload.this,"Upload Failed",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d("AWS_Upload % ", String.valueOf((bytesCurrent/bytesTotal)*100));
            }

            @Override
            public void onError(int id, Exception ex) {
                try {
                    Log.d("AWS_Upload ERROR ", Objects.requireNonNull(ex.getCause()).toString());
                    Log.d("AWS_Upload ERROR ", Objects.requireNonNull(ex.getLocalizedMessage()).toString());
                    Log.d("AWS_Upload ERROR ", Objects.requireNonNull(ex.getMessage()).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private File getFileFromBitMap(){

        File file = new File(getCacheDir(), "image_"+System.currentTimeMillis());
        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
