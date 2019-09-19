package com.zihron.projectmanagementapp;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;


import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
  import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.sendbird.android.SendBird;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ZihronProjectManagmentApplication extends Application  {

    private static String APP_ID="115D445B-D50D-4DC9-B559-C82B6AE4E503";
    private static ZihronProjectManagmentApplication instance;
  private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private static CognitoUser cognitoUser;
    private static CognitoCachingCredentialsProvider credentialsProvider;
    private boolean isRefreshTokenExpired;
    private CognitoUserPool userPool;
    private static  AmazonSimpleEmailService amazonSimpleEmailService;
    private Activity activity;
    private static String regionInString;
    private com.zihron.projectmanagementapp.Utility.HttpRequestClass httpRequestClass;
    @Override
    public void onCreate() {
        super.onCreate();

        ZihronProjectManagmentApplication.instance = this;

        SendBird.init(APP_ID, this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
               .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
               .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .init();
        OneSignal .setLogLevel(OneSignal.LOG_LEVEL.ERROR, OneSignal.LOG_LEVEL.ERROR);
        createProjectFolder();

    }

    public static ZihronProjectManagmentApplication get() {
        return instance;
    }


    public void createProjectFolder()
    {


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Zihron");
        if (!file.exists()) {
            file.mkdirs();
        }


    }



    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String notificationID = notification.payload.notificationID;
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;



            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                {

                }

            }
        }
    }


    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            Object activityToLaunch = HomeActivity.class;


            if (actionType == OSNotificationAction.ActionType.ActionTaken) {


                if (result.action.actionID.equals("id1")) {

                    activityToLaunch = HomeActivity.class;
                }

            }

            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        }
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


    public AmazonSimpleEmailService getAmazonSimpleEmailService()
    {

        AmazonSimpleEmailService amazonSimpleEmailService =new AmazonSimpleEmailServiceClient(new BasicAWSCredentials("AKIAJIDAFOZFABDUTHKQ","YM/oQ+BTl/vWNcUo3hJEFz0uta1CH4M9tFQ10qq1"));
        amazonSimpleEmailService.setRegion(Region.getRegion(Regions.US_EAST_1));
        return amazonSimpleEmailService;
    }

public void setRefreshTokenValid(Boolean isMRefreshTokenExpired)
{
    isRefreshTokenExpired= isMRefreshTokenExpired;
}


    public Boolean getIsRefreshTokenValid()
    {
        return isRefreshTokenExpired ;
    }


    public CognitoUser getCognitoUser()
    {
        return cognitoUser;
    }

    public void  setCognitoUser(CognitoUser user)
    {
         cognitoUser = user;
    }

public CognitoCachingCredentialsProvider getCredentialsProvider()
{
    return credentialsProvider;
}


    public CognitoUserPool  getCognitoUserPool()
    {
        return userPool;
    }


    public AmazonSimpleEmailService getAwsCrediantils()
    {
        return amazonSimpleEmailService;
    }

    public CognitoUserPool getUserPoolCredentials(Activity activity)
    {
        this.activity=activity;
       String  result = getUserPoolKeyDetails();
       JSONObject tempObject;
       String userPoolId =null;
       String clientId=null;
       String clientSecrete=null;
       String region = null;
       if(result!=null)
           {
               try {
                   tempObject = new JSONObject(result);
                   userPoolId = tempObject.getString("userpoolId");
                   clientId = tempObject.getString("clientId");
                   clientSecrete = tempObject.getString("clientSecret");
                   region = tempObject.getString("region");
                   regionInString = region;
               } catch (JSONException e) {
                   e.printStackTrace();
               }

//               com.amazonaws.regions.Region s3Region = com.amazonaws.regions.Region.getRegion(Regions.valueOf(region));
               userPool = new CognitoUserPool(activity, userPoolId, clientId, clientSecrete, Regions.US_EAST_1);
            }

return userPool;
    }

public void getCognitoCachingCredentialsProvider(Activity activity)
{
    String result = getCognitoCachingCredentials();
    JSONObject tempObject=null;
    String identityPoolId = null;
    String region=null;
    if(result!=null)
    {
        try {
            tempObject = new JSONObject(result);
            identityPoolId = tempObject.getString("credentialKey");
            region = tempObject.getString("region");
            regionInString = region;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        credentialsProvider = new CognitoCachingCredentialsProvider(
                activity.getApplicationContext(),
                identityPoolId, // Identity pool ID
                Regions.US_EAST_1// Region
        );
    }
}

public String getRegionNameInString()
{
    return regionInString;
}
     public String getUserPoolKeyDetails()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();

            httpRequestClass = new com.zihron.projectmanagementapp.Utility.HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fupds/",postDataParams, activity,"application/json","" );
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if(result!=null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            }
            else if(result==null)
            {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }



        return result;
    }
    public String getCognitoCachingCredentials()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();

        httpRequestClass = new com.zihron.projectmanagementapp.Utility.HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpcds/",postDataParams, activity,"application/json","" );
        result = httpRequestClass.getResult();
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        if(result!=null) {
            if (result.equalsIgnoreCase("exception")) {
                asyncErrorDialogDisplay.handleException(activity);
            } else if (result.equalsIgnoreCase("No network")) {
                Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
            } else if (android.text.TextUtils.isDigitsOnly(result)) {
                asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
            }
        }
        else if(result==null)
        {
            Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
        }



        return result;
    }





public void getBasicAwsCredentians(Activity activity)
{
    String result = getAWSClientCredentials();
    String accessKey=null;
    String secretKey = null;
    JSONObject tempObject;
    if(result!=null) {
        try {
            tempObject = new JSONObject(result);
            accessKey = tempObject.getString("accessKey");
            secretKey = tempObject.getString("secretKey");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        amazonSimpleEmailService = new AmazonSimpleEmailServiceClient(new BasicAWSCredentials(accessKey, secretKey));
        amazonSimpleEmailService.setRegion(Region.getRegion(Regions.US_EAST_1));
    }
}

    public String getAWSClientCredentials()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();

        httpRequestClass = new com.zihron.projectmanagementapp.Utility.HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuads/",postDataParams, activity,"application/json","" );
        result = httpRequestClass.getResult();
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        if(result!=null) {
            if (result.equalsIgnoreCase("exception")) {
                asyncErrorDialogDisplay.handleException(activity);
            } else if (result.equalsIgnoreCase("No network")) {
                Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
            } else if (android.text.TextUtils.isDigitsOnly(result)) {
                asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
            }
        }
        else if(result==null)
        {
            Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
        }



        return result;
    }

}
