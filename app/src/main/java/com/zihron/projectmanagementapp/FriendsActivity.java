package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

public class FriendsActivity extends AppCompatActivity {

    private TextView  confirmdenyFriendTextView;
    private TextView  allUserFriendsTextView;
    private TextView  suggestionFriendsTextView;
    private SharedPreferences sharedPreferences;
    private String userName;
    Typeface fontAwesomeIcon;
    private HttpRequestClass httpRequestClass;
    private LinearLayout allUserFriendsLinearLayout;
    private LinearLayout confirmedDeniedLinearLayout;
    private LinearLayout  friendsSuggestionsLinearLayout;
    private JSONArray friendListJSONArray;
    private JSONArray allFriendsListJSONArray;
    private JSONArray approvedDeniedFriendsJSONArray;
    private Activity activity;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ProgressBarClass progressBarClass;
    private String isNotificationLoad;
    private SearchView friendsSearchView;
    private TextView freindsStatusappbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        fontAwesomeIcon = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        confirmdenyFriendTextView = (TextView)findViewById(R.id.confirmationFriendTxtVWId);
        suggestionFriendsTextView = (TextView)findViewById(R.id.suggestionFriendsTxtVWId);
        allUserFriendsTextView = (TextView)findViewById(R.id.approvedFriendTxtVWId);
        allUserFriendsLinearLayout = (LinearLayout)findViewById(R.id.friendsApprvdLinearLayoutId);
        confirmedDeniedLinearLayout = (LinearLayout)findViewById(R.id.friendsConfirmatnLinearLayoutId);
        friendsSuggestionsLinearLayout = (LinearLayout)findViewById(R.id.friendsSuggestLinearLayoutId);
        freindsStatusappbarTextView = (TextView)findViewById(R.id.freindsStatusappbarTxtVWId);
        friendsSearchView = (SearchView) findViewById(R.id.friendsSearchViewId);
        isNotificationLoad= getIntent().getStringExtra("notificationLoad");
        if(isNotificationLoad.equalsIgnoreCase("yes"))
        {
            allUserFriendsLinearLayout.removeAllViews();
            confirmedDeniedLinearLayout.removeAllViews();
            friendsSuggestionsLinearLayout.removeAllViews();
            allUserFriendsLinearLayout.setVisibility(View.GONE);
            confirmedDeniedLinearLayout.setVisibility(View.VISIBLE);
            friendsSuggestionsLinearLayout.setVisibility(View.GONE);
            confirmDeniedFriendsListView();
            freindsStatusappbarTextView.setText("Freinds Requested");
        }
        else
        {
            allUserFriendsLinearLayout.removeAllViews();
            confirmedDeniedLinearLayout.removeAllViews();
            friendsSuggestionsLinearLayout.removeAllViews();
            allUserFriendsLinearLayout.setVisibility(View.VISIBLE);
            confirmedDeniedLinearLayout.setVisibility(View.GONE);
            friendsSuggestionsLinearLayout.setVisibility(View.GONE);
            allUserFriendsListView();
            freindsStatusappbarTextView.setText("Freinds Approved");
        }



        confirmdenyFriendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allUserFriendsLinearLayout.removeAllViews();
                confirmedDeniedLinearLayout.removeAllViews();
                friendsSuggestionsLinearLayout.removeAllViews();
                allUserFriendsLinearLayout.setVisibility(View.GONE);
                confirmedDeniedLinearLayout.setVisibility(View.VISIBLE);
                friendsSuggestionsLinearLayout.setVisibility(View.GONE);
                confirmDeniedFriendsListView();
                freindsStatusappbarTextView.setText("Freinds Requested");
            }
        });

        allUserFriendsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allUserFriendsLinearLayout.removeAllViews();
                confirmedDeniedLinearLayout.removeAllViews();
                friendsSuggestionsLinearLayout.removeAllViews();
                allUserFriendsLinearLayout.setVisibility(View.VISIBLE);
                confirmedDeniedLinearLayout.setVisibility(View.GONE);
                friendsSuggestionsLinearLayout.setVisibility(View.GONE);
                allUserFriendsListView();
                freindsStatusappbarTextView.setText("Freinds Approved");


            }
        });

        suggestionFriendsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allUserFriendsLinearLayout.removeAllViews();
                confirmedDeniedLinearLayout.removeAllViews();
                friendsSuggestionsLinearLayout.removeAllViews();
                allUserFriendsLinearLayout.setVisibility(View.GONE);
                confirmedDeniedLinearLayout.setVisibility(View.GONE);
                friendsSuggestionsLinearLayout.setVisibility(View.VISIBLE);
                friendSuggestionListView();
                freindsStatusappbarTextView.setText("Freinds Suggested");
            }
        });

    }

    public void friendSuggestionListView() {


        String result = null;
        try {
            result = getUserGeneralResult(userName, "suggested");

            if(result!=null) {

                friendListJSONArray = new JSONArray(result);
                new FriendsSuggestionsLayout(userName,friendListJSONArray,friendsSuggestionsLinearLayout,fontAwesomeIcon,getApplicationContext(),activity,friendsSearchView);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }






        }

    public void backToUserProfile(View view)
    {
        Intent hAIntent = new Intent(FriendsActivity.this, HomeActivity.class);
        startActivity(hAIntent);
    }



    public void allUserFriendsListView() {


        String result = null;
        try {
            result = getUserGeneralResult(userName, "approved");
            if(result!=null) {

                allFriendsListJSONArray = new JSONArray(result);
                new AllUserFriendsLayout(userName,allFriendsListJSONArray,allUserFriendsLinearLayout,fontAwesomeIcon,getApplicationContext(),activity,friendsSearchView);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }






    }
    @Override
    public void onBackPressed() {

    }

    public void confirmDeniedFriendsListView() {

        FriendsConfirmationsLayout friendsConfirmationsLayout = null;

        String result = null;
        try {
            result = getUserGeneralResult(userName, "confirmation");

            if(result!=null) {

                approvedDeniedFriendsJSONArray = new JSONArray(result);

                 new FriendsConfirmationsLayout(userName,approvedDeniedFriendsJSONArray,confirmedDeniedLinearLayout,fontAwesomeIcon,getApplicationContext(),activity,friendsSearchView);

            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }






    }




    public String getUserGeneralResult(String userName,String selectionType)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            if(selectionType.equalsIgnoreCase("approved")) {
               httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuaf/",postDataParams, activity,"application/json", "application/json");
            }
            else if (selectionType.equalsIgnoreCase("confirmation"))
            {
                 httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuancfr/",postDataParams, activity,"application/json", "application/json");

            }
            else if (selectionType.equalsIgnoreCase("suggested"))
            {
                httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuafs/",postDataParams, activity,"application/json", "application/json");

            }
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
