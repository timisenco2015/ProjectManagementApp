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
import com.zihron.projectmanagementapp.Utility.S3ImageClass;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import ZihronChatApp.ZihronWorkChat.Channels.ZWCGroupChannel;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.zihron.projectmanagementapp.ArrowDirection.BOTTOM_CENTER;
import static com.zihron.projectmanagementapp.ArrowDirection.TOP_CENTER;
import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class ProjectDelegateNotiReview {

    private String subType;
    private String notificationId;
    private Activity activity;
    private static PopupWindow popUp;
    private final Random random;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private int[] location;
    private String userName;
    private S3ImageClass s3ImageClass;
    private String notificationType;
    private Point screenSize;
    private int screenHeight;
private View view;
    private ZWCGroupChannel zihronChatAppGroupChannel;
private String requesterUserName;
private String lastName;
private String firstName;

private JSONArray selectNotiMeberJArray;
    private JSONArray notificationDetailsJArray;
    private JSONObject notificationDetailsJObject;
    private SharedPreferences sharedPreferences;
    private String fetchProjectName;
    private String fecthGroupName;
    private boolean endOfListView;
    private HttpRequestClass httpRequestClass;
private ListView userNotificationsListView;
    private boolean checkForPermission = true;
    private boolean startOfListView;
    public ProjectDelegateNotiReview(String notificationId, String subType, String notificationType, Activity activity, View view, String userName, String requesterUserName, String firstName, String lastName,boolean endOfListView,boolean startOfListView)
    {
        Display display =activity.getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
        screenHeight = screenSize.y;
        this.subType = subType;
        this.notificationId = notificationId;
        this.activity = activity;
        this.view = view;

        this.asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.random = new Random();
        this.location = new int[2];
        this.userName=userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startOfListView=startOfListView;
        this.notificationType=notificationType;
        this.requesterUserName =requesterUserName;
        this.s3ImageClass = new S3ImageClass();
       this.endOfListView =endOfListView;
        selectNotiMeberJArray = new JSONArray();
        sharedPreferences = activity.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        zihronChatAppGroupChannel = new ZWCGroupChannel(activity);

        try {
            String result = getEachProjectDelegatedNotifications(notificationId);

            if(result!=null)
            {
                notificationDetailsJArray = new JSONArray(result);
                notificationDetailsJObject = new JSONObject(notificationDetailsJArray.getString(0));
                requesterUserName = notificationDetailsJObject.getString("senderEmail");
                fetchProjectName = notificationDetailsJObject.getString("projectName");
                fecthGroupName = notificationDetailsJObject.getString("groupName");
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        view.getLocationInWindow(location);
        switch(subType)
        {
            case "Requested":
                getDelegateForAcceptantce();
                break;
            case "Accepted":
                getDelegateAccepted();
                break;
            case "Denied":
                getDelegateDenied();
                break;
            case "Removed":
                getDelegateRemoved();
                break;
            case "Updated":
                getDelegateUpdated();
                break;
            default:
                break;
        }



    }

    public void getDelegateForAcceptantce()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView();

    }


    public void getDelegateUpdated()
    {
        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

        String sourceString = firstName+" "+lastName+" has changed your project privileges for project name, "+fetchProjectName+" under group name, "+fecthGroupName+".";
        int projectStartIndex = firstName.length()+lastName.length()+55;
        int projectEndIndex = projectStartIndex+fetchProjectName.length();

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



        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);
    }

    public void getDelegateAccepted()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

        String sourceString = "Deligate Request sent to "+firstName+" "+lastName+" for project name, "+fetchProjectName+ " you under group name, "+fecthGroupName+"has been accepted.";
        int projectStartIndex =26+firstName.length()+lastName.length()+19;
        int projectEndIndex = projectStartIndex+fetchProjectName.length();


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



        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);



    }

    public void getDelegateRemoved()
    {
        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

      String sourceString = firstName+" "+lastName+" has undeligated you from project name, "+fetchProjectName+ "  under group name, "+fecthGroupName+".";

        int projectStartIndex =firstName.length()+lastName.length()+40;
        int projectEndIndex = projectStartIndex+fetchProjectName.length();

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



       myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);

    }

    public void sendNotificationsToUsers(String notiMessage,String requestType)
    {
        String result =null;

        try {
            JSONObject tempObject = new JSONObject();
            tempObject.put("userName",requesterUserName);
            tempObject.put("groupName",fecthGroupName);
            selectNotiMeberJArray.put(tempObject);
            result =  createNotifications(userName, selectNotiMeberJArray.toString(),  "03",requestType);
            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < selectNotiMeberJArray.length(); i++) {
                   result = getOneSignalIdAttachedUserName(selectNotiMeberJArray.getJSONObject(i).getString("userName"));
                    if(result!=null) {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + notiMessage + "'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getDelegateDenied()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

        String sourceString ="Deligate Request sent to "+firstName+" "+lastName+" for project name, "+fetchProjectName+ " under group name, "+fecthGroupName+"has been denied.";
        int projectStartIndex =25+firstName.length()+lastName.length()+20;
        int projectEndIndex = projectStartIndex+fetchProjectName.length();


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



        myString.setSpan(projectLinkClickSpan,projectStartIndex,projectEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        popUpView(myString);



    }

    public void popUpView()
    {



        try {

        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
        rlpInnerProfileImageVWLayout.setMargins(5, 0, 0, 0);
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

            sourceString = firstName+" "+lastName+" sent a request to deligate project name, "+fetchProjectName+ " you under group name, "+notificationDetailsJObject.getString("groupName")+". Choose action below";
            int projectStartIndex = firstName.length()+lastName.length()+43;
            int projectEndIndex = projectStartIndex+fetchProjectName.length();

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
             String result =   updateProjectDelegatesRequest(userName,fetchProjectName,fecthGroupName,"Accepted");

                    if(result.equalsIgnoreCase("record updated"))
                    {
                        Toast.makeText(activity,"Project Delegation request accepted",Toast.LENGTH_LONG).show();
                     sendNotificationsToUsers(firstName+" "+lastName+" has accepted your project delegation request","Accepted");
                        zihronChatAppGroupChannel.acceptUserInvitation(fecthGroupName);
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
                String result =   updateProjectDelegatesRequest(userName,fetchProjectName,fecthGroupName,"Denied");

                if(result.equalsIgnoreCase("Record Inserted"))
                    {
                        Toast.makeText(activity,"Project Delegation request denied",Toast.LENGTH_LONG).show();
                        sendNotificationsToUsers(firstName+" "+lastName+" has denied your project delegation request","Denied");
                        zihronChatAppGroupChannel.declineUserInvitation(fecthGroupName);
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
        innerLayout.addView(acceptButton);
        innerLayout.addView(rejectButton);


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
                520
        );


        popUp.setOutsideTouchable(true);
        popUp.setFocusable(true);
        popUp.setElevation(5.0f);



            if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
            {
                popUpLsyout.setArrowDirection(BOTTOM_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 885);

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
    public void popUpView(SpannableString myString)
    {



        try {


            CircleImageView profileImageVW = new CircleImageView(activity);
            RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, 120);
            rlpInnerProfileImageVWLayout.setMargins(5, 0, 0, 0);
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
                    380
            );
            popUp.setOutsideTouchable(true);
            popUp.setFocusable(true);
            popUp.setElevation(5.0f);
            if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
            {
                popUpLsyout.setArrowDirection(BOTTOM_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 750);

            }
            else
            {
                popUpLsyout.setArrowDirection(TOP_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, view.getHeight() + location[1]);

            }

                view.setBackgroundColor(activity.getResources().getColor(R.color.color15));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String updateProjectDelegatesRequest(String userName, String projectName, String groupName, String requestStatus)
    {

        String  result = null;
        JSONObject postDataParams = new JSONObject();
       String requestStatusUpdateDate =new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("emailOfUser",userName);
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("viewedDate", requestStatusUpdateDate);
            postDataParams.put("viewedStatus", requestStatus);
            postDataParams.put("idOfNotification", notificationId);








            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/updrs/",postDataParams, activity,"text/plain", "application/json");
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


    public String getEachProjectDelegatedNotifications(String notificationId)
    {

        String  result = null;
        JSONObject postDataParams = new JSONObject();
       try {
            postDataParams.put("idOfNotification",notificationId);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpdmn/",postDataParams, activity,"application/json", "application/json");
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
            postDataParams.put("nameOfProject", fetchProjectName);
            postDataParams.put("dateCreated", notiCreatedDate);
            postDataParams.put("entityId", entityId);
            postDataParams.put("entityType", entityType);
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
