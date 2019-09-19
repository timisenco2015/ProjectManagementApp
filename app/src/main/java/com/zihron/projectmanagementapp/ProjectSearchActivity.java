package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class ProjectSearchActivity extends AppCompatActivity {
    private CheckBox projectPeriodCheckBox;
    private CheckBox projectRangeCheckBox;
    private CheckBox keyWordSearchChkBox;
    private SearchView projectNameSearchView;

    private RadioGroup projectEndStartDateRangeRadioGroup;
    private RadioGroup projectDateInDaysRangeGroup;
    private RadioGroup projectEndStartDatePeriodRadioGroup;
    private CalendarView calendarToView;
    private EditText endDateEditText;
    private EditText startDateEditText;
    private CalendarView calendarFromView;
    private Button searchKeyWordButton ;
    private Button searchDateRangeButton ;
    private Button searchProjectDatePeriodButton;
    private LinearLayout keyWordSearchLayout;
    private LinearLayout dateRangeLayout ;
    private LinearLayout datePeriodLayout;
    private JSONArray searchResultJSONArray;
    private SearchProjectByDatePeriod  searchProjectByDatePeriod;
    private SearchProjectByName searchProjectByName;
    private SearchProjectByDateRange  searchProjectByDateRange;
private Activity activity;
    private SharedPreferences sharedPreferences;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_search);
        //Intent PTAIntent = new Intent(getApplication(), ProjectSearchActivity.class);
        // startActivityForResult(PTAIntent,1);
        //ProjectActivity.this.startActivity(PTAIntent)
       
        activity = this;
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
      searchKeyWordButton = (Button)findViewById(R.id.searchKeyWDBtnId);
      searchDateRangeButton = (Button)findViewById(R.id.searchDateRangeBtnId);
        searchProjectDatePeriodButton = (Button) findViewById(R.id.searchDatePeriodBtnId);
        projectPeriodCheckBox = (CheckBox)findViewById(R.id.projectPeriodChkBtnId);
        projectRangeCheckBox = (CheckBox)findViewById(R.id.projectRangeChkBtnId);
        keyWordSearchChkBox = (CheckBox)findViewById(R.id.keyWordSearchChkBtnId);



        projectNameSearchView = (SearchView)findViewById(R.id.projectNameSearchViewId);
        projectEndStartDatePeriodRadioGroup = (RadioGroup) findViewById(R.id.projectEndStartDatePeriodRadioGroupId);
        projectEndStartDateRangeRadioGroup = (RadioGroup)findViewById(R.id.projectEndStartDateRangeRadioGroupId);
        projectDateInDaysRangeGroup = (RadioGroup)findViewById(R.id.projectDateInDaysRangeGroupId);
        endDateEditText = (EditText)findViewById(R.id.endDateEditTxtId);
        startDateEditText = (EditText)findViewById(R.id.startDateEditTxtId);
        calendarToView = (CalendarView)findViewById(R.id.calendarToViewId);
        calendarFromView = (CalendarView)findViewById(R.id.calendarFromViewId);
       keyWordSearchLayout = (LinearLayout)findViewById(R.id.keyWordSearchLayoutId);
       dateRangeLayout =  (LinearLayout)findViewById(R.id.dateRangeLayoutId);
       datePeriodLayout =  (LinearLayout)findViewById(R.id.datePeriodLayoutId);

        keyWordSearchChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked)
                {
                    projectPeriodCheckBox.setChecked(false);
                    projectRangeCheckBox.setChecked(false);
                    searchKeyWordButton.setEnabled(true);
                    searchDateRangeButton.setEnabled(false);
                    searchProjectDatePeriodButton.setEnabled(false);
                    setCalendaToViewDate();
                    setCalendarFromViewDate();

                    searchProjectByName = new SearchProjectByName(activity, projectNameSearchView, userName,searchKeyWordButton);

                    searchKeyWordButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchResultJSONArray = searchProjectByName.getSearchResultJSONArray();

                            Intent returnIntent = getIntent();
                            if(searchResultJSONArray!=null) {

                                returnIntent.putExtra("result", searchResultJSONArray.toString());
                            }
                            else
                            {
                                returnIntent.putExtra("result", "NoValue");
                            }
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    });



                }
                else
                {
                    searchKeyWordButton.setEnabled(false);

                }
            }
        });
        projectPeriodCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    projectRangeCheckBox.setChecked(false);
                    keyWordSearchChkBox.setChecked(false);
                    searchKeyWordButton.setEnabled(false);
                    searchDateRangeButton.setEnabled(false);
                    searchProjectDatePeriodButton.setEnabled(true);
                    setCalendaToViewDate();
                    setCalendarFromViewDate();
                     searchProjectByDatePeriod = new SearchProjectByDatePeriod( activity, endDateEditText,startDateEditText, calendarToView ,calendarFromView, projectEndStartDatePeriodRadioGroup,userName, searchProjectDatePeriodButton);

                    searchProjectDatePeriodButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchResultJSONArray = searchProjectByDatePeriod.getSearchResultJSONArray();

                            Intent returnIntent = getIntent();
                            if(searchResultJSONArray!=null) {

                                returnIntent.putExtra("result", searchResultJSONArray.toString());
                            }
                            else
                            {

                                returnIntent.putExtra("result", "NoValue");
                            }
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    });





                }
                else
                {
                    searchProjectDatePeriodButton.setEnabled(false);

                }
            }
        });



        projectRangeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                {
                    projectPeriodCheckBox.setChecked(false);
                    keyWordSearchChkBox.setChecked(false);
                    searchKeyWordButton.setEnabled(false);
                    searchProjectDatePeriodButton.setEnabled(false);
                    searchDateRangeButton.setEnabled(true);


                    searchProjectByDateRange = new SearchProjectByDateRange( activity, userName,  projectDateInDaysRangeGroup,  projectEndStartDateRangeRadioGroup,searchDateRangeButton);

                    searchDateRangeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchResultJSONArray = searchProjectByDateRange.getSearchResultJSONArray();
                            Intent returnIntent = new Intent();
                            if(searchResultJSONArray!=null) {
                                returnIntent.putExtra("result", searchResultJSONArray.toString());
                            }
                            else
                            {
                                returnIntent.putExtra("result", "No Value");
                            }
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                        }
                    });






                }
                else
                {
                    searchDateRangeButton.setEnabled(false);
                    // projectPeriodCheckBox.setChecked(true);

                }
            }
        });








    }
    @Override
    public void onBackPressed() {

    }

    public void setCalendarFromViewDate()  {
        calendarFromView.setDate(Calendar.getInstance().getTimeInMillis());
        String dateToString = "" + 2018 + "/" + 01 + "/" +01;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date startDate =null;
        try {
            startDate = format.parse(dateToString );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendarFromView.setMinDate(startDate.getTime());
    }

    public void setCalendaToViewDate()
    {
        calendarToView.setDate(Calendar.getInstance().getTimeInMillis());
        calendarToView.setMaxDate(Calendar.getInstance().getTimeInMillis());
    }


    public void backToProjectPage(View view)
    {
        Intent pSAIntent = new Intent(ProjectSearchActivity.this, ProjectActivity.class);
        startActivity(pSAIntent);
    }

}
