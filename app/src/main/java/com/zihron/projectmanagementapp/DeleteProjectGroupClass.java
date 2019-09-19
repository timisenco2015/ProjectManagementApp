package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zihron.projectmanagementapp.Utility.HttpRequestClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeleteProjectGroupClass {




    private JSONArray allProjectGroupsJArray;
    private Activity activity;
    private String groupName;
    private Gson googleJson;
    String userName;
    private ArrayList<String> allProjectGroupsList;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;
    private ArrayAdapter arrayJSONAdapter;
    private String projectName;
    private String firstName, lastName;
    private HttpRequestClass httpRequestClass;
    public DeleteProjectGroupClass(final Activity activity,final String projectName,final String userName,String firstName, String lastName,AutoCompleteTextView deleteGrpAutoCompleteTextView, ImageView deleteProjectGrpButton,final LinearLayout deleteGroupLayout)
    {
        this.activity = activity;
        this.userName = userName;
        this.projectName =projectName;
        this.firstName = firstName;
        this.lastName = lastName;
        try {
            String result =getAllProjectGroups(projectName);
           if(result!=null)
           {
               allProjectGroupsJArray = new JSONArray(result);
               googleJson = new Gson();
               allProjectGroupsList = googleJson.fromJson( allProjectGroupsJArray.toString(), ArrayList.class);

               deleteGrpAutoCompleteTextView.setEnabled(true);
               arrayJSONAdapter = new ArrayAdapter(activity,  android.R.layout.simple_dropdown_item_1line, allProjectGroupsList);
               deleteGrpAutoCompleteTextView.setAdapter(arrayJSONAdapter);
               deleteGrpAutoCompleteTextView.setThreshold(1);

           }
        }  catch (JSONException e) {
            e.printStackTrace();
        }


        deleteGrpAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {

                    groupName = allProjectGroupsJArray.getJSONArray(position).getString(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        deleteProjectGrpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(activity);
                View convertView= inflater.inflate(R.layout.projecttaskexpadinblelistview, null);
                CoordinatorLayout snackBarLayout = (CoordinatorLayout) convertView.findViewById(R.id.snackBarLLT);
                Snackbar snackBar = null;
                snackBar  = Snackbar.make(snackBarLayout,"This group will be deleted completely. To Retrieve information regarding you have to contact our customer. Incase you changed your mind, click cancel",Snackbar.LENGTH_SHORT)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteGroupLayout.setEnabled(false);
                            }
                        })
                .setActionTextColor(activity.getResources().getColor(R.color.color24));
                View snackView = snackBar.getView();
                TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(activity.getResources().getColor(R.color.color12));
                snackBar.show();

                    String result = deleProjectGroups();
                    if(result.equalsIgnoreCase("Record Deleted"))
                    {
                        Toast.makeText(activity,"Deleted",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(activity,"Not Deleted",Toast.LENGTH_LONG).show();
                    }
            }
        });

    }


    public String getAllProjectGroups(String projectName)
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fapg/",postDataParams, activity,"application/json", "application/json");
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


    public String deleProjectGroups()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("nameOfProject", projectName);
            postDataParams.put("nameOfGroup", groupName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/dpg/",postDataParams, activity,"text/plain", "application/json");
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










}
