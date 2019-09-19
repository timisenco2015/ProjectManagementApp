package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Picasso;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectEachAssignedTaskReviewActivity extends AppCompatActivity {

    private TextView projectTaskStatusUpdateTextVW;
    private TextView startDateTxtVW;
    private TextView tasknameTxtVW;
    private TextView projectOwnerNameTextView;
    private JSONObject projectTaskAllMemebersJsonObject;
    private PieChart projectTaskStatusMainChart;
    private TextView projectTaskDescrptTextView;
    private S3ImageClass s3ImageClass;
    private TextView projectNameTextView;
    private LinearLayout projectTaskMembersLLayout;
    private CircleImageView viewProjectTaskCommentsClick;
    private String projectTaskName;
    private  AlertDialog alertDialog;
    private SharedPreferences sharedPreferences;
    private TextView endDateTextView;
    private String userName;
    private String projectAssignedName;
    private Activity activity;
    private float percentageDone;
    private HttpRequestClass httpRequestClass;
    private Typeface fontAwesomeIcon;
    private JSONArray projectTaskAssignedDetailsList;
    private LinearLayout projectTaskMemberAppraisalLlt;
    private String projectOwnerUserEmail;
    private int appraisalQuestIndex =0;
    private boolean checkForPermission = true;
    private JSONArray projectTaskAllMemebersJsonArray;
    private AlertDialog.Builder alertDialogBuilder;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String projectTaskStartDate;
    private String projectTaskEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_each_assigned_task);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        projectAssignedName = sharedPreferences.getString("projectName",null);
        this.s3ImageClass = new S3ImageClass();
        startDateTxtVW = (TextView)findViewById(R.id.startDateTxtvwId);
        tasknameTxtVW = (TextView)findViewById(R.id.tasknameTxtvwId);
        projectOwnerNameTextView = (TextView)findViewById(R.id.projectOwnerNameId);
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");
        projectTaskDescrptTextView = (TextView)findViewById(R.id.projectTaskDescpvwId);
        projectTaskMembersLLayout = (LinearLayout) findViewById(R.id.memeberLayoutId);
        projectNameTextView = (TextView)findViewById(R.id.projectNameTxtvwId);
        viewProjectTaskCommentsClick = (CircleImageView) findViewById(R.id.viewProjectTaskCommentsClick);
        projectNameTextView = (TextView)findViewById(R.id.projectNameTxtvwId);
       // projectTaskMemberAppraisalLlt = (LinearLayout)findViewById(R.id.projectTaskMemberAppraisalLltId);
        endDateTextView = (TextView)findViewById(R.id.endDateTxtvwId);
        projectTaskStatusMainChart = (PieChart) findViewById(R.id.projectTaskStatusMainChart);
        userName = sharedPreferences.getString("userName", null);
        projectOwnerUserEmail= sharedPreferences.getString("projectOwnerUserEmail",null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
 String result = null;

        try {
            result = getProjectAssignedTaskDetails();
            //final String[] questionsPartA = new String[]{"I am convinced that my supervisor believes that I will continue to…" };
            projectTaskAssignedDetailsList = new JSONArray(result);
            projectTaskAllMemebersJsonObject = new JSONObject(projectTaskAssignedDetailsList.get(0).toString());
            projectNameTextView.setText( projectTaskAllMemebersJsonObject.getString("nameOfProect"));
            projectTaskStartDate =projectTaskAllMemebersJsonObject.getString("dateProjectTaskStarted");
            projectTaskEndDate = projectTaskAllMemebersJsonObject.getString("dateProjectTaskEnded");
            startDateTxtVW.setText(projectTaskAllMemebersJsonObject.getString("dateProjectTaskStarted"));
            endDateTextView.setText(projectTaskAllMemebersJsonObject.getString("dateProjectTaskEnded"));
            tasknameTxtVW.setText(projectTaskAllMemebersJsonObject.getString("nameOfProjectTask"));
            projectTaskDescrptTextView.setText(projectTaskAllMemebersJsonObject.getString("descriptionOfProjectTask"));

            projectOwnerNameTextView.setText(projectTaskAllMemebersJsonObject.getString("firstNameOfUser")+" "+projectTaskAllMemebersJsonObject.getString("lastNameOfUser"));
            percentageDone =  Float.parseFloat(projectTaskAllMemebersJsonObject.getString("percentageDoneOfProjectTask"));

            projectTaskAllMemebersJsonArray = new JSONArray(projectTaskAllMemebersJsonObject.getString("allProjectTaskMemeber"));






            populateGraph();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        generateMemberLayout();



    }


    public void selfAppraisalBtn(View view)
    {
        Intent pEATRAIntent = new Intent(ProjectEachAssignedTaskReviewActivity.this, ProjectTaskSelfAppraisalActivity.class);
        pEATRAIntent.putExtra("taskStartDate",projectTaskStartDate);
        pEATRAIntent.putExtra("taskEndDate",projectTaskEndDate);
        startActivity(pEATRAIntent);
    }

    public void viewProjectTaskCommentsClick(View view)
    {
        Intent VPAIntent = new Intent(activity, ProjectTaskCommentActivity.class);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("projectName", projectAssignedName);
        editor.putString("projectTaskName", projectTaskName);
        editor.commit();
        startActivity(VPAIntent);
    }





    public void backToProjectTaskAssingedListActivity(View view)
    {
        Intent pEATRAIntent = new Intent(ProjectEachAssignedTaskReviewActivity.this, ProjectTaskAssignedListActivity.class);
        startActivity(pEATRAIntent);
    }






    public void projectTaskStatusUpdate(View view)
    {
        final Dialog projectTaskStatusUpdateDialog = new Dialog(activity);
        projectTaskStatusUpdateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        projectTaskStatusUpdateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        projectTaskStatusUpdateDialog.setContentView(R.layout.updateprojectassignedtaskstatus);

        Button updateProjectTskStatusBtn = (Button) projectTaskStatusUpdateDialog.findViewById(R.id.updateProjectTaskStatusId);
        updateProjectTskStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText projectTaskDonepercentageEditText1 = (EditText) projectTaskStatusUpdateDialog.findViewById(R.id.projectTaskWorkDonePerId1);
                EditText projectTaskDonepercentageEditText2 = (EditText) projectTaskStatusUpdateDialog.findViewById(R.id.projectTaskWorkDonePerId2);
                EditText projectTaskDWorkDoneDescrptnEditText = (EditText) projectTaskStatusUpdateDialog.findViewById(R.id.projectTaskWorkDoneDescrpId);
                if(!checkFiledValuesErrors(projectTaskDonepercentageEditText1,projectTaskDonepercentageEditText2, projectTaskDWorkDoneDescrptnEditText)) {
                    String percentageDoneValue =  projectTaskDonepercentageEditText1.getText().toString()+"."+projectTaskDonepercentageEditText2.getText().toString();


                        String result =updateProjectAssignedTasks(projectOwnerUserEmail,projectAssignedName, userName, projectTaskName, "Not Done", projectTaskDWorkDoneDescrptnEditText.getText().toString(), Double.parseDouble(percentageDoneValue));
                        if(result.equalsIgnoreCase("Record Updated"))
                        {
                            Toast.makeText(activity,"Progress status updated",Toast.LENGTH_LONG).show();
                        }
                        else if (result.equalsIgnoreCase("Record Not Updated"))
                        {
                            Toast.makeText(activity,"Unable to update your status at this time",Toast.LENGTH_LONG).show();
                        }
                        projectTaskStatusUpdateDialog.dismiss();

                }
            }
        });


        projectTaskStatusUpdateDialog.show();
    }

    @Override
    public void onBackPressed() {

    }

public void populateGraph()
{

    float notDonePercentage = 100 - percentageDone;
    List<PieEntry> entries = new ArrayList<>();
    final int[] piecolors = new int[]{
            Color.rgb(140, 146, 172),
            Color.rgb	(95,158,160)};

    entries.add(new PieEntry(percentageDone, ""));
    entries.add(new PieEntry(notDonePercentage, ""));

    PieDataSet set = new PieDataSet(entries, "Project Status");
    //  set.setMaxVisibleValueCount(3)
    set.setColors(piecolors);
    set.setValueTextSize(20);
    PieData data = new PieData(set);

    projectTaskStatusMainChart.setData(data);
    projectTaskStatusMainChart.getDescription().setEnabled(false);
    projectTaskStatusMainChart.setDrawHoleEnabled(false);
    //projectStatusChart.setBackgroundColor(Color.LTGRAY);
    Legend legend = projectTaskStatusMainChart.getLegend();
    legend.setEnabled(true);
    List<LegendEntry> legendEntries = new ArrayList<>();
    LegendEntry entry = new LegendEntry();
    entry.formColor=  piecolors[0];
    entry.label = "Completed";
    legendEntries.add(entry);
    entry = new LegendEntry();
    entry.formColor=  piecolors[1];;
    entry.label = "Not Completed";
    legendEntries.add(entry);
    legend.setCustom(legendEntries);
    legend.setFormSize(10f); // set the size of the legend forms/shapes
    legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
    legend.setTextSize(12f);
    legend.setTextColor(Color.BLACK);
    legend.setXEntrySpace(25f); // set the space between the legend entries on the x-axis
    legend.setYEntrySpace(25f);

}


    public void generateMemberLayout()
    {
        projectTaskMembersLLayout.removeAllViews();

        JSONObject projectTaskAllMemebersJSonObject = null;
        for( int i=0; i<projectTaskAllMemebersJsonArray.length(); i++) {
            try {
                 projectTaskAllMemebersJSonObject = new JSONObject(projectTaskAllMemebersJsonArray.getString(i));


            } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    RelativeLayout outerLinearLayout = new RelativeLayout(this);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(580, 480);
                    outerLinearLayout.setBackground(getResources().getDrawable(R.drawable.projectmemberbackground,null));
                    outerLinearLayout.setPadding(60,20,0,0);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                    outerLinearLayoutLP.setMargins(0, 0, 40, 0);






                    RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(470, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final  TextView memberNameTextView = new TextView(this);
                    llpChatPageContentDateTxtViewLayout.setMargins(75, 5, 0, 0);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberLogoId);
                    memberNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                    memberNameTextView.setTextSize(12);
                    memberNameTextView.setText(projectTaskAllMemebersJSonObject.getString("firstNameOfMember")+" "+projectTaskAllMemebersJSonObject.getString("lastNameOfMember"));
                    memberNameTextView.setId(R.id.memberNameLogoId);
                    memberNameTextView.setTextColor(getResources().getColor(R.color.color23));


                    RelativeLayout.LayoutParams llpMemebrEmailTxtViewLayout = new RelativeLayout.LayoutParams(470, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    final TextView memberEmailTextView = new TextView(this);
                    llpMemebrEmailTxtViewLayout.setMargins(0, 20, 0, 0);
                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberNameLogoId);
                    memberEmailTextView.setLayoutParams(llpMemebrEmailTxtViewLayout);
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setTextSize(12);
                    memberEmailTextView.setSingleLine(true);
                    memberEmailTextView.setText(projectTaskAllMemebersJSonObject.getString("emailOfMember"));
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setId(R.id.memberEmailLogoId);
                    memberEmailTextView.setTextColor(getResources().getColor(R.color.color23));


                    CircleImageView  profileImageVW = new CircleImageView(this);
                    RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(250, 250);
                    llpInnerProfileImageVWLayout.setMargins(80, 20, 0, 0);
                    profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                    profileImageVW.setId(R.id.memberLogoId);

                    if(!s3ImageClass.hasPermission())
                    {
                        checkForPermission = s3ImageClass.getPermission();
                    }

                    if(s3ImageClass.hasPermission() && checkForPermission)
                    {

                        if(s3ImageClass.confirmIfImageInPhone(memberEmailTextView.getText().toString()))
                        {
                            profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberEmailTextView.getText().toString()));
                        }

                        else {
                            s3ImageClass = new S3ImageClass(activity,memberEmailTextView.getText().toString(), "profilepicfolder");
                            Bitmap bitMap = s3ImageClass.getImageBitMap();
                            if (s3ImageClass.isObjectExists()) {
                                if(s3ImageClass.hasPermission()) {
                                    s3ImageClass.writeToPhone(memberEmailTextView.getText().toString(), bitMap);
                                }
                                profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                                profileImageVW.invalidate();
                            }
                            else
                            {
                                Picasso.with(activity).load("https://ui-avatars.com/api/?name="+projectTaskAllMemebersJSonObject.getString("firstNameOfMember")+" "+projectTaskAllMemebersJSonObject.getString("lastNameOfMember")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                            }
                        }
                    }



                    if(s3ImageClass.confirmIfImageInPhone(memberEmailTextView.getText().toString()))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(memberEmailTextView.getText().toString()));
                    }
                    else {
                        s3ImageClass = new S3ImageClass(activity,memberEmailTextView.getText().toString(), "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            s3ImageClass.writeToPhone(memberEmailTextView.getText().toString(),bitMap);
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+projectTaskAllMemebersJSonObject.getString("firstNameOfMember")+" "+projectTaskAllMemebersJSonObject.getString("lastNameOfMember")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);

                        }
                    }

                    outerLinearLayout.addView(profileImageVW);
                    outerLinearLayout.addView(memberNameTextView);
                    outerLinearLayout.addView(memberEmailTextView);
                    outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
                            View dialogView = inflater.inflate(R.layout.profileimagereviewlayout,null);
                            ImageView profileReviewDialog = dialogView.findViewById(R.id.profileReviewDialogId);
                            ImageView profileImageReview = dialogView.findViewById(R.id.profileImageReviewId);
                            profileReviewDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+ memberNameTextView.getText().toString()+"&background=90a8a8&color=fff&size=128").into(profileImageReview);
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            builder.setView(dialogView);

                          alertDialog = builder.create();
                            alertDialog.show();
                           // projectMemberAllTasksView(memberEmailTextView.getText().toString(), memberNameTextView.getText().toString());
                        }
                    });

                    projectTaskMembersLLayout.addView(outerLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    public String getProjectAssignedTaskDetails()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectAssignedName);
            postDataParams.put("projectTaskName", projectTaskName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpueatd/",postDataParams, activity,"application/json", "application/json");
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
            else if(result==null)
            {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }






    public boolean checkFiledValuesErrors(EditText projectTaskDoneTxtVW1,EditText projectTaskDoneTxtVW2, EditText projectTaskDescripIdTxtVW)    {
        boolean fieldError = false;
        ValidationClass validationClass = new ValidationClass();


        if(validationClass.checkFieldEmpty(projectTaskDescripIdTxtVW.getText().toString()) )
        {


            projectTaskDescripIdTxtVW.setError("Please enter value in this field");
            fieldError=true;
        }

        else
        {
            if(validationClass.checkStartWithInvalidChars(projectTaskDescripIdTxtVW.getText().toString()) )
            {
                projectTaskDescripIdTxtVW.setError("filed value starts start with invalid characters: e.g. @, ., ...");
                fieldError=true;
            }
            else if (validationClass.checkInvalidChar(projectTaskDescripIdTxtVW.getText().toString()) )
            {
                projectTaskDescripIdTxtVW.setError("filed value contains invalid characters: e.g. @, ., ...");
                fieldError=true;
            }
        }

        if(validationClass.checkFieldEmpty(projectTaskDoneTxtVW1.getText().toString()))
        {

            projectTaskDoneTxtVW1.setError("Please enter value in this field");
            fieldError=true;
        }
        else if(validationClass.checkNumericOnlyValue(projectTaskDoneTxtVW1.getText().toString()) )
        {
            projectTaskDoneTxtVW1.setError("field value can only accept value of either of this format 0.0 or 0");
            fieldError=true;
        }

        if(validationClass.checkFieldEmpty(projectTaskDoneTxtVW2.getText().toString()))
        {

            projectTaskDoneTxtVW2.setError("Please enter value in this field");
            fieldError=true;
        }
        else if(validationClass.checkNumericOnlyValue(projectTaskDoneTxtVW2.getText().toString()) )
        {
            projectTaskDoneTxtVW2.setError("field value can only accept value of either of this format 0.0 or 0");
            fieldError=true;
        }

        return fieldError;
    }

    public void errorAlertDialogBox (String errorDisplayText)
    {

        alertDialogBuilder.setMessage(errorDisplayText);
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog .show();
    }


    public String updateProjectAssignedTasks(String projectOwnerUserEmail, String projectName,  String userName, String projectTaskName,String projectTaskStatus,String projectTaskStatusDescrpt, double projectTaskStatusDonePer)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String projectAssinedTaskUpdateDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

        try {
            postDataParams.put("projectTaskAssignedTo",userName);
            postDataParams.put("userName",projectOwnerUserEmail);
            postDataParams.put("projectName",projectName);
            postDataParams.put("projectTaskName", projectTaskName);
            postDataParams.put("projectTaskStatus", projectTaskStatus);
            postDataParams.put("projectTaskStatusDescription", projectTaskStatusDescrpt);
            postDataParams.put("projectTaskDonePercentage", projectTaskStatusDonePer);
            postDataParams.put("projectTaskStatusUpdateDateTime", projectAssinedTaskUpdateDateTime);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/upatsd/",postDataParams, activity,"text/plain", "application/json");
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
            else if(result==null)
            {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }








}
