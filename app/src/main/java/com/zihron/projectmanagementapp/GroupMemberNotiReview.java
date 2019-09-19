package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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

import ZihronChatApp.ZihronWorkChat.Channels.ZWCGroupChannel;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.zihron.projectmanagementapp.ArrowDirection.BOTTOM_CENTER;
import static com.zihron.projectmanagementapp.ArrowDirection.TOP_CENTER;
import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class GroupMemberNotiReview {


    private String notificationId;
    private Activity activity;
    private static PopupWindow popUp;
    private final Random random;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private int[] location;
    private String userName;
    private S3ImageClass s3ImageClass;
    private View view;
    private String requesterUserName;
    private String lastName;
    private String firstName;
    private JSONArray selectNotiMeberJArray;
    private JSONArray notificationDetailsJArray;
    private JSONObject notificationDetailsJObject;
    private SharedPreferences sharedPreferences;
    private String fecthGroupName;
    private String fetchProjectName;
    private boolean endOfListView;
    private boolean startOfListView;
    private String userFirstName;
    private String userLastName;
    private HttpRequestClass httpRequestClass;
    private boolean checkForPermission = true;
    private int membersCount;
    private String entitySubType;
    private String notificationDate;
    private boolean isViewed;
   private String requestStatus;
   private Point screenSize;
   private int screenHeight;
    private ZWCGroupChannel zihronChatAppGroupChannel;
    private Gson googleJson;
    public GroupMemberNotiReview(String notificationId,   Activity activity, View view,   String firstName, String lastName,boolean endOfListView,boolean startOfListView,int membersCount)
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
this.lastName=lastName;
        this.membersCount=membersCount;
this.startOfListView=startOfListView;

        this.s3ImageClass = new S3ImageClass();
        this.endOfListView =endOfListView;
        selectNotiMeberJArray = new JSONArray();
        zihronChatAppGroupChannel = new ZWCGroupChannel(activity);
        sharedPreferences = activity.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userFirstName = sharedPreferences.getString("firstName",null);
        userLastName= sharedPreferences.getString("lastName",null);
        try {
            String result = getEachProjectMemberNotifications(notificationId);
            if(result!=null)
            {
                notificationDetailsJArray = new JSONArray(result);
                notificationDetailsJObject = new JSONObject(notificationDetailsJArray.getString(0));
                fecthGroupName = notificationDetailsJObject.getString("groupName");
                fetchProjectName = notificationDetailsJObject.getString("projectName");
                requesterUserName = notificationDetailsJObject.getString("senderEmail");
                userName = notificationDetailsJObject.getString("recieverEmail");
                isViewed = notificationDetailsJObject.getBoolean("isViewed");
                entitySubType = notificationDetailsJObject.getString("entitySubType");
                notificationDate = notificationDetailsJObject.getString("notificationDate");
                requestStatus= notificationDetailsJObject.getString("requestStatus");


                selectNotiMeberJArray.put(requesterUserName);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.getLocationInWindow(location);

        switch(entitySubType)
        {
            case "Requested":
                getGroupMemberForAcceptantce();
                break;
           case "Accepted":
                getGroupMemberAccepted();
                break;
            case "Denied":
                getGroupMemberDenied();
                break;
            case "Removed":
                getGroupMemberRemoved();
                break;
            default:
                break;
        }



    }

    public void getGroupMemberForAcceptantce()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView();

    }

    public void getGroupMemberAccepted()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

        String sourceString = "Group member request sent to "+firstName+" "+lastName+" for group name "+fecthGroupName+" has been accepted.";


        SpannableString content = new SpannableString(sourceString);
        UnderlineSpan us1=new UnderlineSpan();
        UnderlineSpan us2=new UnderlineSpan();
        UnderlineSpan us3=new UnderlineSpan();
        content.setSpan(us1, 29, 29+firstName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setSpan(us2, 29+firstName.length()+1, (29+firstName.length()+1)+lastName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndex = 29+firstName.length()+1 +lastName.length()+16;

        content.setSpan(us3, startIndex, startIndex+fecthGroupName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        popUpView(content);

    }

    public void getGroupMemberRemoved()
    {
        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }

        String sourceString = firstName+" "+lastName+" has removed you from group name "+fecthGroupName+".";
        SpannableString content = new SpannableString(sourceString);
        UnderlineSpan us1=new UnderlineSpan();
        UnderlineSpan us2=new UnderlineSpan();
        UnderlineSpan us3=new UnderlineSpan();
        content.setSpan(us1, 0, firstName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setSpan(us2, firstName.length()+1, (firstName.length()+1)+lastName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndex = firstName.length()+1 +lastName.length()+33;

        content.setSpan(us3, startIndex, startIndex+fecthGroupName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        popUpView(content);

    }

    public void sendNotificationsToUsers(String notiMessage, String requestStatus)
    {
        String result =null;
       try {
            result =  createNotifications(userName, selectNotiMeberJArray.toString(),  "02",requestStatus);
            if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < selectNotiMeberJArray.length(); i++) {
                    result = getOneSignalIdAttachedUserName(selectNotiMeberJArray.getString(i));
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+notiMessage+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                }
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getGroupMemberDenied()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
       String sourceString = firstName+" "+lastName+" has denied group member add request for group name "+fecthGroupName+".";
        SpannableString content = new SpannableString(sourceString);
        UnderlineSpan us1=new UnderlineSpan();
        UnderlineSpan us2=new UnderlineSpan();
        UnderlineSpan us3=new UnderlineSpan();
        content.setSpan(us1, 0, firstName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setSpan(us2, firstName.length()+1, (firstName.length()+1)+lastName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndex = firstName.length()+1 +lastName.length()+52;

        content.setSpan(us3, startIndex, startIndex+fecthGroupName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        popUpView(content);



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
            String sourceString = firstName+" "+lastName+" sent a request to add you as a member to group name"+ ". Choose action below";
            notificationMSgTextView.setMovementMethod(LinkMovementMethod.getInstance());
            notificationMSgTextView.setText(sourceString);
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

                        String result = updateProjectMemberAddRequest(userName,fetchProjectName,fecthGroupName,"Accepted");
                        if(result.equalsIgnoreCase("Record Updated"))
                        {
                            Toast.makeText(activity,"Project group member request accepted",Toast.LENGTH_LONG).show();
                            sendNotificationsToUsers(userFirstName+" "+userLastName+" has accepted your project group member add request","Accepted");
                            googleJson = new Gson();
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

                        String result = updateProjectMemberAddRequest(userName,fetchProjectName,fecthGroupName,"Denied");
                        if(result.equalsIgnoreCase("Record Updated"))
                        {
                            Toast.makeText(activity,"Project group member add request denied",Toast.LENGTH_LONG).show();
                            sendNotificationsToUsers(userFirstName+" "+userLastName+" has denied your project group member add request","Denied");
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
                    480
            );


            popUp.setOutsideTouchable(true);
            popUp.setFocusable(true);
            popUp.setElevation(5.0f);


            if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
            {
                popUpLsyout.setArrowDirection(BOTTOM_CENTER);
                popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 740);

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
            notificationDateTextView.setTextColor(activity.getResources().getColor(R.color.color1));
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
                    280
            );
            popUp.setOutsideTouchable(true);
            popUp.setFocusable(true);
            popUp.setElevation(5.0f);

             if((screenHeight-(view.getHeight()*3))<=location[1] && location[1]<=screenHeight)
        {
            popUpLsyout.setArrowDirection(BOTTOM_CENTER);
            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 640);

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





    public String updateProjectMemberAddRequest(String memberUserName,String projectName,String groupName, String requestStatus)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String  requestStatusUpdateDate =new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("idOfNotification", notificationId);

            postDataParams.put("approvedDate", requestStatusUpdateDate);
            postDataParams.put("userNameOfMember", memberUserName);
            postDataParams.put("StatusOfRequest", requestStatus);


            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/upmus/",postDataParams, activity,"text/plain", "application/json");
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
            postDataParams.put("entityType", entityType);
            postDataParams.put("nameOfGroup", fecthGroupName);
            postDataParams.put("nameOfProject", fetchProjectName);

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


    public String getEachProjectMemberNotifications(String notificationId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String  notiCreatedDate=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("idOfNotification",notificationId);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fgmrn/",postDataParams, activity,"application/json", "application/json");
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
