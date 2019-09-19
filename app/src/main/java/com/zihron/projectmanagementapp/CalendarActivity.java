package com.zihron.projectmanagementapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;


import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CalendarActivity extends Fragment {
    Typeface fontAwesomeIcon;
    private  int start_year, start_month, start_day;
    private String selectedDateString;
    TextView calendarDateTxtVW;
    TextView doubleBackArrowTV;
    TextView singleBackArrowTv;
    TextView singleForwardArrowTv;
    TextView doubleForwardArrowTv;
    private CountDownTimer countDownTimer;
    private HttpRequestClass httpRequestClass;
    private long dateToMillis;
    private TextView dateNameTextView;
    private SharedPreferences sharedPreferences;
    private String userName;
    private String projectEndDate;
    private JSONArray userAllTaskJSONArray;
    private  ListView taskCardListView;
    private TextView projectNameTextView;
    private int finalDaysRemaining;
    private LinearLayout alltaskDueLinearlayout;
private DatePicker datePicker;
private TextView startDatePickedTextView;
    private Activity activity;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    public static PopupWindow popUp;
    private int myAPI;
    private SwipeRefreshLayout projectActivityRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_calendar, container, false);

        if( ProjectTaskAssignedActivity.popUp !=null)
        {
            ProjectTaskAssignedActivity.popUp.dismiss();
        }

        if( CalendarActivity.popUp !=null)
        {
            CalendarActivity.popUp.dismiss();
        }
        alltaskDueLinearlayout = (LinearLayout) rootView.findViewById(R.id.alltaskDueLLTId);
        projectActivityRefresh = (SwipeRefreshLayout)rootView.findViewById(R.id.projectActivityRefreshId);
        datePicker = (DatePicker)rootView.findViewById(R.id.datePickerId);
        startDatePickedTextView = (TextView)rootView.findViewById(R.id.startDatePickedTxtVWId);
        activity = getActivity();
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);

        sharedPreferences = this.getActivity().getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        Calendar cal = Calendar.getInstance();
        start_year = cal.get(Calendar.YEAR);
        start_month = cal.get(Calendar.MONTH);
        start_day = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(start_year, start_month, start_day);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date startDate = null;
        Date date = new Date();
        myAPI = Build.VERSION.SDK_INT;
        selectedDateString = dateFormat.format(date);
        getUserAllTasksList(userName, selectedDateString);
        startDatePickedTextView.setText("Date Picked: "+selectedDateString);
        try {

            startDate = dateFormat.parse(selectedDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePicker.setMinDate(startDate.getTime());
        projectActivityRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUserAllTasksList(userName, selectedDateString);
                projectActivityRefresh.setRefreshing(false);
            }
        });

        if(myAPI<26)
        {
            datePickerListenerTargetBelow26();
        }
        else if(myAPI>=26)
        {
            datePickerListenerTarget26();
        }



        return rootView;
    }
    @TargetApi(26)
    public void datePickerListenerTarget26()
    {

        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDateString = ""+year+"/"+monthOfYear+"/"+dayOfMonth;


                String dayOfMonthInString = "";
                String monthOfYearInString = "";
                if (dayOfMonth < 10) {
                    dayOfMonthInString = "0" + dayOfMonth;

                } else {
                    dayOfMonthInString = "" + dayOfMonth;

                }

                if ((monthOfYear + 1) < 10) {
                    monthOfYearInString = "0" + (monthOfYear + 1);

                } else {
                    monthOfYearInString = "" + (monthOfYear + 1);

                }
                selectedDateString = "" + year + "/" + monthOfYearInString + "/" + dayOfMonthInString;
                startDatePickedTextView.setText("Date Picked: "+selectedDateString);
                getUserAllTasksList( userName, selectedDateString);
            }
        });
    }


    public void datePickerListenerTargetBelow26()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDateString = ""+year+"/"+monthOfYear+"/"+dayOfMonth;


                String dayOfMonthInString = "";
                String monthOfYearInString = "";
                if (dayOfMonth < 10) {
                    dayOfMonthInString = "0" + dayOfMonth;

                } else {
                    dayOfMonthInString = "" + dayOfMonth;

                }

                if ((monthOfYear + 1) < 10) {
                    monthOfYearInString = "0" + (monthOfYear + 1);

                } else {
                    monthOfYearInString = "" + (monthOfYear + 1);

                }
                selectedDateString = "" + year + "/" + monthOfYearInString + "/" + dayOfMonthInString;
                startDatePickedTextView.setText("Date Picked: "+selectedDateString);
                getUserAllTasksList( userName, selectedDateString);
            }
        });
    }


    @TargetApi(26)
   public void allprojecttasksNormalLayout()
   {
       alltaskDueLinearlayout.removeAllViews();
       CardView containerCardView=null;

     try {
       for (int i=0; i<userAllTaskJSONArray.length();i++) {

           containerCardView = new CardView(activity);
          LinearLayout.LayoutParams containerCardViewLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           containerCardViewLLT.setMargins(0,20,0,80);
          containerCardView.setElevation(50);
          containerCardView.setLayoutParams(containerCardViewLLT);


           LinearLayout innerContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerContainer.setOrientation(LinearLayout.VERTICAL);
           innerContainer.setBackground(this.getResources().getDrawable(R.drawable.calendarborder, null));
           innerContainer.setLayoutParams(innerContainerLLT);

          projectEndDate = userAllTaskJSONArray.getJSONArray(i).getString(3);
           LinearLayout.LayoutParams timerTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           timerTxtViewLayout.setMargins(20,20,0,0);
           TextView timerTxtView = new TextView(activity);
           timerTxtView.setLayoutParams(timerTxtViewLayout);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               timerTxtView.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               timerTxtView.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

           timerTxtView.setTextSize(15);
           timerTxtView.setText("Almost Done");
           timerTxtView.setTextColor(activity.getResources().getColor(R.color.color28));
           Animation anim = new AlphaAnimation(0.0f, 1.0f);
          if(finalDaysRemaining==0) {
              anim.setDuration(500);
          }
          else{
              anim.setDuration(5000);
          }
           anim.setStartOffset(20);
           anim.setRepeatMode(Animation.REVERSE);
           anim.setRepeatCount(Animation.INFINITE);
           timerTxtView.startAnimation(anim);
           setTaskDeadlineTimer(timerTxtView);

           LinearLayout innerProjectTextVWContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerProjectTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerProjectTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
           innerProjectTextVWContainerLLT.setMargins(40,20,10,0);
           innerProjectTextVWContainer.setLayoutParams(innerProjectTextVWContainerLLT);

           LinearLayout.LayoutParams projectNameTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           TextView projectNameTxtViewA = new TextView(activity);
           projectNameTxtViewA.setLayoutParams(projectNameTxtViewLayoutA);

           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               projectNameTxtViewA.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               projectNameTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

           projectNameTxtViewA.setTextSize(15);
           projectNameTxtViewA.setText("Project Name:");
           projectNameTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));


           LinearLayout.LayoutParams projectNameTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           projectNameTxtViewLayoutB.setMargins(30,0,0,0);
           TextView projectNameTxtViewB = new TextView(activity);
           projectNameTxtViewB.setLayoutParams(projectNameTxtViewLayoutB);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               projectNameTxtViewB.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               projectNameTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

           projectNameTxtViewB.setTextSize(15);
          projectNameTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(0));
           projectNameTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


           innerProjectTextVWContainer.addView(projectNameTxtViewA);
           innerProjectTextVWContainer.addView(projectNameTxtViewB);

           LinearLayout innerTaskNameTextVWContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerTaskNameTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerTaskNameTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
           innerTaskNameTextVWContainerLLT.setMargins(40,40,10,0);
           innerTaskNameTextVWContainer.setLayoutParams(innerTaskNameTextVWContainerLLT);


           LinearLayout.LayoutParams taskNameTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           TextView taskNameTxtViewA = new TextView(activity);
           taskNameTxtViewA.setLayoutParams(taskNameTxtViewLayoutA);
           //taskNameTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
          taskNameTxtViewA.setTextSize(15);
           taskNameTxtViewA.setText("Task Name:");
           taskNameTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));


           LinearLayout.LayoutParams taskNameTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           taskNameTxtViewLayoutB.setMargins(30,0,0,0);
           TextView taskNameTxtViewB = new TextView(activity);
           taskNameTxtViewB.setLayoutParams(taskNameTxtViewLayoutB);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskNameTxtViewB.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskNameTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

          taskNameTxtViewB.setTextSize(15);
         taskNameTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(1));
           taskNameTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


           innerTaskNameTextVWContainer.addView(taskNameTxtViewA);
           innerTaskNameTextVWContainer.addView(taskNameTxtViewB);


           LinearLayout innerTaskStrtDateTextVWContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerTaskStrtDateTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerTaskStrtDateTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
           innerTaskStrtDateTextVWContainerLLT.setMargins(40,40,10,0);
           innerTaskStrtDateTextVWContainer.setLayoutParams(innerTaskStrtDateTextVWContainerLLT);

           LinearLayout.LayoutParams taskStartDateTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           TextView taskStartDateTxtViewA = new TextView(activity);
           taskStartDateTxtViewA.setLayoutParams(taskStartDateTxtViewLayoutA);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskStartDateTxtViewA.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskStartDateTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

          taskStartDateTxtViewA.setTextSize(15);
           taskStartDateTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
           taskStartDateTxtViewA.setText("Task Start Date:");

           LinearLayout.LayoutParams taskStartDateViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           taskStartDateViewLayoutB.setMargins(30,0,0,0);
           TextView taskStartDateTxtViewB = new TextView(activity);
           taskStartDateTxtViewB.setLayoutParams(taskStartDateViewLayoutB);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskStartDateTxtViewB.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskStartDateTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

          taskStartDateTxtViewB.setTextSize(15);
         taskStartDateTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(2));
           taskStartDateTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


           innerTaskStrtDateTextVWContainer.addView(taskStartDateTxtViewA);
           innerTaskStrtDateTextVWContainer.addView(taskStartDateTxtViewB);


           LinearLayout innerTaskEndDateTextVWContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerTaskEndDateTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerTaskEndDateTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
           innerTaskEndDateTextVWContainerLLT.setMargins(40,40,10,0);
           innerTaskEndDateTextVWContainer.setLayoutParams(innerTaskEndDateTextVWContainerLLT);


           LinearLayout.LayoutParams taskEndDateTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           TextView taskEndDateTxtViewA = new TextView(activity);
           taskEndDateTxtViewA.setLayoutParams(taskEndDateTxtViewLayoutA);


       if(myAPI<26)
       {
           fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
           taskEndDateTxtViewA.setTypeface(fontAwesomeIcon);
       }
       else if(myAPI>=26)
       {
           taskEndDateTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
       }

           taskEndDateTxtViewA.setTextSize(15);
           taskEndDateTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
           taskEndDateTxtViewA.setText("Task End Date:");

           LinearLayout.LayoutParams taskEndDateTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           taskEndDateTxtViewLayoutB.setMargins(30,0,0,0);
           TextView taskEndDateTxtViewB = new TextView(activity);
           taskEndDateTxtViewB.setLayoutParams(taskStartDateViewLayoutB);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskEndDateTxtViewB.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskEndDateTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
           }
          taskEndDateTxtViewB.setTextSize(15);
        taskEndDateTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(3));
           taskEndDateTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


           innerTaskEndDateTextVWContainer.addView(taskEndDateTxtViewA);
           innerTaskEndDateTextVWContainer.addView(taskEndDateTxtViewB);


           LinearLayout innerTaskStatusTextVWContainer = new LinearLayout(activity);
           LinearLayout.LayoutParams innerTaskStatusTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           innerTaskStatusTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
           innerTaskStatusTextVWContainerLLT.setMargins(40,40,10,40);
           innerTaskStatusTextVWContainer.setLayoutParams(innerTaskStatusTextVWContainerLLT);


           LinearLayout.LayoutParams taskStatusTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           TextView taskStatusTxtViewA = new TextView(activity);
           taskStatusTxtViewA.setLayoutParams(taskEndDateTxtViewLayoutA);

           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskStatusTxtViewA.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskStatusTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

          taskStatusTxtViewA.setTextSize(15);
           taskStatusTxtViewA.setText("Task Status:");
           taskStatusTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
            int ststusProgressForProgressBar =  userAllTaskJSONArray.getJSONArray(i).getInt(4);
           double statusProgress =userAllTaskJSONArray.getJSONArray(i).getDouble(4);
            ProgressBar progressBar = new ProgressBar(activity, null,
                   android.R.attr.progressBarStyleHorizontal);

           LinearLayout.LayoutParams progressBarLLT = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
           progressBar.setLayoutParams(progressBarLLT);
           progressBarLLT.setMargins(30,15,0,0);
           progressBar.setMax(100);
           progressBar.setScaleY(3);

           progressBar.setMin(0);
           progressBar.setProgress(ststusProgressForProgressBar);

           LinearLayout.LayoutParams taskStatusTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           taskStatusTxtViewLayoutB.setMargins(30,0,0,0);
           TextView taskStatusTxtViewB = new TextView(activity);
           taskStatusTxtViewB.setLayoutParams(taskStatusTxtViewLayoutB);
           if(myAPI<26)
           {
               fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
               taskStatusTxtViewB.setTypeface(fontAwesomeIcon);
           }
           else if(myAPI>=26)
           {
               taskStatusTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
           }

           taskStatusTxtViewB.setTextSize(15);
           DecimalFormat f = new DecimalFormat("##.00");
           taskStatusTxtViewB.setText(f.format(statusProgress)+"% done");
           taskStatusTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));

           innerTaskStatusTextVWContainer.addView(taskStatusTxtViewA);
           innerTaskStatusTextVWContainer.addView(progressBar);
           innerTaskStatusTextVWContainer.addView(taskStatusTxtViewB);


           innerContainer.addView(timerTxtView);
           innerContainer.addView(innerProjectTextVWContainer);
           innerContainer.addView(innerTaskNameTextVWContainer);
           innerContainer.addView(innerTaskStrtDateTextVWContainer);
           innerContainer.addView(innerTaskEndDateTextVWContainer);
           innerContainer.addView(innerTaskStatusTextVWContainer);

           containerCardView.addView(innerContainer);
           alltaskDueLinearlayout.addView(containerCardView);
       }
       } catch (JSONException e) {
         e.printStackTrace();
      }

   }


    @TargetApi(26)
    public void allprojecttasksLargeLayout()
    {
        alltaskDueLinearlayout.removeAllViews();
        CardView containerCardView=null;

        try {
            for (int i=0; i<userAllTaskJSONArray.length();i++) {

                containerCardView = new CardView(activity);
                LinearLayout.LayoutParams containerCardViewLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                containerCardViewLLT.setMargins(0,40,0,80);
                containerCardView.setElevation(20);
                containerCardView.setLayoutParams(containerCardViewLLT);


                LinearLayout innerContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerContainer.setOrientation(LinearLayout.VERTICAL);
                innerContainer.setBackground(this.getResources().getDrawable(R.drawable.calendarborder, null));
                innerContainer.setLayoutParams(innerContainerLLT);

                projectEndDate = userAllTaskJSONArray.getJSONArray(i).getString(3);
                LinearLayout.LayoutParams timerTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                timerTxtViewLayout.setMargins(20,20,0,0);
                TextView timerTxtView = new TextView(activity);
                timerTxtView.setLayoutParams(timerTxtViewLayout);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    timerTxtView.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    timerTxtView.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                timerTxtView.setTextSize(20);
                timerTxtView.setText("Almost Done");
                timerTxtView.setTextColor(activity.getResources().getColor(R.color.color28));
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                if(finalDaysRemaining==0) {
                    anim.setDuration(500);
                }
                else{
                    anim.setDuration(5000);
                }
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                timerTxtView.startAnimation(anim);
                setTaskDeadlineTimer(timerTxtView);

                LinearLayout innerProjectTextVWContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerProjectTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerProjectTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
                innerProjectTextVWContainerLLT.setMargins(40,20,10,0);
                innerProjectTextVWContainer.setLayoutParams(innerProjectTextVWContainerLLT);

                LinearLayout.LayoutParams projectNameTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView projectNameTxtViewA = new TextView(activity);
                projectNameTxtViewA.setLayoutParams(projectNameTxtViewLayoutA);

                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    projectNameTxtViewA.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    projectNameTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                projectNameTxtViewA.setTextSize(20);
                projectNameTxtViewA.setText("Project Name:");
                projectNameTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));


                LinearLayout.LayoutParams projectNameTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                projectNameTxtViewLayoutB.setMargins(30,0,0,0);
                TextView projectNameTxtViewB = new TextView(activity);
                projectNameTxtViewB.setLayoutParams(projectNameTxtViewLayoutB);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    projectNameTxtViewB.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    projectNameTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                projectNameTxtViewB.setTextSize(20);
                projectNameTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(0));
                projectNameTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


                innerProjectTextVWContainer.addView(projectNameTxtViewA);
                innerProjectTextVWContainer.addView(projectNameTxtViewB);

                LinearLayout innerTaskNameTextVWContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerTaskNameTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerTaskNameTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
                innerTaskNameTextVWContainerLLT.setMargins(40,40,10,0);
                innerTaskNameTextVWContainer.setLayoutParams(innerTaskNameTextVWContainerLLT);


                LinearLayout.LayoutParams taskNameTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskNameTxtViewA = new TextView(activity);
                taskNameTxtViewA.setLayoutParams(taskNameTxtViewLayoutA);
                //taskNameTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
                taskNameTxtViewA.setTextSize(20);
                taskNameTxtViewA.setText("Task Name:");
                taskNameTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));


                LinearLayout.LayoutParams taskNameTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                taskNameTxtViewLayoutB.setMargins(30,0,0,0);
                TextView taskNameTxtViewB = new TextView(activity);
                taskNameTxtViewB.setLayoutParams(taskNameTxtViewLayoutB);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskNameTxtViewB.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskNameTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskNameTxtViewB.setTextSize(20);
                taskNameTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(1));
                taskNameTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


                innerTaskNameTextVWContainer.addView(taskNameTxtViewA);
                innerTaskNameTextVWContainer.addView(taskNameTxtViewB);


                LinearLayout innerTaskStrtDateTextVWContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerTaskStrtDateTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerTaskStrtDateTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
                innerTaskStrtDateTextVWContainerLLT.setMargins(40,40,10,0);
                innerTaskStrtDateTextVWContainer.setLayoutParams(innerTaskStrtDateTextVWContainerLLT);

                LinearLayout.LayoutParams taskStartDateTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskStartDateTxtViewA = new TextView(activity);
                taskStartDateTxtViewA.setLayoutParams(taskStartDateTxtViewLayoutA);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskStartDateTxtViewA.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskStartDateTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskStartDateTxtViewA.setTextSize(20);
                taskStartDateTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
                taskStartDateTxtViewA.setText("Task Start Date:");

                LinearLayout.LayoutParams taskStartDateViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                taskStartDateViewLayoutB.setMargins(30,0,0,0);
                TextView taskStartDateTxtViewB = new TextView(activity);
                taskStartDateTxtViewB.setLayoutParams(taskStartDateViewLayoutB);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskStartDateTxtViewB.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskStartDateTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskStartDateTxtViewB.setTextSize(20);
                taskStartDateTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(2));
                taskStartDateTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


                innerTaskStrtDateTextVWContainer.addView(taskStartDateTxtViewA);
                innerTaskStrtDateTextVWContainer.addView(taskStartDateTxtViewB);


                LinearLayout innerTaskEndDateTextVWContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerTaskEndDateTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerTaskEndDateTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
                innerTaskEndDateTextVWContainerLLT.setMargins(40,40,10,0);
                innerTaskEndDateTextVWContainer.setLayoutParams(innerTaskEndDateTextVWContainerLLT);


                LinearLayout.LayoutParams taskEndDateTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskEndDateTxtViewA = new TextView(activity);
                taskEndDateTxtViewA.setLayoutParams(taskEndDateTxtViewLayoutA);


                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskEndDateTxtViewA.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskEndDateTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskEndDateTxtViewA.setTextSize(20);
                taskEndDateTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
                taskEndDateTxtViewA.setText("Task End Date:");

                LinearLayout.LayoutParams taskEndDateTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                taskEndDateTxtViewLayoutB.setMargins(30,0,0,0);
                TextView taskEndDateTxtViewB = new TextView(activity);
                taskEndDateTxtViewB.setLayoutParams(taskStartDateViewLayoutB);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskEndDateTxtViewB.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskEndDateTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
                }
                taskEndDateTxtViewB.setTextSize(20);
                taskEndDateTxtViewB.setText(userAllTaskJSONArray.getJSONArray(i).getString(3));
                taskEndDateTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));


                innerTaskEndDateTextVWContainer.addView(taskEndDateTxtViewA);
                innerTaskEndDateTextVWContainer.addView(taskEndDateTxtViewB);


                LinearLayout innerTaskStatusTextVWContainer = new LinearLayout(activity);
                LinearLayout.LayoutParams innerTaskStatusTextVWContainerLLT = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                innerTaskStatusTextVWContainer.setOrientation(LinearLayout.HORIZONTAL);
                innerTaskStatusTextVWContainerLLT.setMargins(40,40,10,40);
                innerTaskStatusTextVWContainer.setLayoutParams(innerTaskStatusTextVWContainerLLT);


                LinearLayout.LayoutParams taskStatusTxtViewLayoutA = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView taskStatusTxtViewA = new TextView(activity);
                taskStatusTxtViewA.setLayoutParams(taskEndDateTxtViewLayoutA);

                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskStatusTxtViewA.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskStatusTxtViewA.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskStatusTxtViewA.setTextSize(20);
                taskStatusTxtViewA.setText("Task Status:");
                taskStatusTxtViewA.setTextColor(activity.getResources().getColor(R.color.color23));
                int ststusProgressForProgressBar =  userAllTaskJSONArray.getJSONArray(i).getInt(4);
                double statusProgress =userAllTaskJSONArray.getJSONArray(i).getDouble(4);
                ProgressBar progressBar = new ProgressBar(activity, null,
                        android.R.attr.progressBarStyleHorizontal);

                LinearLayout.LayoutParams progressBarLLT = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
                progressBar.setLayoutParams(progressBarLLT);
                progressBarLLT.setMargins(30,15,0,0);
                progressBar.setMax(100);
                progressBar.setScaleY(3);

                progressBar.setMin(0);
                progressBar.setProgress(ststusProgressForProgressBar);

                LinearLayout.LayoutParams taskStatusTxtViewLayoutB = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                taskStatusTxtViewLayoutB.setMargins(30,0,0,0);
                TextView taskStatusTxtViewB = new TextView(activity);
                taskStatusTxtViewB.setLayoutParams(taskStatusTxtViewLayoutB);
                if(myAPI<26)
                {
                    fontAwesomeIcon = Typeface.createFromAsset(getActivity().getAssets(), "font/Roboto-Regular.ttf");
                    taskStatusTxtViewB.setTypeface(fontAwesomeIcon);
                }
                else if(myAPI>=26)
                {
                    taskStatusTxtViewB.setTypeface(activity.getResources().getFont(R.font.roboto));
                }

                taskStatusTxtViewB.setTextSize(20);
                DecimalFormat f = new DecimalFormat("##.00");
                taskStatusTxtViewB.setText(f.format(statusProgress)+"% done");
                taskStatusTxtViewB.setTextColor(activity.getResources().getColor(R.color.color23));

                innerTaskStatusTextVWContainer.addView(taskStatusTxtViewA);
                innerTaskStatusTextVWContainer.addView(progressBar);
                innerTaskStatusTextVWContainer.addView(taskStatusTxtViewB);


                innerContainer.addView(timerTxtView);
                innerContainer.addView(innerProjectTextVWContainer);
                innerContainer.addView(innerTaskNameTextVWContainer);
                innerContainer.addView(innerTaskStrtDateTextVWContainer);
                innerContainer.addView(innerTaskEndDateTextVWContainer);
                innerContainer.addView(innerTaskStatusTextVWContainer);

                containerCardView.addView(innerContainer);
                alltaskDueLinearlayout.addView(containerCardView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    public void setTaskDeadlineTimer(final TextView timerTxtView)
   {
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
       Date fromDate = new Date();

       String fromDateInString= sdf.format(fromDate);
       String toDateInString = projectEndDate+" "+"24:00:00";
       Date toDate = null;
       try {
           toDate = sdf.parse(toDateInString);
           fromDate = sdf.parse(fromDateInString);
       } catch (ParseException e) {
           e.printStackTrace();
       }
       dateToMillis = Math.abs(toDate.getTime()- fromDate.getTime());

       countDownTimer = new CountDownTimer(dateToMillis,1000)
       {
           public void onTick(long millisUntilFinished)
           {
               String remainingTime = calculateTimeRemaining(millisUntilFinished);
               timerTxtView.setText("Deadline: "+ remainingTime);

           }

           public void onFinish()
           {
        //       timerTxtView.setText("Assumed Completed!");
           }
       }.start();
   }

    public String calculateTimeRemaining(long millisUntilFinished) {
        int millis = 0;
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        if (millisUntilFinished > 1000) {
            seconds = (int) millisUntilFinished / 1000;
            millis = (int) millisUntilFinished % 1000;
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds = seconds % 60;

        }

        if (minutes > 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        if (hours > 24) {
            days = hours / 24;
            hours = hours % 24;
            finalDaysRemaining=days;
        }

        return days+" day(s) "+ hours+" hour(s) " + minutes + " min(s) " + seconds + " secs";
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
        LinearLayout.LayoutParams llpChatPageInnerRLT = new LinearLayout.LayoutParams(850, 130);

        innerContainer.setLayoutParams(llpChatPageInnerRLT);


        LinearLayout.LayoutParams llpChatProjectNameTxtViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView projectNameTextView = new TextView(activity);
        llpChatProjectNameTxtViewLayout.setMargins(30,30,0,0);
        projectNameTextView.setText(textMessage);
        projectNameTextView.setLayoutParams(llpChatProjectNameTxtViewLayout);
        projectNameTextView.setTextSize(14);
        projectNameTextView.setTextColor(activity.getResources().getColor(R.color.color12));
//        projectNameTextView.setTypeface(activity.getResources().getFont(R.font.montserrat));

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

        popUp.showAtLocation(customView, Gravity.NO_GRAVITY, 30, 1300);




    }

    private void getUserAllTasksList(String userName, String selectedDate)
    {

        try {
            String result  = getUserAllTasksDetails(userName, selectedDate);
            if(popUp!=null && popUp.isShowing())
            {
                popUp.dismiss();
            }
            userAllTaskJSONArray = new JSONArray(result);
            if(userAllTaskJSONArray.length()>0) {
            userAllTaskJSONArray = new JSONArray(result);



                int screenSize = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

                switch(screenSize) {
                    case Configuration.SCREENLAYOUT_SIZE_LARGE:
                        allprojecttasksLargeLayout();
                        break;
                    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                        allprojecttasksNormalLayout();
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

            }
            else
            {


                popUpView("No Assigned Task for this date yet");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getUserAllTasksDetails(String userName,String selectedDate)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("dateSelect", selectedDate);
            postDataParams.put("userName", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/gaut/",postDataParams, activity,"application/json", "application/json");
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
