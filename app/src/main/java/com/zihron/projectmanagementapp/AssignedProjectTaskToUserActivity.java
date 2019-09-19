package com.zihron.projectmanagementapp;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.florent37.shapeofview.shapes.PolygonView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignedProjectTaskToUserActivity extends AppCompatActivity {




private Activity activity;
    private SharedPreferences sharedPreferences;
    private String userName;
private String projectTaskName;
private String projectName;
    private TextView switchToGroupTextView;
    private RelativeLayout groupTaskAssignedRelativeLayout;
    private RelativeLayout friendTaskAssignedRelativeLayout;
    private VideoView backgroundVideView;
    private TextView switchToFriendTextView;
    private LinearLayout selectedFriendHViewLinearLayout;
    private Button selectedFriendsButton;
    private LinearLayout assignedMembersHViewLLinearLayout;
private Button removedFriendsButton;
private PolygonView assignedGrpSwitchButton;
private PolygonView unAssignedGrpSwitchButton;
private LinearLayout allGroupsLinearLayout;
private LinearLayout allGroupsMembersLinearlayout;
private ProjectTaskSelectGroupsClass projectTaskSelectGroupsClass;
private    ObjectAnimator rotateLayoutObject;
    private String defaultGroupName;
    private ProjectTaskSelectMembersClass projectTaskSelectMembersClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assigned_project_task_to_user);
        activity = this;

switchToGroupTextView = (TextView)findViewById(R.id.switchToGroupTxtVWId);
        switchToFriendTextView = (TextView)findViewById(R.id.switchToFriendTxtVWId);
        switchToGroupTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        switchToFriendTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        backgroundVideView = (VideoView) findViewById(R.id.backgroundVideViewId);

        groupTaskAssignedRelativeLayout = (RelativeLayout)findViewById(R.id.groupTaskAssignedLLTId);
        friendTaskAssignedRelativeLayout = (RelativeLayout)findViewById(R.id.friendTaskAssignedLLTId);
        selectedFriendHViewLinearLayout = (LinearLayout) findViewById(R.id.selectedFriendHViewLLTId);
        selectedFriendsButton = (Button)findViewById(R.id.selectedFriendssBtnId);
        assignedMembersHViewLLinearLayout = (LinearLayout)findViewById(R.id.assignedMembersHViewLLTId);
         removedFriendsButton = (Button)findViewById(R.id.removedFriendssBtnId);
        allGroupsLinearLayout = (LinearLayout)findViewById(R.id.allGroupsLLTId);
         allGroupsMembersLinearlayout = (LinearLayout) findViewById(R.id.allGroupsMembersLLTId);
       assignedGrpSwitchButton = (PolygonView) findViewById(R.id.assignedGrpSwitchBtnId);
        unAssignedGrpSwitchButton = (PolygonView) findViewById(R.id.unAssignedGrpSwitchBtnId);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE );
        userName = sharedPreferences.getString("userName", null);
        projectName = sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        defaultGroupName=sharedPreferences.getString("projectDefaultGroup", null);


        friendTaskAssignedRelativeLayout.animate().translationX(0);
        groupTaskAssignedRelativeLayout.animate().translationX(1500);

        projectTaskSelectMembersClass = new ProjectTaskSelectMembersClass(activity, selectedFriendHViewLinearLayout, selectedFriendsButton,assignedMembersHViewLLinearLayout,removedFriendsButton);
        switchToFriendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendTaskAssignedRelativeLayout.animate().translationX(0).setDuration(300);
                groupTaskAssignedRelativeLayout.animate().translationX(1500).setDuration(300);
                projectTaskSelectMembersClass = new ProjectTaskSelectMembersClass(activity, selectedFriendHViewLinearLayout, selectedFriendsButton,assignedMembersHViewLLinearLayout, removedFriendsButton);
            }
        });

        switchToGroupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendTaskAssignedRelativeLayout.animate().translationX(-1500).setDuration(300);
                groupTaskAssignedRelativeLayout.animate().translationX(0).setDuration(300);
                projectTaskSelectGroupsClass= new ProjectTaskSelectGroupsClass(activity, assignedGrpSwitchButton,unAssignedGrpSwitchButton, allGroupsLinearLayout,  allGroupsMembersLinearlayout);

            }
        });


        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier("biostorm", "raw", pkgName);
        backgroundVideView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));

        backgroundVideView.start();
        backgroundVideView.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                backgroundVideView.start();
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideView.start();
    }


    public void backViewEachProjectTaskActivity(View view)
    {
        Intent aPTTUAIntent = new Intent(AssignedProjectTaskToUserActivity.this,ViewEachProjectTaskActivity.class);
        startActivity(aPTTUAIntent);
    }

    @Override
    public void onBackPressed() {

    }








}

