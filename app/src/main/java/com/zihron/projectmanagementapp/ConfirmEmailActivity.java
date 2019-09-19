package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.CreateCustomVerificationEmailTemplateRequest;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.amazonaws.auth.policy.actions.SimpleEmailServiceActions.CreateCustomVerificationEmailTemplate;

public class ConfirmEmailActivity extends AppCompatActivity {
TextView emailEditTextView;
String userName;
private HttpRequestClass httpRequestClass;
private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
private Activity activity;
    AmazonSimpleEmailService amazonSimpleEmailService;
    private ValidationUserInformationClass validationUserInformationClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        emailEditTextView = (TextView)findViewById(R.id.emailEditTxtId);
        validationUserInformationClass = new ValidationUserInformationClass();


    }

    public void checkIfEmailIsVerified(View view)
    {
        userName = emailEditTextView.getText().toString();
        if (!validationUserInformationClass.validateEmail(userName))
        {
            emailEditTextView.setError("email not valid");
        }
        else
        {
            String result = checkIfUserExists(userName);
            if (!Boolean.parseBoolean(result)) {
                VerifyEmailAddressTask verifyEmailAddressTask = new VerifyEmailAddressTask(userName);
                boolean isEmailConfirmed = false;
                try {
                    isEmailConfirmed = verifyEmailAddressTask.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (isEmailConfirmed) {
                        Intent cEAIntent = new Intent(ConfirmEmailActivity.this, NewUserActivity.class);
                        cEAIntent.putExtra("emailAddrs",userName);
                        startActivity(cEAIntent);
                }
                else
                {
                    amazonSimpleEmailService = ZihronProjectManagmentApplication.get().getAwsCrediantils();
                    CreateCustomVerificationEmailTemplateRequest
                    amazonSimpleEmailService.createCustomVerificationEmailTemplate(C);

                }
            }
            else
            {
                emailEditTextView.setError("Email already exist in our record. Please use forgot password link to reset your password");
            }
        }

    }

        public String checkIfUserExists(String userName)
        {
            String  result = null;
            JSONObject postDataParams = new JSONObject();
            try {


                postDataParams.put("nameOfUser",  userName);
                httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/iue/",postDataParams, activity,"text/plain", "application/json");
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


        private boolean verifyEmailAddress(String address) {
        boolean isVerified = true;
        AmazonSimpleEmailService amazonSimpleEmailService =ZihronProjectManagmentApplication.get().getAwsCrediantils();
        //CreateCustomVerificationEmailTemplate createCustomVerificationEmailTemplate = new CreateCustomVerificationEmailTemplate();

            ListVerifiedEmailAddressesResult verifiedEmails = amazonSimpleEmailService.listVerifiedEmailAddresses();
        if (verifiedEmails!=null && !verifiedEmails.getVerifiedEmailAddresses().contains(address))
        {
            amazonSimpleEmailService.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(address));
            isVerified = false;

        }
        return isVerified;
    }


    private class VerifyEmailAddressTask extends AsyncTask<String, Void, Boolean> {
        private String userName;
        public VerifyEmailAddressTask(String userName) {

            this.userName=userName;

        }

        protected Boolean doInBackground(String... arg0) {
            boolean bool = verifyEmailAddress(userName);

            return bool;
        }

        protected void onPostExecute(Void feed) {

        }
    }
}
