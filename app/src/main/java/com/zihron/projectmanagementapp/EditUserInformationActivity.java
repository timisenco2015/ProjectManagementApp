package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class EditUserInformationActivity extends AppCompatActivity {
    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText homeAddEditText;
    private EditText lastNameEditText;
    private ArrayList<String> countryNamesArrayList = null;
    private JSONArray countriesJSONArray = null;
    private List<String> allProvinceStateArrayList;
    private ArrayList<String> allCitiesArrayList;
    private JSONArray allCitiesJSONArray;
    private TextView backToUserInformatnPageTextView;
    private JSONArray allProvinceStateJSONArray;
    private ArrayList<String> allProvinceStateList;
    private AutoCompleteTextView genderAutoCompleteTextView;
    private AutoCompleteTextView provinceStateAutoCompleteTextView;
    private AutoCompleteTextView countryAutoCompleteTextView;
    private AutoCompleteTextView cityAutoCompleteTextView;
    private EditText postalZipCodeEditText;
    private EditText phoneNumberEditText;
    private EditText phoneNoCountryCodeEditText;
    private String fullAddress;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private JSONObject userRecordObject;
    private VideoView backgroundVideView;

    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private String userName;
    private String selectedCountryName;
    private String selectedProvinceStateName;
    private String  selectedCityName;
    private String selectedGeneder;
    private HttpRequestClass httpRequestClass;
    private SharedPreferences sharedPreferences;

    private JSONArray attributesList;

    private CognitoUserAttributes userAttributes;
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
    private String phoneNoCountryCode;
    private String initialPhoneNumber;
    private String finalPhoneNumber;
    private Activity activity;
    private CognitoUser user;
    private String initialHomeAddress;
    private ValidationClass validationClass;
    private ProgressBarClass progressBarClass;
private JSONArray genderJSONArray;
    private Country_Province_State_Cities_Adapter c_p_s_c_adapter;

    private ValidatePhoneWithNumVerify validatePhoneWithNumVerifyTask;
    private ValidationUserInformationClass validationUserInformationClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_information);
        activity = this;
        this.progressBarClass = new ProgressBarClass(activity);
        validationClass = new ValidationClass();
        alertDialogBuilder = new AlertDialog.Builder(activity);
        userAttributes = new CognitoUserAttributes();
        validationUserInformationClass = new ValidationUserInformationClass();
        userPool = new CognitoUserPool(getApplicationContext(), "us-east-1_2LXNY6KWn", "780vf0449180oaspjnd8ffsa73", "1b8tk71ihesli3nuhbsfmsts9dqig26mjpnc06cl24qs0dd57hff", Regions.US_EAST_1);
        firstNameEditText = (EditText) findViewById(R.id.firstNameEditTxtId);
        middleNameEditText= (EditText) findViewById(R.id.middleNameEditTxtId);
        lastNameEditText= (EditText) findViewById(R.id.lastNameEditTxtId);
        genderAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.genderAutoCompleteTtVWId);
        phoneNumberEditText= (EditText) findViewById(R.id.phoneNoEditTxtId);
        phoneNoCountryCodeEditText = (EditText)findViewById(R.id.phoneNoCountryCodeEditTxtId);
        homeAddEditText= (EditText) findViewById(R.id.homeAddEditTxtId);
        cityAutoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTxtVWId);
        provinceStateAutoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.provinceStateAutoCompleteTxtVWId);
        countryAutoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.countryAutoCompleteTtVWId);
        backgroundVideView = (VideoView) findViewById(R.id.backgroundVideViewId);
        postalZipCodeEditText= (EditText) findViewById(R.id.postalZipCodeEditTxtId);
        backToUserInformatnPageTextView = (TextView)findViewById(R.id.backToUserInformatnPageTxtVWId);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", null);

        backToUserInformatnPageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent eUIAIntent = new Intent(EditUserInformationActivity.this, HomeActivity.class);
                startActivity(eUIAIntent);
            }
        });

        try {
            String result =getUserInfromation();
            userRecordObject = new JSONObject(result);
            initialUpdateFields();


        }  catch (JSONException e) {
            e.printStackTrace();
        }

        String pkgName = this.getPackageName();
        // Return 0 if not found.
        int resID = this.getResources().getIdentifier("biostorm", "raw", pkgName);
        backgroundVideView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + resID));

        backgroundVideView.start();
        backgroundVideView.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                backgroundVideView.start();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        backgroundVideView.start();
    }
    public void  setCityAutoCompleteTextViewAdapter(String selectedProvinceStateName)
    {
        try {

            String result = getAllCities(selectedProvinceStateName.trim());
            if(result.length()<=0)
            {
                provinceStateAutoCompleteTextView.setError("province or state enter does not exists");
            }
            else {
                allCitiesJSONArray = new JSONArray(result);
                createCitiesNamesJSONArray();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, allCitiesArrayList);
                adapter.setNotifyOnChange(true);
                adapter.notifyDataSetChanged();
                cityAutoCompleteTextView.setAdapter(adapter);
                cityAutoCompleteTextView.setThreshold(3);

                cityAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedCityName = parent.getItemAtPosition(position).toString();

                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


public void setProvinceStateAutoCompleteTextViewAdapter(String countryName)
{
   try {


        String result = getAllStatesProvinces(countryName.trim());

        if(result.length()<=0)
        {
            countryAutoCompleteTextView.setError("Country name enter does not exist");
        }
        else {
            allProvinceStateJSONArray = new JSONArray(result);
            createProvinceStateNameJSONArray();

            ArrayAdapter<String> aPSadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, allProvinceStateArrayList);
            aPSadapter.setNotifyOnChange(true);
            aPSadapter.notifyDataSetChanged();
            provinceStateAutoCompleteTextView.setAdapter(aPSadapter);
            provinceStateAutoCompleteTextView.setThreshold(2);
            provinceStateAutoCompleteTextView.invalidate();
            provinceStateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedProvinceStateName = parent.getItemAtPosition(position).toString();
                    setCityAutoCompleteTextViewAdapter(selectedProvinceStateName);
                    cityAutoCompleteTextView.setText(allCitiesArrayList.get(0));
                }
            });

        }
    }  catch (JSONException e) {
        e.printStackTrace();
    }
}
public void initialUpdateFields()
{
    try {

        String tempHomeAddress = userRecordObject.getString("userAddress");
        String[] splittedTempHomeAddress = tempHomeAddress.split(",");
        String finalHomeAddress ="";
        int index=0;
        for(int i=0; i<(splittedTempHomeAddress.length-5); i++) {
            finalHomeAddress+=splittedTempHomeAddress[i];
            finalHomeAddress+=", ";
            index =i;
        }
        finalHomeAddress+=splittedTempHomeAddress[index];
        homeAddEditText.setHint(finalHomeAddress);
        countryName = splittedTempHomeAddress[(splittedTempHomeAddress.length-2)];
        province_StateName = splittedTempHomeAddress[(splittedTempHomeAddress.length-3)];
        cityName = splittedTempHomeAddress[(splittedTempHomeAddress.length-4)];

        ArrayList<String> ageArray = new ArrayList<String>();
        ageArray.add("Male");
        ageArray.add("Female");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line, ageArray);
        genderAutoCompleteTextView.setAdapter(adapter);
        genderAutoCompleteTextView.setThreshold(2);

        genderAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGeneder = parent.getItemAtPosition(position).toString();
            }
        });


        String  result = getAllCountries();
        countriesJSONArray = new JSONArray(result);
        createCountryNameJSONArray();
        adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_dropdown_item_1line, countryNamesArrayList);
        countryAutoCompleteTextView.setAdapter(adapter);
        countryAutoCompleteTextView.setThreshold(2);
        countryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCountryName = parent.getItemAtPosition(position).toString();
                phoneNoCountryCodeEditText.setHint(getCountryPhoneCodeJSONArray(selectedCountryName));
                setProvinceStateAutoCompleteTextViewAdapter(selectedCountryName);
                provinceStateAutoCompleteTextView.setText(allProvinceStateArrayList.get(0));


            }
        });

        setProvinceStateAutoCompleteTextViewAdapter(countryName);
        setCityAutoCompleteTextViewAdapter(province_StateName);



        firstNameEditText.setHint(userRecordObject.getString("firstName"));
        middleNameEditText.setHint(userRecordObject.getString("middleName"));
        lastNameEditText.setHint(userRecordObject.getString("lastName"));
        genderAutoCompleteTextView.setHint(userRecordObject.getString("userGender"));
        phoneNumberEditText.setHint(userRecordObject.getString("phoneNo"));
        phoneNoCountryCodeEditText.setHint(userRecordObject.getString("countryPhoneCode"));
        countryAutoCompleteTextView.setHint(countryName);
        provinceStateAutoCompleteTextView.setHint(province_StateName);
        cityAutoCompleteTextView.setHint(cityName);
        postalZipCodeEditText.setHint(splittedTempHomeAddress[(splittedTempHomeAddress.length-1)]);


    } catch (JSONException e) {
        e.printStackTrace();
    }
}

public void getUpdatedFieldValue()
{
    if((firstNameEditText.getText().toString()).length()>0) {
        firstName = firstNameEditText.getText().toString();
    }
    else
    {
        firstName = firstNameEditText.getHint().toString();
    }

    if((middleNameEditText.getText().toString()).length()>0) {
        middleName = middleNameEditText.getText().toString();
}
else
{
    middleName = middleNameEditText.getHint().toString();
}

    if((lastNameEditText.getText().toString()).length()>0) {
        lastName = lastNameEditText.getText().toString();

    }
    else
    {
        lastName =lastNameEditText.getHint().toString();
    }


    if((homeAddEditText.getText().toString()).length()>0) {
        homeAddress = homeAddEditText.getText().toString();
    }
    else
    {
        homeAddress =homeAddEditText.getHint().toString();
    }

    if(selectedCountryName ==null)
    {
        countryName = countryAutoCompleteTextView.getHint().toString();
    }
    else
    {
        countryName = selectedCountryName;
    }

    if(selectedProvinceStateName ==null)
    {
        province_StateName = provinceStateAutoCompleteTextView.getHint().toString();
    }
    else
    {
        province_StateName = selectedProvinceStateName;
    }

    if(selectedCityName==null)
    {
        cityName = cityAutoCompleteTextView.getHint().toString();
    }
    else
    {
        cityName = selectedCityName;
    }


    if(selectedGeneder == null)
    {
        gender = genderAutoCompleteTextView.getHint().toString();
    }
    else
    {
        gender =selectedGeneder;
    }


    if((postalZipCodeEditText.getText().toString()).length()>0) {
        postal_ZipCode = postalZipCodeEditText.getText().toString();
    }
    else
    {
        postal_ZipCode =postalZipCodeEditText.getHint().toString();
    }

    if((phoneNumberEditText.getText().toString()).length()>0) {
        phoneNumber = phoneNumberEditText.getText().toString();

    }
    else
    {
        phoneNumber =phoneNumberEditText.getHint().toString();

    }


    if((phoneNoCountryCodeEditText.getText().toString()).length()>0) {
        phoneNoCountryCode = phoneNoCountryCodeEditText.getText().toString();

    }
    else
    {
        phoneNoCountryCode =phoneNoCountryCodeEditText.getHint().toString();

    }



}

    @Override
    public void onBackPressed() {

    }

    public boolean  validateAllEditTextValues() {
        getUpdatedFieldValue();
        boolean errorFound = false;
        String firstNameStringError = validationUserInformationClass.validationFirstName(firstName);
        String middleNameStringError = validationUserInformationClass.validationMiddleName(middleName);
        String lastNameStringError = validationUserInformationClass.validationLastName(lastName);
        String homeAddressStringError = validationUserInformationClass.validateHomeAddress(homeAddress);
        if (!firstNameStringError.equalsIgnoreCase("field okay")) {
            firstNameEditText.setError(firstNameStringError);
            errorFound = true;

        }

        if (!middleNameStringError.equalsIgnoreCase("field okay")) {
            middleNameEditText.setError(middleNameStringError);
            errorFound = true;

        }

        if (!lastNameStringError.equalsIgnoreCase("field okay")) {
            lastNameEditText.setError(lastNameStringError);
            errorFound = true;

        }

        if (!homeAddressStringError.equalsIgnoreCase("field okay")) {
            homeAddEditText.setError(homeAddressStringError);
            errorFound = true;

        }

        if (phoneNumber.length() <= 0) {
            phoneNumberEditText.setError("This field cannot be empty");
            errorFound = true;

        } else
        {
            try {
                finalPhoneNumber = "+" + phoneNoCountryCode.trim() + phoneNumber.trim();
                String result = null;
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


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

        return  errorFound;
    }


    public void createProvinceStateNameJSONArray()
    {
        allProvinceStateArrayList = new ArrayList<String>();
        for (int i=0; i<allProvinceStateJSONArray.length(); i++)
        {

            try {
                allProvinceStateArrayList.add(allProvinceStateJSONArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void createCitiesNamesJSONArray()
    {
        allCitiesArrayList = new ArrayList<String>();
        for (int i=0; i<allCitiesJSONArray.length(); i++)
        {

            try {


                allCitiesArrayList.add(allCitiesJSONArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void UpdateUser(View view)
    {

        if(!validateAllEditTextValues())
        {

            fullAddress = homeAddress+ ", "+ cityName+ ", "+ province_StateName+", "+ countryName+ ", "+ postal_ZipCode;

                String result = updateUserInfromation(phoneNoCountryCode);
                if(result.equalsIgnoreCase("Record updated")) {
                    alertDialogBuilder.setMessage(result);
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertDialog.dismiss();
                                }
                            });

                }
                else
                {
                    alertDialogBuilder.setMessage("ooooopppPPPSSSS!!! Server currently not available. Please try again in some minutes");
                    alertDialogBuilder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alertDialog.dismiss();
                                }
                            });
                }


        }
        else
        {
            alertDialogBuilder.setMessage("There is an error in one of the fields. Please ensure that your inputs are right");
            alertDialogBuilder.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            alertDialog.dismiss();
                        }
                    });
        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



public void createCountryNameJSONArray()
{
    countryNamesArrayList = new ArrayList<String>();
    for (int i=0; i<countriesJSONArray.length(); i++)
    {

        try {
            JSONArray tempArray = countriesJSONArray.getJSONArray(i);

            countryNamesArrayList.add(tempArray.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

    public String getCountryPhoneCodeJSONArray(String countryName)
    {
        String countryCode=null;

        countryNamesArrayList = new ArrayList<String>();
        for (int i=0; i<countriesJSONArray.length(); i++)
        {

            try {
                JSONArray tempArray = countriesJSONArray.getJSONArray(i);
                if(tempArray.getString(1).equalsIgnoreCase(countryName))
                {
                    countryCode = tempArray.getString(0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countryCode;
    }



    public String getUserInfromation()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("nameOfUser", userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fufd/",postDataParams, activity,"application/json", "application/json");
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




    public String updateUserInfromation(String countryPhoneCode)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("firstNameOfUser", firstName);
            postDataParams.put("middleNameOfUser", middleName);
            postDataParams.put("lastNameOfUser", lastName);
            postDataParams.put("phoneNoOfUser", phoneNumber);
            postDataParams.put("addressOfUser", fullAddress);
            postDataParams.put("genderOfUser", gender);
            postDataParams.put("emailOfUser", userName);
            postDataParams.put("countryPhCode", countryPhoneCode);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/uud/",postDataParams, activity,"text/plain", "application/json");
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








    public String getAllStatesProvinces(String countryName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("nameOfCountry", countryName.trim());
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/faspn/",postDataParams, activity,"application/json", "application/json");
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


    public String getAllCities(String stateName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {


            postDataParams.put("nameOfStateProvince", stateName.trim());
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/facns/",postDataParams, activity,"application/json", "application/json");
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


            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/facn/",postDataParams, activity,"application/json", "application/json");
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


        @Override
        protected void onPreExecute() {

            progressBarClass.showDialog();
        }

        public ValidatePhoneWithNumVerify(String phoneNo) {



            alertDialogBuilder = new AlertDialog.Builder(activity);
            this.phoneNo=phoneNo;

        }

        @Override
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


            progressBarClass.dismissDialog();

        }
    }




}
