package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserNotificationsDisplayActivity extends AppCompatActivity {

    private ListView userNotificationsListView;
    private JSONArray allNotificationJSONArray;
    private Activity activity;
    private ProgressBarClass progressBarClass;
    private SharedPreferences sharedPreferences;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String userName;
    private HttpRequestClass httpRequestClass;
    private  ProjectDelegateNotiReview  projectDelegateNotiReview;
    private FriendRequestNotiReview friendRequestNotiReview;
    private CustomUserNotificationsListViewAdapter customUserNotificationsListViewAdapter;
    private ImageView navToReviewDetailsImageView;
    private  ImageView navToFullDetailsImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notifications_display);
        activity=this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);

        try {
            String result = getUserAllNotifications(userName);
            if(result!=null)
            {
                allNotificationJSONArray = new JSONArray(result);
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }
        userNotificationsListView = (ListView)findViewById(R.id.userNotificationsListVWId);
        customUserNotificationsListViewAdapter = new CustomUserNotificationsListViewAdapter(getApplicationContext(),allNotificationJSONArray,activity,userName);
        userNotificationsListView.setAdapter(customUserNotificationsListViewAdapter);





    }
    @Override
    public void onBackPressed() {

    }

    public void backToHomePage(View view)
    {
        Intent hAIntent = new Intent(UserNotificationsDisplayActivity.this, HomeActivity.class);
        startActivity(hAIntent);
}

    public void viewFriendRequestNoti(String notificationSubType)
    {

        if(notificationSubType.equalsIgnoreCase("Requested"))
        {

        }
    }



    public String getUserAllNotifications(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faun/",postDataParams, activity,"application/json", "application/json");
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




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}
