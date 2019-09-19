package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.zihron.projectmanagementapp.ArrowDirection.RIGHT;
import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class ViewProjectActivity extends AppCompatActivity {

    PieChart projectStatusChart;
    final Context context = this;
    private  String userName;
    private String projectName;
    private  float percentageDone;
    private SharedPreferences sharedPreferences;
    Typeface fontAwesomeIcon;
    LinearLayout memeberLayoutIdHSView;
    TextView projectOwnerNameView;
    TextView projectNameView;
    TextView projectStartDateView;
    TextView projectEndDateView;
    TextView projectMemebersView;
    TextView projectTaskView;
    TextView projectSummaryView;
    private S3ImageClass s3ImageClass;
    private String projectTaskCount;
    private String projectOwner;
    private JSONArray resultInJSONArray;
    private JSONArray projectAllMembersJSONArray;
    private boolean canRateMemeber =false;
    private boolean canDelegateProject=false;
    private boolean canAssignTask = false;
    private boolean canCreateTask=false;
    private String projectEndDate;
    private String projectStartDate;
    private String startDateToString;
    private String endDateToString;
    private Activity activity;
    private ImageButton delegateProjectFloatingButton;
    private ImageButton fabMainFloatingButton;
    private HttpRequestClass httpRequestClass;
    private ImageButton projectGroups_MembersFloatingButton;
    private ImageButton  editProjectFloatingButton;
    private Boolean isFabShow = false;
    private JSONObject jsonProjectDetailsObject;
    private JSONObject editProjectDetailsObject;
    private String projectDescription;
    private EditText projectDescriptionEditTextView;
    private EditText endDateEditTextView;
    private int projectDescriptionCount;
    private String projectMemberCount;
    private boolean isSupervisor;
    private String defaultProjectGroupName;

    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private static PopupWindow popUp;
    private int[] location;
    private ViewProjectMemeberGeneratorClass viewProjectMemeberGeneratorClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project);
        activity = this;
        progressBarClass = new ProgressBarClass(activity);
        fontAwesomeIcon = Typeface.createFromAsset(getAssets(),"font/fontawesome-webfont.ttf");
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        sharedPreferences = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        projectNameView = (TextView)findViewById(R.id.projectNameTxtvwId);
        projectOwnerNameView = (TextView)findViewById(R.id.projectOwnerNamevwId);
        projectStartDateView =(TextView) findViewById(R.id.projectStartDatevwId);
        projectEndDateView = (TextView)findViewById(R.id.projectEndDatevwId);
        projectMemebersView = (TextView)findViewById(R.id.projectMemebrsvwId);
        projectTaskView = (TextView)findViewById(R.id.projectTaskvwId);
        projectSummaryView = (TextView)findViewById(R.id.projectSummaryvwId);
        fabMainFloatingButton= (ImageButton) findViewById(R.id.fabMainFloatingBtnId);
        delegateProjectFloatingButton= (ImageButton) findViewById(R.id.delegateProjectFloatingBtnId);
        projectGroups_MembersFloatingButton = (ImageButton) findViewById(R.id.projectGroups_MembersFloatingBtnId);
        editProjectFloatingButton = (ImageButton) findViewById(R.id.editProjectFloatingBtnId);
s3ImageClass = new S3ImageClass();
        this.location = new int[2];

        //Display or hide fab
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



                memeberLayoutIdHSView = (LinearLayout)findViewById(R.id.memeberLayoutId);


        userName = sharedPreferences.getString("userName", null);
        projectName = sharedPreferences.getString("projectName", null);



        try {

            String result = getFullProjectDetails();

            resultInJSONArray = new JSONArray(result);
            String projectDetailsString = resultInJSONArray.getString(0);

            jsonProjectDetailsObject = new JSONObject(projectDetailsString);
            editProjectDetailsObject = new JSONObject();
             projectName= jsonProjectDetailsObject.getString("projectname");
           canRateMemeber = jsonProjectDetailsObject.getBoolean("canratemember");
            canAssignTask = jsonProjectDetailsObject.getBoolean("canassigntask");

            projectOwner = jsonProjectDetailsObject.getString("firstname")+" "+jsonProjectDetailsObject.getString("lastname");
            endDateToString = jsonProjectDetailsObject.getString("projectenddate");
            canDelegateProject = jsonProjectDetailsObject.getBoolean("candelegateproject");
            canCreateTask = jsonProjectDetailsObject.getBoolean("cancreatetask");
            startDateToString = jsonProjectDetailsObject.getString("projectstartdate");

            projectTaskCount = jsonProjectDetailsObject.getString("projecttaskscount");
          isSupervisor = jsonProjectDetailsObject.getBoolean("issupervisor");
            projectDescription = jsonProjectDetailsObject.getString("projectdescription");
            projectMemberCount =jsonProjectDetailsObject.getString("projectname");
            defaultProjectGroupName= jsonProjectDetailsObject.getString("defaultgroup");



            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("projectOwner", projectOwner);
            editor.putBoolean("canratemember", canRateMemeber);

            editor.putBoolean("canassigntask", canAssignTask);
            editor.putBoolean("cancreatetask", canCreateTask);
            editor.putBoolean("issupervisor", isSupervisor);
            editor.putString("projectstartdate", startDateToString);
            editor.putString("projectenddate", endDateToString);
            editor.putString("projectDefaultGroup",  defaultProjectGroupName);
            editor.commit();

            editProjectDetailsObject.put("projectName",projectName);
            editProjectDetailsObject.put("projectenddate",endDateToString);
            editProjectDetailsObject.put("projectstartdate",startDateToString);
            editProjectDetailsObject.put("projectdescription", projectDescription);
            editProjectDetailsObject.put("projecttaskscount", projectTaskCount);

            projectOwnerNameView.setText(projectOwner);
            projectNameView.setText(projectName);
            projectStartDateView.setText(startDateToString);
            projectEndDateView.setText(endDateToString);

           projectMemebersView.setText(jsonProjectDetailsObject.getString("projectmemberscount"));
           projectTaskView.setText(projectTaskCount);
            projectSummaryView.setText(jsonProjectDetailsObject.getString("projectdescription"));
             percentageDone = Float.parseFloat(jsonProjectDetailsObject.getString("projectdonepercentage"));

            projectAllMembersJSONArray = jsonProjectDetailsObject.getJSONArray("allProjectMemebers");

        }  catch (JSONException e) {
            e.printStackTrace();
        }




        PieChart projectStatusChart = (PieChart) findViewById(R.id.statusChart);
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
        viewProjectMemeberGeneratorClass =new ViewProjectMemeberGeneratorClass (projectAllMembersJSONArray,activity,memeberLayoutIdHSView,projectName);

    }

public void editProject(View view)
{
    Intent vPAIntent = new Intent(ViewProjectActivity.this, EditProjectActivity.class);
    vPAIntent.putExtra("editProjectInfo",editProjectDetailsObject.toString());
    ViewProjectActivity.this.startActivity(vPAIntent);

}


    public void viewProjectCommentsBtn(View view)
    {

            Intent VPAIntent = new Intent(ViewProjectActivity.this, ProjectCommentsActivity.class);

            ViewProjectActivity.this.startActivity(VPAIntent);


    }



   public void showFABMenu()
   {
       isFabShow=true;

       delegateProjectFloatingButton.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
       projectGroups_MembersFloatingButton.animate().translationY(-getResources().getDimension(R.dimen.standard_115));
       editProjectFloatingButton.animate().translationY(-getResources().getDimension(R.dimen.standard_165));
   }
   public void closeFABMenu()
   {
       isFabShow=false;
       delegateProjectFloatingButton.animate().translationY(0);
       projectGroups_MembersFloatingButton.animate().translationY(0);
       editProjectFloatingButton.animate().translationY(0);
   }





    public void reviewUserProjectPrivileges (View view)
    {
        int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                popUpViewLarge(view);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                popUpViewNormal(view);
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                // friendsSuggestionsLayout.friendSuggestnListVwXLargeScrnSizeLT();
                break;
            case Configuration.SCREENLAYOUT_SIZE_UNDEFINED:
                break;

            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                // friendsSuggestionsLayout.friendSuggestnListVwSmallScreenSizeLT();
                break;
            default:

        }

    }

    public void popUpViewNormal(View view)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout canDelegateLinearLayout = new LinearLayout(activity);
        canDelegateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canDelegateLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canDelegateLinearLayout.setLayoutParams(canDelegateLinearLayoutLLP);

        CheckBox canDelegateCheckBox = new CheckBox(activity);
       // LinearLayout.LayoutParams canDelegateCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
      //  canDelegateCheckBox.setLayoutParams(canDelegateCheckBoxLLP);
        canDelegateCheckBox.setChecked(true);
        canDelegateCheckBox.setScaleY(.8f);
        canDelegateCheckBox.setScaleX(.8f);
        canDelegateCheckBox.setClickable(false);

        TextView canDelegateTextView = new TextView(activity);
        LinearLayout.LayoutParams canDelegateTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canDelegateTextViewLLP.setMargins(15,0,0,0);
        canDelegateTextView.setTextSize(16);
       ///// canDelegateTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canDelegateTextView.setTextColor(getResources().getColor(R.color.color30));
        canDelegateTextView.setLayoutParams(canDelegateTextViewLLP);

        canDelegateLinearLayout.addView(canDelegateCheckBox);
        canDelegateLinearLayout.addView(canDelegateTextView);



        LinearLayout canRateMemberLinearLayout = new LinearLayout(activity);
        canRateMemberLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canRateMemberLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canRateMemberLinearLayout.setLayoutParams(canRateMemberLinearLayoutLLP);

        CheckBox canRateMemberCheckBox = new CheckBox(activity);
       // LinearLayout.LayoutParams canRateMemberCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
       // canRateMemberCheckBox.setLayoutParams(canRateMemberCheckBoxLLP);
        canRateMemberCheckBox.setScaleX(.8f);
        canRateMemberCheckBox.setScaleY(.8f);
        canRateMemberCheckBox.setChecked(true);
        canRateMemberCheckBox.setClickable(false);

        TextView canRateMemberTextView = new TextView(activity);
        LinearLayout.LayoutParams canRateMemberTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canRateMemberTextViewLLP.setMargins(15,0,0,0);
        canRateMemberTextView.setLayoutParams(canRateMemberTextViewLLP);
        canRateMemberTextView.setTextSize(16);
       ///// canRateMemberTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canRateMemberTextView.setTextColor(getResources().getColor(R.color.color30));

        canRateMemberLinearLayout.addView(canRateMemberCheckBox);
        canRateMemberLinearLayout.addView(canRateMemberTextView);


        LinearLayout canAssignTaskLinearLayout = new LinearLayout(activity);
        canAssignTaskLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canAssignTaskLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canAssignTaskLinearLayout.setLayoutParams(canAssignTaskLinearLayoutLLP);

        CheckBox canAssignTaskCheckBox = new CheckBox(activity);
      //  LinearLayout.LayoutParams canAssignTaskCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
       // canAssignTaskCheckBox.setLayoutParams(canAssignTaskCheckBoxLLP);
        canAssignTaskCheckBox.setScaleY(.8f);
        canAssignTaskCheckBox.setScaleX(.8f);
        canAssignTaskCheckBox.setChecked(true);
        canAssignTaskCheckBox.setClickable(false);

        TextView canAssignTaskTextView = new TextView(activity);
        LinearLayout.LayoutParams canAssignTaskTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canAssignTaskTextViewLLP.setMargins(15,0,0,0);
        canAssignTaskTextView.setLayoutParams(canAssignTaskTextViewLLP);
        canAssignTaskTextView.setTextSize(16);
       //// canAssignTaskTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canAssignTaskTextView.setTextColor(getResources().getColor(R.color.color30));

        canAssignTaskLinearLayout.addView(canAssignTaskCheckBox);
        canAssignTaskLinearLayout.addView(canAssignTaskTextView);



        LinearLayout canCreateTaskLinearLayout = new LinearLayout(activity);
        canCreateTaskLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canCreateTaskLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canCreateTaskLinearLayout.setLayoutParams(canCreateTaskLinearLayoutLLP);

        CheckBox canCreateTaskCheckBox = new CheckBox(activity);
       // LinearLayout.LayoutParams canCreateTaskCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
       // canCreateTaskCheckBox.setLayoutParams(canCreateTaskCheckBoxLLP);
        canCreateTaskCheckBox.setScaleY(.8f);
        canCreateTaskCheckBox.setScaleY(.8f);
        canCreateTaskCheckBox.setChecked(true);

        TextView canCreateTaskTextView = new TextView(activity);
        LinearLayout.LayoutParams canCreateTaskTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canCreateTaskTextViewLLP.setMargins(15,0,0,0);
        canCreateTaskTextView.setLayoutParams(canCreateTaskTextViewLLP);
        canCreateTaskTextView.setTextSize(16);
      ////  canCreateTaskTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canCreateTaskTextView.setTextColor(getResources().getColor(R.color.color30));

        canCreateTaskLinearLayout.addView(canCreateTaskCheckBox);
        canCreateTaskLinearLayout.addView(canCreateTaskTextView);



        LinearLayout  isSupervisorLinearLayout = new LinearLayout(activity);
        canDelegateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams  isSupervisorLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        isSupervisorLinearLayout.setLayoutParams( isSupervisorLinearLayoutLLP);

        CheckBox  isSupervisorCheckBox = new CheckBox(activity);
      //  LinearLayout.LayoutParams  isSupervisorCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
       // isSupervisorCheckBox.setLayoutParams( isSupervisorCheckBoxLLP);
        isSupervisorCheckBox.setScaleX(.8f);
        isSupervisorCheckBox.setScaleY(.8f);
        isSupervisorCheckBox.setChecked(true);
        isSupervisorCheckBox.setClickable(false);

        TextView  isSupervisorTextView = new TextView(activity);
        LinearLayout.LayoutParams  isSupervisorTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        isSupervisorTextViewLLP.setMargins(15,0,0,0);
        isSupervisorTextView.setLayoutParams( isSupervisorTextViewLLP);
        isSupervisorTextView.setTextSize(16);
      ////  isSupervisorTextView.setTypeface(getResources().getFont(R.font.montserrat));
        isSupervisorTextView.setTextColor(getResources().getColor(R.color.color30));

        isSupervisorLinearLayout.addView( isSupervisorCheckBox);
        isSupervisorLinearLayout.addView( isSupervisorTextView);


        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupbubblelayout,null);
        BubbleLayout popUpLsyout =(BubbleLayout) customView.findViewById(R.id.popLayoutId);
        popUpLsyout.setArrowDirection(RIGHT);

        LinearLayout outerLinearLayout = new LinearLayout(activity);
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams outerLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLLP);
        outerLinearLayout.setBackgroundColor(getResources().getColor(R.color.trans1));
               if(isSupervisor)
               {
                   isSupervisorTextView.setText("You are a supervisor");
                   outerLinearLayout.addView(isSupervisorLinearLayout);
               }
                if( canDelegateProject) {
                    canDelegateTextView.setText("You can deligate this project");
                    outerLinearLayout.addView(canDelegateLinearLayout);
                }
                if(canAssignTask) {
                    canAssignTaskTextView.setText("You can assign tasks ");
                    outerLinearLayout.addView(canAssignTaskLinearLayout);
                }
           if(canCreateTask) {
               canCreateTaskTextView.setText("You can create task");
               outerLinearLayout.addView(canCreateTaskLinearLayout);
           }
        if(canRateMemeber) {
            canRateMemberTextView.setText("You can rate members");
            outerLinearLayout.addView(canRateMemberLinearLayout);
        }
/*
        popUpLsyout.addView(canAssignTaskLinearLayout);
        popUpLsyout.addView(canCreateTaskLinearLayout);
        popUpLsyout.addView(canRateMemberLinearLayout);
        popUpLsyout.addView(isSupervisorLinearLayout);
*/
        popUpLsyout.setBackgroundColor(getResources().getColor(R.color.trans1));
       popUpLsyout.addView(outerLinearLayout);

        popUp = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        );
        popUp.setOutsideTouchable(true);
        popUp.setFocusable(true);
        popUp.setElevation(5.0f);


            popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, view.getWidth()+location[0]+60, view.getHeight() + location[1]+5);




    }



    public void popUpViewLarge(View view)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        LinearLayout closeTextViewLinearLayout = new LinearLayout(activity);
        closeTextViewLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams closeTextViewLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        closeTextViewLinearLayout.setLayoutParams(closeTextViewLinearLayoutLLP);



        TextView closeTextView = new TextView(activity);
        LinearLayout.LayoutParams closeTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        closeTextViewLLP.setMargins(20,20,0,20);
        closeTextView.setLayoutParams(closeTextViewLLP);
        closeTextView.setTypeface(fontAwesomeIcon);
        closeTextView.setText(activity.getResources().getString(R.string.closebutton));
        closeTextView.setTextSize(15);
        closeTextView.setClickable(true);
        //// canAssignTaskTextView.setTypeface(getResources().getFont(R.font.montserrat));
        closeTextView.setTextColor(getResources().getColor(R.color.color30));
        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.dismiss();
            }
        });


        closeTextViewLinearLayout.addView(closeTextView);


        LinearLayout canDelegateLinearLayout = new LinearLayout(activity);
        canDelegateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canDelegateLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canDelegateLinearLayout.setLayoutParams(canDelegateLinearLayoutLLP);

        CheckBox canDelegateCheckBox = new CheckBox(activity);
        // LinearLayout.LayoutParams canDelegateCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
        //  canDelegateCheckBox.setLayoutParams(canDelegateCheckBoxLLP);
        canDelegateCheckBox.setChecked(true);
        canDelegateCheckBox.setScaleY(1.1f);
        canDelegateCheckBox.setScaleX(1.1f);
        canDelegateCheckBox.setClickable(false);

        TextView canDelegateTextView = new TextView(activity);
        LinearLayout.LayoutParams canDelegateTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canDelegateTextViewLLP.setMargins(20,0,0,0);
        canDelegateTextView.setTextSize(20);
        ///// canDelegateTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canDelegateTextView.setTextColor(getResources().getColor(R.color.color30));
        canDelegateTextView.setLayoutParams(canDelegateTextViewLLP);

        canDelegateLinearLayout.addView(canDelegateCheckBox);
        canDelegateLinearLayout.addView(canDelegateTextView);



        LinearLayout canRateMemberLinearLayout = new LinearLayout(activity);
        canRateMemberLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canRateMemberLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canRateMemberLinearLayout.setLayoutParams(canRateMemberLinearLayoutLLP);

        CheckBox canRateMemberCheckBox = new CheckBox(activity);
        // LinearLayout.LayoutParams canRateMemberCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
        // canRateMemberCheckBox.setLayoutParams(canRateMemberCheckBoxLLP);
        canRateMemberCheckBox.setScaleX(1.1f);
        canRateMemberCheckBox.setScaleY(1.1f);
        canRateMemberCheckBox.setChecked(true);
        canRateMemberCheckBox.setClickable(false);

        TextView canRateMemberTextView = new TextView(activity);
        LinearLayout.LayoutParams canRateMemberTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canRateMemberTextViewLLP.setMargins(20,0,0,0);
        canRateMemberTextView.setLayoutParams(canRateMemberTextViewLLP);
        canRateMemberTextView.setTextSize(20);
        ///// canRateMemberTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canRateMemberTextView.setTextColor(getResources().getColor(R.color.color30));

        canRateMemberLinearLayout.addView(canRateMemberCheckBox);
        canRateMemberLinearLayout.addView(canRateMemberTextView);


        LinearLayout canAssignTaskLinearLayout = new LinearLayout(activity);
        canAssignTaskLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canAssignTaskLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canAssignTaskLinearLayout.setLayoutParams(canAssignTaskLinearLayoutLLP);

        CheckBox canAssignTaskCheckBox = new CheckBox(activity);
        //  LinearLayout.LayoutParams canAssignTaskCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
        // canAssignTaskCheckBox.setLayoutParams(canAssignTaskCheckBoxLLP);
        canAssignTaskCheckBox.setScaleY(1.1f);
        canAssignTaskCheckBox.setScaleX(1.1f);
        canAssignTaskCheckBox.setChecked(true);
        canAssignTaskCheckBox.setClickable(false);

        TextView canAssignTaskTextView = new TextView(activity);
        LinearLayout.LayoutParams canAssignTaskTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canAssignTaskTextViewLLP.setMargins(20,0,0,0);
        canAssignTaskTextView.setLayoutParams(canAssignTaskTextViewLLP);
        canAssignTaskTextView.setTextSize(20);
        //// canAssignTaskTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canAssignTaskTextView.setTextColor(getResources().getColor(R.color.color30));

        canAssignTaskLinearLayout.addView(canAssignTaskCheckBox);
        canAssignTaskLinearLayout.addView(canAssignTaskTextView);



        LinearLayout canCreateTaskLinearLayout = new LinearLayout(activity);
        canCreateTaskLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams canCreateTaskLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        canCreateTaskLinearLayout.setLayoutParams(canCreateTaskLinearLayoutLLP);

        CheckBox canCreateTaskCheckBox = new CheckBox(activity);
        // LinearLayout.LayoutParams canCreateTaskCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
        // canCreateTaskCheckBox.setLayoutParams(canCreateTaskCheckBoxLLP);
        canCreateTaskCheckBox.setScaleY(1.1f);
        canCreateTaskCheckBox.setScaleY(1.1f);
        canCreateTaskCheckBox.setChecked(true);

        TextView canCreateTaskTextView = new TextView(activity);
        LinearLayout.LayoutParams canCreateTaskTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        canCreateTaskTextViewLLP.setMargins(20,0,0,0);
        canCreateTaskTextView.setLayoutParams(canCreateTaskTextViewLLP);
        canCreateTaskTextView.setTextSize(20);
        ////  canCreateTaskTextView.setTypeface(getResources().getFont(R.font.montserrat));
        canCreateTaskTextView.setTextColor(getResources().getColor(R.color.color30));

        canCreateTaskLinearLayout.addView(canCreateTaskCheckBox);
        canCreateTaskLinearLayout.addView(canCreateTaskTextView);



        LinearLayout  isSupervisorLinearLayout = new LinearLayout(activity);
        canDelegateLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams  isSupervisorLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,80);
        isSupervisorLinearLayout.setLayoutParams( isSupervisorLinearLayoutLLP);

        CheckBox  isSupervisorCheckBox = new CheckBox(activity);
        //  LinearLayout.LayoutParams  isSupervisorCheckBoxLLP = new LinearLayout.LayoutParams(40,40);
        // isSupervisorCheckBox.setLayoutParams( isSupervisorCheckBoxLLP);
        isSupervisorCheckBox.setScaleX(1.1f);
        isSupervisorCheckBox.setScaleY(1.1f);
        isSupervisorCheckBox.setChecked(true);
        isSupervisorCheckBox.setClickable(false);

        TextView  isSupervisorTextView = new TextView(activity);
        LinearLayout.LayoutParams  isSupervisorTextViewLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        isSupervisorTextViewLLP.setMargins(20,0,0,0);
        isSupervisorTextView.setLayoutParams( isSupervisorTextViewLLP);
        isSupervisorTextView.setTextSize(20);
        ////  isSupervisorTextView.setTypeface(getResources().getFont(R.font.montserrat));
        isSupervisorTextView.setTextColor(getResources().getColor(R.color.color30));

        isSupervisorLinearLayout.addView( isSupervisorCheckBox);
        isSupervisorLinearLayout.addView( isSupervisorTextView);


        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupbubblelayout,null);
        BubbleLayout popUpLsyout =(BubbleLayout) customView.findViewById(R.id.popLayoutId);
        popUpLsyout.setArrowDirection(RIGHT);

        LinearLayout outerLinearLayout = new LinearLayout(activity);
        outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams outerLinearLayoutLLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        outerLinearLayout.setLayoutParams(outerLinearLayoutLLP);
        outerLinearLayout.setBackgroundColor(getResources().getColor(R.color.trans1));

        outerLinearLayout.addView(closeTextViewLinearLayout);


        if(isSupervisor)
        {
            isSupervisorTextView.setText("You are a supervisor");
            outerLinearLayout.addView(isSupervisorLinearLayout);
        }
        if( canDelegateProject) {
            canDelegateTextView.setText("You can deligate this project");
            outerLinearLayout.addView(canDelegateLinearLayout);
        }
        if(canAssignTask) {
            canAssignTaskTextView.setText("You can assign tasks ");
            outerLinearLayout.addView(canAssignTaskLinearLayout);
        }
        if(canCreateTask) {
            canCreateTaskTextView.setText("You can create task");
            outerLinearLayout.addView(canCreateTaskLinearLayout);
        }
        if(canRateMemeber) {
            canRateMemberTextView.setText("You can rate members");
            outerLinearLayout.addView(canRateMemberLinearLayout);
        }
/*
        popUpLsyout.addView(canAssignTaskLinearLayout);
        popUpLsyout.addView(canCreateTaskLinearLayout);
        popUpLsyout.addView(canRateMemberLinearLayout);
        popUpLsyout.addView(isSupervisorLinearLayout);
*/
        popUpLsyout.setBackgroundColor(getResources().getColor(R.color.trans1));
        popUpLsyout.addView(outerLinearLayout);

        popUp = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        );
        popUp.setOutsideTouchable(true);

        popUp.setFocusable(true);
        popUp.setElevation(5.0f);


        popUp.showAtLocation(popUpLsyout, Gravity.NO_GRAVITY, view.getWidth()+location[0]+230, view.getHeight() + location[1]+5);




    }

public void project_groups_members(View view)
{
    Intent dAI = new Intent(ViewProjectActivity.this, ProjectMembersGroupInviteActivity.class);
    ViewProjectActivity.this.startActivity(dAI);
}

    @Override
    public void onBackPressed() {

    }

    public void delegateProject(View view)
    {
        if(canDelegateProject) {

            Intent dAI = new Intent(ViewProjectActivity.this, DelegateUndelegateProjectActivity.class);
            ViewProjectActivity.this.startActivity(dAI);

        }
        else
        {
            Toast.makeText(this,"You are not allowed to deligate this project another memeber", Toast.LENGTH_LONG).show();
        }

    }



    public void backToHomePage(View view)
    {
        Intent VPAIntent = new Intent(ViewProjectActivity.this, HomeActivity.class);
        ViewProjectActivity.this.startActivity(VPAIntent);
    }







    public  void viewProjectTasksBtn(View view)
    {

        Intent vPAIntent = new Intent(ViewProjectActivity.this, ViewProjectTaskActivity.class);

        ViewProjectActivity.this.startActivity(vPAIntent);
    }









    public String getFullProjectDetails()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectName);
            postDataParams.put("nameOfUser",userName);

           httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/gepfi/",postDataParams, activity,"application/json", "application/json");
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
