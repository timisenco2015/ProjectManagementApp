package com.zihron.projectmanagementapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {
    private static final int  MY_PERMISSIONS_REQUEST_CODE = 100;
    private static final int GALLERY_PICTURE_REQUEST = 200;
    private static final int READ_STORAGE_PERMISSION_CODE = 300;
    private static final int WRITE_STORAGE_PERMISSION_CODE = 400;
   private ImageView imageViewDisplay;
    private S3ImageClass s3ImageClass;
    private S3Object s3object;
    private ImageView saveImageAmazonS3ImageView;
    private Activity activity;
    private Bitmap imageBitMap;
    private SharedPreferences sharedPreferences;
    private String userName;
    private String folderType;
    private String accesName;
    private String selectionType;
    private String groupName;
    private String fullName;

    private ImageView backArrowLogoImageVW;
    private static final int SELECTED_PICTURE=1;
    private static String[]permissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        activity = this;
        imageViewDisplay = (ImageView)findViewById(R.id.imageViewDisplayId);
        saveImageAmazonS3ImageView = (ImageView)findViewById(R.id.saveImageAmazonS3Id);
        backArrowLogoImageVW = (ImageView) findViewById(R.id.backToUserInformatnPageImgVWId);
        saveImageAmazonS3ImageView.setEnabled(false);
        backArrowLogoImageVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gAIntent = new Intent(GalleryActivity.this, HomeActivity.class);
                startActivity(gAIntent);
            }
        });

        folderType = getIntent().getExtras().getString("typeOfFolder");
        accesName = getIntent().getExtras().getString("accesName");
        selectionType = getIntent().getExtras().getString("selectionType", null);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        s3ImageClass = new S3ImageClass(activity, accesName, "profilepicfolder");

        if(selectionType.equalsIgnoreCase("friend"))
        {
            fullName = getIntent().getExtras().getString("fullName");
            if( s3ImageClass.isObjectExists())
            {
                imageViewDisplay.setImageBitmap(s3ImageClass.getImageBitMap());
                imageViewDisplay.invalidate();
            }
            else
            {
                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+fullName+"&background=90a8a8&color=fff&size=128").into( imageViewDisplay);
            }
        }
        else if(selectionType.equalsIgnoreCase("group"))
        {
            groupName = getIntent().getExtras().getString("groupName");
            if( s3ImageClass.isObjectExists())
            {
                imageViewDisplay.setImageBitmap(s3ImageClass.getImageBitMap());
                imageViewDisplay.invalidate();
            }
            else
            {
                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+groupName+"&background=90a8a8&color=fff&size=128").into( imageViewDisplay);
            }
        }
        permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};







        saveImageAmazonS3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitMapToFile(imageBitMap);
            }
        });

    }
    @Override
    public void onBackPressed() {

    }

    public void bitMapToFile(Bitmap imageBitMap)
    {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + accesName+".jpg");
        try {
            if(!file.exists())
            {
                file.createNewFile();
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitMap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            byte[] bitmapData = bytes.toByteArray();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bitmapData);
            fo.flush();
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        storeImage(file);
    }

    public void storeImage(final File file)
    {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                            getApplicationContext(),
                            "us-east-1:940f442e-e5a4-418c-a822-9f08506c8690", // Identity pool ID
                            Regions.US_EAST_1 // Region
                    );
                    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                    s3.setRegion(Region.getRegion(Regions.US_EAST_1));
                    if (!s3.doesBucketExist("zihronprojectmanagementappbucket")) {
                        s3.createBucket(new CreateBucketRequest("projectmanagementappbucket"));
                    }

                    s3.putObject(
                            "zihronprojectmanagementappbucket",
                            "profilepicfolder/"+accesName+".jpg",
                            file
                    );
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(activity)
                                    .setMessage("Profile Picture Changed")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent gAIntent = new Intent(GalleryActivity.this, HomeActivity.class);
                                            startActivity(gAIntent);
                                        }
                                    })
                                    .create().show();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(activity)
                                    .setMessage("Your Profile Picture cann't be changed right now, problem with  network. Please try again later")
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent eUIAIntent = new Intent(GalleryActivity.this, HomeActivity.class);
                                            startActivity(eUIAIntent);
                                        }
                                    })
                                    .create().show();
                        }
                    });
                }
            }
        });

        thread.start();

    }

    public void addImageFromGallery(View view) {

       if(!hasPermission(activity, permissions))
       {
           if(ActivityCompat.shouldShowRequestPermissionRationale(
                   activity,Manifest.permission.CAMERA)
                   || ActivityCompat.shouldShowRequestPermissionRationale(
                   activity,Manifest.permission.READ_EXTERNAL_STORAGE)
                   || ActivityCompat.shouldShowRequestPermissionRationale(
                   activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
               new AlertDialog.Builder(activity)
                       .setTitle("Permission needed")
                       .setMessage("This permission is needed")
                       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               ActivityCompat.requestPermissions(activity,permissions,
                                       MY_PERMISSIONS_REQUEST_CODE);



                           }
                       })
                       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       })
                       .create().show();
           }
           else
           {
               ActivityCompat.requestPermissions(activity,permissions,
                       MY_PERMISSIONS_REQUEST_CODE);
           }

       }
       else
       {
           saveImageAmazonS3ImageView.setEnabled(true);
           final Intent gAintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
           startActivityForResult(gAintent,SELECTED_PICTURE);
       }
    }

    public static boolean hasPermission(Context context,String... permissions)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null)
        {
            for(String permission:permissions)
            {
                if(ActivityCompat.checkSelfPermission(context,permission)!=PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {

            case SELECTED_PICTURE:

                if(resultCode==RESULT_OK)
                {
                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    imageBitMap = BitmapFactory.decodeFile(filePath);
                    imageViewDisplay.setImageBitmap(imageBitMap);
                }
                break;

        }
    }








}
