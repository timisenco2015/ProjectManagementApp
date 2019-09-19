package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.amazonaws.auth.policy.actions.SimpleEmailServiceActions.CreateCustomVerificationEmailTemplate;

public class NewUserActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditText;
   private TextInputEditText middleNameEditText;
    private TextInputEditText homeAddEditText;
    private TextInputEditText lastNameEditText;
    private Spinner citySpinner;
    private Spinner genderSpinner;
    private Spinner provinceStateSpinner;
    private Spinner countrySpinner;
    private TextInputEditText postalZipCodeEditText;
    private TextInputEditText userNameEditText;
    private TextInputEditText reUserNameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText rePasswordEditText;
    private JSONArray attributesList;
    private String finalPhoneNumber;
    private CognitoUserAttributes userAttributes;
    private HttpRequestClass httpRequestClass;
    CognitoUserPool userPool;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String emailAddress;
    private String homeAddress;
    private String cityName;
    private String province_StateName;
    private String countryName;
    private String postal_ZipCode;
    private String phoneNumber;
    private String userName;
    private String reUserName;
    private String password;
    private String rePassword;
    private String phoneNoCountryCode;
    private String initialPhoneNumber;
    private Activity activity;
    private CognitoUser user;
    private String initialHomeAddress;
    private ValidationClass validationClass;

    private TextInputEditText phoneNumberEditText;
    private JSONArray allCountriesJSONArray;
    private JSONArray allProvinceStateJSONArray;
    private JSONArray allCitiesJSONArray;
    private JSONArray genderJSONArray;
    private TextView navigateToLoginTextVieW;
    private Country_Province_State_Cities_Adapter c_p_s_c_adapter;

    private ValidatePhoneWithNumVerify validatePhoneWithNumVerifyTask;
    private ValidationUserInformationClass validationUserInformationClass;
    private AlertDialog.Builder alertDialogBuilder;
    private TextInputEditText phoneNoCountryCodeEditText;
    private AlertDialog alertDialog;
    private RelativeLayout relativeLayoutA;
    private RelativeLayout relativeLayoutB;
    private RelativeLayout relativeLayoutC;
    private RelativeLayout relativeLayoutD;
    private TextView  provinceStateSpinnerId;
    private ProgressBarClass progressBarClass;
    private TextView genderSpinnerErrorTextView;
    private TextView  citySpinnerErrorTextView;
    private TextView  pSSpinnerErrorTextView;
    private TextView  countrySpinnerErrorTextView;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private Button addUserButton;
    private String userEmailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        activity = this;
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        progressBarClass = new ProgressBarClass(activity);
        validationClass = new ValidationClass();
         userAttributes = new CognitoUserAttributes();
         userEmailAddress = getIntent().getStringExtra("emailAddrs");
        validationUserInformationClass = new ValidationUserInformationClass();

            userPool = ZihronProjectManagmentApplication.get().getCognitoUserPool() ;

        relativeLayoutA = (RelativeLayout)findViewById(R.id.relativeLayoutAId);
        relativeLayoutB = (RelativeLayout)findViewById(R.id.relativeLayoutBId);
        relativeLayoutC = (RelativeLayout)findViewById(R.id.relativeLayoutCId);
        relativeLayoutD = (RelativeLayout)findViewById(R.id.relativeLayoutDId);
        addUserButton = (Button)findViewById(R.id.addUserBtnId);
        firstNameEditText = (TextInputEditText) findViewById(R.id.firstNameEditTxtId);
         middleNameEditText= (TextInputEditText) findViewById(R.id.middleNameEditTxtId);
        lastNameEditText= (TextInputEditText) findViewById(R.id.lastNameEditTxtId);
        genderSpinner = (Spinner) findViewById(R.id.genderSpinnerId);
        phoneNumberEditText= (TextInputEditText) findViewById(R.id.phoneNoEditTxtId);
        phoneNoCountryCodeEditText = (TextInputEditText)findViewById(R.id.phoneNoCountryCodeEditTxtId);
       homeAddEditText= (TextInputEditText) findViewById(R.id.homeAddEditTxtId);
       navigateToLoginTextVieW = (TextView)findViewById(R.id.navigateToLoginTxtVWId);
        citySpinner= (Spinner) findViewById(R.id.citySpinnerId);
        provinceStateSpinner= (Spinner) findViewById(R.id.provinceStateSpinnerId);
    countrySpinner= (Spinner) findViewById(R.id.countrySpinnerId);
    postalZipCodeEditText= (TextInputEditText) findViewById(R.id.postalZipCodeEditTxtId);
    userNameEditText= (TextInputEditText) findViewById(R.id.userNameEditTxtId);
        reUserNameEditText= (TextInputEditText) findViewById(R.id.reUsernameEditTxtId);
        userNameEditText.setText(userEmailAddress);
        userNameEditText.setEnabled(false);
        reUserNameEditText.setText(userEmailAddress);
        reUserNameEditText.setEnabled(false);
        passwordEditText= (TextInputEditText) findViewById(R.id.passwordEditTxtId);
        rePasswordEditText= (TextInputEditText) findViewById(R.id.rePasswordEditTxtId);
        genderSpinnerErrorTextView = (TextView)findViewById(R.id.genderSpinnerErrorTextVWId);
                citySpinnerErrorTextView = (TextView)findViewById(R.id.citySpinnerErrorTextVWId);
        pSSpinnerErrorTextView = (TextView)findViewById(R.id.pSSpinnerErrorTextVWId);
        countrySpinnerErrorTextView = (TextView)findViewById(R.id.countrySpinnerErrorTextVWId);
        genderSpinnerErrorTextView.setVisibility(View.GONE);
                citySpinnerErrorTextView.setVisibility(View.GONE);
        pSSpinnerErrorTextView.setVisibility(View.GONE);
                countrySpinnerErrorTextView.setVisibility(View.GONE);

        navigateToLoginTextVieW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nUAIntent = new Intent(NewUserActivity.this, LoginActivity.class);
                startActivity(nUAIntent);
            }
        });


        relativeLayoutB.animate().translationY(2000);
        relativeLayoutC.animate().translationY(-2000);
        relativeLayoutD.animate().translationX(-2000);
        addUserButton.setVisibility(View.GONE);

        String pkgName = this.getPackageName();
        // Return 0 if not found.



        populateGenderSpinner();
        populateCountriesSpinner();


    }




    public void populateGenderSpinner()
    {
        try {
         genderJSONArray = new JSONArray();
        JSONArray genderInnerJSONArray1 = new JSONArray();
        JSONArray genderInnerJSONArray2 = new JSONArray();
        genderJSONArray.put(0,"");
        genderInnerJSONArray1.put("Male");
        genderInnerJSONArray2.put("FeMale");
        genderJSONArray.put(genderInnerJSONArray1);
        genderJSONArray.put(genderInnerJSONArray2);


        c_p_s_c_adapter = new Country_Province_State_Cities_Adapter(this, genderJSONArray,false,true);

        // attaching data adapter to spinner
        genderSpinner.setAdapter(c_p_s_c_adapter);
        genderSpinner.setSelection(0, false);

            genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    JSONArray tempArray = null;

                    try {
                        tempArray = genderJSONArray.getJSONArray(position);
                        genderSpinnerErrorTextView.setVisibility(View.GONE);
                        genderSpinnerErrorTextView.setText("");
                        gender = tempArray.getString(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            } catch (JSONException e) {
        e.printStackTrace();
    }
    }



    public void populateCountriesSpinner()
    {
        boolean isProviceStateCity =false;

        String result = null;
        try {
            result = getAllCountries();
            allCountriesJSONArray = new JSONArray(result);
            allCountriesJSONArray.put(0,"");


        } catch (JSONException e) {
            e.printStackTrace();
        }



        c_p_s_c_adapter = new Country_Province_State_Cities_Adapter(this, allCountriesJSONArray,false,false);

        // attaching data adapter to spinner
        countrySpinner.setAdapter(c_p_s_c_adapter);
        countrySpinner.setSelection(0, false);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONArray tempArray = allCountriesJSONArray.getJSONArray(position);
                    phoneNoCountryCodeEditText.setText(tempArray.getString(3));
                    countrySpinnerErrorTextView.setVisibility(View.GONE);
                    countrySpinnerErrorTextView.setText("");

                    countryName = tempArray.getString(2);
                    populateStateProvinceSpinner(tempArray.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void populateStateProvinceSpinner(String countryId)
    {
        try {
            boolean isProviceStateCity =true;
            String result =  getAllStatesProvinces(countryId);

            allProvinceStateJSONArray = new JSONArray(result);
            allProvinceStateJSONArray.put(0,"");

           c_p_s_c_adapter = new Country_Province_State_Cities_Adapter(this, allProvinceStateJSONArray,true,false);

            // attaching data adapter to spinner
            provinceStateSpinner.setAdapter(c_p_s_c_adapter);
            provinceStateSpinner.setSelection(0, false);
            pSSpinnerErrorTextView.setVisibility(View.GONE);
            provinceStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONArray tempArray = allProvinceStateJSONArray.getJSONArray(position);

                        pSSpinnerErrorTextView.setText("");

                        province_StateName = tempArray.getString(1);

                        populateCitiesSpinner( tempArray.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void populateCitiesSpinner(String stateProvinceId)
    {


        try {
            boolean isProviceStateCity =true;
            String result =  getAllCities(stateProvinceId);

            allCitiesJSONArray = new JSONArray(result);
            allCitiesJSONArray.put(0,"");
              c_p_s_c_adapter = new Country_Province_State_Cities_Adapter(this, allCitiesJSONArray, true,false);
            // attaching data adapter to spinner
            citySpinner.setAdapter(c_p_s_c_adapter);

            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONArray tempArray = allCitiesJSONArray.getJSONArray(position);
                        citySpinnerErrorTextView.setVisibility(View.GONE);
                        citySpinnerErrorTextView.setText("");

                        cityName = tempArray.getString(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

  public void initializeAttributes()
  {

      try {

          userAttributes.addAttribute("email", attributesList.getString(0));
          userAttributes.addAttribute("custom:password", attributesList.getString(1));
          userAttributes.addAttribute("phone_number", attributesList.getString(2));
      } catch (JSONException e) {
          e.printStackTrace();
      }

  }

    @Override
    public void onBackPressed() {

    }
  public boolean validateAllSegmentA()
  {
      boolean  errorFound=false;
      firstName = firstNameEditText.getText().toString();
      middleName = middleNameEditText.getText().toString();
      lastName = lastNameEditText.getText().toString();
      String firstNameStringError=validationUserInformationClass.validationFirstName(firstName);
      String middleNameStringError=validationUserInformationClass.validationMiddleName(middleName);
      String lastNameStringError=validationUserInformationClass.validationLastName(lastName);
      if(!firstNameStringError.equalsIgnoreCase("field okay"))
      {
          firstNameEditText.setError(firstNameStringError);
          errorFound=true;
      }

      if(!middleNameStringError.equalsIgnoreCase("field okay"))
      {
          middleNameEditText.setError(middleNameStringError);
          errorFound=true;
      }

      if(!lastNameStringError.equalsIgnoreCase("field okay"))
      {
          lastNameEditText.setError(lastNameStringError);
          errorFound=true;
      }
return  errorFound;
  }

  public boolean   validateAllSegmentB()
  {
      boolean errorFound = false;


      if(gender==null)
      {
          errorFound = true;
          genderSpinnerErrorTextView.setVisibility(View.VISIBLE);

          genderSpinnerErrorTextView.setText("You haven't selected any gender");
      }

      if(cityName==null)
      {

          citySpinnerErrorTextView.setVisibility(View.VISIBLE);

          errorFound = true;
          citySpinnerErrorTextView.setText("You haven't selected any city");
      }

      if(province_StateName==null)
      {
          errorFound = true;

          pSSpinnerErrorTextView.setVisibility(View.VISIBLE);

          pSSpinnerErrorTextView.setText("You haven't selected any city");
      }
      if(countryName==null)
      {

          countrySpinnerErrorTextView.setVisibility(View.VISIBLE);
          countrySpinnerErrorTextView.setText("You haven't selected any city");
          errorFound = true;
      }


      return  errorFound;
  }

    public boolean   validateAllSegmentC()
    {boolean errorFound=false;
        homeAddress = homeAddEditText.getText().toString();
        initialHomeAddress = homeAddEditText.getText().toString();
        postal_ZipCode = postalZipCodeEditText.getText().toString();


        initialPhoneNumber = phoneNumberEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();
        phoneNoCountryCode = phoneNoCountryCodeEditText.getText().toString();
        String homeAddressStringError = validationUserInformationClass.validateHomeAddress(homeAddress);

        if(!homeAddressStringError.equalsIgnoreCase("field okay"))
        {
            homeAddEditText.setError(homeAddressStringError);
            errorFound=true;
        }

        try {

            if(phoneNumber.length()>0) {
                String result = null;
                finalPhoneNumber = "+" + phoneNoCountryCodeEditText.getText().toString().trim() + phoneNumber.trim();
                validatePhoneWithNumVerifyTask = new ValidatePhoneWithNumVerify(finalPhoneNumber);
                result = validatePhoneWithNumVerifyTask.execute().get();
                JSONObject phoneValidationObject = new JSONObject(result);
                String lineType = phoneValidationObject.getString("line_type");

                if (!phoneValidationObject.getBoolean("valid")) {
                    phoneNumberEditText.setError("Phone no not valid/ not valid for the country selected");
                    errorFound = true;
                } else if (lineType.equalsIgnoreCase("special_services") || lineType.equalsIgnoreCase("toll_free") || lineType.equalsIgnoreCase("premium_rate") || lineType.equalsIgnoreCase("satellite") || lineType.equalsIgnoreCase("paging")) {
                    phoneNumberEditText.setError("only mobile and landline number allowed");
                    errorFound = true;
                }
            }
            else {
                phoneNumberEditText.setError("Field can not be empty");
                errorFound=true;
            }

        } catch(InterruptedException e){
            e.printStackTrace();
        } catch(ExecutionException e){
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
        homeAddress = homeAddress+", "+cityName+", "+province_StateName+", "+countryName+", "+postal_ZipCode;

        return errorFound;
    }

    public boolean   validateAllSegmentD()
    {
        boolean errorFound=false;
        String passWordStringError = validationUserInformationClass.validatePassword(password);


        if(!passWordStringError.equalsIgnoreCase("field okay"))
        {

            passwordEditText.setError(passWordStringError);
            errorFound=true;
        }

        if(!password.equalsIgnoreCase(rePassword))
        {
            rePasswordEditText.setError("rePassword does match with password");

            errorFound=true;
        }

        return errorFound;
    }




    public void prevToLayoutB(View view)
    {
        relativeLayoutA.animate().translationX(2000);
        relativeLayoutD.animate().translationX(-2000);
        relativeLayoutC.animate().translationY(-2000).setDuration(300);
        relativeLayoutB.animate().translationY(0).setDuration(300);
        addUserButton.setVisibility(View.GONE);

    }

    public void prevToLayoutC(View view)
    {
        relativeLayoutB.animate().translationY(2000);
        relativeLayoutA.animate().translationX(2000);
        relativeLayoutC.animate().translationY(0).setDuration(300);
        relativeLayoutD.animate().translationX(-2000).setDuration(300);
        addUserButton.setVisibility(View.GONE);
    }

    public void prevToLayoutA(View view)
    {
        addUserButton.setVisibility(View.GONE);
        relativeLayoutC.animate().translationY(-2000);
        relativeLayoutD.animate().translationX(-2000);
        relativeLayoutA.animate().translationX(0).setDuration(300);
        relativeLayoutB.animate().translationY(2000).setDuration(300);

    }

    public void nextToLayoutC(View view)
    {
     relativeLayoutA.animate().translationX(2000);
        relativeLayoutC.animate().translationY(-2000);
        relativeLayoutD.animate().translationX(-2000);
        if (!validateAllSegmentB()) {
            relativeLayoutB.animate().translationY(2000).setDuration(300);
            relativeLayoutC.animate().translationY(0).setDuration(300);
            addUserButton.setVisibility(View.GONE);
        }
    }



    public void nextToLayoutD(View view)
    {
        relativeLayoutB.animate().translationY(2000);
        relativeLayoutA.animate().translationX(2000);
        if (!validateAllSegmentC()) {
            relativeLayoutC.animate().translationY(-2000).setDuration(300);
            relativeLayoutD.animate().translationX(0).setDuration(300);
        }
        addUserButton.setVisibility(View.VISIBLE);
    }
    public void nextToLayoutB(View view)
    {
        addUserButton.setVisibility(View.GONE);

        relativeLayoutB.animate().translationY(2000);
        relativeLayoutC.animate().translationY(-2000);
        relativeLayoutD.animate().translationX(-2000);
        if (!validateAllSegmentA()) {
            relativeLayoutA.animate().translationX(2000).setDuration(300);
            relativeLayoutB.animate().translationY(0).setDuration(300);
        }

    }

  public boolean getUserInformationFromInputFields()
  {
      boolean error = true;
      attributesList = new JSONArray();

      emailAddress = userNameEditText.getText().toString();
      userName = userNameEditText.getText().toString();
      reUserName= reUserNameEditText.getText().toString();
      password= passwordEditText.getText().toString();
     rePassword= rePasswordEditText.getText().toString();


     if(! validateAllSegmentD()) {
         error =false;

         attributesList.put(userName);
         attributesList.put(password);
         attributesList.put("+"+phoneNoCountryCode+phoneNumber);
         initializeAttributes();

     }
     return error;
  }

  public void addUser(View view)
  {





              boolean check = getUserInformationFromInputFields();
              if ( !check) {



                        String  result = insertUserInformation(firstName, middleName, lastName, gender, userName, homeAddress, password, false, phoneNoCountryCode, phoneNumber);
                          if (result.equalsIgnoreCase("record inserted")) {
                              SignUpHandler signupCallback = new SignUpHandler() {

                                  @Override
                                  public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                                      // Sign-up was successful

                                      // Check if this user (cognitoUser) needs to be confirmed
                                      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                      builder.setMessage("Your information is now in our database and verification code sent to email associated to your registration. One more step to go..");
                                      builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int id) {
                                              Intent nUAIntnent = new Intent(NewUserActivity.this, PasswordResetQuestionsActivity.class);
                                              nUAIntnent.putExtra("userName", userName);
                                              startActivity(nUAIntnent);
                                          }
                                      });
                                      AlertDialog alertDialog = builder.create();
                                      alertDialog.show();


                                  }

                                  @Override
                                  public void onFailure(Exception exception) {
                                      deleteUser(userName);
                                      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                      builder.setMessage("Something wrong with network. Please try again later");
                                      builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int id) {

                                              alertDialog.dismiss();
                                          }
                                      });
                                      alertDialog = builder.create();
                                      alertDialog.show();
                                  }
                              };

                              userPool.signUpInBackground(userName, password, userAttributes, null, signupCallback);


                              user = userPool.getUser(emailAddress);
                              VerificationHandler resendConfCodeHandler = new VerificationHandler() {
                                  @Override
                                  public void onSuccess(CognitoUserCodeDeliveryDetails details) {

                         /* alertDialogBuilder.setTitle("Code sent to "+details.getDestination()+" via "+details.getDeliveryMedium()+".");
                          alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  alertDialog.dismiss();
                              }
                          });
                          alertDialog = alertDialogBuilder.create();
                          alertDialog.show();
                          */


                                  }

                                  @Override
                                  public void onFailure(Exception exception) {
                                      // showDialogMessage("Confirmation code request has failed", AppHelper.formatException(exception), false);
/*
                          alertDialogBuilder.setTitle("Confirmation code request has failed");
                          alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  alertDialog.dismiss();
                              }
                          });
                          alertDialog = alertDialogBuilder.create();
                          alertDialog.show();
                          */
                                  }
                              };
                              user.resendConfirmationCodeInBackground(resendConfCodeHandler);
                          }
                          else {
                              AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                              builder.setMessage("Unable to create account for you. Please re-enter your details again");
                              builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int id) {
                                      alertDialog.dismiss();
                                      Intent nUAIntent = new Intent(NewUserActivity.this, LoginActivity.class);
                                      startActivity(nUAIntent);

                                  }
                              });
                              alertDialog = builder.create();
                              if (alertDialog != null && alertDialog.isShowing()) {
                                  alertDialog.dismiss();
                              }
                              alertDialog.show();
                          }




              }



  }






    public String insertUserInformation(String firstName, String middleName, String lastName, String gender, String userName, String fullAddress, String userPassword, boolean userVerified,String countryPhoneCode, String phoneNumber)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("firstNameOfUser", firstName);
            postDataParams.put("middleNameOfUser", middleName);
            postDataParams.put("lastNameOfUser", lastName);
            postDataParams.put("phoneNoOfUser", phoneNumber);
            postDataParams.put("addressOfUser", fullAddress);
            postDataParams.put("emailOfUser", userName);
            postDataParams.put("genderOfUser", gender);
            postDataParams.put("userPassword", userPassword);
            postDataParams.put("countryPhCode", countryPhoneCode);
            postDataParams.put("userVerified", userVerified);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/inud/",postDataParams, activity,"text/plain", "application/json");
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



    public String deleteUser(String userName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("nameOfUser",  userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/du/",postDataParams, activity,"text/plain", "application/json");
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



    public String getAllStatesProvinces(String countryId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("countryId", countryId);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fspwc/",postDataParams, activity,"application/json", "application/json");
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


    public String getAllCities(String stateId)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("stateProvinceId", stateId);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fcwsp/",postDataParams, activity,"application/json", "application/json");
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



    public String getAllCountries()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();


        httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/facs/",postDataParams, activity,"application/json", "application/json");
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






        return result;
    }





    public class ValidatePhoneWithNumVerify extends AsyncTask<String, Void, String> {

        private String phoneNo;
        private String result;


        protected void onPreExecute() {

        }

        public ValidatePhoneWithNumVerify(String phoneNo) {



            alertDialogBuilder = new AlertDialog.Builder(activity);
            this.phoneNo=phoneNo;

        }

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://apilayer.net/api/validate?access_key=80dadfd474a3adcbdee85333b6c1b56f&number="+phoneNo); // here is your URL path




                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    result = sb.toString();

                } else {
                    asyncErrorDialogDisplay.errorCodeCheck(responseCode);
                }
            } catch (Exception e) {
                asyncErrorDialogDisplay.handleException(activity);
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {


        }
    }

}
