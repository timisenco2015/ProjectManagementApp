package com.zihron.projectmanagementapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;

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

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewEachProjectTaskActivity extends AppCompatActivity {

    private HttpRequestClass httpRequestClass;
    private LinearLayout statusListLinearLayout;
    private AlertDialog.Builder builder;
    private ImageButton assignedProjectTaskFloatingButton;
    private ImageButton editProjectTaskFloatingButton;
    private ImageButton appraiseeProjectTaskFloatingButton;
    private ImageButton fabMainFloatingButton;
    private boolean isFabShow = false;
    private TextView projectTaskStartDateTextView;
    private TextView projectTaskkEndDateTextView;
    private TextView projectTaskNameTextView;
    private S3ImageClass s3ImageClass;
    private TextView projectNameTextView;
    private TextView projectOwnerNameTextView;
    private JSONArray projectTaskJSONArray;
    private JSONObject  projectTaskJObject;
    private PieChart projectTaskStatusMainChart;
    private TextView projectTaskDescriptionTextView;
    private LinearLayout projectMemeberLayout;
    private CircleImageView viewProjectTaskCommentsClick;
    private JSONObject editProjectTaskObject;
    private boolean canRateMemeber =false;
    private boolean canAssignTask = false;
    private String projectTaskEndDate;
    private String projectTaskStartDate;
    private SharedPreferences sharedPreferences;
    private String projectOwner;
    private boolean isSupervisor;
    private boolean canCreateTask;
    private int membersCount;
    private AlertDialog alertDialog;
    private String projectName;
    private String projectTaskName;
    private Activity activity;
private String userName;
    private JSONArray membersJSONArray;
    private JSONObject membersJSONObject;
    private TextView appbarTextView;
    private ProgressBarClass progressBarClass;
    private boolean checkForPermission = true;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_each_project_task);

        s3ImageClass = new S3ImageClass();
        assignedProjectTaskFloatingButton = (ImageButton)findViewById(R.id.assignedProjectTaskFloatingBtnId);
        editProjectTaskFloatingButton = (ImageButton)findViewById(R.id.editProjectTaskFloatingBtnId);
        projectMemeberLayout = (LinearLayout)findViewById(R.id.projectMemeberLayoutId);
        appbarTextView = (TextView)findViewById(R.id.appbarTextViewId);
        activity = this;
        progressBarClass = new ProgressBarClass(activity);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE );
        projectName = sharedPreferences.getString("projectName", null);
        projectTaskName =sharedPreferences.getString("projectTaskName", null);
        userName = sharedPreferences.getString("userName", null);
        appbarTextView.setText(projectTaskName);
      try {
          String result =getProjectSpecificTasks() ;





          projectTaskJSONArray = new JSONArray(result);
          projectTaskJObject = new JSONObject(projectTaskJSONArray.getString(0)) ;
            projectTaskStartDateTextView = (TextView)findViewById(R.id.projectTaskStartDateTxtVwId);
        projectTaskkEndDateTextView = (TextView)findViewById(R.id.projectTaskkEndDateTxtvwId);
        projectTaskNameTextView = (TextView)findViewById(R.id.tasknameTxtvwId);
        projectTaskStatusMainChart = (PieChart)findViewById(R.id.projectTaskStatusMainChartId);
       projectTaskDescriptionTextView = (TextView)findViewById(R.id.projectTaskDescpTxtVWId);
        projectNameTextView = (TextView)findViewById(R.id.projectNameTxtvwId);
        projectOwnerNameTextView = (TextView)findViewById(R.id.projectOwnerNameTxtVWId);
        projectMemeberLayout = (LinearLayout)findViewById(R.id.projectMemeberLayoutId);
            viewProjectTaskCommentsClick = (CircleImageView)findViewById(R.id.viewProjectTaskCommentsClick);
        statusListLinearLayout = (LinearLayout)findViewById(R.id.statusListLLTId);
          appraiseeProjectTaskFloatingButton = (ImageButton) findViewById(R.id.appraiseeProjectTaskFloatingBtnId);




        projectTaskStartDate =projectTaskJObject.getString("dateProjectTaskStarted");
        projectTaskEndDate =projectTaskJObject.getString("dateProjectTaskEnded");
            projectTaskStartDateTextView.setText(projectTaskJObject.getString("dateProjectTaskStarted"));
            projectTaskkEndDateTextView.setText(projectTaskJObject.getString("dateProjectTaskEnded"));
            projectTaskNameTextView.setText(projectTaskJObject.getString("nameOfProjectTask"));
            projectNameTextView.setText(projectTaskJObject.getString("nameOfProject"));
            projectOwnerNameTextView.setText(projectTaskJObject.getString("createdByFName")+" "+projectTaskJObject.getString("createdByLName"));
            projectTaskDescriptionTextView.setText(projectTaskJObject.getString("descriptionOfProjectTask"));
            membersJSONArray =  projectTaskJObject.getJSONArray("allProjectTaskMemeber");

        //    Toast.makeText(activity,"===>"+membersJSONArray.toString(),Toast.LENGTH_LONG).show();
            int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    /// friendsSuggestionsLayout.friendSuggestnListVwLargeScrnSizeLT();
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    generateMemberLayout();
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



          canCreateTask=  sharedPreferences.getBoolean("cancreatetask",false);
            canRateMemeber =  sharedPreferences.getBoolean("canratemember",false);
            canAssignTask=sharedPreferences.getBoolean("canassigntask",false);
            isSupervisor = sharedPreferences.getBoolean("issupervisor",false);
            editProjectTaskObject = new JSONObject();

          editProjectTaskObject.put("membersCount",projectTaskJObject.getInt("membersCount"));
            editProjectTaskObject.put("projectName",projectTaskJObject.getString("nameOfProject"));
            editProjectTaskObject.put("taskName",projectTaskJObject.getString("nameOfProjectTask"));
            editProjectTaskObject.put("taskNameStartDate",projectTaskJObject.getString("dateProjectTaskStarted"));
            editProjectTaskObject.put("taskNameEndDate",projectTaskJObject.getString("dateProjectTaskEnded"));
            editProjectTaskObject.put("taskDescrp",projectTaskJObject.getString("descriptionOfProjectTask"));



          float   percentageDone  = Float.parseFloat(projectTaskJObject.getString("percentageDoneOfProjectTask"));

            PieChart projectStatusChart = (PieChart) findViewById(R.id.projectTaskStatusMainChartId);
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
            set.setValueTextSize(percentageDone);
            PieData data = new PieData(set);

            projectStatusChart.setData(data);
          projectStatusChart.getDescription().setEnabled(false);
            projectStatusChart.setDrawHoleEnabled(false);
            //projectStatusChart.setBackgroundColor(Color.LTGRAY);
            Legend legend = projectStatusChart.getLegend();
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




       } catch (JSONException e) {
           e.printStackTrace();
      }

        fabMainFloatingButton = (ImageButton)findViewById(R.id.fabMainFloatingBtnId);
        fabMainFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFabShow)
                {
                    showFABMenu();
                }
                else
                {

                    closeFABMenu();
                }
            }
        });



    }

public void backToViewEachProject(View view)
{
    Intent vPAIntent = new Intent(ViewEachProjectTaskActivity.this, ViewProjectActivity.class);
    startActivity(vPAIntent);
}
    public void generateMemberLayout()
    {
        if(membersJSONArray!=null)
        {
            Typeface memberNameTF =  ResourcesCompat.getFont(activity, R.font.lato);
            projectMemeberLayout.removeAllViews();
            JSONObject membersObject = null;
            int isAppraised = 0;

            for (int i = 0; i < membersJSONArray.length(); i++) {
                try {
                    membersObject = new JSONObject(membersJSONArray.getString(i));
                    isAppraised =  membersObject.getInt("isAppraisedCount");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    RelativeLayout outerLinearLayout = new RelativeLayout(this);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(600, 500);
                    outerLinearLayout.setPadding(60, 20, 0, 0);
                    outerLinearLayoutLP.setMargins(0, 0, 40, 0);
                    if(isAppraised==0) {

                        outerLinearLayout.setBackground(getResources().getDrawable(R.drawable.notratedprojecttaskmemberbackground, null));
                        outerLinearLayout.setLayoutParams(outerLinearLayoutLP);

                    }
                    else if (isAppraised>0)
                    {
                       outerLinearLayout.setBackground(getResources().getDrawable(R.drawable.ratedprojecttaskmemberbackground, null));
                        outerLinearLayout.setLayoutParams(outerLinearLayoutLP);

                    }

                    RelativeLayout.LayoutParams llpChatPageContentDateTxtViewLayout = new RelativeLayout.LayoutParams(470, RelativeLayout.LayoutParams.WRAP_CONTENT);
                   final TextView memberNameTextView = new TextView(this);
                    llpChatPageContentDateTxtViewLayout.setMargins(70, 5, 0, 0);
                    llpChatPageContentDateTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberLogoId);
                    memberNameTextView.setLayoutParams(llpChatPageContentDateTxtViewLayout);
                    memberNameTextView.setTextSize(12);
                    memberNameTextView.setTypeface(memberNameTF);
                    memberNameTextView.setText(membersObject.getString("firstNameOfMember") + " " + membersObject.getString("lastNameOfMember"));
                    memberNameTextView.setId(R.id.memberNameLogoId);
                    memberNameTextView.setTextColor(getResources().getColor(R.color.color43));


                    RelativeLayout.LayoutParams llpMemebrEmailTxtViewLayout = new RelativeLayout.LayoutParams(470, RelativeLayout.LayoutParams.WRAP_CONTENT);
                 final   TextView memberEmailTextView = new TextView(this);
                    llpMemebrEmailTxtViewLayout.setMargins(0, 20, 0, 0);
                    llpMemebrEmailTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.memberNameLogoId);
                    memberEmailTextView.setLayoutParams(llpMemebrEmailTxtViewLayout);
                    memberEmailTextView.setTextSize(12);
                    memberEmailTextView.setSingleLine(true);
                    memberEmailTextView.setTypeface(memberNameTF);
                    memberEmailTextView.setText(membersObject.getString("emailOfMember"));
                    memberEmailTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    memberEmailTextView.setId(R.id.memberEmailLogoId);
                    memberEmailTextView.setTextColor(getResources().getColor(R.color.color43));


                    CircleImageView  profileImageVW = new CircleImageView(this);
                    RelativeLayout.LayoutParams llpInnerProfileImageVWLayout = new RelativeLayout.LayoutParams(250, 250);
                    llpInnerProfileImageVWLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    llpInnerProfileImageVWLayout.setMargins(80, 20, 0, 0);
                    profileImageVW.setLayoutParams(llpInnerProfileImageVWLayout);
                    profileImageVW.setId(R.id.memberLogoId);

                   if(s3ImageClass.confirmIfImageInPhone(membersObject.getString("emailOfMember")))
                    {
                        profileImageVW.setImageBitmap(s3ImageClass.readFromPhone(membersObject.getString("emailOfMember")));
                    }
                    else {
                        s3ImageClass = new S3ImageClass(activity,membersObject.getString("emailOfMember"), "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            s3ImageClass.writeToPhone(membersObject.getString("emailOfMember"),bitMap);
                            profileImageVW.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageVW.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+membersObject.getString("firstNameOfMember") + " " + membersObject.getString("lastNameOfMember")+"&background=90a8a8&color=fff&size=128").into( profileImageVW);
                        }
                    }

                    outerLinearLayout.addView(profileImageVW);
                    outerLinearLayout.addView(memberNameTextView);
                    outerLinearLayout.addView(memberEmailTextView);
                    outerLinearLayout.setClickable(true);
                    outerLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            projectMemberAllTasksView(memberEmailTextView.getText().toString(),memberNameTextView.getText().toString());
                        }
                    });


                    projectMemeberLayout.addView(outerLinearLayout);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void projectMemberAllTasksView(final String memberUsername, String memberFullName)
    {

        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.eachprojectmemberalltasksreviewlayout);
        LinearLayout projectMemberAllTasksLinearLT = dialog.findViewById(R.id.projectMemberAllTasksLLTId);
        ImageView closeMbrAllPrjtTasksImageView = dialog.findViewById(R.id.closeMbrAllPrjtTasksImageVWId);
        ImageView profileImageView = dialog.findViewById(R.id.profileImageVWId);
        ImageView rateMemberImageView = dialog.findViewById(R.id.rateMemberImageVWId);
        rateMemberImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vEPTAIntent = new Intent(ViewEachProjectTaskActivity.this, ProjectTaskSupervisorAppraisalActivity.class);
                vEPTAIntent.putExtra("apprasieeUserName",memberUsername);
                vEPTAIntent.putExtra("taskStartDate",projectTaskStartDate);
                vEPTAIntent.putExtra("taskEndDate",projectTaskEndDate);

                startActivity(vEPTAIntent);
            }
        });
        TextView userNameTextView = dialog.findViewById(R.id.userNameTextVWId);

        closeMbrAllPrjtTasksImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        try {
            String result = getMemberAllProjectTasks(memberUsername);

            if(result!=null) {

                JSONArray memberAllProjectTasksList = new JSONArray(result);
                userNameTextView.setText(memberFullName);

                if(!s3ImageClass.hasPermission())
                {
                    checkForPermission = s3ImageClass.getPermission();
                }

                if(s3ImageClass.hasPermission() && checkForPermission)
                {

                    if(s3ImageClass.confirmIfImageInPhone(memberUsername))
                    {
//                        profileImageView.setImageBitmap(s3ImageClass.readFromPhone(memberUsername));
                    }

                    else {
                        s3ImageClass = new S3ImageClass(activity,memberUsername, "profilepicfolder");
                        Bitmap bitMap = s3ImageClass.getImageBitMap();
                        if (s3ImageClass.isObjectExists()) {
                            if(s3ImageClass.hasPermission()) {
                                s3ImageClass.writeToPhone(memberUsername, bitMap);
                            }
                            profileImageView.setImageBitmap(s3ImageClass.getImageBitMap());
                            profileImageView.invalidate();
                        }
                        else
                        {
                            Picasso.with(activity).load("https://ui-avatars.com/api/?name="+memberFullName+"&background=90a8a8&color=fff&size=128").into(profileImageView);
                        }
                    }
                }


                for(int i=0; i<memberAllProjectTasksList.length();i++) {

                    LinearLayout outerLinearLayout = new LinearLayout(this);
                    LinearLayout.LayoutParams outerLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    outerLinearLayoutLP.setMargins(20, 40, 0, 40);
                    outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    outerLinearLayout.setLayoutParams(outerLinearLayoutLP);
                    Typeface taskNameTF =  ResourcesCompat.getFont(activity, R.font.vollkorn);
                    Typeface otherNameTF =  ResourcesCompat.getFont(activity, R.font.nobile);
                    JSONObject tempObject = new JSONObject(memberAllProjectTasksList.getString(i));

                    try {


                            LinearLayout.LayoutParams taskNameTxtViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView taskNameTextView = new TextView(activity);
                            taskNameTxtViewLLP.setMargins(10, 0, 0, 20);
                            taskNameTextView.setLayoutParams(taskNameTxtViewLLP);
                            taskNameTextView.setTextSize(22);
                        taskNameTextView.setTypeface(taskNameTF);
                            ///taskNameTextView.setTypeface(getResources().getFont(R.font.montserrat));
                            taskNameTextView.setText(tempObject.getString("projectTaskName"));
                            taskNameTextView.setTextColor(Color.parseColor("#303952"));


                            LinearLayout yourProjectTaskStatusLinearLayout = new LinearLayout(this);
                            LinearLayout.LayoutParams yourProjectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            yourProjectTaskStatusLinearLayoutLP.setMargins(10, 5, 0, 40);
                            yourProjectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            yourProjectTaskStatusLinearLayout.setLayoutParams(yourProjectTaskStatusLinearLayoutLP);

                            LinearLayout.LayoutParams taskStatusTextTxtView1LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView taskStatusTextTxtView1 = new TextView(activity);
                        taskStatusTextTxtView1LLP.setMargins(0,30,0,0);
                            taskStatusTextTxtView1.setLayoutParams(taskStatusTextTxtView1LLP);
                            taskStatusTextTxtView1.setTextSize(14);
                            taskStatusTextTxtView1.setTypeface(otherNameTF);
                        taskStatusTextTxtView1.setTextColor(Color.parseColor("#786fa6"));
                            taskStatusTextTxtView1.setText("Your done percentage is ");



                            RelativeLayout yourProjectTaskStatusCircleInnerLLT = new RelativeLayout(this);

                            yourProjectTaskStatusCircleInnerLLT.setBackground(getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                            RelativeLayout.LayoutParams yourProjectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(250,80);
                            yourProjectTaskStatusCircleInnerLLTLP.setMargins(160, 15, 0, 0);
                            yourProjectTaskStatusCircleInnerLLT.setLayoutParams(yourProjectTaskStatusCircleInnerLLTLP);


                            RelativeLayout.LayoutParams yourProjectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            yourProjectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                            TextView yourProjectTaskStatusTVW = new TextView(activity);
                            yourProjectTaskStatusTVW.setLayoutParams(yourProjectTaskStatusTxtViewLLP);
                            yourProjectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            yourProjectTaskStatusTVW.setTextSize(15);
                          ///  yourProjectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                            yourProjectTaskStatusTVW.setText(tempObject.getString("memberTskDonePer") + "%");
                            yourProjectTaskStatusTVW.setTextColor(getResources().getColor(R.color.color27));

                            yourProjectTaskStatusCircleInnerLLT.addView(yourProjectTaskStatusTVW);


                            yourProjectTaskStatusLinearLayout.addView(taskStatusTextTxtView1);
                            yourProjectTaskStatusLinearLayout.addView(yourProjectTaskStatusCircleInnerLLT);


                            LinearLayout projectTaskStatusLinearLayout = new LinearLayout(this);
                            LinearLayout.LayoutParams projectTaskStatusLinearLayoutLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            projectTaskStatusLinearLayoutLP.setMargins(10, 0, 0, 5);
                            projectTaskStatusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            projectTaskStatusLinearLayout.setLayoutParams(projectTaskStatusLinearLayoutLP);

                            LinearLayout.LayoutParams taskStatusTextTxtView2LLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView taskStatusTextTxtView2 = new TextView(activity);
                        taskStatusTextTxtView2LLP.setMargins(0,30,0,0);
                            taskStatusTextTxtView2.setLayoutParams(taskStatusTextTxtView2LLP);
                            taskStatusTextTxtView2.setTextSize(17);
                        taskStatusTextTxtView2.setTextSize(14);
                        taskStatusTextTxtView2.setTypeface(otherNameTF);
                        taskStatusTextTxtView2.setTextColor(Color.parseColor("#786fa6"));
                            taskStatusTextTxtView2.setText("Task total done percentage is");



                            RelativeLayout projectTaskStatusCircleInnerLLT = new RelativeLayout(this);
                            RelativeLayout.LayoutParams projectTaskStatusCircleInnerLLTLP = new RelativeLayout.LayoutParams(250,80);
                            projectTaskStatusCircleInnerLLT.setBackground(getResources().getDrawable(R.drawable.curveradiusbackgrnd, null));
                            projectTaskStatusCircleInnerLLTLP.setMargins(50, 15, 0, 0);
                            projectTaskStatusCircleInnerLLT.setLayoutParams(projectTaskStatusCircleInnerLLTLP);

                            RelativeLayout.LayoutParams projectTaskStatusTxtViewLLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                            projectTaskStatusTxtViewLLP.addRule(RelativeLayout.CENTER_IN_PARENT);
                            TextView projectTaskStatusTVW = new TextView(activity);
                            projectTaskStatusTVW.setLayoutParams(projectTaskStatusTxtViewLLP);
                            projectTaskStatusTVW.setTextSize(15);
                          ///  projectTaskStatusTVW.setTypeface(getResources().getFont(R.font.montserrat));
                            projectTaskStatusTVW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            projectTaskStatusTVW.setText(tempObject.getString("taskDonePercentage") + "%");
                            projectTaskStatusTVW.setTextColor(getResources().getColor(R.color.color27));

                            projectTaskStatusCircleInnerLLT.addView(projectTaskStatusTVW);
                            projectTaskStatusLinearLayout.addView(taskStatusTextTxtView2);
                            projectTaskStatusLinearLayout.addView(projectTaskStatusCircleInnerLLT);


                            outerLinearLayout.addView(taskNameTextView);
                            outerLinearLayout.addView(yourProjectTaskStatusLinearLayout);
                            outerLinearLayout.addView(projectTaskStatusLinearLayout);


                            projectMemberAllTasksLinearLT.addView(outerLinearLayout);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.show();
                }
            }
            else
            {
                builder = new AlertDialog.Builder(activity);
                builder.setMessage("No task assigned to this member yet");
                builder.setCancelable(true);
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alertDialog.cancel();
                            }
                        });


                alertDialog = builder.create();
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void showFABMenu()
    {
        isFabShow=true;

        editProjectTaskFloatingButton.animate().translationY(-getResources().getDimension(R.dimen.standard_65));

        assignedProjectTaskFloatingButton.animate().translationY(-getResources().getDimension(R.dimen.standard_115));

    }
    public void closeFABMenu()
    {
        isFabShow=false;
        editProjectTaskFloatingButton.animate().translationY(0);

        assignedProjectTaskFloatingButton.animate().translationY(0);

    }

    public void editProjectTask(View view)
    {
        if(isSupervisor) {
            Intent vEPTAIntent = new Intent(ViewEachProjectTaskActivity.this, EditProjectTaskActivity.class);
           Log.e("--++hm",editProjectTaskObject.toString());
            vEPTAIntent.putExtra("taskDetailsInString", editProjectTaskObject.toString());
            startActivity(vEPTAIntent);
        }
        else
        {
Toast.makeText(activity,"You don't have the privilege to edit task",Toast.LENGTH_LONG).show();
        }
    }
    public void viewProjectTaskComments(View view)
    {
        Intent vEPTAIntent = new Intent(ViewEachProjectTaskActivity.this, ProjectTaskCommentActivity.class);
        startActivity(vEPTAIntent);
    }

    public void selfAppraisal(View view)
    {

            Intent vEPTAIntent = new Intent(ViewEachProjectTaskActivity.this, ProjectTaskSelfAppraisalActivity.class);
            startActivity(vEPTAIntent);

    }

    @Override
    public void onBackPressed() {

    }


    public void assignedProjectTask(View view)
    {
        if(canAssignTask) {
            Intent vEPTAIntent = new Intent(ViewEachProjectTaskActivity.this, AssignedProjectTaskToUserActivity.class);
            startActivity(vEPTAIntent);
        }
        else
        {
            Toast.makeText(activity,"You don't have the privilege to assign task",Toast.LENGTH_LONG).show();
        }
    }




    public String getProjectSpecificTasks()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectName);
            postDataParams.put("projectTaskName", projectTaskName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fsptd/",postDataParams, activity,"application/json", "application/json");
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



    public String getMemberAllProjectTasks(String memberUsername)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfProjectTask", projectTaskName);
            postDataParams.put("nameOfUser",memberUsername);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/gaptatm/",postDataParams, activity,"application/json", "application/json");
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
