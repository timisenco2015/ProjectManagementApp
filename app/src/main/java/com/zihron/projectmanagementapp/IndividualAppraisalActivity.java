package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndividualAppraisalActivity extends AppCompatActivity {

    private HttpRequestClass httpRequestClass;
    public static PopupWindow popUp;
    String userName;
    private Activity activity;
    private int appraisalYear;
    private String appraisalMonth;
    private String appraisalWeek;
    private ArrayList<String> monthList;
    private List<String> weekly_Yearly_List;
    private SharedPreferences sharedPreferences;
    private Spinner weekMonthYearSpinner;
    public static final float MAX =6, MIN=0f;
    public static final int NB_QUALITIES = 6;
    private boolean isSpinnerEnable = false;
    private Typeface fontAwesomeIcon;
    private String selectCriteria;
private  ArrayAdapter<String> dataAdapter;
private LineChart individualLineChart;
private RadarChart individualRadarChart;
private JSONArray appraisalReportList;
private TextView appraisalLabelTextView;
private Calendar cal;
private ToggleButton individualToggleButton;
    private TopSheetBehavior topSheetBehavior;
    private LinearLayout topSheetLayout;
private TextView close_open_topSheetTextView;
private LinearLayout getAllAppraisalReviewButton;
private Gson googleJson;
private Spinner projectSelectSpinner;
private Spinner  projectTaskSelectSpinner;
private ArrayList<String> allUserAppraisedProjectList;
private ArrayList<String> allUserAppraisedProjectTaskList;
private String selectedProject;
private String selectedProjectTask;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_appraisal);

        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
       topSheetLayout = (LinearLayout)findViewById(R.id.top_sheet);
        topSheetBehavior = TopSheetBehavior.from(topSheetLayout);
        topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
        projectSelectSpinner = (Spinner)findViewById(R.id.projectSelectSpinnerId);
        projectTaskSelectSpinner= (Spinner)findViewById(R.id.taskSelectSpinnerId);
        individualLineChart = (LineChart) findViewById(R.id.individualLineChartId);
        individualRadarChart = (RadarChart) findViewById(R.id.individualRadarChartId);
        weekMonthYearSpinner = (Spinner)findViewById(R.id.individualWeekSpinnerId);
        appraisalLabelTextView = (TextView)findViewById(R.id.appraisalLabelTxtVWId);
        getAllAppraisalReviewButton = (LinearLayout)findViewById(R.id.getAllAppraisalReviewBtnId);
        close_open_topSheetTextView = (TextView)findViewById(R.id.close_open_topSheetId);
        selectCriteria="Week";
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");
        close_open_topSheetTextView.setTypeface(fontAwesomeIcon);
        close_open_topSheetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topSheetBehavior.getState()==TopSheetBehavior.STATE_COLLAPSED)
                {
                    topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED);
                    close_open_topSheetTextView.setText(getResources().getString(R.string.closebutton));

                }
                else
                {
                    topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
                    close_open_topSheetTextView.setText(getResources().getString(R.string.dropdownmenu));

                }
            }
        });

       try {
            String result = getAllIndividualAppraisedProject(userName);
            JSONArray tempArray;
            if(result!=null)
            {
                tempArray = new JSONArray(result);
                googleJson = new Gson();
                allUserAppraisedProjectList = googleJson.fromJson(tempArray.toString(), ArrayList.class);
                dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  allUserAppraisedProjectList);
                allUserAppraisedProjectList.add(0,"All");
                projectSelectSpinner.setSelection(0,false);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                projectSelectSpinner.setAdapter(dataAdapter);
            }

        }  catch (JSONException e) {
            e.printStackTrace();
        }


        projectSelectSpinner.setSelection(0,false);
        projectSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAllAppraisalReviewButton.setEnabled(true);
                selectedProject = parent.getItemAtPosition(position).toString();
                if(!selectedProject.equalsIgnoreCase("All")) {
                    populateProjectTaskSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        projectTaskSelectSpinner.setSelection(0,false);
        projectTaskSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedProjectTask = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthList = new ArrayList<String>();
        monthList.add("All");
        monthList.add("January");
        monthList.add("Feburary");
        monthList.add("March");
        monthList.add("April");
        monthList.add("May");
        monthList.add("June");
        monthList.add("July");
        monthList.add("August");
        monthList.add("September");
        monthList.add("October");
        monthList.add("November");
        monthList.add("December");



        individualToggleButton = (ToggleButton)findViewById(R.id.individualToggleButtonId);
        weekMonthYearSpinner.setEnabled(false);
         cal = Calendar.getInstance();
       cal.setTime(new Date());
       appraisalYear = cal.get(Calendar.YEAR);
        appraisalMonth = "All";
        appraisalWeek = "Week 0";
        populateSpinnerWeekly();
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weekly_Yearly_List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekMonthYearSpinner.setAdapter(dataAdapter);
        weekMonthYearSpinner.setSelection(0,false);
        weekMonthYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerEnable) {
                    if(selectCriteria.equalsIgnoreCase("Year"))
                    {
                        appraisalYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                    }
                    else if(selectCriteria.equalsIgnoreCase("Month"))
                    {
                        appraisalMonth = parent.getItemAtPosition(position).toString();

                    }
                    else if(selectCriteria.equalsIgnoreCase("Week"))
                    {
                        appraisalWeek = parent.getItemAtPosition(position).toString();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


      //  getWeeklyData();

        getAllAppraisalReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = getIndividualAppraisalDetails(selectedProject, userName, selectedProjectTask, ""+appraisalYear, appraisalMonth, appraisalWeek);
                   if(result!=null) {
                       appraisalReportList = new JSONArray(result);

                       displayLineChart();
                       displayRadarChart();
                   }
                   else
                   {
                           topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED);
                           if (popUp!=null && popUp.isShowing()) {
                               popUp.dismiss();
                           }
                           individualLineChart.setNoDataText("No chart data available");
                           individualRadarChart.setNoDataText("No chart data available");
                           Toast.makeText(activity,"All",Toast.LENGTH_LONG).show();

                           individualLineChart.clear();
                           individualRadarChart.clear();
                           individualLineChart.invalidate();
                           individualRadarChart.invalidate();
                           popUpView("No appraisal review to display");
                       }
                   }  catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }
        });



    }
    @Override
    public void onBackPressed() {

    }
public void populateProjectTaskSpinner()
{
   try {

        String result = getAllIndividualAppraisedProjectTasks(userName,selectedProject);
        JSONArray tempArray;
            if(result!=null) {
                tempArray = new JSONArray(result);

                googleJson = new Gson();
                allUserAppraisedProjectTaskList = googleJson.fromJson(tempArray.toString(), ArrayList.class);

                dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,  allUserAppraisedProjectTaskList);
                allUserAppraisedProjectTaskList.add(0, "All");
                projectTaskSelectSpinner.setSelection(0, false);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                projectTaskSelectSpinner.setAdapter(dataAdapter);
            }
    }  catch (JSONException e) {
        e.printStackTrace();
    }

}
    public void popUpView(String textMessage)
    {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        TextView tv;

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.popupwindowlayout,null);
        LinearLayout outerLinearlayout = (LinearLayout)customView.findViewById(R.id.popLayoutId);
        outerLinearlayout.removeAllViews();

        LinearLayout innerContainer = new LinearLayout(activity);
        innerContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(900, 170);

        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,30,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(14);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
       // projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));

        final CircleImageView closeImageVW = new CircleImageView(activity);
        LinearLayout.LayoutParams rlpcloseImageVWLayout = new LinearLayout.LayoutParams(60, 60);
        rlpcloseImageVWLayout.setMargins(40,40,20,0);
        closeImageVW.setLayoutParams(rlpcloseImageVWLayout);
        closeImageVW.setClickable(true);
        closeImageVW.setBackground(activity.getResources().getDrawable(R.drawable.closeimage,null));
        closeImageVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.dismiss();
            }
        });
        innerContainer.addView(closeImageVW);
        innerContainer.addView(projectNameTextView);


        outerLinearlayout.addView(innerContainer);

        popUp = new PopupWindow(
                customView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popUp.setElevation(30.0f);

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 10, 1100);




    }
    public void populateSpinnerWeekly()
    {

        appraisalWeek = "Week "+cal.get(Calendar.WEEK_OF_YEAR);
        weekly_Yearly_List = new ArrayList<String>();
        weekly_Yearly_List.add(appraisalWeek);
        weekly_Yearly_List.add("Week 0");
        for(int i=1; i<53; i++)
        {
            weekly_Yearly_List.add("Week "+i);
        }
    }


    public void populateSpinnerYearly()
    {
        appraisalYear = cal.get(Calendar.YEAR);

        weekly_Yearly_List = new ArrayList<String>();
        weekly_Yearly_List.add(""+appraisalYear);
        for(int i=1; i<10; i++)
        {
            weekly_Yearly_List.add(""+(appraisalYear+1));
        }
    }


    public void yearViewChange(View view)
    {
        populateSpinnerYearly();
        selectCriteria="Year";
        weekMonthYearSpinner.setEnabled(false);
        individualToggleButton.setChecked(false);
        individualToggleButton.setTextOff("Disabled");
        isSpinnerEnable =false;
        appraisalLabelTextView.setText("Yearly Report Selected");
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weekly_Yearly_List);
        weekMonthYearSpinner.setSelection(0,false);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekMonthYearSpinner.setAdapter(dataAdapter);
       weekMonthYearSpinner.invalidate();


    }

    public void monthViewChange(View view)
    {
        selectCriteria ="Month";
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthList);
        appraisalLabelTextView.setText("Monthly Report Selected");
        weekMonthYearSpinner.setEnabled(false);
        individualToggleButton.setChecked(false);
        individualToggleButton.setTextOff("Disabled");
        isSpinnerEnable =false;
        weekMonthYearSpinner.setSelection(0,false);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekMonthYearSpinner.setAdapter(dataAdapter);
        weekMonthYearSpinner.invalidate();

    }

    public void weekViewChange (View view)
    {
        selectCriteria="Week";
        populateSpinnerWeekly();
        appraisalLabelTextView.setText("Weekly Report Selected");
        weekMonthYearSpinner.setEnabled(false);
        individualToggleButton.setChecked(false);
        individualToggleButton.setTextOff("Disabled");
        isSpinnerEnable =false;
        weekMonthYearSpinner.setSelection(0,false);
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weekly_Yearly_List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekMonthYearSpinner.setAdapter(dataAdapter);
        weekMonthYearSpinner.invalidate();


    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            isSpinnerEnable=true;
            weekMonthYearSpinner.setEnabled(true);
        } else {
            weekMonthYearSpinner.setEnabled(false);
            isSpinnerEnable=false;
        }
    }


   // int understandingValue, int qualityAssuranceValue, int teamValue

    public void displayRadarChart()
    {


        //   appraisalStatusRadarChart.setBackgroundColor(getResources().getColor(R.color.color15));
        individualRadarChart.getDescription().setEnabled(true);
        individualRadarChart.setWebLineWidth(0f);
        individualRadarChart.setWebLineWidthInner(2f);
        individualRadarChart.setWebColor(activity.getResources().getColor(R.color.color24));
        individualRadarChart.setWebColorInner(activity.getResources().getColor(R.color.color17));
        individualRadarChart.setWebAlpha(100);

        individualRadarChart.setScaleX(1);
        individualRadarChart.setScaleY(1);

        List<RadarEntry> entries = new ArrayList<RadarEntry>();
        try {
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(0).getInt(0)/ appraisalReportList.getJSONArray(0).getInt(2)));
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(1).getInt(0)/ appraisalReportList.getJSONArray(1).getInt(2)));
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(2).getInt(0)/ appraisalReportList.getJSONArray(2).getInt(2)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        XAxis xAxis =  individualRadarChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f);
        xAxis.setYOffset(1f);
        xAxis.setTextSize(8f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mFactors = new String[]{"Role Understanding", "Quality Assurance",
                    "Team Value"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFactors[(int) value % mFactors.length];
            }});
        xAxis.setTextColor(activity.getResources().getColor(R.color.color24));
        YAxis yAxis =  individualRadarChart.getYAxis();
        yAxis.setEnabled(true);

        // appraisalStatusLineChart.animateXY(4000, 4000);
        //appraisalStatusLineChart.invalidate();
        yAxis.setLabelCount(NB_QUALITIES,true);
        yAxis.setTextSize(12f);
        yAxis.setAxisMaximum(MAX);
        yAxis.setAxisMinimum(MIN);

        yAxis.setDrawTopYLabelEntry(true);
        yAxis.setDrawLabels(true);
        RadarDataSet radarDataSet = new RadarDataSet(entries, "Label");
        radarDataSet.setColor(activity.getResources().getColor(R.color.color28));
        radarDataSet.setFillColor(activity.getResources().getColor(R.color.color18));
        radarDataSet.setDrawFilled(true);
        radarDataSet.setFillAlpha(180);

        radarDataSet.setLineWidth(2f);
        radarDataSet.setFormSize(8f);
        radarDataSet.setDrawValues(true);
        List<IRadarDataSet> dataSets = new ArrayList<IRadarDataSet>();
        dataSets.add(radarDataSet);
        RadarData radarData = new RadarData(dataSets);
        radarData.setValueTextSize(8f);
        radarData.setDrawValues(true);

        individualRadarChart.getDescription().setEnabled(false);
        radarData.setValueTextColor(activity.getResources().getColor(R.color.color24));
        individualRadarChart.setData(radarData);
        individualRadarChart.setSkipWebLineCount(0);
        individualRadarChart.getLegend().setEnabled(false);
        individualRadarChart.invalidate();

    }

    public void displayLineChart()
    {
        List<Entry> entries = new ArrayList<Entry>();
        try {
            entries.add(new Entry(0,appraisalReportList.getJSONArray(0).getInt(0)/ appraisalReportList.getJSONArray(0).getInt(2)));
            entries.add(new Entry(1, appraisalReportList.getJSONArray(1).getInt(0)/appraisalReportList.getJSONArray(1).getInt(2)));
            entries.add(new Entry(2, appraisalReportList.getJSONArray(2).getInt(0)/appraisalReportList.getJSONArray(2).getInt(2)));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        LineDataSet dataSet = new LineDataSet(entries, "Label");
        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setCircleColors(ColorTemplate.COLORFUL_COLORS );
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSet);
        XAxis xAxis = individualLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(90);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mFactors = new String[]{"Role Understanding", "Quality Assurance",
                    "Team Value"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFactors[(int) value % mFactors.length];
            }});
        xAxis.setDrawGridLines(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = individualLineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(MAX);
        leftAxis.setAxisMinimum(MIN);
        leftAxis.setXOffset(0f);
        YAxis rightAxis = individualLineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);
        individualLineChart.setData(lineData);
        individualLineChart.animateXY(4000, 4000);
        individualLineChart.invalidate();
        individualLineChart.getDescription().setEnabled(false);

        individualLineChart.getLegend().setEnabled(false);
    }

public void navigateToApprActivity(View view)
{

   Intent iAAIntent = new Intent(IndividualAppraisalActivity.this,HomeActivity.class);
   startActivity(iAAIntent);



}


    public String getIndividualAppraisalDetails(String projectName, String userName, String projectTaskName, String appraisalYear, String appriasalMonth, String appraisalWeek)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams = new JSONObject();
            postDataParams = new JSONObject();
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfUser", userName);
            postDataParams.put("nameOfProjectTasks", projectTaskName);
            postDataParams.put("appraisalYear", appraisalYear);
            postDataParams.put("appraisalMonth", appraisalMonth);
            postDataParams.put("appraisalWeek", appraisalWeek);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fsear/",postDataParams, activity,"application/json", "application/json");
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





    public String getAllIndividualAppraisedProject(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faap/",postDataParams, activity,"application/json", "application/json");
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




    public String getAllIndividualAppraisedProjectTasks(String userName, String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);
            postDataParams.put("nameOfProject" , projectName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faapt/",postDataParams, activity,"application/json", "application/json");
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




