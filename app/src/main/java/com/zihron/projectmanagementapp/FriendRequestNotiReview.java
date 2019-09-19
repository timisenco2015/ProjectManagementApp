package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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

public class FriendRequestNotiReview {

    private String entitySubType;
    private String notificationId;
    private Activity activity;
    private static PopupWindow popUp;
    private final Random random;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private int[] location;
    private String userName;
    private S3ImageClass s3ImageClass;
    private SharedPreferences sharedPreferences;

    private String notificationType;
private View view;
private String requesterUserName;
private String lastName;
private String firstName;
    private HttpRequestClass httpRequestClass;
    private boolean checkForPermission = true;
private JSONArray notificationDetailsJArray;
private JSONObject notificationDetailsJObject;
private JSONArray selectNotiMeberJArray;
private boolean endOfListView;
private int membersCount;
private boolean isViewed;
private String notificationDate;
private String requestStatus;
    private ListView userNotificationsListView;
    private Point screenSize;
    private int screenHeight;
    private String userFirstName;
    private String userLastName;
    public FriendRequestNotiReview(String notificationId,Activity activity, View view,String firstName, String lastName,boolean endOfListView,int membersCount)
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
        this.requesterUserName =requesterUserName;
        this.endOfListView=endOfListView;
        this.membersCount=membersCount;
        this.s3ImageClass = new S3ImageClass();
        this.userNotificationsListView =userNotificationsListView;
        sharedPreferences = activity.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userFirstName = sharedPreferences.getString("firstName",null);
        userLastName= sharedPreferences.getString("lastName",null);
        selectNotiMeberJArray = new JSONArray();
       try {
            String result = GetEachFriendRequestNotification(notificationId);

            if(result!=null)
            {
                notificationDetailsJArray = new JSONArray(result);
                Toast.makeText(activity,"--"+notificationType,Toast.LENGTH_LONG).show();
                notificationDetailsJObject = new JSONObject(notificationDetailsJArray.getString(0));
                requesterUserName = notificationDetailsJObject.getString("senderEmail");
                userName = notificationDetailsJObject.getString("recieverEmail");
                isViewed = notificationDetailsJObject.getBoolean("isViewed");
                entitySubType = notificationDetailsJObject.getString("entitySubType");
                notificationDate = notificationDetailsJObject.getString("notificationDate");
                view.getLocationInWindow(location);

                switch(entitySubType)
                {
                    case "Requested":
                        requestStatus = notificationDetailsJObject.getString("requestStatus");
                        getFriendRequestForAcceptantce();
                        break;
                    case "Accepted":
                        getFriendAccepted();
                        break;
                    case "Denied":
                        getFriendDenied();
                        break;
                    default:
                        break;
                }

            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }




    }

    public void getFriendRequestForAcceptantce()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView();





    }

    public void getFriendAccepted()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView(firstName+" "+lastName+" has accepted your friend request. Choose action below");





    }

    public void sendNotificationsToUsers(String notiMessage, String status)
    {
        String result =null;
       try {
           selectNotiMeberJArray.put(requesterUserName);
            result =  createNotifications(userName, selectNotiMeberJArray.toString(),  "01",status);
            if(result.equalsIgnoreCase("Record Inserted")) {
                result = getOneSignalIdAttachedUserName(selectNotiMeberJArray.getString(0));
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+notiMessage+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);


                }
            }
         catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getFriendDenied()
    {

        if(popUp!=null && popUp.isShowing())
        {
            popUp.dismiss();
        }
        popUpView(firstName+" "+lastName+" has denied your friend request. Choose action below");





    }

    public void popUpView()
    {
        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(120, RelativeLayout.LayoutParams.WRAP_CONTENT);
        profileImageVW.setPadding(0,0,0,20);
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
        String sourceString = firstName+" "+lastName+" sent a friend request to you. Choose action below";
        SpannableString str = new SpannableString(sourceString);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        notificationMSgTextView.setText(str);
        notificationMSgTextView.setSingleLine(false);
        notificationMSgTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationMSgTextView.setTextColor(activity.getResources().getColor(R.color.color22));
        notificationMSgTxtViewLayout.setMargins(0, 10, 0, 0);

        LinearLayout inner1Layout = new LinearLayout(activity);
        LinearLayout.LayoutParams inner1LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        inner1LayoutParams.setMargins(10,0,0,0);
        inner1Layout.setOrientation(LinearLayout.HORIZONTAL);
        inner1Layout.setLayoutParams(inner1LayoutParams);



        LinearLayout.LayoutParams statusTextViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView statusTextView = new TextView(activity);
        statusTextViewLayout.setMargins(0,0,20,0);
        notificationMSgTextView.setLayoutParams(statusTextViewLayout);
        statusTextView.setTextSize(15);
        statusTextView.setSingleLine(true);
        statusTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        statusTextView.setTextColor(activity.getResources().getColor(R.color.color43));





        LinearLayout.LayoutParams notificationDateTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView notificationDateTextView = new TextView(activity);
        notificationDateTxtViewLayout.setMargins(0, 5, 0, 0);
        notificationDateTextView.setLayoutParams(notificationMSgTxtViewLayout);
        notificationDateTextView.setTextSize(12);
            notificationDateTextView.setText(notificationDate);

        notificationDateTextView.setSingleLine(false);
        notificationDateTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationDateTextView.setTextColor(activity.getResources().getColor(R.color.color17));



        inner1Layout.addView(statusTextView);
        inner1Layout.addView(notificationDateTextView);


        outerLayout.addView(notificationMSgTextView);
        outerLayout.addView(inner1Layout);

        LinearLayout outerOuterLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterLayout.setLayoutParams(outerOuterLayoutParams);
        outerOuterLayout.setOrientation(LinearLayout.HORIZONTAL);

        outerOuterLayout.addView(profileImageVW);
        outerOuterLayout.addView(outerLayout);





        LinearLayout inner2Layout = new LinearLayout(activity);
        inner2Layout.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams inner2LTLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        inner2Layout.setOrientation(LinearLayout.HORIZONTAL);
        inner2Layout.setLayoutParams(inner2LTLayoutParams);
        inner2LTLayoutParams.setMargins(0,17,0,0);


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


                    String result = UpdateFriendsRequest(requesterUserName,userName,"Following");
                    if(result.equalsIgnoreCase("Record Updated"))
                    {
                        statusTextView.setText("Accepted");
                        Toast.makeText(activity,"Friend request accepted",Toast.LENGTH_LONG).show();
                        sendNotificationsToUsers(userFirstName+" "+userLastName+" has accepted your friend request","Accepted");
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


                    String result = UpdateFriendsRequest(requesterUserName,userName,"Not Following");

                    if(result.equalsIgnoreCase("Record Inserted"))
                    {
                        statusTextView.setText("Rejected");

                        Toast.makeText(activity,"Friend request denied",Toast.LENGTH_LONG).show();
                        sendNotificationsToUsers(userFirstName+" "+userLastName+" has denied your friend request","Denied");
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
                inner2Layout.removeAllViews();
                inner2Layout.addView(statusButton);
            }
            else {
                inner2Layout.removeAllViews();
                inner2Layout.addView(acceptButton);
                inner2Layout.addView(rejectButton);
            }

        LinearLayout outerOuterOuterLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterOuterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterOuterLayout.setLayoutParams(outerOuterOuterLayoutParams);
        outerOuterOuterLayout.setOrientation(LinearLayout.VERTICAL);

        outerOuterOuterLayout.addView(outerOuterLayout);

            outerOuterOuterLayout.addView(inner2Layout);



        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);


        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupbubblelayout,null);
        BubbleLayout popUpLsyout =(BubbleLayout) customView.findViewById(R.id.popLayoutId);

        popUpLsyout.addView(outerOuterOuterLayout);

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
            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 780);

        }
        else
        {
            popUpLsyout.setArrowDirection(TOP_CENTER);
            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, view.getHeight() + location[1]);

        }

    }
    public void popUpView(String textMessage)
    {

        CircleImageView profileImageVW = new CircleImageView(activity);
        RelativeLayout.LayoutParams rlpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(180, 180);
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
        SpannableString str = new SpannableString(textMessage);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        notificationMSgTextView.setText(str);
        notificationMSgTextView.setSingleLine(false);
        notificationMSgTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationMSgTextView.setTextColor(activity.getResources().getColor(R.color.color22));
        notificationMSgTxtViewLayout.setMargins(0, 20, 0, 0);


        LinearLayout.LayoutParams notificationDateTxtViewLayout = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final TextView notificationDateTextView = new TextView(activity);
        notificationDateTextView.setLayoutParams(notificationMSgTxtViewLayout);
        notificationDateTextView.setTextSize(12);
            notificationDateTextView.setText(notificationDate);

        notificationDateTextView.setSingleLine(false);
        notificationDateTextView.setTypeface(Typeface.create("lato", Typeface.NORMAL));
        notificationDateTextView.setTextColor(activity.getResources().getColor(R.color.color17));
        notificationDateTxtViewLayout.setMargins(0, 15, 0, 0);

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
            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, (view.getHeight() + location[1]) - 780);

        }
        else
        {
            popUpLsyout.setArrowDirection(TOP_CENTER);
            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, location[0] + 80, view.getHeight() + location[1]);

        }



        view.setBackgroundColor(activity.getResources().getColor(R.color.color15));

    }





    public String UpdateFriendsRequest(String friendRequestSender,String friendRequestReciever,String updateStatus)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("idOfNotification", notificationId);
            postDataParams.put("requesterUsername",friendRequestSender);
            postDataParams.put("recieverUsername", friendRequestReciever);
            postDataParams.put("updateStatus", updateStatus);
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }





    public String GetEachFriendRequestNotification(String notificationId)
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
