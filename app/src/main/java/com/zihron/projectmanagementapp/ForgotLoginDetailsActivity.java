package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ForgotLoginDetailsActivity extends AppCompatActivity {

    private String userName;
    private String userPassword;
    private Activity activity;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private EditText emailEditText;
    private ImageView backToLoginPageImageVW;
    private ValidationClass validationClass;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private HttpRequestClass httpRequestClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_login_details);
        activity = this;
        alertDialogBuilder = new AlertDialog.Builder(activity);
        progressBarClass = new ProgressBarClass(activity);
            emailEditText = findViewById(R.id.emailEditTxtId);
        backToLoginPageImageVW = (ImageView)findViewById(R.id.backArrowLogoId);
        validationClass = new ValidationClass();

        backToLoginPageImageVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fLDAIntent = new Intent(ForgotLoginDetailsActivity.this, LoginActivity.class);
                startActivity(fLDAIntent);
            }
        });


    }

public void checkIfEmailInRecord(View view)
{
    userName = emailEditText.getText().toString();
    if(userName.length()==0)
    {
        emailEditText.setError("field cannot be empty");
    }
    else if(userName.length()>0 && !validationClass.validateEmail(userName))
    {
        emailEditText.setError("Invalid email. Email you enter is "+userName);
    }
    else
    {
        try {

            String result = getLoginDetails();
            JSONArray tempJArray = new JSONArray(result);
            if(tempJArray.length()>0) {
                JSONObject loginDetailJObject = new JSONObject(tempJArray.getString(0));

                userName = loginDetailJObject.getString("userName");
                userPassword = loginDetailJObject.getString("userPassword");

                Intent fDAIntnent = new Intent(ForgotLoginDetailsActivity.this, ResetPasswordQuestionConfirmation.class);
                fDAIntnent.putExtra("userName",userName);
                fDAIntnent.putExtra("userPassword",userPassword);
                startActivity(fDAIntnent);
            }
            else
            {

                final Dialog dialog = new Dialog(activity, R.style.WideDialog);
                dialog.setContentView(R.layout.passwordresetemailnotfoundlayout);
                Window window = dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                TextView okayTextView = (TextView)dialog.findViewById(R.id.backPressedId);
                okayTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
    @Override
    public void onBackPressed() {

    }


    public String getLoginDetails()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/feunap/",postDataParams, activity,"application/json", "application/json");
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

