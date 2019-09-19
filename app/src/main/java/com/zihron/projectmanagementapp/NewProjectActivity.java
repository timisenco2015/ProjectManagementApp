package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ZihronChatApp.ZihronWorkChat.Channels.ZWCGroupChannel;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class NewProjectActivity extends AppCompatActivity {

    int start_year, start_month, start_day;
    int end_year, end_month, end_day;
    int hour_x, minutes_x, second_x;
    static final int DIALOG_ID1 = 0;
    static final int DIALOG_ID2 = 1;

    TextView startDateTextVW;
    TextView endDateTextVW;

    private SharedPreferences sharedPreferences;
    private String userName;
    private HttpRequestClass httpRequestClass;
    TextView dialogQuestTextVW;
    EditText projectNameEdiTextView;
    EditText projectDescripEditTextVieW;
    EditText startDateEditTxtVW;
    EditText endDateEditTxtVW;
    TextView projecDrespCharCountIdTVW;
    Button createProjectBtn;
    private String projectEndDate;
    private String projectStartDate;
    private String projectName;
    private String startDateToString;
    private String endDateToString;
    ImageView projectBackButtonImgVW;
    private Button createProjectButton;
    private Button updateProjectButton;
    private Activity activity;
    private boolean isProjectNameExis_MaxtError;
    private String projectDescription;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private  AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ProgressBarClass progressBarClass;
    private ZWCGroupChannel zihronChatAppGroupChannel;
    Typeface fontAwesomeIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        fontAwesomeIcon = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        final Calendar calendar = Calendar.getInstance();
        start_year = calendar.get(Calendar.YEAR);
        start_month = calendar.get(Calendar.MONTH);
        start_day = calendar.get(Calendar.DAY_OF_MONTH);
        isProjectNameExis_MaxtError= false;
        createProjectButton = (Button) findViewById(R.id.createProjectButtonId);
        updateProjectButton = (Button) findViewById(R.id.updateProjectButtonId);
        createProjectButton.setEnabled(true);
        activity = this;
        progressBarClass = new ProgressBarClass(activity);
        createProjectButton.setVisibility(View.VISIBLE);
        startDateTextVW = (TextView) findViewById(R.id.startDateTxtView);
        endDateTextVW = (TextView) findViewById(R.id.endDateTxtView);
        projectBackButtonImgVW = (ImageView) findViewById(R.id.projectBackButton);

        projectNameEdiTextView = (EditText) findViewById(R.id.projectNameId);
        projectDescripEditTextVieW = (EditText) findViewById(R.id.projectDescripId);
        startDateEditTxtVW = (EditText) findViewById(R.id.startDateEditTxt);
        endDateEditTxtVW = (EditText) findViewById(R.id.endDateEditTxt);
        endDateTextVW.setEnabled(false);
        startDateEditTxtVW = (EditText) findViewById(R.id.startDateEditTxt);
        projecDrespCharCountIdTVW = (TextView) findViewById(R.id.projecDrespCharCountId);
        endDateEditTxtVW = (EditText) findViewById(R.id.endDateEditTxt);


        startDateTextVW.setTypeface(fontAwesomeIcon);
        endDateTextVW.setTypeface(fontAwesomeIcon);

        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        zihronChatAppGroupChannel = new ZWCGroupChannel(activity);

        projectDescripEditTextVieW.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projecDrespCharCountIdTVW.setText("" + s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TextWatcherTest", "afterTextChanged:\t" + s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

        });


        projectNameEdiTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String result = isProjectNameExists(projectNameEdiTextView.getText().toString());
                    if(!result.equalsIgnoreCase("False"))
                    {
                        projectNameEdiTextView.setError("You already have a group with similar name for this project in our database. Please choose a different name.Thanks");
                        isProjectNameExis_MaxtError = true;
                    }
                    if((projectNameEdiTextView.getText().toString()).length()>=60)
                    {
                        projectNameEdiTextView.setError("Maximum length allowd is 59");
                        isProjectNameExis_MaxtError = true;
                    }
                    else if((projectNameEdiTextView.getText().toString()).length()<60)
                    {
                        isProjectNameExis_MaxtError = false;
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

    }

    public boolean getUpdatedFieldValue() {
        boolean checkError = false;
        ValidateEditProjectInput validateEditProjectInput = new ValidateEditProjectInput();
        String errorMessage = null;
        projectName = projectNameEdiTextView.getText().toString();
        errorMessage = validateEditProjectInput.validationProjectNameField(projectName);
        if (!errorMessage.equalsIgnoreCase("field okay")) {
            projectNameEdiTextView.setError(errorMessage);
            checkError = true;
        }


        projectStartDate = startDateEditTxtVW.getText().toString();
        projectEndDate = endDateEditTxtVW.getText().toString();


        projectDescription = projectDescripEditTextVieW.getText().toString();
        errorMessage = validateEditProjectInput.validationProjectDeescriptionField(projectDescription);
        if (!errorMessage.equalsIgnoreCase("field okay")) {
            projectDescripEditTextVieW.setError(errorMessage);
            checkError = true;
        }


        return checkError;

    }

    public void startDatePicker(View view) {

        createdDialog(0).show();
        // showDialog(0,null);
    }


    public void endDatePicker(View view) {

        createdDialog(1).show();

    }


    protected Dialog createdDialog(int id) {
        if (id == DIALOG_ID1) {
            DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, startDatePickerListener, start_year, start_month, start_day);
            Calendar cal = Calendar.getInstance();
            cal.set(start_year, start_month, start_day);
            Date start = cal.getTime();
            startDatePickerDialog.getDatePicker().setMinDate(start.getTime());
            return startDatePickerDialog;
        } else if (id == DIALOG_ID2) {
            DatePickerDialog endDatePickerDialog = new DatePickerDialog(this, endDatePickerListener, end_year, end_month, end_day);
            Calendar cal = Calendar.getInstance();
            cal.set(end_year, end_month, end_day);

            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd");

            Date endDate = null;
            Date startDate = null;
            try {

                startDate = f.parse(startDateToString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            endDatePickerDialog.getDatePicker().setMinDate(startDate.getTime());

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
            startDateToString = "" + year + "/" + monthOfYearInString + "/" + dayOfMonthInString;

            startDateEditTxtVW.setText(startDateToString);
            endDateTextVW.setEnabled(true);


        }
    };
    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


            String endDateToString = null;
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
            endDateToString = "" + year + "/" + "" + monthOfYearInString + "/" + dayOfMonthInString;


            endDateEditTxtVW.setText(endDateToString);


        }
    };


    public void createNewProject(View view) {

        if (!getUpdatedFieldValue() && !isProjectNameExis_MaxtError) {
            String projectDefaultGroup = "Default"+projectName.substring(0,(projectName.length()/2)-1)+generateCode();
            sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("projectDefaultGroup", projectDefaultGroup);

            editor.commit();

                String result =   insertNewProject(projectName.trim(), projectDescription.trim(), projectStartDate.trim(), projectEndDate.trim(),projectDefaultGroup,userName)
                ;
                if (result.equalsIgnoreCase("Record Inserted")) {
                    List<String> chatMemebersList = new ArrayList<String>();
                    chatMemebersList.add(userName);

                    zihronChatAppGroupChannel.createGroupChannel(chatMemebersList,projectDefaultGroup);
                    questionsAskDialog(projectName);
                } else {
                    alertDialogBuilder.setMessage("Project could  not be updated due to network issue. Pleae try again later");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertDialog.dismiss();
                                    Intent ePAIntent = new Intent(NewProjectActivity.this, ViewProjectActivity.class);
                                    startActivity(ePAIntent);
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }



        }


    }

    public void backToViewProject(View view)
    {
        Intent nPAIntent = new Intent(NewProjectActivity.this, ViewProjectActivity.class);
        startActivity(nPAIntent);
    }


    public String insertNewProject(String projectName, String projectDescription, String projectStartdate, String projectEnddate,String defaultGroupName,String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String projectCreatedate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        try {
            postDataParams.put("projectName", projectName);
            postDataParams.put("projectDescription", projectDescription);
            postDataParams.put("projectStartDate", projectStartdate);
            postDataParams.put("projectCreateDate", projectCreatedate);
            postDataParams.put("projectEndDate", projectEnddate);
            postDataParams.put("projectDonePercentage", 0);
            postDataParams.put("nameOfDefaultGroup", defaultGroupName);
            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inpd/",postDataParams, activity,"text/plain", "application/json");
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


    public String generateCode() {

        Random r = new Random();
        String generatedNumber="";
        String alphabet = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        for (int i = 0; i < 4; i++) {
            generatedNumber+=alphabet.charAt(r.nextInt(alphabet.length()));
        } // prints 50 random characters from alphabet
        return generatedNumber;

    }




    public String isProjectNameExists(String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);

            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/ciue/",postDataParams, activity,"text/plain", "application/json");
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








    public void questionsAskDialog(final String projectName) {

        final Dialog viewProjectTaskQuestDialog = new Dialog(this);
        viewProjectTaskQuestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewProjectTaskQuestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        viewProjectTaskQuestDialog.setContentView(R.layout.createtaskdialogpopquest);
        Button noTaskQuestIdBtn = viewProjectTaskQuestDialog.findViewById(R.id.noTaskCreateQuestId);
        Button yesTaskQuestIdBtn = viewProjectTaskQuestDialog.findViewById(R.id.yesTaskCreateQuestId);
        TextView questionTextView = viewProjectTaskQuestDialog.findViewById(R.id.questionTextViewId);
        questionTextView.setText("Do you want to add task to this projectk");
        noTaskQuestIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent NPAIntent = new Intent(NewProjectActivity.this, HomeActivity.class);
                NewProjectActivity.this.startActivity(NPAIntent);
            }
        });

        yesTaskQuestIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent NPAIntent = new Intent(NewProjectActivity.this, NewProjectTaskActivity.class);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("projectName", projectName);
                editor.putString("projectStartDate", projectStartDate);
                editor.putString("projectEndDate", projectEndDate);
                editor.commit();

                NewProjectActivity.this.startActivity(NPAIntent);

            }

            ;


        });


        viewProjectTaskQuestDialog.show();


    }

}










