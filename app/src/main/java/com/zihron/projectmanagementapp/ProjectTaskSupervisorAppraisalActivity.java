package com.zihron.projectmanagementapp;

        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.util.TypedValue;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.animation.AccelerateInterpolator;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.Spinner;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
        import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.text.SimpleDateFormat;
        import java.util.*;

public class ProjectTaskSupervisorAppraisalActivity extends AppCompatActivity {

    private LinearLayout subQuestnALinearLayout;
    private LinearLayout subQuestnBLinearLayout;
    private LinearLayout subQuestnCLinearLayout;
    private Spinner weekSpinner;
    private String appraisalYear;
    private HttpRequestClass httpRequestClass;
    private RadioGroup subQuestnAAnwsRadioGroup;
    private RadioGroup subQuestnBAnwsRadioGroup;
    private RadioGroup subQuestnCAnwsRadioGroup;
    private String projectTaskStartDate;
    private String projectTaskEndDate;
    private ObjectAnimator fadeOut;
    private ObjectAnimator fadeIn;
    private AnimatorSet mAnimationSet;
    private String appraiseeUserName;
    private List<String> weeklyList;
    private SharedPreferences sharedPreferences;

    private  String userName;
    private String questAAns;
    private String questBAns;
    private String questCAns;
    private TextView subQuestCErrorTextView;
    private TextView subQuestBErrorTextView;
    private TextView subQuestAErrorTextView;
    private Button appraisalButton;
    private JSONArray appraisalJSONArray;
    private JSONObject appraisalJSONObject;
    private int answerPointA;
    private int answerPointB;
    private int answerPointC;
    private String projectName;
    private String projectTaskName;
    private String appraisalWeek;
    private  AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private Activity activity;
    private TextView appraisalLabelTxtView;
    private TextView weekSelectnErrTextView;
    private ProgressBarClass progressBarClass;
    private String appraisalMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_task_supervisor_appraisal);
        activity=this;
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = this.getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);
        projectName = sharedPreferences.getString("projectName", null);
        projectTaskName = sharedPreferences.getString("projectTaskName", null);
        asyncErrorDialogDisplay = new  AsyncErrorDialogDisplay(activity);
        subQuestnALinearLayout = (LinearLayout)findViewById(R.id.subQuestnALLTId);
        subQuestnBLinearLayout= (LinearLayout)findViewById(R.id.subQuestnBLLTId);
        subQuestnCLinearLayout= (LinearLayout)findViewById(R.id.subQuestnCLLTId);

        subQuestnAAnwsRadioGroup = (RadioGroup)findViewById(R.id.subQuestnAAnwsId);
        subQuestnBAnwsRadioGroup= (RadioGroup)findViewById(R.id.subQuestnBAnwsId);
        subQuestnCAnwsRadioGroup= (RadioGroup)findViewById(R.id.subQuestnCAnwsId);
        subQuestCErrorTextView = (TextView) findViewById(R.id.subQuestCErrorTextVWId);
        subQuestBErrorTextView = (TextView) findViewById(R.id.subQuestBErrorTextVWId);
        subQuestAErrorTextView = (TextView) findViewById(R.id.subQuestAErrorTextVWId);
        appraisalLabelTxtView = (TextView) findViewById(R.id.appraisalLabelTxtVWId);
        weekSelectnErrTextView = (TextView)findViewById(R.id.weekSelectnErrTxtVWId);
        weekSpinner = (Spinner)findViewById(R.id.weekSpinnerId);
        weekSpinner.setEnabled(false);
        appraisalButton = (Button)findViewById(R.id.appraisalBtnId);
        projectTaskStartDate = activity.getIntent().getStringExtra("taskStartDate");
        projectTaskEndDate = activity.getIntent().getStringExtra("taskEndDate");
        weeklyList = new ArrayList<String>();
       Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int month =  (cal.get(Calendar.MONTH));
        appraiseeUserName = getIntent().getStringExtra("apprasieeUserName");
        appraisalWeek = "Week "+cal.get(Calendar.WEEK_OF_YEAR);
        populateWeeklySpinner();
        final String spinnerWeek = "Week "+cal.get(Calendar.WEEK_OF_YEAR);
        appraisalYear = ""+cal.get(Calendar.YEAR);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weeklyList) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                ((TextView) v).setTextColor(Color.parseColor("#676767"));

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                ((TextView) v).setTextColor(Color.parseColor("#676767"));

                if (position == 0) {
                    ((TextView) v).setTextColor(Color.parseColor("#979797"));
                }

                return v;
            }

         /*   @Override
            public boolean isEnabled(int position) {
                boolean check = false;
                String[] splittedString1 = weeklyList.get(position).toString().split("\\s+");
                String[] splittedString2 = spinnerWeek.split("\\s+");
                if(Integer.parseInt(splittedString1[1])<=Integer.parseInt(splittedString2[1]))
                {
                    check = true;
                }
                else
                {
                    check = false;
                }
                return check;
            }
            */
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        weekSpinner.setAdapter(dataAdapter);
     //   weekSpinner.setBackgroundColor(getResources().getColor(R.color.color7,null));
//
        subQuestnBLinearLayout.animate().translationX(2000);
        subQuestnCLinearLayout.animate().translationX(2000);
        appraisalJSONArray = new JSONArray();
        subQuestnAAnwsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) activity.findViewById(checkedId);
                questAAns = radioButton.getText().toString();
                subQuestAErrorTextView.setVisibility(View.GONE);
                if(checkedId==R.id.subQuestnAAnwsA)
                {
                    answerPointA =1;
                }
                else  if(checkedId==R.id.subQuestnAAnwsB)
                {
                    answerPointA =2;
                }
                else  if(checkedId==R.id.subQuestnAAnwsC)
                {
                    answerPointA =3;
                }
                else  if(checkedId==R.id.subQuestnAAnwsD)
                {
                    answerPointA =4;
                }
            }
        });

        subQuestnBAnwsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) activity.findViewById(checkedId);
                questBAns = radioButton.getText().toString();
                subQuestBErrorTextView.setVisibility(View.GONE);
                if(checkedId==R.id.subQuestnBAnwsA)
                {
                    answerPointB =1;
                }
                else  if(checkedId==R.id.subQuestnBAnwsB)
                {
                    answerPointB =2;
                }
                else  if(checkedId==R.id.subQuestnBAnwsC)
                {
                    answerPointB =3;
                }
                else  if(checkedId==R.id.subQuestnBAnwsD)
                {
                    answerPointB =4;
                }
            }
        });

        subQuestnCAnwsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) activity.findViewById(checkedId);
                questCAns = radioButton.getText().toString();
                subQuestCErrorTextView.setVisibility(View.GONE);
                if(checkedId==R.id.subQuestnCAnwsA)
                {
                    answerPointC =1;
                }
                else  if(checkedId==R.id.subQuestnCAnwsB)
                {
                    answerPointC =2;
                }
                else  if(checkedId==R.id.subQuestnCAnwsC)
                {
                    answerPointC =3;
                }
                else  if(checkedId==R.id.subQuestnCAnwsD)
                {
                    answerPointC=4;
                }
            }
        });
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appraisalWeek = weekSpinner.getSelectedItem().toString();
                weekSelectnErrTextView.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    @Override
    public void onBackPressed() {

    }
    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            weekSpinner.setEnabled(true);
        } else {
            weekSpinner.setEnabled(false);
        }
    }


    public void backToViewEachProjectTask(View view)
    {
        Intent vEPTAIntent = new Intent(ProjectTaskSupervisorAppraisalActivity.this, ViewEachProjectTaskActivity.class);
        startActivity(vEPTAIntent);
    }




        public void populateWeeklySpinner()
        {

            String[] dateStartSplitted = projectTaskStartDate.split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dateStartSplitted[0]),(Integer.parseInt(dateStartSplitted[1])-1),Integer.parseInt(dateStartSplitted[2]));
            int startWeek =calendar.get(Calendar.WEEK_OF_YEAR);;
            if(Integer.parseInt(dateStartSplitted[1])==12 && startWeek==1)
            {
                startWeek = 52;
            }


            String[] dateEndSplitted = projectTaskEndDate.split("/");
            calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dateEndSplitted[0]),(Integer.parseInt(dateEndSplitted[1])-1),Integer.parseInt(dateEndSplitted[2]));
            int endWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            if(Integer.parseInt(dateEndSplitted[1])==12 && endWeek==1)
            {
                endWeek = 52;
            }
            //  weeklyList.add(appraisalWeek);


            for(int i=startWeek; i<=endWeek; i++)
            {
                weeklyList.add("Week "+i);
            }
        }



    public void prevToLayoutB(View view)
    {
        subQuestBErrorTextView.setVisibility(View.GONE);
        subQuestnCLinearLayout.animate().translationX(2000).setDuration(500).setInterpolator(new AccelerateInterpolator());
        subQuestnBLinearLayout.animate().translationX(0).setDuration(500).setInterpolator(new AccelerateInterpolator());
        appraisalButton.setVisibility(View.GONE);
    }

    public void nextToLayoutC(View view)
    {
        if(questBAns==null)
        {
            subQuestBErrorTextView.setText("You haven't selected answer for this question");
            subQuestBErrorTextView.setVisibility(View.VISIBLE);
        }
        else {
            subQuestnBLinearLayout.animate().translationX(-2000).setDuration(500).setInterpolator(new AccelerateInterpolator());
            subQuestnCLinearLayout.animate().translationX(0).setDuration(500).setInterpolator(new AccelerateInterpolator());
            appraisalButton.setVisibility(View.VISIBLE);
            try {
                appraisalJSONObject = new JSONObject();
                appraisalJSONObject.put("questionNumber","questB");
                appraisalJSONObject.put("answerOptionsNo",questBAns);
                appraisalJSONObject.put("answerPoint",answerPointB );
                appraisalJSONArray.put(appraisalJSONObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void prevToLayoutA(View view)
    {
        subQuestAErrorTextView.setVisibility(View.GONE);
        subQuestnBLinearLayout.animate().translationX(2000).setDuration(500).setInterpolator(new AccelerateInterpolator());
        subQuestnALinearLayout.animate().translationX(0).setDuration(500).setInterpolator(new AccelerateInterpolator());

    }

    public void nextToLayoutB(View view)
    {
        if(questAAns==null)
        {
            subQuestAErrorTextView.setText("You haven't selected answer for this question");
            subQuestAErrorTextView.setVisibility(View.VISIBLE);
        }
        else {
            appraisalJSONObject= new JSONObject();
            try {
                appraisalJSONObject.put("questionNumber","questA");
                appraisalJSONObject.put("answerOptionsNo",questAAns);
                appraisalJSONObject.put("answerPoint",answerPointA );
                appraisalJSONArray.put(appraisalJSONObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            subQuestnALinearLayout.animate().translationX(-2000).setDuration(500).setInterpolator(new AccelerateInterpolator());
            subQuestnBLinearLayout.animate().translationX(0).setDuration(500).setInterpolator(new AccelerateInterpolator());
        }

    }



    public void memberSelfAppraisal(View view)
    {
        if(questCAns==null)
        {
            subQuestCErrorTextView.setText("You haven't selected answer for this question");
            subQuestCErrorTextView.setVisibility(View.VISIBLE);
        }
        else {
            if(appraisalWeek==null || appraisalWeek=="")
            {
                weekSelectnErrTextView.setVisibility(View.VISIBLE);
            }
            else {
                weekSelectnErrTextView.setVisibility(View.GONE);
                try {
                    appraisalJSONObject = new JSONObject();
                    appraisalJSONObject.put("questionNumber", "questC");
                    appraisalJSONObject.put("answerOptionsNo", questCAns);
                    appraisalJSONObject.put("answerPoint", answerPointC);
                    appraisalJSONArray.put(appraisalJSONObject);
                   String result = insertNewSupervisorAppraisal(appraisalJSONArray.toString(), appraisalWeek, userName, projectName, projectTaskName, appraisalYear,appraiseeUserName);
                    if (result.equalsIgnoreCase("Record Inserted")) {
                        Toast.makeText(activity, "Your record has been inserted", Toast.LENGTH_LONG).show();
                        Intent pTSAAIntent = new Intent(ProjectTaskSupervisorAppraisalActivity.this, ViewEachProjectTaskActivity.class);
                        startActivity(pTSAAIntent);
                    } else {
                        Toast.makeText(activity, "Your record not inserted. It might be from our end. Please try again in some minutes", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String insertNewSupervisorAppraisal(String appraisalDetailsToString,String appraisalWeek,String userName, String projectName, String projectTaskName, String appraisalYear, String appraiseeName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        String appraisalTime = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date());
        String appraisalDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        try {

            postDataParams.put("nameOfUser", userName);
            postDataParams.put("projectName",projectName);
            postDataParams.put("projectTaskName", projectTaskName);
            postDataParams.put("appraisalYear",appraisalYear);
            postDataParams.put("appraisalDate", appraisalDate);
            postDataParams.put("appraisalTime", appraisalTime);
            postDataParams.put("appraisalWeek", appraisalWeek);
            postDataParams.put("nameOfAppraisee", appraiseeName);

            postDataParams.put("appraisalDetailsToString", appraisalDetailsToString);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iisad/",postDataParams, activity,"text/plain", "application/json");
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

