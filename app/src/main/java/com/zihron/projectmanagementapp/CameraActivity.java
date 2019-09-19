package com.zihron.projectmanagementapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v8.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import io.github.silvaren.easyrs.tools.Nv21Image;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.google.android.cameraview.CameraView;
import com.google.android.cameraview.CameraViewImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CameraActivity extends AppCompatActivity {

    private Activity activity;
    private CameraView cameraView;
    private static final int  MY_PERMISSIONS_REQUEST_CODE = 100;
    private static String[]permissions;
    private TextView flashCameraTextView;
    private TextView switchCameraTextView;
    private RenderScript rs;
    View shutterEffect;
    private int rotationDegrees;
    private SharedPreferences sharedPreferences;
    private String userName;
    private ImageView backToUserInformatnPageImageView;
    private boolean frameIsProcessing = false;
    private static final int PERMISSION_CODE_CAMERA = 3002;
  //  private static final int TAKE_PICTURE_REQUEST_B = 100;
    private Typeface fontAwesomeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        activity = this;
        shutterEffect = findViewById(R.id.shutter_effect);
        cameraView = (CameraView)findViewById(R.id.camera_view);
        flashCameraTextView = (TextView)findViewById(R.id.flashCameraTxtVwId);
        switchCameraTextView = (TextView)findViewById(R.id.switchCameraTxtVwId);
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");
        switchCameraTextView.setTypeface(fontAwesomeIcon);
        flashCameraTextView.setTypeface(fontAwesomeIcon);
        backToUserInformatnPageImageView = (ImageView)findViewById(R.id.backToUserInformatnPageImgVWId);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        switchCameraTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.switchCamera();
            }
        });

        backToUserInformatnPageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eUIAIntent = new Intent(CameraActivity.this, HomeActivity.class);
                startActivity(eUIAIntent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

            cameraView.start();
            setupCameraCallbacks();

    }


    private void setupCameraCallbacks() {
        cameraView.setOnPictureTakenListener(new CameraViewImpl.OnPictureTakenListener() {
                                                 @Override
                                                 public void onPictureTaken(final Bitmap bitmap, int rotationDegrees) {
                                                     //startSavingPhoto(bitmap, rotationDegrees);

                                                     Matrix matrix = new Matrix();
                                                     matrix.postRotate(-rotationDegrees);
                                                    final Bitmap bitmaps = Bitmap.createBitmap(bitmap, 0, 0,  bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                                                     runOnUiThread(new Runnable() {
                                                         public void run() {
                                                             previewImage(bitmaps);
                                                         }
                                                     });

                                                 }

    });


        cameraView.setOnFocusLockedListener(new CameraViewImpl.OnFocusLockedListener() {
            @Override
            public void onFocusLocked() {
             // playShutterAnimation();
            }
        });
        cameraView.setOnTurnCameraFailListener(new CameraViewImpl.OnTurnCameraFailListener() {
            @Override
            public void onTurnCameraFail(Exception e) {
                Toast.makeText(CameraActivity.this, "Switch Camera Failed. Does you device has a front camera?",
                        Toast.LENGTH_SHORT).show();
            }
        });
        cameraView.setOnCameraErrorListener(new CameraViewImpl.OnCameraErrorListener() {
            @Override
            public void onCameraError(Exception e) {
                Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

       cameraView.setOnFrameListener(new CameraViewImpl.OnFrameListener() {
            @Override
            public void onFrame(final byte[] data, final int width, final int height,final int rotationDegrees) {
                if (frameIsProcessing) return;
                frameIsProcessing = true;
                Observable.fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
                        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(180);
                        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Bitmap frameBitmap) {
                                if (frameBitmap != null) {
                                    Log.i("onFrame", frameBitmap.getWidth() + ", " + frameBitmap.getHeight());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                frameIsProcessing = false;
                            }
                        });
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void previewImage(final Bitmap bitmap)
    {
        final Dialog cameraPreviewDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        //  cameraPreviewDialog.requestWindowFeature(Window.);
        cameraPreviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        cameraPreviewDialog.setContentView(R.layout.camerapicturereviewlayout);
        TextView cancelTxtView = (TextView) cameraPreviewDialog.findViewById(R.id.cancelTxtViewId);
        TextView acceptTxtView = (TextView) cameraPreviewDialog.findViewById(R.id.acceptTxtViewId);
        ImageView imagePreviewImageView = (ImageView) cameraPreviewDialog.findViewById(R.id.imagePreviewImageVWId);
        cancelTxtView.setTypeface(fontAwesomeIcon);
        acceptTxtView.setTypeface(fontAwesomeIcon);
        imagePreviewImageView.setImageBitmap(bitmap);
        cameraPreviewDialog.show();


        cancelTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreviewDialog.dismiss();
            }
        });

        acceptTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreviewDialog.dismiss();
                bitMapToFile(bitmap);

            }
        });

    }

public void bitMapToFile(final Bitmap bitmap)
{
    File file = new File(Environment.getExternalStorageDirectory() + File.separator + userName+".jpg");
    try {
        if(!file.exists())
        {
            file.createNewFile();
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
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
                         "profilepicfolder/"+userName+".jpg",
                         file
                 );
                 runOnUiThread(new Runnable() {
                     public void run() {
                         new AlertDialog.Builder(activity)
                                 .setMessage("Profile picture changed")
                                 .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         dialog.dismiss();
                                         Intent eUIAIntent = new Intent(CameraActivity.this, HomeActivity.class);
                                         startActivity(eUIAIntent);
                                     }
                                 })
                                 .create().show();
                     }
                 });



             } catch (Exception e) {
                 e.printStackTrace();
                 runOnUiThread(new Runnable() {
                     public void run() {
                         new AlertDialog.Builder(activity)
                                 .setMessage("Your Profile Picture cann't be changed right now, problem with  network. Please try again later")
                                 .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         dialog.dismiss();
                                         Intent eUIAIntent = new Intent(CameraActivity.this, HomeActivity.class);
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
    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }





    private void playShutterAnimation() {
        shutterEffect.setVisibility(View.VISIBLE);
      /*  shutterEffect.animate().alpha(0f).setDuration(300).setListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        shutterEffect.setVisibility(View.GONE);
                        shutterEffect.setAlpha(0.8f);
                    }
                });
                */
    }



    public void takePicture(View view)
    {

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

            cameraView.takePicture();
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


}
