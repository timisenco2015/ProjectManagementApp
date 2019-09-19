package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class CustomProjectListViewAdapter  extends BaseAdapter implements ListAdapter
{
    private JSONArray valueArray;
    private static Context context;
    private final Activity activity;
    private TextView projectNameViewIdTVW;
    private TextView projectCreatedDateIdTVW;
    private TextView projectOwnerIdTVW;
    private TextView  projectStartDateIdTVW;
    private TextView projectEndDateIdTVW;
    private TextView progressBarTextView;
    private ProgressBar projectStatusProgressBar;


    @Override
    public long getItemId(int position) {
        return 0;
    }

    private JSONArray innerValueArray;

    public CustomProjectListViewAdapter (Activity activity, JSONArray valueArray) {


        this.valueArray = valueArray;
        this.context=context;
        this.activity = activity;

    }


    @Override public int getCount() {
        if(null==valueArray)
            return 0;
        else
            return valueArray.length();
          //  return valueArray.length();
    }

    @Override public String getItem(int position) {
        String tempString = null;
        if(null!=valueArray) {
            try {
                tempString = valueArray.getString(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tempString;
    }



    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.listitemcustomview, null);



        projectNameViewIdTVW =(TextView)convertView.findViewById(R.id.projectNameViewId);
        projectOwnerIdTVW =(TextView)convertView.findViewById(R.id.projectownerId);
        progressBarTextView = (TextView)convertView.findViewById(R.id.projectStatusProgressBarTexyWId);
        projectStatusProgressBar = (ProgressBar) convertView.findViewById(R.id. projectStatusProgressBarId);
        String tempString = getItem(position);
        if(null!=tempString)
        {
            try {

                JSONObject tempObject = new JSONObject(tempString);
                projectNameViewIdTVW.setText(tempObject.getString("nameOfProject"));
                projectOwnerIdTVW.setText(tempObject.getString("firstName")+" "+tempObject.getString("lastName"));
                projectStatusProgressBar.setProgress((int)Double.parseDouble(tempObject.getString("projectDonePer")));
                DecimalFormat f = new DecimalFormat("##.00");
                progressBarTextView.setText(f.format(Double.parseDouble(tempObject.getString("projectDonePer")))+"% done");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }
}
