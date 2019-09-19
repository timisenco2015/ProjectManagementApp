package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomCalendarListViewAdapter extends BaseAdapter
{
    private JSONArray valueArray;
    private CountDownTimer countDownTimer;
    private static Context context;
    private static LayoutInflater inflater;
    private Intent intent;
    private long dateToMillis;
    private TextView projectStartDateTextView;
    private TextView projectEndDateTextView;
    private TextView   projectNameTextView;
    private TextView   projectTaskNameTextView;
    public CustomCalendarListViewAdapter(Context context,JSONArray valueArray)
    {

        this.valueArray =valueArray;
        this.context=context;

    }

    @Override
    public int getCount() {

        return valueArray.length();
    }


    @Override
    public JSONArray getItem(int position) {
        JSONArray tempJSONArray =null;
        try {
            tempJSONArray =valueArray.getJSONArray(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return   tempJSONArray;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Typeface fontAwesomeIcon;


        Configuration userPhoneConfig = context.getResources().getConfiguration();

         //   Button leftButtonClick;
        //Button rightButtonClick;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calendarlistviewlayout,parent, false);

        fontAwesomeIcon = Typeface.createFromAsset(context.getAssets(),"font/fontawesome-webfont.ttf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date fromDate = new Date();


        final TextView alarmTextVW = (TextView) convertView.findViewById(R.id.alarmTextVWId);
        final Button showTimerBtn = (Button)convertView.findViewById(R.id.showTimer);
        final TextView alarmIdTextVW = (TextView) convertView.findViewById(R.id.alarmId);
        projectStartDateTextView= (TextView) convertView.findViewById(R.id.projectStartDateTextVWId);
         projectEndDateTextView = (TextView) convertView.findViewById(R.id.projectEndDateTextVWId);
        projectNameTextView = (TextView) convertView.findViewById(R.id.projectNameTextVWId);
        projectTaskNameTextView = (TextView) convertView.findViewById(R.id.projectTaskNameTextVWId);
        JSONArray taskJSONArray = getItem(position);
        alarmIdTextVW.setTypeface(fontAwesomeIcon);
     // long dateToMillis = TimeUnit.DAYS.toMillis(30);

        try {
            projectNameTextView.setText(taskJSONArray.getString(0));
            projectTaskNameTextView.setText(taskJSONArray.getString(1));
            projectStartDateTextView.setText(taskJSONArray.getString(2));
            projectEndDateTextView.setText(taskJSONArray.getString(3));
           // Date fromDate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse((selectedDateString+" "+"24:00:00"));

          //  Date toDate = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse((taskJSONArray.getString(3)+" "+"24:00:00"));



            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String toDateInString = taskJSONArray.getString(3)+" "+"24:00:00";
            Date toDate = sdf.parse(toDateInString);
            dateToMillis = Math.abs(toDate.getTime()- fromDate.getTime());

            countDownTimer = new CountDownTimer(dateToMillis,1000)
            {
                public void onTick(long millisUntilFinished)
                {
                    String remainingTime = calculateTimeRemaining(millisUntilFinished);
                    alarmTextVW.setText("Deadline: "+ remainingTime);

                }

                public void onFinish()
                {
                    alarmTextVW.setText("Assumed Completed!");
                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return convertView;

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
    }

    return days+" day(s) "+ hours+" hour(s) " + minutes + " min(s) " + seconds + " secs";
}
}










