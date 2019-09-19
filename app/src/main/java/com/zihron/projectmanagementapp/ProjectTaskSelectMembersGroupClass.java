package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ZihronChatApp.ZihronWorkChat.Channels.ZWCGroupChannel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectTaskSelectMembersGroupClass {
 private Activity activity;
 private String result;
 private JSONArray memberJSONArray;
 private Button selectedFriendsButton;
 private ArrayList<String> selectedMemberArrayList ;
 private JSONObject memberJSONObject;
 private JSONArray seletedMemberJSONArray;
 private ArrayList<String>  chatMemberArrayList;
    private S3ImageClass s3ImageClass;
    private boolean checkForPermission = true;
    private LinearLayout selectedFriendHViewLinearLayout;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private JSONArray notificationMemberJArray;
    private String userName;
    private String projectName;
    private String projectTaskName;
    private String groupName;
    private ZWCGroupChannel zihronChatAppGroupChannel;
    private SharedPreferences sharedPreferences;
    public ProjectTaskSelectMembersGroupClass(LinearLayout selectedFriendHViewLinearLayout, Button selectedFriendsButton, final Activity activity, String result)
    {
        this.activity = activity;
        this.result = result;
        this.selectedFriendsButton =selectedFriendsButton;
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName =  sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        userName = sharedPreferences.getString("userName", null);
        groupName = sharedPreferences.getString("projectDefaultGroup", null);
        zihronChatAppGroupChannel = new ZWCGroupChannel(activity);
        selectedMemberArrayList = new ArrayList<String>();
        this.s3ImageClass = new S3ImageClass();
        this.selectedFriendHViewLinearLayout=selectedFriendHViewLinearLayout;
        try {
            memberJSONArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                membersListVwNormalScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                //  friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                //   friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                break;
            default:

        }
        selectedFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListToJSOnArray();

                try {
                    notificationMemberJArray = new JSONArray(seletedMemberJSONArray.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String  result = assignTaskToSelectedMembers(userName,projectTaskName,projectName,seletedMemberJSONArray.toString(), groupName );
                if(result.equalsIgnoreCase("Record Inserted"))
                {
                    zihronChatAppGroupChannel.InviteUsers(chatMemberArrayList,groupName);
                    sendNotificationsToUsers("You have been assigned to task "+projectTaskName+" for project "+projectName+" assigned by "+userName,"Requested");
                    Toast.makeText(activity,"Task assigned to members",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(activity,"Task not assigned to members. Please try again later",Toast.LENGTH_LONG).show();
                }


                Intent pTSMCIntent = new Intent(activity, AssignedProjectTaskToUserActivity.class);
                activity.startActivity(pTSMCIntent);
            }



        });
    }

    public void sendNotificationsToUsers(String message,String requestType)
    {
        String result =null;
        result= createNotifications(userName, notificationMemberJArray.toString(),  "04",requestType,groupName,projectName,projectTaskName);
        if(result==null)
        {
            Toast.makeText(activity,"It seems that we cannot send notification to user now. Please unassign and reassign this task to members",Toast.LENGTH_LONG).show();

        }
        else if(result.equalsIgnoreCase("Record Inserted")) {
                for (int i = 0; i < notificationMemberJArray.length(); i++) {

                    try {
                        result = getOneSignalIdAttachedUserName(notificationMemberJArray.getString(i));
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            }


    }




    public void membersListVwNormalScrnSizeLT() {
        selectedFriendHViewLinearLayout.removeAllViews();
        try {

            selectedMemberArrayList = new ArrayList<String>();
            for (int i = 0; i < memberJSONArray.length(); i++) {
                memberJSONObject = new JSONObject(memberJSONArray.getString(i));
                String firstName =memberJSONObject.getString("firstName");
                String lastName =memberJSONObject.getString("lastName");
                String recieverUserName =memberJSONObject.getString("userName");
                LinearLayout outerLinearLayout = new LinearLayout(activity);
                LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                outerLinearLayoutLP.setMargins(30, 0, 0, 0);
                outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                outerLinearLayout.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout innerRelativeLayout = new RelativeLayout(activity);
                RelativeLayout.LayoutParams innerRelativeLayoutLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                innerRelativeLayout.setLayoutParams(innerRelativeLayoutLP);


                RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                TextView friendsNameTextView = new TextView(activity);
                llpChatPageContentDateTxtViewLayout.setMargins(0, 30, 0, 0);
                llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                friendsNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                friendsNameTextView.setText(firstName + " " +lastName );
                friendsNameTextView.setTextSize(18);
                friendsNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));

                CircleImageView profileImageVW = new CircleImageView(activity);
                RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(190, 190);
                llpInnerProfileImageVWLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                //profileImageVW.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                llpInnerProfileImageVWLayout.setMargins(70, 0, 0, 0);
                profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
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

                RelativeLayout.LayoutParams llpFunctionCheckBoxLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                llpFunctionCheckBoxLayout.setMargins(10, 30, 0, 0);
                llpFunctionCheckBoxLayout.addRule(RelativeLayout.RIGHT_OF,R.id.memberLogoId);
                final CheckBox allFunctionCheckBox = new CheckBox(activity);
                allFunctionCheckBox.setId(i);
                allFunctionCheckBox.setLayoutParams(llpFunctionCheckBoxLayout);
                allFunctionCheckBox.setTextColor(activity.getResources().getColor(R.color.color23));
                allFunctionCheckBox.setScaleX(.7f);
                allFunctionCheckBox.setScaleY(.7f);

                allFunctionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            JSONObject tempObject = new JSONObject(memberJSONArray.getString(allFunctionCheckBox.getId()));

                            if(isChecked)
                            {
                                selectedFriendsButton.setEnabled(true);

                                selectedMemberArrayList.add(tempObject.getString("userName"));
                                //  seletedMemberJSONArray.put(allFunctionCheckBox.getId(),tempObject.getString("userName"));

                            }
                            else
                            {
                                if(seletedMemberJSONArray.length()==0) {
                                    selectedFriendsButton.setEnabled(false);
                                }
                                selectedMemberArrayList.remove(tempObject.getString("userName"));



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                innerRelativeLayout.addView(profileImageVW);
                innerRelativeLayout.addView(allFunctionCheckBox);

                outerLinearLayout.addView(innerRelativeLayout);
                outerLinearLayout.addView(friendsNameTextView);
                selectedFriendHViewLinearLayout.addView(outerLinearLayout);


            }
        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void arrayListToJSOnArray()
    {
        seletedMemberJSONArray = new JSONArray();
        chatMemberArrayList = new ArrayList<String>();
        for(int i=0; i<selectedMemberArrayList.size(); i++)
        {
            seletedMemberJSONArray.put(selectedMemberArrayList.get(i).toString());
            chatMemberArrayList.add(selectedMemberArrayList.get(i).toString());
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

    public String createNotifications(String notiSenderName, String allNotiRecieverNamesList,  String entityId,String entityType, String groupName, String projectName, String projectTaskName)
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
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask", projectTaskName);

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

    public String assignTaskToSelectedMembers(String userName,String taskName,String projectName,String jsonList, String groupName )

    {
        String  result = null;
       String taskAssignedDate  = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            postDataParams.put("nameOfTask", taskName);
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("taskAssignedDate", taskAssignedDate);
            postDataParams.put("nameOfGroup", groupName);
            postDataParams.put("jsonList", jsonList);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inptatm/",postDataParams, activity,"text/plain", "application/json");
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
