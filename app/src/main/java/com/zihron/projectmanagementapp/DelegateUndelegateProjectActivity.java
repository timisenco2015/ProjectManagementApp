package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.shapeofview.shapes.TriangleView;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DelegateUndelegateProjectActivity extends AppCompatActivity {

    private TextView delegateProjectTextView;
    private TextView unDelegateProjectTextView;
    private TriangleView delegateProjectTriangleView;
    private TriangleView unDelegateProjectTriangleView;
    Typeface fontAwesomeIcon;
    AlertDialog.Builder alertDialogBuilder;
    private HttpRequestClass httpRequestClass;
    private TableLayout membersSelectedUnDelegateOuterTableLayout;
    private TableLayout membersSelectedDelegateOuterTableLayout;
    private DelegateProjectMemberTableDisplayClass delegateProjectMemberTableDisplayClass;
    private UnDelegateProjectMemberTableDisplayClass unDelegateProjectMemberTableDisplayClass;
    private S3ImageClass s3ImageClass;
    private Activity activity;
    private LinearLayout bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private RadioGroup friends_groups_RadioGroup;
    private LinearLayout groups_members_LinearLayout;
    private JSONArray memberListJSONArray;
    private String userName;
    private JSONArray selectedMemberJSONArray;
    private JSONArray finalSelectedMemberJSONArray;
    private String selectionType;
    private String projectName;
    private String groupName;
    private String lastName;
    private String firstName;
    private Button del_unDelMembersButton;
    private JSONArray searchListJSONArray;
    private JSONArray groupsJSONArray;
   private  DelegateProjectGroup_MembersClass  delegateProjectGroup_MembersClass;
   private UnDelegateGroup_MembersClass unDelegateGroup_MembersClass;
    private RelativeLayout openClosedBottomSheet;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private SharedPreferences sharedPreferences;
    private Button getSelectedMembersButton;
    private DelegateProjectToGroupsListClass delegateProjectToGroupsListClass;
    private String delegateType;
    private ArrayList<String> selectedMemberIndexArrayList;


    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ImageView refreshTableLayoutImageView;
    private SearchView friendGroupSearchView;
    private String defautGroup;
    private ProgressBarClass progressBarClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        delegateType = "delegate";
        setContentView(R.layout.activity_delegate_undelegate_project);
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        projectName  = sharedPreferences.getString("projectName", null);
        delegateProjectTextView = (TextView) findViewById(R.id.delegateProjectTxtVWId);
        unDelegateProjectTextView = (TextView) findViewById(R.id.unDelegateProjectTxtVWId);
        delegateProjectTriangleView = (TriangleView) findViewById(R.id.delegateProjectTriangleVWId);
        unDelegateProjectTriangleView = (TriangleView) findViewById(R.id.unDelegateProjectTriangleVWId);
        membersSelectedDelegateOuterTableLayout = (TableLayout) findViewById(R.id.members_Delegate_SelectedOuterLayoutId);
        membersSelectedUnDelegateOuterTableLayout = (TableLayout) findViewById(R.id.members_Undelegate_SelectedOuterLayoutId);
        friendGroupSearchView = (SearchView) findViewById(R.id.friendGroupSearchVWId);
        del_unDelMembersButton = (Button) findViewById(R.id.Del_UnDelMembersBtnId);
        groups_members_LinearLayout = (LinearLayout) findViewById(R.id.groups_members_LinearLTId);
        getSelectedMembersButton = (Button) findViewById(R.id.getSelectedMemebersBtnId);
        bottomSheetLayout = (LinearLayout) findViewById(R.id.bottom_sheet);

        // friends_groups_RadioGroup = (RadioGroup) findViewById(R.id.friends_groups_RadioGrpId);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        openClosedBottomSheet = (RelativeLayout) findViewById(R.id.close_open_bottomSheetId);
        delegateGroupAndFriendsDisplay();
         delegateProjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delegateGroupAndFriendsDisplay();
            }
        });


        unDelegateProjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unDelegateGroupAndFriendsDisplay();

            }
        });

    }

    public void backToViewProject(View view)
    {
        Intent dUPAIntent = new Intent(DelegateUndelegateProjectActivity.this, ViewProjectActivity.class);
        startActivity(dUPAIntent);
    }

    public void delegateGroupAndFriendsDisplay()
    {
        delegateProjectTextView.setTextColor(getResources().getColor(R.color.color13));
        unDelegateProjectTextView.setTextColor(getResources().getColor(R.color.color15));
        delegateProjectTriangleView.setAlpha(1.0f);
        unDelegateProjectTriangleView.setAlpha(0.0f);
        membersSelectedUnDelegateOuterTableLayout.setVisibility(View.GONE);
        membersSelectedDelegateOuterTableLayout.setVisibility(View.VISIBLE);
        groups_members_LinearLayout.removeAllViews();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        String result = getNotProjectDelegatedGroups();
        new  DelegateProjectToGroupsListClass(getSelectedMembersButton, friendGroupSearchView,del_unDelMembersButton, groups_members_LinearLayout, activity,  membersSelectedDelegateOuterTableLayout, result, bottomSheetBehavior);

    }

    public void unDelegateGroupAndFriendsDisplay()
    {
        delegateProjectTextView.setTextColor(getResources().getColor(R.color.color15));
        unDelegateProjectTextView.setTextColor(getResources().getColor(R.color.color13));
        delegateProjectTriangleView.setAlpha(0.0f);
        unDelegateProjectTriangleView.setAlpha(1.0f);
        membersSelectedUnDelegateOuterTableLayout.setVisibility(View.VISIBLE);
        membersSelectedDelegateOuterTableLayout.setVisibility(View.GONE);
        groups_members_LinearLayout.removeAllViews();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        String result = getProjectDelegatedGroups();
        new UnDelegateProjectToGroupsListClass(getSelectedMembersButton, friendGroupSearchView, del_unDelMembersButton, groups_members_LinearLayout, activity, membersSelectedUnDelegateOuterTableLayout, result, bottomSheetBehavior);

    }


    @Override
    public void onBackPressed() {

    }






    public String getNotProjectDelegatedGroups()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fgagtd/",postDataParams, activity,"application/json", "application/json");
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

            else if (result==null)
            {


                alertDialogBuilder =   new AlertDialog.Builder(activity.getApplicationContext());
                alertDialogBuilder.setTitle("Delete entry")
                        .setMessage(Html.fromHtml("Two Scenarios for this error"+"<br>"+"Case 1): It seems that you have delegated this project to all groups" + " <br>"+"Case 2): You are yet to create any group for this project" +
                                "<br><br>"+ "For case1, please click cancel. For case2, please click okay"))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent (activity, ProjectMembersGroupInviteActivity.class);
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
