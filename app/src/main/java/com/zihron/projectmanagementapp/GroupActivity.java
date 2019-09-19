package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    private Activity activity;
    private S3ImageClass s3ImageClass;
    TextView editGroupBackgrndPicTextView;
    Typeface fontAwesomeIcon;
    private LinearLayout groupListLinearLayout;
    private LayoutInflater inflater;
    private  View popupView;
    private String userName;
    private String groupName;
    private String groupOwnerName;
    private JSONArray allGroupFriendsJSONArray;
    public static PopupWindow projectTaskAssignedPopUpWindow=null;
    private String fullNameofGroupOwner;
    private TextView groupOwnerTextView;
    private TextView groupOwnerFullNameTextView;
    private ImageView backgroundGrpImageView;
    private ProgressBarClass progressBarClass;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        fontAwesomeIcon = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        groupListLinearLayout = (LinearLayout)findViewById(R.id.groupListLLTId);
        editGroupBackgrndPicTextView = (TextView)findViewById(R.id.editBackgrndImageTxtVWId);
        editGroupBackgrndPicTextView.setTypeface(fontAwesomeIcon);
        groupOwnerTextView = (TextView) findViewById(R.id.groupOwnerTVWId);
        groupOwnerFullNameTextView = (TextView) findViewById(R.id.groupOwnerFullNameTVWId);
        backgroundGrpImageView = (ImageView)findViewById(R.id.backgroundGrpImageVWId);
        s3ImageClass = new S3ImageClass();
                groupOwnerName = getIntent().getStringExtra("groupOwner");
        groupName = getIntent().getStringExtra("groupName");
        fullNameofGroupOwner = getIntent().getStringExtra("fullNameofGroupOwner");
        groupOwnerTextView.setText(groupName);
        groupOwnerFullNameTextView.setText("Created By: "+fullNameofGroupOwner);

        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        s3ImageClass = new S3ImageClass(activity, groupName, "groupdisplayimage");

        if( s3ImageClass.isObjectExists())
        {
            backgroundGrpImageView.setImageBitmap(s3ImageClass.getImageBitMap());
            backgroundGrpImageView.invalidate();
        }

        else
        {
            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+groupName+"&background=90a8a8&color=fff&size=128").into(backgroundGrpImageView);
        }



            String result = getUserGroupAllFriends(groupOwnerName,groupName);

        try {
            allGroupFriendsJSONArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(allGroupFriendsJSONArray.length()==0)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("We can't get members for the group as a result of network error. Please try again later");
                        alertDialogBuilder.setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                      Intent gAIntent = new Intent(GroupActivity.this, GroupsActivity.class);
                                      startActivity(gAIntent);
                                    }
                                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else
            {
                int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        //friendSuggestnListVwLargeScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        groupFriendsListVwNormalScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                        // friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                        break;

                    case Configuration.SCREENLAYOUT_SIZE_SMALL:
                        // friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                        break;
                    default:

                }

            }



        editGroupBackgrndPicTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uPIAIntent = new Intent(GroupActivity.this, GalleryActivity.class);
                uPIAIntent.putExtra("groupName",groupName);
                uPIAIntent.putExtra("typeOfFolder","groupdisplayimage");
                uPIAIntent.putExtra("accesName",groupName);
                uPIAIntent.putExtra("selectionType","Group");
               startActivity(uPIAIntent);
            }
        });


    }
    @Override
    public void onBackPressed() {

    }
    public void backToViewGroupsActivityActivity(View view)
    {
        Intent gAIntent = new Intent(GroupActivity.this, GroupsActivity.class);
        startActivity(gAIntent);
    }

 public void groupFriendsListVwNormalScrnSizeLT()
 {
     groupListLinearLayout.removeAllViews();
     for (int i = 0; i < allGroupFriendsJSONArray.length(); i++) {

         try {
             JSONArray tempJArray = allGroupFriendsJSONArray.getJSONArray(i);

         String recieverUserName = tempJArray.getString(0);
         String lastName = tempJArray.getString(2);

         String firstName = tempJArray.getString(1);
             String fullName = firstName+" "+lastName;
       LinearLayout outerLinearLayout = new LinearLayout(activity);
         LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
         outerLinearLayoutLP.setMargins(40,0,0,30);
         outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
         outerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

         CircleImageView profileImageVW = new CircleImageView(activity);
         LinearLayout.LayoutParams llpInnerProfileImageVWLayout = new LinearLayout.LayoutParams(120, 120);
         llpInnerProfileImageVWLayout.setMargins(0,40,0,0);
         profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
         profileImageVW.setClickable(true);

         profileImageVW.setId(R.id.memberLogoId);



             if(s3ImageClass.confirmIfImageInPhone(recieverUserName))
             {
                 profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(recieverUserName));
             }
             else {
                 s3ImageClass = new S3ImageClass(activity,recieverUserName, "profilepicfolder");
                 Bitmap bitMap = s3ImageClass.getImageBitMap();
                 if (s3ImageClass.isObjectExists()) {
                     s3ImageClass.writeToPhone(recieverUserName,bitMap);
                     profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                     profileImageVW.invalidate();
                 }
                 else
                 {
                     Picasso.with(activity).load("https://ui-avatars.com/api/?name="+fullName+"&background=90a8a8&color=fff&size=128").into( profileImageVW);

                 }
             }

         LinearLayout innerLinearLayout = new LinearLayout(activity);
         LinearLayout.LayoutParams innerLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
         innerLinearLayoutParams.setMargins(40,45,0,0);
         innerLinearLayout.setLayoutParams(innerLinearLayoutParams);
         innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        innerLinearLayout.setGravity(Gravity.CENTER_VERTICAL);



         LinearLayout.LayoutParams friendNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         final TextView friendNameTextView = new TextView(activity);
         friendNameTextView.setLayoutParams(friendNameTxtViewLayout);
         friendNameTextView.setTextSize(15);
         friendNameTextView.setText(firstName+" "+lastName);

         friendNameTextView.setSingleLine(true);
         friendNameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
         friendNameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
         friendNameTxtViewLayout.setMargins(0, 0, 0, 0);


         LinearLayout.LayoutParams friendUserNameTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         final TextView friendUsernameTextView = new TextView(activity);
         friendUsernameTextView.setLayoutParams(friendNameTxtViewLayout);
         friendUsernameTextView.setTextSize(15);
         friendUsernameTextView.setText(recieverUserName);
         friendUsernameTextView.setSingleLine(true);
         friendUsernameTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
         friendUsernameTextView.setTextColor(activity.getResources().getColor(R.color.color22));
         friendUserNameTxtViewLayout.setMargins(0, 0, 0, 0);

         innerLinearLayout.addView(friendNameTextView);
         innerLinearLayout.addView(friendUsernameTextView);

         outerLinearLayout.addView(profileImageVW);
         outerLinearLayout.addView(innerLinearLayout);
       ;


         groupListLinearLayout.addView(outerLinearLayout);
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

 }


    public String getUserGroupAllFriends(String userName,String groupName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("groupName", groupName);
            postDataParams.put("userName", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fugaf/",postDataParams, activity,"application/json", "application/json");
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
