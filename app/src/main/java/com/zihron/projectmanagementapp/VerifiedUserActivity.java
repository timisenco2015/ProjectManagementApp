package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.regions.Regions;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.zihron.projectmanagementapp.LoginActivity.MyPreferences;

public class VerifiedUserActivity extends AppCompatActivity {

    private EditText verification1EditText;
    private EditText verification2EditText;
    private EditText verification3EditText;
    private EditText verification4EditText;
    private EditText verification5EditText;
    private EditText verification6EditText;
    private Button cancelButton;
    private String userVerifiedCode;
    private Button okayButton;
    private CognitoUser user;
    private CognitoUserPool userPool;
    private SharedPreferences sharedPreferences;
    private String userName;
    private Map<String,String> authentiDetails =null;
    private Thread thread;
    private Activity activity;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String result;
 private  AlertDialog.Builder alertDialogBuilder;
    private HttpRequestClass httpRequestClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_user);
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE );
        userName = sharedPreferences.getString("userName",null);
        userPool = new CognitoUserPool(getApplicationContext(), "us-east-1_ijiaYHaYp", "4n0lk5mjd17u37hjnbhrgb8v8e", "i9hdqm71kafnnhk77iave9abm6q3b5tcau1kirn8a08q9qq613l", Regions.US_EAST_1);
        authentiDetails=  new HashMap<String, String>();
        authentiDetails.put("Username", "value1");
        authentiDetails.put("Password", "value2");
        activity = this;
        user = userPool.getUser( userName);

        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);




        verification1EditText = (EditText)findViewById(R.id.verification1EditTxtId);
        verification2EditText = (EditText)findViewById(R.id.verification2EditTxtId);
        verification3EditText = (EditText)findViewById(R.id.verification3EditTxtId);
        verification4EditText = (EditText)findViewById(R.id.verification4EditTxtId);
        verification5EditText = (EditText)findViewById(R.id.verification5EditTxtId);
        verification6EditText = (EditText)findViewById(R.id.verification6EditTxtId);
        cancelButton = (Button)findViewById(R.id.cancelButtonId);
        okayButton = (Button)findViewById(R.id.okayButtonId);

        verification1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });
        verification2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });

        verification3EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });
        verification4EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });

        verification5EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });

        verification6EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(activity,"ttt "+s.length(),Toast.LENGTH_LONG).show();
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                else if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttoninputborder,null));
                }
                if(s.length()<=0)
                {
                    verification1EditText.setBackground(activity.getResources().getDrawable(R.drawable.verificationbuttonborder,null));
                }
            }
        });
         thread=new Thread(){
            public void run(){
                try
                {



                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };


    }

    public void resendCode(View view)
    {

        resendVerificationCode();
    }

    public void backToLogin(View view)
    {
        Intent vUAIntent = new Intent(VerifiedUserActivity.this, LoginActivity.class);
        startActivity(vUAIntent);
    }

    public void verifiedUser(View view)
    {

        userVerifiedCode = verification1EditText.getText().toString()
                +verification2EditText.getText().toString()
                +verification3EditText.getText().toString()
                +verification4EditText.getText().toString()
                +verification5EditText.getText().toString()
                +verification6EditText.getText().toString();



            result = updateLoginVerification(true, userName, userVerifiedCode);
            if(result!=null && result.equalsIgnoreCase("Record Updated")) {
                confirmUser();
            }
            else
            {
                alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage("Error with network. Please click on cancel button to login again");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent vUAIntent = new Intent(VerifiedUserActivity.this, LoginActivity.class);
                                startActivity(vUAIntent);
                            }
                        });
                alertDialogBuilder.show();
            }




    }

    public void confirmUser()
    {
        GenericHandler confirmationCallback = new GenericHandler() {

            @Override
            public void onSuccess() {

                alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage("You have been verified. You will be redirected to login page");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent vUAIntent = new Intent(VerifiedUserActivity.this, LoginActivity.class);
                                startActivity(vUAIntent);
                            }
                        });
                alertDialogBuilder.show();



            }

            @Override
            public void onFailure(Exception exception) {
                alertDialogBuilder = new AlertDialog.Builder(activity);

                alertDialogBuilder.setMessage( "A network issue has prevented us from confirming you. A confirmation code has been sent to your email. Please re verify account ");
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                resendVerificationCode();
                                Intent vUAIntent = new Intent(VerifiedUserActivity.this, LoginActivity.class);
                                startActivity(vUAIntent);
                            }
                        });
                alertDialogBuilder.show();
            }
        };
        boolean forcedAliasCreation = false;

// Call API to confirm this user
        user.confirmSignUpInBackground(userVerifiedCode, forcedAliasCreation, confirmationCallback);

    }
    @Override
    public void onBackPressed() {

    }

private void resendVerificationCode()
{


    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails details) {

                        Toast.makeText(activity,"Verification code sent to "+details.getAttributeName(),Toast.LENGTH_LONG).show();


        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(activity,"Unable to send verification code, please click on resend button again",Toast.LENGTH_LONG).show();


        }
    };
    user.resendConfirmationCodeInBackground(resendConfCodeHandler);




}



    public String updateLoginVerification(boolean userVerified, String userName, String verifiedCode)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            postDataParams.put("userVerified", userVerified);
            postDataParams.put("userVerifiedCode", verifiedCode);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/uulv/",postDataParams, activity,"text/plain", "application/json");
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
