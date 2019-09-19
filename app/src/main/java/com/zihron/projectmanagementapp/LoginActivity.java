package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import android.support.v7.app.AlertDialog;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText passwordEditextIdETW;
    private EditText usernameEditextIdETW;
    private TextView userSignUpTextView;
    private TextView forgotPasswdTextView;
    private HttpRequestClass httpRequestClass;
    private SharedPreferences sharedPreferences;
    public static final String MyPreferences = "myPrefs";
    private Activity activity;
    private String userName;
    private String passWord;
    private CognitoUserPool userPool;
    private ProgressBarClass progressBarClass;
    private String apiId;
    // private CallbackManager callbackManager;
    private AlertDialog.Builder alertDialogBuilder;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private RequestContext requestContext;
    private String result;
    private AdminInitiateAuthRequest initialRequest;
    private CognitoUser user;
    private AlertDialog alertDialog = null;
    private Map authenticateDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FacebookSdk.sdkInitialize(getApplicationContext());
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("userName", null);

        setContentView(R.layout.activity_login);

        requestContext = RequestContext.create(getApplicationContext());
        activity = this;
        ZihronProjectManagmentApplication.get().getUserPoolCredentials(activity);
        ZihronProjectManagmentApplication.get().getCognitoCachingCredentialsProvider(activity);
        ZihronProjectManagmentApplication.get().getBasicAwsCredentians(activity);
        alertDialogBuilder = new AlertDialog.Builder(activity);
        passwordEditextIdETW = (EditText) findViewById(R.id.passwordEditextId);
        usernameEditextIdETW = (EditText) findViewById(R.id.usernameEditextId);
        forgotPasswdTextView = (TextView) findViewById(R.id.forgotPasswdTV);
        userSignUpTextView = (TextView) findViewById(R.id.userSignUpTV);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        userPool = ZihronProjectManagmentApplication.get().getCognitoUserPool();


        userSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Intent lAIntent = new Intent(LoginActivity.this, NewUserActivity.class);
                //   startActivity(lAIntent);
                Intent lAIntent = new Intent(LoginActivity.this, ConfirmEmailActivity.class);
                startActivity(lAIntent);
            }
        });
        forgotPasswdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lAIntent = new Intent(LoginActivity.this, ForgotLoginDetailsActivity.class);
                startActivity(lAIntent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ZihronProjectManagmentApplication.get().getIsRefreshTokenValid()) {
            Intent lAIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(lAIntent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(ZihronProjectManagmentApplication.get().getIsRefreshTokenValid()) {
            Intent lAIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(lAIntent);
        }
    }


    public void loadMainPage(View view) {
        Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
        sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.commit();
        startActivity(loginIntent);

            try {
                passWord = passwordEditextIdETW.getText().toString().trim();
                userName = usernameEditextIdETW.getText().toString().trim();


                result = confirmLoginDetails( userName, passWord);


                if (result.equalsIgnoreCase("Correct details")) {

                    String result = getLoginDetails(userName, passWord);
                    JSONArray loginJSONArray = new JSONArray(result);
                    authenticateDetails = new Hashtable();
                    authenticateDetails.put("username", userName);
                    authenticateDetails.put("password", passWord);
                    user = userPool.getUser(userName);
                    ZihronProjectManagmentApplication.get().setCognitoUser(user);
                    sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
                    editor.putString("userName", userName);
                    editor.putString("firstName", loginJSONArray.getJSONArray(0).getString(4));
                    editor.putString("lastName", loginJSONArray.getJSONArray(0).getString(5));
                    editor.commit();
                    final String mfaVerificationCode = loginJSONArray.getJSONArray(0).getString(3);

                    boolean isVerified = loginJSONArray.getJSONArray(0).getBoolean(2);
                    if (!isVerified) {

                      //  Intent loginIntent = new Intent(LoginActivity.this, VerifiedUserActivity.class);
                        startActivity(loginIntent);
                    } else {

                        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {


                            @Override
                            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                                String result = getLoginDetailsResetQuestions(userName);
                                if(result!=null)
                                {
                                    try {
                                        JSONArray tempArray = new JSONArray(result);
                                        if(tempArray.length()==0)
                                        {

                                            Intent loginIntent = new Intent(LoginActivity.this, PasswordResetQuestionsActivity.class);
                                            sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("userName", userName);
                                            editor.commit();
                                            startActivity(loginIntent);
                                        }
                                        else if (tempArray.length()>0)
                                        {
                                            Intent loginIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                            sharedPreferences = getSharedPreferences(MyPreferences, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("userName", userName);
                                            editor.commit();
                                            startActivity(loginIntent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                                // The API needs user sign-in credentials to continue
                                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userName, passWord, null);

                                // Pass the user sign-in credentials to the continuation

                                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                                // Allow the sign-in to continue
                                authenticationContinuation.continueTask();


                            }

                            @Override
                            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
                                // Multi-factor authentication is required; get the verification code from user
                                multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
                                // Allow the sign-in process to continue
                                multiFactorAuthenticationContinuation.continueTask();

                            }

                            @Override
                            public void authenticationChallenge(ChallengeContinuation continuation) {

                            }

                            @Override
                            public void onFailure(Exception exception) {
                                asyncErrorDialogDisplay.errorDialogDisplay("There is an error with the network. Please try again");

                            }
                        };

                        // Sign in the user
                        //  user.initiateUserAuthentication()
                        user.getSessionInBackground(authenticationHandler);

                    }


               } else {
                   asyncErrorDialogDisplay.errorDialogDisplay(result);
                }


            }  catch (JSONException e) {
                e.printStackTrace();
            }




    }



    public String getLoginDetails(String userName, String userPassword)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userName", userName);
            postDataParams.put("userPassword", userPassword);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fld/",postDataParams, activity,"application/json", "application/json");
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




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String getLoginDetailsResetQuestions(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("emailOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fldrq/",postDataParams, activity,"application/json", "application/json");
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




        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }



    public String confirmLoginDetails(String userName, String userPassword)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("userName", userName);
            postDataParams.put("userPassword", userPassword);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/cld/",postDataParams, activity,"text/plain", "application/json");
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

