package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
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
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectTaskAssignedListActivity extends AppCompatActivity {


    private LinearLayout projectTaskAssignedListContainer;
    private Activity activity;
    private String projectAssignedName;
    private TextView projectNameTxtvw;
    private TextView projectOwnerNameTxtvw;
    private TextView projectStartDateTextVW;
    private TextView projectEndDatevwTextVW;
    private TextView projectMemebrsTextVW;
    private PieChart projectAssignedMainChart;
    private TextView projectTaskTextVW;
    private TextView projectDescripTxtVW;
    private JSONArray projectDetailsList;
    private JSONObject projectDetailsObject;
    private JSONArray projectAssignedTasksList;
    private String projectTaskName;
    private SharedPreferences sharedPreferences;
    private String userName;
    private HttpRequestClass httpRequestClass;
    private double projectDonePercentage;
    private float percentageDone;
    private String projectOwnerUserEmail;
    private int memberCount;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ProgressBarClass progressBarClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_task_assigned_list);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        projectTaskAssignedListContainer = (LinearLayout) findViewById(R.id.projectTaskAssignedListContainerId);
        sharedPreferences = activity.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);

        userName = sharedPreferences.getString("userName", null);


        projectAssignedName = sharedPreferences.getString("projectName",null);
       // projectAssignedName = getIntent().getStringExtra("projectAssignedName");
        projectAssignedMainChart = (PieChart) findViewById(R.id.projectAssignedChartId);
        projectNameTxtvw = (TextView)findViewById(R.id.projectNameTxtvwId);
        projectStartDateTextVW = (TextView)findViewById(R.id.projectStartDatevwId);
        projectOwnerNameTxtvw = (TextView)findViewById(R.id.projectOwnerNamevwId);
        projectEndDatevwTextVW = (TextView)findViewById(R.id.projectEndDatevwId);
        projectMemebrsTextVW = (TextView) findViewById(R.id.projectMemebrsvwId);
        projectTaskTextVW = (TextView)findViewById(R.id.projectTaskvwId);
        projectDescripTxtVW = (TextView)findViewById(R.id.projectDescripTxtVwId);
        projectMemebrsTextVW = (TextView)findViewById(R.id.projectMemebrsTextVwId);
        projectTaskTextVW = (TextView)findViewById(R.id.projectTaskTextVwId);

        try {
            String result = getProjectAllAssignedTasks(projectAssignedName,userName);
            projectDetailsList = new JSONArray(result);
            projectDetailsObject = new JSONObject(projectDetailsList.getString(0));
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        initializeTextViewField();
        populateGraph();
        generateAssignedTaskToUserView();
    }
    @Override
    public void onBackPressed() {

    }

    public void initializeTextViewField()
    {
        try {
            projectOwnerUserEmail = projectDetailsObject.getString("emailOfUser");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("projectOwnerUserEmail", projectOwnerUserEmail);
            editor.commit();
            projectNameTxtvw.setText(projectDetailsObject.getString("nameOfProject"));
            projectOwnerNameTxtvw.setText(projectDetailsObject.getString("firstNameOfUser")+" "+ projectDetailsObject.getString("lastNameOfUser"));
            projectStartDateTextVW.setText(projectDetailsObject.getString("startDateOfProject"));
            projectEndDatevwTextVW.setText(projectDetailsObject.getString("endDateOfProject"));
            projectDescripTxtVW.setText(projectDetailsObject.getString("descriptionOfProject"));
            percentageDone = Float.parseFloat(projectDetailsObject.getString("projectDonePercentage"));
            memberCount = projectDetailsObject.getInt("projectMembersTotalCount");
            projectMemebrsTextVW.setText(""+memberCount);
            projectTaskTextVW.setText(projectDetailsObject.getString("projectTotalTasksCount"));
            projectAssignedTasksList= projectDetailsObject.getJSONArray("allProjectAssignedTasks");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void generateAssignedTaskToUserView() {

        for (int i = 0;  i< projectAssignedTasksList.length(); i++)
        {

 try {

     JSONObject taskAssignedObject = new JSONObject(projectAssignedTasksList.getString(i));
     Space newSpace = new Space(this);
            LinearLayout.LayoutParams spaceLT = new LinearLayout.LayoutParams(20, 20);
            newSpace.setLayoutParams(spaceLT);

            ImageView taskImageView = new ImageView(this);
            RelativeLayout.LayoutParams taskCircleImageLayout = new RelativeLayout.LayoutParams(100, 100);
            taskCircleImageLayout.setMargins(0, 40, 0,0);
            taskImageView.setBackground(this.getResources().getDrawable(R.drawable.addbutton, null));
            taskCircleImageLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
            taskImageView.setId(R.id.taskAssignedListId);
            taskImageView.setLayoutParams(taskCircleImageLayout);

     RelativeLayout.LayoutParams llpChatProjectDonePercentageTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
     final TextView projectDonePercentageTextView = new TextView(this);
     llpChatProjectDonePercentageTxtViewLayout.setMargins(0, 10, 0,0);
     llpChatProjectDonePercentageTxtViewLayout.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
     llpChatProjectDonePercentageTxtViewLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
     projectDonePercentageTextView.setId(R.id.projectTaskDoneTextViewId);
     projectDonePercentageTextView.setTextColor(getResources().getColor(R.color.color28));
     llpChatProjectDonePercentageTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.taskAssignedListId);
     projectDonePercentageTextView.setLayoutParams(llpChatProjectDonePercentageTxtViewLayout);
     double totalDonePercentage = taskAssignedObject.getDouble("totalDonePercentage");
     projectDonePercentageTextView.setText(""+totalDonePercentage);
     projectDonePercentageTextView.setTextSize(13);



/*
           LinearLayout innerContainerSideA = new LinearLayout(this);
            LinearLayout.LayoutParams llpChatPageInnerRLTSideA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llpChatPageInnerRLT.setMargins(20, 0, 20, 20);
            innerContainer.setBackground(this.getResources().getDrawable(R.drawable.border, null));
            innerContainer.setLayoutParams(llpChatPageInnerRLTSideA);
*/
            RelativeLayout.LayoutParams llpChatProjectNameTxtViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
           final TextView projectTaskNameTextView = new TextView(this);
            llpChatProjectNameTxtViewLayout.setMargins(0, 10, 0,0);
            llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
            llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
            projectTaskNameTextView.setId(R.id.projectNameTextViewId);
            llpChatProjectNameTxtViewLayout.addRule(RelativeLayout.BELOW, R.id.projectTaskDoneTextViewId);
            projectTaskNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
            projectTaskNameTextView.setTextSize(13);
            projectTaskNameTextView.setFilters(new InputFilter[] { new InputFilter.LengthFilter(14) });

                projectTaskNameTextView.setText(taskAssignedObject.getString("projectTaskName"));

                projectTaskNameTextView.setTextColor(this.getResources().getColor(R.color.color23));


            RelativeLayout.LayoutParams dateTextViewLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView dateTextView = new TextView(this);
            dateTextViewLayout.setMargins(0, 20, 0,0);
            dateTextViewLayout.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
            dateTextViewLayout.addRule(RelativeLayout.BELOW, R.id.projectNameTextViewId);
            dateTextViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);;
            dateTextView.setLayoutParams(dateTextViewLayout);
            dateTextView.setText(taskAssignedObject.getString("projectTaskAssignedDate"));
            dateTextView.setTextSize(9);
            dateTextView.setTextColor(this.getResources().getColor(R.color.color22));

            RelativeLayout innerContainer = new RelativeLayout(this);
            LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(400, 380);
            llpChatPageInnerRLT.setMargins(30, 30, 30, 80);
            llpChatPageInnerRLT.bottomMargin=80;
            if(totalDonePercentage==100.00 ||  totalDonePercentage==100.0) {
                innerContainer.setBackground(this.getResources().getDrawable(R.drawable.assignedtaskdoneroundborder, null));

            }
            else
            {
                innerContainer.setBackground(this.getResources().getDrawable(R.drawable.assigndtasknotdoneroundborder, null));

            }
     innerContainer.setLayoutParams(llpChatPageInnerRLT);
            innerContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pTAAIntent = new Intent(activity, ProjectEachAssignedTaskReviewActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("projectTaskName",projectTaskNameTextView.getText().toString());
                    editor.commit();

                    activity.startActivity(pTAAIntent);
                }
            });


           // innerContainer.addView(newSpace);
            innerContainer.addView(taskImageView);
     innerContainer.addView(projectDonePercentageTextView);

            innerContainer.addView(projectTaskNameTextView);
            innerContainer.addView(dateTextView);
     projectTaskAssignedListContainer.addView(innerContainer);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void backToProjectTaskAssingedActivity(View view)
    {
        Intent pEATRAIntent = new Intent(ProjectTaskAssignedListActivity.this, HomeActivity.class);
        startActivity(pEATRAIntent);
    }

    public void populateGraph()
    {
        try {
            percentageDone = Float.parseFloat(projectDetailsObject.getString("projectDonePercentage"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PieChart projectStatusChart = (PieChart) findViewById(R.id.projectAssignedChartId);
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





    }

    public void backToViewProjectAssingedActivity(View view)
    {
        Intent pTALAIntent = new Intent(activity, HomeActivity.class);
        startActivity(pTALAIntent);
    }

    public String getProjectAllAssignedTasks(String projectName,  String projectAssignedToUserName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();

        try {
            postDataParams.put("projectTaskAssignedUserEmail", projectAssignedToUserName);
            postDataParams.put("projectName", projectName);
            Log.e("--++==",postDataParams.toString());
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuatpd/",postDataParams, activity,"application/json", "application/json");
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
