package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectTaskSelectMembersClass {

    private Activity activity;
    private LinearLayout selectedMembersHViewLinearLayout;
    private HttpRequestClass httpRequestClass;
    private Button selectedMembersButton;
    Typeface fontAwesomeIcon;
    private String userName;
    private String projectName;
    private LinearLayout assignedMembersHViewLLinearLayout;
    private String projectTaskName;
    private Button removedFriendsButton;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private SharedPreferences sharedPreferences;

    public ProjectTaskSelectMembersClass(final Activity activity, LinearLayout selectedMembersHViewLinearLayout, Button selectedMembersButton, LinearLayout assignedMembersHViewLLinearLayout, final Button removedMemberssButton) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName =  sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        userName = sharedPreferences.getString("userName", null);
        this.projectTaskName = projectTaskName;
        this.userName = userName;

        this.removedFriendsButton = removedFriendsButton;
        this.selectedMembersHViewLinearLayout = selectedMembersHViewLinearLayout;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.assignedMembersHViewLLinearLayout = assignedMembersHViewLLinearLayout;
        fontAwesomeIcon = Typeface.createFromAsset(activity.getAssets(), "font/fontawesome-webfont.ttf");
        this.selectedMembersButton = selectedMembersButton;



        /*
         */
        String  result = getMembersForTaskAssigned(projectName, projectTaskName);

        if(result!=null)
        {
           new ProjectTaskSelectMembersNotAssignedClass(selectedMembersHViewLinearLayout, selectedMembersButton,  activity,result);
        }
        else if(result==null)
        {
            Toast.makeText(activity, "No members to assigned this project task", Toast.LENGTH_LONG).show();
        }


        result =getMembersTaskAssigned(projectName, projectTaskName);
        if(result!=null)
        {
            new ProjectTaskSelectMembersAssignedClass(assignedMembersHViewLLinearLayout, removedMemberssButton,  activity,result);
        }
        else if(result==null)
        {
            Toast.makeText(activity, "It seems that you have not assigned this task to any member", Toast.LENGTH_LONG).show();
        }

    }




    public String getMembersForTaskAssigned(String projectName, String projectTaskName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask",projectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuafdfta/",postDataParams, activity,"application/json", "application/json");
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



    public String getMembersTaskAssigned(String projectName, String projectTaskName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask",projectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuamdta/",postDataParams, activity,"application/json", "application/json");
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