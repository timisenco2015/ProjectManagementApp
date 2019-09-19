package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProjectTaskSelectAssignedGrpSubMembersClass {

    private LinearLayout allGroupsMembersLinearlayout;
    private Activity activity;
    private JSONArray allGroupMembersJSONArray;
   private ArrayList<JSONObject> selectedGroupMembersJSONArray;
   private JSONArray notificationJSONArray;
    private HttpRequestClass httpRequestClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String userName;
    private String projectName;
    private String projectTaskName;
    private String groupName;
    private JSONArray selectedIndexJArray;
    private SharedPreferences sharedPreferences;
    public ProjectTaskSelectAssignedGrpSubMembersClass(final Activity activity, LinearLayout allGroupsMembersLinearlayout )
    {

        this.allGroupsMembersLinearlayout=allGroupsMembersLinearlayout;
        this.activity = activity;


        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName =  sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        userName = sharedPreferences.getString("userName", null);
        groupName = sharedPreferences.getString("projectDefaultGroup", null);

    }



    public void addGroupAllMembers( String result) {
        try {

            if(result!=null) {
                allGroupMembersJSONArray = new JSONArray(result);
                selectedGroupMembersJSONArray = new ArrayList<JSONObject>();


        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                generateGroupAllMembers();
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
            }
            else
            {
                Toast.makeText(activity,"No members to display",Toast.LENGTH_LONG).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void generateGroupAllMembers()
    {
        LinearLayout outerOuterLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterLinearLayoutLP.setMargins(5, 0, 0, 5);
        outerOuterLinearLayout.setLayoutParams(outerOuterLinearLayoutLP);
        outerOuterLinearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout outerOuterInnerLinearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams outerOuterInnerLinearLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        outerOuterInnerLinearLP.setMargins(30, 0, 0, 0);
        outerOuterInnerLinearLayout.setLayoutParams(outerOuterInnerLinearLP);
        outerOuterInnerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);



        for (int i = 0; i < allGroupMembersJSONArray.length(); i++) {



            LinearLayout outerLinearLayout = new LinearLayout(activity);
            LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT);
            outerLinearLayoutLP.setMargins(30, 0, 30, 0);
            outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
            outerLinearLayout.setOrientation(LinearLayout.VERTICAL);



            String firstName = null;
            String lastName = null;
            String requestStatus= null;

            JSONObject tempJObject = null;
            try {
                tempJObject = new JSONObject(allGroupMembersJSONArray.get(i).toString() );

                firstName = tempJObject.getString("firstName");
                lastName = tempJObject.getString("lastName");
               requestStatus=  tempJObject.getString("viewedStatus");

            } catch (JSONException e) {
                e.printStackTrace();
            }


            RelativeLayout.LayoutParams friendsNameTextViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView friendsNameTextView = new TextView(activity);
            friendsNameTextViewLLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
            friendsNameTextView.setLayoutParams(friendsNameTextViewLLP);
            friendsNameTextView.setText(firstName + " " +lastName);
            friendsNameTextView.setTextSize(18);
            friendsNameTextView.setTextColor(activity.getResources().getColor(R.color.color23));


            RelativeLayout.LayoutParams llpFunctionCheckBoxLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            llpFunctionCheckBoxLayout.setMargins(10, 30, 0, 0);
            final CheckBox allFunctionCheckBox = new CheckBox(activity);
            allFunctionCheckBox.setId(i);
            allFunctionCheckBox.setGravity(Gravity.CENTER_HORIZONTAL);
            allFunctionCheckBox.setLayoutParams(llpFunctionCheckBoxLayout);
            allFunctionCheckBox.setTextColor(activity.getResources().getColor(R.color.color23));
            allFunctionCheckBox.setScaleX(.7f);
            allFunctionCheckBox.setScaleY(.7f);
            allFunctionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     int index = buttonView.getId();

                    JSONObject tempJObject= null;


                    try {

                        if(isChecked)
                        {
                            tempJObject = new JSONObject(allGroupMembersJSONArray.getString(index));
                            tempJObject.remove("firstName");
                            tempJObject.remove("lastName");
                            tempJObject.remove("viewedStatus");
                            selectedGroupMembersJSONArray.add(tempJObject);

                         }
                        else
                        {
                            selectedGroupMembersJSONArray.remove(tempJObject);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            });


            RelativeLayout.LayoutParams llpStatusTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView statusTextView = new TextView(activity);
            llpStatusTxtViewLayout.setMargins(15, 20, 0, 0);
            statusTextView.setLayoutParams(llpStatusTxtViewLayout);
            statusTextView.setText(requestStatus);
            statusTextView.setTextSize(15);
            statusTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            statusTextView.setElevation(70);
            statusTextView.setTextColor(activity.getResources().getColor(R.color.color23));



            outerLinearLayout.addView(allFunctionCheckBox);
            outerLinearLayout.addView(friendsNameTextView);

            if(requestStatus.equalsIgnoreCase("Pending"))
            {
                outerLinearLayout.addView(statusTextView);
            }





            outerOuterInnerLinearLayout.addView(outerLinearLayout);

        }


        Button assignedButton = new Button(activity);
        RelativeLayout.LayoutParams buttonRelativeLayoutLLP = new RelativeLayout.LayoutParams(380,180);
        assignedButton.setLayoutParams(buttonRelativeLayoutLLP);
        assignedButton.setText("UnAssigned");
        assignedButton.setTextSize(14);
        buttonRelativeLayoutLLP.setMargins(0,80,0,0);

        assignedButton.setBackgroundColor(activity.getResources().getColor(R.color.color45));
        assignedButton.setTextColor(activity.getResources().getColor(R.color.color15));
        assignedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String result = assignTaskToSelectedGroupMembers(selectedGroupMembersJSONArray.toString(),projectTaskName, projectName, userName );

                if(result.equalsIgnoreCase("Record Not Deleted"))
                {
                    Toast.makeText(activity,"unable to deactivate members from this project probably due to network. Please try again later",Toast.LENGTH_LONG).show();
                }
                else
                {

                    sendNotificationsToUsers("You have been unassigned from task "+projectTaskName+" for project "+projectName+" unAssigned by "+userName,"Removed");
                    Toast.makeText(activity,"Selected members have been deactivated from this project",Toast.LENGTH_LONG).show();

                }

                Intent pTSNAGSCIntent = new Intent(activity, AssignedProjectTaskToUserActivity.class);
                activity.startActivity(pTSNAGSCIntent);
            }
        });


        outerOuterLinearLayout.addView(outerOuterInnerLinearLayout);
        outerOuterLinearLayout.addView(assignedButton);
        allGroupsMembersLinearlayout.addView(outerOuterLinearLayout);



    }





    public void sendNotificationsToUsers(String message,String requestType)
    {
        String result =null;
        result= createNotifications(userName,  selectedGroupMembersJSONArray.toString(),  "04",requestType,groupName,projectName,projectTaskName);
        if(result==null)
        {
            Toast.makeText(activity,"It seems that we cannot send notification to user now. Please unassign and reassign this task to members",Toast.LENGTH_LONG).show();

        }
        else if(result.equalsIgnoreCase("Record Inserted")) {
            for (int i = 0; i <  selectedGroupMembersJSONArray.size(); i++) {

                try {
                    JSONObject tempObject =  selectedGroupMembersJSONArray.get(i);
                    result = getOneSignalIdAttachedUserName( tempObject.getString("userName"));
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"'}, 'include_player_ids':" + result + ",'headings':{'en':'Zihron'},'small_icon':'notificationbackgroundimage','android_sound':'soundnotification','android_accent_color':'FF00FF00','priority':10,'android_visibility':1}"), null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }







    public String assignTaskToSelectedGroupMembers(String jsonList,String taskName,String projectName,  String userName )

    {
        String  result = null;
        String taskAssignedDate  = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("nameOfProjectTask", taskName);
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("taskAssignedDate", taskAssignedDate);
            postDataParams.put("jsonList", jsonList);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/duftag/",postDataParams, activity,"text/plain", "application/json");
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
