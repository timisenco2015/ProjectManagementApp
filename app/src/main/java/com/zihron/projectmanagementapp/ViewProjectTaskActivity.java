package com.zihron.projectmanagementapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import com.zihron.projectmanagementapp.Utility.HttpRequestClass;
import com.zihron.projectmanagementapp.Utility.ProgressBarClass;
import com.zihron.projectmanagementapp.Utility.S3ImageClass;

public class ViewProjectTaskActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private CustomProjectTaskExpandibleAdapter customProjectTaskExpandibleAdapter;
    private JSONArray allMemberJSONArray;
    private HashMap<String, List<String> > listChildData;
    private Bundle bundle;
    private HttpRequestClass httpRequestClass;
    private Typeface fontAwesomeIcon;
    private String projectName;
    private String projectOwner;
    private SharedPreferences sharedPreferences;
    private String userName;
    private ImageView editPrjectImageView;
    private AlertDialog alertDialog;
    private S3ImageClass s3ImageClass;
    private AlertDialog.Builder alertDialogBuilder;
 private Activity activity;
 private boolean canCreateTask;
 private String projectTaskName;
    private ProgressBarClass progressBarClass;
    private AsyncErrorDialogDisplay asyncErrorDialogDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_project_task);
        activity = this;
        progressBarClass = new ProgressBarClass(activity);
        sharedPreferences = getSharedPreferences(LoginActivity.MyPreferences, Context.MODE_PRIVATE );
         alertDialogBuilder = new AlertDialog.Builder(activity);
        asyncErrorDialogDisplay = new AsyncErrorDialogDisplay(activity);
        fontAwesomeIcon = Typeface.createFromAsset(this.getAssets(),"font/fontawesome-webfont.ttf");
        //editPrjectImageView = (ImageView)findViewById(R.id.editProjectViewId);
        projectName = sharedPreferences.getString("projectName", null);
        projectOwner =sharedPreferences.getString("projectOwner", null);
        canCreateTask=  sharedPreferences.getBoolean("cancreatetask",false);
        s3ImageClass = new S3ImageClass();

        expandableListView = (ExpandableListView) findViewById(R.id.expandibleProjectTskView);


        try {

            String result = getProjectAllTasks();
            if (result != null) {
                allMemberJSONArray = new JSONArray(result);


   customProjectTaskExpandibleAdapter = new CustomProjectTaskExpandibleAdapter(this, allMemberJSONArray);

                // setting list adapter
                expandableListView.setAdapter(customProjectTaskExpandibleAdapter);
                expandableListView.setGroupIndicator(null);
                // Listview Group click listener
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                int groupPosition, long id) {


                       // DeleteProjectSpecificTasks deleteProjectSpecificTasks = new DeleteProjectSpecificTasks(listDataHeader.getJSONArray(groupPosition).getString(0), listDataHeader.getJSONArray(groupPosition).getString(1));

                     Intent vEPTAIntnent = new Intent(ViewProjectTaskActivity.this, ViewEachProjectTaskActivity.class);
                        try {
                           JSONObject tempObject = new JSONObject(allMemberJSONArray.getString(groupPosition));
                            projectTaskName = tempObject.getString("taskName");


                        } catch (JSONException e) {
                            e.printStackTrace();
                       }


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("projectTaskName", projectTaskName);

                        editor.commit();
                        startActivity(vEPTAIntnent);

                        return false;
                    }
                });

            }
            else
            {

                alertDialogBuilder.setMessage("It seems you dont have any task created yet. Please go back to project and create task from there");
                alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

            }  catch(JSONException e){
                e.printStackTrace();
            }




    }

    @Override
    public void onBackPressed() {

    }

    public void newTaskBtn(View view)
    {
        if(canCreateTask) {
            Intent vEPTAIntent = new Intent(ViewProjectTaskActivity.this, NewProjectTaskActivity.class);
            startActivity(vEPTAIntent);
        }
        else
        {

        }
    }

public void backToProjectPage(View view)
{
    Intent vPTAIntent = new Intent(ViewProjectTaskActivity.this,ViewProjectActivity.class);
    startActivity(vPTAIntent);
}


    public String getProjectAllTasks()
    {
        String  result = null;
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put("projectName", projectName);
            postDataParams.put("nameOfUser",userName);
            httpRequestClass = new HttpRequestClass("http://naijaconnectsapis.ca/projectmanagement-1.0/projectmanagement/fpatfd/",postDataParams, activity,"application/json", "application/json");
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








   //

}


