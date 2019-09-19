package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchProjectByDatePeriod {
  private  EditText toEditText;
  private  EditText fromEditText;
   private CalendarView toCalendarView;
  private  CalendarView fromCalendaerView;
  private Button submitButton;
  private String startDateToString;
  private String endDateToString;
  private String searchKeywords;
  private final Activity activity;
  private String userName;
  private String searchValue;
  private Button searchProjectDatePeriodButton;
  private JSONArray searchResultJSONArray;
   private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
   private RadioGroup projectEndStartDatePeriodRadioGroup;
    private ProgressBarClass progressBarClass;

    public SearchProjectByDatePeriod(final Activity activity, EditText toEditText,final EditText fromEditText, CalendarView toCalendarView, CalendarView fromCalendaerView, RadioGroup projectEndStartDatePeriodRadioGroup,String userName,
                                    final Button searchProjectDatePeriodButton)
    {
        progressBarClass = new ProgressBarClass(activity);
        this.toEditText = toEditText;
        this.fromEditText = fromEditText;
        this.toCalendarView = toCalendarView;
        this.fromCalendaerView = fromCalendaerView;
this.projectEndStartDatePeriodRadioGroup=projectEndStartDatePeriodRadioGroup;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        this.activity=activity;
        this.userName = userName;
        this.searchProjectDatePeriodButton= searchProjectDatePeriodButton;
        searchValue=null;
        startDateToString=null;
        endDateToString = null;
        toEditText.setEnabled(false);
        fromEditText.setEnabled(false);
        searchProjectDatePeriodButton.setEnabled(false);
        getSelectedFromDate();
        getSeletedToDate();
        toCalendarView.setAlpha(0);
        searchKeywords = "Created";
        toCalendarView.setEnabled(false);
        projectEndStartDatePeriodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                searchProjectDatePeriodButton.setEnabled(true);
                if(checkedId==R.id.createdDatePeriodButton)
                {
                    searchKeywords = "Created";

                }
                else if(checkedId==R.id.endedDatePeriodButton)
                {
                    searchKeywords = "Ended";
                }
                else  if(checkedId==R.id.StartedDatePeriodButton)
                {
                    searchKeywords = "Started";
                }
            }
        });


    }




public void getSelectedFromDate() {
    fromCalendaerView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            String dayOfMonthInString = "";
            String monthOfYearInString = "";
            toCalendarView.setAlpha(1);
            if (dayOfMonth < 10) {
                dayOfMonthInString = "0" + dayOfMonth;

            } else {
                dayOfMonthInString = "" + dayOfMonth;

            }

            if ((month + 1) < 10) {
                monthOfYearInString = "0" + (month + 1);

            } else {
                monthOfYearInString = "" + (month + 1);

            }

            startDateToString = "" + year + "/" + monthOfYearInString + "/" + dayOfMonthInString;

            fromEditText.setText(startDateToString);
        }
    });
}

public void getSeletedToDate()
{
        toCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
             int   end_year = year;
             int   end_month = month;
              int  end_day = dayOfMonth;

                String dayOfMonthInString="";
                String monthOfYearInString="";
                searchProjectDatePeriodButton.setEnabled(true);
                if(dayOfMonth<10)
                {
                    dayOfMonthInString = "0" +dayOfMonth;

                }
                else
                {
                    dayOfMonthInString = "" +dayOfMonth;

                }

                if((month+1)<10)
                {
                    monthOfYearInString = "0" + (month+1);

                }
                else
                {
                    monthOfYearInString = "" + (month+1);

                }
                endDateToString = "" + year + "/" + monthOfYearInString + "/" +dayOfMonthInString;
                searchProjectDatePeriodButton.setEnabled(true);
                toEditText.setText(endDateToString);
            }
        });

    }

public boolean validateField()
{
    boolean errorCheck = false;


    if (fromEditText.getText().toString()==null)
    {
        fromEditText.setError("You have not selected any date");
        errorCheck = true;
    }

    if(toEditText.getText().toString()==null)
    {
        errorCheck = true;
        toEditText.setError("You have not selected any date");
    }
    return errorCheck;


}
    public JSONArray getSearchResultJSONArray()
    {
        if(!validateField()) {

            try {
                String result = searchProjectByDatePeriod(searchKeywords, startDateToString, endDateToString, userName);
                if(result!=null) {
                    searchResultJSONArray = new JSONArray(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return searchResultJSONArray;
    }





    public String searchProjectByDatePeriod(String searchKeyWords,String searchStartDate, String searchEndDate,String userName ) {
        String result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectSearchValue", searchKeyWords);
            postDataParams.put("startDateToString", searchStartDate);
            postDataParams.put("endDateToString", searchEndDate);
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/guapscdr/", postDataParams, activity, "application/json", "application/json");
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if (result != null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            } else if (result == null) {
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }




}
