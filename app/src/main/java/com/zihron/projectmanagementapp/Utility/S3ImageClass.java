package com.zihron.projectmanagementapp.Utility;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.ZihronProjectManagmentApplication;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

public class S3ImageClass extends AppCompatActivity {
    GetImageFromS3 getImageFromS3Task;
    private AmazonS3 s3;
    private String userName;
    private S3Object s3object;
 private IsImageExsts isImageExstsTasks;
    private String folderName;
    private static Activity activity;
    private ProgressBarClass progressBarClass;
    private static String[]permissions;
    private static boolean permission;
    private static final int  MY_PERMISSIONS_REQUEST_CODE = 100;
    public S3ImageClass()
    {

    }
    public S3ImageClass(Activity activity)
    {
this.activity=activity;
    }

public S3ImageClass  (Activity activity, String userName, String folderName)
{
    this.userName = userName;
    this.folderName = folderName;
    this.activity = activity;
    permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    progressBarClass = new ProgressBarClass(activity);


    CognitoCachingCredentialsProvider credentialsProvider = ZihronProjectManagmentApplication.get().getCredentialsProvider();


   s3 = new AmazonS3Client(credentialsProvider);
    s3.setRegion(Region.getRegion(Regions.US_EAST_1));
   s3 = new AmazonS3Client(credentialsProvider);
     getImageFromS3Task = new GetImageFromS3();
}

public boolean isObjectExists()
{
    boolean isExists = false;
    isImageExstsTasks = new IsImageExsts( "zihronprojectmanagementappbucket",folderName+"/"+userName+".jpg");
    try {
        isExists = isImageExstsTasks.execute().get();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
    return  isExists;


}

public Bitmap getImageBitMap()
{
    getImageFromS3Task = new GetImageFromS3();
    Bitmap bitmap = null;
    try {
        bitmap = getImageFromS3Task.execute().get();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
    return bitmap;
}
    private  class GetImageFromS3 extends AsyncTask<String,Void,Bitmap>
    {
        Bitmap bitmap;
        public GetImageFromS3() {

        }
        @Override
        protected void onPreExecute() {
            // Show progress dialog

          progressBarClass.showDialog();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //Populate Ui
            progressBarClass.dismissDialog();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {

                s3object = s3.getObject("zihronprojectmanagementappbucket", folderName+"/"+userName+".jpg");

                S3ObjectInputStream wrappedStream = s3object.getObjectContent();
                bitmap = BitmapFactory.decodeStream(wrappedStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // Show progress update
            super.onProgressUpdate(values);

        }


    }

   /* private boolean checkIfInPhoneMemory()
    {
       // File file = new File(Environment.DIRECTORY_PICTURES/+"");
      //  Log.i(TAG,"temp exists : " + file.exists());
    }

*/
    private  class IsImageExsts extends AsyncTask<String,Void,Boolean>
    {
       private String folder_objectName;
        private String bucketName;
        public IsImageExsts( String bucketName,String folder_objectName) {
            this.folder_objectName = folder_objectName;
            this.bucketName = bucketName;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            boolean isExists = false;
            try {

                isExists =  s3.doesObjectExist(bucketName, folder_objectName);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return isExists;
        }




    }

    public boolean confirmIfImageInPhone(String userName) {
        boolean isExists = false;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Zihron";
        File newFile = new File(path + File.separator + userName+ ".jpg");
        if (newFile.exists()) {
            isExists = true;
        }
return isExists;
    }



    public void writeToPhone(String userName, Bitmap bitMap)
    {

            OutputStream outStream = null;
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Zihron";
            File newFile = new File(path + File.separator + userName + ".jpg");
            try {
                outStream = new FileOutputStream(newFile);
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

    }


    public Bitmap readFromPhone(String userName)
    {
        Bitmap bitMap =null;


            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "Zihron";
            File newFile = new File(path + File.separator + userName + ".jpg");

            try {
                bitMap = BitmapFactory.decodeStream(new FileInputStream(newFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        return bitMap;
    }

    public static boolean getPermission()
    {
      permission=false;
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, Manifest.permission.CAMERA)
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
                                permission = true;


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
                permission=true;
            }

  return permission;
    }

      public static boolean hasPermission()
    {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && activity!=null && permissions!=null)
        {
            for(String permission:permissions)
            {
                if(ActivityCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
