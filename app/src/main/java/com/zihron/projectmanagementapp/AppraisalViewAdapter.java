package com.zihron.projectmanagementapp;

/**
 * Created by ayoba on 2017-12-20.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


public class AppraisalViewAdapter extends BaseAdapter implements ListAdapter
{
    private JSONArray valueArray;
    private static Context context;
    private final Activity activity;
    private TextView projectNameTextView;
    private TextView projectEndDateTextView;
    private TextView projectCreatedByTextView;



    @Override
    public long getItemId(int position) {
        return 0;
    }

    private JSONArray innerValueArray;

    public AppraisalViewAdapter(Activity activity, JSONArray valueArray) {


        this.valueArray = valueArray;
        this.activity = activity;

    }


    @Override public int getCount() {
        if(null==valueArray)
            return 0;
        else
            return valueArray.length();

    }

    @Override public JSONArray getItem(int position) {
        JSONArray tempArray = null;
        if(null!=valueArray) {
            try {
                tempArray = valueArray.getJSONArray(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tempArray;
    }



    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.appraisalviewprojectlistviewlayout, null);



        projectNameTextView =(TextView)convertView.findViewById(R.id.projectNameTextVWId);
        projectEndDateTextView =(TextView)convertView.findViewById(R.id.projectEndDateTextVWId);
        projectCreatedByTextView =(TextView)convertView.findViewById(R.id.projectCreatedByTextVWId);

        JSONArray tempArray = getItem(position);

        if(null!=tempArray )
        {
            try {

                projectNameTextView.setText(tempArray.getString(0));
                projectEndDateTextView.setText(tempArray.getString(1));
                projectCreatedByTextView.setText(tempArray.getString(3)+" "+tempArray.getString(2));



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }
}
