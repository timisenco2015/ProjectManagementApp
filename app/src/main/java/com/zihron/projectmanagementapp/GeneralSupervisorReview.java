package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralSupervisorReview extends AppCompatActivity {
    public static PopupWindow popUp;
  private  LineChart generalAppraisalReviewLineChart;
    private        RadarChart generalAppraisalReviewRadarChart;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private Activity activity;
    private static final float MAX =6, MIN=0f;
    private static final int NB_QUALITIES = 6;
    private JSONArray appraisalReportList;
    private SharedPreferences sharedPreferences;
    private String userName;
    private HttpRequestClass httpRequestClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_supervisor_review);
        generalAppraisalReviewLineChart = (LineChart) findViewById(R.id.generalLineChartId);
        generalAppraisalReviewRadarChart = (RadarChart)findViewById(R.id.generalRadarChartId);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);

            String result = getSupervisorAppraisalDetails(userName);
        try {
            appraisalReportList = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if( appraisalReportList.length()>0)
            {

                displayRadarChart();
                displayLineChart();
            }
            else
            {
                if(popUp !=null && popUp.isShowing())
                {
                    popUp.dismiss();
                }

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        popUpView("No appraisal report to display");
                    }
                }, 100);




                generalAppraisalReviewLineChart.setNoDataText("No chart data available");
                generalAppraisalReviewRadarChart.setNoDataText("No chart data available");
                generalAppraisalReviewLineChart.clear();
                generalAppraisalReviewRadarChart.clear();
                generalAppraisalReviewLineChart.invalidate();
                generalAppraisalReviewRadarChart.invalidate();
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
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(800, 140);

        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,30,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(14);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
      //  projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));

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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 80, 600);




    }

    public void displayRadarChart()
    {


        //   appraisalStatusRadarChart.setBackgroundColor(getResources().getColor(R.color.color15));
        generalAppraisalReviewRadarChart.getDescription().setEnabled(true);
        generalAppraisalReviewRadarChart.setWebLineWidth(0f);
        generalAppraisalReviewRadarChart.setWebLineWidthInner(2f);
        generalAppraisalReviewRadarChart.setWebColor(activity.getResources().getColor(R.color.color24));
        generalAppraisalReviewRadarChart.setWebColorInner(activity.getResources().getColor(R.color.color17));
        generalAppraisalReviewRadarChart.setWebAlpha(100);
        generalAppraisalReviewRadarChart.setScaleX(1);
        generalAppraisalReviewRadarChart.setScaleY(1);

        List<RadarEntry> entries = new ArrayList<RadarEntry>();
        try {
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(0).getInt(0)/ appraisalReportList.getJSONArray(0).getInt(2)));
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(1).getInt(0)/ appraisalReportList.getJSONArray(1).getInt(2)));
            entries.add(new RadarEntry(appraisalReportList.getJSONArray(2).getInt(0)/ appraisalReportList.getJSONArray(2).getInt(2)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        XAxis xAxis =  generalAppraisalReviewRadarChart.getXAxis();
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
        YAxis yAxis = generalAppraisalReviewRadarChart.getYAxis();
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

        generalAppraisalReviewRadarChart.getDescription().setEnabled(false);
        radarData.setValueTextColor(activity.getResources().getColor(R.color.color24));
        generalAppraisalReviewRadarChart.setData(radarData);
        generalAppraisalReviewRadarChart.setSkipWebLineCount(0);
        generalAppraisalReviewRadarChart.getLegend().setEnabled(false);
        generalAppraisalReviewRadarChart.invalidate();

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
        XAxis xAxis = generalAppraisalReviewLineChart.getXAxis();
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
        YAxis leftAxis = generalAppraisalReviewLineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(MAX);
        leftAxis.setAxisMinimum(MIN);
        leftAxis.setXOffset(0f);
        YAxis rightAxis = generalAppraisalReviewLineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);
        generalAppraisalReviewLineChart.setData(lineData);
        generalAppraisalReviewLineChart.animateXY(4000, 4000);
        generalAppraisalReviewLineChart.invalidate();
        generalAppraisalReviewLineChart.getDescription().setEnabled(false);

        // setCustom(new String[] { "aaaaa", "bbbbb", "ccccc"});
        generalAppraisalReviewLineChart.getLegend().setEnabled(false);
    }



    public String getSupervisorAppraisalDetails(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfUser", userName);


            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fuasar/",postDataParams, activity,"application/json", "application/json");
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




    public void backToHomeActivity(View view)
{
    Intent gSRIntent = new Intent(GeneralSupervisorReview.this, HomeActivity.class);
    startActivity(gSRIntent);
}
}
