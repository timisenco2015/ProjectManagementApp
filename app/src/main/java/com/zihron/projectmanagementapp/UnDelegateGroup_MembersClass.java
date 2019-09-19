package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class UnDelegateGroup_MembersClass {

  private HttpRequestClass httpRequestClass;
  private String projectName, groupName;
  private AlertDialog alertDialog;
  AlertDialog.Builder alertDialogBuilder;
  private Activity activity;
  private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
  private UnDelegateProjectMembersClass unDelegateProjectMembersClass;
  private UnDelegateProjectToGroupsListClass unDelegateProjectToGroupsListClass;
  private SharedPreferences sharedPreferences;


  public UnDelegateGroup_MembersClass(final TableLayout membersSelectedDelegateOuterTableLayout, RelativeLayout openClosedBottomSheet, final BottomSheetBehavior bottomSheetBehavior, RadioGroup friends_groups_RadioGroup, final Button getSelectedMembersButton, final SearchView friendGroupSearchView, final Button del_unDelMembersButton, final LinearLayout groups_members_LinearLayout, final Activity activity) {
    sharedPreferences = activity.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
    projectName = sharedPreferences.getString("projectName", null);
    groupName = sharedPreferences.getString("projectDefaultGroup", null);
    this.activity = activity;
    openClosedBottomSheet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
          bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
          bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

      }
    });


    friends_groups_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (checkedId == R.id.friendsRadioBtnId) {

          String result = getProjectDelegatedMembers();

                    unDelegateProjectMembersClass = new UnDelegateProjectMembersClass(getSelectedMembersButton, friendGroupSearchView, del_unDelMembersButton, groups_members_LinearLayout, activity, membersSelectedDelegateOuterTableLayout, result, bottomSheetBehavior);
        } else if (checkedId == R.id.groupsRadioBtnId) {
          String result = getProjectDelegatedGroups();
          unDelegateProjectToGroupsListClass = new UnDelegateProjectToGroupsListClass(getSelectedMembersButton, friendGroupSearchView, del_unDelMembersButton, groups_members_LinearLayout, activity, membersSelectedDelegateOuterTableLayout, result, bottomSheetBehavior);

        }
      }

    });

  }

  public String getProjectDelegatedMembers() {
    String result = null;
    JSONObject postDataParams = new JSONObject();
    try {
      postDataParams.put("nameOfProject", projectName);
      httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuamapd/", postDataParams, activity, "application/json", "application/json");
      result = httpRequestClass.getResult();
      asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
      if (result != null) {
        if (result.equalsIgnoreCase("exception")) {
          asyncErrorDialogDisplay.handleException(activity);
        } else if (result.equalsIgnoreCase("No network")) {
          Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
        } else if (android.text.TextUtils.isDigitsOnly(result)) {
          asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
        }
      } else if (result == null) {


        alertDialogBuilder = new AlertDialog.Builder(activity.getApplicationContext());
        alertDialogBuilder.setTitle("Delete entry")
                .setMessage(Html.fromHtml("Two Scenarios for this error" + "<br>" + "Case 1): It seems that you have delegated this project to all members" + " <br>" + "Case 2): You are yet to invite members to this group" +
                        "<br><br>" + "For case1, please click cancel. For case2, please click okay"))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(activity, ProjectMembersGroupInviteActivity.class);
                    activity.startActivity(intent);
                  }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                  }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        alertDialog = alertDialogBuilder.create();
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return result;
  }


  public String getProjectDelegatedGroups() {
    String result = null;
    JSONObject postDataParams = new JSONObject();
    try {
      postDataParams.put("nameOfProject", projectName);
      httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpagd/", postDataParams, activity, "application/json", "application/json");
      result = httpRequestClass.getResult();
      asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
      if (result != null) {
        if (result.equalsIgnoreCase("exception")) {
          asyncErrorDialogDisplay.handleException(activity);
        } else if (result.equalsIgnoreCase("No network")) {
          Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
        } else if (android.text.TextUtils.isDigitsOnly(result)) {
          asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
        }
      } else if (result == null) {


        alertDialogBuilder = new AlertDialog.Builder(activity.getApplicationContext());
        alertDialogBuilder.setTitle("Delete entry")
                .setMessage(Html.fromHtml("Two Scenarios for this error" + "<br>" + "Case 1): It seems that you have delegated this project to all groups" + " <br>" + "Case 2): You are yet to create any group for this project" +
                        "<br><br>" + "For case1, please click cancel. For case2, please click okay"))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(activity, ProjectMembersGroupInviteActivity.class);
                    activity.startActivity(intent);
                  }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                  }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        alertDialog = alertDialogBuilder.create();
      }


    } catch (JSONException e) {
      e.printStackTrace();
    }

    return result;
  }
}