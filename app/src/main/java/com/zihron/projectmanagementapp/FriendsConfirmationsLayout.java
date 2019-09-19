package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class FriendsConfirmationsLayout extends AppCompatActivity {
    private String userName;
    private Context context;
    private Activity activity;
    private HttpRequestClass httpRequestClass;
    private S3ImageClass s3ImageClass;
    private JSONArray approvedDeniedFriendsJSONArray;
    private JSONArray finalApprovedDeniedFriendsJSONArray;
    Typeface fontAwesomeIcon;
    LinearLayout  confirmedDeniedLinearLayout;
    private boolean checkForPermission = true;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private JSONArray selectNotiMeberJArray;
    private SearchView friendsSearchView;
    public FriendsConfirmationsLayout(String userName, JSONArray approvedDeniedFriendsJSONArray, LinearLayout  confirmedDeniedLinearLayout, Typeface fontAwesomeIcon, Context context, Activity activity, SearchView friendsSearchView)
    {
        this.userName =userName;
        this.fontAwesomeIcon=fontAwesomeIcon;
        this.activity = activity;
        this.context = context;
        this.s3ImageClass = new S3ImageClass();
        this.progressBarClass = new ProgressBarClass(activity);
        this.confirmedDeniedLinearLayout = confirmedDeniedLinearLayout;
        this.approvedDeniedFriendsJSONArray = approvedDeniedFriendsJSONArray;
        this.friendsSearchView = friendsSearchView;
        finalApprovedDeniedFriendsJSONArray = approvedDeniedFriendsJSONArray;
        displayLayout();
        friendsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
transerSearchViewResult(newText);
                displayLayout();
                return false;
            }
        });

    }

    public void displayLayout()
    { int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch(screenSize) {

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                friendSuggestnListVwNormalScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                friendSuggestnListVwXLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                friendSuggestnListVwSmallScreenSizeLT();
                break;
            default:

        }
    }

    public void friendSuggestnListVwLargeScrnSizeLT()
    {
        confirmedDeniedLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalApprovedDeniedFriendsJSONArray.length(); i++) {
                String tempResult = finalApprovedDeniedFriendsJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("requesterUsername");
                String lastName = tempObject.getString("lastName");
                String firstName = tempObject.getString("firstName");
                String requestStatus = tempObject.getString("requestStatus");
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                outerLinearLayoutLP.setMargins(0,0,0,30);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView profileImageVW = new ImageView(activity);
                profileImageVW.setBackground(activity.getDrawable(R.drawable.background4));
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(250, 250);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setBackgroundColor(activity.getResources().getColor(R.color.color22));
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
                        closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });
                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(recieverUserName, bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }

                LinearLayout innerLinearLayout = new LinearLayout(activity);

                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,45,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText(firstName+" "+lastName);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setLayoutParams(friendUserNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(recieverUserName);
                friendUsernameTextView.setSingleLine(true);
                friendUsernameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 25, 0, 0);



                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayoutLP.setMargins(20,50,0,0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);
                innerSelectionLinearLayout.setGravity(Gravity.RIGHT);

                LinearLayout.LayoutParams confirmFriendRequestTxtViewLayout = new LinearLayout.LayoutParams(230, 80);
                final TextView confirmFriendRequestTextView = new TextView(activity);
                confirmFriendRequestTextView.setLayoutParams(confirmFriendRequestTxtViewLayout);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Following");
                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                                selectNotiMeberJArray.put(friendUsernameTextView.getText().toString());
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                LinearLayout.LayoutParams removeFriendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView removeFriendNameTextView = new TextView(activity);
                removeFriendNameTextView.setLayoutParams(removeFriendNameTxtViewLayout);
                removeFriendNameTextView.setTextSize(18);
                removeFriendNameTextView.setClickable(true);
                removeFriendNameTxtViewLayout.setMargins(40,0,0,0);
                removeFriendNameTextView.setText(activity.getResources().getString(R.string.closebutton));
                removeFriendNameTextView.setTypeface(fontAwesomeIcon);
                removeFriendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                removeFriendNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String result = updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Not Following");
                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });

                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);
                innerSelectionLinearLayout.addView(removeFriendNameTextView);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                confirmedDeniedLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void friendSuggestnListVwNormalScrnSizeLT()
    {

        confirmedDeniedLinearLayout.removeAllViews();
        try {

            for (int i = 0; i < finalApprovedDeniedFriendsJSONArray.length(); i++) {
                String tempResult = finalApprovedDeniedFriendsJSONArray.getString(i);

                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("requesterUsername");
                String requestStatus = tempObject.getString("requestStatus");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                outerLinearLayoutLP.setMargins(0,0,0,30);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView profileImageVW = new ImageView(activity);
                profileImageVW.setBackground(activity.getDrawable(R.drawable.background4));
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(250, 250);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setBackgroundColor(activity.getResources().getColor(R.color.color22));
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
                        closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });
                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(recieverUserName, bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }

                LinearLayout innerLinearLayout = new LinearLayout(activity);

                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,45,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText(firstName+" "+lastName);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setLayoutParams(friendUserNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(recieverUserName);
                friendUsernameTextView.setSingleLine(true);
                friendUsernameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 25, 0, 0);



                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayoutLP.setMargins(20,50,0,0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);
                innerSelectionLinearLayout.setGravity(Gravity.RIGHT);

                LinearLayout.LayoutParams confirmFriendRequestTxtViewLayout = new LinearLayout.LayoutParams(230, 80);
                final TextView confirmFriendRequestTextView = new TextView(activity);
                confirmFriendRequestTextView.setLayoutParams(confirmFriendRequestTxtViewLayout);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                           String result =  updateUserFriends(userName,friendUsernameTextView.getText().toString(), "Following");
                            if(result.equalsIgnoreCase("Record Updated")) {
                                sendNotificationsToUsers("Accepted");
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                LinearLayout.LayoutParams removeFriendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView removeFriendNameTextView = new TextView(activity);
                removeFriendNameTextView.setLayoutParams(removeFriendNameTxtViewLayout);
                removeFriendNameTextView.setTextSize(18);
                removeFriendNameTextView.setClickable(true);
                removeFriendNameTxtViewLayout.setMargins(40,0,0,0);
                removeFriendNameTextView.setText(activity.getResources().getString(R.string.closebutton));
                removeFriendNameTextView.setTypeface(fontAwesomeIcon);
                removeFriendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                removeFriendNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Following");

                            if(result.equalsIgnoreCase("Record Updated")) {
                                sendNotificationsToUsers("Denied");
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });

                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);
                innerSelectionLinearLayout.addView(removeFriendNameTextView);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);
                confirmedDeniedLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }



    public void friendSuggestnListVwXLargeScrnSizeLT()
    {

        confirmedDeniedLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalApprovedDeniedFriendsJSONArray.length(); i++) {
                String tempResult =finalApprovedDeniedFriendsJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                String requestStatus = tempObject.getString("requestStatus");
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                outerLinearLayoutLP.setMargins(0,0,0,30);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView profileImageVW = new ImageView(activity);
                profileImageVW.setBackground(activity.getDrawable(R.drawable.background4));
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(250, 250);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setBackgroundColor(activity.getResources().getColor(R.color.color22));
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
                        closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });
                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(recieverUserName, bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }

                LinearLayout innerLinearLayout = new LinearLayout(activity);

                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,45,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText(firstName+" "+lastName);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setLayoutParams(friendUserNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(recieverUserName);
                friendUsernameTextView.setSingleLine(true);
                friendUsernameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 25, 0, 0);



                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayoutLP.setMargins(20,50,0,0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);
                innerSelectionLinearLayout.setGravity(Gravity.RIGHT);

                LinearLayout.LayoutParams confirmFriendRequestTxtViewLayout = new LinearLayout.LayoutParams(230, 80);
                final TextView confirmFriendRequestTextView = new TextView(activity);
                confirmFriendRequestTextView.setLayoutParams(confirmFriendRequestTxtViewLayout);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Following");

                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                LinearLayout.LayoutParams removeFriendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView removeFriendNameTextView = new TextView(activity);
                removeFriendNameTextView.setLayoutParams(removeFriendNameTxtViewLayout);
                removeFriendNameTextView.setTextSize(18);
                removeFriendNameTextView.setClickable(true);
                removeFriendNameTxtViewLayout.setMargins(40,0,0,0);
                removeFriendNameTextView.setText(activity.getResources().getString(R.string.closebutton));
                removeFriendNameTextView.setTypeface(fontAwesomeIcon);
                removeFriendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                removeFriendNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Following");

                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Now wating for friends approval from your friend", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });

                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);
                innerSelectionLinearLayout.addView(removeFriendNameTextView);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                confirmedDeniedLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }



    public void friendSuggestnListVwSmallScreenSizeLT()
    {
        confirmedDeniedLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalApprovedDeniedFriendsJSONArray.length(); i++) {
                String tempResult = finalApprovedDeniedFriendsJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                String requestStatus = tempObject.getString("requestStatus");
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
                outerLinearLayoutLP.setMargins(0,0,0,30);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setBackgroundColor( activity.getResources().getColor(R.color.color41));
                outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                ImageView profileImageVW = new ImageView(activity);
                profileImageVW.setBackground(activity.getDrawable(R.drawable.background4));
                LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(250, 250);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                profileImageVW.setBackgroundColor(activity.getResources().getColor(R.color.color22));
                profileImageVW.setClickable(true);
                profileImageVW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        dialog.setContentView(R.layout.friendprofilereviewlayout);
                        ImageView closeFrndReviewDialog = (ImageView)dialog.findViewById(R.id.closeFrndReviewDialogId);
                        closeFrndReviewDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();


                    }
                });
                profileImageVW.setId(R.id.memberLogoId);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(recieverUserName, bitMap);
                            }
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+firstName + " " + lastName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }
                }

                LinearLayout innerLinearLayout = new LinearLayout(activity);

                LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerLinearLayoutParams.setMargins(40,45,0,0);
                innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
                innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendNameTextView = new TextView(activity);
                friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendNameTextView.setTextSize(15);
                friendNameTextView.setText(firstName+" "+lastName);
                friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));

                LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(400, RelativeLayout.LayoutParams.WRAP_CONTENT);
                final TextView friendUsernameTextView = new TextView(activity);
                friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
                friendUsernameTextView.setLayoutParams(friendUserNameTxtViewLayout);
                friendUsernameTextView.setTextSize(15);
                friendUsernameTextView.setText(recieverUserName);
                friendUsernameTextView.setSingleLine(true);
                friendUsernameTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                friendUserNameTxtViewLayout.setMargins(0, 25, 0, 0);



                LinearLayout innerSelectionLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams innerSelectionLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerSelectionLinearLayoutLP.setMargins(20,50,0,0);
                innerSelectionLinearLayout.setLayoutParams(innerSelectionLinearLayoutLP);
                innerSelectionLinearLayout.setGravity(Gravity.RIGHT);

                LinearLayout.LayoutParams confirmFriendRequestTxtViewLayout = new LinearLayout.LayoutParams(230, 80);
                final TextView confirmFriendRequestTextView = new TextView(activity);
                confirmFriendRequestTextView.setLayoutParams(confirmFriendRequestTxtViewLayout);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Following");

                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Friend request accepted", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                                sendNotificationsToUsers("Accepted");
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                LinearLayout.LayoutParams removeFriendNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView removeFriendNameTextView = new TextView(activity);
                removeFriendNameTextView.setLayoutParams(removeFriendNameTxtViewLayout);
                removeFriendNameTextView.setTextSize(18);
                removeFriendNameTextView.setClickable(true);
                removeFriendNameTxtViewLayout.setMargins(40,0,0,0);
                removeFriendNameTextView.setText(activity.getResources().getString(R.string.closebutton));
                removeFriendNameTextView.setTypeface(fontAwesomeIcon);
                removeFriendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                removeFriendNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       String result =  updateUserFriends(friendUsernameTextView.getText().toString(),userName, "Not Following");
                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalApprovedDeniedFriendsJSONArray.remove(index);
                                Toast.makeText(context,"Friend request denied", Toast.LENGTH_LONG).show();
                                friendSuggestnListVwNormalScrnSizeLT();
                                confirmedDeniedLinearLayout.invalidate();
                                sendNotificationsToUsers("Denied");
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });

                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);
                innerSelectionLinearLayout.addView(removeFriendNameTextView);


                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                confirmedDeniedLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }



    private int findPositioninJSONArray(String recieveName)
    {
        int index=-1;

        for(int i=0; i<finalApprovedDeniedFriendsJSONArray.length(); i++)
        {

            try {

                JSONObject tempObject = new JSONObject(finalApprovedDeniedFriendsJSONArray.getString(i));
                if(  (tempObject.getString("recieverUsername").equalsIgnoreCase(recieveName))) {
                    index = i;

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return index;
    }


    public JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public String updateUserFriends(String recieverUserName, String requestUserName,String requestStatus)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {

            postDataParams.put("requesterUsername", requestUserName);
            postDataParams.put("recieverUsername", recieverUserName);
            postDataParams.put("updateStatus", requestStatus);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/ufrus/",postDataParams, activity,"text/plain", "application/json");
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





public void transerSearchViewResult(String searchQuery)
{
    finalApprovedDeniedFriendsJSONArray = new JSONArray();
   if(searchQuery!=null && searchQuery!="") {
       for (int i = 0; i < approvedDeniedFriendsJSONArray.length(); i++) {

           try {
               String tempResult = approvedDeniedFriendsJSONArray.getString(i);
               JSONObject tempObject = null;
               String recieverUserName = null;
               tempObject = new JSONObject(tempResult);
               recieverUserName = tempObject.getString("requesterUsername");
               if (recieverUserName.startsWith(searchQuery.toLowerCase()) || recieverUserName.startsWith(searchQuery.toUpperCase())) {
                   finalApprovedDeniedFriendsJSONArray.put(tempResult);
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }


       }
   }
   else
   {
       finalApprovedDeniedFriendsJSONArray = approvedDeniedFriendsJSONArray;
   }
}

    public void sendNotificationsToUsers(String requestStatus)
    {
        String result =null;
       try {
            result =  createNotifications(userName, selectNotiMeberJArray.toString(),  "01",requestStatus);
            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < selectNotiMeberJArray.length(); i++) {
                   result = getOneSignalIdAttachedUserName(selectNotiMeberJArray.getString(i));
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+userName+" has"+requestStatus.toLowerCase()+"accepted your friends request '}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getOneSignalIdAttachedUserName(String memberUserName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", memberUserName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fosau/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String createNotifications(String notiSenderName, String allNotiRecieverNamesList,  String entityId,String entityType)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String  notiCreatedDate=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("userNameOfSender",  notiSenderName);
            postDataParams.put("allRecieversList", allNotiRecieverNamesList);
            postDataParams.put("dateCreated", notiCreatedDate);
            postDataParams.put("entityId", entityId);
            postDataParams.put("entityType", entityType);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iun/",postDataParams, activity,"application/json", "application/json");
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }




}
