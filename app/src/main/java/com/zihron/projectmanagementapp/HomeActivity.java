package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

import ZihronChatApp.ConnectionManager;


public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TextView appbarTextView;
    private TextView notificationIconTextView;
    private String userName;
    private  Bundle bundle;
    Typeface fontAwesomeIcon;
    private SharedPreferences sharedPreferences;
    private String apiId;
    private String result =null;
    private Activity activity;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
private int totalNotificationCount;
    private TextView notificationTextView;
    private TextView notificationTextViewLogo;
    private String userOneSignalId;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        notificationTextView = (TextView)findViewById(R.id.notificationTextVWId);
notificationTextViewLogo = (TextView)findViewById(R.id.notiTxtVWLogoId);
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();

        status.getSubscriptionStatus().getSubscribed();
        status.getSubscriptionStatus().getUserSubscriptionSetting();
        userOneSignalId=  status.getSubscriptionStatus().getUserId();
        userName = sharedPreferences.getString("userName", null);
       connectToSendBird("zihronprojectmanagementapp@gmail.com");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("oneSignalId", userOneSignalId);
        editor.commit();
                result = insertUserOneSignal(userName, userOneSignalId);
                if (result.equalsIgnoreCase("Record inserted"))

                {



                        result =  getUserNotificationCount(userName);
                    totalNotificationCount = Integer.parseInt(result);
                        notificationTextView.setText(result);
                    fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(), "font/fontawesome-webfont.ttf");
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    appbarTextView = (TextView) findViewById(R.id.appbarTextView);
                    notificationTextViewLogo.setTypeface(fontAwesomeIcon);
                    setSupportActionBar(toolbar);
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    tabLayout = (TabLayout) findViewById(R.id.tabs);
                 /*   int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                    switch(screenSize) {
                        case Configuration.SCREENLAYOUT_SIZE_LARGE:
                            generateTabItems();;
                            break;
                        case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                            break;
                        default:

                    }
                    */

                   mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
                        @Override
                        public void onPageSelected(int position) {
                            if (ProjectTaskAssignedActivity.popUp != null) {
                                ProjectTaskAssignedActivity.popUp.dismiss();
                            }

                            if (CalendarActivity.popUp != null) {
                                CalendarActivity.popUp.dismiss();
                            }

                            if (position == 0) {
                                appbarTextView.setText("Project");
                            }
                            if (position == 1) {


                                appbarTextView.setText("Appraisal");
                            }
                            if (position == 2) {

                                appbarTextView.setText("Chats");
                            }
                            if (position == 3) {

                                appbarTextView.setText("Calendar");
                            }
                            if (position == 4) {

                                appbarTextView.setText("Profile");
                            }
                        }
                    });
                    tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
                }

            else
            {
                Toast.makeText(activity,"There is an issue registering this device to be able to recieve notification. You will logged out. Please login again",Toast.LENGTH_LONG).show();
            }



    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    return new ProjectTaskAssignedActivity();
                case 1:
                    return new AppraisalActivity();
                case 2:
                    return new ChatActivity();
                case 3:
                    return new CalendarActivity();
                case 4:
                    return new UserProfileInformationActivity();
                default:
                    return null; // Problem occurs at this condition!
            }
        }

        @Override
        public int getCount() {

            return 5;
        }
    }

public void generateTabItems()
{
    final ImageView tabItemImageVW1 = new ImageView(activity);
    LinearLayout.LayoutParams lLPTabItemImageVWLayout1 = new LinearLayout.LayoutParams(45, 45);
    tabItemImageVW1.setLayoutParams(lLPTabItemImageVWLayout1);
    tabItemImageVW1.setBackground(activity.getResources().getDrawable(R.drawable.project,null));
   tabLayout.getTabAt(0).setCustomView(tabItemImageVW1);

    final ImageView tabItemImageVW2 = new ImageView(activity);
    LinearLayout.LayoutParams lLPTabItemImageVWLayout2 = new LinearLayout.LayoutParams(45, 45);
    tabItemImageVW2.setLayoutParams(lLPTabItemImageVWLayout2);
    tabItemImageVW2.setBackground(activity.getResources().getDrawable(R.drawable.appraisal,null));
    tabLayout.getTabAt(1).setCustomView(tabItemImageVW2);


    final ImageView tabItemImageVW3 = new ImageView(activity);
    LinearLayout.LayoutParams lLPTabItemImageVWLayout3 = new LinearLayout.LayoutParams(45, 45);
    tabItemImageVW3.setLayoutParams(lLPTabItemImageVWLayout3);
    tabItemImageVW3.setBackground(activity.getResources().getDrawable(R.drawable.chat,null));
    tabLayout.getTabAt(2).setCustomView(tabItemImageVW3);


    final ImageView tabItemImageVW4 = new ImageView(activity);
    LinearLayout.LayoutParams lLPTabItemImageVWLayout4 = new LinearLayout.LayoutParams(45, 45);
    tabItemImageVW4.setLayoutParams(lLPTabItemImageVWLayout4);
    tabItemImageVW4.setBackground(activity.getResources().getDrawable(R.drawable.calendar,null));
    tabLayout.getTabAt(3).setCustomView(tabItemImageVW4);

    final ImageView tabItemImageVW5 = new ImageView(activity);
    LinearLayout.LayoutParams lLPTabItemImageVWLayout5 = new LinearLayout.LayoutParams(45, 45);
    tabItemImageVW5.setLayoutParams(lLPTabItemImageVWLayout5);
    tabItemImageVW5.setBackground(activity.getResources().getDrawable(R.drawable.user,null));
    tabLayout.getTabAt(4).setCustomView(tabItemImageVW5);

}

    public void userAllNotifcationsActivityView(View view)
    {

              Intent PTAIntent = new Intent(getApplication(), UserNotificationsDisplayActivity.class);
             HomeActivity.this.startActivity(PTAIntent);

    }
    @Override
    public void onBackPressed() {

    }
    public String insertUserOneSignal(String userName,String oneSignUpId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            postDataParams.put("idOfOneSignal", oneSignUpId);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iod/",postDataParams, activity,"text/plain", "application/json");
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
                Toast.makeText(activity, "Error with connection, Please re-launch this page", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getUserNotificationCount(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/func/",postDataParams, activity,"text/plain", "application/json");
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
                Toast.makeText(activity, "Error with connection, Please re-launch this page", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void connectToSendBird(final String userId) {
        // Show the loading indicator


        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            HomeActivity.this, "" + e.getCode() + ": " + e.getMessage()+"----",
                            Toast.LENGTH_SHORT)
                            .show();
                    // Show login failure snackbar

                    return;
                }


            }
        });
    }

}

