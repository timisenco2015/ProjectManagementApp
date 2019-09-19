
package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PasswordResetQuestionsActivity extends AppCompatActivity {

    private EditText questionAEditText;
    private EditText ansAEditText;
    private  ValidatePasswordReqQuestFields  validatePasswordReqQuestFields;
    private EditText questionBEditText;
    private EditText ansBEditText;
    private EditText questionCEditText;
    private EditText ansCEditText;
    private String questionA;
    private String questionB;
    private String questionC;
    private String answerA;
    private String answerB;
    private String answerC;
    private String userName = null;
    private Activity activity;
    private   AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private HttpRequestClass httpRequestClass;

    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_reset_questions);
        questionAEditText =(EditText)findViewById(R.id.questionAEditTxt);
        ansAEditText =(EditText)findViewById(R.id.ansAEditTxt);
        questionBEditText =(EditText)findViewById(R.id.questionBEditTxt);
        ansBEditText =(EditText)findViewById(R.id.ansBEditTxt);
        questionCEditText =(EditText)findViewById(R.id.questionCEditTxt);
        ansCEditText =(EditText)findViewById(R.id.ansCEditTxt);
        activity=this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
       progressBarClass = new ProgressBarClass(activity);
        alertDialogBuilder = new AlertDialog.Builder(activity);
        userName = getIntent().getStringExtra("userName");

    }

    public void proceedToLogin(View view)
    {

        validatePasswordReqQuestFields = new  ValidatePasswordReqQuestFields();
        JSONArray questionsAndAnswersJArray = new JSONArray();
        JSONObject questionsAndAnswersJObject = null;
         questionA = questionAEditText.getText().toString();
        questionB = questionBEditText.getText().toString();
        questionC = questionCEditText.getText().toString();
        answerA = ansAEditText.getText().toString();
        answerB = ansBEditText.getText().toString();
        answerC = ansCEditText.getText().toString();
        boolean error = false;
        String questError1 = validatePasswordReqQuestFields.validateQuestionsField(questionA);
         String questError2 = validatePasswordReqQuestFields.validateQuestionsField(questionB);
        String questError3 = validatePasswordReqQuestFields.validateQuestionsField(questionC);
         String ansError1 = validatePasswordReqQuestFields.validateQuestionsField(answerA);
        String ansError2 = validatePasswordReqQuestFields.validateQuestionsField(answerB);
        String ansError3 = validatePasswordReqQuestFields.validateQuestionsField(answerC);

        if(!questError1.equalsIgnoreCase("field okay"))
        {
            questionAEditText.setError(questError1);
            error = true;
        }


        if(!questError2.equalsIgnoreCase("field okay"))
        {
            questionBEditText.setError(questError1);
            error = true;
        }

        if(!questError3.equalsIgnoreCase("field okay"))
        {
            questionCEditText.setError(questError1);
            error = true;
        }

        if(!ansError1.equalsIgnoreCase("field okay"))
        {
            ansAEditText.setError(questError1);
            error = true;
        }

        if(!ansError2.equalsIgnoreCase("field okay"))
        {
            ansBEditText.setError(questError1);
            error = true;
        }

        if(!ansError3.equalsIgnoreCase("field okay"))
        {
            ansCEditText.setError(questError1);
            error = true;
        }

        if(!error)
        {
            try {

                questionsAndAnswersJObject = new JSONObject();
               // questionsAndAnswersJObject.put("userName",userName);
                questionsAndAnswersJObject.put("questionNo","QuestA");
                questionsAndAnswersJObject.put("resetQuest",questionA);
                questionsAndAnswersJObject.put("resetQuestAns", answerA);
                questionsAndAnswersJArray.put(questionsAndAnswersJObject);
                questionsAndAnswersJObject = new JSONObject();
                questionsAndAnswersJObject.put("questionNo","QuestB");
                questionsAndAnswersJObject.put("resetQuest",questionB);
                questionsAndAnswersJObject.put("resetQuestAns", answerB);
                questionsAndAnswersJArray.put(questionsAndAnswersJObject);
                questionsAndAnswersJObject = new JSONObject();
                questionsAndAnswersJObject.put("questionNo","QuestC");
                questionsAndAnswersJObject.put("resetQuest",questionC);
                questionsAndAnswersJObject.put("resetQuestAns", answerC);
                questionsAndAnswersJArray.put(questionsAndAnswersJObject);

               String result =insertLoginDetailsRestQuestions(questionsAndAnswersJArray.toString(),userName);
                if(result.equalsIgnoreCase("record inserted"))
                {
                    alertDialogBuilder.setMessage("You are now in our record. You will now be redirected to the Login Page");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent pRQAIntent = new Intent(PasswordResetQuestionsActivity.this, LoginActivity.class);
                                    startActivity(pRQAIntent);
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog .show();

                }

                else
                {
                    alertDialogBuilder.setMessage("We cannot complete the login details reset questions. Please Login to continue. Thank You");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Intent pRQAIntent = new Intent(PasswordResetQuestionsActivity.this, LoginActivity.class);
                                    startActivity(pRQAIntent);
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog .show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    public String insertLoginDetailsRestQuestions(String requestQuestAndAnsToString,String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("requestQuestAndAnsToString", requestQuestAndAnsToString);
            postDataParams.put("userName", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/cldrq/",postDataParams, activity,"text/plain", "application/json");
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
