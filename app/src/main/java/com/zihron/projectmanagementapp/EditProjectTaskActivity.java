package com.zihron.projectmanagementapp;

        import android.app.Activity;
        import android.app.DatePickerDialog;
        import android.app.Dialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Typeface;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.Editable;
        import android.text.TextWatcher;
        import android.view.View;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
        import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

        import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class EditProjectTaskActivity extends AppCompatActivity {

    private EditText projectNameEditText;
    private EditText projectTaskDescriptionEditText;
    private ImageView projectBackButtonImgVW;
    private TextView projectDescriptionCountTextVW;
    private TextView endDateTextVW;
    private EditText endDateEditTxtVW;
    private EditText startDateEditTxtVW;
    private TextView startDateTextView;
    private int projectDescriptionCount=0;
    private Typeface fontAwesomeIcon;
    private String startDateToString;
    private String projectTaskStartDate;
    private String endDateToString;
    private  int start_year, start_month, start_day;
    private  int end_year, end_month, end_day;
    private Activity activity;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private   static final int DIALOG_ID1 = 0;
    private   static final int DIALOG_ID2 = 1;

    private JSONObject jsonProjectDetailsObject;
    private String projectTaskCount="0";
    private String  projectTaskDescription;
    private String projectDetailsInString;
    private  AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String projectName;
    private String taskName;
    private String  projectStartDate;
    private String  projectEndDate;
    private EditText projectTaskNameEditText;
    private JSONObject projectTaskToEditObject;
    private String taskDetailsInString;
    private String taskStartDate;
    private String taskEndDate;
    private SharedPreferences sharedPreferences;
    private  int membersCount;
    private ProgressBarClass progressBarClass;
    private HttpRequestClass httpRequestClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        start_year = calendar.get(Calendar.YEAR);
        start_month = calendar.get(Calendar.MONTH);
        start_day = calendar.get(Calendar.DAY_OF_MONTH);
        setContentView(R.layout.activity_edit_project_task);
        fontAwesomeIcon = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        projectNameEditText =(EditText) findViewById(R.id.projectNameEditTxtId);
        projectTaskNameEditText =(EditText) findViewById(R.id.projectTaskNameEditTxtId);
        projectNameEditText.setEnabled(false);
        projectNameEditText.setClickable(false);
        projectTaskNameEditText.setEnabled(false);
        projectTaskNameEditText.setClickable(false);
        projectTaskDescriptionEditText = findViewById(R.id.projectTaskDescripId);
        endDateTextVW = (TextView)findViewById(R.id.endDateTxtView);
        projectDescriptionCountTextVW = findViewById(R.id.projecDrespCharCountId);
        endDateEditTxtVW = (EditText)findViewById(R.id.endDateEditTxt);
        projectBackButtonImgVW = (ImageView) findViewById(R.id.projectBackButton);
        startDateEditTxtVW = (EditText)findViewById(R.id.startDateEditTxt);
        startDateTextView = (TextView)findViewById(R.id.startDateTxtView);
        endDateTextVW.setTypeface(fontAwesomeIcon);
        endDateTextVW = (TextView)findViewById(R.id.endDateTxtView);
        startDateTextView.setTypeface(fontAwesomeIcon);
        endDateTextVW.setTypeface(fontAwesomeIcon);
        activity = this;
        projectDetailsInString = getIntent().getStringExtra("editProjectInfo");
        endDateEditTxtVW.setEnabled(false);
        startDateEditTxtVW.setEnabled(false);
        taskDetailsInString = getIntent().getStringExtra("taskDetailsInString");
        alertDialogBuilder = new AlertDialog.Builder(activity);
        sharedPreferences = this.getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        projectStartDate = sharedPreferences.getString("projectStartDate",null);
       projectEndDate =  sharedPreferences.getString("projectEndDate",null);

        try {


            projectTaskToEditObject = new JSONObject(taskDetailsInString);
            projectNameEditText.setHint(projectTaskToEditObject.getString("projectName"));
            projectTaskNameEditText.setHint(projectTaskToEditObject.getString("taskName"));
            projectTaskStartDate = projectTaskToEditObject.getString("taskNameStartDate");
            startDateEditTxtVW.setHint(projectTaskStartDate);
            endDateEditTxtVW.setHint( projectTaskToEditObject.getString("taskNameEndDate"));
            projectTaskDescription = projectTaskToEditObject.getString("taskDescrp");
            membersCount =  projectTaskToEditObject.getInt("membersCount");
           // projectTaskCount=jsonProjectDetailsObject.getString("projecttaskscount");
            projectTaskDescriptionEditText.setHint(projectTaskDescription);
            projectDescriptionCount= projectTaskDescription.length();
            projectDescriptionCountTextVW.setText(""+ projectDescriptionCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        projectTaskDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projectDescriptionCountTextVW.setText(""+s.length());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                projectDescriptionCountTextVW.setText("" + projectDescriptionCount);
            }

        });

      if(membersCount>0)
        {
            startDateTextView.setEnabled(false);
            endDateTextVW.setEnabled(true);
        }
        else {

            startDateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createdDialog(0).show();
                }
            });
        }
        endDateTextVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdDialog(1).show();
            }
        });



    }


    public boolean getUpdatedFieldValue()
    {
        boolean checkError = false;
        ValidateEditProjectTaskInput validateEditProjectTaskInput = new ValidateEditProjectTaskInput();
        if((projectNameEditText.getText().toString()).length()>0) {
            projectName = projectNameEditText.getText().toString();
            String errorMessage = validateEditProjectTaskInput.validationProjectNameField(  projectName);
            if(!errorMessage.equalsIgnoreCase("field okay"))
            {
                projectNameEditText.setError(errorMessage);
                checkError=true;
            }
        }
        else
        {
            projectName = projectNameEditText.getHint().toString();
        }

        if(( projectTaskNameEditText.getText().toString()).length()>0) {
            taskName =  projectTaskNameEditText.getText().toString();
            String errorMessage = validateEditProjectTaskInput.validationProjectNameField(  projectName);
            if(!errorMessage.equalsIgnoreCase("field okay"))
            {
                projectTaskNameEditText.setError(errorMessage);
                checkError=true;
            }
        }
        else
        {
            taskName = projectTaskNameEditText.getHint().toString();
        }

        if((startDateEditTxtVW.getText().toString()).length()>0) {
            taskStartDate = startDateEditTxtVW.getText().toString();
        }
        else
        {
            taskStartDate = startDateEditTxtVW.getHint().toString();
        }

        if(( endDateEditTxtVW.getText().toString()).length()>0) {
            taskEndDate =  endDateEditTxtVW.getText().toString();

        }
        else
        {
            taskEndDate = endDateEditTxtVW.getHint().toString();
        }


        if((projectTaskDescriptionEditText.getText().toString()).length()>0) {
            projectTaskDescription = projectTaskDescriptionEditText.getText().toString();
            String errorMessage = validateEditProjectTaskInput.validationProjectDeescriptionField(projectTaskDescription);
            if(!errorMessage.equalsIgnoreCase("field okay"))
            {
                projectTaskDescriptionEditText.setError(errorMessage);
                checkError=true;
            }
        }
        else
        {
            projectTaskDescription = projectTaskDescriptionEditText.getHint().toString();
        }

        return checkError;

    }


    public void backToViewProject(View view)
    {
        Intent ePAIntent = new Intent(EditProjectTaskActivity.this, ViewProjectActivity.class);
        startActivity(ePAIntent);
    }

    @Override
    public void onBackPressed() {

    }

    public void updateProjectTaskButton(View view)
    {
        if(!getUpdatedFieldValue()) {

                String result =updateProjectDetails(projectName.trim(),taskName.trim(), projectTaskDescription.trim(), taskStartDate, taskEndDate)
                ;
                if (result.equalsIgnoreCase("record updated")) {
                  Intent ePAIntent = new Intent(EditProjectTaskActivity.this, ViewProjectActivity.class);
                    startActivity(ePAIntent);
                } else {
                    alertDialogBuilder.setMessage("Project could  not be updated due to network issue. Pleae try again later");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertDialog.dismiss();
                                    Intent ePAIntent = new Intent(EditProjectTaskActivity.this, ViewProjectActivity.class);
                                    startActivity(ePAIntent);
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

        }
    }

    protected Dialog createdDialog(int id) {
        if (id == DIALOG_ID1) {
            DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, startDatePickerListener, start_year, start_month, start_day);
            Calendar cal = Calendar.getInstance();
            cal.set(start_year, start_month, start_day);
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");
            Date startDate = null;
            try {
                startDate = f.parse(projectStartDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDatePickerDialog.getDatePicker().setMinDate(startDate.getTime());
            return startDatePickerDialog;
        }
        else if (id == DIALOG_ID2)
        {
            String[] splitDate = projectTaskStartDate.split("/");
            DatePickerDialog endDatePickerDialog = new DatePickerDialog(this, endDatePickerListener,Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2]));
            Calendar cal = Calendar.getInstance();

            cal.set(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]), Integer.parseInt(splitDate[2]));

            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");

            Date endDate = null;

            try {


                    endDate = f.parse(projectEndDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

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

            if(monthOfYear<10)
            {
                monthOfYearInString = "0" + (monthOfYear+1);

            }
            else
            {
                monthOfYearInString = "" + (monthOfYear+1);

            }
            startDateToString = "" + year + "/" + monthOfYearInString + "/" +dayOfMonthInString;
            startDateEditTxtVW.setText(startDateToString);
            endDateTextVW.setText( startDateToString);
            endDateTextVW.setEnabled(true);



        }
    };
    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String endDateToString=null;
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

            if(monthOfYear<10)
            {
                monthOfYearInString = "0" + (monthOfYear+1);

            }
            else
            {
                monthOfYearInString = "" + (monthOfYear+1);

            }
            endDateToString = "" + year + "/" + "" + monthOfYearInString+ "/"  +dayOfMonthInString;
            endDateEditTxtVW.setText(endDateToString);


        }
    };


    public String updateProjectDetails(String projectName,String projectTaskName,String projectDescription, String projectStartdate,String projectEnddate)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectName);
            postDataParams.put("projectTaskName", projectTaskName);
            postDataParams.put("projectTaskDescription", projectDescription);
            postDataParams.put("projectTaskStartDate", projectStartDate);
            postDataParams.put("projectTaskEndDate", projectEndDate);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/uptd/",postDataParams, activity,"text/plain", "application/json");
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

