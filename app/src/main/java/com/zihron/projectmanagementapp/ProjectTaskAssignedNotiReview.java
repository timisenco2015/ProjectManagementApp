package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.zihron.projectmanagementapp.ArrowDirection.BOTTOM_CENTER;
import static com.zihron.projectmanagementapp.ArrowDirection.TOP_CENTER;
import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class ProjectTaskAssignedNotiReview {

    private String subType;
    private String notificationId;
    private Activity activity;
    private static PopupWindow popUp;
    private final Random random;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private int[] location;
    private String userName;
    private S3ImageClass s3ImageClass;
    private boolean checkForPermission = true;

   private boolean isViewed = true;
   private String requestStatus;
   private String  entitySubType;
private View view;

private String requesterUserName;
private String lastName;
private String firstName;

private JSONArray selectNotiMeberJArray;
    private JSONArray notificationDetailsJArray;
    private JSONObject notificationDetailsJObject;
    private SharedPreferences sharedPreferences;
    private String fetchProjectName;
    private String fecthGroupName;
    private String fetchProjectTaskName;
    private boolean endOfListView;
    private HttpRequestClass httpRequestClass;
private ListView userNotificationsListView;
private boolean startOfListView;
    private Point screenSize;
    private int screenHeight;
    private String notificationDate;
    private String userFirstName;
    private String userLastName;
    public ProjectTaskAssignedNotiReview(String notificationId,  Activity activity, View view,  String firstName, String lastName, boolean endOfListView,boolean startOfListView)
    {
        Display display =activity.getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
        screenHeight = screenSize.y;

        this.notificationId = notificationId;
        this.activity = activity;
        this.view = view;
        this.progressBarClass = new ProgressBarClass(activity);
        this.asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.random = new Random();
        this.location = new int[2];
        this.firstName = firstName;
        this.lastName = lastName;

        this.s3ImageClass = new S3ImageClass();
       this.endOfListView =endOfListView;
        selectNotiMeberJArray = new JSONArray();
        this.startOfListView=startOfListView;
        sharedPreferences = activity.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userFirstName = sharedPreferences.getString("firstName",null);
        userLastName= sharedPreferences.getString("lastName",null);

        try {
            String result = getEachProjectTaskAssigneddNotifications(notificationId);

            if(result!=null)
            {
                notificationDetailsJArray = new JSONArray(result);
                notificationDetailsJObject = new JSONObject(notificationDetailsJArray.getString(0));
                fetchProjectName = notificationDetailsJObject.getString("projectName");
                fetchProjectTaskName = notificationDetailsJObject.getString("projectTaskName");
                fecthGroupName = notificationDetailsJObject.getString("groupName");
                notificationDate = notificationDetailsJObject.getString("notificationDate");
                isViewed = notificationDetailsJObject.getBoolean("isViewed");
                entitySubType = notificationDetailsJObject.getString("entitySubType");
                requesterUserName = notificationDetailsJObject.getString("senderEmail");
                userName = notificationDetailsJObject.getString("recieverEmail");





            }

        view.getLocationInWindow(location);
        switch(entitySubType)
        {
            case "Requested":
                requestStatus= notificationDetailsJObject.getString("requestStatus");
                getTaskAssignedForAcceptantce();
                break;
            case "Accepted":
                getTaskAssignedAccepted();
                break;
            case "Denied":
                getTaskAssignedDenied();
                break;
            case "Removed":
                getTaskAssignedRemoved();
            break;
            default:
                break;
        }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTaskAssignedForAcceptantce()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView();





    }

    public void getTaskAssignedAccepted()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }



        String sourceString = "Project task name, "+fetchProjectTaskName+" assigned Request sent to "+firstName+" "+lastName+" for project name, "+fetchProjectName+ " you under group name, "+fecthGroupName+"has been accepted.";

        int taskStartIndex = 19;
        int taskEndIndex =taskStartIndex+fetchProjectTaskName.length();

        int projectStartIndex = taskEndIndex+26+firstName.length()+lastName.length()+20;
        int projectEndIndex =projectStartIndex+fetchProjectName.length();

        SpannableString myString = new SpannableString(sourceString);

        ClickableSpan projectLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                Intent pDNRIntent = new Intent(activity, ViewProjectActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };


        ClickableSpan projectTaskLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                editor.putString("projectTaskName",fetchProjectTaskName);
                Intent pDNRIntent = new Intent(activity, ViewEachProjectTaskActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };
        myString.setSpan(projectTaskLinkClickSpan,taskStartIndex,taskEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);


    }

    public void sendNotificationsToUsers(String notiMessage, String requestType)
    {
        JSONObject tempObject = new JSONObject();

        String result =null;
        try {
            tempObject.put("userName",requesterUserName);
            tempObject.put("groupName",fecthGroupName);
            selectNotiMeberJArray.put(tempObject);
            result =  createNotifications(userName, selectNotiMeberJArray.toString(),  "04",requestType);

            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < selectNotiMeberJArray.length(); i++) {
                    result =  getOneSignalIdAttachedUserName(selectNotiMeberJArray.getJSONObject(i).getString("userName"));
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+notiMessage+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getTaskAssignedDenied()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        String sourceString;

            sourceString = "Project task name, "+fetchProjectTaskName +" assigned request sent to "+firstName+" "+lastName+" for project name, "+fetchProjectName+ " you under group name, "+fecthGroupName+"has been denied";
        int taskStartIndex = 19;
        int taskEndIndex =taskStartIndex+fetchProjectTaskName.length();

        int projectStartIndex = taskEndIndex+26+firstName.length()+lastName.length()+18;
        int projectEndIndex =projectStartIndex+fetchProjectName.length();


        SpannableString myString = new SpannableString(sourceString);

        ClickableSpan projectLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                Intent pDNRIntent = new Intent(activity, ViewProjectActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };


        ClickableSpan projectTaskLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                editor.putString("projectTaskName",fetchProjectTaskName);
                Intent pDNRIntent = new Intent(activity, ViewEachProjectTaskActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };
        myString.setSpan(projectTaskLinkClickSpan,taskStartIndex,taskEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);





    }

    public void  getTaskAssignedRemoved()
    {
        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        String sourceString = null;


            sourceString = firstName+" "+lastName+" has unassigned task name, "+fetchProjectTaskName+" for project name "+fetchProjectName+" under group name, "+fetchProjectName+" from you.";

        int taskStartIndex = firstName.length()+lastName.length()+28;
        int taskEndIndex =taskStartIndex+fetchProjectTaskName.length();

        int projectStartIndex = taskEndIndex+26+firstName.length()+lastName.length()+17;
        int projectEndIndex =projectStartIndex+fetchProjectName.length();

        SpannableString myString = new SpannableString(sourceString);

        ClickableSpan projectLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                Intent pDNRIntent = new Intent(activity, ViewProjectActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };


        ClickableSpan projectTaskLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName",fetchProjectName);
                editor.putString("projectTaskName",fetchProjectTaskName);
                Intent pDNRIntent = new Intent(activity, ViewEachProjectTaskActivity.class);
                activity.startActivity(pDNRIntent);

            }

        };
        myString.setSpan(projectTaskLinkClickSpan,taskStartIndex,taskEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);

    }

    public void popUpView()
    {



        try {


        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
        rlpInnerProfileImageVWLayout.setMargins(5, 10, 0, 0);
        profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
        profileImageVW.setId(R.id.memberLogoId);
            if (!s3ImageClass.hasPermission()) {
                checkForPermission = s3ImageClass.getPermission();
            }

            if (s3ImageClass.hasPermission() && checkForPermission) {

                if (s3ImageClass.confirmIfImageInPhone(requesterUserName)) {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(requesterUserName));
                } else {
                    s3ImageClass = new S3ImageClass(activity, requesterUserName, "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        if (s3ImageClass.hasPermission()) {
                            s3ImageClass.writeToPhone(requesterUserName, bitMap);
                        }
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    } else {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name=" + firstName + " " + lastName + "&background=90a8a8&color=fff&size=128").into(profileImageVW);
                    }
                }
            }


            LinearLayout outerLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        outerLayoutParams.setMargins(10,0,0,0);
        outerLayout.setLayoutParams(outerLayoutParams);
        outerLayout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout.LayoutParams notificationMSgTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView notificationMSgTextView = new TextView(activity);
        notificationMSgTextView.setLayoutParams(notificationMSgTxtViewLayout);
        notificationMSgTextView.setTextSize(15);
        String sourceString = null;

            sourceString = firstName+" "+lastName+" sent a request to assign project task name, "+fetchProjectTaskName+" for project name "+fetchProjectName+" under group name, "+notificationDetailsJObject.getString("groupName")+". Choose action below";

            int taskStartIndex = firstName.length()+lastName.length()+46;
            int taskEndIndex =taskStartIndex+fetchProjectTaskName.length();

            int projectStartIndex = taskEndIndex+18;
            int projectEndIndex =projectStartIndex+fetchProjectName.length();



            SpannableString myString = new SpannableString(sourceString);

        ClickableSpan projectLinkClickSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("projectName",fetchProjectName);
                    Intent pDNRIntent = new Intent(activity, ViewProjectActivity.class);
                    activity.startActivity(pDNRIntent);

                }

            };


            ClickableSpan projectTaskLinkClickSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("projectName",fetchProjectName);
                    editor.putString("projectTaskName",fetchProjectTaskName);
                    Intent pDNRIntent = new Intent(activity, ViewEachProjectTaskActivity.class);
                    activity.startActivity(pDNRIntent);

                }

            };
        myString.setSpan(projectTaskLinkClickSpan,taskStartIndex,taskEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        notificationMSgTextView.setMovementMethod(LinkMovementMethod.getInstance());
        notificationMSgTextView.setText(myString);
        notificationMSgTextView.setSingleLine(false);
        notificationMSgTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationMSgTextView.setTextColor(activity.getResources().getColor(R.color.color22));
        notificationMSgTxtViewLayout.setMargins(0, 10, 0, 0);


        LinearLayout.LayoutParams notificationDateTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView notificationDateTextView = new TextView(activity);
        notificationDateTextView.setLayoutParams(notificationMSgTxtViewLayout);
        notificationDateTextView.setTextSize(12);
        notificationDateTextView.setText(notificationDate);
        notificationDateTextView.setSingleLine(false);
        notificationDateTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationDateTextView.setTextColor(activity.getResources().getColor(R.color.color17));
        notificationDateTxtViewLayout.setMargins(0, 5, 0, 0);

        outerLayout.addView(notificationMSgTextView);
        outerLayout.addView(notificationDateTextView);

        LinearLayout outerOuterLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterLayout.setLayoutParams(outerOuterLayoutParams);
        outerOuterLayout.setOrientation(LinearLayout.HORIZONTAL);

        outerOuterLayout.addView(profileImageVW);
        outerOuterLayout.addView(outerLayout);


        LinearLayout innerLayout = new LinearLayout(activity);
        innerLayout.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams innerLTLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLayout.setLayoutParams(innerLTLayoutParams);
        innerLTLayoutParams.setMargins(0,17,0,0);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button acceptButton = new Button(activity);
        acceptButton.setText("Accept");
        acceptButton.setTextSize(11);
        acceptButton.setBackgroundColor(activity.getResources().getColor(R.color.color27));
        acceptButton.setTextColor(activity.getResources().getColor(R.color.color15));
        LinearLayout.LayoutParams acceptButtonLayoutParams = new LinearLayout.LayoutParams(250, 100);
        acceptButton.setLayoutParams(acceptButtonLayoutParams);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    String result = updateProjectTaskAssignedRequest(notificationId,fetchProjectName,fecthGroupName,"Accepted",fetchProjectTaskName,userName);

                    if(result.equalsIgnoreCase("Record Updated"))
                    {
                        Toast.makeText(activity,"Project task assigned request accepted",Toast.LENGTH_LONG).show();


                     sendNotificationsToUsers(userFirstName+" "+userLastName+" has accepted your project task assigned request","Accepted");
                    }
                    else
                    {
                        Toast.makeText(activity,"Unable to update your request at this time. Please try again",Toast.LENGTH_LONG).show();
                    }
                if(popUp!=null && popUp.isShowing())
                {
                    popUp.dismiss();
                    view.setBackgroundColor(activity.getResources().getColor(R.color.color15));
                }

            }
        });

        Button rejectButton = new Button(activity);
        LinearLayout.LayoutParams rejectButtonLayoutParams = new LinearLayout.LayoutParams(250,100);
        rejectButton.setText("Reject");
        rejectButton.setTextSize(11);
        rejectButton.setBackgroundColor(activity.getResources().getColor(R.color.color19));
        rejectButton.setTextColor(activity.getResources().getColor(R.color.color27));
        rejectButtonLayoutParams.setMargins(5,0,0,0);
        rejectButton.setLayoutParams(rejectButtonLayoutParams);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = updateProjectTaskAssignedRequest(notificationId,fetchProjectName,fecthGroupName,"Denied",fetchProjectTaskName,userName);

                    if(result.equalsIgnoreCase("Record Updated"))
                    {
                        Toast.makeText(activity,"Project task assigned request denied",Toast.LENGTH_LONG).show();
                        sendNotificationsToUsers(userFirstName+" "+userLastName+" has denied your project task assigned request","Denied");
                    }
                    else
                    {
                        Toast.makeText(activity,"Unable to update your request at this time. Please try again",Toast.LENGTH_LONG).show();
                    }
                if(popUp!=null && popUp.isShowing())
                {
                    popUp.dismiss();
                    view.setBackgroundColor(activity.getResources().getColor(R.color.color15));
                }
            }
        });
            Button statusButton = new Button(activity);
            statusButton.setText(requestStatus);
            statusButton.setTextSize(12);
            TypedValue value= new TypedValue();
            activity.getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, value, true);
            statusButton.setBackgroundResource(value.resourceId);
            statusButton.setTextColor(activity.getResources().getColor(R.color.color27));
            LinearLayout.LayoutParams statusButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
            statusButton.setLayoutParams(statusButtonLayoutParams);

            if(isViewed)
            {
                innerLayout.removeAllViews();
                innerLayout.addView(statusButton);
            }
            else {
                innerLayout.removeAllViews();
                innerLayout.addView(acceptButton);
                innerLayout.addView(rejectButton);
            }




            LinearLayout outerOuterOuterLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterOuterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterOuterLayout.setLayoutParams(outerOuterOuterLayoutParams);
        outerOuterOuterLayout.setOrientation(LinearLayout.VERTICAL);

        outerOuterOuterLayout.addView(outerOuterLayout);
        outerOuterOuterLayout.addView(innerLayout);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);


        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupbubblelayout,null);
        BubbleLayout popUpLsyout =(BubbleLayout) customView.findViewById(R.id.popLayoutId);


        popUpLsyout.addView(outerOuterOuterLayout);

        popUp = new PopupWindow(
                customView,
                950,
                550
        );


        popUp.setOutsideTouchable(true);
        popUp.setFocusable(true);
        popUp.setElevation(5.0f);


            if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
            {
                popUpLsyout.setArrowDirection(BOTTOM_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 910);

            }
            else
            {
                popUpLsyout.setArrowDirection(TOP_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, view.getHeight() + location[1]);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void popUpView( SpannableString myString)
    {



        try {


            CircleImageView profileImageVW = new CircleImageView(activity);
            RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
            rlpInnerProfileImageVWLayout.setMargins(5, 10, 0, 0);
            profileImageVW.setLayoutParams(rlpInnerProfileImageVWLayout);
            profileImageVW.setId(R.id.memberLogoId);
            if (!s3ImageClass.hasPermission()) {
                checkForPermission = s3ImageClass.getPermission();
            }

            if (s3ImageClass.hasPermission() && checkForPermission) {

                if (s3ImageClass.confirmIfImageInPhone(requesterUserName)) {
                    profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(requesterUserName));
                } else {
                    s3ImageClass = new S3ImageClass(activity, requesterUserName, "profilepicfolder");
                    Bitmap bitMap = s3ImageClass.getImageBitMap();
                    if (s3ImageClass.isObjectExists()) {
                        if (s3ImageClass.hasPermission()) {
                            s3ImageClass.writeToPhone(requesterUserName, bitMap);
                        }
                        profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                        profileImageVW.invalidate();
                    } else {
                        Picasso.with(activity).load("https://ui-avatars.com/api/?name=" + firstName + " " + lastName + "&background=90a8a8&color=fff&size=128").into(profileImageVW);
                    }
                }
            }


            LinearLayout outerLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams outerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            outerLayoutParams.setMargins(10,0,0,0);
            outerLayout.setLayoutParams(outerLayoutParams);
            outerLayout.setOrientation(LinearLayout.VERTICAL);


            LinearLayout.LayoutParams notificationMSgTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final TextView notificationMSgTextView = new TextView(activity);
            notificationMSgTextView.setLayoutParams(notificationMSgTxtViewLayout);
            notificationMSgTextView.setTextSize(15);
            notificationMSgTextView.setText(myString);
            notificationMSgTextView.setSingleLine(false);
            notificationMSgTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            notificationMSgTextView.setTextColor(activity.getResources().getColor(R.color.color22));
            notificationMSgTxtViewLayout.setMargins(0, 10, 0, 0);


            LinearLayout.LayoutParams notificationDateTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            final TextView notificationDateTextView = new TextView(activity);
            notificationDateTextView.setLayoutParams(notificationMSgTxtViewLayout);
            notificationDateTextView.setTextSize(12);
            notificationDateTextView.setText(notificationDetailsJObject.getString("notificationDate"));
            notificationDateTextView.setSingleLine(false);
            notificationDateTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
            notificationDateTextView.setTextColor(activity.getResources().getColor(R.color.color17));
            notificationDateTxtViewLayout.setMargins(0, 5, 0, 0);

            outerLayout.addView(notificationMSgTextView);
            outerLayout.addView(notificationDateTextView);

            LinearLayout outerOuterLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams outerOuterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            outerOuterLayout.setLayoutParams(outerOuterLayoutParams);
            outerOuterLayout.setOrientation(LinearLayout.HORIZONTAL);

            outerOuterLayout.addView(profileImageVW);
            outerOuterLayout.addView(outerLayout);






            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);


            // Inflate the custom layout/view
            View customView = inflater.inflate(R.layout.popupbubblelayout,null);
            BubbleLayout popUpLsyout =(BubbleLayout) customView.findViewById(R.id.popLayoutId);
            popUpLsyout.addView(outerOuterLayout);

            popUp = new PopupWindow(
                    customView,
                    950,
                    450
            );
            popUp.setOutsideTouchable(true);
            popUp.setFocusable(true);
            popUp.setElevation(5.0f);
            if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
            {
                popUpLsyout.setArrowDirection(BOTTOM_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 800);

            }
            else
            {
                popUpLsyout.setArrowDirection(TOP_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, view.getHeight() + location[1]);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    public String updateProjectTaskAssignedRequest(String notificationId,String projectName,String groupName, String requestStatus,String projectTaskName,String taskAssignedUName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String requestStatusUpdateDate =new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        try {

            postDataParams.put("emailOfUserTaskAssigned",taskAssignedUName);
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask", projectTaskName);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("viewedDate", requestStatusUpdateDate);
            postDataParams.put("viewedStatus", requestStatus);
            postDataParams.put("idOfNotification", notificationId);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/uptaa/",postDataParams, activity,"text/plain", "application/json");
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







    public String getEachProjectTaskAssigneddNotifications(String notificationId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("idOfNotification",notificationId);


            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fptamn/",postDataParams, activity,"application/json", "application/json");
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
            postDataParams.put("nameOfGroup", fecthGroupName);
            postDataParams.put("entityType", entityType);
            postDataParams.put("nameOfProject", fetchProjectName);
            postDataParams.put("nameOfProjectTask", fetchProjectTaskName);
          Log.e("--pl",postDataParams.toString());
           httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iun/",postDataParams, activity,"text/plain", "application/json");
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
