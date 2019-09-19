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

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class AllUserFriendsLayout extends AppCompatActivity {
    private String userName;
    private Context context;
    private HttpRequestClass httpRequestClass;
    private Activity activity;
    private S3ImageClass s3ImageClass;
    private JSONArray allFriendsListJSONArray;
    private JSONArray finalAllFriendsListJSONArray;
    Typeface fontAwesomeIcon;
    private String requestStatus;
    LinearLayout  userAllFriendsLinearLayout;
    private SearchView friendsSearchView;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private boolean  checkForPermission = false;
    public AllUserFriendsLayout(String userName, JSONArray allFriendsListJSONArray, LinearLayout  userAllFriendsLinearLayout, Typeface fontAwesomeIcon, Context context, Activity activity, SearchView friendsSearchView )
    {
        this.userName =userName;
        this.fontAwesomeIcon=fontAwesomeIcon;
        this.activity = activity;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.context = context;

this.s3ImageClass = new S3ImageClass();
        this.userAllFriendsLinearLayout = userAllFriendsLinearLayout;

        this.allFriendsListJSONArray = allFriendsListJSONArray;
        finalAllFriendsListJSONArray= allFriendsListJSONArray;
        displayLayout();
        this.friendsSearchView = friendsSearchView;

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
    {  int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                allFiendsListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                allFiendsListVwNormalScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                allFiendsListVwXLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                allFiendsListVwSmallScreenSizeLT();
                break;
            default:

        }
    }

    @Override
    public void onBackPressed() {

    }

    public void allFiendsListVwLargeScrnSizeLT()
    {
        userAllFriendsLinearLayout.removeAllViews();
        try {


            for (int i = 0; i <finalAllFriendsListJSONArray.length(); i++) {
                String tempResult = finalAllFriendsListJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                String requestStatus =  tempObject.getString("requestStatus");
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
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTypeface(fontAwesomeIcon);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String requestStatus = confirmFriendRequestTextView.getText().toString();
                            String  result = null;
                            if(requestStatus.equalsIgnoreCase("Following"))
                            {
                               result = updateUserFriends(userName,friendUsernameTextView.getText().toString(), "Not Following");


                            }


                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalAllFriendsListJSONArray.remove(index);
                                Toast.makeText(context,"You have unfollowed "+friendUsernameTextView.getText().toString(), Toast.LENGTH_LONG).show();
                                allFiendsListVwLargeScrnSizeLT();
                                userAllFriendsLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });



                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);



                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                userAllFriendsLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void allFiendsListVwNormalScrnSizeLT()
    {
        userAllFriendsLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalAllFriendsListJSONArray.length(); i++) {


                String tempResult = finalAllFriendsListJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                 requestStatus =  tempObject.getString("requestStatus");
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
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTypeface(fontAwesomeIcon);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String requestStatus = confirmFriendRequestTextView.getText().toString();
                            String  result = null;
                            if(requestStatus.equalsIgnoreCase("Following"))
                            {
                                result = updateUserFriends(userName,friendUsernameTextView.getText().toString(), "Not Following");


                            }


                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalAllFriendsListJSONArray.remove(index);
                                Toast.makeText(context,"You have unfollowed "+friendUsernameTextView.getText().toString(), Toast.LENGTH_LONG).show();
                                allFiendsListVwLargeScrnSizeLT();
                                userAllFriendsLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });



                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);



                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                userAllFriendsLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


    }



    public void allFiendsListVwXLargeScrnSizeLT()
    {
        userAllFriendsLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalAllFriendsListJSONArray.length(); i++) {
                String tempResult =finalAllFriendsListJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                String requestStatus =  tempObject.getString("requestStatus");
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
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTypeface(fontAwesomeIcon);
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String requestStatus = confirmFriendRequestTextView.getText().toString();
                            String  result = null;
                            if(requestStatus.equalsIgnoreCase("Following"))
                            {
                                result = updateUserFriends(userName,friendUsernameTextView.getText().toString(), "Not Following");

                            }


                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalAllFriendsListJSONArray.remove(index);
                                Toast.makeText(context,"You have unfollowed "+friendUsernameTextView.getText().toString(), Toast.LENGTH_LONG).show();
                                allFiendsListVwLargeScrnSizeLT();
                                userAllFriendsLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);



                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                userAllFriendsLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }



    public void allFiendsListVwSmallScreenSizeLT()
    {

        userAllFriendsLinearLayout.removeAllViews();
        try {


            for (int i = 0; i < finalAllFriendsListJSONArray.length(); i++) {
                String tempResult = finalAllFriendsListJSONArray.getString(i);
                JSONObject tempObject = new JSONObject(tempResult);
                String recieverUserName = tempObject.getString("recieverUsername");
                String lastName = tempObject.getString("lastName");
                String middleName = tempObject.getString("middleName");
                String firstName = tempObject.getString("firstName");
                String requestStatus =  tempObject.getString("requestStatus");
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
                confirmFriendRequestTextView.setPadding(10,10,0,0);
                confirmFriendRequestTextView.setTextSize(15);
                confirmFriendRequestTextView.setText(requestStatus);
                confirmFriendRequestTextView.setTypeface(fontAwesomeIcon);
                confirmFriendRequestTextView.setBackground(activity.getResources().getDrawable(R.drawable.border3,null));
                confirmFriendRequestTextView.setTypeface(Typeface.create("libre_franklin_thin", Typeface.NORMAL));
                confirmFriendRequestTextView.setTextColor(activity.getResources().getColor(R.color.color22));
                confirmFriendRequestTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String requestStatus = confirmFriendRequestTextView.getText().toString();
                            String  result = null;
                            if(requestStatus.equalsIgnoreCase("Following"))
                            {
                               result = updateUserFriends(userName,friendUsernameTextView.getText().toString(), "Not Following");

                            }


                            if(result.equalsIgnoreCase("Record Updated")) {
                                int index = findPositioninJSONArray(friendUsernameTextView.getText().toString());
                                finalAllFriendsListJSONArray.remove(index);
                                Toast.makeText(context,"You have unfollowed "+friendUsernameTextView.getText().toString(), Toast.LENGTH_LONG).show();
                                allFiendsListVwLargeScrnSizeLT();
                                userAllFriendsLinearLayout.invalidate();
                            }
                            else
                            {
                                Toast.makeText(context,"Network Issue Prevented us from sending this Friend's Request. Try Again!", Toast.LENGTH_LONG).show();
                            }

                    }
                });


                innerSelectionLinearLayout.addView(confirmFriendRequestTextView);



                innerLinearLayout.addView(friendNameTextView);
                innerLinearLayout.addView(friendUsernameTextView);

                outerLinearLayout.addView(profileImageVW);
                outerLinearLayout.addView(innerLinearLayout);
                outerLinearLayout.addView( innerSelectionLinearLayout);


                userAllFriendsLinearLayout.addView(outerLinearLayout);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void transerSearchViewResult(String searchQuery)
    {

        finalAllFriendsListJSONArray = new JSONArray();
        if(searchQuery!=null && searchQuery!="") {

            for (int i = 0; i < allFriendsListJSONArray.length(); i++) {

                try {
                    String tempResult =  allFriendsListJSONArray.getString(i);
                    JSONObject tempObject = null;
                    String recieverUserName = null;
                    tempObject = new JSONObject(tempResult);
                    recieverUserName =  tempObject.getString("recieverUsername");
                    if (recieverUserName.startsWith(searchQuery.toLowerCase()) || recieverUserName.startsWith(searchQuery.toUpperCase())) {
                        finalAllFriendsListJSONArray.put(tempResult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        else
        {
            finalAllFriendsListJSONArray = allFriendsListJSONArray;
        }
    }


    private int findPositioninJSONArray(String recieveName)
    {
        int index=-1;

        for(int i=0; i<finalAllFriendsListJSONArray.length(); i++)
        {

            try {

                JSONObject tempObject = new JSONObject(finalAllFriendsListJSONArray.getString(i));
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





    public String updateUserFriends(String requestUserName, String recieverUserName,String requestStatus)
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

}
