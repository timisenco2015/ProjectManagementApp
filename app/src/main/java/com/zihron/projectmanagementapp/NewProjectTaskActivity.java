package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewProjectTaskActivity extends AppCompatActivity {
    int start_year, start_month, start_day;
    int end_year, end_month, end_day;
    EditText endDateEditText;
    private boolean isProjectTskNameMaxtError;
    TextView startDateTextVW;
    TextView endDateTextVW;
    EditText  projectNameEditTxtIdTVW;
    EditText  projectTskDscpEditTxtIdTVW;
    EditText  projectTskNameEditTxtIdTVW;
    private String projectStartdate;
    private String projectEndDate;
    EditText projectDescripIdtxtVW;
    EditText startDateEditTxtVW;
    EditText endDateEditTxtVW;
    private Activity activity;
    TextView projecTskDrespCharCountIdTVW;
    String projectName;
    private String startDateToString;
    private Button createProjectTaskButton;
    private Button updateProjectTaskButton;
    private ProgressBarClass progressBarClass;
    private SharedPreferences sharedPreferences;
  private String userName;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    Typeface fontAwesomeIcon;
    private HttpRequestClass httpRequestClass;
    static final int DIALOG_ID1=0;
    static final int DIALOG_ID2=1;
    private  boolean asyncError = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_task);
        final Calendar calendar = Calendar.getInstance();
        start_year = calendar.get(Calendar.YEAR);
        start_month = calendar.get(Calendar.MONTH);
        start_day = calendar.get(Calendar.DAY_OF_MONTH);
        isProjectTskNameMaxtError = false;
activity = this;
        fontAwesomeIcon = Typeface.createFromAsset(getAssets(),"font/fontawesome-webfont.ttf");

        startDateTextVW = (TextView) findViewById(R.id.startDateTxtView);
        endDateTextVW= (TextView) findViewById(R.id.endDateTxtView);
        projectNameEditTxtIdTVW = (EditText) findViewById(R.id.projectNameEditTxtId);
        startDateEditTxtVW = (EditText)findViewById(R.id.startDateEditTxt);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE );
        endDateEditTxtVW = (EditText) findViewById(R.id.endDateEditTxt);
        endDateEditTxtVW.setEnabled(false);
        projectTskNameEditTxtIdTVW = (EditText) findViewById(R.id.projectTskNameEdtTxtId);
        projectTskDscpEditTxtIdTVW = (EditText) findViewById(R.id.projectTskDescripId);
        projecTskDrespCharCountIdTVW =(TextView)findViewById(R.id.projecTskDrespCharCountId);

        projectStartdate = sharedPreferences.getString("projectStartDate", null);
        projectEndDate = sharedPreferences.getString("projectEndDate", null);
        projectName = sharedPreferences.getString("projectName", null);

        createProjectTaskButton = (Button) findViewById(R.id.createProjectTaskId);
        updateProjectTaskButton = (Button) findViewById(R.id.updateProjectTaskId);
        TextView closeNewProjectTaskTextVW = (TextView)findViewById(R.id.closeNewProjectTask);


        userName = sharedPreferences.getString("userName", null);
        projectNameEditTxtIdTVW.setText(projectName);
        updateProjectTaskButton.setVisibility(View.GONE);
        createProjectTaskButton.setVisibility(View.VISIBLE);


        projectTskDscpEditTxtIdTVW.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projecTskDrespCharCountIdTVW.setText(""+s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TextWatcherTest", "afterTextChanged:\t" +s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        });

        projectTskNameEditTxtIdTVW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if((projectTskNameEditTxtIdTVW.getText().toString()).length()>=40)
                    {
                        projectTskNameEditTxtIdTVW.setError("Maximum length allowd is 39");
                        isProjectTskNameMaxtError=true;
                    }
                    else  if((projectTskNameEditTxtIdTVW.getText().toString()).length()<40)
                    {
                        isProjectTskNameMaxtError=false;
                    }
                }
            }
        });


        closeNewProjectTaskTextVW.setTypeface(fontAwesomeIcon);
        closeNewProjectTaskTextVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NPAIntent = new Intent(NewProjectTaskActivity.this, ViewProjectActivity.class);
                NewProjectTaskActivity.this.startActivity(NPAIntent);
            }
        });

        startDateTextVW.setTypeface(fontAwesomeIcon);
        endDateTextVW.setTypeface(fontAwesomeIcon);

    }

    @Override
    public void onBackPressed() {

    }


    public void createProjectTask(View view) {
        if(!checkFiledValuesErrors(projectTskNameEditTxtIdTVW, projectTskDscpEditTxtIdTVW, startDateEditTxtVW) && !isProjectTskNameMaxtError) {


                String result = insetNewProjectTask(projectName,projectTskNameEditTxtIdTVW.getText().toString().trim(), projectTskDscpEditTxtIdTVW.getText().toString().trim(), startDateEditTxtVW.getText().toString().trim(), endDateEditTxtVW.getText().toString().trim());
                if(result!=null && !asyncError)
                {
                    Intent nPTAIntent = new Intent(NewProjectTaskActivity.this, ViewProjectActivity.class);
                    startActivity(nPTAIntent);
                }
                else
                {
                    asyncError=false;
                    Toast.makeText(activity, "Task not created",Toast.LENGTH_LONG).show();
                }

        }

}


    public String insetNewProjectTask(String projectName,String projectTaskName,String projectTaskDescription, String projectTaskStartdate,String projectTaskEnddate )
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            String projectTaskCreatedate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
            postDataParams.put("projectName", projectName);
            postDataParams.put("projectTaskName", projectTaskName);
            postDataParams.put("projectTaskDescription", projectTaskDescription);
            postDataParams.put("projectTaskStartdate", projectTaskStartdate);
            postDataParams.put("projectTaskCreatedDate", projectTaskCreatedate);
            postDataParams.put("projectTaskEnddate", projectTaskEnddate);
            postDataParams.put("projectTaskDonePercentage", 0);
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inpt/",postDataParams, activity,"text/plain", "application/json");
            result = httpRequestClass.getResult();
            asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
            if(result!=null) {
                if (result.equalsIgnoreCase("exception")) {
                    asyncError=true;
                    asyncErrorDialogDisplay.handleException(activity);
                } else if (result.equalsIgnoreCase("No network")) {
                    asyncError=true;
                    Toast.makeText(activity, "Your phone is not connected to internet", Toast.LENGTH_LONG).show();
                } else if (android.text.TextUtils.isDigitsOnly(result)) {
                    asyncError=true;
                    asyncErrorDialogDisplay.errorCodeCheck(Integer.parseInt(result));
                }
            }
            else if(result==null)
            {
                asyncError=true;
                Toast.makeText(activity, "Error with connection, Please re-launch this app ", Toast.LENGTH_LONG).show();
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void startDatePicker(View view)
    {

        createdDialog(0).show();
        // showDialog(0,null);

    }



    public void endDatePicker(View view)
    {

        createdDialog(1).show();

    }



    protected Dialog createdDialog(int id) {
        if (id == DIALOG_ID1) {

            DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, startDatePickerListener, start_year, start_month, start_day);
            Calendar cal = Calendar.getInstance();
            cal.set(start_year, start_month, start_day);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
            Date endDate = null;
            Date startDate = null;
            try {
                endDate = f.parse(projectEndDate);
                startDate = f.parse(projectStartdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date start = cal.getTime();
            startDatePickerDialog.getDatePicker().setMinDate(startDate.getTime());
            startDatePickerDialog.getDatePicker().setMaxDate(endDate.getTime());

            return startDatePickerDialog;
        }
        else if (id == DIALOG_ID2)
        {
            DatePickerDialog endDatePickerDialog = new DatePickerDialog(this, endDatePickerListener, end_year, end_month, end_day);
            Calendar cal = Calendar.getInstance();
            cal.set(end_year, end_month, end_day);

            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");

            Date endDate = null;
            Date startDate = null;
            try {
                endDate = f.parse(projectEndDate);
                startDate = f.parse(startDateToString );
            } catch (ParseException e) {
                e.printStackTrace();
            }

            endDatePickerDialog.getDatePicker().setMinDate(startDate.getTime());
                endDatePickerDialog.getDatePicker().setMaxDate(endDate.getTime());
            return endDatePickerDialog;
        }



        return null;

    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            end_year = year;
            end_month = monthOfYear;
            end_day = dayOfMonth;

            String dayOfMonthInString="";
            String monthOfYearInString="";
            if(dayOfMonth<10)
            {
                dayOfMonthInString = "0" +dayOfMonth;

            }
            else
            {
                dayOfMonthInString = "" +dayOfMonth;

            }


            if((monthOfYear+1)<10)
            {
                monthOfYearInString = "0" + (monthOfYear+1);

            }
            else
            {
                monthOfYearInString = "" + (monthOfYear+1);

            }
            startDateToString = "" + year + "/" + monthOfYearInString + "/" +dayOfMonthInString;

            startDateEditTxtVW.setText(startDateToString);
            endDateTextVW.setEnabled(true);
        }
    };
    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            end_year = year;
            end_month = monthOfYear+1;
            end_day = dayOfMonth;

            String endDateToString=null;
            String dayOfMonthInString="";
            String monthOfYearInString="";

            if((end_day+1)<10)
            {
                dayOfMonthInString = "0" +dayOfMonth;

            }
            else
            {
                dayOfMonthInString = "" +dayOfMonth;

            }

            if(end_month<10)
            {
                monthOfYearInString = "0" + (monthOfYear+1);

            }
            else
            {
                monthOfYearInString = "" + (monthOfYear+1);

            }
            endDateToString = "" + end_year + "/" + "" + monthOfYearInString+ "/"  +dayOfMonthInString;

                endDateEditTxtVW.setText(endDateToString);



        }
    };








    public boolean checkFiledValuesErrors(EditText projectTaskNameIdEdtxtVW, EditText projectTaskDescripIdtxtVW, EditText startDateEditTxtVW )
    {
        boolean fieldError = false;
        ValidationClass validationClass = new ValidationClass();

        if(validationClass.checkFieldEmpty(startDateEditTxtVW.getText().toString()) )
        {


            startDateEditTxtVW.setError("Please enter value in this fieldhhh");
            fieldError=true;
        }
        if(validationClass.checkFieldEmpty(projectTaskNameIdEdtxtVW.getText().toString()) )
        {


            projectTaskNameIdEdtxtVW.setError("Please enter value in this fieldhhh");
            fieldError=true;
        }

        else
        {
            if(validationClass.checkStartWithInvalidChars(projectTaskNameIdEdtxtVW.getText().toString()) )
            {
                projectTaskNameIdEdtxtVW.setError("filed value starts start with invalid characters: e.g. @, ., ...");
                fieldError=true;
            }
            else if (validationClass.checkInvalidChar(projectTaskNameIdEdtxtVW.getText().toString()) )
            {
                projectTaskNameIdEdtxtVW.setError("filed value contains invalid characters: e.g. @, ., ...");
                fieldError=true;
            }
        }

        if(validationClass.checkFieldEmpty(projectTaskDescripIdtxtVW.getText().toString()))
        {


            projectTaskDescripIdtxtVW.setError("Please enter value in this field");
            fieldError=true;
        }

        else
        {
            if(validationClass.checkStartWithInvalidChars(projectTaskDescripIdtxtVW.getText().toString()) )
            {
                projectTaskDescripIdtxtVW.setError("filed value starts start with invalid characters: e.g. @, ., ...");
                fieldError=true;
            }
            else if (validationClass.checkInvalidChar(projectTaskDescripIdtxtVW.getText().toString()) )
            {
                projectTaskDescripIdtxtVW.setError("filed value contains invalid characters: e.g. @, ., ...");
                fieldError=true;
            }


        }



        return fieldError;
    }

}
