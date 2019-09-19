package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.florent37.shapeofview.shapes.TriangleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class ProjectMembersGroupInviteActivity extends AppCompatActivity {
    private TextView projectAddGrpMbrTextView;
    private TextView projectGroupTextView;
    private TriangleView projectAddGrpMbrTriangleView;
    private TriangleView projectAddGroupTriangleView;
    private TriangleView projectRmvGrpMbrTriangleView;
private Spinner groupLevelSpinner;
    private String selectedType;
    private LinearLayout membersRemoveLinearLayout;
    private LinearLayout  groupsCreateLinearLayout;
    private EditText inviteMembersEditText;
    private LinearLayout inviteMembersLLTButton;
    private ValidationClass validationClass;
    private LinearLayout editTextUsernameLinearLayout;
    private Activity activity;
    private String  projectName;
    private  ArrayList<String>  projectLevelArray;
    private LinearLayout groupListLinearLayout;
    private JSONArray projectMembersJSONArray;
    private LinearLayout membersListLinearLayout;
    private TextView projectRmvGrpMbrTextView;
    private LinearLayout groupsMembersLinearLayout;
    private LinearLayout selectedMemberHViewLinearlayout;
    private SharedPreferences sharedPreferences;
    private ImageButton newGroupButtonClick;
    private EditText groupNameEditText;
    private String userName;
    Typeface fontAwesomeIcon;
    private ListView projectMembersListView;
    private AutoCompleteTextView groupMemberAutoCompleteTextView;
    private AutoCompleteTextView groupRemoveAutoCompleteTextView;
    private AutoCompleteTextView groupAutoCompleteTextView;
    private LinearLayout removeMembersBtnLLT;
    private LinearLayout addMembersBtnLLT;
    private LinearLayout removeFriendsFrmGrpLinearLayout;
    private AutoCompleteTextView groupMbrsAddAutoCompleteTextView;
    private LinearLayout addFriendsToGrpLinearLayout;
    private LinearLayout addGroupMembersBtnLinearlayout;
    private AutoCompleteTextView groupMbrsRemoveAutoCompleteTextView;
    private LinearLayout groupsMembersAddLinearLayout;
    private LinearLayout groupsMembersRemoveLinearLayout;
    private RadioGroup groupAddSwitchRadioGroup;
    private RadioGroup groupRemoveSwitchRadioGroup;
   private AutoCompleteTextView deleteGrpAutoCompleteTextView;
    private ImageButton delProjectGrpLLTImageButton;
    private ImageView deleteProjectGrpButton;
    private TextView closeDelGrpTextView;
    private LinearLayout deleteGroupLayout;
    private String delegateProjectDefaultGroup;
    private static PopupWindow popUp;
    private String firstName;
    private String lastName;
private String defautGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_members_group_invite);
        activity = this;
delegateProjectDefaultGroup = getIntent().getStringExtra("defaultGroup");
        projectAddGrpMbrTextView = (TextView) findViewById(R.id.projectAddGrpMbrTxtVWId);
        projectGroupTextView = (TextView) findViewById(R.id.projectGroupTxtVWId);
        deleteProjectGrpButton = (ImageView) findViewById(R.id.deleteProjectGrpBtnId);
        closeDelGrpTextView = (TextView) findViewById(R.id.closeDelGrpLLTId);
deleteGrpAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.dltGrpAutoCompleteTtVWId);
        projectRmvGrpMbrTextView = (TextView)findViewById(R.id.projectRmvGrpMbrTxtVWId);
        projectAddGrpMbrTriangleView = (TriangleView) findViewById(R.id.projectAddGrpMbrTriangleVWId);
        projectRmvGrpMbrTriangleView = (TriangleView)findViewById(R.id.projectRmvGrpMbrTriangleVWId);
        projectAddGroupTriangleView = (TriangleView) findViewById(R.id.projectAddGrpTriangleVWId);
       groupsCreateLinearLayout = (LinearLayout)findViewById(R.id.groupsCreateLLTId);
        delProjectGrpLLTImageButton = (ImageButton)findViewById(R.id.delProjectGrpLLTImageBtnId);
        membersListLinearLayout = (LinearLayout) findViewById(R.id. membersListLLTId);
        selectedMemberHViewLinearlayout = (LinearLayout)findViewById(R.id.selectedMemberHViewLLTId);
        newGroupButtonClick = (ImageButton)findViewById(R.id.newGroupButtonClickId);
        deleteGroupLayout = (LinearLayout) findViewById(R.id.deleteGroupLayoutId);
        groupNameEditText = (EditText) findViewById(R.id.groupNameEditTxtId);
        removeMembersBtnLLT = (LinearLayout)findViewById(R.id.removeMembersBtnLLTId);
        groupLevelSpinner = (Spinner)findViewById(R.id.groupLevelSpinnerId);
        removeFriendsFrmGrpLinearLayout = (LinearLayout)findViewById(R.id.removeFriendsFrmGrpLLTId);
        groupMbrsAddAutoCompleteTextView  = (AutoCompleteTextView) findViewById(R.id.groupMbrsAddAutoCompleteTtVWId);
        addFriendsToGrpLinearLayout = (LinearLayout)findViewById(R.id.addFriendsToGrpLLTId);
        addGroupMembersBtnLinearlayout = (LinearLayout)findViewById(R.id.addGroupMembersBtnLLTId);
       groupsMembersAddLinearLayout = (LinearLayout)findViewById(R.id.groupsMembersAddLLTId);
       groupsMembersRemoveLinearLayout = (LinearLayout)findViewById(R.id.groupsMembersRemoveLLTId);
        groupAddSwitchRadioGroup = (RadioGroup)findViewById(R.id.groupAddSwitchRadioGrpId);
        groupRemoveSwitchRadioGroup = (RadioGroup)findViewById(R.id.groupRemoveSwitchRadioGrpId);
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");
        groupMbrsRemoveAutoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.groupMbrsRemoveAutoCompleteTtVWId);
       // groupsCreateScrollView.setClickable(false);
        deleteGroupLayout.setVisibility(View.GONE);

        validationClass = new ValidationClass();
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectName = sharedPreferences.getString("projectName", null);
        userName =sharedPreferences.getString("userName", null);
        firstName = sharedPreferences.getString("firstName", null);
        lastName = sharedPreferences.getString("lastName", null);
        defautGroup=sharedPreferences.getString("projectDefaultGroup", null);

        displayAddMembersToGroupLayout();

        projectRmvGrpMbrTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectAddGrpMbrTextView.setTextColor(getResources().getColor(R.color.color15));
                projectGroupTextView.setTextColor(getResources().getColor(R.color.color15));
                projectRmvGrpMbrTextView.setTextColor(getResources().getColor(R.color.color13));
                projectAddGroupTriangleView.setAlpha(0.0f);
                projectRmvGrpMbrTriangleView.setAlpha(1.0f);
                projectAddGrpMbrTriangleView.setAlpha(0.0f);
                deleteGroupLayout.setVisibility(View.GONE);
                groupsMembersAddLinearLayout.setVisibility(View.GONE);
                groupsCreateLinearLayout.setVisibility(View.GONE);
                groupsMembersRemoveLinearLayout.setVisibility(View.VISIBLE);
                new ProjectGroupRemoveMemberClass(activity, projectName,userName,firstName, lastName,groupMbrsRemoveAutoCompleteTextView,removeFriendsFrmGrpLinearLayout,removeMembersBtnLLT,groupRemoveSwitchRadioGroup,defautGroup);
            }
        });




        projectAddGrpMbrTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                projectAddGrpMbrTextView.setTextColor(getResources().getColor(R.color.color13));
                projectGroupTextView.setTextColor(getResources().getColor(R.color.color15));
                projectRmvGrpMbrTextView.setTextColor(getResources().getColor(R.color.color15));
                projectAddGroupTriangleView.setAlpha(0.0f);
                deleteGroupLayout.setVisibility((View.GONE));
                projectAddGrpMbrTriangleView.setAlpha(1.0f);
                projectRmvGrpMbrTriangleView.setAlpha(0.0f);
                selectedType ="Member";


                groupsMembersAddLinearLayout.setVisibility(View.VISIBLE);
                groupsCreateLinearLayout.setVisibility(View.GONE);
                groupsMembersRemoveLinearLayout.setVisibility(View.GONE);
               new ProjectGroupAddMemberClass(activity, projectName,userName,firstName, lastName,groupMbrsAddAutoCompleteTextView, addFriendsToGrpLinearLayout,addGroupMembersBtnLinearlayout,groupAddSwitchRadioGroup,defautGroup);

            }
        });



        projectGroupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupListLinearLayout = (LinearLayout) findViewById(R.id.groupListLLTId);

                projectAddGrpMbrTextView.setTextColor(getResources().getColor(R.color.color15));
                projectGroupTextView.setTextColor(getResources().getColor(R.color.color13));
                projectRmvGrpMbrTextView.setTextColor(getResources().getColor(R.color.color15));
                projectAddGroupTriangleView.setAlpha(1.0f);
                deleteGroupLayout.setVisibility((View.GONE));
                projectRmvGrpMbrTriangleView.setAlpha(0.0f);
                projectAddGrpMbrTriangleView.setAlpha(0.0f);
                selectedType ="Group";


                groupsMembersAddLinearLayout.setVisibility(View.GONE);
                groupsCreateLinearLayout.setVisibility(View.VISIBLE);
                groupsMembersRemoveLinearLayout.setVisibility(View.GONE);
                new ProjectGroupCreateClass(activity, projectName,userName,firstName, lastName,membersListLinearLayout,selectedMemberHViewLinearlayout, newGroupButtonClick, groupNameEditText,groupLevelSpinner,projectLevelArray);



            }
        });

        delProjectGrpLLTImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroupLayout.setVisibility((View.VISIBLE));
                new DeleteProjectGroupClass(activity, projectName,userName,firstName, lastName, deleteGrpAutoCompleteTextView, deleteProjectGrpButton,deleteGroupLayout);
                closeDelGrpTextView.setTypeface( fontAwesomeIcon);

                closeDelGrpTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteGroupLayout.setVisibility((View.GONE));
                    }
                });

            }
        });

         projectLevelArray = new ArrayList<String>();
        projectLevelArray.add("Task Assign");
        projectLevelArray.add("Supervisor");
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(activity, R.layout.custom_textview_to_spinner,  projectLevelArray);
        adapter.setDropDownViewResource(R.layout.custom_textview_to_spinner);
        groupLevelSpinner.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {

    }
    public void displayAddMembersToGroupLayout()
    {
        projectAddGrpMbrTextView.setTextColor(getResources().getColor(R.color.color13));
        projectGroupTextView.setTextColor(getResources().getColor(R.color.color15));
        projectRmvGrpMbrTextView.setTextColor(getResources().getColor(R.color.color15));
        projectAddGroupTriangleView.setAlpha(0.0f);
        deleteGroupLayout.setVisibility((View.GONE));
        projectAddGrpMbrTriangleView.setAlpha(1.0f);
        projectRmvGrpMbrTriangleView.setAlpha(0.0f);
        selectedType ="Member";


        groupsMembersAddLinearLayout.setVisibility(View.VISIBLE);
        groupsCreateLinearLayout.setVisibility(View.GONE);
        groupsMembersRemoveLinearLayout.setVisibility(View.GONE);
       new ProjectGroupAddMemberClass(activity, projectName,userName,firstName,lastName,groupMbrsAddAutoCompleteTextView, addFriendsToGrpLinearLayout,addGroupMembersBtnLinearlayout,groupAddSwitchRadioGroup,defautGroup);

    }

public void backToViewProject(View view)
{
    Intent pMGIAIntent = new Intent(ProjectMembersGroupInviteActivity.this,ViewProjectActivity.class);
    startActivity(pMGIAIntent);
}










}

