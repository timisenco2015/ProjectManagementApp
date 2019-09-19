package com.zihron.projectmanagementapp;

import android.graphics.Typeface;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

public class SearchProjectByDateRange {

   private final RadioGroup projectDateInDaysRangeGroup;
   private RadioGroup projectEndStartDateRangeRadioGroup;
    private HttpRequestClass httpRequestClass;
   private Activity activity;
   private JSONArray searchResultJSONArray;
   private String userName;
    private String startDateToString;
    private String endDateToString;
    private String searchKeywords;
    private String rangeInDays;
   private  DateFormat dateFormat;
   private Button searchDateRangeButton;
   private int[] projectDateInDaysRangeGroupIds;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ProgressBarClass progressBarClass;
    public SearchProjectByDateRange (final Activity activity, String userName,  RadioGroup projectDateInDaysRangeGroup, RadioGroup projectEndStartDateRangeRadioGroup,final Button searchDateRangeButton)
    {
        progressBarClass = new ProgressBarClass(activity);
        this.projectDateInDaysRangeGroup=projectDateInDaysRangeGroup;
        this.projectEndStartDateRangeRadioGroup=projectEndStartDateRangeRadioGroup;
        this.activity = activity;
        this.userName = userName;
        this.searchDateRangeButton = searchDateRangeButton;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        searchDateRangeButton.setEnabled(false);
        searchKeywords = "Created";
        projectDateInDaysRangeGroupIds = new int[]{R.id.rangeDateButton1,R.id.rangeDateButton2,R.id.rangeDateButton3,R.id.rangeDateButton4,R.id.rangeDateButton5,R.id.rangeDateButton6,R.id.rangeDateButton7,R.id.rangeDateButton8,R.id.rangeDateButton9};
        projectDateInDaysRangeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

               RadioButton radioButton = (RadioButton) activity.findViewById(checkedId);
                rangeInDays = radioButton.getText().toString();
                radioButton.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                radioButton.setTextColor(activity.getResources().getColor(R.color.color28));
                startDateToString = getDateFrom(rangeInDays);
                int indexOf = group.indexOfChild(radioButton);
                searchDateRangeButton.setEnabled(true);
                changeSelectedRadiButtnInRadioGrpBckGround(indexOf);


            }

        });
        projectEndStartDateRangeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
              // dateRangeErrorTextView1.setVisibility(View.GONE);
                searchDateRangeButton.setEnabled(true);
                if(checkedId==R.id.createdDateRangeButton)
                {
                    searchKeywords = "Created";

                }
                else if(checkedId==R.id.endedDateRangeButton)
                {
                    searchKeywords = "Ended";
                }
                else  if(checkedId==R.id.startedDateRangeButton)
                {
                    searchKeywords = "Started";
                }
            }
        });

    }


    public  int getDateRangeInDays(String rangeInString) {
        int dateRangeInDays =0;
        String tempArray[] = rangeInString.split(" ");
        if (tempArray[1].compareTo("Week") == 0 || tempArray[1].compareTo("Weeks") == 0) {
            dateRangeInDays = Integer.parseInt(tempArray[0]) * 7;
        } else if (tempArray[1].compareTo("Month") == 0 || tempArray[1].compareTo("Months") == 0) {
            dateRangeInDays = Integer.parseInt(tempArray[0]) * 4 * 7;

        }
        return dateRangeInDays;
    }


    public String getDateFrom(String rangeInString)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, getDateRangeInDays(rangeInString));
        Date dateBefore = cal.getTime();
        String strDate = dateFormat.format(dateBefore);
        return strDate;
    }

    public String getDateTo()
    {
        Date date = Calendar.getInstance().getTime();
        String endDate = dateFormat.format(date);
        return endDate;
    }






    public void changeSelectedRadiButtnInRadioGrpBckGround(int checkId)
    {

        for(int i=0; i<projectDateInDaysRangeGroupIds.length; i++)
        {


            if(i!=checkId)
            {
                RadioButton view = (RadioButton) activity.findViewById(projectDateInDaysRangeGroupIds[i]) ;
                view.setTypeface(Typeface.create("lato", Typeface.NORMAL));
                view.setTextColor(activity.getResources().getColor(R.color.color6));

            }
        }
    }


    public JSONArray getSearchResultJSONArray()
    {

            endDateToString =  getDateTo();
            try {
                String result = searchProjectByDateRange(searchKeywords, startDateToString, endDateToString, userName);
                if(result!=null) {
                    searchResultJSONArray = new JSONArray(result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        return searchResultJSONArray;
    }



    public String searchProjectByDateRange(String searchKeyWords,String searchStartDate, String searchEndDate,String userName ) {
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
