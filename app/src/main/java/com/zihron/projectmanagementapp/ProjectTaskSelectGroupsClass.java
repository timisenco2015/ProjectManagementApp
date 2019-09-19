package com.zihron.projectmanagementapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.PolygonView;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectTaskSelectGroupsClass {

    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
    private    ObjectAnimator assignedRotateLayoutObject;
    private    ObjectAnimator unAssignedRotateLayoutObject;
    private String projectName;
    private String projectTaskName;
    private Activity activity;


    private SharedPreferences sharedPreferences;

    public ProjectTaskSelectGroupsClass(final Activity activity, PolygonView assignedGrpSwitchButton, PolygonView unAssignedGrpSwitchButton
                                        ,final LinearLayout allGroupsLinearLayout, final LinearLayout allGroupsMembersLinearlayout)
    {


        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName =  sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        this.asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        assignedRotateLayoutObject = ObjectAnimator.ofFloat(assignedGrpSwitchButton, "rotation", 0f,360f);
        assignedRotateLayoutObject.setDuration(5000);
        assignedRotateLayoutObject.setRepeatCount(ValueAnimator.INFINITE);
        assignedRotateLayoutObject.start();
        this.activity = activity;
        unAssignedRotateLayoutObject = ObjectAnimator.ofFloat(unAssignedGrpSwitchButton, "rotation", 0f,360f);
        unAssignedRotateLayoutObject.setDuration(5000);
        unAssignedRotateLayoutObject.setRepeatCount(ValueAnimator.INFINITE);
        String result = getGroupTaskNotAssigned(projectName, projectTaskName);
       new ProjectTaskSelectNotAssignedGrpSubClass(activity, allGroupsLinearLayout, allGroupsMembersLinearlayout,result);
        //assignedGrpSwitchButton,unAssignedGrpSwitchButton,
        assignedGrpSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assignedRotateLayoutObject.start();
                unAssignedRotateLayoutObject.cancel();
                String result = getGroupTaskNotAssigned(projectName, projectTaskName);
                if(result!=null) {
                    new ProjectTaskSelectNotAssignedGrpSubClass(activity, allGroupsLinearLayout,   allGroupsMembersLinearlayout,result);
                }
                else
                {
                    Toast.makeText(activity,"There is no group assigned to this task yet",Toast.LENGTH_LONG).show();
                }


            }
        });
        unAssignedGrpSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                assignedRotateLayoutObject.cancel();
                unAssignedRotateLayoutObject.start();
               String result = getGroupTaskAssigned(projectName, projectTaskName);
               if(result!=null) {
                  new ProjectTaskSelectAssignedGrpSubClass(activity, allGroupsLinearLayout,  allGroupsMembersLinearlayout,result);
               }
               else
               {
                   Toast.makeText(activity,"There is no group assigned to this task yet",Toast.LENGTH_LONG).show();
               }
            }
        });

    }

    public String getGroupTaskAssigned(String projectName, String projectTaskName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask",projectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuagta/",postDataParams, activity,"application/json", "application/json");
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

    public String getGroupTaskNotAssigned(String projectName,String projectTaskName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask", projectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fgpagfta/",postDataParams, activity,"application/json", "application/json");
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
